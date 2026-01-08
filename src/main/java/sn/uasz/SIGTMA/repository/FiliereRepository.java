package sn.uasz.SIGTMA.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sn.uasz.SIGTMA.model.Filiere;

public interface FiliereRepository extends JpaRepository<Filiere, Long> {
    java.util.List<Filiere> findByNomContainingIgnoreCase(String nom);
}