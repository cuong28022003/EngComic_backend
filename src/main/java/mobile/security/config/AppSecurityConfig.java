package mobile.security.config;

import mobile.security.JWT.AuthEntryPointJwt;
import mobile.security.Service.AppUserDetailService;
import mobile.security.filter.AuthTokenFilter;
import mobile.common.UserPermission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class AppSecurityConfig {

    @Autowired
    AppUserDetailService userDetailsService;

    @Autowired
    private AuthEntryPointJwt unauthorizedHandler;

    @Bean
    public AuthTokenFilter authenticationJwtTokenFilter() {
        return new AuthTokenFilter();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.cors(cors -> cors.disable())
            .csrf(csrf -> csrf.disable())
            .exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler))
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**", "/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/admin/**").hasAnyAuthority(UserPermission.ADMIN_READ.getPermission())
                .requestMatchers(HttpMethod.POST, "/api/admin/**").hasAnyAuthority(UserPermission.ADMIN_WRITE.getPermission())
                .requestMatchers(HttpMethod.GET, "/api/user/**").hasAnyAuthority(UserPermission.USER_READ.getPermission(), UserPermission.ADMIN_READ.getPermission())
                .requestMatchers(HttpMethod.POST, "/api/user/**").hasAnyAuthority(UserPermission.USER_WRITE.getPermission(), UserPermission.ADMIN_WRITE.getPermission())
                .anyRequest().permitAll()
            );

        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
