package sn.uasz.SIGTMA.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import sn.uasz.SIGTMA.service.CustomUserDetailsService;

@Configuration(proxyBeanMethods = false)
@EnableWebSecurity
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;
    private final CustomAuthenticationSuccessHandler successHandler;

    public SecurityConfig(CustomUserDetailsService userDetailsService,
            CustomAuthenticationSuccessHandler successHandler) {
        this.userDetailsService = userDetailsService;
        this.successHandler = successHandler;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/css/**", "/js/**", "/images/**", "/webjars/**").permitAll()
                        .requestMatchers("/login", "/register", "/api/utilisateurs/inscription").permitAll()
                        .requestMatchers("/admin/**", "/api/utilisateurs/**", "/api/admin/stats/**")
                        .hasRole("ADMINISTRATEUR")
                        .requestMatchers("/api/utilisateurs/profil").authenticated()
                        .requestMatchers("/bibliothecaire/**", "/api/bibliothecaire/stats/**")
                        .hasAnyRole("BIBLIOTHECAIRE", "ADMINISTRATEUR")
                        .requestMatchers("/api/theses/files/**", "/api/theses/recherche", "/api/theses").authenticated()
                        .requestMatchers("/api/theses/*/valider", "/api/theses/*/rejeter")
                        .hasAnyRole("BIBLIOTHECAIRE", "ADMINISTRATEUR")
                        .requestMatchers("/api/filieres/**")
                        .hasAnyRole("ADMINISTRATEUR", "AIDE_BIBLIOTHECAIRE", "BIBLIOTHECAIRE")
                        .requestMatchers("/aide-bibliothecaire/**", "/api/aide/**", "/api/theses/depot",
                                "/api/etudiants/**", "/api/encadrants/**")
                        .hasRole("AIDE_BIBLIOTHECAIRE")
                        .anyRequest().authenticated())
                .formLogin(form -> form
                        .loginPage("/login")
                        .successHandler(successHandler)
                        .permitAll())
                .logout(logout -> logout
                        .logoutSuccessUrl("/login?logout")
                        .permitAll())
                .headers(headers -> headers
                        .frameOptions(frame -> frame.sameOrigin()));

        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider(PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
