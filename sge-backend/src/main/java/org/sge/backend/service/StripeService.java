package org.sge.backend.service;

import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sge.backend.dto.response.StripePaymentIntentResponse;
import org.sge.backend.model.entity.Pago;
import org.sge.backend.repository.AlumnoRepository;
import org.sge.backend.repository.CronogramaPagoRepository;
import org.sge.backend.repository.PagoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

@Slf4j
@Service
@RequiredArgsConstructor
public class StripeService {
    private final PagoRepository pagoRepo;
    private final AlumnoRepository alumnoRepo;
    private final CronogramaPagoRepository cronogramaRepo;

    public StripePaymentIntentResponse crearPaymentIntent(Long cronogramaPagoId, Long alumnoId, Long usuarioId) {
        var cronograma = cronogramaRepo.findById(cronogramaPagoId)
            .orElseThrow(() -> new RuntimeException("Cronograma de pago no encontrado"));
        var alumno = alumnoRepo.findById(alumnoId)
            .orElseThrow(() -> new RuntimeException("Alumno no encontrado"));

        if (!"PENDIENTE".equals(cronograma.getEstado())) {
            throw new RuntimeException("El cronograma de pago ya fue pagado o cancelado");
        }

        var montoCentavos = cronograma.getMonto().multiply(BigDecimal.valueOf(100)).longValue();

        var params = PaymentIntentCreateParams.builder()
            .setAmount(montoCentavos)
            .setCurrency("pen")
            .setDescription("Pago - " + alumno.getNombres() + " " + alumno.getApellidos()
                + " - " + cronograma.getConceptoPago().getNombre())
            .putMetadata("cronograma_pago_id", cronogramaPagoId.toString())
            .putMetadata("alumno_id", alumnoId.toString())
            .putMetadata("usuario_id", usuarioId.toString())
            .build();

        try {
            var intent = PaymentIntent.create(params);
            return new StripePaymentIntentResponse(
                intent.getClientSecret(), intent.getId(), montoCentavos, "pen");
        } catch (StripeException e) {
            log.error("Error al crear PaymentIntent en Stripe", e);
            throw new RuntimeException("Error al procesar el pago con Stripe: " + e.getMessage());
        }
    }

    @Transactional
    public void procesarWebhook(PaymentIntent intent) {
        var metadata = intent.getMetadata();
        var cronogramaId = Long.valueOf(metadata.get("cronograma_pago_id"));
        var alumnoId = Long.valueOf(metadata.get("alumno_id"));
        var usuarioId = metadata.containsKey("usuario_id") ? Long.valueOf(metadata.get("usuario_id")) : null;

        var cronograma = cronogramaRepo.findById(cronogramaId)
            .orElseThrow(() -> new RuntimeException("Cronograma no encontrado: " + cronogramaId));
        var alumno = alumnoRepo.findById(alumnoId)
            .orElseThrow(() -> new RuntimeException("Alumno no encontrado: " + alumnoId));

        if ("succeeded".equals(intent.getStatus())) {
            var monto = BigDecimal.valueOf(intent.getAmount()).divide(BigDecimal.valueOf(100));
            var pago = Pago.builder()
                .alumno(alumno).cronogramaPago(cronograma)
                .monto(monto)
                .metodo("STRIPE")
                .referencia(intent.getId())
                .stripePaymentIntentId(intent.getId())
                .fechaPago(LocalDate.now())
                .usuarioId(usuarioId)
                .build();
            pagoRepo.save(pago);

            cronograma.setEstado("PAGADO");
            cronogramaRepo.save(cronograma);

            log.info("Pago procesado exitosamente via Stripe: {}", intent.getId());
        }
    }
}
