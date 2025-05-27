package CurrencyExchange.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class ExchangeConvertDto {
    private final long id;
    private final CurrencyDto baseCurrencyId;
    private final CurrencyDto targetCurrencyId;
    private final BigDecimal rate;
    private final BigDecimal amount;
    private final BigDecimal convertedAmount;
}
