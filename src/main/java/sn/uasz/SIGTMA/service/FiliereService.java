package sn.uasz.SIGTMA.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sn.uasz.SIGTMA.model.Filiere;
import sn.uasz.SIGTMA.repository.FiliereRepository;

import java.util.List;

@Service
public class FiliereService {

    @Autowired
    private FiliereRepository filiereRepository;

    public Filiere ajouterFiliere(Filiere filiere) {
        return filiereRepository.save(filiere);
    }

    public List<Filiere> listerFilieres() {
        return filiereRepository.findAll();
    }

    public void supprimerFiliere(Long id) {
        filiereRepository.deleteById(id);
    }

    public Filiere modifierFiliere(Long id, Filiere details) {
        Filiere f = filiereRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Filière non trouvée"));
        f.setNom(details.getNom());
        f.setNomUfr(details.getNomUfr());
        return filiereRepository.save(f);
    }

    public List<Filiere> rechercherFilieres(String motCle) {
        return filiereRepository.findByNomContainingIgnoreCase(motCle);
    }
}