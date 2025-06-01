package CurrencyExchange.utils;

import CurrencyExchange.dto.CurrencyDto;
import CurrencyExchange.dto.ExchangeRateRequestDto;
import CurrencyExchange.dto.ExchangeRatesDto;
import CurrencyExchange.exceptions.InvalidParameterException;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Set;
import java.util.stream.Collectors;

public class ValidationUtils {
    private final static int REQUIRED_LENGTH_FOR_CODE = 3;
    private final static int REQUIRED_LENGTH_FOR_EXCHANGE_RATE = 6;
    private static Set<String> currencyCodes;

    public static void validate(CurrencyDto currencyDto) {
        String code = currencyDto.getCode();
        String name = currencyDto.getName();
        String sign = currencyDto.getSign();

        if(code == null || code.isBlank()){
            throw new InvalidParameterException("Missing parameter - code");
        }
        if(name == null || name.isBlank()){
            throw new InvalidParameterException("Missing parameter - name");
        }
        if(sign == null || sign.isBlank()){
            throw new InvalidParameterException("Missing parameter - sign");
        }

        validateCurrencyCode(code);
    }

    public static void validate(ExchangeRateRequestDto exchangeRateRequestDto) {
        String baseCurrencyCode = exchangeRateRequestDto.getBaseCurrencyCode();
        String targetCurrencyCode = exchangeRateRequestDto.getTargetCurrencyCode();
        BigDecimal rate = exchangeRateRequestDto.getRate();

        if (baseCurrencyCode == null || baseCurrencyCode.isBlank()) {
            throw new InvalidParameterException("Missing parameter - baseCurrencyCode");
        }

        if (targetCurrencyCode == null || targetCurrencyCode.isBlank()) {
            throw new InvalidParameterException("Missing parameter - targetCurrencyCode");
        }

        if (rate == null) {
            throw new InvalidParameterException("Missing parameter - rate");
        }

        if (rate.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidParameterException("Invalid parameter - rate must be non-negative");
        }
        validateCurrencyCode(baseCurrencyCode);
        validateCurrencyCode(targetCurrencyCode);
    }






    public static void validateCurrencyCode(String code) {
        if(code.length() != REQUIRED_LENGTH_FOR_CODE) {
            throw new InvalidParameterException("Invalid currency code");
        }

        if (currencyCodes == null) {
            Set<Currency> currencies = Currency.getAvailableCurrencies();
            currencyCodes = currencies.stream()
                    .map(Currency::getCurrencyCode)
                    .collect(Collectors.toSet());
        }

        if (!currencyCodes.contains(code)) {
            throw new InvalidParameterException("Currency code must be in ISO 4217 format");
        }

    }

    public static void checkLength(String exchangeRateCode){
        if(exchangeRateCode.length() != REQUIRED_LENGTH_FOR_EXCHANGE_RATE) {
            throw new InvalidParameterException("The exchange rate must contain 6 characters and be in the correct format.");
        }
    }

}
