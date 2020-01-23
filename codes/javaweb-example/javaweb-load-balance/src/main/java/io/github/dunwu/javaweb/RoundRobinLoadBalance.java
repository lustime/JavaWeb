package io.github.dunwu.javaweb;

import io.github.dunwu.tool.collection.CollectionUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * (加权)轮询负载均衡策略
 *
 * @author <a href="mailto:forbreak@163.com">Zhang Peng</a>
 * @since 2020-01-20
 */
public class RoundRobinLoadBalance<V extends Node> implements LoadBalance<V> {

    private boolean weightMode;

    private AtomicInteger offset = new AtomicInteger(0);

    private List<V> nodeList = Collections.emptyList();

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

        int totalWeight = nodeList.stream().mapToInt(Node::getWeight).sum();
        int number = offset.getAndIncrement() % totalWeight;

        for (V node : nodeList) {
            if (node.getWeight() > number) {
                return node;
            }
            number -= node.getWeight();
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
