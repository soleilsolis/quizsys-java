package process;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class Menu {
    private final int id;
    private static Scanner input = new Scanner(System.in);
    private static String message = null;
    public Menu(int id){
        this.id = id;
    }
    public String myQuizzes() throws SQLException {
        message = "My Quizzes\n";

        Database database = new Database();
        ResultSet quizzes = database.getResultSet(
                String.format("select `quizzes`.*, (select count(*) from `questions`) as 'questions_count' ,(select count(*) from `responses` where `quizzes`.`id` = `responses`.`quiz_id` and `correct` = 1) as `responses_count`, `users`.name from `quizzes`, `users` WHERE `quizzes`.user_id = `users`.id and `users`.id = %d order BY `responses_count` desc", this.id)
        );

        while(quizzes.next()){
            message += String.format("\n%s - %d/%d", quizzes.getString("name"), quizzes.getInt("responses_count"), quizzes.getInt("questions_count"));
        }

        return message+"\n\n";
    }
    public static String leaderboard() throws SQLException {
        message = "Leaderboard\n";

        Database database = new Database();
        ResultSet quizzes = database.getResultSet("select `quizzes`.*, (select count(*) from `questions`) as 'questions_count' ,(select count(*) from `responses` where `quizzes`.`id` = `responses`.`quiz_id` and `correct` = 1) as `responses_count`, `users`.name from `quizzes`, `users` WHERE `quizzes`.user_id = `users`.id order BY `responses_count` desc");

        while(quizzes.next()){
            message += String.format("\n%s - %d/%d", quizzes.getString("name"), quizzes.getInt("responses_count"), quizzes.getInt("questions_count"));
        }

        return message+"\n\n";
    }
    public String takeQuiz() throws SQLException {
        ResultSet insertQuiz = Database.getResultSet("SELECT * FROM quizzes");
        int correctAnswers = 0;

        insertQuiz.moveToInsertRow();
        insertQuiz.updateInt("user_id", this.id);
        insertQuiz.insertRow();
        insertQuiz.moveToCurrentRow();
        insertQuiz.last();

        ResultSet questions = Database.getResultSet("SELECT * FROM questions");
        ResultSet responses = Database.getResultSet(
                String.format("SELECT * FROM responses WHERE quiz_id = %d", insertQuiz.getInt("id"))
        );

        System.out.println(insertQuiz.getInt("id"));

        while (questions.next()){
            System.out.println(questions.getString("value"));

            ResultSet answers = Database.getResultSet(String.format("SELECT * FROM answers WHERE question_id = %d ORDER BY sequence ASC", questions.getInt("id")));

            while(answers.next()){
                System.out.println(
                    String.format("%d: %s",answers.getRow(), answers.getString("value"))
                );
            }

            String myAnswerString = input.nextLine();
            int myAnswer = 0;

            try{
                myAnswer = Integer.parseInt(myAnswerString);
                if(myAnswer > 0 && myAnswer < 5){
                    answers.absolute(myAnswer);

                    responses.moveToInsertRow();
                    responses.updateInt("quiz_id", insertQuiz.getInt("id"));
                    responses.updateInt("answer_id", answers.getInt("id"));

                    if(answers.getInt("correct") == 0){
                        message = "Awwww. Incorrect Answer.";
                    }else {
                        message =  "Correct!";
                        correctAnswers++;
                        responses.updateInt("correct",1);
                    }

                    responses.insertRow();
                    responses.moveToCurrentRow();
                }

            }catch (Exception e){
                message =  "Invalid Answer!";
                questions.previous();
            }

            System.out.println(message);
        }

        questions.last();

        return String.format("Score: %d/%d", correctAnswers, questions.getRow());
    }
}
