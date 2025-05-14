package CurrencyExchange;

import CurrencyExchange.dao.ExchangeRatesDao;
import CurrencyExchange.dto.ExchangeConvertDto;

import CurrencyExchange.entity.ExchangeRates;
import CurrencyExchange.exceptions.NotFoundException;
import CurrencyExchange.mapper.UserMapper;
import CurrencyExchange.service.CurrencyService;


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


    public ExchangeConvertDto getDirectConvertedCurrency(String exchangeRateCode, BigDecimal amount){
        ExchangeRates exchangeRate = exchangeRatesDao.findByCode(exchangeRateCode)
                .orElseThrow(()-> new NotFoundException("No exchange rate found with code " + exchangeRateCode));
        BigDecimal rate = exchangeRate.getRate();
        BigDecimal convertedAmount = calculateDirectCurrency(exchangeRate, amount);
        return convertToExchangeDtoWithId(exchangeRate, amount, convertedAmount, rate);
    }

    public ExchangeConvertDto getReverseConvertedCurrency(String exchangeRateCode, BigDecimal amount) {
        ExchangeRates exchangeRate = exchangeRatesDao.findByCode(exchangeRateCode)
                .orElseThrow(()-> new NotFoundException("No exchange rate found with code " + exchangeRateCode));
        BigDecimal rate = exchangeRate.getRate();
        BigDecimal reverseRate = BigDecimal.ONE.divide(rate, 6, BigDecimal.ROUND_HALF_UP);
        BigDecimal convertedAmount = calculateReverseCurrency(exchangeRate, amount);
        return convertToReverseExchangeDtoWithId(exchangeRate, amount, convertedAmount, reverseRate);
    }

    public ExchangeConvertDto getCrossCurrency(ExchangeRates checkBaseCode, ExchangeRates checkTargetCode,BigDecimal amount) {
        BigDecimal convertedAmount = calculateCrossRateCurrency(checkBaseCode, checkTargetCode, amount);
        return convertToExchangeDtoWithoutId(checkBaseCode, checkTargetCode,amount, convertedAmount);
    }


    private BigDecimal calculateDirectCurrency(ExchangeRates exchangeRates, BigDecimal amount) {
        BigDecimal rate = exchangeRates.getRate();
        BigDecimal convertedAmount = amount.multiply(rate);
        convertedAmount.setScale(6, BigDecimal.ROUND_HALF_UP);
        return convertedAmount;
    }

    private BigDecimal calculateReverseCurrency(ExchangeRates exchangeRates, BigDecimal amount) {
        BigDecimal rate = exchangeRates.getRate();
        BigDecimal reverseRate = BigDecimal.ONE.divide(rate, 6, BigDecimal.ROUND_HALF_UP);
        BigDecimal convertedAmount = amount.multiply(reverseRate);

        return convertedAmount;
    }

    private BigDecimal calculateCrossRateCurrency(ExchangeRates checkBaseCode, ExchangeRates checkTargetCode, BigDecimal amount) {
        BigDecimal baseRate = checkBaseCode.getRate();
        BigDecimal targetRate = checkTargetCode.getRate();
        BigDecimal crossRate = targetRate.divide(baseRate, 6, BigDecimal.ROUND_HALF_UP);
        BigDecimal convertedAmount = amount.multiply(crossRate);
        return convertedAmount;
    }

    private ExchangeConvertDto convertToExchangeDtoWithId(ExchangeRates exchangeRate, BigDecimal amount, BigDecimal convertedAmount, BigDecimal rate) {
        return UserMapper.INSTANCE.exchangeRateToExchangeConvertDtoWithId(
                exchangeRate.getId(),
                currencyService.getCurrencyById(exchangeRate.getBaseCurrencyId()),
                currencyService.getCurrencyById(exchangeRate.getTargetCurrencyId()),
                rate,
                amount,
                convertedAmount
        );
    }

    private ExchangeConvertDto convertToReverseExchangeDtoWithId(ExchangeRates exchangeRate, BigDecimal amount, BigDecimal convertedAmount, BigDecimal rate) {
        return UserMapper.INSTANCE.exchangeRateToExchangeConvertDtoWithId(
                exchangeRate.getId(),
                currencyService.getCurrencyById(exchangeRate.getTargetCurrencyId()),
                currencyService.getCurrencyById(exchangeRate.getBaseCurrencyId()),
                rate,
                amount,
                convertedAmount
        );
    }


    private ExchangeConvertDto convertToExchangeDtoWithoutId(ExchangeRates checkBaseCode, ExchangeRates checkTargetCode,  BigDecimal amount, BigDecimal convertedAmount) {
        BigDecimal baseRate = checkBaseCode.getRate();
        BigDecimal targetRate = checkTargetCode.getRate();
        BigDecimal crossRate = targetRate.divide(baseRate, 6, BigDecimal.ROUND_HALF_UP);

        return UserMapper.INSTANCE.exchangeRateToExchangeConvertDtoWithoutId(
                currencyService.getCurrencyById(checkBaseCode.getTargetCurrencyId()),
                currencyService.getCurrencyById(checkTargetCode.getTargetCurrencyId()),
                crossRate,
                amount,
                convertedAmount
        );
    }

}
