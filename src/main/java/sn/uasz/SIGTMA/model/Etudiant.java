package sn.uasz.SIGTMA.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Etudiant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nom;
    private String prenom;
    private String matricule;
    private String email;
    private String anneeAcademique;

    @ManyToOne
    private Filiere filiere;

    @OneToMany(mappedBy = "etudiant")
    @JsonIgnore
    private java.util.List<TheseMemoire> theses;
}
