package io.github.dunwu.javaweb;

import io.github.dunwu.tool.collection.CollectionUtil;
import io.github.dunwu.tool.map.MapUtil;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * (加权)轮询负载均衡策略
 *
 * @author <a href="mailto:forbreak@163.com">Zhang Peng</a>
 * @since 2020-01-20
 */
public class RoundRobinLoadBalance<V> implements WeightLoadBalance<V> {

    private boolean weightMode;

    private AtomicInteger offset = new AtomicInteger(0);

    private List<V> nodeList = Collections.emptyList();

    private Map<V, Integer> nodeMap = new LinkedHashMap<>();

    public RoundRobinLoadBalance() {
        this.weightMode = false;
    }

    public RoundRobinLoadBalance(boolean weightMode) {
        this.weightMode = weightMode;
    }

    @Override
    public void buildInList(final Collection<V> collection) {
        this.offset = new AtomicInteger(0);
        this.nodeList = new ArrayList<>(collection);
        this.nodeMap = new LinkedHashMap<>(collection.size());
        collection.forEach(item -> {
            nodeMap.putIfAbsent(item, 1);
        });
    }

    @Override
    public void buildInMap(final Map<V, Integer> map) {
        this.nodeList = new ArrayList<>(map.keySet());
        this.nodeMap = new LinkedHashMap<>(map);
    }

    @Override
    public V next() {
        if (weightMode) {
            return getNextInWeightMode();
        } else {
            return getNextInNormalMode();
        }
    }

    private V getNextInWeightMode() {
        if (MapUtil.isEmpty(nodeMap)) {
            return null;
        }

        int totalWeight = nodeMap.values().stream().mapToInt(a -> a).sum();
        int number = offset.getAndIncrement() % totalWeight;

        for (Map.Entry<V, Integer> item : nodeMap.entrySet()) {
            if (item.getValue() > number) {
                return item.getKey();
            }
            number -= item.getValue();
        }
        return null;
    }

    private V getNextInNormalMode() {
        if (CollectionUtil.isEmpty(this.nodeList)) {
            return null;
        }

        int size = this.nodeList.size();
        offset.compareAndSet(size, 0);
        return nodeList.get(offset.getAndIncrement());
    }

}
