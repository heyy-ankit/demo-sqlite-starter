# SQLite Spring Boot Starter

A Spring Boot starter that provides auto-configuration for SQLite databases with JdbcTemplate support, including schema and data initialization capabilities.

## Features

- Auto-configuration of SQLite DataSource and JdbcTemplate
- Automatic directory creation for database files
- SQLite-specific PRAGMA settings (WAL mode, synchronous mode, busy timeout, cache size)
- **Schema and data initialization from SQL files**
- Configuration properties with validation
- Support for both classpath and file system resources

## Installation

Add the dependency to your `pom.xml`:

```xml
<dependency>
    <groupId>org.example</groupId>
    <artifactId>demo-sqlite-starter</artifactId>
    <version>1.1-SNAPSHOT</version>
</dependency>
```

## Basic Configuration

Configure SQLite in your `application.properties` or `application.yml`:

### application.properties
```properties
# Enable SQLite support
sqlite.enabled=true

# Database file location
sqlite.file=./data/myapp.db

# Optional: WAL mode (default: true)
sqlite.wal=true

# Optional: Synchronous mode (default: NORMAL)
# Valid values: OFF, NORMAL, FULL, EXTRA
sqlite.synchronous=NORMAL

# Optional: Busy timeout in milliseconds
sqlite.busy-timeout=5000

# Optional: Cache size (positive=pages, negative=KiB)
sqlite.cache-size=-2000
```

### application.yml
```yaml
sqlite:
  enabled: true
  file: ./data/myapp.db
  wal: true
  synchronous: NORMAL
  busy-timeout: 5000
  cache-size: -2000
```

## Schema and Data Initialization

The starter supports automatic initialization of database schema and data from SQL files.

### Configuration

```properties
# Schema initialization
sqlite.init.schema-locations=classpath:schema.sql,classpath:schema-extensions.sql

# Data initialization
sqlite.init.data-locations=classpath:data.sql,classpath:data-seed.sql

# Initialization mode (default: EMBEDDED)
# ALWAYS - always run initialization
# NEVER - never run initialization
# EMBEDDED - run for embedded databases (recommended for SQLite)
sqlite.init.mode=EMBEDDED

# Continue on error (default: false)
sqlite.init.continue-on-error=false
```

### Example Schema File (schema.sql)

```sql
-- Create tables
CREATE TABLE IF NOT EXISTS users (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    username TEXT NOT NULL UNIQUE,
    email TEXT NOT NULL UNIQUE,
    full_name TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS products (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL,
    price REAL NOT NULL,
    stock_quantity INTEGER DEFAULT 0
);

-- Create indexes
CREATE INDEX IF NOT EXISTS idx_users_username ON users(username);
CREATE INDEX IF NOT EXISTS idx_products_name ON products(name);
```

### Example Data File (data.sql)

```sql
-- Insert initial data
INSERT INTO users (username, email, full_name) VALUES 
    ('admin', 'admin@example.com', 'Administrator'),
    ('user', 'user@example.com', 'Regular User');

INSERT INTO products (name, price, stock_quantity) VALUES 
    ('Product A', 29.99, 100),
    ('Product B', 49.99, 50);
```

### Resource Location Formats

The starter supports both classpath and file system resources:

- **Classpath**: `classpath:schema.sql`, `classpath:sql/schema.sql`
- **File System**: `file:/path/to/schema.sql`, `file:./data/schema.sql`
- **Multiple Files**: Comma-separated list of locations

## Usage

### Injecting the JdbcTemplate

```java
@Service
public class UserService {
    
    private final JdbcTemplate jdbcTemplate;
    
    public UserService(@Qualifier("sqliteJdbcTemplate") JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    
    public List<User> findAll() {
        return jdbcTemplate.query(
            "SELECT * FROM users",
            (rs, rowNum) -> new User(
                rs.getLong("id"),
                rs.getString("username"),
                rs.getString("email")
            )
        );
    }
    
    public void createUser(User user) {
        jdbcTemplate.update(
            "INSERT INTO users (username, email, full_name) VALUES (?, ?, ?)",
            user.getUsername(),
            user.getEmail(),
            user.getFullName()
        );
    }
}
```

### Injecting the DataSource

```java
@Configuration
public class MyConfiguration {
    
    public MyConfiguration(@Qualifier("sqliteDataSource") DataSource dataSource) {
        // Use the SQLite DataSource directly if needed
    }
}
```

## Configuration Properties Reference

| Property | Type | Default | Description |
|----------|------|---------|-------------|
| `sqlite.enabled` | boolean | false | Enable SQLite support |
| `sqlite.file` | Path | - | Path to the SQLite database file (required) |
| `sqlite.wal` | boolean | true | Enable Write-Ahead Logging mode |
| `sqlite.synchronous` | String | NORMAL | Synchronous mode (OFF, NORMAL, FULL, EXTRA) |
| `sqlite.busy-timeout` | Integer | - | Timeout in ms when database is locked |
| `sqlite.cache-size` | Integer | - | Database cache size (pages or KiB) |
| `sqlite.init.schema-locations` | List<String> | [] | Schema SQL file locations |
| `sqlite.init.data-locations` | List<String> | [] | Data SQL file locations |
| `sqlite.init.mode` | String | EMBEDDED | Initialization mode (ALWAYS, NEVER, EMBEDDED) |
| `sqlite.init.continue-on-error` | boolean | false | Continue on initialization errors |

## Best Practices

### 1. Use WAL Mode
Write-Ahead Logging (WAL) provides better concurrency:
```properties
sqlite.wal=true
```

### 2. Set Appropriate Busy Timeout
Prevent "database is locked" errors:
```properties
sqlite.busy-timeout=5000
```

### 3. Use IF NOT EXISTS in Schema Files
Make schema files idempotent:
```sql
CREATE TABLE IF NOT EXISTS users (...);
```

### 4. Separate Schema and Data Files
Keep schema and data separate for better organization:
```properties
sqlite.init.schema-locations=classpath:schema.sql
sqlite.init.data-locations=classpath:data.sql
```

### 5. Use EMBEDDED Mode for SQLite
This ensures initialization runs appropriately for embedded databases:
```properties
sqlite.init.mode=EMBEDDED
```

## Examples

### Development with In-Memory Database

```yaml
sqlite:
  enabled: true
  file: ":memory:"
  init:
    schema-locations: classpath:schema.sql
    data-locations: classpath:test-data.sql
    mode: ALWAYS
```

### Production with File-Based Database

```yaml
sqlite:
  enabled: true
  file: /var/data/production.db
  wal: true
  synchronous: NORMAL
  busy-timeout: 10000
  cache-size: -4000
  init:
    schema-locations: classpath:schema.sql
    mode: EMBEDDED
```

## License

This is a demonstration project.
