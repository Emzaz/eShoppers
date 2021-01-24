package com.emzaz.eshoppers.jdbc;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
import java.util.ResourceBundle;

public class ConnectionPool {
    private static final ConnectionPool INSTANCE = new ConnectionPool();

    private ConnectionPool() {
    }

    public static ConnectionPool getInstance() {
        return INSTANCE;
    }

    public DataSource getDataSource() {
        ResourceBundle dbProp = ResourceBundle.getBundle("db");

        HikariConfig config = new HikariConfig();

        config.setJdbcUrl(dbProp.getString("db.url"));
        config.setUsername(dbProp.getString("db.user"));
        config.setPassword(dbProp.getString("db.password"));
        config.setDriverClassName(dbProp.getString("db.driver"));

        String maxPoolSize = dbProp.getString("db.max.connections");

        config.setMaximumPoolSize(Integer.parseInt(maxPoolSize));

        return new HikariDataSource(config);
    }
}
