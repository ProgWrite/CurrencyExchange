package CurrencyExchange.service;

import CurrencyExchange.dao.CurrencyDao;
import CurrencyExchange.dao.ExchangeRatesDao;
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
}
