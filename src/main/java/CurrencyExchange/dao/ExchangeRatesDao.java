package CurrencyExchange.dao;

import CurrencyExchange.entity.Currencies;
import CurrencyExchange.entity.ExchangeRates;
import CurrencyExchange.util.SQLConnectionManager;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

//TODO дублирование кода в переменных, в методах, мб что-то можно придумать


public class ExchangeRatesDao implements Dao<Long, ExchangeRates> {

    private final static ExchangeRatesDao INSTANCE = new ExchangeRatesDao();

    private final static String FIND_ALL = """
    SELECT * FROM ExchangeRates
    """;

    private final static String FIND_BY_CODE = """
            SELECT er.id, er.BaseCurrencyId, er.TargetCurrencyId, er.Rate
            FROM ExchangeRates er
            JOIN Currencies bc ON (er.BaseCurrencyId = bc.id )
            JOIN Currencies tc ON (er.TargetCurrencyId = tc.id)
            WHERE bc.code = ? AND tc.code = ?;
            """;

    //TODO возможно хватит просто "ADD", подумай об этом

    private final static String ADD_EXCHANGE_RATE = """
            INSERT INTO ExchangeRates (BaseCurrencyId, TargetCurrencyId, Rate) 
            VALUES (?, ?, ?);
            """;

    private final static String UPDATE = """
            UPDATE ExchangeRates SET Rate = ?
            WHERE id = ?;
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

    public ExchangeRates findByCode(String code) {
        try (Connection connection = SQLConnectionManager.getDataSource().getConnection()) {
            PreparedStatement statement = connection.prepareStatement(FIND_BY_CODE);
            String baseCurrency = code.substring(0, 3);
            String targetCurrency = code.substring(3, 6);
            statement.setString(1, baseCurrency);
            statement.setString(2, targetCurrency);
            ResultSet resultSet = statement.executeQuery();

            if(resultSet.next()) {
                return buildExchangeRates(resultSet);
            }else{
                throw new NoSuchElementException("exchange rate not found");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ExchangeRates findById(Long id) {
        return null;
    }


    //TODO тоже нужна валидация

    @Override
    public void update(ExchangeRates exchangeRates) {
        try (Connection connection = SQLConnectionManager.getDataSource().getConnection()) {
            PreparedStatement statement = connection.prepareStatement(UPDATE);
            statement.setBigDecimal(1, exchangeRates.getRate());
            statement.setLong(2, exchangeRates.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    //TODO приведи в порядок. Везде используй add(), а не set()

    @Override
    public ExchangeRates save(ExchangeRates exchangeRates) {
        try (Connection connection = SQLConnectionManager.getDataSource().getConnection()) {
            PreparedStatement statement = connection.prepareStatement(ADD_EXCHANGE_RATE);
            statement.setInt(1, exchangeRates.getBaseCurrencyId());
            statement.setInt(2, exchangeRates.getTargetCurrencyId());
            statement.setBigDecimal(3, exchangeRates.getRate());
            statement.execute();
            ResultSet resultSet = statement.getGeneratedKeys();
            exchangeRates.setId(resultSet.getLong(1));
            return exchangeRates;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
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
