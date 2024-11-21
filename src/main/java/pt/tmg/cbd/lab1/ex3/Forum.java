package pt.tmg.cbd.lab1.ex3;

import redis.clients.jedis.Jedis;

public class Forum {
    public static void main(String[] args) {
// Ensure you have redis-server running
        Jedis jedis = new Jedis();
        System.out.println(jedis.ping());
        System.out.println(jedis.info());
        jedis.close();
    }
}