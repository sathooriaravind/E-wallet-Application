package org.example.configs;

import org.example.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    UserService userService;

    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.
                csrf()
                .ignoringAntMatchers("/user/signup")
                .and()
                .httpBasic()
                .and()
                .authorizeRequests()
                .antMatchers("/user/phone/**").hasAuthority(Constants.OTHER_SERVICE_ACCESS)
                .antMatchers(HttpMethod.GET,"/user/**").hasAuthority(Constants.USER_SELF_ACCESS)
                .antMatchers("/**").permitAll()
                .and()
                .formLogin();
    }

}
