-- Example data file for SQLite database initialization
-- This file demonstrates initial data loading for the SQLite starter

-- Insert sample users
INSERT INTO users (username, email, full_name) VALUES 
    ('john_doe', 'john.doe@example.com', 'John Doe'),
    ('jane_smith', 'jane.smith@example.com', 'Jane Smith'),
    ('bob_wilson', 'bob.wilson@example.com', 'Bob Wilson');

-- Insert sample products
INSERT INTO products (name, description, price, stock_quantity) VALUES 
    ('Laptop', 'High-performance laptop for development', 1299.99, 10),
    ('Wireless Mouse', 'Ergonomic wireless mouse', 29.99, 50),
    ('Mechanical Keyboard', 'RGB mechanical keyboard', 149.99, 25),
    ('USB-C Hub', 'Multi-port USB-C hub', 49.99, 30),
    ('Monitor', '27-inch 4K monitor', 399.99, 15);

-- Insert sample orders
INSERT INTO orders (user_id, total_amount, status) VALUES 
    (1, 1299.99, 'completed'),
    (2, 179.98, 'pending'),
    (3, 79.98, 'completed');
