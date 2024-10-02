import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;
import org.mindrot.jbcrypt.BCrypt;

public class User {
    private Connection connection;
    private Scanner scanner;

    public User(Connection connection,Scanner scanner){
        this.connection=connection;
        this.scanner=scanner;
    }

    public void register_user(){
        scanner.nextLine();

        String name = null;
        String email = null;
        String password = null;
        while (true) {
            System.out.println("Enter name: ");
            name = scanner.nextLine();
            if (name == null || name.isEmpty()) {
                System.out.println("Please enter a valid name.");
            } else {
                break;
            }
        }
        while (true) {
            System.out.println("Enter email: ");
            email = scanner.nextLine();
            if (email == null || email.isEmpty()) {
                System.out.println("Please enter a valid email.");
            } else if (user_exists(email)) {
                System.out.println("User already exists for this email!!");
            } else {
                break;
            }
        }

        while (true) {
            System.out.println("Enter password: ");
            password = scanner.nextLine();
            if (password == null || password.isEmpty()) {
                System.out.println("Please enter a valid password.");
            } else {
                break;
            }
        }
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());


        String query = "INSERT INTO users (full_name,email,password) VALUES (?,?,?)";
        try {

            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1,name);
            preparedStatement.setString(2,email);
            preparedStatement.setString(3,hashedPassword);
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

    //String is used so that we can return email through the program.
    public String login_user(){
        scanner.nextLine();
        System.out.println("Enter your email : ");
        String email = scanner.nextLine();
        System.out.println("Enter password : ");
        String password = scanner.nextLine();

        String query = "select * from users where email = ? ";

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1,email);
            ResultSet resultSet= preparedStatement.executeQuery();

            if (resultSet.next()){
                String hashedPassword = resultSet.getString("password");
                if (BCrypt.checkpw(password,hashedPassword)){
                    System.out.println("login success");
                    return email;
                }


            }
            else {
                System.out.println("please enter correct email or password");
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
