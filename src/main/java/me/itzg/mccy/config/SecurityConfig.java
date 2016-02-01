package me.itzg.mccy.config;

import me.itzg.mccy.controllers.CsrfHeaderFilter;
import me.itzg.mccy.types.MccyConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
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

    @Autowired
    private Environment env;

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http
                .authorizeRequests()
                    .antMatchers(HttpMethod.GET, "/api/downloads/**").permitAll()
                    .antMatchers(HttpMethod.GET, "/api/settings").permitAll()
                    .antMatchers(HttpMethod.GET, "/api/containers").permitAll()
                    .antMatchers(HttpMethod.GET, "/api/containers/**").permitAll()
                .antMatchers("/**").hasRole("USER")
                .and().formLogin()
                    .loginPage("/login")
                    .defaultSuccessUrl("/")
                    .permitAll()
                .and().logout().logoutSuccessUrl("/")
                .and().addFilterAfter(new CsrfHeaderFilter(), CsrfFilter.class)
                .csrf().csrfTokenRepository(csrfTokenRepository());

        if (env.acceptsProfiles(MccyConstants.PROFILE_BASIC_AUTH)) {
            http
                    .httpBasic()
                    .and().csrf().ignoringAntMatchers("/**");
        }
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
                .antMatchers("/apidocs/**");
    }

    @Bean
    public CsrfTokenRepository csrfTokenRepository() {
        HttpSessionCsrfTokenRepository repository = new HttpSessionCsrfTokenRepository();
        repository.setHeaderName(MccyConstants.X_XSRF_TOKEN);
        return repository;
    }
}
