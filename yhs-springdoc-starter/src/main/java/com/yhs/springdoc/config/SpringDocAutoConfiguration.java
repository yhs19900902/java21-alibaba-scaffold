package com.yhs.springdoc.config;

import com.yhs.springdoc.properties.SpringDocProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.parameters.HeaderParameter;
import io.swagger.v3.oas.models.security.OAuthFlow;
import io.swagger.v3.oas.models.security.OAuthFlows;
import io.swagger.v3.oas.models.security.Scopes;
import io.swagger.v3.oas.models.security.SecurityScheme;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.customizers.GlobalOpenApiCustomizer;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * @author 07664-linwei
 * @version Id: SpringDocAutoConfiguration.java, v 0.1 2022/4/30 8:41 lw Exp $
 */
@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(SpringDocProperties.class)
public class SpringDocAutoConfiguration {
    @Schema
    private static final String BASE_PATH = "/**";
    private static final String VERSION = "yhs_version";
    private final SpringDocProperties springDocProperties;

    @Bean
    public OpenAPI groupedOpenApi() {
        Components components = new Components();
        components.addParameters(VERSION, new HeaderParameter().required(false)
                .name(VERSION).schema(new StringSchema()).required(false));
        List<SpringDocProperties.Headers> headers = springDocProperties.getHeaders();
        headers.forEach(h -> components.addParameters(h.getHeaderName(), new HeaderParameter().description(h.getDescription())
                .required(false).allowEmptyValue(h.isAllowEmptyValue())
                .example(h.getExample()).name(h.getHeaderName()).schema(new StringSchema()).required(h.isRequired())));
        components.addSecuritySchemes("oauth2", new SecurityScheme().type(SecurityScheme.Type.OAUTH2)
                .flows(oauth()));

        return new OpenAPI()
                .info(apiInfo())
                .components(components);
    }


    private OAuthFlows oauth() {
        OAuthFlows oAuthFlows = new OAuthFlows();

        SpringDocProperties.Authorization authorization = springDocProperties.getAuthorization();
        OAuthFlow oAuthFlow = new OAuthFlow()
                .tokenUrl(springDocProperties.getAuthorization().getTokenUrl())
                .authorizationUrl(springDocProperties.getAuthorization().getAuthorizationUrl())
                .refreshUrl(springDocProperties.getAuthorization().getRefreshUrl());
        Scopes scopes = new Scopes();
        springDocProperties.getAuthorization().getScopes().forEach(s -> scopes.addString(s, s));
        oAuthFlow.scopes(scopes);
        if (authorization.getGrantType() == SpringDocProperties.Authorization.GrantType.AUTHORIZATIONCODE) {
            oAuthFlows.setAuthorizationCode(oAuthFlow);
        } else if (authorization.getGrantType() == SpringDocProperties.Authorization.GrantType.IMPLICIT) {
            oAuthFlows.setImplicit(oAuthFlow);
        } else if (authorization.getGrantType() == SpringDocProperties.Authorization.GrantType.PASSWORD) {
            oAuthFlows.password(oAuthFlow);
        }
        return oAuthFlows;
    }


    private Info apiInfo() {
        return new Info()
                .title(springDocProperties.getTitle())
                .description(springDocProperties.getDescription())
                .license(
                        new License().name(springDocProperties.getLicense())
                                .url(springDocProperties.getLicenseUrl())
                ).termsOfService(springDocProperties.getTermsOfServiceUrl())
                .contact(new Contact()
                        .name(springDocProperties.getContact().getName())
                        .url(springDocProperties.getContact().getUrl())
                        .email(springDocProperties.getContact().getEmail()))
                .version(springDocProperties.getVersion());
    }

    //@Bean
    public GroupedOpenApi publicApi() {
        // base-path处理

        return GroupedOpenApi.builder().group("doc")
                .pathsToMatch(BASE_PATH)
                .pathsToExclude("/error", "/actuator/**").build();
    }

    @Bean
    public GlobalOpenApiCustomizer globalOpenApiCustomizer() {
        List<SpringDocProperties.Headers> headers = springDocProperties.getHeaders();
        return openApi -> openApi.getPaths().values().stream().flatMap(pathItem -> pathItem.readOperations().stream())
                .forEach(operation -> {
                    operation.addParametersItem(new HeaderParameter().$ref("#/components/parameters/" + VERSION));
                    headers.forEach(h -> operation.addParametersItem(new HeaderParameter().$ref("#/components/parameters/" + h.getHeaderName())));
                });
    }
}
