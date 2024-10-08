import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class Accounts {
    private Connection connection;
    private Scanner scanner;

    public Accounts(Connection connection, Scanner scanner) {
        this.scanner = scanner;
        this.connection = connection;
    }

    public int open_Account(String email) {
        if (!account_exists(email)) {
            String query = "INSERT INTO accounts (full_name, email, balance, security_pin) VALUES (?, ?, ?, ?)";
            String fullName = App.getFullName();
            Double balance  = App.getBalance();
            String pin      = App.getPin();

            try {
                PreparedStatement preparedStatement = connection.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS);
                preparedStatement.setString(1,fullName );
                preparedStatement.setString(2, email);
                preparedStatement.setDouble(3, balance);
                preparedStatement.setString(4, pin);

                int affectedRows = preparedStatement.executeUpdate();

                if (affectedRows > 0) {
                    // getGeneratedKeys retrieve the auto-generated keys (such as primary key values) when inserting data into a database
                    ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
                    if (generatedKeys.next()) {
                        int accountNumber = generatedKeys.getInt(1);
                        return accountNumber;
                    } else {
                        System.out.println("Error: No account number was generated.");
                    }
                } else {
                    System.out.println("Error: Account creation failed.");
                }

            } catch (SQLException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
        throw new RuntimeException("Account already exists");
    }

    public int getAccountNumber(String email) {
        String query = "SELECT account_number FROM accounts WHERE email = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, email);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("account_number");
                } else {
                    throw new RuntimeException("Account doesn't exist");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Database error occurred", e);
        }
    }

    public boolean account_exists(String email) {
        String query = "select * from accounts where email=?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, email);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return true;
            } else {
                return false;
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return false;
    }


}
