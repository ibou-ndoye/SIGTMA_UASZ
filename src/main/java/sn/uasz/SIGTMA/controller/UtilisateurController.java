package sn.uasz.SIGTMA.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import sn.uasz.SIGTMA.model.Utilisateur;
import sn.uasz.SIGTMA.service.UtilisateurService;

import java.util.List;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/utilisateurs")
@CrossOrigin("*")
public class UtilisateurController {

    @Autowired
    private UtilisateurService utilisateurService;

    @PostMapping("/inscription")
    public Utilisateur inscription(@Valid @RequestBody Utilisateur utilisateur) {
        return utilisateurService.creerUtilisateur(utilisateur);
    }

    @GetMapping
    public List<Utilisateur> listerUtilisateurs() {
        return utilisateurService.listerUtilisateurs();
    }

    @GetMapping("/profil")
    public Utilisateur monProfil() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return utilisateurService.trouverParEmail(auth.getName());
    }

    @PutMapping("/{id}")
    public Utilisateur modifier(@PathVariable Long id, @RequestBody Utilisateur utilisateur) {
        return utilisateurService.modifierUtilisateur(id, utilisateur);
    }

    @DeleteMapping("/{id}")
    public void supprimer(@PathVariable Long id) {
        utilisateurService.supprimerUtilisateur(id);
    }
}