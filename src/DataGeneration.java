import java.sql.*;
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Random;

public class DataGeneration {

    private static Random randomGenerator = new Random();

    public static void insertData() throws ClassNotFoundException, SQLException {
        Connection conn = null;
        try
        {
            Class.forName("com.mysql.jdbc.Driver");

            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/unidb", "root", "4HisGlory!123");

            BufferedReader reader1 = new BufferedReader(new FileReader("studentIDList.txt"));
            ArrayList<String> listOfStudentID = new ArrayList<>();

            String line1 = reader1.readLine();
            while (line1 != null) {
                listOfStudentID.add(line1);
                line1 = reader1.readLine();
            }

            reader1.close();

            BufferedReader reader2 = new BufferedReader(new FileReader("classesList.txt"));
            ArrayList<String> listOfClasses = new ArrayList<>();

            String line2 = reader2.readLine();
            while (line2 != null) {
                listOfClasses.add(line2);
                line2 = reader2.readLine();
            }

            reader2.close();

            String[] letterGrade = {"A", "B", "C", "D", "F"};

            randomGenerator = new Random();

            int n = listOfStudentID.size();
            int m = listOfClasses.size();
            int o = 5;

            String sql = "INSERT INTO hastaken VALUES(?, ?, ?) ";

            PreparedStatement ps = conn.prepareStatement(sql);
            for (int i = 0; i < n; i++) {

                ps.setString(1, listOfStudentID.get(i));

                for(int j = 0; j < randomGenerator.nextInt(o); j++) {


                    int randomClass = randomGenerator.nextInt(m);
                    int randomLetter = randomGenerator.nextInt(letterGrade.length);

                    ps.setString(2, listOfClasses.get(randomClass));
                    ps.setString(3, letterGrade[randomLetter]);
                    ps.executeUpdate();

                }

            }

            System.out.println("Inserted " + n + " rows!");
        }
        catch (Exception e){
            e.printStackTrace();
        }
        finally
        {
            if (conn != null)
            {
                conn.close();
            }
        }
    }
    public static void main(String[] args) throws SQLException,
            ClassNotFoundException {
        insertData();
    }
}
