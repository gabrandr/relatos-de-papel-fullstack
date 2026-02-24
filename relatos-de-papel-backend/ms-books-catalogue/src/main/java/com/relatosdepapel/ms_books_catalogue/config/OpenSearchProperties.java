package com.relatosdepapel.ms_books_catalogue.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ConfigurationProperties(prefix = "opensearch")
public class OpenSearchProperties {
    private String url;
    private String username;
    private String password;
    private String index = "relatos";
}
