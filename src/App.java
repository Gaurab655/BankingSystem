import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.InputMismatchException;
import java.util.Objects;
import java.util.Scanner;

public class App {
    private static final String url = "jdbc:postgresql://localhost:5432/bv2";
    private static final String username = "postgres";
    private static final String password = "password";
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("PostgreSQL JDBC Driver not found. " + e.getMessage());
            return;
        }

        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            User user = new User(connection, scanner);
            Accounts accounts = new Accounts(connection,scanner);
            AccountManager accountManager = new AccountManager(connection,scanner);

            if (!Objects.isNull(connection)) {
                System.out.println("Connection successful.");
                mainMenu(user,accounts,accountManager);

            } else {
                System.out.println("Error connecting to the database.");
            }
        } catch (SQLException e) {
            System.out.println("Database connection error: " + e.getMessage());
        }
    }
    public static void mainMenu(User user , Accounts accounts,AccountManager accountManager) throws SQLException {
        String email;
        int account_number;
        while (true) {
            System.out.println("Choose the following options:");
            System.out.println("1. Register");
            System.out.println("2. login");
            System.out.println("0. Exit");

            int choose;
            try {
                choose = scanner.nextInt();
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a number." +e.getMessage());
                scanner.nextLine();
                continue;
            }

            switch (choose) {
                case 1:
                    user.register_user();
                    break;
                case 2:
                    email =user.login_user();
                    if (email!=null){
                        System.out.println();
                        System.out.println("logged in");
                        if (!accounts.account_exists(email)){
                            System.out.println();
                            System.out.println("1. open account");
                            System.out.println("2. Get account number");
                            System.out.println("3. Exit");
                            int choice2 = scanner.nextInt();
                            if (choice2==1){
                                account_number=accounts.open_Account(email);
                                System.out.println("Account successfully created !!");
                                System.out.println("your account number is :" +account_number);
                            }
                            else if (choice2 == 2) {
                                try {
                                    int accountNumber = accounts.getAccountNumber(email);
                                    System.out.println("Account number: " + accountNumber);
                                    mainMenu(user , accounts,accountManager);
                                } catch (RuntimeException e) {
                                    System.out.println(e.getMessage());
                                    mainMenu(user , accounts,accountManager);


                                }
                            } else {
                                break;
                            }


                        }
                        account_number=accounts.getAccountNumber(email);
                        int choice2=0;
                        while(choice2!=5){
                            System.out.println();
                            System.out.println("1. Debit Money");
                            System.out.println("2. Credit Money");
                            System.out.println("3. Transfer Money");
                            System.out.println("4. Check Balance");
                            System.out.println("5. Log Out");
                            System.out.println("Enter your Choice: ");
                            choice2 = scanner.nextInt();

                            switch (choice2){
                                case 1:
                                    accountManager.debit_amount(account_number);
                                    break;
                                case 2:
                                    accountManager.credit_amount(account_number);
                                    break;
                                case 3:
                                    accountManager.transferMoney(account_number);
                                    break;
                                case 4:
                                    accountManager.checkBalance(account_number);
                                    break;
                                case 5:
                                    System.out.println("logging out.....!!");
                                    accountManager.logOut(user,accounts,accountManager);

                                    break;
                            }

                        }
                    }
                    break;
                case 0:
                    System.out.println("Exiting the application. Goodbye!");
                    return;
                default:
                    System.out.println("please select one");
            }
        }
    }
}
