package CurrencyExchange;

import CurrencyExchange.dao.ExchangeRatesDao;
import CurrencyExchange.dto.CurrencyDto;
import CurrencyExchange.dto.ExchangeConvertDto;
import CurrencyExchange.entity.Currencies;
import CurrencyExchange.entity.ExchangeRates;
import CurrencyExchange.exceptions.NotFoundException;
import CurrencyExchange.service.CurrencyService;
import CurrencyExchange.utils.MappingUtils;


import java.math.BigDecimal;


public class CurrencyConverter {
    private final static CurrencyConverter INSTANCE = new CurrencyConverter();
    private final CurrencyService currencyService = CurrencyService.getInstance();
    private final ExchangeRatesDao exchangeRatesDao = ExchangeRatesDao.getInstance();


    public CurrencyConverter() {

    }

    public static CurrencyConverter getInstance() {
        return INSTANCE;
    }


    public ExchangeConvertDto getDirectConvertedCurrency(String exchangeRateCode, BigDecimal amount) {
        ExchangeRates exchangeRate = exchangeRatesDao.findByCode(exchangeRateCode)
                .orElseThrow(() -> new NotFoundException("No exchange rate found with code " + exchangeRateCode));
        BigDecimal rate = exchangeRate.getRate();
        BigDecimal convertedAmount = calculateDirectCurrency(exchangeRate, amount);
        return convertToDirectExchangeDtoWithId(exchangeRate, amount, convertedAmount, rate);
    }

    public ExchangeConvertDto getReverseConvertedCurrency(String exchangeRateCode, BigDecimal amount) {
        ExchangeRates exchangeRate = exchangeRatesDao.findByCode(exchangeRateCode)
                .orElseThrow(() -> new NotFoundException("No exchange rate found with code " + exchangeRateCode));
        BigDecimal rate = exchangeRate.getRate();
        BigDecimal reverseRate = BigDecimal.ONE.divide(rate, 6, BigDecimal.ROUND_HALF_UP);
        BigDecimal convertedAmount = calculateReverseCurrency(exchangeRate, amount);
        return convertToReverseExchangeDtoWithId(exchangeRate, amount, convertedAmount, reverseRate);
    }

    public ExchangeConvertDto getCrossCurrency(ExchangeRates checkBaseCode, ExchangeRates checkTargetCode, BigDecimal amount) {
        BigDecimal convertedAmount = calculateCrossRateCurrency(checkBaseCode, checkTargetCode, amount);
        return convertToCrossExchangeDtoWithoutId(checkBaseCode, checkTargetCode, amount, convertedAmount);
    }


    private BigDecimal calculateDirectCurrency(ExchangeRates exchangeRates, BigDecimal amount) {
        BigDecimal rate = exchangeRates.getRate();
        BigDecimal convertedAmount = amount.multiply(rate);
        convertedAmount.setScale(2, BigDecimal.ROUND_HALF_UP);
        return convertedAmount;
    }

    private BigDecimal calculateReverseCurrency(ExchangeRates exchangeRates, BigDecimal amount) {
        BigDecimal rate = exchangeRates.getRate();
        BigDecimal reverseRate = BigDecimal.ONE.divide(rate, 2, BigDecimal.ROUND_HALF_UP);
        BigDecimal convertedAmount = (amount.multiply(reverseRate)).setScale(2, BigDecimal.ROUND_HALF_UP);
        return convertedAmount;
    }

    private BigDecimal calculateCrossRateCurrency(ExchangeRates checkBaseCode, ExchangeRates checkTargetCode, BigDecimal amount) {
        BigDecimal baseRate = checkBaseCode.getRate();
        BigDecimal targetRate = checkTargetCode.getRate();
        BigDecimal crossRate = targetRate.divide(baseRate, 2, BigDecimal.ROUND_HALF_UP);
        BigDecimal convertedAmount = (amount.multiply(crossRate)).setScale(2, BigDecimal.ROUND_HALF_UP);
        return convertedAmount;
    }

    private ExchangeConvertDto convertToDirectExchangeDtoWithId(ExchangeRates exchangeRate, BigDecimal amount, BigDecimal convertedAmount, BigDecimal rate) {
        Currencies baseCurrency =  exchangeRate.getBaseCurrency();
        CurrencyDto baseCurrencyDto = MappingUtils.convertToDto(baseCurrency);
        Currencies targetCurrency = exchangeRate.getTargetCurrency();
        CurrencyDto targetCurrencyDto = MappingUtils.convertToDto(targetCurrency);

        ExchangeConvertDto exchangeConvertDto = new ExchangeConvertDto();
        exchangeConvertDto.setId(exchangeRate.getId());
        exchangeConvertDto.setBaseCurrencyId(baseCurrencyDto);
        exchangeConvertDto.setTargetCurrencyId(targetCurrencyDto);
        exchangeConvertDto.setRate(rate);
        exchangeConvertDto.setAmount(amount);
        exchangeConvertDto.setConvertedAmount(convertedAmount);
        return exchangeConvertDto;
    }

    private ExchangeConvertDto convertToReverseExchangeDtoWithId(ExchangeRates exchangeRate, BigDecimal amount, BigDecimal convertedAmount, BigDecimal rate) {
        Currencies baseCurrency =  exchangeRate.getBaseCurrency();
        CurrencyDto baseCurrencyDto = MappingUtils.convertToDto(baseCurrency);
        Currencies targetCurrency = exchangeRate.getTargetCurrency();
        CurrencyDto targetCurrencyDto = MappingUtils.convertToDto(targetCurrency);

        ExchangeConvertDto exchangeConvertDto = new ExchangeConvertDto();
        exchangeConvertDto.setId(exchangeRate.getId());
        exchangeConvertDto.setBaseCurrencyId(targetCurrencyDto);
        exchangeConvertDto.setTargetCurrencyId(baseCurrencyDto);
        exchangeConvertDto.setRate(rate);
        exchangeConvertDto.setAmount(amount);
        exchangeConvertDto.setConvertedAmount(convertedAmount);
        return exchangeConvertDto;
    }


    private ExchangeConvertDto convertToCrossExchangeDtoWithoutId(ExchangeRates baseExchangeRate, ExchangeRates targetExchangeRate, BigDecimal amount, BigDecimal convertedAmount) {
        Currencies baseCurrency =  baseExchangeRate.getTargetCurrency();
        CurrencyDto baseCurrencyDto = MappingUtils.convertToDto(baseCurrency);
        Currencies targetCurrency = targetExchangeRate.getTargetCurrency();
        CurrencyDto targetCurrencyDto = MappingUtils.convertToDto(targetCurrency);

        BigDecimal baseRate = baseExchangeRate.getRate();
        BigDecimal targetRate = targetExchangeRate.getRate();
        BigDecimal crossRate = targetRate.divide(baseRate, 2, BigDecimal.ROUND_HALF_UP);

        ExchangeConvertDto exchangeConvertDto = new ExchangeConvertDto();
        exchangeConvertDto.setBaseCurrencyId(baseCurrencyDto);
        exchangeConvertDto.setTargetCurrencyId(targetCurrencyDto);
        exchangeConvertDto.setRate(crossRate);
        exchangeConvertDto.setAmount(amount);
        exchangeConvertDto.setConvertedAmount(convertedAmount);
        return exchangeConvertDto;
    }
}
