package com.relatosdepapel.ms_books_payments.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * Configuración de RestTemplate para comunicación entre microservicios,
 * proporciona métodos para hacer peticiones HTTP: GET, POST, PUT, DELETE, etc.
 * El @LoadBalanced permite usar nombres de Eureka en lugar de IP:puerto.
 */
@Configuration // Indica que esta clase tiene configuración de Spring
public class RestTemplateConfig {
    /**
     * Crea un RestTemplate con balanceo de carga habilitado.
     * 
     * @LoadBalanced hace que Spring use Eureka para resolver nombres de servicio.
     *               Ejemplo: "http://MS-BOOKS-CATALOGUE" se traduce a
     *               "http://192.168.1.5:8081"
     * 
     * @return RestTemplate configurado para microservicios
     */
    @Bean // Crea un objeto que Spring puede inyectar en otros componentes
    @LoadBalanced // CLAVE: permite usar nombres Eureka (MS-BOOKS-CATALOGUE)
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
        return restTemplate;
    }
}