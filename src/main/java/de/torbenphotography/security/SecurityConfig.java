package de.torbenphotography.security;

import de.torbenphotography.services.RequestService;
import de.torbenphotography.views.LoginView;
import com.vaadin.flow.spring.security.VaadinSecurityConfigurer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

/*
 * Public pages are open to everyone, the request overview needs the admin login.
 * The admin account is configured with app.admin.username and app.admin.password
 * (environment variables ADMIN_USERNAME / ADMIN_PASSWORD).
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(jsr250Enabled = true)
public class SecurityConfig {

    private static final Logger LOG = LoggerFactory.getLogger(SecurityConfig.class);

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth -> auth.requestMatchers("/images/**").permitAll());
        return http.with(VaadinSecurityConfigurer.vaadin(), vaadin -> vaadin.loginView(LoginView.class)).build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    UserDetailsService userDetailsService(PasswordEncoder passwordEncoder,
                                          @Value("${app.admin.username:admin}") String username,
                                          @Value("${app.admin.password:}") String password) {
        if (password.isBlank()) {
            LOG.warn("Kein Admin-Passwort gesetzt (app.admin.password / ADMIN_PASSWORD), die Anmeldung ist deaktiviert");
            return new InMemoryUserDetailsManager();
        }
        return new InMemoryUserDetailsManager(User.withUsername(username)
                .password(passwordEncoder.encode(password))
                .roles(RequestService.ADMIN_ROLE)
                .build());
    }
}
