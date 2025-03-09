package com.ptit.blogtechnology.config;

import com.mysql.cj.jdbc.MysqlDataSource;
import org.flywaydb.core.Flyway;

public class DatabaseMigration {
    public static void main(String[] args) {
        MysqlDataSource dataSource = new MysqlDataSource();
        dataSource.setUrl("jdbc:mysql://localhost:3306/blog-technology?allowPublicKeyRetrieval=true&useSSL=false");
        dataSource.setUser("root");
        dataSource.setPassword("root");

        Flyway flyway = Flyway.configure()
                .dataSource(dataSource)
                .locations("classpath:db/migration")
                .validateMigrationNaming(true)
                .load();

        flyway.migrate();
    }
}

