package edu.teco.serverless.auth;

import edu.teco.serverless.auth.jwtSpringExtention.JwtAuthEntryPoint;
import edu.teco.serverless.auth.jwtSpringExtention.JwtAuthProvider;
import edu.teco.serverless.auth.jwtSpringExtention.JwtAuthSuccessHandler;
import edu.teco.serverless.auth.jwtSpringExtention.JwtAuthTokenFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.Arrays;

/**
 * Configuration for Authentication
 */

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class AuthenticationConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private JwtAuthEntryPoint unauthorizedHandler;
    @Autowired
    private JwtAuthProvider authenticationProvider;

    /**
     * Returns JwtAuthEntryPoint as ProviderManager
     * @return JwtAuthEntryPoint as ProviderManager
     * @throws Exception when subclass fails
     */
    @Bean
    @Override
    public AuthenticationManager authenticationManager() throws Exception {
        return new ProviderManager(Arrays.asList(authenticationProvider));
    }

    /**
     * Sets JwtAuthProvider as AuthenticationManager and JwtAuthSuccessHandler as AuthenticationSuccessHandler
     * @return JwtAuthTokenFilter with settings
     * @throws Exception if subclass fails
     */
    @Bean
    public JwtAuthTokenFilter authenticationTokenFilterBean() throws Exception {
        JwtAuthTokenFilter authenticationTokenFilter = new JwtAuthTokenFilter();
        authenticationTokenFilter.setAuthenticationManager(authenticationManager());
        authenticationTokenFilter.setAuthenticationSuccessHandler(new JwtAuthSuccessHandler());
        return authenticationTokenFilter;
    }

    /**
     * Main configuration file.
     * @param httpSecurity HttpSecurity object
     * @throws Exception if subclass fails
     */
    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .csrf().disable()
                .authorizeRequests()
                    .antMatchers(HttpMethod.POST,"/lambdas").permitAll()
                    .antMatchers(HttpMethod.POST, "/lambdas/paymentACK").permitAll()
                    .antMatchers(HttpMethod.GET, "/lambdas/payment/*").permitAll()
                    .antMatchers(HttpMethod.POST, "/lambdas/pay").permitAll()
                    .antMatchers("/lambdas/*/execute").hasAnyRole("SUB", "MASTER")
                    .antMatchers("/lambdas/*/*").hasRole("MASTER")
                    .antMatchers("/lambdas/*").hasRole("MASTER")
                .anyRequest().authenticated()
                .and()
                // errorHandler if authentication/authorisation fails
                .exceptionHandling().authenticationEntryPoint(unauthorizedHandler)
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS); //.and()
        // Custom JWT based security filter
        httpSecurity
                .addFilterBefore(authenticationTokenFilterBean(), UsernamePasswordAuthenticationFilter.class);
        httpSecurity.headers().cacheControl();
    }
}
