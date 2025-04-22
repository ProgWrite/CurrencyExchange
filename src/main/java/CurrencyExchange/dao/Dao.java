package CurrencyExchange.dao;

import java.util.List;
import java.util.Optional;

public interface Dao <K,T> {

    List<T> findAll();


    //TODO потом переделай на Optional этот метод, по всей цепочке!

    T findById(K id);

    void update(T entity);

    T save(T entity);
}
