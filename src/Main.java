public class Main {
    public static void main(String[] args) throws Exception {

        String url = args[0];
        String uname = args[1];
        String pass = args[2];
        new App(url, uname, pass).run();
    }
}