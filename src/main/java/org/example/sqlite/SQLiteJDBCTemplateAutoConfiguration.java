package org.example.sqlite;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.sqlite.SQLiteDataSource;

import javax.sql.DataSource;
import java.nio.file.Files;
import java.nio.file.Path;

@AutoConfiguration
@ConditionalOnProperty(prefix = "sqlite", name = "enabled", havingValue = "true")
@EnableConfigurationProperties(SqliteProperties.class)
public class SQLiteJDBCTemplateAutoConfiguration {

    @Bean(name = "sqliteDataSource")
    @ConditionalOnMissingBean(name = "sqliteDataSource")
    DataSource sqliteDataSource(SqliteProperties props) {
        props.validate();
        
        Path filePath = props.getFile();
        Path parentDir = filePath.getParent();
        
        if (parentDir != null && !Files.exists(parentDir)) {
            try {
                Files.createDirectories(parentDir);
            } catch (Exception e) {
                throw new IllegalStateException(
                    "Failed to create directory for SQLite database: " + parentDir, e
                );
            }
        }
        
        SQLiteDataSource ds = new SQLiteDataSource();
        ds.setUrl("jdbc:sqlite:" + filePath);
        return ds;
    }

    @Bean(name = "sqliteJdbcTemplate")
    @ConditionalOnMissingBean(name = "sqliteJdbcTemplate")
    public JdbcTemplate sqliteJdbcTemplate(@Qualifier("sqliteDataSource") DataSource ds) {
        return new JdbcTemplate(ds);
    }

    @Bean
    @ConditionalOnProperty(prefix = "sqlite", name = "enabled", havingValue = "true")
    public SqlitePragmaInitializer sqlitePragmaInitializer(
            @Qualifier("sqliteJdbcTemplate") JdbcTemplate jdbcTemplate,
            SqliteProperties props) {
        return new SqlitePragmaInitializer(jdbcTemplate, props);
    }

}
