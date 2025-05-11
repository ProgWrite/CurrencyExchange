package CurrencyExchange.service;

import CurrencyExchange.dao.CurrencyDao;
import CurrencyExchange.dto.CurrencyDto;
import CurrencyExchange.entity.Currencies;
import CurrencyExchange.exceptions.NotFoundException;
import CurrencyExchange.exceptions.ServiceException;
import CurrencyExchange.mapper.UserMapper;

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


    public CurrencyDto getCurrencyByCode(String code) {
       Currencies currency = currencyDao.findByCode(code)
                .orElseThrow(()-> new NotFoundException("Currency not found with code:" + code));
       return convertToDto(currency);
    }

    public CurrencyDto getCurrencyById(long id) {
        Currencies currency =  currencyDao.findById(id).
                orElseThrow(() -> new NotFoundException("Currency not found with id: " + id));
        return convertToDto(currency);
    }


    public CurrencyDto create(CurrencyDto currencyDto) {
        try{
            Currencies currency = convertToCurrency(currencyDto);
            Currencies newCurrency = currencyDao.create(currency);
            return convertToDto(newCurrency);
        }catch(RuntimeException e){
            throw new ServiceException("Currency creation failed.");
        }
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
