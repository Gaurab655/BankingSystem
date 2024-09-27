import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class AccountManager {
    private Connection connection;
    private Scanner scanner;
    public AccountManager(Connection connection, Scanner scanner){
        this.connection=connection;
        this.scanner=scanner;
    }
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
            if (account_number!=0){
                PreparedStatement preparedStatement= connection.prepareStatement(query);
                preparedStatement.setString(1,pin);
                ResultSet resultSet= preparedStatement.executeQuery();
                if (resultSet.next()){
                    double current_balance =resultSet.getDouble("balance");
                    if (amount<=current_balance){
                        PreparedStatement preparedStatement1= connection.prepareStatement("Update accounts set balance = balance-? where account_number=?");
                        preparedStatement1.setDouble(1,amount);
                        preparedStatement1.setLong(2,account_number);
                        int affectedRows = preparedStatement1.executeUpdate();
                        if (affectedRows > 0){
                            System.out.println(amount+ " amount debited successful");
                            connection.commit();
                            connection.setAutoCommit(true);
                        }else {
                            System.out.println("failed!!");
                            connection.rollback();
                            connection.setAutoCommit(true);

                        }
                    }else {
                        System.out.println("insufficient balance");
                    }
                }else {
                    System.out.println("invalid pin ");
                }
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        connection.setAutoCommit(true);
    }
    public void credit_amount(long account_number )throws SQLException{
        scanner.nextLine();
        System.out.println("Enter amount : ");
        double amount = scanner.nextDouble();
        System.out.println("Enter pin ");
        String pin = scanner.nextLine();
try{
    if (account_number!=0){
        connection.setAutoCommit(false);
        String query = "select * from accounts where security_pin =?";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setString(1,pin);
        ResultSet resultSet = preparedStatement.executeQuery();
        if (resultSet.next()){
            String query1="update  accounts set balance=balance+? where account_number=? ";
            PreparedStatement preparedStatement1 = connection.prepareStatement(query1);
            preparedStatement1.setDouble(1,amount);
            preparedStatement1.setLong(2,account_number);
            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows >0){
                System.out.println(amount+"amount added");
                connection.commit();
                connection.setAutoCommit(true);
            }else {
                System.out.println("failed");
                connection.rollback();
                connection.setAutoCommit(true);
            }

        }
    }

}catch (SQLException e){
    e.printStackTrace();
}
    }
    public void transferMoney(long account_number){
        scanner.nextLine();
        System.out.println("Enter account number : ");
        long accountNumber = scanner.nextLong();
        System.out.println("Enter amount : ");
        double amount = scanner.nextDouble();
        System.out.println("Enter security pin");
        String pin = scanner.nextLine();

        String query = "select * from accounts where account_number = ? and security_pin = ?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setDouble(1,accountNumber);
            preparedStatement.setString(2,pin);
            ResultSet resultSet= preparedStatement.executeQuery();
            if (resultSet.next()){
                double balance = resultSet.getDouble("balance");
                if (balance>=amount){
                String query1 = "update accounts set balance =balance+? where account_number=?";
                try {
                    PreparedStatement preparedStatement1 = connection.prepareStatement(query1);
                    preparedStatement1.setDouble(1,amount);
                    preparedStatement1.setLong(2,accountNumber);
                    int affectedRows = preparedStatement1.executeUpdate();
                    if (affectedRows>0){
                        System.out.println("amount transferred succcessfull!! " +amount);
                        String query2 = "update accounts set balance=balance-?";

                    }
                }catch (SQLException e){
                    e.printStackTrace();
                }} else {
                    System.out.println("please enter correct amount!!");
                }


            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }



}
