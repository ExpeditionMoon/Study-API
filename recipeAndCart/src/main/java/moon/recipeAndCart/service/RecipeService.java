package moon.recipeAndCart.service;

import lombok.RequiredArgsConstructor;
import moon.recipeAndCart.config.WebClientConfig;
import moon.recipeAndCart.repository.RecipeRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class RecipeService {

    private final WebClientConfig webClientConfig;

    public Mono<String> fetchMenu(String menu) {
        return this.webClientConfig.recipeWebClient().get()
                .uri(uriBuilder -> uriBuilder
                        .path("/RCP_NM=" + menu)
                        .build())
                .retrieve()
                .bodyToMono(String.class);
    }
}
