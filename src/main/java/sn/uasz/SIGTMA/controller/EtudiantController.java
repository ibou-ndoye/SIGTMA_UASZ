package sn.uasz.SIGTMA.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import sn.uasz.SIGTMA.model.Etudiant;
import sn.uasz.SIGTMA.service.EtudiantService;

import java.util.List;

@RestController
@RequestMapping("/api/etudiants")
@CrossOrigin("*")
public class EtudiantController {

    @Autowired
    private EtudiantService etudiantService;

    @PostMapping
    public Etudiant ajouterEtudiant(@RequestBody Etudiant etudiant) {
        return etudiantService.ajouterEtudiant(etudiant);
    }

    @GetMapping
    public List<Etudiant> listerEtudiants() {
        return etudiantService.listerTousLesEtudiants();
    }

    @GetMapping("/{id}")
    public Etudiant getEtudiant(@PathVariable Long id) {
        return etudiantService.trouverEtudiant(id);
    }

    @PutMapping("/{id}")
    public Etudiant modifierEtudiant(@PathVariable Long id, @RequestBody Etudiant etudiant) {
        return etudiantService.modifierEtudiant(id, etudiant);
    }

    @DeleteMapping("/{id}")
    public void supprimerEtudiant(@PathVariable Long id) {
        etudiantService.supprimerEtudiant(id);
    }

    @GetMapping("/recherche")
    public List<Etudiant> rechercher(@RequestParam String motCle) {
        return etudiantService.rechercherEtudiants(motCle);
    }
}