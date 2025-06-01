package CurrencyExchange.dao;

import CurrencyExchange.entity.Currencies;
import CurrencyExchange.exceptions.DataBaseException;
import CurrencyExchange.exceptions.EntityExistsException;
import CurrencyExchange.utils.SQLConnectionManager;
import org.sqlite.SQLiteErrorCode;
import org.sqlite.SQLiteException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
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
            }
        } catch (SQLException e) {
            throw new DataBaseException("Failed to find currency with code " + code);
        }
        return Optional.empty();
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
        }
        catch (SQLException e) {
            if (e instanceof SQLiteException) {
                SQLiteException exception = (SQLiteException) e;
                if (exception.getResultCode().code == SQLiteErrorCode.SQLITE_CONSTRAINT_UNIQUE.code) {
                    throw new EntityExistsException("Currency with code '" + currency.getCode() + "' already exists");
                }
            }
            throw new DataBaseException("Failed to save currency with code '" + currency.getCode() + "' to the database");
        }
    }

    private Currencies buildCurrency(ResultSet resultSet) throws SQLException {
        return new Currencies(
                resultSet.getLong("id"),
                resultSet.getString("code"),
                resultSet.getString("name"),
                resultSet.getString("sign")
        );
    }
}