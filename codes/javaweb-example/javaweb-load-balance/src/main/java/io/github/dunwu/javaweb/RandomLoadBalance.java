package io.github.dunwu.javaweb;

import io.github.dunwu.tool.collection.CollectionUtil;
import io.github.dunwu.tool.map.MapUtil;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * (加权)随机负载均衡策略
 *
 * @author <a href="mailto:forbreak@163.com">Zhang Peng</a>
 * @see <a href="https://www.cnblogs.com/CodeBear/archive/2019/03/11/10508880.html">Zhang Peng</a>
 * @since 2020-01-20
 */
public class RandomLoadBalance<V> implements WeightLoadBalance<V> {

    public static final Integer DEFAULT_WEIGHT = 1;

    private boolean weightMode;

    private final Random random = ThreadLocalRandom.current();

    private List<V> nodeList = Collections.emptyList();

    private final Map<V, Integer> keyWeightMap = new HashMap<>();

    public RandomLoadBalance() {
        this.weightMode = false;
    }

    public RandomLoadBalance(boolean weightMode) {
        this.weightMode = weightMode;
    }

    @Override
    public void buildInList(final Collection<V> collection) {
        this.nodeList = new ArrayList<>(collection);
        collection.forEach(item -> {
            keyWeightMap.put(item, DEFAULT_WEIGHT);
        });
    }

    @Override
    public void buildInMap(final Map<V, Integer> map) {
        this.nodeList = new ArrayList<>(map.keySet());
        this.keyWeightMap.putAll(map);
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
        if (MapUtil.isEmpty(keyWeightMap)) {
            return null;
        }

        List<V> list = new ArrayList<>();
        for (Map.Entry<V, Integer> item : keyWeightMap.entrySet()) {
            for (int i = 0; i < item.getValue(); i++) {
                list.add(item.getKey());
            }
        }

        int totalWeight = keyWeightMap.values().stream().mapToInt(a -> a).sum();
        int number = random.nextInt(totalWeight);
        return list.get(number);
    }

    private V getNextInNormalMode() {
        if (CollectionUtil.isEmpty(nodeList)) {
            return null;
        }

        int offset = random.nextInt(nodeList.size());
        return nodeList.get(offset);
    }

}
