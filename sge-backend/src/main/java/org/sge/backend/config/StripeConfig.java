package org.sge.backend.config;

import com.stripe.Stripe;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class StripeConfig {

    @PostConstruct
    public void init() {
        var secretKey = System.getenv("STRIPE_SECRET_KEY");
        if (secretKey == null || secretKey.isBlank()) {
            log.warn("STRIPE_SECRET_KEY no configurada. Stripe no estará disponible.");
            return;
        }
        Stripe.apiKey = secretKey;
        log.info("Stripe inicializado correctamente");
    }
}
