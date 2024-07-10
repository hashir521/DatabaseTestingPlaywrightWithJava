package DataInsert;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;

public class InsertClass extends TestClass {
    public void dataBase() throws InterruptedException, IOException {
        String JDBC_URL = "jdbc:mysql://localhost:3306/test";
        String USERNAME = "root";
        String PASSWORD = "";
        String s;
        ResultSet rs;

        try (Connection con = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD)) {
            System.out.println("Connected to the MySQL database.");

            Statement stmt = con.createStatement();
            
            String dropTable = "DROP TABLE IF EXISTS muhammad";
            stmt.executeUpdate(dropTable);
            System.out.println("Previous table dropped"+ "\n");


            String createTableQuery = "CREATE TABLE IF NOT EXISTS muhammad (" + "id INT AUTO_INCREMENT PRIMARY KEY,"
                    + "username VARCHAR(50) NOT NULL," + "email VARCHAR(100) NOT NULL UNIQUE)";
            stmt.executeUpdate(createTableQuery);

            // Batch insert statements
            stmt.addBatch("INSERT IGNORE INTO muhammad (username, email) VALUES ('Arqum', 'Arqum1222@gmail.com')");
            stmt.addBatch("INSERT IGNORE INTO muhammad (username, email) VALUES ('ssss', 'ssss9@hotmail.com')");
            stmt.addBatch("INSERT IGNORE INTO muhammad (username, email) VALUES ('Uzair', 'Uzair@2333@yahoo.com')");
            stmt.addBatch("INSERT IGNORE INTO muhammad (username, email) VALUES ('Muhammad', 'Muhammad10@yahoo.com')");
            stmt.addBatch("INSERT IGNORE INTO muhammad (username, email) VALUES ('Hasan', 'Hasan999@hotmail.com')");
            stmt.addBatch("INSERT IGNORE INTO muhammad (username, email) VALUES ('Haqq', 'Haqq999@hotmail.com')");

            int[] rowsInserted = stmt.executeBatch();
            System.out.println(rowsInserted.length + " row(s) inserted.");

            System.out.println("");
            System.out.println("DataBase Records");
            System.out.println("");

            s = "SELECT username, email FROM muhammad";
            rs = stmt.executeQuery(s);

            try (Playwright playwright = Playwright.create()) {
                Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));
                Page page = browser.newPage();

                // Set a higher timeout for navigation
                page.setDefaultNavigationTimeout(60000); // 60 seconds

                page.navigate("https://demoqa.com/login");

                // Wait for a specific element to be loaded
                page.waitForSelector("#userName");

                while (rs.next()) {
                    String UserName = rs.getString("username");
                    String Email = rs.getString("email");

                    System.out.println(UserName + " " + Email);

                    page.fill("#userName", UserName);
                    page.fill("#password", Email);
                    Thread.sleep(2000);

                    page.click("#login");
                    Thread.sleep(4000);

                    // Check if login was successful
                    boolean loginSuccess = false;

                    try {
                        // Wait for the username element to be visible
                        page.waitForSelector("#userName-value");

                        // Verify if username displayed matches the database value
                        String userNameUI = page.innerText("#userName-value");
                        if (UserName.equals(userNameUI)) {
                            System.out.println("Passed: Username matched with DataBase");
                            System.out.println("Logged in as: " + userNameUI);
                            loginSuccess = true;
                        }
                    } catch (Exception e) {
                        System.out.println("Failed: Invalid username or password");
                        System.out.println("Attempting to register a new user...");

                        // Handle registration
                        page.click("#newUser");
                        page.fill("#firstname", UserName);
                        Thread.sleep(1500);

                        page.fill("#lastname", UserName);
                        Thread.sleep(1500);
                        page.fill("#userName", UserName);
                        Thread.sleep(1500);
                        page.fill("#password", Email);
                        Thread.sleep(3000);

                        // Handle CAPTCHA manually
                        System.out.println("Please solve the CAPTCHA manually and press Enter to continue...");
                        Scanner scanner = new Scanner(System.in);
                        scanner.nextLine(); // Wait for the user to solve CAPTCHA and press Enter

                        page.click("#register");
                        System.out.println("User Registered Successfully." + "\n");
                        page.click("#gotologin");
                        Thread.sleep(2000);
                        continue; // Move to the next record in the database
                    }

                    // Perform logout
                    page.click("//button[contains(text(),'Log out')]");

                    // Wait for the login form to be visible after logout
                    page.waitForSelector("#login");
                    System.out.println(UserName + " Logged out successfully" + "\n");
                }

                browser.close();
            }
            con.close();
        } catch (SQLException e) {
            System.err.println("Failed to connect to the database or execute query.");
            e.printStackTrace();
        }
    }
}
