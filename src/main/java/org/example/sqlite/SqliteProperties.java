package org.example.sqlite;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@ConfigurationProperties("sqlite")
public class SqliteProperties {
    private static final Set<String> VALID_SYNCHRONOUS_VALUES = Set.of("OFF", "NORMAL", "FULL", "EXTRA");
    private static final Set<String> VALID_INIT_MODE_VALUES = Set.of("ALWAYS", "NEVER", "EMBEDDED");

    /**
     * Enable SQLite support.
     */
    private boolean enabled;
    
    /**
     * Path to the SQLite database file.
     */
    private Path file;
    
    /**
     * Enable Write-Ahead Logging (WAL) mode for better concurrency.
     * Default is true.
     */
    private boolean wal = true;
    
    /**
     * Synchronous mode for SQLite transactions.
     * Valid values: OFF, NORMAL, FULL, EXTRA. Default is NORMAL.
     */
    private String synchronous = "NORMAL";
    
    /**
     * Timeout in milliseconds to wait when the database is locked by another connection.
     */
    private Integer busyTimeout;
    
    /**
     * Maximum number of database disk pages that SQLite will hold in memory at once.
     * Positive values are in pages, negative values are in kibibytes (KiB).
     */
    private Integer cacheSize;
    
    /**
     * Schema initialization configuration.
     */
    private final Init init = new Init();

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Path getFile() {
        return file;
    }

    public void setFile(Path file) {
        this.file = file;
    }

    public boolean isWal() {
        return wal;
    }

    public void setWal(boolean wal) {
        this.wal = wal;
    }

    public String getSynchronous() {
        return synchronous;
    }

    public void setSynchronous(String synchronous) {
        this.synchronous = synchronous;
    }

    public Integer getBusyTimeout() {
        return busyTimeout;
    }

    public void setBusyTimeout(Integer busyTimeout) {
        this.busyTimeout = busyTimeout;
    }

    public Integer getCacheSize() {
        return cacheSize;
    }

    public void setCacheSize(Integer cacheSize) {
        this.cacheSize = cacheSize;
    }
    
    public Init getInit() {
        return init;
    }

    public void validate() {
        if (file == null) {
            throw new IllegalArgumentException("SQLite file path must be specified");
        }
        if (synchronous != null && !VALID_SYNCHRONOUS_VALUES.contains(synchronous.toUpperCase())) {
            throw new IllegalArgumentException("Invalid synchronous value: " + synchronous + ". Must be one of: " + VALID_SYNCHRONOUS_VALUES);
        }
        if (busyTimeout != null && busyTimeout < 0) {
            throw new IllegalArgumentException("busyTimeout must be non-negative");
        }
        if (init.mode != null && !VALID_INIT_MODE_VALUES.contains(init.mode.toUpperCase())) {
            throw new IllegalArgumentException("Invalid init mode: " + init.mode + ". Must be one of: " + VALID_INIT_MODE_VALUES);
        }
    }
    
    /**
     * Schema and data initialization properties.
     */
    public static class Init {
        /**
         * Schema SQL file locations. Supports classpath: and file: prefixes.
         * Example: classpath:schema.sql or file:/path/to/schema.sql
         */
        private List<String> schemaLocations = new ArrayList<>();
        
        /**
         * Data SQL file locations. Supports classpath: and file: prefixes.
         * Example: classpath:data.sql or file:/path/to/data.sql
         */
        private List<String> dataLocations = new ArrayList<>();
        
        /**
         * Mode for schema/data initialization.
         * ALWAYS - always initialize
         * NEVER - never initialize
         * EMBEDDED - initialize only for embedded databases (default for SQLite)
         */
        private String mode = "EMBEDDED";
        
        /**
         * Continue on error during initialization.
         */
        private boolean continueOnError = false;
        
        public List<String> getSchemaLocations() {
            return schemaLocations;
        }
        
        public void setSchemaLocations(List<String> schemaLocations) {
            this.schemaLocations = schemaLocations;
        }
        
        public List<String> getDataLocations() {
            return dataLocations;
        }
        
        public void setDataLocations(List<String> dataLocations) {
            this.dataLocations = dataLocations;
        }
        
        public String getMode() {
            return mode;
        }
        
        public void setMode(String mode) {
            this.mode = mode;
        }
        
        public boolean isContinueOnError() {
            return continueOnError;
        }
        
        public void setContinueOnError(boolean continueOnError) {
            this.continueOnError = continueOnError;
        }
    }
}