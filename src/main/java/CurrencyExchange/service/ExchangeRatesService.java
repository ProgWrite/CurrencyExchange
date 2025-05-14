package CurrencyExchange.service;

import CurrencyExchange.CurrencyConverter;
import CurrencyExchange.dao.CurrencyDao;
import CurrencyExchange.dao.ExchangeRatesDao;
import CurrencyExchange.dto.ExchangeConvertDto;
import CurrencyExchange.dto.ExchangeRatesDto;
import CurrencyExchange.entity.Currencies;
import CurrencyExchange.entity.ExchangeRates;
import CurrencyExchange.exceptions.NotFoundException;
import CurrencyExchange.exceptions.ServiceException;
import CurrencyExchange.mapper.UserMapper;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.stream.Collectors;


public class ExchangeRatesService {
    private final static ExchangeRatesService INSTANCE = new ExchangeRatesService();
    private final ExchangeRatesDao exchangeRatesDao = ExchangeRatesDao.getInstance();
    private final CurrencyService currencyService = CurrencyService.getInstance();
    private final CurrencyDao currencyDao = CurrencyDao.getInstance();
    private final CurrencyConverter currencyConverter = CurrencyConverter.getInstance();
    private final List <Currencies> currencies = currencyDao.findAll();
    private static final Logger logger = LoggerFactory.getLogger(ExchangeRatesService.class);

    private ExchangeRatesService() {

    }

    public static ExchangeRatesService getInstance() {
        return INSTANCE;
    }

    public List<ExchangeRatesDto> findAll() {
        return exchangeRatesDao.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public ExchangeRatesDto getExchangeRateByCode(String code) {
        Optional<ExchangeRates > exchangeRate = exchangeRatesDao.findByCode(code);

        return exchangeRate.map(this::convertToDto)
                        .orElseThrow(()-> new NotFoundException("No exchange rate found with code " + code));
    }

    public ExchangeRatesDto create(String baseCurrencyCode, String targetCurrencyCode, BigDecimal rate) {
        ExchangeRates exchangeRate = new ExchangeRates();

        int baseCurrencyId =  convertCurrencyIdToInt(baseCurrencyCode);
        int targetCurrencyId = convertCurrencyIdToInt(targetCurrencyCode);

        exchangeRate.setBaseCurrencyId(baseCurrencyId);
        exchangeRate.setTargetCurrencyId(targetCurrencyId);
        exchangeRate.setRate(rate);

        try{
            ExchangeRates addedExchangeRate = exchangeRatesDao.create(exchangeRate);
            return convertToDto(addedExchangeRate);
        }catch (RuntimeException e){
            throw new ServiceException("Exchange rate creation failed.");
        }
    }

    public ExchangeRatesDto update(String pathInfo, BigDecimal rate) {
        ExchangeRates updatedExchangeRate = exchangeRatesDao.findByCode(pathInfo)
                .orElseThrow(() -> new NotFoundException("No exchange rate found with path " + pathInfo));
        updatedExchangeRate.setRate(rate);
        exchangeRatesDao.update(updatedExchangeRate);
        return convertToDto(updatedExchangeRate);
    }

    public ExchangeConvertDto makeExchange(String exchangeRateCode, BigDecimal amount) {
        String baseCurrencyCode = exchangeRateCode.substring(0, 3);
        String targetCurrencyCode = exchangeRateCode.substring(3, 6);
        String reverseExchangeRateCode = targetCurrencyCode + baseCurrencyCode;

        try{
            ExchangeRates directExchangeRate = findExchangeRate(exchangeRateCode);
            return currencyConverter.getDirectConvertedCurrency(exchangeRateCode, amount);
        }catch (RuntimeException e){
            logger.error("Failed to find direct exchange rate for code: {}", exchangeRateCode, e);
        }

        try{
            ExchangeRates reverseExchangeRate = findExchangeRate(reverseExchangeRateCode);
            return currencyConverter.getReverseConvertedCurrency(reverseExchangeRateCode, amount);
        }catch (RuntimeException e){
            logger.error("Failed to find reverse exchange rate for code: {}", reverseExchangeRateCode, e);
        }

        try{
            Boolean isCrossRate = isCrossRateExists(exchangeRateCode, reverseExchangeRateCode);
            return convertToCrossRate(baseCurrencyCode, targetCurrencyCode, amount);
        }catch (RuntimeException e){
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

    private boolean isCrossRateExists(String baseCurrencyCode, String targetCurrencyCode){
        String result = checkCrossRate(baseCurrencyCode, targetCurrencyCode, false);
        return "true".equals(result);
    }

    private String codeForCrossRate(String baseCurrencyCode, String targetCurrencyCode){
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

    private int convertCurrencyIdToInt(String currencyCode) {
        Currencies currency = currencyDao.findByCode(currencyCode)
                .orElseThrow(()-> new NotFoundException("No currency found with code " + currencyCode));
        long CurrencyId = currency.getId();
        int currencyIdInt = (int) CurrencyId;
        return currencyIdInt;
    }

    private ExchangeRatesDto convertToDto(ExchangeRates exchangeRates) {
        return UserMapper.INSTANCE.exchangeRateToExchangeRateDtoWithId(
                exchangeRates.getId(),
                currencyService.getCurrencyById(exchangeRates.getBaseCurrencyId()),
                currencyService.getCurrencyById(exchangeRates.getTargetCurrencyId()),
                exchangeRates.getRate()
        );
    }
}
