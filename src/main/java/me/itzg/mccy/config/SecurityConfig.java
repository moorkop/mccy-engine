package me.itzg.mccy.config;

import me.itzg.mccy.controllers.CsrfHeaderFilter;
import me.itzg.mccy.types.MccyConstants;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
import org.springframework.security.web.util.matcher.RequestMatcher;

/**
 * @author Geoff Bourne
 * @since 12/23/2015
 */
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http
                .authorizeRequests()
                    .antMatchers("/api/downloads/**").permitAll()
                    .antMatchers("/api/settings").permitAll()
                    .antMatchers("/api/containers").permitAll()
                .antMatchers("/**").hasRole("USER")
                .and().formLogin()
                    .loginPage("/login")
                    .defaultSuccessUrl("/")
                    .permitAll()
                .and().logout().logoutSuccessUrl("/")
                .and().addFilterAfter(new CsrfHeaderFilter(), CsrfFilter.class)
                .csrf().csrfTokenRepository(csrfTokenRepository());
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring()
                .antMatchers("/css/**")
                .antMatchers("/js/**")
                .antMatchers("/fonts/**")
                .antMatchers("/webjars/**")
                .antMatchers("/img/**")
                .antMatchers("/ng-bits/**")
                .antMatchers("/views/**")
                .antMatchers("/**/favicon.ico")
                .antMatchers('/apidocs/**');
    }

    @Bean
    public CsrfTokenRepository csrfTokenRepository() {
        HttpSessionCsrfTokenRepository repository = new HttpSessionCsrfTokenRepository();
        repository.setHeaderName(MccyConstants.X_XSRF_TOKEN);
        return repository;
    }
}
