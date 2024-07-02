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


	                        
	            int rowsInserted[] = stmt.executeBatch();
	            System.out.println(rowsInserted.length + " row(s) inserted.");

	            System.out.println("");
	            System.out.println("DataBase Records");
	            System.out.println("");

	            s = "select username , email from muhammad";
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

	                    System.out.print(UserName + " ");
	                    System.out.println(Email);

	                    page.fill("#userName", UserName);
	                    page.fill("#password", Email);
	                    Thread.sleep(2000);

	                    page.click("#login");
	                    Thread.sleep(4000);

	                    boolean loginSuccess = false;

	                    try {
	                        // Check if login was successful
	                        Locator usernameElement = page.locator("#userName-value");
	                        if (usernameElement.isVisible() && usernameElement.innerText().equals(UserName)) {
	                            System.out.println("Passed: Username matched");
	                            System.out.println("Logged in as: " + usernameElement.innerText());
	                            loginSuccess = true;
	                        }
	                    } catch (Exception e) {
	                        System.out.println("Failed: Invalid username or password");
	                        System.out.println("Failed: username or password does not matched");
	                    }

	                    if (!loginSuccess) {
	                        // Handle unsuccessful login here
	                        page.click("#newUser");
	                        page.fill("#firstname", UserName);
	                        Thread.sleep(2000);

	                        page.fill("#lastname", UserName);
	                        Thread.sleep(2000);
	                        page.fill("#userName", UserName);
	                        Thread.sleep(2000);
	                        page.fill("#password", Email);
	                        Thread.sleep(3000);

	                        // Handle CAPTCHA manually
	                        System.out.println("Please solve the CAPTCHA manually and press Enter to continue...");
	                        Scanner scanner = new Scanner(System.in);
	                        scanner.nextLine();  // Wait for the user to solve CAPTCHA and press Enter

	                        page.click("#register");
	                        System.out.println("User Register Successfully."+ "\n");
	                        page.click("#gotologin");
	                        Thread.sleep(2000);
	                        continue;
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
