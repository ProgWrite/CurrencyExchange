package CurrencyExchange.utils;

import CurrencyExchange.dto.CurrencyDto;
import CurrencyExchange.dto.ExchangeRatesDto;
import CurrencyExchange.entity.Currencies;
import CurrencyExchange.entity.ExchangeRates;
import org.modelmapper.ModelMapper;

import java.util.Currency;

public class MappingUtils {

    private static final ModelMapper MODEL_MAPPER;

    static {
        MODEL_MAPPER = new ModelMapper();
        MODEL_MAPPER.typeMap(CurrencyDto.class, Currency.class);
    }

    public static Currencies convertToEntity(CurrencyDto currencyDto){
        return MODEL_MAPPER.map(currencyDto, Currencies.class);
    }

    public static CurrencyDto convertToDto(Currencies currencies){
        return MODEL_MAPPER.map(currencies, CurrencyDto.class);
    }

    public static ExchangeRatesDto convertToDto(ExchangeRates exchangeRates){
        return MODEL_MAPPER.map(exchangeRates, ExchangeRatesDto.class);
    }

}
