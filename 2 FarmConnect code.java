package farmConnect;

import java.sql.*;
import java.util.*;


// -------- Database Connection --------


class DBConnection 
{
    private static final String URL = "jdbc:mysql://localhost:3306/farmconnect";
    private static final String USER = "root";    
    private static final String PASSWORD = "23@akankshaK"; 

    public static Connection getConnection() throws SQLException 
    {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}


// -------- Farmer Class --------


class Farmer {
    private int farmerId;
    private String name;

    public Farmer(String name) {
        this.name = name;
        this.farmerId = registerFarmer(name);
    }

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
    public int getFarmerId() {
        return farmerId;
    }
    

    // Register farmer or return existing ID
    
    
    private int registerFarmer(String name)
    {
        try (Connection conn = DBConnection.getConnection())
        {
            String checkSql = "SELECT farmer_id FROM farmers WHERE name = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkSql);
            checkStmt.setString(1, name);
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next()) 
            {
                return rs.getInt("farmer_id");
            }

            String insertSql = "INSERT INTO farmers (name) VALUES (?)";
            PreparedStatement insertStmt = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS);
            insertStmt.setString(1, name);
            insertStmt.executeUpdate();
            ResultSet keys = insertStmt.getGeneratedKeys();
            if (keys.next())
            {
                return keys.getInt(1);
            }
        } 
        catch (SQLException e) 
        {
            e.printStackTrace();
        }
        return -1;
    }
    

    // Add Product
    
    
    public void addProduct(String name, String description, double price, int quantity) {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "INSERT INTO products (name, description, price, quantity, farmer_id) VALUES (?,?,?,?,?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, name);
            stmt.setString(2, description);
            stmt.setDouble(3, price);
            stmt.setInt(4, quantity);
            stmt.setInt(5, this.farmerId);
            stmt.executeUpdate();
            System.out.println("✅ Product added successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    

    // View Orders
    
    
    public void viewOrders() {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT o.order_id, p.name, o.quantity, o.total_price, o.status " +
                         "FROM orders o JOIN products p ON o.product_id = p.product_id " +
                         "WHERE p.farmer_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, this.farmerId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                System.out.println("OrderID: " + rs.getInt("order_id") +
                                   " | Product: " + rs.getString("name") +
                                   " | Qty: " + rs.getInt("quantity") +
                                   " | Total: " + rs.getDouble("total_price") +
                                   " | Status: " + rs.getString("status"));
            }
        }
        catch (SQLException e) 
        {
            e.printStackTrace();
        }
    }
}


// -------- Buyer Class --------


class Buyer {
    private int buyerId;
    private String name;

