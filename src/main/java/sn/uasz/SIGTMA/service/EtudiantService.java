package sn.uasz.SIGTMA.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sn.uasz.SIGTMA.model.Etudiant;
import sn.uasz.SIGTMA.repository.EtudiantRepository;

import java.util.List;

@Service
public class EtudiantService {

    @Autowired
    private EtudiantRepository etudiantRepository;

    public Etudiant ajouterEtudiant(Etudiant etudiant) {
        // Ici, on pourrait vérifier si le matricule existe déjà
        return etudiantRepository.save(etudiant);
    }

    public List<Etudiant> listerTousLesEtudiants() {
        return etudiantRepository.findAll();
    }

    public Etudiant trouverEtudiant(Long id) {
        return etudiantRepository.findById(id).orElse(null);
    }

    public Etudiant modifierEtudiant(Long id, Etudiant etudiantModifie) {
        return etudiantRepository.findById(id)
                .map(etudiant -> {
                    etudiant.setNom(etudiantModifie.getNom());
                    etudiant.setPrenom(etudiantModifie.getPrenom());
                    etudiant.setEmail(etudiantModifie.getEmail());
                    etudiant.setMatricule(etudiantModifie.getMatricule());
                    etudiant.setFiliere(etudiantModifie.getFiliere());
                    return etudiantRepository.save(etudiant);
                }).orElse(null);
    }

    public void supprimerEtudiant(Long id) {
        etudiantRepository.deleteById(id);
    }

    public List<Etudiant> rechercherEtudiants(String motCle) {
        return etudiantRepository.findByNomContainingIgnoreCaseOrPrenomContainingIgnoreCase(motCle, motCle);
    }
}