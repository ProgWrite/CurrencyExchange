package CurrencyExchange.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class ExchangeRatesDto {
    private final long id;
    private final CurrencyDto baseCurrency;
    private final CurrencyDto targetCurrency;
    private final BigDecimal rate;
}
