package com.unir.gateway.filter;

import com.unir.gateway.decorator.RequestDecoratorFactory;
import com.unir.gateway.model.GatewayRequest;
import com.unir.gateway.utils.RequestBodyExtractor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Filtro global que aplica el contrato de gateway `POST + targetMethod`.
 * Traduce la petición entrante en una petición mutada hacia el microservicio destino.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class RequestTranslationFilter implements GlobalFilter {

    private final RequestBodyExtractor requestBodyExtractor;
    private final RequestDecoratorFactory requestDecoratorFactory;

    /**
     * Valida y traduce la petición HTTP antes de enrutarla.
     * Permite `OPTIONS` para preflight CORS, bloquea métodos distintos de `POST`
     * y transforma el body JSON en una request mutada con método y path finales.
     *
     * @param exchange intercambio HTTP reactivo actual.
     * @param chain cadena de filtros del gateway.
     * @return señal reactiva de finalización de la petición.
     */
    @Override
    public Mono<Void> filter(
            ServerWebExchange exchange,
            GatewayFilterChain chain) {
        HttpMethod method = exchange.getRequest().getMethod();

        if (HttpMethod.OPTIONS.equals(method)) {
            return chain.filter(exchange);
        }

        if (!HttpMethod.POST.equals(method)) {
            exchange.getResponse().setStatusCode(HttpStatus.METHOD_NOT_ALLOWED);
            log.info("Non-POST request blocked by gateway translation filter: {}", method);
            return exchange.getResponse().setComplete();
        }

        if (exchange.getRequest().getHeaders().getContentType() == null) {
            exchange.getResponse().setStatusCode(HttpStatus.BAD_REQUEST);
            log.info("POST request without content type");
            return exchange.getResponse().setComplete();
        }

        return DataBufferUtils.join(exchange.getRequest().getBody())
                .flatMap(dataBuffer -> {
                    GatewayRequest request = requestBodyExtractor.getRequest(exchange, dataBuffer);
                    ServerHttpRequest mutatedRequest = requestDecoratorFactory.getDecorator(request);
                    // RouteToRequestUrlFilter calcula la URL antes de este filtro global.
                    // Sobrescribir este atributo garantiza que se use la URI mutada.
                    exchange.getAttributes().put(ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR,
                            mutatedRequest.getURI());
                    if (request.getQueryParams() != null) {
                        request.getQueryParams().clear();
                    }
                    log.info("Proxying request: {} {}", mutatedRequest.getMethod(), mutatedRequest.getURI());
                    return chain.filter(exchange.mutate().request(mutatedRequest).build());
                });
    }
}
