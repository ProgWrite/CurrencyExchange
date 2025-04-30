package CurrencyExchange.service;

import CurrencyExchange.dao.CurrencyDao;
import CurrencyExchange.dao.ExchangeRatesDao;
import CurrencyExchange.dto.ExchangeConvertDto;
import CurrencyExchange.dto.ExchangeRatesDto;
import CurrencyExchange.entity.Currencies;
import CurrencyExchange.entity.ExchangeRates;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

public class ExchangeRatesService {
    private final static ExchangeRatesService INSTANCE = new ExchangeRatesService();
    private final ExchangeRatesDao exchangeRatesDao = ExchangeRatesDao.getInstance();
    private final CurrencyService currencyService = CurrencyService.getInstance();
    private final CurrencyDao currencyDao = CurrencyDao.getInstance();
    private final List <Currencies> currencies = currencyDao.findAll();

    private ExchangeRatesService() {

    }

    public static ExchangeRatesService getInstance() {
        return INSTANCE;
    }


    //TODO есть дублирование кода в методе!

    public List<ExchangeRatesDto> findAll() {
        return exchangeRatesDao.findAll().stream()
                .map(exchangeRates -> new ExchangeRatesDto(
                        exchangeRates.getId(),
                        currencyService.getCurrencyById(exchangeRates.getBaseCurrencyId()),
                        currencyService.getCurrencyById(exchangeRates.getTargetCurrencyId()),
                        exchangeRates.getRate()
                ))
                .collect(Collectors.toList());
    }

    public ExchangeRatesDto getExchangeRateByCode(String code) {
        ExchangeRates exchangeRate = exchangeRatesDao.findByCode(code);
        ExchangeRatesDto exchangeRateDto = new ExchangeRatesDto(
                exchangeRate.getId(),
                currencyService.getCurrencyById(exchangeRate.getBaseCurrencyId()),
                currencyService.getCurrencyById(exchangeRate.getTargetCurrencyId()),
                exchangeRate.getRate()
        );
        return exchangeRateDto;
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

        ExchangeRatesDto addedExchangeDto = new ExchangeRatesDto(
                addedExchangeRate.getId(),
                currencyService.getCurrencyById(addedExchangeRate.getBaseCurrencyId()),
                currencyService.getCurrencyById(addedExchangeRate.getTargetCurrencyId()),
                addedExchangeRate.getRate()
        );
        return addedExchangeDto;
    }

    public ExchangeRatesDto updateExchangeRate(String pathInfo, BigDecimal rate) {
        ExchangeRates updatedExchangeRate = exchangeRatesDao.findByCode(pathInfo);
        updatedExchangeRate.setRate(rate);
        exchangeRatesDao.update(updatedExchangeRate);

        ExchangeRatesDto updatedExchangeDto = new ExchangeRatesDto(
                updatedExchangeRate.getId(),
                currencyService.getCurrencyById(updatedExchangeRate.getBaseCurrencyId()),
                currencyService.getCurrencyById(updatedExchangeRate.getTargetCurrencyId()),
                updatedExchangeRate.getRate()
        );
        return updatedExchangeDto;
    }

    // TODO дублирование кода



    public ExchangeConvertDto convert(String exchangeRateCode, BigDecimal amount) {
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
            isCrossRate = checkCrossRate(baseCurrencyCode, targetCurrencyCode);
        }catch (Exception e){
            e.printStackTrace();
        }


        if(directExchangeRate != null ) {
            return getConvertedCurrency(exchangeRateCode, amount);
        }else if(reverseExchangeRate != null ) {
            return getReverseConvertedCurrency(checkReverseExchangeRateCode, amount);
        }else if(isCrossRate){
            String code = codeForCrossRate(baseCurrencyCode, targetCurrencyCode);
            String baseCrossCode = code + baseCurrencyCode;
            String targetCrossCode = code + targetCurrencyCode;
            ExchangeRates crossBaseExchangeRate = exchangeRatesDao.findByCode(baseCrossCode);
            ExchangeRates crossTargetExchangeRate = exchangeRatesDao.findByCode(targetCrossCode);

            return getCrossCurrency(crossBaseExchangeRate, crossTargetExchangeRate, amount);
        }

