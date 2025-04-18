package CurrencyExchange.dao;

import CurrencyExchange.entity.Currencies;
import CurrencyExchange.util.SQLConnectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CurrencyDao implements Dao<Long, Currencies> {

    private final static CurrencyDao INSTANCE = new CurrencyDao();
    private final static String FIND_ALL = """
    SELECT * FROM Currencies;
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
            try (PreparedStatement statement = connection.prepareStatement(FIND_ALL);
                 ResultSet resultSet = statement.executeQuery()) {

                while (resultSet.next()) {
                    currencies.add(buildCurrency(resultSet));
                }

                return currencies;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Currencies> findById(Long id) {
        return Optional.empty();
    }

    @Override
    public void update(Currencies entity) {

    }

    @Override
    public Currencies save(Currencies entity) {
        return null;
    }

    private Currencies buildCurrency(ResultSet resultSet) throws SQLException {
        return new Currencies(
                resultSet.getObject("id", Long.class),
                resultSet.getObject("code", String.class),
                resultSet.getObject("fullname", String.class),
                resultSet.getObject("sign", String.class)
        );
    }

}
