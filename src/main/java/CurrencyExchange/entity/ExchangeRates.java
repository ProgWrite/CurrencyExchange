package CurrencyExchange.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class ExchangeRates {
    private Long id;
    private Currencies baseCurrency;
    private Currencies targetCurrency;
    private BigDecimal rate;

    public ExchangeRates(Currencies baseCurrency, Currencies targetCurrency, BigDecimal rate) {
        this.baseCurrency = baseCurrency;
        this.targetCurrency = targetCurrency;
        this.rate = rate;
    }
}




