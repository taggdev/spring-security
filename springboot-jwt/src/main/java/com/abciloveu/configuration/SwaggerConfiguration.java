package com.abciloveu.configuration;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.common.collect.Lists;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@EnableSwagger2
@Configuration
public class SwaggerConfiguration {

	public static final String AUTHORIZATION_HEADER = "Authorization";
	
    public static final String DEFAULT_INCLUDE_PATTERN = "/api/.*";
	
    private static final Logger LOG = LoggerFactory.getLogger(SwaggerConfiguration.class);

    
	@Bean
	public Docket swaggerPersonApi10() { //@formatter:off
		return new Docket(DocumentationType.SWAGGER_2)
				.securityContexts(Lists.newArrayList(securityContext()))
		        .securitySchemes(Lists.newArrayList(apiKey()))
		        .useDefaultResponseMessages(false)
				
		         .select()
					.apis(RequestHandlerSelectors.basePackage("com.abciloveu.web.controller"))
					.paths(PathSelectors.any())
				.build()
				.apiInfo(new ApiInfoBuilder()
						.version("1.0")
						.title("Microengine Admin API")
						.build());
		//@formatter:on
	}
	

    private ApiKey apiKey() {
        return new ApiKey("JWT", AUTHORIZATION_HEADER, "header");
    }

    private SecurityContext securityContext() {
        return SecurityContext.builder()
            .securityReferences(defaultAuth())
            .forPaths(PathSelectors.regex(DEFAULT_INCLUDE_PATTERN))
            .build();
    }

    List<SecurityReference> defaultAuth() {
        final AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
        final AuthorizationScope[] authorizationScopes = { authorizationScope };
        
        return Lists.newArrayList(new SecurityReference("JWT", authorizationScopes));
    }
}
