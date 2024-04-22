package org.example;
import org.example.Students.Student;
import java.sql.*;

public class DateBase {
    private Connection connection;
    public DateBase() {
        try{
            String url = "jdbc:postgresql://localhost:5432/postgres";
            String user = "postgres";
            String password = "postgres";
            this.connection = DriverManager.getConnection(url,user,password) ;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public void insertTable(Student student){
        String insert = "INSERT INTO laba (x, y, time_born, time_life, gender) VALUES (?, ?, ?, ?, ?)";
        try {
            PreparedStatement statement = connection.prepareStatement(insert);
            statement.setInt(1, student.x);
            statement.setInt(2, student.y);
            statement.setInt(3, (int) student.timeToBorn);
            statement.setInt(4, (int) student.timeToDie);
            statement.setString(5, student.getGender());
            int rowsInserted = statement.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("Данные успешно добавлены в таблицу");
            } else {
                System.out.println("Не удалось добавить данные в таблицу");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public ResultSet selectTable(){
        String select = "SELECT * FROM laba";
        try{
            PreparedStatement preparedStatement = connection.prepareStatement(select);
            return preparedStatement.executeQuery();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public void clearTable(){
        String clear = "TRUNCATE TABLE laba";
        try{
            PreparedStatement statement = connection.prepareStatement(clear);
            int clearIns = statement.executeUpdate();
            System.out.println("Данные успешно очищены.");
        } catch (SQLException e) {
            System.out.println("Не удалось очистить данные.");
        }
    }
    //    public void deleteRow(int x, int y ,int time_born, int time_life){
//        String delete = String.format("DELETE FROM laba where x='%d' AND y='%d' AND time_born='%d' AND time_life='%d'",x,y,time_born,time_life);
//        try{
//            PreparedStatement statement = connection.prepareStatement(delete);
//            int rowsDelete = statement.executeUpdate();
//            if(rowsDelete>0){
//                System.out.println("Данные успешно удалены из таблицы");
//            }else{
//                System.out.println("Не удалось удалить данные из таблицы");
//            }
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
//    }
}
