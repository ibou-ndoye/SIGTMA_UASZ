package sn.uasz.SIGTMA.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sn.uasz.SIGTMA.repository.EncadrantRepository;
import sn.uasz.SIGTMA.repository.EtudiantRepository;
import sn.uasz.SIGTMA.repository.FiliereRepository;
import sn.uasz.SIGTMA.repository.TheseMemoireRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/aide/stats")
@CrossOrigin("*")
public class AideStatsController {

        @Autowired
        private EtudiantRepository etudiantRepository;

        @Autowired
        private EncadrantRepository encadrantRepository;

        @Autowired
        private FiliereRepository filiereRepository;

        @Autowired
        private TheseMemoireRepository theseRepository;

        private static final Logger logger = LoggerFactory.getLogger(AideStatsController.class);

        @GetMapping("/summary")
        public Map<String, Object> getSummary() {
                Map<String, Object> stats = new HashMap<>();
                stats.put("totalEtudiants", etudiantRepository.count());
                stats.put("totalEncadrants", encadrantRepository.count());
                stats.put("totalFilieres", filiereRepository.count());
                stats.put("todayUpdates", theseRepository.countByDateDeDepot(java.time.LocalDate.now()));

                logger.info("Aide Stats - Etudiants: {}, Encadrants: {}, Filieres: {}, Today: {}",
                                stats.get("totalEtudiants"), stats.get("totalEncadrants"), stats.get("totalFilieres"),
                                stats.get("todayUpdates"));

                List<Map<String, Object>> recentData = theseRepository.findTop5ByOrderByDateDeDepotDesc().stream()
                                .map(t -> {
                                        Map<String, Object> map = new HashMap<>();
                                        map.put("nomEtudiant",
                                                        t.getEtudiant() != null
                                                                        ? (t.getEtudiant().getPrenom() + " "
                                                                                        + t.getEtudiant().getNom())
                                                                        : "Inconnu");
                                        map.put("filiere",
                                                        (t.getEtudiant() != null
                                                                        && t.getEtudiant().getFiliere() != null)
                                                                                        ? t.getEtudiant().getFiliere()
                                                                                                        .getNom()
                                                                                        : "-");
                                        map.put("date", t.getDateDeDepot() != null ? t.getDateDeDepot().toString()
                                                        : "-");
                                        map.put("id", t.getId());
                                        return map;
                                })
                                .collect(Collectors.toList());

                stats.put("recentDeposits", recentData);
                return stats;
        }
}
