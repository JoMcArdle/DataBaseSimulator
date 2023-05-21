import java.sql.*;
import java.util.Scanner;

public class App {

    private String url;
    private String uname;
    private String pass;
    private Scanner sc = new Scanner(System.in);
    private Connection con;
    private Statement st;
    private Statement st2;
    private Statement st3;
    private Statement st4;
    private Statement st5;
    private String yearQuery;
    private double averageGPA = 0.0;

    /**
     * Parameterized constructor, takes in information to access a user's database from MySQL.
     * @param url
     * @param uname
     * @param pass
     */
    public App(String url, String uname, String pass) {

        this.url = url;
        this.uname = uname;
        this.pass = pass;

    }

    /**
     * Handles different queries based on user input using a switch case.
     * @param input
     * @throws Exception
     */
    private void operations(String input) throws Exception {

        switch(input) {
            case "1":
                nameQuery();
                break;

            case "2":
                studentYearQuery();
                break;

            case "3":
                GPAQuery1();
                break;

            case "4":
                GPAQuery2();
                break;

            case "5":
                departmentQuery();
                break;

            case "6":
                classQuery();
                break;

            case "7":
                arbitraryQuery();
                break;

            case "8":
                System.out.println("Goodbye.");
                break;

            default:
                System.out.println(input + " is an invalid command!");
                break;
        }
    }


    //------------------------------------------------------------------------------------------------------------------


    /**
     * Searches for students in the database whose first or last name matches the search string entered.
     * @throws Exception
     */
    public void nameQuery() throws Exception {

        System.out.println("Please enter the name.");

        String input = sc.nextLine();

        String query = "select * from students where first_name like '" + input + "%'" + " or last_name like '" + input + "%'";

        createStatements();

        ResultSet rs = st.executeQuery(query);

        printStudentInfo(rs);

        st.close();
        con.close();
    }


    //------------------------------------------------------------------------------------------------------------------


    /**
     * Searches students by year (Freshman, Sophomore, Junior, Senior), student's year depends on how many credits they
     * have completed.
     * @throws Exception
     */
    public void studentYearQuery() throws Exception {

        System.out.println("Please enter the year.");

        String input = sc.nextLine();

        createStatements();

        findYear(input);

        ResultSet rs = st.executeQuery(yearQuery);

        printStudentInfo(rs);

        st.close();
        con.close();

    }

    //------------------------------------------------------------------------------------------------------------------


    /**
     * Search for students with a GPA equal to or above a given threshold.
     * @throws Exception
     */
    public void GPAQuery1() throws Exception {

        System.out.println("Please enter the threshold.");

        String input = sc.nextLine();

        String query = "SELECT s.id, s.first_name, s.last_name, SUM(c.credits * (CASE h.grade " +
                "WHEN 'A' THEN 4 " +
                "WHEN 'B' THEN 3 " +
                "WHEN 'C' THEN 2 " +
                "WHEN 'D' THEN 1 " +
                "WHEN 'F' THEN 0 " +
                "END)) / SUM(c.credits) AS GRADE_POINT " +
                "FROM Students s, Classes c, HasTaken h " +
                "WHERE s.id = h.sid AND c.name = h.cname " +
                "GROUP BY s.id, s.first_name, s.last_name " +
                "HAVING GRADE_POINT >= " + input;

        createStatements();

        ResultSet rs = st.executeQuery(query);

        printStudentInfo(rs);

        st.close();
        con.close();
    }

    //------------------------------------------------------------------------------------------------------------------


    /**
     * Search for students with a GPA equal to or below a given threshold.
     * @throws Exception
     */
    public void GPAQuery2() throws Exception {

        System.out.println("Please enter the threshold.");

        String input = sc.nextLine();

        String query = "SELECT s.id, s.first_name, s.last_name, SUM(c.credits * (CASE h.grade " +
                "WHEN 'A' THEN 4 " +
                "WHEN 'B' THEN 3 " +
                "WHEN 'C' THEN 2 " +
                "WHEN 'D' THEN 1 " +
                "WHEN 'F' THEN 0 " +
                "END)) / SUM(c.credits) AS GRADE_POINT " +
                "FROM Students s, Classes c, HasTaken h " +
                "WHERE s.id = h.sid AND c.name = h.cname " +
                "GROUP BY s.id, s.first_name, s.last_name " +
                "HAVING GRADE_POINT <= " + input;

        createStatements();

        ResultSet rs = st.executeQuery(query);

        printStudentInfo(rs);

        st.close();
        con.close();

    }

    //------------------------------------------------------------------------------------------------------------------


