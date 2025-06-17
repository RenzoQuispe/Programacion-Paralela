/*
A HashMap is a data structure in Java that stores data in key-value pairs. You can think of it like a dictionary:

Each key maps to a value.

You use the key to store, retrieve, or update the value
Equivalent in Python is DICTIONARY
*/


import java.util.HashMap;

public class HashMapExample {
    public static void main(String[] args) {
        // Create a HashMap
        HashMap<String, Integer> ageMap = new HashMap<>();

        // Add key-value pairs
        ageMap.put("Alice", 25);
        ageMap.put("Bob", 30);
        ageMap.put("Charlie", 22);

        // Retrieve a value using a key
        System.out.println("Bob's age: " + ageMap.get("Bob"));

        // Update a value
        ageMap.put("Bob", 31);

        // Check if a key exists
        if (ageMap.containsKey("Alice")) {
            System.out.println("Alice is in the map.");
        }

        // Remove a key-value pair
        ageMap.remove("Charlie");

        // Loop through all entries
        for (String name : ageMap.keySet()) {
            System.out.println(name + " -> " + ageMap.get(name));
        }
    }
}