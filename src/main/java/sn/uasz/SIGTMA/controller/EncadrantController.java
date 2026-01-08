package sn.uasz.SIGTMA.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import sn.uasz.SIGTMA.model.Encadrant;
import sn.uasz.SIGTMA.service.EncadrantService;

import java.util.List;

@RestController
@RequestMapping("/api/encadrants")
@CrossOrigin("*")
public class EncadrantController {

    @Autowired
    private EncadrantService encadrantService;

    @PostMapping
    public Encadrant ajouterEncadrant(@RequestBody Encadrant encadrant) {
        return encadrantService.ajouterEncadrant(encadrant);
    }

    @GetMapping
    public List<Encadrant> listerEncadrants() {
        return encadrantService.listerEncadrants();
    }

    @GetMapping("/{id}")
    public Encadrant getEncadrant(@PathVariable Long id) {
        return encadrantService.trouverEncadrant(id);
    }

    @DeleteMapping("/{id}")
    public void supprimerEncadrant(@PathVariable Long id) {
        encadrantService.supprimerEncadrant(id);
    }

    @GetMapping("/recherche")
    public List<Encadrant> rechercher(@RequestParam String motCle) {
        return encadrantService.rechercherEncadrants(motCle);
    }
}