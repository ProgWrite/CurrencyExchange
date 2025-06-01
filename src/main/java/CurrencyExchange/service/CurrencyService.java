package CurrencyExchange.service;

import CurrencyExchange.dao.CurrencyDao;
import CurrencyExchange.dto.CurrencyDto;
import CurrencyExchange.entity.Currencies;
import CurrencyExchange.exceptions.NotFoundException;
import CurrencyExchange.utils.MappingUtils;

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
                .map(MappingUtils::convertToDto)
                .collect(Collectors.toList());
    }

    public CurrencyDto getCurrencyByCode(String code) {
        Currencies currency = currencyDao.findByCode(code)
                .orElseThrow(() -> new NotFoundException("Currency not found with code:" + code));
        return MappingUtils.convertToDto(currency);
    }

    public CurrencyDto create(CurrencyDto currencyDto) {
        Currencies currency = MappingUtils.convertToEntity(currencyDto);
        Currencies newCurrency = currencyDao.create(currency);
        return MappingUtils.convertToDto(newCurrency);

    }
}
