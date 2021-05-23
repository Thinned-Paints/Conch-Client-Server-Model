import javax.swing.*;
import java.io.*;
import java.net.ConnectException;
import java.net.Socket;
import java.sql.SQLOutput;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

public class Client implements AutoCloseable {
    final int port = 9999;
    private int ID;
    private final Scanner reader;
    private final PrintWriter writer;

    private Set<Integer> clients = new HashSet<>();


    public int getClientId() {
        return ID;
    }

    public synchronized void updateClients(List<Integer> newClients) {
        clients.clear();
        clients.addAll(newClients);
    }

    public Client() throws Exception {
        // Connecting to the server and creating objects for communications
        Socket socket;
        while(true){
            try {
                socket = new Socket("localhost", port);
                break;
            }catch (ConnectException e){
                System.out.println("Waiting for server");
                Thread.sleep(5000);
            }
        }


        System.out.println("Connected");

        reader = new Scanner(socket.getInputStream());

        Scanner inputreader = new Scanner(System.in);

        // Automatically flushes the stream with every command
        writer = new PrintWriter(socket.getOutputStream(), true);

        writer.println("GiveID");
        String SiD = reader.nextLine();

        try {
            ID = Integer.parseInt(SiD);
        }catch (IllegalArgumentException e){
            e.printStackTrace();
            close();
            return;
        }

        System.out.println("" +
                "\n" +
                "       /\\\n" +
                "      {.-}\n" +
                "     ;_.-'\\\n" +
                "    {    _.}_\n" +
                "     \\.-' /  `,\n" +
                "      \\  |    /\n" +
                "       \\ |  ,/\n" +
                "        \\|_/\n" +"Your ID is: "+ID+"\n"+"Welcome, to the client"+"Commands are: \n"+
                "ShowClients\n"+"ShowConch\n"+"GiveConch\n"+"Speak\n"+"Exit");

        new Thread(new ClientListener(this, reader)).start();

        while(true){
            Thread.sleep(100);
            System.out.println("Enter a command:");
            String terminalinput = inputreader.nextLine();
            switch (terminalinput){
                case "ShowClients":
                    writer.println("ShowClients");
                    break;
                case "GiveConch":
                    System.out.println("To who?");
                    String SID = inputreader.nextLine();
                    int ID = Integer.parseInt(SID);
                    writer.println("GiveConch "+ID);
                    break;

                case "ShowConch":
                    writer.println("ShowConch");
                    break;

                case "Speak":
                    writer.println("Speak");
                    break;

                case "Exit":
                    writer.println("Exit");
                    System.exit(0);
                    break;

                default:
                    System.out.println("Invalid input\n"+"Commands are: \n"+
                            "ShowClients\n"+"ShowConch\n"+"GiveConch\n"+"Speak\n"+"Exit");

            }
        }
    }
    //This just shuts the whole thing down
    @Override
    public void close(){
        try {
            reader.close();
            writer.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        System.exit(0);
    }

}
