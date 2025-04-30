package studio.clashbuddy.clashaccess.auth.authorization;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import studio.clashbuddy.clashaccess.auth.SecretStorage;
import studio.clashbuddy.clashaccess.auth.authentication.JwtUtility;
import studio.clashbuddy.clashaccess.exceptions.ClashAccessDeniedException;
import studio.clashbuddy.clashaccess.gateway.ClashAccessGatewayEndpointServicesManager;

import java.util.Objects;


@Component
public class ClashAccessAuthorizationFilter  extends AbstractGatewayFilterFactory<ClashAccessAuthorizationFilter.Config> implements Ordered {

    private final JwtUtility jwtUtility;
    private final ClashAccessGatewayEndpointServicesManager endpointServicesManager;

    public ClashAccessAuthorizationFilter(ClashAccessGatewayEndpointServicesManager clashAccessGatewayEndpointServicesManager, SecretStorage secretStorage) {
        super(Config.class);
        this.endpointServicesManager = clashAccessGatewayEndpointServicesManager;
        this.jwtUtility = new JwtUtility(secretStorage.getSecret());
    }

    private boolean isAllowedPath(ServerHttpRequest request,String serviceId) {
        var endingPoint = request.getURI().getPath();
        if(endpointServicesManager.isPublicEndpoint(endingPoint, request.getMethod().name(),serviceId))
            return true;
        var upgrade  = request.getHeaders().get("upgrade");
        if(upgrade == null) return false;
        return upgrade.contains("websocket");
    }

    private String extractToken(HttpHeaders headers){
        if(!headers.containsKey(HttpHeaders.AUTHORIZATION))
            throw new ClashAccessDeniedException("Missing authorization header",401);
        String token = Objects.requireNonNull(headers.get(HttpHeaders.AUTHORIZATION)).get(0);
        if(token == null || !token.startsWith("Bearer "))
            throw new ClashAccessDeniedException("Bearer token is not provided",401);
        return  token;
    }



    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            var serviceId = serviceId(exchange.getRequest());
            if(isAllowedPath(exchange.getRequest(),serviceId))
                return chain.filter(exchange);
            var token = extractToken(exchange.getRequest().getHeaders());
            var jwtSession = jwtUtility.validateToken(token);
            var payload = jwtSession.getFirst();
            var type = jwtSession.getSecond();
            if(!type.equals(JwtUtility.TokenType.ACCESS))
                throw new ClashAccessDeniedException("Token is not access token please login again",403);
            exchange.getRequest()
                    .mutate()
                    .header("x-ca-uid",payload.getUserId())
                    .header("x-ca-urs",payload.getRoles())
                    .header("x-ca-ups", payload.getPermissions());
            return chain.filter(exchange);
        };
    }

    private String serviceId(ServerHttpRequest request) {
        if (request.getHeaders().containsKey("x-service-id")) {
            return request.getHeaders().getFirst("x-service-id");
        }

        String path = request.getURI().getPath();
        String[] segments = path.split("/");

        if (segments.length > 1) {
            return segments[1];
        }

        throw new ClashAccessDeniedException("Missing service id header",401);
    }


    @Override
    public int getOrder() {
        return 1;
    }

    public static class Config{}




}
