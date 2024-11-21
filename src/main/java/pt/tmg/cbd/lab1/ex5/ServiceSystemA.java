package pt.tmg.cbd.lab1.ex5;

import redis.clients.jedis.Jedis;

import java.io.IOException;
import java.util.Scanner;

public class ServiceSystemA {
    private static int DEFAULT_LIMIT = 5;
    private static int DEFAULT_TIMESLOT = 30;

    private static final Jedis jedis = new Jedis();
    private static int limit;
    private static int timeslot;

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

    public static void registarProduto(String username, String produto){
        long currentTime = System.currentTimeMillis() / 1000;
        jedis.zremrangeByScore(username, 0, currentTime - timeslot);

        if(jedis.zcard(username)>=limit){
            System.err.println("You have exceeded the limit of " + limit + " products in " + timeslot + " seconds.");
        }
        else{
            jedis.zadd(username, currentTime, produto);
            System.out.println("["+username+"] Product: "+produto +" purchased successfully. ");
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
        System.out.println("Insert the product to buy. (Type 'exit' to quit)");

        int count = 1;
        while (sc.hasNextLine()){
            String line = sc.nextLine();
            if (line.equals("exit")) {
                System.out.println("Thank you!\n");
                return;
            }
            else {
                registarProduto(username, line + count);
                count++;
            }
            System.out.println("\nInsert the product to buy. (Type 'exit' to quit)");
        }
    }

    public static void setLimit(int newLimit){
        System.out.println("Purchase limit set to " + newLimit + " products.");
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
