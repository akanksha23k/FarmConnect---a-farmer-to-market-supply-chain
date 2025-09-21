CREATE DATABASE farmconnect;
USE farmconnect;

-- Farmers Table

CREATE TABLE Farmers (
    farmer_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    contact VARCHAR(50),
    location VARCHAR(100)
);
-- Buyers Table

CREATE TABLE Buyers (
    buyer_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    contact VARCHAR(50),
    location VARCHAR(100)
);
-- Products Table
CREATE TABLE Products (
    product_id INT AUTO_INCREMENT PRIMARY KEY,
    product_name VARCHAR(100) NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    quantity INT NOT NULL,
    description VARCHAR(200),
    farmer_id INT,
    FOREIGN KEY (farmer_id) REFERENCES Farmers(farmer_id)
);
-- Orders Table

CREATE TABLE Orders (
    order_id INT AUTO_INCREMENT PRIMARY KEY,
    buyer_id INT,
    product_id INT,
    quantity INT NOT NULL,
    total_price DECIMAL(10,2),
    status VARCHAR(50) DEFAULT 'Pending',
    FOREIGN KEY (buyer_id) REFERENCES Buyers(buyer_id),
    FOREIGN KEY (product_id) REFERENCES Products(product_id)
);
CREATE TABLE payments (
    payment_id INT AUTO_INCREMENT PRIMARY KEY,
    order_id INT,
    buyer_id INT,
    amount DOUBLE,
    payment_method VARCHAR(50),
    FOREIGN KEY (order_id) REFERENCES orders(order_id),
    FOREIGN KEY (buyer_id) REFERENCES buyers(buyer_id)
);
ALTER TABLE payments ADD COLUMN status VARCHAR(20) DEFAULT 'Pending';

-- Farmers

INSERT INTO Farmers (name, contact, location) VALUES
('Akanksha Kapadnis', '9876543210', 'Nashik');

-- Buyers

INSERT INTO Buyers (name, contact, location) VALUES
('Village Shopkeeper', '9123456780', 'Nashik'),
('Wholesaler', '9988776655', 'Nashik City');

-- Products 

INSERT INTO Products (product_name, price, quantity, description, farmer_id) VALUES
('Onions', 30.00, 200, 'Fresh red onions from farm', 1),
('Tomatoes', 20.00, 100, 'Organic tomatoes harvested today', 1),
('Sugarcane', 300.00, 10, 'High quality sugarcane (per ton)', 1),
('Sweet Corn', 25.00, 50, 'Fresh sweet corn', 1),
('Pulses (Toor Dal)', 80.00, 150, 'Organic pulses grown locally', 1),
('Millets (Jowar)', 60.00, 120, 'Healthy millets direct from farm', 1);


