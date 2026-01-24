package org.example.sqlite;

import org.springframework.jdbc.core.JdbcTemplate;

public class SqlitePragmaInitializer {

    public SqlitePragmaInitializer(JdbcTemplate jdbcTemplate, SqliteProperties props) {
        if (props.isWal()) {
            jdbcTemplate.execute("PRAGMA journal_mode=WAL");
        }
        jdbcTemplate.execute("PRAGMA synchronous=" + props.getSynchronous());
        
        if (props.getBusyTimeout() != null) {
            jdbcTemplate.execute("PRAGMA busy_timeout=" + props.getBusyTimeout());
        }
        
        if (props.getCacheSize() != null) {
            jdbcTemplate.execute("PRAGMA cache_size=" + props.getCacheSize());
        }
    }
}
