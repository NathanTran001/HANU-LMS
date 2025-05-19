package fit.se2.hanulms;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@Configuration
@EnableWebSecurity
public class SecurityCfg {
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http, UserDetailsService myUserDetailsService) throws Exception {
        return http
                .authorizeHttpRequests(req -> req
                        .requestMatchers("/myCourses").hasAnyAuthority("LECTURER", "STUDENT")
                        .requestMatchers("/admin/**").hasAuthority("ADMIN")
                        .anyRequest().authenticated()
                )
                .userDetailsService(myUserDetailsService)
                // if we don't configure formLogin, Spring Security will have 0 login methods
                .formLogin(form -> form
                        .loginPage("/login")
                        .permitAll()
                        .successHandler(myAuthenticationSuccessHandler())
//                        .loginProcessingUrl("/loginPerform")
//                        .defaultSuccessUrl("/myCourses",true)
//                        .failureUrl("/login?error=true")
                )
                .build();
    }

    @Bean
    public AuthenticationSuccessHandler myAuthenticationSuccessHandler(){
        return new MySimpleUrlAuthenticationSuccessHandler();
    }
}
