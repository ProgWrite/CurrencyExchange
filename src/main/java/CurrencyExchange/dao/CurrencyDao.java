package CurrencyExchange.dao;

import CurrencyExchange.entity.Currencies;
import CurrencyExchange.util.SQLConnectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public class CurrencyDao implements Dao<Long, Currencies> {

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
            throw new RuntimeException(e);
        }
    }

    //TODO может быть здесь нужен Optional, надо уточнять. Что из этого больше похоже на "взрослый"
    // подход к решению такой дилеммы (эту фразу можно искать по поиску в чате java). Null здесь это заглушка, надо будет исправлять!


    public Currencies findByCode(String code) {
        try (Connection connection = SQLConnectionManager.getDataSource().getConnection()) {
            PreparedStatement statement = connection.prepareStatement(FIND_BY_CODE);
            statement.setString(1, code);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return buildCurrency(resultSet);
            } else {
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    //TODO Здесь есть дублирование кода c другим методом. Надо подумать, мб все это в интерфейс потом

    @Override
    public Currencies findById(Long id) {
        try (Connection connection = SQLConnectionManager.getDataSource().getConnection()) {
            PreparedStatement statement = connection.prepareStatement(FIND_BY_ID);
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return (buildCurrency(resultSet));
            } else {
                throw new NoSuchElementException("currency not found");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }



    @Override
    public Currencies save(Currencies currency) {
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
            throw new RuntimeException(e);
        }
    }





    @Override
    public void update(Currencies entity) {

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
