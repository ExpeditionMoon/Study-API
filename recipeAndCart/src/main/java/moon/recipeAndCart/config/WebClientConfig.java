package moon.recipeAndCart.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Value("${recipe.base-url}")
    private String baseUrl;
    @Value("${recipe.req-type}")
    private String reqType;
    @Value("${api.key}")
    private String apiKey;

    @Bean
    public WebClient recipeWebClient() {
        return WebClient.builder()
                .baseUrl(baseUrl + apiKey + reqType)
                .build();
    }
}
