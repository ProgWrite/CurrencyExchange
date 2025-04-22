package CurrencyExchange.service;

import CurrencyExchange.dao.CurrencyDao;
import CurrencyExchange.dao.ExchangeRatesDao;
import CurrencyExchange.dto.ExchangeRatesDto;
import CurrencyExchange.entity.Currencies;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ExchangeRatesService {
    private final static ExchangeRatesService INSTANCE = new ExchangeRatesService();
    private final ExchangeRatesDao exchangeRatesDao = ExchangeRatesDao.getInstance();
    private final CurrencyService currencyService = CurrencyService.getInstance();

    private ExchangeRatesService() {

    }

    public static ExchangeRatesService getInstance() {
        return INSTANCE;
    }

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

}
