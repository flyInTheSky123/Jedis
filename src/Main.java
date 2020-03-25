import org.junit.Before;
import org.junit.Test;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.HashMap;
import java.util.Iterator;

public class Main {
    JedisPool jedisPool;
    Jedis jedis;

    @Before
    public void setUp() {
        jedis = new Jedis("localhost");

    }


    /**
     * redis存储字符串
     */
    @Test
    public void testString() {
//      添加数据
        jedis.set("name", "李x");
        System.out.println("value:" + jedis.get("name")); //value:李x
        jedis.set("age", "12");

        //覆盖
        jedis.set("age", "10");
        System.out.println(jedis.get("age")); //10

        //删除
        jedis.del("age");
        System.out.println(jedis.get("age")); //null


        /**
         * mset:
         * 等同于：
         * jedis.set("tel","136578789");
         * jedis.set("addre","cq");
         */
        jedis.mset("tel", "134656734", "addre", "cq");
        System.out.println("mget" + jedis.mget("tel", "addre"));


    }

    /**
     * 操作hash
     * hset,hget
     * hdel,hmset,hmget
     */

    @Test
    public void testHash() {
        //添加hash数据
        jedis.hset("person", "name", "欧x");
        jedis.hset("person", "age", "18");
        System.out.println("你好 ：" + jedis.hvals("person"));

        HashMap<String, String> person1 = new HashMap<>();
        person1.put("age", "18");
        person1.put("name", "赵xx");

        HashMap<String, String> person2 = new HashMap<>();
        person2.put("name", "李x");
        person2.put("age", "16");

        //删除
        // jedis.hdel("person1","name");
        //将person 放入redis中
        jedis.hmset("person1", person1);
        jedis.hmset("person2", person2);

        System.out.println("好久不见：" + jedis.hvals("person1")); //好久不见：[18, 赵xx]
        System.out.println("hgetall：" + jedis.hgetAll("person1")); //hgetall：{age=18, name=赵xx.
        System.out.println("hmget:" + jedis.hmget("person1", "name")); //hmget:[赵xx]

        //iterator(),遍 历
        Iterator<String> pi = jedis.hkeys("person").iterator();
        while (pi.hasNext()) {
            String next = pi.next();
            System.out.println("person " + next + " :" + jedis.hmget("person", next));
            /**结果：
             * person name :[欧x]
             * person age :[18]
             */
        }


    }


    /**
     * 操作list类型
     * lpush ,rpush
     * lpop,rpop,llen
     */
    @Test
    public void testList() {
        /**开始前，先清除相同的key
         * 否则出现：(error) WRONGTYPE Operation against a key holding the wrong kind of value。
         *         一种类型数据命令，操作另外一种数据类型的key（name 在前的类型中已经使用了）。
         */
        jedis.del("name");

        /**
         * lpush 放在最前面
         * rpush 放在最后面
         * rpop  弹出最前面
         * lpop  弹出最后面
         */

        jedis.lpush("name", "李x");
        jedis.lpush("name", "欧x");
        jedis.lpush("name", "张雨x");
        jedis.rpush("name", "赵xx");
        System.out.println(jedis.lrange("name", 0, -1));

        //获取第一个并且进行抛出。
        System.out.println(jedis.lpop("name"));
        System.out.println(jedis.lrange("name", 0, -1));

        //获取最后一个并且进行抛出。
        System.out.println(jedis.rpop("name"));
    }

    /**
     * 用来测试set类型:
     * sdd,smembers 添加/查询。
     * sismember(),判断是否存在。
     */
    @Test
    public void testSet() {
        jedis.sadd("phone", "apple");
        jedis.sadd("phone", "华为");
        jedis.sadd("phone", "vivo");
        //获取set类型phone字段 里面的数据
        System.out.println(jedis.smembers("phone"));
        //判断该数据是否存在于该字段中
        System.out.println(jedis.sismember("phone", "小米"));

    }

    /**
     * 用来操作sortedSet（有序set集合） 数据类型：
     * zadd  添加
     * zrangebyscore  //通过分数排序输出
     * zcount         //该范围内有多少数据数量
     * zcard          //得到成员数量
     * zremrangebyrank key start  stop 在给定的范围内删除数据
     * zrank key member           //判断该字段的成员是否存在
     */

    @Test
    public void testSortedSet() {

        jedis.zadd("fruit", 90, "banana");
        jedis.zadd("fruit", 40, "orange");
        jedis.zadd("fruit", 60, "apple");
        jedis.zadd("fruit", 80, "strawberry");
        //在fruit中 分数在0 100 的数据个数是
        System.out.println(jedis.zcount("fruit", 0, 100));       // 4
        //输出全部
        System.out.println(jedis.zrangeByScore("fruit", 0, 100)); //[orange, apple, strawberry, banana]
        //输出成员数量
        System.out.println(jedis.zcard("fruit"));

        //判断该字段的成员是否存在
        System.out.println(jedis.zrank("fruit", "app"));  // null

        //删除 50~80分数范围内的数据
        System.out.println("删除分数为 50～80 ：" + jedis.zremrangeByScore("fruit", 50, 80)); //删除分数为 50～80 ：2 （删除2个）

        //输出删除之后的数据。
        System.out.println(jedis.zrangeByScore("fruit", 0, 100)); //[orange, banana]


    }
}
