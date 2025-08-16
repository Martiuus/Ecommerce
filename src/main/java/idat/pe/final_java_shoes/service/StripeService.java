package idat.pe.final_java_shoes.service;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Service
public class StripeService {

    @Value("${stripe.api.secretKey}")
    private String secretKey;

    @PostConstruct
    public void init() {
        Stripe.apiKey = secretKey;
    }

    public Charge charge(String token, BigDecimal amount, String currency) throws StripeException {
        Map<String, Object> chargeParams = new HashMap<>();
        // El monto en Stripe se maneja en centavos, por eso multiplicamos por 100
        chargeParams.put("amount", amount.multiply(new BigDecimal("100")).intValue());
        chargeParams.put("currency", currency);
        chargeParams.put("description", "Pago de ZapShoes");
        chargeParams.put("source", token);
        return Charge.create(chargeParams);
    }
}