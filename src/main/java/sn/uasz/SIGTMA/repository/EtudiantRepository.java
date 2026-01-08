package sn.uasz.SIGTMA.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sn.uasz.SIGTMA.model.Etudiant;

public interface EtudiantRepository extends JpaRepository<Etudiant, Long> {

    java.util.Optional<Etudiant> findByMatricule(String matricule);

    java.util.List<Etudiant> findByNomContainingIgnoreCaseOrPrenomContainingIgnoreCase(String nom, String prenom);
}