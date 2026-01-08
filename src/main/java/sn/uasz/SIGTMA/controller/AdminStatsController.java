package sn.uasz.SIGTMA.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import sn.uasz.SIGTMA.repository.TheseMemoireRepository;
import sn.uasz.SIGTMA.repository.UtilisateurRepository;
import sn.uasz.SIGTMA.enums.StatutThese;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/stats")
public class AdminStatsController {

    @Autowired
    private TheseMemoireRepository theseRepository;

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @GetMapping("/summary")
    public Map<String, Object> getSummary(@RequestParam(required = false) String period) {
        Map<String, Object> stats = new HashMap<>();
        LocalDate now = LocalDate.now();
        LocalDate start;
        LocalDate end = now;

        if ("journalier".equals(period)) {
            start = now;
        } else if ("mensuel".equals(period)) {
            start = now.with(TemporalAdjusters.firstDayOfMonth());
        } else if ("annuel".equals(period)) {
            start = now.with(TemporalAdjusters.firstDayOfYear());
        } else {
            // Global
            start = LocalDate.of(2000, 1, 1);
        }

        stats.put("totalTheses", theseRepository.countByDateDeDepotBetween(start, end));
        stats.put("totalUsers", utilisateurRepository.count());
        stats.put("thesesValidees",
                theseRepository.countByStatusAndDateDeDepotBetween(StatutThese.VALIDEE, start, end));
        stats.put("thesesRejetees",
                theseRepository.countByStatusAndDateDeDepotBetween(StatutThese.REJETEE, start, end));
        stats.put("thesesEnAttente",
                theseRepository.countByStatusAndDateDeDepotBetween(StatutThese.EN_ATTENTE, start, end));

        return stats;
    }

    @GetMapping("/by-filiere")
    public List<Map<String, Object>> getStatsByFiliere() {
        List<Object[]> results = theseRepository.countThesesByFiliere();
        List<Map<String, Object>> stats = new ArrayList<>();

        long total = theseRepository.count();

        for (Object[] row : results) {
            Map<String, Object> map = new HashMap<>();
            map.put("filiere", row[0]);
            map.put("count", row[1]);
            if (total > 0) {
                map.put("percentage", Math.round(((Long) row[1] * 100.0) / total));
            } else {
                map.put("percentage", 0);
            }
            stats.add(map);
        }
        return stats;
    }

    @GetMapping("/trends")
    public Map<String, Object> getTrends() {
        LocalDate sixMonthsAgo = LocalDate.now().minusMonths(6).with(TemporalAdjusters.firstDayOfMonth());
        List<Object[]> results = theseRepository.countTrendsByType(sixMonthsAgo);

        Map<String, Map<String, Long>> dataMap = new HashMap<>(); // Month -> {Type -> Count}
        List<String> months = new ArrayList<>();

        for (Object[] row : results) {
            String month = (String) row[0];
            String type = (String) row[1];
            long count = (Long) row[2];

            if (!months.contains(month))
                months.add(month);
            dataMap.computeIfAbsent(month, k -> new HashMap<>()).put(type, count);
        }

        List<Long> theses = new ArrayList<>();
        List<Long> memoires = new ArrayList<>();

        for (String m : months) {
            theses.add(dataMap.get(m).getOrDefault("THESE", 0L));
            memoires.add(dataMap.get(m).getOrDefault("MEMOIRE", 0L));
        }

        Map<String, Object> response = new HashMap<>();
        response.put("labels", months);
        response.put("theses", theses);
        response.put("memoires", memoires);
        return response;
    }

    @GetMapping("/distribution")
    public List<Map<String, Object>> getDistribution() {
        List<Object[]> results = theseRepository.countByDepositType();
        List<Map<String, Object>> stats = new ArrayList<>();

        for (Object[] row : results) {
            Map<String, Object> map = new HashMap<>();
            map.put("type", row[0]);
            map.put("count", row[1]);
            stats.add(map);
        }
        return stats;
    }

    @GetMapping("/yearly-detail")
    public List<Map<String, Object>> getYearlyDetail() {
        List<Object[]> results = theseRepository.countThesesByAnnee();
        List<Map<String, Object>> stats = new ArrayList<>();

        for (Object[] row : results) {
            int annee = (Integer) row[0];
            Map<String, Object> map = new HashMap<>();
            map.put("annee", annee);
            map.put("total", row[1]);
            map.put("theses", theseRepository.countByTypeAndAnnee("THESE", annee));
            map.put("memoires", theseRepository.countByTypeAndAnnee("MEMOIRE", annee));
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

        // Find top filière for context (like "dont X en médecine")
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
}
