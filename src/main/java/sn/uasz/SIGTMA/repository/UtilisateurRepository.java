package sn.uasz.SIGTMA.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sn.uasz.SIGTMA.model.Utilisateur;
import java.util.Optional;

public interface UtilisateurRepository extends JpaRepository<Utilisateur, Long> {

    // Pour trouver un user quand il essaie de se connecter
    Optional<Utilisateur> findByEmail(String email);
}