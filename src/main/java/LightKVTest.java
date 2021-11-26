import redis.clients.jedis.Jedis;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class LightKVTest {
    public static void main(String[] args) throws IOException {
        //和hashmap对比一下性能
        //随机插入、查找、删除N次
        final long N = 1000000;

        LightKV lightKV = new LightKV("E:/kvdb/test1.db");
        long startTime_lightKV = System.currentTimeMillis();
        for (long i = 0; i < N; ++i) {
            if (Math.random() < 0.5) {
                lightKV.setCmd(String.valueOf(Math.random() * 0x7fffffff), "fdsaghhhgfgcb");
            }
            if (Math.random() < 0.5) {
                lightKV.getCmd(String.valueOf(Math.random() * 0x7fffffff));
            }
            if (Math.random() < 0.5) {
                lightKV.delCmd(String.valueOf(Math.random() * 0x7fffffff));
            }
        }
        long endTime_lightKV = System.currentTimeMillis();
        System.out.println("LightKV:" + (endTime_lightKV - startTime_lightKV) + "ms");

        Map<String, String> map = new HashMap<>();
        long startTime_map = System.currentTimeMillis();
        for (long i = 0; i < N; ++i) {
            if (Math.random() < 0.5) {
                map.put(String.valueOf(Math.random() * 0x7fffffff), "fdsaghhhgfgcb");
            }
            if (Math.random() < 0.5) {
                map.get(String.valueOf(Math.random() * 0x7fffffff));
            }
            if (Math.random() < 0.5) {
                map.remove(String.valueOf(Math.random() * 0x7fffffff));
            }
        }
        long endTime_map = System.currentTimeMillis();
        System.out.println("HashMap:" + (endTime_map - startTime_map) + "ms");

        Jedis jedis = new Jedis("localhost", 6379);
        long startTime_redis = System.currentTimeMillis();
        for (long i = 0; i < N; ++i) {
            if (Math.random() < 0.5) {
                jedis.set(String.valueOf(Math.random() * 0x7fffffff), "fdsaghhhgfgcb");
            }
            if (Math.random() < 0.5) {
                jedis.get(String.valueOf(Math.random() * 0x7fffffff));
            }
            if (Math.random() < 0.5) {
                jedis.del(String.valueOf(Math.random() * 0x7fffffff));
            }
        }
        long endTime_redis = System.currentTimeMillis();
        System.out.println("Redis:" + (endTime_redis - startTime_redis) + "ms");
        jedis.close();

    }
}
