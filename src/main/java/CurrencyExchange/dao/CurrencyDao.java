package CurrencyExchange.dao;

import CurrencyExchange.entity.Currencies;
import CurrencyExchange.exceptions.DataBaseException;
import CurrencyExchange.exceptions.NotFoundException;
import CurrencyExchange.util.SQLConnectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

public class CurrencyDao implements Dao<Currencies> {

    private final static CurrencyDao INSTANCE = new CurrencyDao();

    private final static String FIND_ALL = """
    SELECT * FROM Currencies;
    """;

    private final static String FIND_BY_CODE = """
    SELECT * FROM Currencies
    WHERE code = ?
    """;

    private final static String FIND_BY_ID = """
    SELECT * FROM Currencies
    WHERE id = ?
    """;



    private final static String SAVE_CURRENCY = """
            INSERT INTO Currencies(code, name, sign)
            VALUES (?, ?, ?)
            """;


    private CurrencyDao() {

    }

    public static CurrencyDao getInstance() {
        return INSTANCE;
    }


    @Override
    public List<Currencies> findAll() {
        List<Currencies> currencies = new ArrayList<>();
        try (Connection connection = SQLConnectionManager.getDataSource().getConnection()) {
            PreparedStatement statement = connection.prepareStatement(FIND_ALL);
                 ResultSet resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    currencies.add(buildCurrency(resultSet));
                }
                return currencies;
        } catch (SQLException e) {
            throw new DataBaseException("Failed to find currencies");
        }
    }


    public Optional<Currencies> findByCode(String code) {
        try (Connection connection = SQLConnectionManager.getDataSource().getConnection()) {
            PreparedStatement statement = connection.prepareStatement(FIND_BY_CODE);
            statement.setString(1, code);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return Optional.of(buildCurrency(resultSet));
            } else {
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new DataBaseException("Failed to find currency with code " + code);
        }
    }


    //TODO Здесь есть дублирование кода c методом выше, может что-то можно придумать

    public   Optional<Currencies> findById(Long id) {
        try (Connection connection = SQLConnectionManager.getDataSource().getConnection()) {
            PreparedStatement statement = connection.prepareStatement(FIND_BY_ID);
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return Optional.of((buildCurrency(resultSet)));
            } else {
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new DataBaseException("Failed to find currency with id " + id);
        }
    }

    @Override
    public Currencies create(Currencies currency) {
        try (Connection connection = SQLConnectionManager.getDataSource().getConnection()) {
            PreparedStatement statement = connection.prepareStatement(SAVE_CURRENCY);
            statement.setString(1, currency.getCode());
            statement.setString(2, currency.getName());
            statement.setString(3, currency.getSign());
            statement.execute();
            ResultSet resultSet = statement.getGeneratedKeys();
            currency.setId(resultSet.getLong(1));
            return currency;
        } catch (SQLException e) {
            throw new DataBaseException("Failed to create currency");
        }
    }

    private Currencies buildCurrency(ResultSet resultSet) throws SQLException {
        return new Currencies(
                resultSet.getObject("id", Long.class),
                resultSet.getObject("code", String.class),
                resultSet.getObject("name", String.class),
                resultSet.getObject("sign", String.class)
        );
    }

}
