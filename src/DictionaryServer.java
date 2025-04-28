/**
 * Dictionary Server
 * Using Thread Pool, TCP
 * It is used for get commands, process commands and run command and edit dictionary
 * result will not change the original dictionary input file
 * Given Name: Jiali
 * Surname: Ying
 * Student ID: 1346717
 */

import java.net.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.concurrent.ConcurrentHashMap;


public class DictionaryServer{
    // port number
    private int port;
    // store dictionary from input file
    private Map<String, List<String>> dictionary;
    // input dictionary file
    private final String dictionaryFile;
    // Thread Pool
    private final ExecutorService executorService;
    private final Processor processor;

    public DictionaryServer(int port, String dictionaryFile){
        this.port = port;
        this.dictionaryFile = dictionaryFile;
        this.dictionary =  new ConcurrentHashMap<>();
        this.processor = new Processor(dictionary);
        this.executorService = Executors.newFixedThreadPool(10);

        loadDictionary();
    }
    // get dictionary from input
    // format - word: ["meaning 1", "meaning 2", "meaning 3"]
    private void loadDictionary() {
        try (BufferedReader br = new BufferedReader(new FileReader(dictionaryFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                // remove head and tail whitespace
                line = line.trim();
                if (!line.isEmpty()) {
                    //separate word and meaning
                    String[] parts = line.split(":");
                    if (parts.length == 2) {
                        String word = parts[0].trim();
                        String meaningsString = parts[1].trim();
                        List<String> meanings = getMeanings(meaningsString);
                        dictionary.put(word, meanings);
                    }
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("Error: Dictionary file not found - " + dictionaryFile);
        } catch (IOException e) {
            System.out.println("Error reading dictionary file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void start() {
        // create a socket
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server started, listening to port: " + port);

            // continue accepting new connections
            while (true) {
                try{
                    Socket clientSocket = serverSocket.accept();
                    System.out.println("Connected with client at " + clientSocket.getInetAddress());

                    // Thread Pool
                    executorService.submit(() -> handleClient(clientSocket));
                } catch (IOException e) {
                    if (e instanceof BindException) {
                        // port is already occupied
                        System.out.println("Error: Port " + port + " is already in use. Please try a different port.");
                    } else {
                        System.out.println("Error starting the server: " + e.getMessage());
                    }
                    e.printStackTrace();
                }

            }
        } catch (IOException e) {
            System.out.println("Error starting the server: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void handleClient(Socket clientSocket){
        try(BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)){

            String input;

            while((input = in.readLine()) != null){
                System.out.println("got input: " + input);
                String response = processor.processCommand(input);
                out.println(response);
            }
        }catch (UnknownHostException uhe){
            uhe.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private List<String> getMeanings(String input) {
        List<String> meanings = new ArrayList<>();
        // anything between ""
        Matcher matcher = Pattern.compile("\"([^\"]+)\"").matcher(input);
        while (matcher.find()) {
            meanings.add(matcher.group(1));
        }
        return meanings;
    }

    public static void main(String args[]) {
        if(args.length != 2){
            System.out.println("use: java DictionaryServer <port> <file>");
            return;
        }
        try{
            int port = Integer.parseInt(args[0]);
            String dictionaryFile = args[1];
            File file = new File(dictionaryFile);
            if (!file.exists()) {
                throw new FileNotFoundException("Dictionary file not found: " + dictionaryFile);
            }
            DictionaryServer server = new DictionaryServer(port, dictionaryFile);
            server.start();
        }catch (NumberFormatException e) {
            System.out.println("Invalid port number format.");
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        } catch (Exception e) {
            System.out.println("Unexpected error: " + e.getMessage());
            e.printStackTrace();
        }

    }
}