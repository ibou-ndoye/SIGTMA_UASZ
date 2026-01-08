package sn.uasz.SIGTMA.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import sn.uasz.SIGTMA.model.Filiere;
import sn.uasz.SIGTMA.service.FiliereService;

import java.util.List;

@RestController
@RequestMapping("/api/filieres")
@CrossOrigin("*")
public class FiliereController {

    @Autowired
    private FiliereService filiereService;

    @PostMapping
    public Filiere ajouterFiliere(@RequestBody Filiere filiere) {
        return filiereService.ajouterFiliere(filiere);
    }

    @GetMapping
    public List<Filiere> listerFilieres() {
        return filiereService.listerFilieres();
    }

    @DeleteMapping("/{id}")
    public void supprimerFiliere(@PathVariable Long id) {
        filiereService.supprimerFiliere(id);
    }

    @PutMapping("/{id}")
    public Filiere modifierFiliere(@PathVariable Long id, @RequestBody Filiere filiere) {
        return filiereService.modifierFiliere(id, filiere);
    }

    @GetMapping("/recherche")
    public List<Filiere> rechercher(@RequestParam String motCle) {
        return filiereService.rechercherFilieres(motCle);
    }
}