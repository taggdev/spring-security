package com.abciloveu.config;

import com.abciloveu.config.properties.JwtTokenProperties;
import com.abciloveu.security.authentication.LimitLoginAuthenticationProvider;
import com.abciloveu.security.filter.JwtAuthenticationFilter;
import com.abciloveu.security.jwt.Http401UnauthorizedEntryPoint;
import com.abciloveu.security.jwt.JwtUtils;
import com.abciloveu.security.service.LoginAttemptsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.boot.actuate.info.InfoEndpoint;
import org.springframework.boot.actuate.metrics.MetricsEndpoint;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsPasswordService;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;


/**
 * @see org.springframework.security.access.expression.SecurityExpressionOperations
 * @see org.springframework.security.access.PermissionEvaluator
 *
 */
@Order(1)
@Configuration
@EnableWebSecurity
@EnableConfigurationProperties(JwtTokenProperties.class)
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {

	static final Logger LOG = LoggerFactory.getLogger(WebSecurityConfiguration.class);

	private final UserDetailsService userDetailsService;

	private final LoginAttemptsService loginAttemptsService;

	private final JwtUtils jwtUtils;

	public WebSecurityConfiguration(UserDetailsService userDetailsService, LoginAttemptsService loginAttemptsService, JwtUtils jwtUtils) {
		this.userDetailsService = userDetailsService;
		this.loginAttemptsService = loginAttemptsService;
		this.jwtUtils = jwtUtils;
	}

	@Bean(BeanIds.AUTHENTICATION_MANAGER)
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}

	@Override
	public void configure(AuthenticationManagerBuilder authenManagerBuilder) throws Exception {

		LOG.info("Config globalUserDetails...");
		authenManagerBuilder.authenticationProvider(authenticationProvider());
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception { //@formatter:off

		http
		  .cors().and()
		
		   // we don't need CSRF because our token is invulnerable
		  .csrf().disable()
		  
		.exceptionHandling().authenticationEntryPoint(jwtAuthenticationEntryPoint()).and()
		
		 // no need to create session as JWT auth is stateless and per request
		.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
		
		.authorizeRequests()
        	.requestMatchers(EndpointRequest.to(InfoEndpoint.class, HealthEndpoint.class, MetricsEndpoint.class)).permitAll()
//			.requestMatchers(EndpointRequest.toAnyEndpoint()).authenticated()
			.antMatchers("/auth/**", "/bus/**", "/websocket/**", "/test/**").permitAll()       // allow anyone to try and authenticate
			.antMatchers(HttpMethod.OPTIONS, "/**").permitAll()                                // allow CORS pre-flighting
			.antMatchers("/api/ui/**").permitAll()                                             // lock down everything else
			.antMatchers("/api/**").authenticated()                                            // lock down everything else
			.anyRequest().permitAll();
		
		 // Add our custom JWT security filter before Spring Security's Username/Password filter
		http.addFilterBefore(authenticationTokenFilterBean(), UsernamePasswordAuthenticationFilter.class);

        // Disable page caching in the browser
        http.headers().cacheControl().disable();
        
		 //@formatter:on
	}

	@Override
	public void configure(WebSecurity web) throws Exception { 
		//@formatter:off
		web.ignoring()
			.antMatchers(
					"/swagger-ui.html", 
					"/v2/api-docs", 
					"/auth/**",
					"/error",
					"/swagger-resources/**", 
					"/webjars/**"
				);
		
		//@formatter:on
	}

	@Bean
	public JwtAuthenticationFilter authenticationTokenFilterBean() throws Exception {
		return new JwtAuthenticationFilter(jwtUtils);
	}

	@Bean
	public Http401UnauthorizedEntryPoint jwtAuthenticationEntryPoint() {
		return new Http401UnauthorizedEntryPoint();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		PasswordEncoder defaultPasswordEncoder = NoOpPasswordEncoder.getInstance();

		LOG.debug("Creating DelegatingPasswordEncoder: with defaultPasswordEncoder: {}", defaultPasswordEncoder);
		final DelegatingPasswordEncoder encoder = (DelegatingPasswordEncoder) PasswordEncoderFactories
				.createDelegatingPasswordEncoder();

		encoder.upgradeEncoding("noop");
		encoder.setDefaultPasswordEncoderForMatches(defaultPasswordEncoder);

		return encoder;
	}
	
	@Bean
	public AuthenticationProvider authenticationProvider() {
		final LimitLoginAuthenticationProvider provider = new LimitLoginAuthenticationProvider(loginAttemptsService);
		provider.setUserDetailsService(this.userDetailsService);
		provider.setPasswordEncoder(passwordEncoder());
		
		if(userDetailsService instanceof UserDetailsPasswordService) {
			provider.setUserDetailsPasswordService((UserDetailsPasswordService) this.userDetailsService);
		}
		
		return provider;
	}

	/**
	 * Allowing all origins, headers and methods here is only intended to keep this example simple.
	 * This is not a default recommended configuration. Make adjustments as
	 * necessary to your use case.
	 */
	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		final CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowedOrigins(Arrays.asList("*"));
		configuration.setAllowedMethods(Arrays.asList("HEAD", "GET", "POST", "PATCH", "PUT", "DELETE"));
		
		// setAllowCredentials(true) is important, otherwise:
		// The value of the 'Access-Control-Allow-Origin' header in the response must not be the wildcard '*' when the request's credentials mode is 'include'.
		configuration.setAllowCredentials(true);
		
		// setAllowedHeaders is important! Without it, OPTIONS preflight request
		// will fail with 403 Invalid CORS request
		configuration.setAllowedHeaders(Arrays.asList("Authorization", "Cache-Control", "Content-Type"));

		final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);

		return source;
	}
}