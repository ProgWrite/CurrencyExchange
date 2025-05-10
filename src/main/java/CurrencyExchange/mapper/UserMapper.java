package CurrencyExchange.mapper;

import CurrencyExchange.dto.CurrencyDto;
import CurrencyExchange.dto.ExchangeConvertDto;
import CurrencyExchange.dto.ExchangeRatesDto;
import CurrencyExchange.entity.Currencies;
import CurrencyExchange.entity.ExchangeRates;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;

@Mapper
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    default CurrencyDto currencyToCurrencyDtoWithId(long id, String code, String name, String sign) {
        return new CurrencyDto(id, code, name, sign);
    }

    default Currencies currencyDtoToCurrenciesWithoutId(String code, String name, String sign) {
        return new Currencies(0L, code, name, sign);
    }

    default ExchangeRatesDto exchangeRateToExchangeRateDtoWithId(long id, CurrencyDto baseCurrency, CurrencyDto targetCurrency, BigDecimal rate) {
        return new ExchangeRatesDto(id, baseCurrency, targetCurrency, rate);
    }

    default ExchangeConvertDto exchangeRateToExchangeConvertDtoWithId(
            long id,
            CurrencyDto baseCurrency,
            CurrencyDto targetCurrency,
            BigDecimal rate,
            BigDecimal amount,
            BigDecimal convertedAnount) {
        return new ExchangeConvertDto(id, baseCurrency, targetCurrency, rate, amount, convertedAnount);
    }

    default ExchangeConvertDto exchangeRateToExchangeConvertDtoWithoutId(
            CurrencyDto baseCurrency,
            CurrencyDto targetCurrency,
            BigDecimal rate,
            BigDecimal amount,
            BigDecimal convertedAnount) {
        return new ExchangeConvertDto(0L, baseCurrency, targetCurrency, rate, amount, convertedAnount);
    }


}
