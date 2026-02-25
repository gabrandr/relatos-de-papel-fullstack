package com.relatosdepapel.ms_books_catalogue.config;

import java.net.URI;

import org.apache.hc.client5.http.auth.AuthScope;
import org.apache.hc.client5.http.auth.UsernamePasswordCredentials;
import org.apache.hc.client5.http.impl.auth.BasicCredentialsProvider;
import org.apache.hc.core5.http.HttpHost;
import org.opensearch.client.RestClient;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración del cliente REST de OpenSearch para el microservicio de catálogo.
 * Soporta credenciales explícitas por propiedades y fallback a user-info en la URL.
 */
@Configuration
@EnableConfigurationProperties(OpenSearchProperties.class)
public class OpenSearchConfig {

    /**
     * Crea el cliente REST de OpenSearch con autenticación básica cuando está disponible.
     *
     * @param properties propiedades externas de OpenSearch.
     * @return cliente REST listo para operaciones del repositorio.
     * @throws IllegalStateException cuando no se define `opensearch.url`.
     */
    @Bean(destroyMethod = "close")
    public RestClient openSearchRestClient(OpenSearchProperties properties) {
        if (properties.getUrl() == null || properties.getUrl().isBlank()) {
            throw new IllegalStateException("OPENSEARCH_URL es obligatorio para ms-books-catalogue");
        }

        URI uri = URI.create(properties.getUrl());
        int port = uri.getPort() == -1 ? ("https".equalsIgnoreCase(uri.getScheme()) ? 443 : 80) : uri.getPort();
        String username = properties.getUsername();
        String password = properties.getPassword();

        if ((username == null || username.isBlank()) && uri.getUserInfo() != null) {
            String[] split = uri.getUserInfo().split(":", 2);
            username = split[0];
            if (password == null || password.isBlank()) {
                password = split.length > 1 ? split[1] : "";
            }
        }

        BasicCredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        if (username != null && !username.isBlank()) {
            credentialsProvider.setCredentials(
                    new AuthScope(uri.getHost(), port),
                    new UsernamePasswordCredentials(username, password != null ? password.toCharArray() : new char[0]));
        }

        HttpHost host = new HttpHost(uri.getScheme(), uri.getHost(), port);

        return RestClient.builder(host)
                .setHttpClientConfigCallback(httpClientBuilder -> httpClientBuilder
                        .setDefaultCredentialsProvider(credentialsProvider))
                .build();
    }
}
