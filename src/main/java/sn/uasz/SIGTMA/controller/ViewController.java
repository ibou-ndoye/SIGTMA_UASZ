package sn.uasz.SIGTMA.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ViewController {

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/")
    public String root() {
        return "redirect:/login";
    }

    @GetMapping("/admin/dashboard")
    public String adminDashboard() {
        return "admin/dashboard";
    }

    @GetMapping("/admin/users")
    public String adminUsers() {
        return "admin/users";
    }

    @GetMapping("/admin/filieres")
    public String adminFilieres() {
        return "admin/filieres";
    }

    @GetMapping("/admin/rapports")
    public String adminReports() {
        return "admin/rapports";
    }

    @GetMapping("/bibliothecaire/dashboard")
    public String bibliothecaireDashboard() {
        return "bibliothecaire/dashboard";
    }

    @GetMapping("/bibliothecaire/catalogue")
    public String bibliothecaireCatalogue() {
        return "bibliothecaire/catalogue";
    }

    @GetMapping("/bibliothecaire/rapports")
    public String bibliothecaireRapports() {
        return "bibliothecaire/rapports";
    }

    @GetMapping("/bibliothecaire/search")
    public String bibliothecaireSearch() {
        return "bibliothecaire/search";
    }

    @GetMapping("/aide-bibliothecaire/dashboard")
    public String aideBibliothecaireDashboard() {
        return "aide-bibliothecaire/dashboard";
    }

    @GetMapping("/aide-bibliothecaire/etudiants")
    public String aideBibliothecaireEtudiants() {
        return "aide-bibliothecaire/etudiants";
    }

    @GetMapping("/etudiant/dashboard")
    public String etudiantDashboard() {
        return "etudiant/dashboard";
    }
}