    /**
     * Prints out the number of students in a given department and the average of those students' GPA.
     * @throws Exception
     */
    public void departmentQuery() throws Exception {

        System.out.println("Please enter the department.");

        String input = sc.nextLine();

        String query = "SELECT d.name, COUNT(DISTINCT s.id) " +
                "FROM Students s, Departments d, Majors ma, Minors mi " +
                "WHERE s.id = ma.sid AND s.id = mi.sid AND (ma.dname = d.name OR mi.dname = d.name) AND d.name = '" + input + "'" +
                " GROUP BY d.name";


        String query2 = "SELECT distinct s.id, SUM(c.credits * (CASE h.grade " +
                "WHEN 'A' THEN 4 " +
                "WHEN 'B' THEN 3 " +
                "WHEN 'C' THEN 2 " +
                "WHEN 'D' THEN 1 " +
                "WHEN 'F' THEN 0 " +
                "END)) / SUM(c.credits) AS GRADE_POINT " +
                "FROM Students s, Classes c, HasTaken h, Majors ma, Minors mi, Departments d " +
                "WHERE s.id = h.sid AND c.name = h.cname and s.id = ma.sid and s.id = mi.sid and (ma.dname = d.name OR mi.dname = d.name) " +
                "AND d.name = '" + input + "' " +
                "GROUP BY s.id";

        Class.forName("com.mysql.jdbc.Driver");
        Connection con = DriverManager.getConnection(url, uname, pass);
        Statement st = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        Statement st2 = con.createStatement();

        ResultSet rs = st.executeQuery(query);

        rs.next();
        int count = rs.getInt(2);
        System.out.println("Num students: " + count);

        ResultSet rs2 = st2.executeQuery(query2);

        while (rs2.next()) {

            averageGPA += rs2.getDouble(2);

        }

        System.out.println("Average GPA: " + Math.round(averageGPA * 10 / count) / 10.0);
        averageGPA = 0.0;


        st.close();
        con.close();
    }


    //------------------------------------------------------------------------------------------------------------------


    /**
     * Prints out the number of students currently taking a given class. Also shows the number of students who've gotten
     * each letter grade for that class.
     * @throws Exception
     */
    public void classQuery() throws Exception {

        System.out.println("Please enter the class name.");

        String input = sc.nextLine();

        String query = "SELECT c.name, COUNT(distinct s.id) " +
                "FROM Students s, Classes c, IsTaking t " +
                "WHERE s.id = t.sid AND c.name = t.cname AND c.name = '" + input + "' " +
                "GROUP BY c.name";

        String query2 = "SELECT h.grade, COUNT(distinct s.id) " +
                "FROM Students s, Classes c, HasTaken h " +
                "WHERE s.id = h.sid AND c.name = h.cname AND c.name = '" + input + "' " +
                "AND (h.grade = 'A' OR h.grade = 'B' OR h.grade = 'C' OR h.grade = 'D' OR h.grade = 'F') " +
                "GROUP BY grade";

        Class.forName("com.mysql.jdbc.Driver");
        Connection con = DriverManager.getConnection(url, uname, pass);
        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery(query);

        rs.next();
        int count = rs.getInt(2);
        System.out.println(count + " student(s) currently enrolled.");

        Statement st2 = con.createStatement();
        ResultSet rs2 = st2.executeQuery(query2);
        System.out.println("Grades of previous enrollees: ");

        while(rs2.next()) {

            System.out.println(rs2.getString(1) + " " + rs2.getInt(2));

        }

        st.close();
        con.close();

    }
    //------------------------------------------------------------------------------------------------------------------


    /**
     * Executes an arbitrary SQL query.
     * @throws Exception
     */
    public void arbitraryQuery() throws Exception {

        System.out.println("Please enter the query.");

        String input = sc.nextLine();

        Class.forName("com.mysql.jdbc.Driver");
        Connection con = DriverManager.getConnection(url, uname, pass);

        Statement st = con.createStatement();

        ResultSet rs = st.executeQuery(input);

        ResultSetMetaData rsmd = rs.getMetaData();

        int numColumns = rsmd.getColumnCount();

        for(int i = 1; i <= numColumns; i++) {

            System.out.print(rsmd.getColumnName(i) + "    ");
        }

        System.out.print("\n");

        while(rs.next()) {

            for(int i = 1; i <= numColumns; i++) {

                System.out.print(rs.getObject(i) + "    ");
            }
            System.out.print("\n");
        }

        st.close();
        con.close();

    }

    //------------------------------------------------------------------------------------------------------------------