        throw new IllegalArgumentException("Exchange rate code not found");
    }


    private boolean checkCrossRate(String baseCurrencyCode, String targetCurrencyCode){
        for(Currencies currency : currencies) {
            String checkCode = currency.getCode();
            String checkBaseCode = checkCode + baseCurrencyCode;
            String checkTargetCode = checkCode + targetCurrencyCode ;

            try{
                ExchangeRates baseExchangeRate = exchangeRatesDao.findByCode(checkBaseCode);
                ExchangeRates targetExchangeRate = exchangeRatesDao.findByCode(checkTargetCode);
                if(baseExchangeRate != null && targetExchangeRate != null) {
                    return true;
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return false;
    }

    //TODO код дублируется с методом checkCrossRate

    private String codeForCrossRate(String baseCurrencyCode, String targetCurrencyCode){
        for(Currencies currency : currencies) {
            String checkCode = currency.getCode();
            String checkBaseCode = checkCode + baseCurrencyCode;
            String checkTargetCode = checkCode + targetCurrencyCode;

            try{
                ExchangeRates baseExchangeRate = exchangeRatesDao.findByCode(checkBaseCode);
                ExchangeRates targetExchangeRate = exchangeRatesDao.findByCode(checkTargetCode);
                if(baseExchangeRate != null && targetExchangeRate != null) {
                    return checkCode;
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return "Cross Rate is not found";
    }

    // TODO дублирование кода

    private ExchangeConvertDto getConvertedCurrency(String exchangeRateCode, BigDecimal amount){

        ExchangeRates exchangeRate = exchangeRatesDao.findByCode(exchangeRateCode);
        BigDecimal convertedAmount = convertCurrency(exchangeRate, amount);

        ExchangeConvertDto exchangeConvertDto = new ExchangeConvertDto(
                exchangeRate.getId(),
                currencyService.getCurrencyById(exchangeRate.getBaseCurrencyId()),
                currencyService.getCurrencyById(exchangeRate.getTargetCurrencyId()),
                exchangeRate.getRate(),
                amount,
                convertedAmount
        );
        return exchangeConvertDto;
    }

    // TODO дублирование кода

    private ExchangeConvertDto getReverseConvertedCurrency(String exchangeRateCode, BigDecimal amount) {

        ExchangeRates exchangeRate = exchangeRatesDao.findByCode(exchangeRateCode);
        BigDecimal convertedAmount = convertReverseCurrency(exchangeRate, amount);

        ExchangeConvertDto exchangeConvertDto = new ExchangeConvertDto(
                exchangeRate.getId(),
                currencyService.getCurrencyById(exchangeRate.getTargetCurrencyId()),
                currencyService.getCurrencyById(exchangeRate.getBaseCurrencyId()),
                BigDecimal.ONE.divide(exchangeRate.getRate(), 6, BigDecimal.ROUND_HALF_UP),
                amount,
                convertedAmount
        );
        return exchangeConvertDto;
    }



    // TODO тут полно дублирования кода
    private ExchangeConvertDto getCrossCurrency(ExchangeRates checkBaseCode, ExchangeRates checkTargetCode,BigDecimal amount) {

        BigDecimal convertedAmount = getCrossRateCurrency(checkBaseCode, checkTargetCode, amount);
        BigDecimal baseRate = checkBaseCode.getRate();
        BigDecimal targetRate = checkTargetCode.getRate();
        BigDecimal crossRate = targetRate.divide(baseRate, 6, BigDecimal.ROUND_HALF_UP);


        ExchangeConvertDto exchangeConvertDto = new ExchangeConvertDto(
                currencyService.getCurrencyById(checkBaseCode.getTargetCurrencyId()),
                currencyService.getCurrencyById(checkTargetCode.getTargetCurrencyId()),
                crossRate,
                amount,
                convertedAmount
                );
        return exchangeConvertDto;
    }


    // TODO дублирование кода

    private BigDecimal convertCurrency(ExchangeRates exchangeRates, BigDecimal amount) {
        BigDecimal rate = exchangeRates.getRate();
        BigDecimal convertedAmount = amount.multiply(rate);
        convertedAmount.setScale(6, BigDecimal.ROUND_HALF_UP);
        return convertedAmount;
    }


    // TODO дублирование кода

    private BigDecimal convertReverseCurrency(ExchangeRates exchangeRates, BigDecimal amount) {
        BigDecimal rate = exchangeRates.getRate();
        BigDecimal reverseRate = BigDecimal.ONE.divide(rate, 6, BigDecimal.ROUND_HALF_UP);
        BigDecimal convertedAmount = amount.multiply(reverseRate);

        return convertedAmount;
    }

    private BigDecimal getCrossRateCurrency(ExchangeRates checkBaseCode, ExchangeRates checkTargetCode, BigDecimal amount) {
         BigDecimal baseRate = checkBaseCode.getRate();
         BigDecimal targetRate = checkTargetCode.getRate();
         BigDecimal crossRate = targetRate.divide(baseRate, 6, BigDecimal.ROUND_HALF_UP);
         BigDecimal convertedAmount = amount.multiply(crossRate);
         return convertedAmount;
    }
}
