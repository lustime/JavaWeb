package io.github.dunwu.javaweb;

import java.util.Map;

/**
 * 加权负载均衡策略接口
 *
 * @author <a href="mailto:forbreak@163.com">Zhang Peng</a>
 * @since 2020-01-20
 */
public interface WeightLoadBalance<V> extends LoadBalance<V> {

    void buildInMap(Map<V, Integer> map);

}
