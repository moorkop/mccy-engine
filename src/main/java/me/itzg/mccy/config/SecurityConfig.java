package me.itzg.mccy.config;

import me.itzg.mccy.controllers.CsrfHeaderFilter;
import me.itzg.mccy.types.MccyConstants;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;

/**
 * @author Geoff Bourne
 * @since 12/23/2015
 */
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
//        2015-12-23 12:07:  INFO 10796 --- [ost-startStop-1] o.s.s.web.DefaultSecurityFilterChain     : Creating filter chain: Ant [pattern='/css/**'], []
//        2015-12-23 12:07:48.681  INFO 10796 --- [ost-startStop-1] o.s.s.web.DefaultSecurityFilterChain     : Creating filter chain: Ant [pattern='/js/**'], []
//        2015-12-23 12:07:48.681  INFO 10796 --- [ost-startStop-1] o.s.s.web.DefaultSecurityFilterChain     : Creating filter chain: Ant [pattern='/images/**'], []
//        2015-12-23 12:07:48.681  INFO 10796 --- [ost-startStop-1] o.s.s.web.DefaultSecurityFilterChain     : Creating filter chain: Ant [pattern='/**/favicon.ico'], []
//        2015-12-23 12:07:48.6848.6811  INFO 10796 --- [ost-startStop-1] o.s.s.web.DefaultSecurityFilterChain     : Creating filter chain: Ant [pattern='/error'], []

        http
                .authorizeRequests()
                    .antMatchers("/css/**").permitAll()
                    .antMatchers("/js/**").permitAll()
                    .antMatchers("/fonts/**").permitAll()
                    .antMatchers("/webjars/**").permitAll()
                    .antMatchers("/images/**").permitAll()
                    .antMatchers("/ng-bits/**").permitAll()
                    .antMatchers("/views/**").permitAll()
                    .antMatchers("/**/favicon.ico").permitAll()
                    .antMatchers("/api/downloads/**").permitAll()
                .antMatchers("/**").hasRole("USER")
                .and().formLogin()
                    .loginPage("/login")
                    .permitAll()
                .and().addFilterAfter(new CsrfHeaderFilter(), CsrfFilter.class)
                .csrf().csrfTokenRepository(csrfTokenRepository());
    }

    @Bean
    public CsrfTokenRepository csrfTokenRepository() {
        HttpSessionCsrfTokenRepository repository = new HttpSessionCsrfTokenRepository();
        repository.setHeaderName(MccyConstants.X_XSRF_TOKEN);
        return repository;
    }
}
