package sn.uasz.SIGTMA.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import sn.uasz.SIGTMA.model.Utilisateur;
import sn.uasz.SIGTMA.repository.UtilisateurRepository;

import java.util.List;
import java.util.Optional;

@Service
public class UtilisateurService {

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Utilisateur creerUtilisateur(Utilisateur utilisateur) {
        utilisateur.setMotDePasse(passwordEncoder.encode(utilisateur.getMotDePasse()));
        if (utilisateur.getRole() == null || utilisateur.getRole().isEmpty()) {
            utilisateur.setRole("ETUDIANT");
        }
        return utilisateurRepository.save(utilisateur);
    }

    public List<Utilisateur> listerUtilisateurs() {
        return utilisateurRepository.findAll();
    }

    public Utilisateur verifierConnexion(String email, String motDePasse) {
        Optional<Utilisateur> user = utilisateurRepository.findByEmail(email);
        if (user.isPresent()) {
            if (passwordEncoder.matches(motDePasse, user.get().getMotDePasse())) {
                return user.get();
            }
        }
        return null;
    }

    public Utilisateur trouverParEmail(String email) {
        return utilisateurRepository.findByEmail(email).orElse(null);
    }

    public Utilisateur modifierUtilisateur(Long id, Utilisateur details) {
        Utilisateur u = utilisateurRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        u.setPrenom(details.getPrenom());
        u.setNom(details.getNom());
        u.setEmail(details.getEmail());
        u.setRole(details.getRole());
        u.setSexe(details.getSexe());
        u.setAdresse(details.getAdresse());
        u.setTelephone(details.getTelephone());

        if (details.getMotDePasse() != null && !details.getMotDePasse().isEmpty()) {
            u.setMotDePasse(passwordEncoder.encode(details.getMotDePasse()));
        }

        return utilisateurRepository.save(u);
    }

    public void supprimerUtilisateur(Long id) {
        utilisateurRepository.deleteById(id);
    }
}