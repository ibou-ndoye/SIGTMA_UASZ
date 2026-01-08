package sn.uasz.SIGTMA.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sn.uasz.SIGTMA.model.RapportStatistique;
import sn.uasz.SIGTMA.model.TheseMemoire;
import sn.uasz.SIGTMA.repository.RapportStatistiqueRepository;
import sn.uasz.SIGTMA.repository.TheseMemoireRepository;

import java.time.LocalDate;
import java.util.List;

@Service
public class RapportStatistiqueService {

    @Autowired
    private RapportStatistiqueRepository rapportRepository;

    @Autowired
    private TheseMemoireRepository theseRepository;

    public RapportStatistique genererRapport(RapportStatistique rapport) {
        rapport.setDateGeneration(LocalDate.now());
        // Ici, on pourrait ajouter une logique pour calculer des stats réelles
        // et les mettre dans rapport.setResultat(...)
        String type = rapport.getTypeRapport();
        LocalDate startDate = rapport.getDateDebut();
        LocalDate endDate = rapport.getDateFin();

        if (type != null && startDate != null && endDate != null) {
            List<TheseMemoire> theses = theseRepository.findByTypeAndDateDeDepotBetween(type, startDate, endDate);
            String result = "Nombre de thèses/mémoires de type '" + type + "' entre " + startDate + " et " + endDate
                    + ": " + theses.size();
            rapport.setResultat(result);
        } else {
            rapport.setResultat("Critères de rapport incomplets pour générer des statistiques détaillées.");
        }
        return rapportRepository.save(rapport);
    }

    public List<RapportStatistique> listerRapports() {
        return rapportRepository.findAll();
    }

    public List<TheseMemoire> getThesesPourRapport(String type, LocalDate start, LocalDate end) {
        return theseRepository.findByTypeAndDateDeDepotBetween(type, start, end);
    }

    public List<TheseMemoire> getThesesPourRapportAnnuel(String type, int annee) {
        return theseRepository.findByTypeAndAnnee(type, annee);
    }

    public String genererRapportCSV(String type, LocalDate start, LocalDate end) {
        List<TheseMemoire> theses = theseRepository.findByTypeAndDateDeDepotBetween(type, start, end);
        return convertToCSV(theses);
    }

    public String genererRapportCSVParAnnee(String type, int annee) {
        List<TheseMemoire> theses = theseRepository.findByTypeAndAnnee(type, annee);
        return convertToCSV(theses);
    }

    public java.util.Map<String, java.util.Map<String, List<TheseMemoire>>> getThesesGroupeesParUfrEtFiliere(
            String type, int annee) {
        List<TheseMemoire> theses = theseRepository.findByTypeAndAnnee(type, annee);

        return theses.stream()
                .collect(java.util.stream.Collectors.groupingBy(
                        t -> (t.getEtudiant() != null && t.getEtudiant().getFiliere() != null
                                && t.getEtudiant().getFiliere().getNomUfr() != null)
                                        ? t.getEtudiant().getFiliere().getNomUfr()
                                        : "UFR NON DÉFINIE",
                        java.util.stream.Collectors.groupingBy(
                                t -> (t.getEtudiant() != null && t.getEtudiant().getFiliere() != null)
                                        ? t.getEtudiant().getFiliere().getNom()
                                        : "DÉPARTEMENT NON DÉFINI")));
    }

    private String convertToCSV(List<TheseMemoire> theses) {
        StringBuilder csv = new StringBuilder();
        csv.append("ID;Titre;Type;Annee;Etudiant;Encadrant;Filiere;Date Depot;Statut\n");

        for (TheseMemoire t : theses) {
            String etudiant = t.getEtudiant() != null ? t.getEtudiant().getPrenom() + " " + t.getEtudiant().getNom()
                    : "-";
            String encadrant = t.getEncadrant() != null ? t.getEncadrant().getPrenom() + " " + t.getEncadrant().getNom()
                    : "-";
            String filiere = (t.getEtudiant() != null && t.getEtudiant().getFiliere() != null)
                    ? t.getEtudiant().getFiliere().getNom()
                    : "-";

            csv.append(t.getId()).append(";")
                    .append(escapeSpecialCharacters(t.getTitre())).append(";")
                    .append(t.getType()).append(";")
                    .append(t.getAnnee()).append(";")
                    .append(escapeSpecialCharacters(etudiant)).append(";")
                    .append(escapeSpecialCharacters(encadrant)).append(";")
                    .append(escapeSpecialCharacters(filiere)).append(";")
                    .append(t.getDateDeDepot()).append(";")
                    .append(t.getStatus()).append("\n");
        }

        return csv.toString();
    }

    private String escapeSpecialCharacters(String data) {
        if (data == null)
            return "";
        String escapedData = data.replaceAll("\\R", " ");
        if (data.contains(";") || data.contains("\"") || data.contains("'")) {
            data = data.replace("\"", "\"\"");
            escapedData = "\"" + data + "\"";
        }
        return escapedData;
    }
}