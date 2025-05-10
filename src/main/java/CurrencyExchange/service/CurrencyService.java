package CurrencyExchange.service;

import CurrencyExchange.dao.CurrencyDao;
import CurrencyExchange.dto.CurrencyDto;
import CurrencyExchange.entity.Currencies;
import CurrencyExchange.mapper.UserMapper;

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
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }


    //TODO null быть не должно, это заглушка

    public CurrencyDto getCurrencyByCode(String code) {
       Currencies currency =  currencyDao.findByCode(code);
       if(currency == null) {
           return null;
       }
       return convertToDto(currency);
    }


    public CurrencyDto getCurrencyById(long id) {
        Currencies currency =  currencyDao.findById(id);
        return convertToDto(currency);
    }


    //TODO здесь понадобится exception
    //TODO лучше переименуй метод на save()

    public CurrencyDto saveCurrency(CurrencyDto currencyDto) {
        Currencies currency = convertToCurrency(currencyDto);
        Currencies newCurrency = currencyDao.save(currency);
        return convertToDto(newCurrency);
    }






    private CurrencyDto convertToDto(Currencies currency) {
        return UserMapper.INSTANCE.currencyToCurrencyDtoWithId(
                currency.getId(),
                currency.getCode(),
                currency.getName(),
                currency.getSign()
        );
    }

    private Currencies convertToCurrency(CurrencyDto currencyDto) {
        return UserMapper.INSTANCE.currencyDtoToCurrenciesWithoutId(
                currencyDto.getCode(),
                currencyDto.getName(),
                currencyDto.getSign()
        );
    }

}
