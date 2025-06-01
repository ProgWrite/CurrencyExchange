package CurrencyExchange.service;


import CurrencyExchange.CurrencyConverter;
import CurrencyExchange.dao.CurrencyDao;
import CurrencyExchange.dao.ExchangeRatesDao;
import CurrencyExchange.dto.ExchangeConvertDto;
import CurrencyExchange.entity.Currencies;
import CurrencyExchange.entity.ExchangeRates;
import CurrencyExchange.exceptions.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.List;

public class ExchangeService {
    private final static ExchangeService INSTANCE = new ExchangeService();
    private final ExchangeRatesDao exchangeRatesDao = ExchangeRatesDao.getInstance();
    private final CurrencyDao currencyDao = CurrencyDao.getInstance();
    private final CurrencyConverter currencyConverter = CurrencyConverter.getInstance();
    private final List<Currencies> currencies = currencyDao.findAll();
    private static final Logger logger = LoggerFactory.getLogger(ExchangeRatesService.class);

    public ExchangeService() {

    }

    public static ExchangeService getInstance() {
        return INSTANCE;
    }

    public ExchangeConvertDto makeExchange(String exchangeRateCode, BigDecimal amount) {
        String baseCurrencyCode = exchangeRateCode.substring(0, 3);
        String targetCurrencyCode = exchangeRateCode.substring(3, 6);
        String reverseExchangeRateCode = targetCurrencyCode + baseCurrencyCode;

        try {
            findExchangeRate(exchangeRateCode);
            return currencyConverter.getDirectConvertedCurrency(exchangeRateCode, amount);
        } catch (RuntimeException e) {
            logger.error("Failed to find direct exchange rate for code: {}", exchangeRateCode, e);
        }

        try {
            findExchangeRate(reverseExchangeRateCode);
            return currencyConverter.getReverseConvertedCurrency(reverseExchangeRateCode, amount);
        } catch (RuntimeException e) {
            logger.error("Failed to find reverse exchange rate for code: {}", reverseExchangeRateCode, e);
        }

        try {
            isCrossRateExists(exchangeRateCode, reverseExchangeRateCode);
            return convertToCrossRate(baseCurrencyCode, targetCurrencyCode, amount);
        } catch (RuntimeException e) {
            logger.error("Failed to check cross rate for currencies: {} and {}", baseCurrencyCode, targetCurrencyCode, e);
        }
        throw new IllegalArgumentException("Exchange rate code not found");
    }


    private ExchangeRates findExchangeRate(String exchangeRateCode) {
        return exchangeRatesDao.findByCode(exchangeRateCode)
                .orElseThrow(() -> new NotFoundException("No exchange rate found with code " + exchangeRateCode));
    }

    private ExchangeConvertDto convertToCrossRate(String baseCurrencyCode, String targetCurrencyCode, BigDecimal amount) {
        String code = codeForCrossRate(baseCurrencyCode, targetCurrencyCode);
        String baseCrossCode = code + baseCurrencyCode;
        String targetCrossCode = code + targetCurrencyCode;
        ExchangeRates crossBaseExchangeRate = findExchangeRate(baseCrossCode);
        ExchangeRates crossTargetExchangeRate = findExchangeRate(targetCrossCode);
        return currencyConverter.getCrossCurrency(crossBaseExchangeRate, crossTargetExchangeRate, amount);
    }

    private boolean isCrossRateExists(String baseCurrencyCode, String targetCurrencyCode) {
        String result = checkCrossRate(baseCurrencyCode, targetCurrencyCode, false);
        return "true".equals(result);
    }

    private String codeForCrossRate(String baseCurrencyCode, String targetCurrencyCode) {
        return checkCrossRate(baseCurrencyCode, targetCurrencyCode, true);
    }

    private String checkCrossRate(String baseCurrencyCode, String targetCurrencyCode, boolean returnCode) {
        for (Currencies currency : currencies) {
            String checkCode = currency.getCode();
            String checkBaseCode = checkCode + baseCurrencyCode;
            String checkTargetCode = checkCode + targetCurrencyCode;

            try {
                ExchangeRates baseExchangeRate = exchangeRatesDao.findByCode(checkBaseCode)
                        .orElseThrow(() -> new NotFoundException("No exchange rate found with code " + checkBaseCode));
                ExchangeRates targetExchangeRate = exchangeRatesDao.findByCode(checkTargetCode)
                        .orElseThrow(() -> new NotFoundException("No exchange rate found with code " + checkTargetCode));
                if (baseExchangeRate != null && targetExchangeRate != null) {
                    return returnCode ? checkCode : "true";
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return returnCode ? "Cross Rate is not found" : "false";
    }

}
