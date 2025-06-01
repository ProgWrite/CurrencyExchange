package CurrencyExchange.utils;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class SQLConnectionManager {
    private static final HikariDataSource dataSource;

    private SQLConnectionManager(){

    }

    static {
        HikariConfig config = new HikariConfig();
        Properties properties = new Properties();
        try (InputStream input = SQLConnectionManager.class.getClassLoader().getResourceAsStream("application.properties")) {
            properties.load(input);
            config.setJdbcUrl(properties.getProperty("db.url"));
            config.setDriverClassName("org.sqlite.JDBC");
            config.setMaximumPoolSize(Integer.parseInt(properties.getProperty("db.pool.size")));
            config.setConnectionTimeout(Long.parseLong(properties.getProperty("db.connection.timeout")));
        } catch (IOException e) {
            e.printStackTrace();
        }

        dataSource = new HikariDataSource(config);
    }

    public static DataSource getDataSource() {
        return dataSource;
    }
}