    /**
     * Helper method for studentYearQuery, takes in user input and creates the appropriate query.
     * @param year, user input for the student year
     */
    public void findYear(String year) {

        switch (year) {
            case "Fr" -> yearQuery = "Select s.id, s.first_name, s.last_name, SUM(c.credits) AS num_credits " +
                    "FROM Students s, Classes c, HasTaken h " +
                    "WHERE s.id = h.sid AND c.name = h.cname AND grade <> 'F' " +
                    "GROUP BY s.id " +
                    "HAVING SUM(c.credits) BETWEEN 0 AND 29";
            case "So" -> yearQuery = "Select s.id, s.first_name, s.last_name, SUM(c.credits) AS num_credits " +
                    "FROM Students s, Classes c, HasTaken h " +
                    "WHERE s.id = h.sid AND c.name = h.cname AND grade <> 'F' " +
                    "GROUP BY s.id " +
                    "HAVING SUM(c.credits) BETWEEN 30 AND 59";
            case "Ju" -> yearQuery = "Select s.id, s.first_name, s.last_name, SUM(c.credits) AS num_credits " +
                    "FROM Students s, Classes c, HasTaken h " +
                    "WHERE s.id = h.sid AND c.name = h.cname AND grade <> 'F' " +
                    "GROUP BY s.id " +
                    "HAVING SUM(c.credits) BETWEEN 60 AND 89";
            case "Sr" -> yearQuery = "Select s.id, s.first_name, s.last_name, SUM(c.credits) AS num_credits " +
                    "FROM Students s, Classes c, HasTaken h " +
                    "WHERE s.id = h.sid AND c.name = h.cname AND grade <> 'F' " +
                    "GROUP BY s.id " +
                    "HAVING SUM(c.credits) >= 90";
        }

    }


    /**
     * Helper method for nameQuery, studentYearQuery, GPA1Query, GPA2Query, prints out the information for each student
     * from the database via queries.
     * @param rs
     * @throws Exception
     */
    public void printStudentInfo(ResultSet rs) throws Exception {

        int count = 0;

        while (rs.next()) {

            count++;
        }

        System.out.println(count + " student(s) found");

        rs.beforeFirst();

        while (rs.next()) {

            String first_name = rs.getString(2);
            String last_name = rs.getString(3);
            int id_num = rs.getInt(1);

            ResultSet getMajor = st2.executeQuery("SELECT m.dname from Majors m where sid = " + id_num);
            ResultSet getMinor = st3.executeQuery("Select m.dname from Minors m where sid = " + id_num);

            System.out.println(last_name + ", " + first_name);
            System.out.println("ID: " + id_num);

            System.out.print("Major(s): ");
            while (getMajor.next()) {
                System.out.print(getMajor.getString(1) + ", ");
            }

            System.out.print("\n" + "Minor(s): ");
            while (getMinor.next()) {
                System.out.print(getMinor.getString(1) + ", ");
            }

            System.out.print("\n");

            ResultSet getGPA = st4.executeQuery("SELECT s.id, SUM(c.credits * (CASE h.grade " +
                    "WHEN 'A' THEN 4 " +
                    "WHEN 'B' THEN 3 " +
                    "WHEN 'C' THEN 2 " +
                    "WHEN 'D' THEN 1 " +
                    "WHEN 'F' THEN 0 " +
                    "END)) / SUM(c.credits) AS GRADE_POINT " +
                    "FROM Students s, Classes c, HasTaken h " +
                    "WHERE s.id = h.sid AND s.id = " + id_num + " AND c.name = h.cname");

            getGPA.next();
            System.out.println("GPA: " + getGPA.getDouble(2));

            ResultSet getCredits = st5.executeQuery("Select s.id, SUM(c.credits) AS num_credits " +
                    "FROM Students s, Classes c, HasTaken h " +
                    "WHERE s.id = h.sid AND s.id = " + id_num + " AND c.name = h.cname AND grade <> 'F' ");
            getCredits.next();
            System.out.println("Credits: " + getCredits.getInt(2));

            System.out.print("\n");
        }
    }


    /**
     * Helper method for nameQuery, studentYearQuery, GPAQuery1, GPAQuery2, creates the necessary statements to print out
     * student information from the database via queries.
     * @throws Exception
     */
    public void createStatements() throws Exception{

        Class.forName("com.mysql.jdbc.Driver");
        con = DriverManager.getConnection(url, uname, pass);
        st = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        st2 = con.createStatement();
        st3 = con.createStatement();
        st4 = con.createStatement();
        st5 = con.createStatement();
    }

    //------------------------------------------------------------------------------------------------------------------


    /**
     * Run method that takes in user input and acts as a user interface via command-line input.
     * @throws Exception
     */
    public void run() throws Exception {

        System.out.println("Welcome to the university database. Queries available:");

        System.out.println("1. Search students by name.");
        System.out.println("2. Search students by year.");
        System.out.println("3. Search for students with a GPA >= threshold.");
        System.out.println("4. Search for students with a GPA <= threshold.");
        System.out.println("5. Get department statistics.");
        System.out.println("6. Get class statistics.");
        System.out.println("7. Execute an arbitrary SQL query.");
        System.out.println("8. Exit the application." + "\n");

        System.out.println("Which query would you like to run (1-8)?");

        while(sc.hasNextLine()) {


            String command = sc.nextLine();

            operations(command);


            if(command.equals("8")) {

                break;
            }

            System.out.println("Which query would you like to run (1-8)?");

        }
        sc.close();

    }
}
