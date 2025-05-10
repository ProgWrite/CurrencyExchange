package CurrencyExchange.service;

import CurrencyExchange.CurrencyConverter;
import CurrencyExchange.dao.CurrencyDao;
import CurrencyExchange.dao.ExchangeRatesDao;
import CurrencyExchange.dto.ExchangeConvertDto;
import CurrencyExchange.dto.ExchangeRatesDto;
import CurrencyExchange.entity.Currencies;
import CurrencyExchange.entity.ExchangeRates;
import CurrencyExchange.mapper.UserMapper;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

public class ExchangeRatesService {
    private final static ExchangeRatesService INSTANCE = new ExchangeRatesService();
    private final ExchangeRatesDao exchangeRatesDao = ExchangeRatesDao.getInstance();
    private final CurrencyService currencyService = CurrencyService.getInstance();
    private final CurrencyDao currencyDao = CurrencyDao.getInstance();
    private final CurrencyConverter currencyConverter = CurrencyConverter.getInstance();
    private final List <Currencies> currencies = currencyDao.findAll();

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
        ExchangeRates exchangeRate = exchangeRatesDao.findByCode(code);
        if(exchangeRate == null) {
            return null;
        }
        return convertToDto(exchangeRate);
    }

    //TODO интересно это костыль или нет, мб это надо в отдельный метод (long в int). Также дублирование

    public ExchangeRatesDto addNewExchangeRate(String baseCurrencyCode, String targetCurrencyCode, BigDecimal rate) {
        ExchangeRates exchangeRate = new ExchangeRates();
        Currencies baseCurrency = currencyDao.findByCode(baseCurrencyCode);
        long baseCurrencyId = baseCurrency.getId();
        int baseCurrencyIdInt = (int) baseCurrencyId;

        Currencies targetCurrency = currencyDao.findByCode(targetCurrencyCode);
        long targetCurrencyId = targetCurrency.getId();
        int targetCurrencyIdInt = (int) targetCurrencyId;

        exchangeRate.setBaseCurrencyId(baseCurrencyIdInt);
        exchangeRate.setTargetCurrencyId(targetCurrencyIdInt);
        exchangeRate.setRate(rate);

        ExchangeRates addedExchangeRate = exchangeRatesDao.save(exchangeRate);
        return convertToDto(addedExchangeRate);
    }

    public ExchangeRatesDto updateExchangeRate(String pathInfo, BigDecimal rate) {
        ExchangeRates updatedExchangeRate = exchangeRatesDao.findByCode(pathInfo);
        updatedExchangeRate.setRate(rate);
        exchangeRatesDao.update(updatedExchangeRate);
        return convertToDto(updatedExchangeRate);
    }


    // TODO надо что-то менять в методе
    public ExchangeConvertDto makeExchange(String exchangeRateCode, BigDecimal amount) {
        String baseCurrencyCode = exchangeRateCode.substring(0, 3);
        String targetCurrencyCode = exchangeRateCode.substring(3, 6);
        String checkReverseExchangeRateCode = targetCurrencyCode + baseCurrencyCode;

        ExchangeRates directExchangeRate = null;
        ExchangeRates reverseExchangeRate = null;
        boolean isCrossRate = false;

        try{
            directExchangeRate = exchangeRatesDao.findByCode(exchangeRateCode);
        }catch (Exception e){
            e.printStackTrace();
        }

        try{
            reverseExchangeRate = exchangeRatesDao.findByCode(checkReverseExchangeRateCode);
        }catch (Exception e){
            e.printStackTrace();
        }

        try{
            isCrossRate = isCrossRateExists(baseCurrencyCode, targetCurrencyCode);
        }catch (Exception e){
            e.printStackTrace();
        }

        if(directExchangeRate != null ) {
            return currencyConverter.getDirectConvertedCurrency(exchangeRateCode, amount);
        }else if(reverseExchangeRate != null ) {
            return currencyConverter.getReverseConvertedCurrency(checkReverseExchangeRateCode, amount);
        }else if(isCrossRate){
            String code = codeForCrossRate(baseCurrencyCode, targetCurrencyCode);
            String baseCrossCode = code + baseCurrencyCode;
            String targetCrossCode = code + targetCurrencyCode;
            ExchangeRates crossBaseExchangeRate = exchangeRatesDao.findByCode(baseCrossCode);
            ExchangeRates crossTargetExchangeRate = exchangeRatesDao.findByCode(targetCrossCode);
            return  currencyConverter.getCrossCurrency(crossBaseExchangeRate, crossTargetExchangeRate, amount);
        }
        throw new IllegalArgumentException("Exchange rate code not found");
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
                ExchangeRates baseExchangeRate = exchangeRatesDao.findByCode(checkBaseCode);
                ExchangeRates targetExchangeRate = exchangeRatesDao.findByCode(checkTargetCode);
                if (baseExchangeRate != null && targetExchangeRate != null) {
                    return returnCode ? checkCode : "true";
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return returnCode ? "Cross Rate is not found" : "false";
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
