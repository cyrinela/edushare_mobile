package Gateway.project.com;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.server.WebFilter;
import org.springframework.web.cors.reactive.CorsWebFilter;


@Configuration
public class CorsConfig {

    @Bean
    public WebFilter corsFilter() {
        CorsConfiguration corsConfig = new CorsConfiguration();

        // Autoriser toutes les origines (frontends)
        corsConfig.addAllowedOriginPattern("*");

        // Autoriser toutes les méthodes HTTP
        corsConfig.addAllowedMethod("*");

        // Autoriser tous les en-têtes
        corsConfig.addAllowedHeader("*");

        // Ne pas autoriser les cookies avec CORS si vous avez toutes les origines
        // corsConfig.setAllowCredentials(true); // Ne pas activer cela si vous utilisez "*"

        // Configuration des règles CORS
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig);

        // Retourner un WebFilter pour appliquer la configuration CORS
        return new CorsWebFilter(source);
    }
}