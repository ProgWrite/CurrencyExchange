package CurrencyExchange.service;

import CurrencyExchange.dao.CurrencyDao;
import CurrencyExchange.dto.CurrencyDto;
import CurrencyExchange.entity.Currencies;

import java.util.Currency;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    //TODO null быть не должно, это заглушка

    public CurrencyDto getCurrencyByCode(String code) {
       Currencies currency =  currencyDao.findByCode(code);
       if(currency == null) {
           return null;
       }
       CurrencyDto currencyDto = new CurrencyDto(
               currency.getId(),
               currency.getCode(),
               currency.getFullName(),
               currency.getSign()
       );
       return currencyDto;
    }

    public CurrencyDto getCurrencyById(long id) {
        Currencies currency =  currencyDao.findById(id);
        CurrencyDto currencyDto = new CurrencyDto(
                currency.getId(),
                currency.getCode(),
                currency.getFullName(),
                currency.getSign()
        );
        return currencyDto;
    }




    //TODO здесь понадобится exception
    //TODO лучше переименуй метод на save()

    public CurrencyDto saveCurrency(CurrencyDto currencyDto) {
        Currencies currency = new Currencies();
        currency.setCode(currencyDto.getCode());
        currency.setFullName(currencyDto.getFullName());
        currency.setSign(currencyDto.getSign());

        Currencies newCurrency = currencyDao.save(currency);

        CurrencyDto newCurrencyDto = new CurrencyDto(
                newCurrency.getId(),
                newCurrency.getCode(),
                newCurrency.getFullName(),
                newCurrency.getSign()
        );
        return newCurrencyDto;
    }









}
