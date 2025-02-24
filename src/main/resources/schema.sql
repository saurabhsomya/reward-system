DROP TABLE IF EXISTS transactions;

CREATE TABLE transactions (
    transaction_id INT PRIMARY KEY AUTO_INCREMENT, 
    customer_id INTEGER,
    customer_name VARCHAR(255),                          
    amount DECIMAL(10,2),                         
    transaction_date DATE
);