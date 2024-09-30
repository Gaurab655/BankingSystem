import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class AccountManager {
    private Connection connection;
    private Scanner scanner;

    public AccountManager(Connection connection, Scanner scanner) {
        this.connection = connection;
        this.scanner = scanner;
    }
    App app = new App();

    public void debit_amount(long account_number) throws SQLException {
        System.out.println();
        System.out.println("Enter amount : ");
        long amount = scanner.nextLong();
        scanner.nextLine();
        System.out.println("Enter pin : ");
        String pin = scanner.nextLine();
        try {
            connection.setAutoCommit(false);
            String query = "Select * from accounts where security_pin=?";
            if (account_number != 0) {
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, pin);
                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    double current_balance = resultSet.getDouble("balance");
                    if (amount <= current_balance) {
                        PreparedStatement preparedStatement1 = connection.prepareStatement("Update accounts set balance = balance-? where account_number=?");
                        preparedStatement1.setDouble(1, amount);
                        preparedStatement1.setLong(2, account_number);
                        int affectedRows = preparedStatement1.executeUpdate();
                        if (affectedRows > 0) {
                            System.out.println(amount + " amount debited successful");
                            connection.commit();
                            connection.setAutoCommit(true);
                        } else {
                            System.out.println("failed!!");
                            connection.rollback();
                            connection.setAutoCommit(true);

                        }
                    } else {
                        System.out.println("insufficient balance");
                    }
                } else {
                    System.out.println("invalid pin ");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        connection.setAutoCommit(true);
    }

    public void credit_amount(long account_number) throws SQLException {
        scanner.nextLine();
        System.out.println("Enter amount : ");
        double amount = scanner.nextDouble();
        scanner.nextLine();
        System.out.println("Enter pin ");
        String pin = scanner.nextLine();
        try {
            if (account_number != 0) {
                connection.setAutoCommit(false);
                String query = "select * from accounts where account_number=? and security_pin =?";
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setLong(1, account_number);
                preparedStatement.setString(2, pin);
                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    String query1 = "update  accounts set balance=balance+? where account_number=? ";
                    PreparedStatement preparedStatement1 = connection.prepareStatement(query1);
                    preparedStatement1.setDouble(1, amount);
                    preparedStatement1.setLong(2, account_number);
                    int affectedRows = preparedStatement1.executeUpdate();
                    if (affectedRows > 0) {
                        System.out.println(amount + " amount added ");
                        connection.commit();
                        connection.setAutoCommit(true);
                        return;
                    } else {
                        System.out.println("failed");
                        connection.rollback();
                        connection.setAutoCommit(true);
                    }

                } else {
                    System.out.println("Invalid pin!!");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void transferMoney(long senders_account_number) throws SQLException {
        scanner.nextLine();
        System.out.println("Enter account number to transfer : ");
        long receiver_account_number = scanner.nextLong();
        System.out.println("Enter amount : ");
        double amount = scanner.nextDouble();
        scanner.nextLine();
        System.out.println("Enter security pin");
        String pin = scanner.nextLine();

        String query = "select * from accounts where account_number = ? and security_pin = ?";
        try {
            connection.setAutoCommit(false);
            if (senders_account_number != 0 && receiver_account_number != 0) {
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setDouble(1, senders_account_number);
                preparedStatement.setString(2, pin);
                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    double balance = resultSet.getDouble("balance");
                    if (balance >= amount) {
                        int serviceCharge = (receiver_account_number%2==0)?5:10;
                        double totalDebit =amount+serviceCharge;
                        String creditQuery = "update accounts set balance =balance+? where account_number=?";
                        String debitQuery = "update accounts set balance =balance-? where account_number=?";
                        try {
                            PreparedStatement creditPreparedStatement = connection.prepareStatement(creditQuery);
                            PreparedStatement debitPreparedStatement = connection.prepareStatement(debitQuery);
                            creditPreparedStatement.setDouble(1, amount);
                            creditPreparedStatement.setLong(2, receiver_account_number);
                            debitPreparedStatement.setDouble(1, totalDebit);
                            debitPreparedStatement.setLong(2, senders_account_number);

                            int rowsAffected1 = debitPreparedStatement.executeUpdate();
                            int rowsAffected2 = creditPreparedStatement.executeUpdate();

                            if (rowsAffected1 > 0 && rowsAffected2 > 0) {
                                System.out.println(amount + " amount transferred successful!! ");
                                System.out.println("service charge - "+serviceCharge);
                                connection.commit();
                                connection.setAutoCommit(true);
                                return;
                            } else {
                                System.out.println("failed to transfer ");
                                connection.rollback();
                                connection.setAutoCommit(true);
                            }

                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    } else {
                        System.out.println("Insufficient balance to transfer! ");
                    }


                } else {
                    System.out.println("invalid security pin ");
                }
            } else {
                System.out.println("Enter valid acc number ");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public void checkBalance(long account_number){
        System.out.println("Enter pin : ");
        String pin = scanner.next();
        String query = "select * from accounts where account_number= ? and security_pin=?";
       try {
           PreparedStatement preparedStatement = connection.prepareStatement(query);
           preparedStatement.setLong(1,account_number);
           preparedStatement.setString(2,pin);
           ResultSet resultSet = preparedStatement.executeQuery();
           if (resultSet.next()){
               double balance = resultSet.getDouble("balance");
               System.out.println("your total amount is : " +balance);
           }else {
               System.out.println("Error fetching balance!");
           }

       }catch (SQLException e){
           e.printStackTrace();
       }

    }
    public void logOut(User user, Accounts accounts, AccountManager accountManager) throws SQLException{
        System.out.println("logged out successfully!!");
        App.mainMenu(user,accounts,accountManager);

    }

}
