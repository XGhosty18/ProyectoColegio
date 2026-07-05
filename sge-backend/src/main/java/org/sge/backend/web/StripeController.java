package org.sge.backend.web;

import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.net.Webhook;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sge.backend.dto.request.StripePaymentIntentRequest;
import org.sge.backend.dto.response.StripePaymentIntentResponse;
import org.sge.backend.service.StripeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/stripe")
@RequiredArgsConstructor
public class StripeController {
    private final StripeService service;

    @PostMapping("/create-payment-intent")
    public StripePaymentIntentResponse createPaymentIntent(@Valid @RequestBody StripePaymentIntentRequest req) {
        return service.crearPaymentIntent(req.cronogramaPagoId(), req.alumnoId(), req.usuarioId());
    }

    @PostMapping("/webhook")
    public ResponseEntity<String> webhook(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String sigHeader) {

        var webhookSecret = System.getenv("STRIPE_WEBHOOK_SECRET");
        Event event = null;
        if (webhookSecret != null && !webhookSecret.isBlank()) {
            try {
                event = Webhook.constructEvent(payload, sigHeader, webhookSecret);
            } catch (SignatureVerificationException e) {
                log.warn("Firma de webhook inválida");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Firma inválida");
            }
        } else {
            var type = extractJsonValue(payload, "type");
            var dataId = extractJsonValue(payload, "data.object.id");
            if (type == null || dataId == null) {
                log.warn("No se pudo extraer type/dataId del payload webhook");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Payload inválido");
            }
            event = new Event();
            event.setType(type);
            event.setId(dataId);
        }

        return procesarEvento(event);
    }

    private String extractJsonValue(String json, String path) {
        try {
            var parts = path.split("\\.");
            var current = json;
            for (var part : parts) {
                var key = "\"" + part + "\":";
                var start = current.indexOf(key);
                if (start < 0) return null;
                start += key.length();
                while (start < current.length() && current.charAt(start) == ' ') start++;
                if (current.charAt(start) == '"') {
                    start++;
                    var end = current.indexOf('"', start);
                    if (end < 0) return null;
                    current = current.substring(start, end);
                } else {
                    var end = start;
                    while (end < current.length() && current.charAt(end) != ',' && current.charAt(end) != '}' && current.charAt(end) != ']') end++;
                    current = current.substring(start, end).trim();
                }
            }
            return current;
        } catch (Exception e) {
            log.warn("Error extrayendo '{}' del JSON: {}", path, e.getMessage());
            return null;
        }
    }

    private ResponseEntity<String> procesarEvento(Event event) {
        if ("payment_intent.succeeded".equals(event.getType())) {
            try {
                var intent = PaymentIntent.retrieve(event.getId());
                service.procesarWebhook(intent);
                log.info("Webhook payment_intent.succeeded procesado: {}", intent.getId());
            } catch (Exception e) {
                log.error("Error al procesar webhook", e);
            }
        }
        return ResponseEntity.ok("OK");
    }
}
