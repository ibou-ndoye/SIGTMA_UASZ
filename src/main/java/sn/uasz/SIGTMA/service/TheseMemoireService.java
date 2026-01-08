package sn.uasz.SIGTMA.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sn.uasz.SIGTMA.model.TheseMemoire;
import sn.uasz.SIGTMA.model.Etudiant;
import sn.uasz.SIGTMA.model.Encadrant;
import sn.uasz.SIGTMA.model.Filiere;
import sn.uasz.SIGTMA.dto.DossierDTO;
import sn.uasz.SIGTMA.repository.TheseMemoireRepository;
import sn.uasz.SIGTMA.repository.EtudiantRepository;
import sn.uasz.SIGTMA.repository.EncadrantRepository;
import sn.uasz.SIGTMA.repository.FiliereRepository;

import java.time.LocalDate;
import java.util.List;

@Service
public class TheseMemoireService {

    @Autowired
    private TheseMemoireRepository theseMemoireRepository;

    @Autowired
    private EtudiantRepository etudiantRepository;

    @Autowired
    private EncadrantRepository encadrantRepository;

    @Autowired
    private FiliereRepository filiereRepository;

    public TheseMemoire deposerDossier(DossierDTO dto, org.springframework.web.multipart.MultipartFile fichier) {
        // 1. Rechercher ou créer l'étudiant
        Etudiant etudiant;
        if (dto.getEtudiantId() != null) {
            etudiant = etudiantRepository.findById(dto.getEtudiantId())
                    .orElseThrow(() -> new RuntimeException("Étudiant non trouvé"));
        } else {
            etudiant = etudiantRepository.findByMatricule(dto.getMatriculeEtudiant())
                    .orElseGet(() -> {
                        Etudiant newEt = new Etudiant();
                        newEt.setMatricule(dto.getMatriculeEtudiant());
                        newEt.setNom(dto.getNomEtudiant());
                        newEt.setPrenom(dto.getPrenomEtudiant());
                        newEt.setEmail(dto.getEmailEtudiant());
                        newEt.setAnneeAcademique(dto.getAnneeAcademique());
                        if (dto.getFiliereId() != null) {
                            filiereRepository.findById(dto.getFiliereId()).ifPresent(newEt::setFiliere);
                        }
                        return etudiantRepository.save(newEt);
                    });
        }

        // 2. Rechercher ou créer l'encadrant
        Encadrant encadrant;
        if (dto.getEncadrantId() != null) {
            encadrant = encadrantRepository.findById(dto.getEncadrantId())
                    .orElseThrow(() -> new RuntimeException("Encadrant non trouvé"));
        } else {
            encadrant = encadrantRepository.findByEmail(dto.getEmailEncadrant())
                    .orElseGet(() -> {
                        Encadrant newEnc = new Encadrant();
                        newEnc.setNom(dto.getNomEncadrant());
                        newEnc.setPrenom(dto.getPrenomEncadrant());
                        newEnc.setGrade(dto.getGradeEncadrant());
                        newEnc.setEmail(dto.getEmailEncadrant());
                        return encadrantRepository.save(newEnc);
                    });
        }

        // 3. Créer la thèse/mémoire
        TheseMemoire these = new TheseMemoire();
        these.setTitre(dto.getTitre());
        these.setType(dto.getType());
        these.setAnnee(dto.getAnnee());
        these.setResume(dto.getResume());
        these.setDateDeDepot(LocalDate.now());
        these.setStatus(sn.uasz.SIGTMA.enums.StatutThese.EN_ATTENTE);
        these.setEtudiant(etudiant);
        these.setEncadrant(encadrant);

        // Sauvegarde du fichier
        if (fichier != null && !fichier.isEmpty()) {
            try {
                String fileName = System.currentTimeMillis() + "_" + fichier.getOriginalFilename();
                java.nio.file.Path uploadPath = java.nio.file.Paths.get("uploads");
                if (!java.nio.file.Files.exists(uploadPath)) {
                    java.nio.file.Files.createDirectories(uploadPath);
                }
                java.nio.file.Files.copy(fichier.getInputStream(), uploadPath.resolve(fileName),
                        java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                these.setFichier(fileName);
            } catch (java.io.IOException e) {
                throw new RuntimeException("Erreur lors du téléchargement du fichier", e);
            }
        }

        return theseMemoireRepository.save(these);
    }

    public TheseMemoire ajouterThese(TheseMemoire theseMemoire) {
        // Ajout automatique de la date de dépôt si elle n'est pas fournie
        if (theseMemoire.getDateDeDepot() == null) {
            theseMemoire.setDateDeDepot(LocalDate.now());
        }
        return theseMemoireRepository.save(theseMemoire);
    }

    public List<TheseMemoire> listerTheses() {
        return theseMemoireRepository.findAll();
    }

    public List<TheseMemoire> listerThesesRecentes() {
        return theseMemoireRepository.findTop10ByOrderByDateDeDepotDesc();
    }

    public TheseMemoire trouverThese(Long id) {
        return theseMemoireRepository.findById(id).orElse(null);
    }

    public List<TheseMemoire> rechercher(String motCle, LocalDate date, Integer annee, String type) {
        if ((motCle != null && !motCle.isEmpty()) || date != null || annee != null
                || (type != null && !type.isEmpty())) {
            String term = (motCle == null) ? "" : motCle;
            return theseMemoireRepository.rechercherAvecFiltres(term, date, annee, type);
        }
        return theseMemoireRepository.findAll();
    }

    public TheseMemoire validerThese(Long id) {
        TheseMemoire these = trouverThese(id);
        if (these != null) {
            these.setStatus(sn.uasz.SIGTMA.enums.StatutThese.VALIDEE);
            return theseMemoireRepository.save(these);
        }
        return null;
    }

    public TheseMemoire rejeterThese(Long id) {
        TheseMemoire these = trouverThese(id);
        if (these != null) {
            these.setStatus(sn.uasz.SIGTMA.enums.StatutThese.REJETEE);
            return theseMemoireRepository.save(these);
        }
        return null;
    }
}