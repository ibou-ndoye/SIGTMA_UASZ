package sn.uasz.SIGTMA.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sn.uasz.SIGTMA.model.RapportStatistique;
import sn.uasz.SIGTMA.service.PdfService;
import sn.uasz.SIGTMA.service.RapportStatistiqueService;
import sn.uasz.SIGTMA.model.TheseMemoire;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Base64;
import java.nio.file.Files;
import java.nio.file.Paths;

@RestController
@RequestMapping("/api/rapports")
@CrossOrigin("*")
public class RapportStatistiqueController {

    @Autowired
    private RapportStatistiqueService rapportService;

    @Autowired
    private PdfService pdfService;

    @GetMapping("/export/pdf")
    public ResponseEntity<byte[]> exportRapportPdf(
            @RequestParam String type,
            @RequestParam int annee) {

        Map<String, Map<String, List<TheseMemoire>>> groupedData = rapportService.getThesesGroupeesParUfrEtFiliere(type,
                annee);

        Map<String, Object> data = new HashMap<>();
        data.put("data", groupedData);
        data.put("annee", annee);
        data.put("typePlural", type.equals("THESE") ? "THÈSES" : (type.equals("MEMOIRE") ? "MÉMOIRES" : "DÉPÔTS"));

        // Logo base64
        try {
            byte[] logoBytes = Files.readAllBytes(Paths.get("src/main/resources/static/images/uasz-logo.png"));
            String base64Logo = "data:image/png;base64," + Base64.getEncoder().encodeToString(logoBytes);
            data.put("logo", base64Logo);
        } catch (Exception e) {
            data.put("logo", "");
        }

        byte[] pdf = pdfService.generatePdfFromHtml("pdf/repertoire", data);

        String filename = "repertoire_" + type.toLowerCase() + "_" + annee + ".pdf";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

    @PostMapping
    public RapportStatistique genererRapport(@RequestBody RapportStatistique rapport) {
        return rapportService.genererRapport(rapport);
    }

    @GetMapping
    public List<RapportStatistique> listerRapports() {
        return rapportService.listerRapports();
    }

    @GetMapping("/preview")
    public ResponseEntity<?> previewRapport(
            @RequestParam String type,
            @RequestParam(required = false) String start,
            @RequestParam(required = false) String end,
            @RequestParam(required = false) Integer annee) {
        try {
            if (annee != null) {
                return ResponseEntity.ok(rapportService.getThesesPourRapportAnnuel(type, annee));
            } else {
                if (start == null || end == null || start.isEmpty() || end.isEmpty()) {
                    return ResponseEntity.badRequest().body("Dates manquantes pour le filtrage par période");
                }
                LocalDate startDate = LocalDate.parse(start);
                LocalDate endDate = LocalDate.parse(end);
                return ResponseEntity.ok(rapportService.getThesesPourRapport(type, startDate, endDate));
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Erreur serveur : " + e.getMessage());
        }
    }

    @GetMapping("/export")
    public ResponseEntity<byte[]> exportRapport(
            @RequestParam String type,
            @RequestParam(required = false) String start,
            @RequestParam(required = false) String end,
            @RequestParam(required = false) Integer annee) {

        String csv;
        String filename;

        if (annee != null) {
            csv = rapportService.genererRapportCSVParAnnee(type, annee);
            filename = "rapport_annuel_" + type + "_" + annee + ".csv";
        } else {
            LocalDate startDate = LocalDate.parse(start);
            LocalDate endDate = LocalDate.parse(end);
            csv = rapportService.genererRapportCSV(type, startDate, endDate);
            filename = "rapport_activite_" + type + "_" + start + "_au_" + end + ".csv";
        }

        byte[] content = csv.getBytes();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(content);
    }
}