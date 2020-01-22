package io.github.dunwu.javaweb;

import io.github.dunwu.javaweb.support.HashStrategy;
import io.github.dunwu.javaweb.support.MurmurHashStrategy;
import lombok.Data;

import java.util.*;

public class ConsistentHashLoadBalancer<V> {

    private HashStrategy hashStrategy = new MurmurHashStrategy();

    private final static int VIRTUAL_NODE_SIZE = 100;

    private final static String VIRTUAL_NODE_SUFFIX = "&&";

    private List<V> nodeList = Collections.emptyList();

    public void buildInList(final Collection<V> collection) {
        this.nodeList = new ArrayList<>(collection);
        // collection.forEach(item -> {
        //     keyWeightMap.put(item, DEFAULT_WEIGHT);
        // });
    }

    public V select(Invocation invocation) {
        int hashCode = hashStrategy.hashCode(invocation.getHashKey());
        TreeMap<Integer, V> hashRing = buildConsistentHashRing();
        return locate(hashRing, hashCode);
    }

    private V locate(TreeMap<Integer, V> ring, int hashCode) {
        // 向右找到第一个 key
        Map.Entry<Integer, V> entry = ring.ceilingEntry(hashCode);
        if (entry == null) {
            // 想象成一个环，超过尾部则取第一个 key
            entry = ring.firstEntry();
        }
        return entry.getValue();
    }

    private TreeMap<Integer, V> buildConsistentHashRing() {
        TreeMap<Integer, V> hashRing = new TreeMap<>();
        for (V node : nodeList) {
            for (int i = 0; i < VIRTUAL_NODE_SIZE; i++) {
                // 新增虚拟节点的方式如果有影响，也可以抽象出一个由物理节点扩展虚拟节点的类
                hashRing.put(hashStrategy.hashCode(node + VIRTUAL_NODE_SUFFIX + i), node);
            }
        }
        return hashRing;
    }

    @Data
    public static class Server {

        private String url;

        public Server(String url) {
            this.url = url;
        }

    }

    @Data
    public static class Invocation {

        private String hashKey;

        public Invocation(String hashKey) {
            this.hashKey = hashKey;
        }

    }

}
