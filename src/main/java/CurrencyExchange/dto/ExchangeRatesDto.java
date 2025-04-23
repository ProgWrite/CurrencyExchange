package CurrencyExchange.dto;

import CurrencyExchange.entity.Currencies;

import java.math.BigDecimal;
import java.util.Objects;

public class ExchangeRatesDto {
    private final long id;
    private final CurrencyDto baseCurrencyId;
    private final CurrencyDto targetCurrencyId;
    private final BigDecimal rate;

    public ExchangeRatesDto(long id, CurrencyDto baseCurrencyId, CurrencyDto targetCurrencyId, BigDecimal rate) {
        this.id = id;
        this.baseCurrencyId = baseCurrencyId;
        this.targetCurrencyId = targetCurrencyId;
        this.rate = rate;
    }

    public ExchangeRatesDto(CurrencyDto baseCurrencyId, CurrencyDto targetCurrencyId, BigDecimal rate){
        this(0L, baseCurrencyId, targetCurrencyId, rate);
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExchangeRatesDto that = (ExchangeRatesDto) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "ExchangeRatesDto{" +
                "id=" + id +
                ", baseCurrencyId=" + baseCurrencyId +
                ", targetCurrencyId=" + targetCurrencyId +
                ", rate=" + rate +
                '}';
    }

}
