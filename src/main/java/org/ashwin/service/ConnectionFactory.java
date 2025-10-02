package org.ashwin.service;

import org.ashwin.service.annotations.DbConfig;

import java.sql.Connection;
import java.sql.DriverManager;

public class ConnectionFactory {
    private static Connection connection;

    public static Connection getConnection(Class<?> configClass) throws Exception {
        if (connection == null || connection.isClosed()) {
            if (configClass.isAnnotationPresent(DbConfig.class)) {
                DbConfig dbConfig = configClass.getAnnotation(DbConfig.class);
                Class.forName(dbConfig.driver());
                connection = DriverManager.getConnection(dbConfig.url(), dbConfig.username(), dbConfig.password());
            } else {
                throw new Exception("No DbConfig annotation found");
            }
        }
        return connection;
    }
}
