package org.example.tictactoe.config;

import org.example.tictactoe.AppUser;
import org.example.tictactoe.repository.AppUserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/login", "/register", "/error", "/css/**", "/js/**").permitAll()
                        .anyRequest().authenticated())
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/game/mode", true)
                        .permitAll())

                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .permitAll())

                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/game/**", "/login", "/register"))
                .headers(h -> h.frameOptions(f -> f.disable())); // for H2

        return http.build();
    }

    // Updated security: BCryptPasswordEncoder
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // seeder just in case
    @Bean
    CommandLineRunner initUsers(AppUserRepository repo, PasswordEncoder encoder) {
        return args -> {
            if (repo.findByUsername("haze").isEmpty()) {
                AppUser u = new AppUser();
                u.setUsername("haze");
                u.setPassword(encoder.encode("pass123"));
                u.setRole("ROLE_USER");
                repo.save(u);
            }
        };
    }
}
