import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class ServerManager {
    private static ServerManager instance;

    private ServerSocket serverSocket;

    private Map<Integer, ClientHandler> clientThreads = new HashMap<>();

    private ServerManager() {
        instance = this;
    }
    //This is just a getter for the instance, so that a ClientHandler can access servermanager stuff occasionally.
    public static ServerManager getInstance() {
        if(instance == null) return new ServerManager();

        return instance;
    }
    //This returns a list of clientThreads
    public Set<Integer> getlist(){
        return clientThreads.keySet();
    }
    //This is the main for this class, it opens a new serversocket, then contuinually listens for incoming connections, and assigns them an ID, a thread and an instance of ClientHandler
    public void mstart(int port) throws IOException {
        serverSocket = new ServerSocket(port);

        while (true) {
            Socket socket = serverSocket.accept();
            int id = generateId();
            System.out.println("Client:"+id+" Connected");

            clientThreads.put(id, new ClientHandler(socket, id));
            new Thread(clientThreads.get(id)).start();
        }
    }
    public void publishList(){
        Set<Integer> list = getlist();
        for(Map.Entry<Integer,ClientHandler> x:clientThreads.entrySet()){
            x.getValue().sendlist(list);
        }
    }

    private Random r = new Random();
    //This generates a new unique ID and appends it to the end of the array.
    public int generateId() {
        while(true) {
            int ID = r.nextInt(1000);
            if(clientThreads.containsKey(ID)) continue;
            return ID;
        }
    }
    //Just kills the server
    public void stop() throws IOException {
        serverSocket.close();
    }
    //Kills the threads and kills the server
    public void stopNremove(ClientHandler ch){
        clientThreads.remove(ch.getClientId());
        ch.stop();
    }

}
