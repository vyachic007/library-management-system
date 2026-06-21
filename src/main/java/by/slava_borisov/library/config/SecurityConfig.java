package by.slava_borisov.library.config;

import by.slava_borisov.library.security.CustomAccessDeniedHandler;
import by.slava_borisov.library.security.CustomAuthenticationEntryPoint;
import by.slava_borisov.library.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserDetailsService userDetailsService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();

        authenticationProvider.setUserDetailsService(userDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder());

        return authenticationProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)

                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(customAuthenticationEntryPoint)
                        .accessDeniedHandler(customAccessDeniedHandler)
                )

                .authenticationProvider(authenticationProvider())

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(antMatcher("/api/auth/**")).permitAll()

                        .requestMatchers(antMatcher(HttpMethod.GET, "/api/books/**")).hasAnyRole("USER", "ADMIN")
                        .requestMatchers(antMatcher(HttpMethod.POST, "/api/books/**")).hasRole("ADMIN")
                        .requestMatchers(antMatcher(HttpMethod.PUT, "/api/books/**")).hasRole("ADMIN")
                        .requestMatchers(antMatcher(HttpMethod.DELETE, "/api/books/**")).hasRole("ADMIN")

                        .requestMatchers(antMatcher(HttpMethod.GET, "/api/authors/**")).hasAnyRole("USER", "ADMIN")
                        .requestMatchers(antMatcher(HttpMethod.POST, "/api/authors/**")).hasRole("ADMIN")
                        .requestMatchers(antMatcher(HttpMethod.PUT, "/api/authors/**")).hasRole("ADMIN")
                        .requestMatchers(antMatcher(HttpMethod.DELETE, "/api/authors/**")).hasRole("ADMIN")

                        .requestMatchers(antMatcher(HttpMethod.GET, "/api/categories/**")).hasAnyRole("USER", "ADMIN")
                        .requestMatchers(antMatcher(HttpMethod.POST, "/api/categories/**")).hasRole("ADMIN")
                        .requestMatchers(antMatcher(HttpMethod.PUT, "/api/categories/**")).hasRole("ADMIN")
                        .requestMatchers(antMatcher(HttpMethod.DELETE, "/api/categories/**")).hasRole("ADMIN")

                        .requestMatchers(antMatcher(HttpMethod.GET, "/api/book-copies/**")).hasAnyRole("USER", "ADMIN")
                        .requestMatchers(antMatcher(HttpMethod.POST, "/api/book-copies/**")).hasRole("ADMIN")
                        .requestMatchers(antMatcher(HttpMethod.PUT, "/api/book-copies/**")).hasRole("ADMIN")
                        .requestMatchers(antMatcher(HttpMethod.PATCH, "/api/book-copies/**")).hasRole("ADMIN")
                        .requestMatchers(antMatcher(HttpMethod.DELETE, "/api/book-copies/**")).hasRole("ADMIN")

                        .requestMatchers(antMatcher(HttpMethod.GET, "/api/users/*/profile")).hasAnyRole("USER", "ADMIN")
                        .requestMatchers(antMatcher(HttpMethod.PUT, "/api/users/*/profile")).hasAnyRole("USER", "ADMIN")
                        .requestMatchers(antMatcher("/api/users/**")).hasRole("ADMIN")

                        .requestMatchers(antMatcher(HttpMethod.GET, "/api/borrow-records")).hasRole("ADMIN")
                        .requestMatchers(antMatcher(HttpMethod.GET, "/api/borrow-records/active")).hasRole("ADMIN")
                        .requestMatchers(antMatcher(HttpMethod.GET, "/api/borrow-records/overdue")).hasRole("ADMIN")
                        .requestMatchers(antMatcher(HttpMethod.GET, "/api/borrow-records/book-copy/**")).hasRole("ADMIN")

                        .requestMatchers(antMatcher(HttpMethod.GET, "/api/borrow-records/user/**")).hasAnyRole("USER", "ADMIN")
                        .requestMatchers(antMatcher(HttpMethod.GET, "/api/borrow-records/*")).hasAnyRole("USER", "ADMIN")

                        .requestMatchers(antMatcher(HttpMethod.POST, "/api/borrow-records/rent")).hasAnyRole("USER", "ADMIN")
                        .requestMatchers(antMatcher(HttpMethod.POST, "/api/borrow-records/return")).hasAnyRole("USER", "ADMIN")
                        .requestMatchers(antMatcher(HttpMethod.POST, "/api/borrow-records/*/extend")).hasAnyRole("USER", "ADMIN")

                        .requestMatchers(
                                antMatcher("/v3/api-docs/**"),
                                antMatcher("/swagger-ui/**"),
                                antMatcher("/swagger-ui.html"),
                                antMatcher("/webjars/**")
                        ).permitAll()

                        .anyRequest().authenticated()
                )

                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)

                .build();
    }
}