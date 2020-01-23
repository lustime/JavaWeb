package io.github.dunwu.javaweb;

import io.github.dunwu.tool.collection.CollectionUtil;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * (加权)随机负载均衡策略
 *
 * @author <a href="mailto:forbreak@163.com">Zhang Peng</a>
 * @see <a href="https://www.cnblogs.com/CodeBear/archive/2019/03/11/10508880.html">Zhang Peng</a>
 * @since 2020-01-20
 */
public class RandomLoadBalance<V extends Node> implements LoadBalance<V> {

    private boolean weightMode;

    private final Random random = ThreadLocalRandom.current();

    private List<V> nodeList = Collections.emptyList();

    public RandomLoadBalance() {
        this.weightMode = false;
    }

    public RandomLoadBalance(boolean weightMode) {
        this.weightMode = weightMode;
    }

    @Override
    public void buildInList(final Collection<V> collection) {
        this.nodeList = new ArrayList<>(collection);
    }

    @Override
    public void addNode(V node) {
        this.nodeList.add(node);
    }

    @Override
    public void removeNode(V node) {
        this.nodeList.remove(node);
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
        if (CollectionUtil.isEmpty(nodeList)) {
            return null;
        }

        List<V> list = new ArrayList<>();
        for (V node : nodeList) {
            for (int i = 0; i < node.getWeight(); i++) {
                list.add(node);
            }
        }

        int totalWeight = nodeList.stream().mapToInt(Node::getWeight).sum();
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
