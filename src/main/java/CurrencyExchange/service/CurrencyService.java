package CurrencyExchange.service;

import CurrencyExchange.dao.CurrencyDao;
import CurrencyExchange.dto.CurrencyDto;
import CurrencyExchange.entity.Currencies;

import java.util.Currency;
import java.util.List;
import java.util.stream.Collectors;

public class CurrencyService {
    private static final CurrencyService INSTANCE = new CurrencyService();
    private final CurrencyDao currencyDao = CurrencyDao.getInstance();

    private CurrencyService() {

    }

    public static CurrencyService getInstance() {
        return INSTANCE;
    }

    public List<CurrencyDto> findAll() {
        return currencyDao.findAll().stream()
                .map(currencies -> new CurrencyDto(
                        currencies.getId(),
                        currencies.getCode(),
                        currencies.getFullName(),
                        currencies.getSign()
                ))
                .collect(Collectors.toList());
    }

}
