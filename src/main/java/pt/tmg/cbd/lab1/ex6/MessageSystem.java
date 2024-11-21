package pt.tmg.cbd.lab1.ex6;

import redis.clients.jedis.Jedis;

import java.io.IOException;
import java.sql.SQLOutput;
import java.util.Scanner;

public class MessageSystem {
    private static final Jedis jedis = new Jedis();

    private static String activeUser = "";

    public static String USERS_SET = "USERS";
    public static String FOLLOWERS_SET = "FOLLOWERS";
    public static String MESSAGES_LIST = "MESSAGES";
    public static String LOGIN_SET = "LOGIN";

    public static void addUser(String user, String password) {
        jedis.set(LOGIN_SET + ":" + user, password);
        jedis.sadd(USERS_SET, user);
    }



    public static void addFollower(String username, String follower){
        jedis.sadd(FOLLOWERS_SET+":"+username, follower);
    }

    public static void addMessage(String user, String message){
        jedis.rpush(MESSAGES_LIST+":"+user, message);
    }

    public static String[] getMessages(String user){
        return jedis.lrange(MESSAGES_LIST+":"+user, 0,-1).toArray(new String[0]);
    }

    public static String[] getFollowers(String user) {
        return jedis.smembers(FOLLOWERS_SET + ":" + user).toArray(new String[0]);
    }


    public static String[] getUsers() {
        return jedis.smembers(USERS_SET).toArray(new String[0]);
    }

    public static void removeUser(String user){
        jedis.srem(USERS_SET, user);

        String[] users = getUsers();
        for(int i = 0; i < users.length; i++){
            jedis.srem(FOLLOWERS_SET+":"+users[i],user);
        }

        jedis.del(FOLLOWERS_SET+":"+user);
        jedis.del(MESSAGES_LIST+":"+user);
        jedis.del(LOGIN_SET+":"+user);
    }

    public static void removeFollower(String user, String follower){
        jedis.srem(FOLLOWERS_SET+":"+user, follower);
    }

    public static boolean checkIfFollowUser(String follower, String user){
        return jedis.sismember(FOLLOWERS_SET+":"+user, follower);
    }

    public static boolean checkIfUsernameExists(String username){
        return jedis.sismember(USERS_SET, username);
    }

    public static boolean checkIfValidLogin(String username, String password) {
        if (jedis.sismember(USERS_SET, username)) {
            String storedPassword = jedis.get(LOGIN_SET + ":" + username);

            if (storedPassword != null && storedPassword.equals(password)) {
                System.out.println("Successfully logged in.");
                activeUser = username;
                return true;
            }
        }

        System.err.println("Invalid username or password.");
        return false;
    }






