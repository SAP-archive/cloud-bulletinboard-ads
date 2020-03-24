package com.sap.bulletinboard.ads.config;


import com.sap.cloud.security.xsuaa.XsuaaServiceConfiguration;
import com.sap.cloud.security.xsuaa.XsuaaServiceConfigurationDefault;
import com.sap.cloud.security.xsuaa.XsuaaServicePropertySourceFactory;
import com.sap.cloud.security.xsuaa.token.TokenAuthenticationConverter;
import com.sap.cloud.security.xsuaa.token.authentication.XsuaaJwtDecoderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;

import static org.springframework.http.HttpMethod.*;

@Configuration
@EnableWebSecurity
@PropertySource(factory = XsuaaServicePropertySourceFactory.class, value = { "" })
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    public static final String DISPLAY_SCOPE_LOCAL = "Display";
    public static final String UPDATE_SCOPE_LOCAL = "Update";

    @Autowired
    XsuaaServiceConfiguration xsuaaServiceConfiguration;


    // configure Spring Security, demand authentication and specific scopes
    @Override
    public void configure(HttpSecurity http) throws Exception {

        // @formatter:off
        http
            .sessionManagement()
                // session is created by approuter
                .sessionCreationPolicy(SessionCreationPolicy.NEVER)
                .and()
                    // demand specific scopes depending on intended request
                    .authorizeRequests()
                    .antMatchers(GET, "/health", "/").permitAll() //used as health check on CF: must be accessible by "anybody"
                    .antMatchers(POST, "/api/v1/ads/**").hasAuthority(UPDATE_SCOPE_LOCAL)
                    .antMatchers(PUT, "/api/v1/ads/**").hasAuthority(UPDATE_SCOPE_LOCAL)
                    .antMatchers(DELETE, "/api/v1/ads/**").hasAuthority(UPDATE_SCOPE_LOCAL)
                    .antMatchers(GET, "/api/v1/ads/**").hasAuthority(DISPLAY_SCOPE_LOCAL)
                    .anyRequest().denyAll() // deny anything not configured above
                .and()
                    .oauth2ResourceServer()
                    .jwt()
                    .jwtAuthenticationConverter(getJwtAuthenticationConverter());
        // @formatter:on
    }

    @Bean
    JwtDecoder jwtDecoder() {
        return new XsuaaJwtDecoderBuilder(xsuaaServiceConfiguration).build();
    }

    @Bean
    @ConditionalOnMissingBean(XsuaaServiceConfiguration.class)
    public XsuaaServiceConfiguration xsuaaServiceConfiguration() {
        return new XsuaaServiceConfigurationDefault();
    }

    /**
     * Customizes how GrantedAuthority are derived from a Jwt
     */
    Converter<Jwt, AbstractAuthenticationToken> getJwtAuthenticationConverter() {
        TokenAuthenticationConverter converter = new TokenAuthenticationConverter(xsuaaServiceConfiguration);
        converter.setLocalScopeAsAuthorities(true);
        return converter;
    }

}