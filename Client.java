import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) {
        //ensures the args contain both host name and port
        if (args.length != 3){
            System.out.println("Usage: java client <hostname> <port> <file>");
            return;
        }

        String hostname = args[0];
        int port = Integer.parseInt(args[1]);
        String filename = args[2];
        
        
        //try to establish a connection to the server
        try (Socket socket = new Socket(hostname, port)){
            //try to initialise all tools needed to exchange info with server
            try (OutputStream output = socket.getOutputStream();
                PrintWriter writer = new PrintWriter(output, true);
                InputStream input = socket.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                )
            {
                //get the file and open it
                File file = new File(filename);
                Scanner scanner = new Scanner(file);

                //read each line
                while(scanner.hasNextLine()){
                    //assign the line into a string
                    String line = scanner.nextLine();

                    //split the line by spaces
                    String[] lineArray = line.split(" ", 3);
                    
                    //assign each element to its respective variables
                    String action = lineArray[0];
                    String key = lineArray[1];
                    String value = "";
                    String formattedString = "";
                    
                    
                    //add value if there is a value
                    if(lineArray.length > 2){
                        value = lineArray[2];
                    }

                    //process due to protocol (NNN R k)
                    int messageLength = 6 + lineArray[0].length() + lineArray[1].length();
                    if(messageLength > 999 || messageLength < 7){
                        System.out.println("Error: Line too long. Line: " + line);
                        //go to next line
                        continue; 
                    }
                    
                    //creating the formatted string
                    if(messageLength < 10){
                        formattedString += "00" + messageLength;
                    }
                    else if(messageLength < 100){
                        formattedString += "0" + messageLength;
                    }

                    if(action.equals("READ")){
                        formattedString += " R ";
                    }
                    else if(action.equals("GET")){
                        formattedString += " G ";
                    }
                    else if(action.equals("PUT")){
                        formattedString += " P ";
                    }
                    else{
                        System.out.println("Error: Invalid action. Line: " + line);
                        continue;
                    }

                    formattedString += key + " " + value;

                    //writes to server
                    writer.println(formattedString);

                    //reasponse from server
                    String response = reader.readLine();
                    String[] reponseArray = response.split(" ", 2);
                    String serverResponseLine = reponseArray[1];

                    String outputString = action + " " + key + " " + value + ": " + serverResponseLine;

                    System.out.println(outputString);
                }

                scanner.close();
            } 
        } 
        catch (IOException e) {
            System.out.println("Client error: " + e.getMessage());
        }


    }

}
