package sn.uasz.SIGTMA.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Utilisateur implements UserDetails { // 1. Implémenter UserDetails

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le prénom est obligatoire")
    private String prenom;

    @NotBlank(message = "Le nom est obligatoire")
    private String nom;

    private String sexe;
    private String adresse;

    @Column(unique = true, nullable = false)
    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "L'email doit être valide")
    private String email; // Ce sera notre "username"

    private String telephone;

    @NotBlank(message = "Le mot de passe est obligatoire")
    @Size(min = 6, message = "Le mot de passe doit contenir au moins 6 caractères")
    @JsonIgnore
    private String motDePasse; // Spring Security utilisera ce champ

    private String role; // "ADMIN", "BIBLIOTHECAIRE", "ETUDIANT"

    @OneToMany(mappedBy = "utilisateur")
    @JsonIgnore
    private List<RapportStatistique> rapports;

    @OneToMany(mappedBy = "utilisateur")
    @JsonIgnore
    private List<TheseMemoire> theses;

    // --- Méthodes imposées par UserDetails ---

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Convertit votre rôle String en autorité Spring Security
        String roleName = role != null ? role : "ETUDIANT";
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + roleName));
    }

    @Override
    public String getPassword() {
        return motDePasse;
    }

    @Override
    public String getUsername() {
        return email; // On utilise l'email pour se connecter
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}