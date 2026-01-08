package sn.uasz.SIGTMA.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sn.uasz.SIGTMA.model.Encadrant;
import sn.uasz.SIGTMA.repository.EncadrantRepository;

import java.util.List;

@Service
public class EncadrantService {

    @Autowired
    private EncadrantRepository encadrantRepository;

    public Encadrant ajouterEncadrant(Encadrant encadrant) {
        return encadrantRepository.save(encadrant);
    }

    public List<Encadrant> listerEncadrants() {
        return encadrantRepository.findAll();
    }

    public Encadrant trouverEncadrant(Long id) {
        return encadrantRepository.findById(id).orElse(null);
    }

    public void supprimerEncadrant(Long id) {
        encadrantRepository.deleteById(id);
    }

    public List<Encadrant> rechercherEncadrants(String motCle) {
        return encadrantRepository.findByNomContainingIgnoreCaseOrPrenomContainingIgnoreCase(motCle, motCle);
    }
}