    public Buyer(String name) {
        this.setName(name);
        this.buyerId = registerBuyer(name);
    }

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	
    // Register buyer
	
	
    private int registerBuyer(String name)
    {
        try (Connection conn = DBConnection.getConnection()) 
        {
            String checkSql = "SELECT buyer_id FROM buyers WHERE name = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkSql);
            checkStmt.setString(1, name);
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next()) 
            {
                return rs.getInt("buyer_id");
            }

            String insertSql = "INSERT INTO buyers (name) VALUES (?)";
            PreparedStatement insertStmt = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS);
            insertStmt.setString(1, name);
            insertStmt.executeUpdate();
            ResultSet keys = insertStmt.getGeneratedKeys();
            if (keys.next()) 
            {
                return keys.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    // View Products
    
    public void viewProducts() {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT product_id, name, price, quantity, description FROM products";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                System.out.println("ID: " + rs.getInt("product_id") +
                                   " | " + rs.getString("name") +
                                   " | Price: " + rs.getDouble("price") +
                                   " | Qty: " + rs.getInt("quantity") +
                                   " | Desc: " + rs.getString("description"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    
    // Place Order
    
    
    public void placeOrder(int productId, int qty)
    {
        try (Connection conn = DBConnection.getConnection()) 
        {
            String checkSql = "SELECT price, quantity FROM products WHERE product_id = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkSql);
            checkStmt.setInt(1, productId);
            ResultSet rs = checkStmt.executeQuery();
            
            if (rs.next()) 
            {
                int stock = rs.getInt("quantity");
                double price = rs.getDouble("price");
                if (qty <= 0) 
                {
                    System.out.println("❌ Invalid quantity!");
                    return;
                }
                if (stock < qty) 
                {
                    System.out.println("❌ Out of stock!");
                    return;
                }
                double total = price * qty;

                String orderSql = "INSERT INTO orders (product_id, buyer_id, quantity, total_price) VALUES (?,?,?,?)";
                PreparedStatement orderStmt = conn.prepareStatement(orderSql);
                orderStmt.setInt(1, productId);
                orderStmt.setInt(2, this.buyerId);
                orderStmt.setInt(3, qty);
                orderStmt.setDouble(4, total);
                orderStmt.executeUpdate();

                String updateSql = "UPDATE products SET quantity = quantity - ? WHERE product_id = ?";
                PreparedStatement updateStmt = conn.prepareStatement(updateSql);
                updateStmt.setInt(1, qty);
                updateStmt.setInt(2, productId);
                updateStmt.executeUpdate();

                System.out.println("✅ Order placed successfully! Total = " + total);
            } else 
            {
                System.out.println("❌ Product not found!");
            }
        } 
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }
    

    // View Order History

    
    public void viewOrderHistory() {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT o.order_id, p.name, o.quantity, o.total_price, o.status " +
                         "FROM orders o JOIN products p ON o.product_id = p.product_id " +
                         "WHERE o.buyer_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, this.buyerId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                System.out.println("OrderID: " + rs.getInt("order_id") +
                                   " | Product: " + rs.getString("name") +
                                   " | Qty: " + rs.getInt("quantity") +
                                   " | Total: " + rs.getDouble("total_price") +
                                   " | Status: " + rs.getString("status"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}


// -------- Main Program --------


public class FarmConnect 
{
    public static void main(String[] args) 
    {
        Scanner sc = new Scanner(System.in);
        while (true) 
        {
            System.out.println("\n--- FarmConnect ---");
            System.out.println("1. Farmer");
            System.out.println("2. Buyer");
            System.out.println("3. Exit");
            System.out.print("Choose role: ");
            int choice = sc.nextInt();
            sc.nextLine();

            if (choice == 1) 
            {
                System.out.print("Enter Farmer Name: ");
                String name = sc.nextLine();
                Farmer farmer = new Farmer(name);

                System.out.println("1. Add Product\n2. View Orders");
                int fChoice = sc.nextInt();
                sc.nextLine();
                if (fChoice == 1) {
                    System.out.print("Product Name: ");
                    String pname = sc.nextLine();
                    System.out.print("Description: ");
                    String desc = sc.nextLine();
                    System.out.print("Price: ");
                    double price = sc.nextDouble();
                    System.out.print("Quantity: ");
                    int qty = sc.nextInt();
                    farmer.addProduct(pname, desc, price, qty);
                } else if (fChoice == 2) {
                    farmer.viewOrders();
                }

            } else if (choice == 2) {
                System.out.print("Enter Buyer Name: ");
                String name = sc.nextLine();
                Buyer buyer = new Buyer(name);

                System.out.println("1. View Products\n2. Place Order\n3. View Order History");
                int bChoice = sc.nextInt();
                sc.nextLine();
                if (bChoice == 1) {
                    buyer.viewProducts();
                } else if (bChoice == 2) {
                    buyer.viewProducts();
                    System.out.print("Enter Product ID: ");
                    int pid = sc.nextInt();
                    System.out.print("Enter Quantity: ");
                    int qty = sc.nextInt();
                    buyer.placeOrder(pid, qty);
                } else if (bChoice == 3) {
                    buyer.viewOrderHistory();
                }

            } else {
                System.out.println(" >>>>>>> Exiting...Thank you for using farmConnect ! See you soon :)");
                break;
            }
        }
        sc.close();
    }
}
