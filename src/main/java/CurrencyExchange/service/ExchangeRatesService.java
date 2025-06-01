package CurrencyExchange.service;

import CurrencyExchange.dao.CurrencyDao;
import CurrencyExchange.dao.ExchangeRatesDao;
import CurrencyExchange.dto.ExchangeRatesDto;
import CurrencyExchange.entity.Currencies;
import CurrencyExchange.entity.ExchangeRates;
import CurrencyExchange.exceptions.NotFoundException;
import CurrencyExchange.utils.MappingUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


public class ExchangeRatesService {
    private final static ExchangeRatesService INSTANCE = new ExchangeRatesService();
    private final ExchangeRatesDao exchangeRatesDao = ExchangeRatesDao.getInstance();
    private final CurrencyDao currencyDao = CurrencyDao.getInstance();

    private ExchangeRatesService() {

    }

    public static ExchangeRatesService getInstance() {
        return INSTANCE;
    }

    public List<ExchangeRatesDto> findAll() {
        return exchangeRatesDao.findAll().stream()
                .map(MappingUtils::convertToDto)
                .collect(Collectors.toList());
    }

    public ExchangeRatesDto getExchangeRateByCode(String code) {
        Optional<ExchangeRates> exchangeRate = exchangeRatesDao.findByCode(code);
        return exchangeRate.map(MappingUtils::convertToDto)
                .orElseThrow(() -> new NotFoundException("No exchange rate found with code " + code));
    }


    public ExchangeRatesDto create(String baseCurrencyCode, String targetCurrencyCode, BigDecimal rate) {
        Currencies baseCurrency = currencyDao.findByCode(baseCurrencyCode)
                .orElseThrow(() -> new NotFoundException("No currency found with code " + baseCurrencyCode));
        Currencies targetCurrency = currencyDao.findByCode(targetCurrencyCode)
                .orElseThrow(() -> new NotFoundException("No currency found with code " + targetCurrencyCode));

        ExchangeRates exchangeRate = new ExchangeRates(baseCurrency, targetCurrency, rate);
        ExchangeRates newExchangeRate = exchangeRatesDao.create(exchangeRate);

        return MappingUtils.convertToDto(newExchangeRate);
    }


    public ExchangeRatesDto update(String pathInfo, BigDecimal rate) {
        ExchangeRates updatedExchangeRate = exchangeRatesDao.findByCode(pathInfo)
                .orElseThrow(() -> new NotFoundException("No exchange rate found with path " + pathInfo));
        updatedExchangeRate.setRate(rate);
       exchangeRatesDao.update(updatedExchangeRate);
       return MappingUtils.convertToDto(updatedExchangeRate);
    }
}
