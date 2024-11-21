package pt.tmg.cbd.lab1.ex3;

import redis.clients.jedis.Jedis;

public class SimplePost {
	public static String USERS_LIST = "usersList"; // List for users name
	public static String USERS_MAP = "usersMap"; //HashMap for users name

	public static void main(String[] args) {
		Jedis jedis = new Jedis();
// some users
		String[] users = { "Ana", "Pedro", "Maria", "Luis" };
		// jedis.del(USERS_KEY); // remove if exists to avoid wrong type
		int n = 1;
		for (String user : users) {
			//redis list write
			jedis.lpush(USERS_LIST, user);
			//redis hashmap write
			jedis.hset(USERS_MAP, "user" + n, user);
			n++;
		}

		//redis list reading
		System.out.println("List");
		jedis.lrange(USERS_LIST,0,-1).forEach(System.out::println);
		//redis hashmap reading
		System.out.println("\nHashMap");
		jedis.hgetAll(USERS_MAP).entrySet().forEach(System.out::println);

		//clean the data
		jedis.del(USERS_LIST);
		jedis.del(USERS_MAP);
		jedis.close();
	}
}


