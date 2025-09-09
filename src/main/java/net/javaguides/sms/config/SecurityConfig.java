package net.javaguides.sms.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder encoder) {
        InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
        manager.createUser(User.withUsername("admin").password(encoder.encode("admin123")).roles("ADMIN").build());
        manager.createUser(User.withUsername("staff").password(encoder.encode("staff123")).roles("STAFF").build());
        manager.createUser(User.withUsername("viewer").password(encoder.encode("viewer123")).roles("VIEWER").build());
        // Additional roles
        manager.createUser(User.withUsername("principal").password(encoder.encode("principal123")).roles("PRINCIPAL").build());
        manager.createUser(User.withUsername("viceprincipal").password(encoder.encode("vice123")).roles("VICE_PRINCIPAL").build());
        manager.createUser(User.withUsername("registrar").password(encoder.encode("registrar123")).roles("REGISTRAR").build());
        manager.createUser(User.withUsername("hod").password(encoder.encode("hod123")).roles("HOD").build());
        manager.createUser(User.withUsername("librarian").password(encoder.encode("librarian123")).roles("LIBRARIAN").build());
        manager.createUser(User.withUsername("accountant").password(encoder.encode("accountant123")).roles("ACCOUNTANT").build());
        manager.createUser(User.withUsername("counselor").password(encoder.encode("counselor123")).roles("COUNSELOR").build());
        manager.createUser(User.withUsername("parent").password(encoder.encode("parent123")).roles("PARENT").build());
        manager.createUser(User.withUsername("student").password(encoder.encode("student123")).roles("STUDENT").build());
        manager.createUser(User.withUsername("guest").password(encoder.encode("guest123")).roles("GUEST").build());
        return manager;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/css/**", "/js/**", "/images/**").permitAll()
                .requestMatchers("/login", "/error").permitAll()
                .requestMatchers("/", "/dashboard", "/students", "/teachers", "/courses", "/timetables").hasAnyRole(
                        "ADMIN","STAFF","VIEWER","PRINCIPAL","VICE_PRINCIPAL","REGISTRAR","HOD","LIBRARIAN","ACCOUNTANT","COUNSELOR","PARENT","STUDENT","GUEST")
                .requestMatchers("/students/**", "/teachers/**", "/courses/**", "/timetables/**").hasAnyRole(
                        "ADMIN","STAFF","REGISTRAR","HOD","PRINCIPAL","VICE_PRINCIPAL")
                .requestMatchers("/enrollments/**", "/attendance/**").hasAnyRole(
                        "ADMIN","STAFF","REGISTRAR","HOD","PRINCIPAL","VICE_PRINCIPAL")
                .requestMatchers("/library/**").hasAnyRole("LIBRARIAN","ADMIN","PRINCIPAL")
                .requestMatchers("/finance/**").hasAnyRole("ACCOUNTANT","ADMIN","PRINCIPAL")
                .requestMatchers("/counsel/**").hasAnyRole("COUNSELOR","ADMIN","PRINCIPAL")
                .requestMatchers("/files/**").hasAnyRole("ADMIN","STAFF","LIBRARIAN","PRINCIPAL")
                .requestMatchers("/chat/**").authenticated()
                .anyRequest().authenticated()
            )
            .exceptionHandling(ex -> ex
                .accessDeniedPage("/access-denied")
            )
            .formLogin(login -> login
                .loginPage("/login")
                .defaultSuccessUrl("/", true)
                .permitAll()
            )
            .logout(logout -> logout
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .logoutSuccessUrl("/login?logout")
                .permitAll()
            )
            .httpBasic(Customizer.withDefaults());

        return http.build();
    }
}
