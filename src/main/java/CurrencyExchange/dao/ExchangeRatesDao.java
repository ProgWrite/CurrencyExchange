package CurrencyExchange.dao;

import CurrencyExchange.entity.ExchangeRates;
import CurrencyExchange.exceptions.DataBaseException;
import CurrencyExchange.util.SQLConnectionManager;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class ExchangeRatesDao implements Dao<ExchangeRates> {

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
            throw new DataBaseException("Failed to find exchange rates");
        }
    }

    public Optional<ExchangeRates> findByCode(String code) {
        try (Connection connection = SQLConnectionManager.getDataSource().getConnection()) {
            PreparedStatement statement = connection.prepareStatement(FIND_BY_CODE);
            String baseCurrency = code.substring(0, 3);
            String targetCurrency = code.substring(3, 6);
            statement.setString(1, baseCurrency);
            statement.setString(2, targetCurrency);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return Optional.of(buildExchangeRates(resultSet));
            } else {
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new DataBaseException("Failed to find exchange rate with code " + code);
        }
    }


    public void update(ExchangeRates exchangeRates) {
        try (Connection connection = SQLConnectionManager.getDataSource().getConnection()) {
            PreparedStatement statement = connection.prepareStatement(UPDATE);
            statement.setBigDecimal(1, exchangeRates.getRate());
            statement.setLong(2, exchangeRates.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataBaseException("Failed to update exchange rate with id " + exchangeRates.getId());
        }
    }

    @Override
    public ExchangeRates create(ExchangeRates exchangeRates) {
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
            throw new DataBaseException("Failed to create exchange rate");
        }
    }

    private ExchangeRates buildExchangeRates(ResultSet resultSet) throws SQLException {
        return new ExchangeRates(
                resultSet.getLong("id"),
                resultSet.getInt("BaseCurrencyId"),
                resultSet.getInt("TargetCurrencyId"),
                resultSet.getBigDecimal("Rate")
        );
    }
}
