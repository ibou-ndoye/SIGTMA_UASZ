package sn.uasz.SIGTMA.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import sn.uasz.SIGTMA.repository.TheseMemoireRepository;
import sn.uasz.SIGTMA.enums.StatutThese;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

@RestController
@RequestMapping("/api/bibliothecaire/stats")
@CrossOrigin("*")
public class BibliothecaireStatsController {

    @Autowired
    private TheseMemoireRepository theseRepository;

    @GetMapping("/summary")
    public Map<String, Object> getSummary() {
        Map<String, Object> stats = new HashMap<>();

        long archived = theseRepository.countByStatus(StatutThese.VALIDEE);
        long pending = theseRepository.countByStatus(StatutThese.EN_ATTENTE);

        // Change "Recent" logic to "Last 30 Days"
        LocalDate now = LocalDate.now();
        LocalDate thirtyDaysAgo = now.minusDays(30);
        long recent = theseRepository.countByDateDeDepotBetween(thirtyDaysAgo, now);

        long totalTheses = theseRepository.countByTypeAndStatus("THESE", StatutThese.VALIDEE);
        long totalMemoires = theseRepository.countByTypeAndStatus("MEMOIRE", StatutThese.VALIDEE);

        stats.put("archived", archived);
        stats.put("totalTheses", totalTheses);
        stats.put("totalMemoires", totalMemoires);
        stats.put("pending", pending);
        stats.put("recentToday", recent);
        stats.put("capacity", "92%");

        return stats;
    }

    @GetMapping("/yearly-detail")
    public List<Map<String, Object>> getYearlyDetail() {
        List<Object[]> rows = theseRepository.countThesesByAnnee();
        List<Map<String, Object>> result = new java.util.ArrayList<>();

        for (Object[] row : rows) {
            int annee = (Integer) row[0];
            Map<String, Object> map = new HashMap<>();
            map.put("annee", annee);
            map.put("theses", theseRepository.countByTypeAndAnnee("THESE", annee));
            map.put("memoires", theseRepository.countByTypeAndAnnee("MEMOIRE", annee));
            map.put("total", row[1]);
            result.add(map);
        }
        return result;
    }

    @GetMapping("/distribution")
    public List<Map<String, Object>> getDistribution() {
        List<Object[]> results = theseRepository.countByDepositType();
        List<Map<String, Object>> stats = new java.util.ArrayList<>();
        for (Object[] row : results) {
            Map<String, Object> map = new HashMap<>();
            map.put("type", row[0]);
            map.put("count", row[1]);
            stats.add(map);
        }
        return stats;
    }

    @GetMapping("/publish-summary")
    public Map<String, String> getPublishSummary(@RequestParam(required = false) Integer annee) {
        int targetYear = (annee != null) ? annee : LocalDate.now().getYear();
        int prevYear = targetYear - 1;

        long tMemoires = theseRepository.countByTypeAndAnnee("MEMOIRE", targetYear);
        long tTheses = theseRepository.countByTypeAndAnnee("THESE", targetYear);

        List<Object[]> filiereStats = theseRepository.countByFiliereAndTypeForYear(targetYear);
        String topFiliere = "";
        long topFiliereCount = 0;
        for (Object[] row : filiereStats) {
            long count = (Long) row[2];
            if (count > topFiliereCount) {
                topFiliereCount = count;
                topFiliere = (String) row[0];
            }
        }

        long pMemoires = theseRepository.countByTypeAndAnnee("MEMOIRE", prevYear);
        long pTheses = theseRepository.countByTypeAndAnnee("THESE", prevYear);

        StringBuilder sb = new StringBuilder();
        sb.append("Bonjour,\n");
        sb.append(
                "Je vous prie de bien vouloir trouver ci-joint le répertoire des mémoires et thèses déposés à la Bibliothèque Centrale en ")
                .append(targetYear).append(".\n\n");
        sb.append("Au total, ").append(tMemoires).append(" mémoires et ").append(tTheses).append(" thèses");
        if (!topFiliere.isEmpty()) {
            sb.append(" (dont ").append(topFiliereCount).append(" en ").append(topFiliere).append(")");
        }
        sb.append(" ont été déposés.\n\n");
        sb.append("Pour rappel, en ").append(prevYear).append(", ").append(pMemoires).append(" mémoires et ")
                .append(pTheses).append(" thèses ont été déposés.\n\n");
        sb.append("Cordialement,\n\n");
        sb.append("La Direction de la Bibliothèque Centrale\n");
        sb.append("Université Assane Seck de Ziguinchor");

        Map<String, String> response = new HashMap<>();
        response.put("content", sb.toString());
        return response;
    }

    @GetMapping("/reports/filiere")
    public Map<String, Long> getStatsByFiliere() {
        Map<String, Long> result = new HashMap<>();
        List<Object[]> rows = theseRepository.countThesesByFiliere();
        if (rows != null) {
            for (Object[] row : rows) {
                if (row[0] != null && row[1] != null) {
                    String filiere = (String) row[0];
                    Long count = ((Number) row[1]).longValue();
                    result.put(filiere, count);
                }
            }
        }
        return result;
    }

    @GetMapping("/reports/annee")
    public Map<Integer, Long> getStatsByAnnee() {
        Map<Integer, Long> result = new HashMap<>();
        List<Object[]> rows = theseRepository.countThesesByAnnee();
        if (rows != null) {
            for (Object[] row : rows) {
                if (row[0] != null && row[1] != null) {
                    Integer annee = (Integer) row[0];
                    Long count = ((Number) row[1]).longValue();
                    result.put(annee, count);
                }
            }
        }
        return result;
    }
}
