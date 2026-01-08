package sn.uasz.SIGTMA.dto;

import lombok.Data;

@Data
public class DossierDTO {
    // Étudiant
    private String nomEtudiant;
    private String prenomEtudiant;
    private String matriculeEtudiant;
    private String emailEtudiant;
    private String anneeAcademique;
    private Long filiereId;

    // Encadrant
    private String nomEncadrant;
    private String prenomEncadrant;
    private String gradeEncadrant;
    private String emailEncadrant;

    // IDs pour sélection directe (optionnel)
    private Long etudiantId;
    private Long encadrantId;

    // Thèse/Mémoire
    private String titre;
    private String type; // THESE ou MEMOIRE
    private int annee;
    private String resume;
}
