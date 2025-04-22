package CurrencyExchange.dao;

import CurrencyExchange.entity.Currencies;
import CurrencyExchange.entity.ExchangeRates;
import CurrencyExchange.util.SQLConnectionManager;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.Optional;

//TODO дублирование кода в переменных, в методах, мб что-то можно придумать


public class ExchangeRatesDao implements Dao<Long, ExchangeRates> {

    private final static ExchangeRatesDao INSTANCE = new ExchangeRatesDao();

    private final static String FIND_ALL = """
    SELECT * FROM ExchangeRates
    """;


    private ExchangeRatesDao() {

    }


    public static ExchangeRatesDao getInstance() {
        return INSTANCE;
    }


    @Override
    public List<ExchangeRates> findAll() {
        List<ExchangeRates> exchangeRates = new ArrayList<>();
        try (Connection connection = SQLConnectionManager.getDataSource().getConnection()) {
            PreparedStatement statement = connection.prepareStatement(FIND_ALL);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                exchangeRates.add(buildExchangeRates(resultSet));
            }
            return exchangeRates;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public ExchangeRates findById(Long id) {
        return null;
    }

    @Override
    public void update(ExchangeRates entity) {

    }

    @Override
    public ExchangeRates save(ExchangeRates entity) {
        return null;
    }

    private ExchangeRates buildExchangeRates(ResultSet resultSet) throws SQLException {
        return new ExchangeRates(
                resultSet.getObject("id", Long.class),
                resultSet.getObject("BaseCurrencyId", Integer.class),
                resultSet.getObject("TargetCurrencyId", Integer.class),
                resultSet.getObject("Rate", BigDecimal.class)
        );
    }

}
