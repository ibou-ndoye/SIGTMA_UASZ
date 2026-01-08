package sn.uasz.SIGTMA.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sn.uasz.SIGTMA.repository.TheseMemoireRepository;
import sn.uasz.SIGTMA.repository.UtilisateurRepository;

import java.util.HashMap;
import java.util.Map;

@Service
public class DashboardStatsService {

    @Autowired
    private TheseMemoireRepository theseMemoireRepository;

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    public Map<String, Long> getStats() {
        Map<String, Long> stats = new HashMap<>();
        stats.put("totalTheses", theseMemoireRepository.count());
        stats.put("totalUtilisateurs", utilisateurRepository.count());
        // Add more granular stats here (e.g. count by status)
        return stats;
    }
}
