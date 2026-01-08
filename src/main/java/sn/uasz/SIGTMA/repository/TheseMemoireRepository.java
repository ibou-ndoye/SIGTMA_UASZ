package sn.uasz.SIGTMA.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import sn.uasz.SIGTMA.model.TheseMemoire;
import sn.uasz.SIGTMA.enums.StatutThese;
import java.util.List;
import java.time.LocalDate;

public interface TheseMemoireRepository extends JpaRepository<TheseMemoire, Long> {

        List<TheseMemoire> findByTitreContainingIgnoreCase(String motCle);

        List<TheseMemoire> findByType(String type);

        long countByDateDeDepot(LocalDate date);

        @Query("SELECT COUNT(t) FROM TheseMemoire t WHERE t.dateDeDepot BETWEEN :start AND :end")
        long countByDateDeDepotBetween(LocalDate start, LocalDate end);

        long countByStatus(StatutThese status);

        @Query("SELECT COUNT(t) FROM TheseMemoire t WHERE t.status = :status AND t.dateDeDepot BETWEEN :start AND :end")
        long countByStatusAndDateDeDepotBetween(StatutThese status, LocalDate start, LocalDate end);

        List<TheseMemoire> findTop5ByOrderByDateDeDepotDesc();

        List<TheseMemoire> findTop10ByOrderByDateDeDepotDesc();

        @Query("SELECT t FROM TheseMemoire t WHERE " +
                        "(LOWER(t.titre) LIKE LOWER(CONCAT('%', :motCle, '%')) OR " +
                        "LOWER(t.etudiant.nom) LIKE LOWER(CONCAT('%', :motCle, '%')) OR " +
                        "LOWER(t.etudiant.prenom) LIKE LOWER(CONCAT('%', :motCle, '%')) OR " +
                        "LOWER(t.encadrant.nom) LIKE LOWER(CONCAT('%', :motCle, '%')) OR " +
                        "LOWER(t.encadrant.prenom) LIKE LOWER(CONCAT('%', :motCle, '%')) OR " +
                        "LOWER(t.etudiant.filiere.nom) LIKE LOWER(CONCAT('%', :motCle, '%'))) " +
                        "AND (:date IS NULL OR t.dateDeDepot = :date) " +
                        "AND (:annee IS NULL OR t.annee = :annee) " +
                        "AND (:type IS NULL OR :type = '' OR t.type = :type)")
        List<TheseMemoire> rechercherAvecFiltres(String motCle, LocalDate date, Integer annee, String type);

        @Query("SELECT t.etudiant.filiere.nom, COUNT(t) FROM TheseMemoire t GROUP BY t.etudiant.filiere.nom")
        List<Object[]> countThesesByFiliere();

        @Query("SELECT t.annee, COUNT(t) FROM TheseMemoire t GROUP BY t.annee ORDER BY t.annee DESC")
        List<Object[]> countThesesByAnnee();

        @Query("SELECT DATE_FORMAT(t.dateDeDepot, '%Y-%m'), COUNT(t) FROM TheseMemoire t WHERE t.dateDeDepot >= :start GROUP BY DATE_FORMAT(t.dateDeDepot, '%Y-%m') ORDER BY DATE_FORMAT(t.dateDeDepot, '%Y-%m') ASC")
        List<Object[]> countThesesTrends(LocalDate start);

        @Query("SELECT DATE_FORMAT(t.dateDeDepot, '%Y-%m'), t.type, COUNT(t) FROM TheseMemoire t WHERE t.dateDeDepot >= :start GROUP BY DATE_FORMAT(t.dateDeDepot, '%Y-%m'), t.type ORDER BY DATE_FORMAT(t.dateDeDepot, '%Y-%m') ASC")
        List<Object[]> countTrendsByType(LocalDate start);

        @Query("SELECT t.type, COUNT(t) FROM TheseMemoire t GROUP BY t.type")
        List<Object[]> countByDepositType();

        List<TheseMemoire> findByDateDeDepotBetween(LocalDate start, LocalDate end);

        @Query("SELECT t FROM TheseMemoire t WHERE (t.type = :type OR :type = 'TOUS') AND t.annee = :annee")
        List<TheseMemoire> findByTypeAndAnnee(String type, int annee);

        @Query("SELECT t FROM TheseMemoire t WHERE (t.type = :type OR :type = 'TOUS') AND t.dateDeDepot BETWEEN :start AND :end")
        List<TheseMemoire> findByTypeAndDateDeDepotBetween(String type, LocalDate start, LocalDate end);

        @Query("SELECT COUNT(t) FROM TheseMemoire t WHERE t.type = :type AND t.annee = :annee")
        long countByTypeAndAnnee(String type, int annee);

        long countByTypeAndStatus(String type, StatutThese status);

        @Query("SELECT COUNT(t) FROM TheseMemoire t WHERE t.type = :type AND t.annee = :annee AND t.etudiant.filiere.nom = :filiereNom")
        long countByTypeAndAnneeAndFiliere(String type, int annee, String filiereNom);

        @Query("SELECT t.etudiant.filiere.nom, t.type, COUNT(t) FROM TheseMemoire t WHERE t.annee = :annee GROUP BY t.etudiant.filiere.nom, t.type")
        List<Object[]> countByFiliereAndTypeForYear(int annee);
}