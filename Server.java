import java.io.*;
import java.net.*;
import java.text.ParseException;

public class Server {

        //initiates tuple space
        public static TupleSpace tupleSpace = new TupleSpace();


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
                    while (true) {
                        //connects with client
                        try (Socket clientSocket = serverSocket.accept()) {
                            System.out.println("Connection established with " + clientSocket.getInetAddress());
        
                            // Get the input stream from the client socket
                            InputStream input = clientSocket.getInputStream();
                            // Wrap the input stream in a BufferedReader to read text data
                            BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                            // Get the output stream to send data back to the client
                            OutputStream output = clientSocket.getOutputStream();
                            // Wrap the output stream in a PrintWriter for convenient text output
                            PrintWriter writer = new PrintWriter(output, true);

                            // Read the request line from the client
                            String requestLine = reader.readLine();
                            writer.println(processLine(requestLine));
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
            String action = lineArray[1]; //action (R, P, G)
            String key = lineArray[2]; //key (k)
            String value = ""; //value (v)
            String outputMessage = "";

            if(lineArray.length > 3){
                value = lineArray[3];
            }


            //process info
            if(action.equals("R")){
                //get the return from tuplespace
                value = tupleSpace.read(key);
                //if return value is 1, format the output
                if(value == ""){
                    String errorMessage = "does not exist";
                    //format the error message
                    String formattedErrorMessage = messageLength + " " + errorMessage.length() + " ERR " + key + " " + errorMessage;
                    return formattedErrorMessage;
                }
                else{
                    outputMessage = "read";
                    return messageLength + " " + " OK " + "(" + key + ", " + value + ") " + outputMessage;
                }
            }
            //if action is put
            else if(action.equals("P")){
                //get the return from tuplespace
                int returnValue = tupleSpace.put(key, lineArray[2]);
                //if return value is 1, format the output
                if(returnValue == 1){
                    outputMessage = "added";
                    tupleSpace.put(key, value);
                    return messageLength + " " + " OK " + "(" + key + ", " + value + ") " + outputMessage;
                }
                else{
                    String errorMessage = "already exists";
                    //format the error message
                    String formattedErrorMessage = messageLength + " " + " ERR " + key + " " + errorMessage;
                    return formattedErrorMessage;
                }
            }
            //if the action is get
            else if(action.equals("G")){
                //get the return from tuplespace
                value = tupleSpace.get(key);
                //if return value is 1, format the output
                if(value == ""){
                    String errorMessage = "does not exist";
                    //format the error message
                    String formattedErrorMessage = messageLength + " " + " ERR " + key + " " + errorMessage;
                    return formattedErrorMessage;
                }
                else{
                    outputMessage = "removed";
                    return messageLength + " " + " OK " + "(" + key + ", " + value + ") " + outputMessage;
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
}
