package CurrencyExchange.dao;

import java.util.List;
import java.util.Optional;

public interface Dao <T> {

    List<T> findAll();

    Optional<T> findByCode(String code);

    T create(T entity);
}
