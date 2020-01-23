package io.github.dunwu.javaweb;

import io.github.dunwu.javaweb.support.HashStrategy;
import io.github.dunwu.javaweb.support.MurmurHashStrategy;

import java.util.*;
import java.util.function.Predicate;

public class ConsistentHashLoadBalance<V extends Node> implements LoadBalance<V> {

    private HashStrategy hashStrategy = new MurmurHashStrategy();

    private final static int VIRTUAL_NODE_SIZE = 1000;

    private final static String VIRTUAL_NODE_SUFFIX = "&&";

    private List<V> nodeList = Collections.emptyList();

    private TreeMap<Integer, V> hashRing;

    @Override
    public void buildInList(final Collection<V> collection) {
        this.nodeList = new ArrayList<>(collection);
        this.hashRing = buildConsistentHashRing(this.nodeList);
    }

    @Override
    public void addNode(V node) {
        this.nodeList.add(node);
        this.hashRing = buildConsistentHashRing(this.nodeList);
    }

    @Override
    public void removeNode(V node) {
        this.nodeList.removeIf(v -> v.equals(node));
        this.hashRing = buildConsistentHashRing(this.nodeList);
    }

    @Override
    public V next() {
        return next(UUID.randomUUID().toString());
    }

    public V next(String key) {
        int hashCode = hashStrategy.hashCode(key);
        // 向右找到第一个 key
        Map.Entry<Integer, V> entry = hashRing.ceilingEntry(hashCode);
        if (entry == null) {
            // 想象成一个环，超过尾部则取第一个 key
            entry = hashRing.firstEntry();
        }
        return entry.getValue();
    }

    private TreeMap<Integer, V> buildConsistentHashRing(List<V> nodeList) {
        TreeMap<Integer, V> hashRing = new TreeMap<>();
        for (V node : nodeList) {
            for (int i = 0; i < VIRTUAL_NODE_SIZE; i++) {
                // 新增虚拟节点的方式如果有影响，也可以抽象出一个由物理节点扩展虚拟节点的类
                hashRing.put(hashStrategy.hashCode(node + VIRTUAL_NODE_SUFFIX + i), node);
            }
        }
        return hashRing;
    }

}
