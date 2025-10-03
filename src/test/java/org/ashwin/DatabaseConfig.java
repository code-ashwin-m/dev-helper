package org.ashwin;

import org.ashwin.service.annotations.DbConfig;

@DbConfig(
        url = "jdbc:sqlite:test.db",
        username = "root",
        password = "root",
        driver = "org.sqlite.JDBC"
)
public class DatabaseConfig {
}
