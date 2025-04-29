import java.util.HashMap;

public class TupleSpace {
    //Define hash map to store key and item
    private final HashMap<String, String> dataHashMap = new HashMap<>();

    //for server output
    private static int noOfTuples = 0;
    private static double avgTupleSize = 0;
    private static double avgKeySize = 0;
    private static double avgValueSize = 0;
    private static int totalReads = 0;
    private static int totalGets = 0;
    private static int totalPuts = 0;
    private static int totalErrors = 0;
    private static int totalOperations = 0;


    //methods to get the data from the hash map
    public synchronized int getNoOfTuples() {
        return noOfTuples;
    }

    public synchronized double getAvgTupleSize() {
        return avgTupleSize;
    }

    public synchronized double getAvgKeySize() {
        return avgKeySize;
    }

    public synchronized double getAvgValueSize() {
        return avgValueSize;
    }

    public synchronized int getTotalReads() {
        return totalReads;
    }

    public synchronized int getTotalGets() {
        return totalGets;
    }

    public synchronized int getTotalPuts() {
        return totalPuts;
    }

    public synchronized int getTotalErrors() {
        return totalErrors;
    }

    public synchronized int getTotalOperations() {
        return totalOperations;
    }

    /**
     * Updates the averages of all the fields
     */
    private void updateAvgSizes(){
        int noOfEntries = 0;
        int totalKeySize = 0;
        int totalValueSize = 0;

        //iterates through the hashmap
        for (String key : dataHashMap.keySet()) {
            //gets the value at the current key
            String value = dataHashMap.get(key); 
            totalValueSize += value.length();
            totalKeySize += key.length();

            noOfEntries++;
        }

        //ensure that its not dividing by 0
        if(noOfEntries != 0){
            //updates all avearges
            avgKeySize = totalKeySize/(double)noOfEntries;
            avgValueSize = totalValueSize/(double)noOfEntries;
            avgTupleSize = (totalValueSize + totalKeySize)/(double)noOfEntries;
        }
    }
    

    /**
     * put the data into the hash map
     * @param key the key of the item
     * @param value the value of the item
     * @return returns an int that indicates if the item has been successfully added
     */
    public synchronized int put(String key, String value){
       
        //Error message
        if(dataHashMap.containsKey(key)){
            totalOperations++;
            totalErrors++;
            return 1;
        }
        else{
            totalOperations++;
            totalPuts++;
            //adds the data in the hash map
            dataHashMap.put(key, value);
            noOfTuples++;
            updateAvgSizes();
            return 0;
        }
    }

    /**
     * gets the data and removes it from hash map
     * @param key the key of the item   
     * @param value the value of the item
     * @return returns either empty or the value to that indicates if the item was successfully removed 
     */
    public synchronized String get(String key){
        totalOperations++;
        System.out.println("contains key: " + key + ": " + dataHashMap.containsKey(key));
        
        if(dataHashMap.containsKey(key)){
            totalGets++;
            totalOperations++;
            //removes data from hashmap 
            String tempKey = dataHashMap.get(key);
            dataHashMap.remove(key);
            noOfTuples--;
            updateAvgSizes();
            return tempKey;
        }
        //return empty if it doesnt exist exists
        else{
            totalErrors++;
            return "";
        }
    }

    /**
     * reads the data from hash map
     * @param key the key of the item   
     * @param value the value of the item
     * @return returns either empty or the value to that indicates if the item was successfully removed 
     */
    public synchronized String read(String key){
        totalOperations++;

        //return empty if it doesnt exist exists
        if(!dataHashMap.containsKey(key)){
            totalErrors++;
            return "";
        }
        else{
            totalReads++;
            //returns value
            return dataHashMap.get(key);
            
        }
    }
}
