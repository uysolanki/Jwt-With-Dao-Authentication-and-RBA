package com.greatlearning.batch5Jwt.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.greatlearning.batch5Jwt.filter.JwtRequestFilter;
import com.greatlearning.batch5Jwt.service.FetchPrincipalFromDb;

@Configuration
@EnableWebSecurity
public class MyWebConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	FetchPrincipalFromDb myUserDetailsService;
	
	@Autowired
	JwtRequestFilter jwtRequestFilter;
	
	@Override  //Authentication
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.authenticationProvider(myGlAuthPro());
		
	}
	
	@Bean
	public DaoAuthenticationProvider myGlAuthPro() {
		DaoAuthenticationProvider dap=new DaoAuthenticationProvider();
		dap.setUserDetailsService(myGlUserDetails());
		dap.setPasswordEncoder(myGlPassEnc());
		
		return dap;
	}

	@Bean
	public PasswordEncoder myGlPassEnc() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public UserDetailsService myGlUserDetails() //Decorated UserDetails
	{
		return new FetchPrincipalFromDb();
	}
	
	@Override
	protected void configure(HttpSecurity httpSecurity) throws Exception {
	    httpSecurity.csrf().disable()
	            .authorizeRequests()
	            .antMatchers("/authenticate").permitAll()
	            .antMatchers("/one").hasAnyAuthority("USER","ADMIN")
	            .antMatchers("/two").hasAuthority("ADMIN")
	            .anyRequest()
	            .authenticated()
	            .and()
	            .exceptionHandling().accessDeniedPage("/403")
	            .and()
	            .sessionManagement()
	            .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
	    
	    httpSecurity.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
	}

	
//	@Bean
//	public PasswordEncoder getPasswordEncoder()
//	{
//		return NoOpPasswordEncoder.getInstance();
//	}
	
	@Override
	@Bean
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}



}
