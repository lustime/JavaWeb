package io.github.dunwu.javaweb;

import org.junit.Test;

import java.util.*;

/**
 * 负载均衡测试
 *
 * @author <a href="mailto:forbreak@163.com">Zhang Peng</a>
 * @since 2020-01-20
 */
public class LoadBalanceTests {

    private Map<String, Integer> initSample() {
        Map<String, Integer> map = new LinkedHashMap<>();
        map.put("192.168.1.1", 1);
        map.put("192.168.1.2", 1);
        map.put("192.168.1.3", 2);
        map.put("192.168.1.4", 3);
        map.put("192.168.1.5", 3);
        return map;
    }

    /**
     * 统计负载均衡命中次数，样本数为 100000 次访问
     *
     * @param algorithm
     */
    private void countLoadBalance(LoadBalance<String> algorithm) {
        Map<String, Integer> countMap = new TreeMap<>();

        for (int i = 0; i < 100000; i++) {
            String node = algorithm.next();
            if (countMap.containsKey(node)) {
                Integer value = countMap.get(node);
                countMap.put(node, ++value);
            } else {
                countMap.put(node, 1);
            }
        }
        countMap.forEach((key, value) -> {
            System.out.printf("key = %s, value = %s\n", key, value);
        });
    }

    @Test
    public void randomLoadBalanceTest() {
        RandomLoadBalance<String> algorithm = new RandomLoadBalance<>();
        algorithm.buildInList(initSample().keySet());
        countLoadBalance(algorithm);

        RandomLoadBalance<String> algorithm2 = new RandomLoadBalance<>(true);
        algorithm2.buildInMap(initSample());
        countLoadBalance(algorithm2);
    }

    @Test
    public void roundRobinLoadBalanceTest() {
        Map<String, Integer> map = initSample();

        LoadBalance<String> algorithm = new RoundRobinLoadBalance<>();
        algorithm.buildInList(map.keySet());
        countLoadBalance(algorithm);

        WeightLoadBalance<String> algorithm2 = new RoundRobinLoadBalance<>(true);
        algorithm2.buildInMap(map);
        countLoadBalance(algorithm2);
    }

    @Test
    public void testDistribution() {

        List<String> nodes = new ArrayList<>(100);
        for (int i = 1; i <= 100; i++) {
            String item = "192.168.0." + "i";
            nodes.add(item);
        }

        ConsistentHashLoadBalancer<String> loadBalancer = new ConsistentHashLoadBalancer();
        // 构造 10000 随机请求
        List<ConsistentHashLoadBalancer.Invocation> invocations = new ArrayList<>();
        for (int i = 0; i < 10000; i++) {
            invocations.add(new ConsistentHashLoadBalancer.Invocation(UUID.randomUUID().toString()));
        }
        // 统计分布
        Map<String, Long> map = new LinkedHashMap<>();
        for (String node : nodes) {
            map.put(node, 0L);
        }
        for (ConsistentHashLoadBalancer.Invocation invocation : invocations) {
            String selectNode = loadBalancer.select(invocation);
            System.out.printf("server = %s, key = %s\n", selectNode, invocation.getHashKey());
            Long count = map.get(selectNode);
            count++;
            map.put(selectNode, count);
        }
        System.out.println(StatisticsUtil.variance(map.values().toArray(new Long[0])));
        System.out.println(StatisticsUtil.standardDeviation(map.values().toArray(new Long[] {})));
    }

    /**
     * 测试节点新增删除后的变化程度
     */
    @Test
    public void testNodeAddAndRemove() {
        // 构造 10000 随机请求
        List<ConsistentHashLoadBalancer.Invocation> invocations = new ArrayList<>();
        for (int i = 0; i < 10000; i++) {
            invocations.add(new ConsistentHashLoadBalancer.Invocation(UUID.randomUUID().toString()));
        }

        List<String> nodes = new ArrayList<>(100);
        for (int i = 1; i <= 100; i++) {
            String item = "192.168.0." + "i";
            nodes.add(item);
        }
        List<String> newNodes = nodes.subList(0, 80);
        ConsistentHashLoadBalancer<String> oldLoadBalance = new ConsistentHashLoadBalancer<>();
        oldLoadBalance.buildInList(nodes);
        ConsistentHashLoadBalancer<String> newLoadBalance = new ConsistentHashLoadBalancer<>();
        newLoadBalance.buildInList(newNodes);

        int count = 0;
        for (ConsistentHashLoadBalancer.Invocation invocation : invocations) {
            String oldNode = oldLoadBalance.select(invocation);
            String newNode = newLoadBalance.select(invocation);
            if (oldNode.equals(newNode)) count++;
        }
        System.out.println(count / 10000D);
    }

}
