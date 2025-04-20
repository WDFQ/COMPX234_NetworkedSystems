import java.util.HashMap;

public class TupleSpace {
    //Define hash map to store key and item
    private final HashMap<String, String> dataHashMap = new HashMap<>();

    /**
     * put the data into the hash map
     * @param key the key of the item
     * @param value the value of the item
     * @return returns an int that indicates if the item has been successfully added
     */
    public synchronized int put(String key, String value){
        //Error message
        if(dataHashMap.containsKey(key)){
            return 0;
        }
        else{
            //adds the data in the hash map
            dataHashMap.put(key, value);
            return 1;
        }
    }

    /**
     * gets the data and removes it from hash map
     * @param key the key of the item   
     * @param value the value of the item
     * @return returns either empty or the value to that indicates if the item was successfully removed 
     */
    public synchronized String get(String key){

        //return empty if it doesnt exist exists
        if(!dataHashMap.containsKey(key)){
            return "";
        }
        else{
            //removes data from hashmap 
            String tempKey = dataHashMap.get(key);
            dataHashMap.remove(key);
            return tempKey;
        }
    }

    /**
     * reads the data from hash map
     * @param key the key of the item   
     * @param value the value of the item
     * @return returns either empty or the value to that indicates if the item was successfully removed 
     */
    public synchronized String read(String key){
        //return empty if it doesnt exist exists
        if(!dataHashMap.containsKey(key)){
            return "";
        }
        else{
            //returns value
            return dataHashMap.get(key);
        }
    }

    
    


}
