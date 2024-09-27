import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class User {
    private Connection connection;
    private Scanner scanner;

    public User(Connection connection,Scanner scanner){
        this.connection=connection;
        this.scanner=scanner;
    }

    public void register_user(){
        scanner.nextLine();
        System.out.println("Enter name : ");
        String name = scanner.nextLine();
        System.out.println("Enter email : ");
        String email = scanner.nextLine();
        System.out.println("Enter password");
        String password = scanner.nextLine();
        if (user_exists(email)){
            System.out.println("User already exists for this email!!");
        }

        String query = "INSERT INTO users (full_name,email,password) VALUES (?,?,?)";
        try {

            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1,name);
            preparedStatement.setString(2,email);
            preparedStatement.setString(3,password);
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected>0){
                System.out.println("Registered Successful!!");
            }else {
                System.out.println("Registration Failed!! ");
            }

        }catch (SQLException e){
            System.out.println(e.getMessage());
        }
    }
    public String login_user(){
        scanner.nextLine();
        System.out.println("Enter your email : ");
        String email = scanner.nextLine();
        System.out.println("Enter password : ");
        String password = scanner.nextLine();

        String query = "select * from users where email = ? and password = ?";

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1,email);
            preparedStatement.setString(2,password);
            ResultSet resultSet= preparedStatement.executeQuery();

            if (resultSet.next()){
                System.out.println("login success");
                return email;

            }
            else {
                System.out.println("please enter correct pass");
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return null;

    }
    public boolean user_exists(String email){
        String query = "SELECT * FROM users WHERE email =?";

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1,email);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()){
                return true;
            }
            else{
                return false;
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return false;
}
}
