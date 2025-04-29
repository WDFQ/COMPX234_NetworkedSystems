import java.io.*;
import java.net.*;

public class Server {

    //initiates tuple space
    public static TupleSpace tupleSpace = new TupleSpace();
    public static int totalClientConnections = 0;

    public static void main(String[] args) {
    

        // Ensure the correct number of arguments are provided
        if (args.length != 1) {
            System.out.println("Usage: java Server <port>");
            return;
        }

        //try to parse port no to int
        try {
            int port = Integer.parseInt(args[0]);
            //ensure that the given port number is within boundary
            if (!(port >= 50000 && port <= 59999)) {
                System.out.println("Usage: java Server <port>");
                return;
            }
            else{

                //try to activate the server socket
                try (ServerSocket serverSocket = new ServerSocket(port)) {
                    System.out.println("Server is listening on port " + port);

                    //thread for displaying information
                    Thread newTimerThread = new Thread(() -> {
                        try {
                            //run until interrupted
                            while(!Thread.currentThread().isInterrupted()){
                                //display the information every 10 seconds
                                display();
                                Thread.sleep(10000);
                            }
                        } 
                        catch (Exception e) {
                            System.out.println("Timer Thread Error: " + e.getMessage());
                        }
                    }
                    );
                    
                    //daemon thread does not block the program from exiting
                    newTimerThread.setDaemon(true);
                    //start the thread
                    newTimerThread.start();
                    while (true) {
                        try {
                            //connects with client
                            Socket clientSocket = serverSocket.accept();
                            totalClientConnections++;

                            //creates new thread to run the operations
                            Thread newClientThread = new Thread(() -> {
                                //creates new socket for this thread
                                Socket newthreadSocket = clientSocket;
                                try {
                                    System.out.println("Connection established with " + clientSocket.getInetAddress());
        
                                    // Get the input stream from the client socket
                                    InputStream input = newthreadSocket.getInputStream();
                                    // Wrap the input stream in a BufferedReader to read text data
                                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                                    // Get the output stream to send data back to the client
                                    OutputStream output = newthreadSocket.getOutputStream();
                                    // Wrap the output stream in a PrintWriter for convenient text output
                                    PrintWriter writer = new PrintWriter(output, true);
                                    //run continously until no more lines to put to server
                                    while(true){
                                        // Read the request line from the client
                                        String requestLine = reader.readLine();
                                        //stop resbonding if no more lines is coming from client
                                        if(requestLine == null){
                                            System.out.println("Client disconnected");
                                            break;
                                        }

                                        //process the line
                                        String processedLine = processLine(requestLine);
                                        //if processing fails, skip current and accept next line from client
                                        if(processedLine.isEmpty()){
                                            continue;
                                        }
                                        
                                        //return info to client
                                        writer.println(processedLine);
                                    }
                                } 
                                catch (Exception e) {
                                    System.out.println("Client input error: " + e.getMessage());
                                }
                                finally{

                                    try {
                                        newthreadSocket.close();
                                    } 
                                    catch (Exception e) {
                                        System.out.println("Error closing client thread: " + e.getMessage());
                                    }
                                }
                            }
                            );

                            //start the thread
                            newClientThread.start();
                            
                        } 
                        catch (IOException e) {
                            System.out.println("Error handling client: " + e.getMessage());
                        }
                    }
                } catch (IOException e) {
                    System.out.println("Server error: " + e.getMessage());
                }
            }
        } 
        catch (Exception e) {
            System.out.println("port not to specification. Needs to be between 50000 and 59999");
        }
    }

    /**
     * * Processes the request line from the client.
     * @param requestLine the request line from the client
     * @return the response to be sent back to the server
     */
    private static String processLine(String requestLine) {
        try {
            //split the line by spaces
            String[] lineArray = requestLine.split(" ");

            //decodes the information sent from client
            String messageLength = lineArray[0];
            String action = lineArray[1]; 
            String key = lineArray[2]; 
            String value = ""; 
            String outputMessage = "";

            if(lineArray.length > 3){
                value = lineArray[3];
            }

            String response = "";
            //process info
            if(action.equals("R")){
                //get the return from tuplespace
                value = tupleSpace.read(key);
                //if return value is 1, format the output
                if(value.equals("")){
                    String errorMessage = "does not exist";
                    //format the error message
                    response = messageLength + " " + "ERR" + " " + key + " " + errorMessage;
                    return response;
                }
                else{
                    outputMessage = "read";
                    return messageLength + " " + "OK" + " " + "(" + key + ", " + value + ")" + " " + outputMessage;
                }
            }
            //if action is put
            else if(action.equals("P")){
                //get the return from tuplespace
                int returnValue = tupleSpace.put(key, value);
                //if return value is 1, format the output
                if(returnValue == 0){
                    outputMessage = "added";
                    return messageLength + " " + "OK" + " " + "(" + key + ", " + value + ")" + " " + outputMessage;
                }
                else{
                    String errorMessage = "already exists";
                    //format the error message
                    response = messageLength + " " + "ERR" + " " + key + " " + errorMessage;
                    return response;
                }
            }
            //if the action is get
            else if(action.equals("G")){
                //get the return from tuplespace
                value = tupleSpace.get(key);
                //if return value is 1, format the output
                if(value.equals("")){
                    String errorMessage = "does not exist";
                    //format the error message
                    response = messageLength + " " + "ERR" + " " + key + " " + errorMessage;
                    return response;
                }
                else{
                    outputMessage = "removed";
                    return messageLength + " " + "OK" + " " + "(" + key + ", " + value + ")" + " " + outputMessage;
                }
            }
            else{
                System.out.println("Invalid action, Line: " + requestLine);
                return "";
            }       
        } 
        catch (Exception e) {
           System.out.println("Error: " + e.getMessage());
           return "";
        }
       
    }

    /**
     * display system information
     */
    private static void display(){
        System.out.println("--- Tuple Space Stats ---");
        System.out.println("Tuples: " + tupleSpace.getNoOfTuples());

        System.out.println("Avg Tuple Size: " + tupleSpace.getAvgTupleSize());
        System.out.println("Avg Key Size: " + tupleSpace.getAvgKeySize());
        System.out.println("Avg Value Size: " + tupleSpace.getAvgValueSize());

        System.out.println("Clients: " + totalClientConnections);
        System.out.println("Operations: " + tupleSpace.getTotalOperations());

        System.out.println("Reads: " + tupleSpace.getTotalReads());
        System.out.println("Gets: " + tupleSpace.getTotalGets());
        System.out.println("Puts: " + tupleSpace.getTotalPuts());
        System.out.println("Errors: " + tupleSpace.getTotalErrors());
    }
}
