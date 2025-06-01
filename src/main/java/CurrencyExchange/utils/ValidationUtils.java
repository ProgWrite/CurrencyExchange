package CurrencyExchange.utils;

import CurrencyExchange.dto.CurrencyDto;
import CurrencyExchange.exceptions.EntityExistsException;
import CurrencyExchange.exceptions.InvalidParameterException;
import CurrencyExchange.service.CurrencyService;

import java.util.Currency;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ValidationUtils {
    private final static int REQUIRED_LENGTH = 3;
    private static Set<String> currencyCodes;
    private final static CurrencyService currencyService = CurrencyService.getInstance();


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


    public static void validateCurrencyCode(String code) {
        if(code.length() != REQUIRED_LENGTH) {
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

}
