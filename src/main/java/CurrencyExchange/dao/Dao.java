package CurrencyExchange.dao;

import java.util.List;
import java.util.Optional;

public interface Dao <K,T> {

    List<T> findAll();

    Optional<T> findByCode(String code);

    void update(T entity);

    T create(T entity);
}
