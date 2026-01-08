package sn.uasz.SIGTMA.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import sn.uasz.SIGTMA.model.Utilisateur;
import sn.uasz.SIGTMA.repository.UtilisateurRepository;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner init(UtilisateurRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            // Création de l'Administrateur si inexistant
            if (userRepository.findByEmail("admin@sigtma.sn").isEmpty()) {
                Utilisateur admin = new Utilisateur();
                admin.setPrenom("Admin");
                admin.setNom("SIGTMA");
                admin.setEmail("admin@sigtma.sn");
                admin.setMotDePasse(passwordEncoder.encode("admin123"));
                admin.setRole("ADMINISTRATEUR");
                admin.setSexe("M");
                admin.setAdresse("UASZ Ziguinchor");
                admin.setTelephone("330000000");
                userRepository.save(admin);
            }

            // Création du Bibliothécaire si inexistant
            if (userRepository.findByEmail("biblio@sigtma.sn").isEmpty()) {
                Utilisateur biblio = new Utilisateur();
                biblio.setPrenom("Jean");
                biblio.setNom("Biblio");
                biblio.setEmail("biblio@sigtma.sn");
                biblio.setMotDePasse(passwordEncoder.encode("biblio123"));
                biblio.setRole("BIBLIOTHECAIRE");
                biblio.setSexe("M");
                biblio.setAdresse("Bibliothèque UASZ");
                biblio.setTelephone("331111111");
                userRepository.save(biblio);
            }

            // Création de l'Aide-Bibliothécaire si inexistant
            if (userRepository.findByEmail("aide@sigtma.sn").isEmpty()) {
                Utilisateur aide = new Utilisateur();
                aide.setPrenom("Marie");
                aide.setNom("Aide");
                aide.setEmail("aide@sigtma.sn");
                aide.setMotDePasse(passwordEncoder.encode("aide123"));
                aide.setRole("AIDE_BIBLIOTHECAIRE");
                aide.setSexe("F");
                aide.setAdresse("Support SIGTMA");
                aide.setTelephone("332222222");
                userRepository.save(aide);
            }
        };
    }
}
