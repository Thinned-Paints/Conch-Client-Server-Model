import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ClientHandler extends Thread{
    private Socket sock;
    private PrintWriter outgoing;
    private BufferedReader incoming;
    private static Integer Conch;
    private int id;

    private static long messageCount = 0;
    //This is just a constructor
    public ClientHandler(Socket socket, int id){
        this.sock = socket;
        this.id = id;
        this.Conch = null;
    }

    public void sendlist(Set<Integer> s){

        outgoing.println("ShowClients " + s.stream().map(Objects::toString).collect(Collectors.joining(",")));

    }

    private static synchronized void incrementMessage() {
        messageCount++;
    }
    //This gives someone a conch, but only if the sender has the conch, and the recipient exits.
    public boolean setConch(int recipient){
        boolean validrecipient;
        Set<Integer> list = ServerManager.getInstance().getlist();
        validrecipient = list.contains(recipient);
        if(Conch==null || Conch == id&&validrecipient){
            Conch = recipient;
            return true;
        }else{
            return false;
        }
    }

    public int getClientId() {
        return id;
    }

    public void run() {
        try {
            outgoing = new PrintWriter(sock.getOutputStream(),true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            incoming = new BufferedReader(new InputStreamReader(sock.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        //This loops for as long as the socket is open, as it is the listener.
        while (!sock.isClosed()) {
            Set<Integer> list = ServerManager.getInstance().getlist();
            if(list.size()==1){
                int soleuser = new ArrayList<>(list).get(0);
                Conch = soleuser;
            }

            Boolean exitrecieved=false;
            if(Conch==null){
                Random r = new Random();
                int rn = r.nextInt(list.size());
                int target = new ArrayList<>(list).get(rn);
                Conch = target;
            }



            try {
                String line = incoming.readLine();
                String action = line.split(" ")[0];


                //Messages received will be a command, then a space and params if there are any. First string before space is the message type
                switch (action) {
                    case "GiveID":
                        outgoing.println(id);
                        break;

                    case "ShowClients":
                        sendlist(ServerManager.getInstance().getlist());
                        break;

                    case "GiveConch":
                        String recipientString = line.split(" ")[1];
                        System.out.println(id+" is attempting to pass the conch to: "+recipientString);
                        int recipient = Integer.parseInt(recipientString);
                        Boolean result = setConch(recipient);
                        if (result){
                            outgoing.println("GiveConch 1");
                            System.out.println("They succeeded");
                        }else{
                            outgoing.println("GiveConch 2");
                            System.out.println("They failed");
                        }

                        break;
                    case "ShowConch":
                        outgoing.println("ShowConch "+Conch);
                        break;
                    case "Speak":
                        if(id!=Conch){
                            outgoing.println("Speak 1");
                        }else{
                            String quote = getquote();
                            outgoing.println("Speak : "+quote);
                        }
                        break;

                    case "Exit":
                        exitrecieved = true;
                        break;
                    default:
                        System.out.println("An invalid message was received and discarded");
                }
                if (exitrecieved){
                    list.remove(id);
                    System.out.println("Client "+id+" disconnected remaining clients are: "+list);
                    break;
                }
                incrementMessage();
            } catch (IOException e) {
                list.remove(id);
                System.out.println("Client "+id+" disconnected, remaining clients are: "+list);
                break;
            }

        }

        //TODO socket closed or dead, cleanup
        ServerManager.getInstance().stopNremove(this);
    }
    //A fun little method I decided to include, thanks to the whole "Conch" thing
    public String getquote() {
        String[] quotes = new String[]{"What are we? Humans? Or animals? Or savages?",
                "Jack stood up as he said this, the bloodied knife in his hand. The two boys faced each other. There was the brilliant world of hunting, tactics, fierce exhilaration, skill; and there was the world of longing and baffled commonsense.",
        "Bollocks to the rules! We’re strong – we hunt! If there’s a beast, we’ll hunt it down! We’ll close in and beat and beat and beat -",
        "We’ve got to have special people for looking after the fire. Any day there may be a ship out there… and if we have a signal going they’ll come and take us off. And another thing. We ought to have more rules. Where the conch is, that’s a meeting. The same up here as down there"};
        Random r = new Random();
        int rq = r.nextInt(quotes.length);
        return quotes[rq];
    }
}