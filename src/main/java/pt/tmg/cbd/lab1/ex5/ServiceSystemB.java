package pt.tmg.cbd.lab1.ex5;

import redis.clients.jedis.Jedis;

import java.io.IOException;
import java.util.Scanner;
import java.util.Map;

public class ServiceSystemB {
    private static int DEFAULT_LIMIT = 5;
    private static int DEFAULT_TIMESLOT = 30;

    private static final Jedis jedis = new Jedis();
    private static int limit;
    private static int timeslot;
    private static int count=1;

    public static void main(String[] args) throws IOException {
        jedis.flushAll();

        //set the variables
        setLimit(DEFAULT_LIMIT);
        setTimeslot(DEFAULT_TIMESLOT);

        String username;

        Scanner sc = new Scanner(System.in);

        while(true) {
            System.out.print("Insert client username (Type 'quit' to quit): ");
            String usernameInput = usernameLoop(sc);
            if (checkIfQuit(usernameInput)) {
                break;
            }
            username = usernameInput;

            productPurchaseLoop(sc, username);
        }

    }

    public static void registerProduct(String username, String produto, int quantity){
        long currentTime = System.currentTimeMillis() / 1000;

        jedis.zremrangeByScore(username, 0, currentTime - timeslot);

        int totalUnits = calculateTotalUnits(username);

        if(totalUnits+quantity>limit){
            System.err.println("You have exceeded the limit of " + limit + " products in " + timeslot + " seconds.");
        }
        else{
            jedis.zadd(username, currentTime, produto);
            System.out.println("["+username+"] Product: "+produto + " " + quantity +"x purchased successfully. ");
            jedis.hset(username+"(quantity)", produto, String.valueOf(quantity));
        }
    }

    public static String usernameLoop(Scanner sc){
        String usernameInput = sc.nextLine();
        while(usernameInput.isEmpty()) {
            System.out.println("Error. You must insert a client username.");
            System.out.print("Insert client username (Type 'quit' to quit): ");
            usernameInput = sc.nextLine();
        }
        return usernameInput;
    }

    public static Boolean checkIfQuit(String usernameInput){
        return usernameInput.equalsIgnoreCase("quit");
    }

    public static void productPurchaseLoop(Scanner sc, String username){
        System.out.println("Hi " + username);
        System.out.println("\nInsert the product to buy [{prdouctName} {quantity}]. (Type 'exit' to quit)");

        while (sc.hasNextLine()){
            String line = sc.nextLine();
            if (line.equals("exit")) {
                System.out.println("Thank you!\n");
                return;
            }
            else {
                int quantity = readQuantity(line);
                String productName = productNameGetter(line);

                registerProduct(username, productName + count, quantity);
                count++;
            }
            System.out.println("\nInsert the product to buy [{prdouctName} {quantity}]. (Type 'exit' to quit)");
        }
    }

    public static String productNameGetter(String line){
        String[] productNameArr = line.split(" ");
        String productName = "";
        int quantityWords=0;
        if (productNameArr[productNameArr.length-1].matches("\\d+")){
            quantityWords=1;
        }

        for (int i=0; i<productNameArr.length-quantityWords; i++){
            productName+=productNameArr[i]+" ";
        }
        return productName;
    }

    public static int readQuantity(String line){
        String[] purchase = line.split(" ");
        try {
            return Integer.parseInt(purchase[purchase.length-1]);
        } catch (NumberFormatException e) {
            return 1;
        }
    }

    public static int calculateTotalUnits(String username){
        Map<String, String> productsQuantities= jedis.hgetAll(username+"(quantity)");
        int totalQuantity = 0;

        for(String product : jedis.zrange(username, 0, limit)){
            if (productsQuantities.containsKey(product)){
                totalQuantity += Integer.parseInt(productsQuantities.get(product));
            }
        }
        return totalQuantity;
    }

    public static void setLimit(int newLimit){
        System.out.println("Purchase limit set to " + newLimit + " units.");
        limit = newLimit;
    }

    public static int getLimit(){
        return limit;
    }

    public static void setTimeslot(int newTimeslot){
        System.out.println("Purchase time set to " + newTimeslot + " seconds.");
        timeslot = newTimeslot;
    }

    public static int getTimeslot(){
        return timeslot;
    }
}

