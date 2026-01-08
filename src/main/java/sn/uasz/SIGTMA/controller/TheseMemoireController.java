package sn.uasz.SIGTMA.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import sn.uasz.SIGTMA.model.TheseMemoire;
import sn.uasz.SIGTMA.dto.DossierDTO;
import sn.uasz.SIGTMA.service.PdfService;
import sn.uasz.SIGTMA.service.TheseMemoireService;

import java.util.List;

@RestController
@RequestMapping("/api/theses")
@CrossOrigin("*")
public class TheseMemoireController {

    @Autowired
    private TheseMemoireService theseMemoireService;

    @Autowired
    private PdfService pdfService;

    @GetMapping("/{id}/attestation")
    public org.springframework.http.ResponseEntity<byte[]> getAttestation(@PathVariable Long id) {
        TheseMemoire these = theseMemoireService.trouverThese(id);
        if (these == null)
            return org.springframework.http.ResponseEntity.notFound().build();

        java.util.Map<String, Object> data = new java.util.HashMap<>();
        data.put("type", these.getType());
        data.put("nomEtudiant", these.getEtudiant().getPrenom() + " " + these.getEtudiant().getNom());
        data.put("filiere",
                these.getEtudiant().getFiliere() != null ? these.getEtudiant().getFiliere().getNom() : "N/A");
        data.put("titre", these.getTitre());
        data.put("date",
                java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy").format(java.time.LocalDate.now()));

        byte[] pdf = pdfService.generatePdfFromHtml("pdf/attestation", data);

        return org.springframework.http.ResponseEntity.ok()
                .contentType(org.springframework.http.MediaType.APPLICATION_PDF)
                .header(org.springframework.http.HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"attestation_depot_" + id + ".pdf\"")
                .body(pdf);
    }

    @PostMapping(path = "/depot", consumes = { org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE })
    public TheseMemoire deposerDossier(
            @RequestPart("dossier") String dossierJson,
            @RequestPart("fichier") org.springframework.web.multipart.MultipartFile fichier)
            throws com.fasterxml.jackson.core.JsonProcessingException {
        com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
        DossierDTO dossierDTO = mapper.readValue(dossierJson, DossierDTO.class);
        return theseMemoireService.deposerDossier(dossierDTO, fichier);
    }

    @PostMapping(consumes = { "multipart/form-data" })
    public TheseMemoire ajouterThese(
            @RequestParam("titre") String titre,
            @RequestParam("type") String type,
            @RequestParam("annee") int annee,
            @RequestParam("resume") String resume,
            @RequestParam("utilisateurId") Long utilisateurId,
            @RequestParam(value = "fichier", required = false) org.springframework.web.multipart.MultipartFile fichier) {

        TheseMemoire these = new TheseMemoire();
        these.setTitre(titre);
        these.setType(type);
        these.setAnnee(annee);
        these.setResume(resume); // Correction de setResumer en setResume si nécessaire

        // Logique de sauvegarde de fichier simplifiée pour l'exemple
        if (fichier != null && !fichier.isEmpty()) {
            String fileName = System.currentTimeMillis() + "_" + fichier.getOriginalFilename();
            // Dans un cas réel, sauvegarder sur le disque ou S3
            these.setFichier(fileName);
        }

        // On peut récupérer l'utilisateur via son ID (ajouter la logique service si
        // besoin)
        // these.setUtilisateur(...);

        return theseMemoireService.ajouterThese(these);
    }

    @GetMapping
    public List<TheseMemoire> listerTheses() {
        return theseMemoireService.listerTheses();
    }

    @GetMapping("/recents")
    public List<TheseMemoire> listerRecents() {
        return theseMemoireService.listerThesesRecentes();
    }

    @PostMapping("/{id}/valider")
    public TheseMemoire validerThese(@PathVariable Long id) {
        return theseMemoireService.validerThese(id);
    }

    @PostMapping("/{id}/rejeter")
    public TheseMemoire rejeterThese(@PathVariable Long id) {
        return theseMemoireService.rejeterThese(id);
    }

    @GetMapping("/recherche")
    public List<TheseMemoire> rechercher(
            @RequestParam(required = false) String motCle,
            @RequestParam(required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) java.time.LocalDate date,
            @RequestParam(required = false) Integer annee,
            @RequestParam(required = false) String type) {
        return theseMemoireService.rechercher(motCle, date, annee, type);
    }

    @GetMapping("/{id}")
    public TheseMemoire getThese(@PathVariable Long id) {
        return theseMemoireService.trouverThese(id);
    }

    @GetMapping("/files/{filename:.+}")
    @ResponseBody
    public org.springframework.http.ResponseEntity<org.springframework.core.io.Resource> serveFile(
            @PathVariable String filename,
            @RequestParam(defaultValue = "false") boolean download) {
        try {
            java.nio.file.Path file = java.nio.file.Paths.get("uploads").resolve(filename);
            if (!java.nio.file.Files.exists(file)) {
                return org.springframework.http.ResponseEntity.notFound().build();
            }
            org.springframework.core.io.Resource resource = new org.springframework.core.io.UrlResource(file.toUri());

            String disposition = download ? "attachment" : "inline";

            return org.springframework.http.ResponseEntity.ok()
                    .contentType(org.springframework.http.MediaType.APPLICATION_PDF)
                    .header(org.springframework.http.HttpHeaders.CONTENT_DISPOSITION,
                            disposition + "; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);
        } catch (Exception e) {
            return org.springframework.http.ResponseEntity.internalServerError().build();
        }
    }
}