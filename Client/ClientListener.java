import jdk.swing.interop.SwingInterOpUtils;

import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;
import java.util.Scanner;
import java.util.stream.Collectors;

public class ClientListener extends Thread {
    private final Scanner input;
    private final Client client;

    public ClientListener(Client client, Scanner input) {
        this.input = input;
        this.client = client;
    }

    public void run() {
        //This is the listener, and will listen for any inputs, then process them in the order they arrived.
        while (true) {
            try {
                String message = input.nextLine();
                String command = message.split(" ")[0];
                switch (command){
                    case "ShowClients": //received when users join/leave the server and when requested by us
                        String clients = message.split(" ")[1];
                        System.out.println(clients);
                        client.updateClients(
                                Arrays.stream(clients.split(","))
                                        .map(Integer::parseInt)
                                        .collect(Collectors.toList())
                        );
                        break;

                    case "Speak":

                        String scase = message.split(" ")[1];

                        if(scase.equals("1")){
                            System.out.println("You do not have the conch, so you cannot speak!");
                        }else{
                            String quote = message.split(":")[1];
                            System.out.println(quote);
                        }

                        break;

                    case "GiveConch":
                        String result = message.split(" ")[1];
                        if(result.equals("1")) {
                            System.out.println("Success");
                        }else{
                            System.out.println("Failure to pass conch");
                        }
                        break;

                    case "ShowConch":
                        String sconch = message.split(" ")[1];
                        //Sconch is best read with a  Sean Connery impression
                        int conch = Integer.parseInt(sconch);
                        int ID = client.getClientId();
                        System.out.println("You are: "+ID+", the conch is owned by: "+conch);
                        if(ID==conch){
                            System.out.println("which happens to be you!\n");
                        }else{
                            System.out.println("which does not happen to be you!\n");
                        }
                        break;

                    default:
                        System.out.println("An invalid message was received and discarded");

                }
            }catch (Exception e){
                e.printStackTrace();
                System.exit(0);
            }
        }
    }
}
