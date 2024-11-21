package pt.tmg.cbd.lab1.ex4;

import redis.clients.jedis.Jedis;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class AutoCompleteB {
    public static String USERS_MAP = "namesPopularityList"; //HashMap for users name

    public static void main(String[] args) throws IOException {
        Jedis jedis = new Jedis();
        jedis.flushDB();

        File names = new File("src/main/resources/nomes-pt-2021.csv");
        Scanner sc = new Scanner(names);
        readNamesFile(sc,jedis);
        sc.close();

        Scanner sc2 = new Scanner(System.in);
        System.out.print("Search for ('Enter' for quit): ");
        while (sc2.hasNextLine()) {
            String name = sc2.nextLine().trim();
            if (name.equalsIgnoreCase("enter")) {
                break;
            }
            jedis.zrevrange(name, 0,-1).forEach(System.out::println);

            System.out.print("\nSearch for ('Enter' for quit): ");
        }
    }

    public static void readNamesFile(Scanner sc, Jedis jedis) throws IOException {
        while(sc.hasNextLine()){
            String[] line = sc.nextLine().split(";");
            String name = line[0];
            int nameCount = Integer.parseInt(line[1]);

            for (int i=0; i<name.length(); i++) {
                jedis.zadd(name.substring(0,i+1), nameCount, name);
            }

        }
    }
}