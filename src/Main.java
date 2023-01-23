import java.sql.*;
import java.util.Scanner;
import process.Database;
import process.Menu;
public class Main {
    private static Scanner input = new Scanner(System.in);
    private static String username = null;
    private static String password = null;
    private static boolean loggedIn = false;
    private static boolean insideMenu = true;
    private static ResultSet user = null;
    private static int choice = 0;

    public static void main(String[] args) throws SQLException {
        System.out.println("Welcome to Quizsys\n");

        while (! loggedIn){
            System.out.println("Username: ");
            username = input.nextLine();
            System.out.println("Password: ");
            password = input.nextLine();

            Database database = new Database();

            user = database.getResultSet(String.format("SELECT id, name, password FROM users WHERE name = '%s'", username));

            if (user.absolute(1)) {
                loggedIn = true;
            }else {
                System.out.println("\nAccount does not exist!\n");
            }
        }

        String message = null;

        while(insideMenu){
            Menu menu = new Menu(user.getInt("id"));

            System.out.println(
                    "Quizsys Menu\n1. Take the quiz\n2. My Results\n3. Leaderboard\n4. Exit\n"
            );

            String choiceString = input.nextLine();

            try{
                choice = Integer.parseInt(choiceString);

                switch (choice) {
                    case 1:
                        message = menu.takeQuiz();
                        break;
                    case 2:
                        message = menu.myQuizzes();
                        break;
                    case 3:
                        message = menu.leaderboard();
                        break;
                    case 4:
                        message = "Thank you so much!";
                        insideMenu = false;
                        break;
                }

                System.out.println("\n\n\n"+message);
            }catch (Exception e){
                System.out.println("");
            }
        }
    }
}