    public static void main(String[] args) {
        jedis.flushDB();
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("-------- CHAT SYSTEM --------");
            System.out.println("1. Sign in");
            System.out.println("2. Log in");
            System.out.println("3. Exit");
            System.out.print("> ");
            if (sc.hasNextLine()) {
                switch(sc.nextLine()) {
                    case "1":
                        if (signIn(sc)) {
                            menu(activeUser, sc);
                        }
                        break;

                    case "2":
                        if (logIn(sc)) {
                            menu(activeUser, sc);
                        }
                        break;
                    case "3":
                        System.out.println("Exiting...");
                        System.exit(0);
                    default:
                        System.err.println("Invalid input.");
                        break;
                }
            }
        }
    }

    public static boolean signIn(Scanner sc) {
        String username = "invalid";
        String password = "invalid";

        System.out.println("\nUsername: ");
        if (sc.hasNext()) {
            username = sc.next();
            if (checkIfUsernameExists(username)){
                System.out.println("Username already exists.");
                return false;
            }
        }
        else{
            System.err.print("Error. Invalid username.\n");
            return false;
        }
        System.out.println("Password: ");
        if (sc.hasNext()) {
            password = sc.next();
        }
        else{
            System.err.print("Error. Password is empty");
            return false;
        }
        System.out.println("Confirm Password:");
        if (sc.hasNext() && sc.next().equals(password)) {
            System.out.println("Successfully logged in.");
        }
        else {
            System.err.print("Error. Your password does not match.\n");
            return false;
        }

        addUser(username, password);
        activeUser=username;
        return true;
    }

    public static boolean logIn(Scanner sc) {
        String username = "invalid";
        String password = "invalid";

        System.out.println("\nUsername: ");
        if (sc.hasNext()) {
            username = sc.next();
            if (!checkIfUsernameExists(username)){
                System.out.println("This username does not exist.");
                return false;
            }
        }
        else{
            System.err.print("Error. Invalid username.\n");
            return false;
        }
        System.out.println("Password: ");
        if (sc.hasNext()) {
            password = sc.next();
        }
        else{
            System.err.print("Error. Password is empty");
            return false;
        }

        return checkIfValidLogin(username, password);
    }

    public static void menuPrint(){
        System.out.println("\n-------- MENU --------");
        System.out.println("1. Publish a message");
        System.out.println("2. Follow a user");
        System.out.println("3. Unfollow a user");
        System.out.println("4. Read my publish history");
        System.out.println("5. Read a user's publish history");
        System.out.println("6. Check my followers");
        System.out.println("7. Log out");
        System.out.println("8. Delete my account\n");
        System.out.print("Enter your choice: ");
    }

    public static void menu(String user, Scanner sc){
        System.out.println("\nWelcome back, " + user + "!");
        String username;
        while (true) {
            menuPrint();
            String choice = sc.next();
            switch (choice){
                case "1":
                    System.out.println("Write your message below. ");
                    sc.nextLine();
                    String message = sc.nextLine();
                    addMessage(user, message);
                    break;
                case "2":
                    System.out.println("Username of the user you want to follow: ");
                    username = sc.next();
                    if (!checkIfUsernameExists(username)){
                        System.out.println("This username does not exist.");
                        break;
                    }
                    else{
                        addFollower(username, user);
                        System.out.println("You now follow "+ username);
                        break;
                    }
                case "3":
                    System.out.println("Username of the user you want to unfollow: ");
                    username = sc.next();
                    if (!checkIfUsernameExists(username) || !checkIfFollowUser(user, username)){
                        System.out.println("You don't follow this user! ");
                        break;
                    }
                    else{
                        removeFollower(username, user);
                        System.out.println(username+" unfollowed.");
                        break;
                    }
                case "4":
                    if (getMessages(user).length == 0) {
                        System.out.println("You have not published anything yet!");
                        break;
                    }
                    System.out.println("Your publish history:");
                    for (String msg : getMessages(user)) {
                        System.out.println(msg);
                    }
                    break;
                case "5":
                    System.out.print("Enter the username of the user: ");
                    username=sc.next();
                    if(!checkIfUsernameExists(username)){
                        System.out.println("This username does not exist.");
                        break;
                    }
                    if(!checkIfFollowUser(user, username)){
                        System.out.println("You don't follow this user! ");
                        break;
                    }
                    if (getMessages(username).length == 0) {
                        System.out.println(username+" have not published anything yet.");
                        break;
                    }
                    System.out.println(username + " publish history:");
                    for (String msg : getMessages(username)) {
                        System.out.println(msg);
                    }
                    break;
                case "6":
                    if(getFollowers(user).length == 0){
                        System.out.println("You don't have any follower.");
                        break;
                    }
                    System.out.println("Your followers: ");
                    for (String follower : getFollowers(user)) {
                        System.out.println(follower);
                    }
                    break;
                case "7":
                    activeUser="";
                    System.out.println(user + " logged out successfully.");
                    sc.nextLine();
                    return;
                case "8":
                    activeUser="";
                    removeUser(user);
                    System.out.println("Your account has been deleted permanently.");
                    sc.nextLine();
                    return;
            }
        }
    }
}
