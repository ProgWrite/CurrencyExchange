package CurrencyExchange.dto;

import java.math.BigDecimal;
import java.util.Objects;

public class ExchangeConvertDto {

    private final long id;
    private final CurrencyDto baseCurrencyId;
    private final CurrencyDto targetCurrencyId;
    private final BigDecimal rate;
    private final BigDecimal amount;
    private final BigDecimal convertedAmount;

    public ExchangeConvertDto(long id, CurrencyDto baseCurrencyId, CurrencyDto targetCurrencyId, BigDecimal rate, BigDecimal amount, BigDecimal convertedAmount) {
        this.id = id;
        this.baseCurrencyId = baseCurrencyId;
        this.targetCurrencyId = targetCurrencyId;
        this.rate = rate;
        this.amount = amount;
        this.convertedAmount = convertedAmount;
    }

    public ExchangeConvertDto(CurrencyDto baseCurrencyId, CurrencyDto targetCurrencyId, BigDecimal rate, BigDecimal amount, BigDecimal convertedAmount) {
        this(0L, baseCurrencyId, targetCurrencyId, rate, amount, convertedAmount);
    }


    public long getId() {
        return id;
    }

    public CurrencyDto getBaseCurrencyId() {
        return baseCurrencyId;
    }

    public CurrencyDto getTargetCurrencyId() {
        return targetCurrencyId;
    }

    public BigDecimal getRate() {
        return rate;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public BigDecimal getConvertedAmount() {
        return convertedAmount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExchangeConvertDto that = (ExchangeConvertDto) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "ExchangeConvertDto{" +
                "id=" + id +
                ", baseCurrencyId=" + baseCurrencyId +
                ", targetCurrencyId=" + targetCurrencyId +
                ", rate=" + rate +
                ", amount=" + amount +
                ", convertedAmount=" + convertedAmount +
                '}';
    }
}
