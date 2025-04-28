/**
 * Dictionary for Client side
 * It is responsible for getting input and passing to GUI
 * Given Name: Jiali
 * Surname: Ying
 * Student ID: 1346717
 */

import javax.swing.*;
import java.io.*;
import java.net.*;
public class DictionaryClient {
    private final String serverAddress;
    private final int serverPort;
    private DictionaryGUI gui;

    public DictionaryClient(String serverAddress, int serverPort) {
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
        this.gui = new DictionaryGUI();
    }


    public void queryWord(String input){
        try(Socket socket = new Socket(serverAddress, serverPort);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true)){
            out.println(input);
            // result from the server
            String response = in.readLine();
            System.out.println("Server Response: " + response);
            // update UI
            SwingUtilities.invokeLater(() -> gui.updateResult(response));

        }catch(IOException e){
            e.printStackTrace();
            SwingUtilities.invokeLater(() -> gui.updateResult("Error connecting to server."));
        }
    }

    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: java DictionaryClient <server_address> <port>");
            return;
        }

        String serverAddress = args[0];
        int port = Integer.parseInt(args[1]);

        DictionaryClient client = new DictionaryClient(serverAddress, port);
        // send to GUI
        client.gui.createGUI(client);
    }

}
