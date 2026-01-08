package sn.uasz.SIGTMA.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sn.uasz.SIGTMA.model.Encadrant;

public interface EncadrantRepository extends JpaRepository<Encadrant, Long> {
    java.util.Optional<Encadrant> findByEmail(String email);

    java.util.List<Encadrant> findByNomContainingIgnoreCaseOrPrenomContainingIgnoreCase(String nom, String prenom);
}