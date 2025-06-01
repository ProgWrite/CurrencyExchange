package CurrencyExchange.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExchangeConvertDto {
    private long id;
    private CurrencyDto baseCurrencyId;
    private CurrencyDto targetCurrencyId;
    private BigDecimal rate;
    private BigDecimal amount;
    private BigDecimal convertedAmount;

    public ExchangeConvertDto(CurrencyDto baseCurrencyId, CurrencyDto targetCurrencyId, BigDecimal rate, BigDecimal amount, BigDecimal convertedAmount) {
        this.baseCurrencyId = baseCurrencyId;
        this.targetCurrencyId = targetCurrencyId;
        this.rate = rate;
        this.amount = amount;
        this.convertedAmount = convertedAmount;
    }
}
