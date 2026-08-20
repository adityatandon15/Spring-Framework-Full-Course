package in.strikes.oauthDemo.config;

import in.strikes.oauthDemo.service.CustomOidcUserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity httpSecurity,
            CustomOidcUserService customOidcUserService) {

        httpSecurity.authorizeHttpRequests(
                auth ->
                        auth.requestMatchers("/")
                                .permitAll()
                                .anyRequest()
                                .authenticated()
                )
                .oauth2Login(oatuh ->
                    oatuh.userInfoEndpoint(userInfo ->
                            userInfo.oidcUserService(customOidcUserService)
                    )
                    .defaultSuccessUrl("/profile", true
                )
        );

        return httpSecurity.build();
    }
}
