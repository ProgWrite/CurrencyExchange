package CurrencyExchange.dao;

import CurrencyExchange.entity.Currencies;
import CurrencyExchange.entity.ExchangeRates;
import CurrencyExchange.exceptions.DataBaseException;
import CurrencyExchange.utils.SQLConnectionManager;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.Optional;


public class ExchangeRatesDao implements Dao<ExchangeRates> {

    private final static ExchangeRatesDao INSTANCE = new ExchangeRatesDao();

    private final static String FIND_ALL =
            "  SELECT" +
                    "    er.id AS id," +
                    "    bc.id AS base_id," +
                    "    bc.code AS base_code," +
                    "    bc.name AS base_name," +
                    "    bc.sign AS base_sign," +
                    "    tc.id AS target_id," +
                    "    tc.code AS target_code," +
                    "    tc.name AS target_name," +
                    "    tc.sign AS target_sign," +
                    "    er.rate AS rate" +
                    "  FROM ExchangeRates er" +
                    "  JOIN Currencies bc ON er.BaseCurrencyId = bc.id" +
                    "  JOIN Currencies tc ON er.TargetCurrencyId = tc.id" +
                    "  ORDER BY er.id";
            ;

    private final static String FIND_BY_CODE =
            "  SELECT" +
                    "    er.id AS id," +
                    "    bc.id AS base_id," +
                    "    bc.code AS base_code," +
                    "    bc.name AS base_name," +
                    "    bc.sign AS base_sign," +
                    "    tc.id AS target_id," +
                    "    tc.code AS target_code," +
                    "    tc.name AS target_name," +
                    "    tc.sign AS target_sign," +
                    "    er.rate AS rate" +
                    "  FROM ExchangeRates er" +
                    "  JOIN Currencies bc ON er.BaseCurrencyId = bc.id" +
                    "  JOIN Currencies tc ON er.TargetCurrencyId = tc.id" +
                    "  WHERE bc.code = ? AND tc.code = ?";


    private final static String ADD_EXCHANGE_RATE = """
            INSERT INTO ExchangeRates (BaseCurrencyId, TargetCurrencyId, Rate) 
            VALUES (?, ?, ?) RETURNING id
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
        try (Connection connection = SQLConnectionManager.getDataSource().getConnection()) {
            PreparedStatement statement = connection.prepareStatement(FIND_ALL);
            ResultSet resultSet = statement.executeQuery();

            List<ExchangeRates> exchangeRates = new ArrayList<>();

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
            }
        } catch (SQLException e) {
            throw new DataBaseException("Failed to find exchange rate with code " + code);
        }
        return Optional.empty();
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

    //TODO проработай эту ошибку
    @Override
    public ExchangeRates create(ExchangeRates exchangeRates) {
        try (Connection connection = SQLConnectionManager.getDataSource().getConnection()) {
            PreparedStatement statement = connection.prepareStatement(ADD_EXCHANGE_RATE);

            statement.setLong(1, exchangeRates.getBaseCurrency().getId());
            statement.setLong(2, exchangeRates.getTargetCurrency().getId());
            statement.setBigDecimal(3, exchangeRates.getRate());
            statement.execute();

            ResultSet resultSet = statement.getGeneratedKeys();
            exchangeRates.setId(resultSet.getLong(1));
            return exchangeRates;
        } catch (SQLException e) {
            throw new DataBaseException(
                    String.format("Failed to save exchange rate '%s' to '%s' to the database",
                            exchangeRates.getBaseCurrency().getCode(), exchangeRates.getTargetCurrency().getCode())
            );
        }
    }

    private ExchangeRates buildExchangeRates(ResultSet resultSet) throws SQLException {
        return new ExchangeRates(
                resultSet.getLong("id"),
                new Currencies(
                        resultSet.getLong("base_id"),
                        resultSet.getString("base_code"),
                        resultSet.getString("base_name"),
                        resultSet.getString("base_sign")
                ),
                new Currencies(
                        resultSet.getLong("target_id"),
                        resultSet.getString("target_code"),
                        resultSet.getString("target_name"),
                        resultSet.getString("target_sign")
                ),
                resultSet.getBigDecimal("rate")
        );
    }
}
