import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        System.out.println("" +
                "\n" +
                "       /\\\n" +
                "      {.-}\n" +
                "     ;_.-'\\\n" +
                "    {    _.}_\n" +
                "     \\.-' /  `,\n" +
                "      \\  |    /\n" +
                "       \\ |  ,/\n" +
                "        \\|_/\n" +"Welcome, to the server side, there is no-one here but me");

        ServerManager.getInstance().mstart(9999);

    }
}
