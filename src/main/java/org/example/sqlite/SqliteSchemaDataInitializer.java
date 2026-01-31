package org.example.sqlite;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.jdbc.datasource.init.ScriptException;

import javax.sql.DataSource;
import java.util.List;

public class SqliteSchemaDataInitializer {
    private static final Logger logger = LoggerFactory.getLogger(SqliteSchemaDataInitializer.class);
    
    private final JdbcTemplate jdbcTemplate;
    private final DataSource dataSource;
    private final SqliteProperties.Init initProperties;
    private final ResourceLoader resourceLoader;
    
    public SqliteSchemaDataInitializer(JdbcTemplate jdbcTemplate, 
                                       DataSource dataSource,
                                       SqliteProperties props) {
        this.jdbcTemplate = jdbcTemplate;
        this.dataSource = dataSource;
        this.initProperties = props.getInit();
        this.resourceLoader = new DefaultResourceLoader();
        
        initializeDatabase();
    }
    
    private void initializeDatabase() {
        String mode = initProperties.getMode().toUpperCase();
        
        if ("NEVER".equals(mode)) {
            logger.info("SQLite database initialization is disabled (mode=NEVER)");
            return;
        }
        
        if ("EMBEDDED".equals(mode)) {
            logger.info("SQLite database initialization mode is EMBEDDED (always runs for SQLite)");
        } else if ("ALWAYS".equals(mode)) {
            logger.info("SQLite database initialization mode is ALWAYS");
        }
        
        // Execute schema scripts
        List<String> schemaLocations = initProperties.getSchemaLocations();
        if (schemaLocations != null && !schemaLocations.isEmpty()) {
            logger.info("Executing schema initialization scripts");
            executeScripts(schemaLocations, "schema");
        }
        
        // Execute data scripts
        List<String> dataLocations = initProperties.getDataLocations();
        if (dataLocations != null && !dataLocations.isEmpty()) {
            logger.info("Executing data initialization scripts");
            executeScripts(dataLocations, "data");
        }
        
        if ((schemaLocations == null || schemaLocations.isEmpty()) && 
            (dataLocations == null || dataLocations.isEmpty())) {
            logger.info("No schema or data initialization scripts configured");
        }
    }
    
    private void executeScripts(List<String> locations, String scriptType) {
        ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
        populator.setContinueOnError(initProperties.isContinueOnError());
        populator.setCommentPrefixes("--", "#");
        populator.setSeparator(";");
        
        for (String location : locations) {
            try {
                Resource resource = resourceLoader.getResource(location);
                if (!resource.exists()) {
                    String message = String.format("SQLite %s script not found: %s", scriptType, location);
                    if (initProperties.isContinueOnError()) {
                        logger.warn(message);
                        continue;
                    } else {
                        throw new IllegalStateException(message);
                    }
                }
                
                logger.info("Executing SQLite {} script: {}", scriptType, location);
                populator.addScript(resource);
                
            } catch (Exception e) {
                String message = String.format("Failed to load SQLite %s script: %s", scriptType, location);
                if (initProperties.isContinueOnError()) {
                    logger.error(message, e);
                } else {
                    throw new IllegalStateException(message, e);
                }
            }
        }
        
        // Execute all collected scripts
        try {
            populator.populate(dataSource.getConnection());
            logger.info("Successfully executed {} {} script(s)", locations.size(), scriptType);
        } catch (ScriptException e) {
            String message = String.format("Error executing SQLite %s scripts", scriptType);
            if (initProperties.isContinueOnError()) {
                logger.error(message, e);
            } else {
                throw new IllegalStateException(message, e);
            }
        } catch (Exception e) {
            String message = String.format("Failed to execute SQLite %s scripts", scriptType);
            if (initProperties.isContinueOnError()) {
                logger.error(message, e);
            } else {
                throw new IllegalStateException(message, e);
            }
        }
    }
}
