package sn.uasz.SIGTMA.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = { "etudiant_id", "type" })
})
public class TheseMemoire {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le titre est obligatoire")
    private String titre;

    @NotBlank(message = "Le type est obligatoire (THESE ou MEMOIRE)")
    private String type;

    @Min(value = 2000, message = "L'année doit être valide")
    private int annee;

    private String resume;
    private String fichier;
    private LocalDate dateDeDepot;

    @Enumerated(EnumType.STRING)
    private sn.uasz.SIGTMA.enums.StatutThese status = sn.uasz.SIGTMA.enums.StatutThese.EN_ATTENTE;

    @ManyToOne
    @com.fasterxml.jackson.annotation.JsonIgnore
    private Utilisateur utilisateur;

    @ManyToOne
    private Encadrant encadrant;

    @ManyToOne
    @JoinColumn(name = "etudiant_id")
    private Etudiant etudiant;
}
