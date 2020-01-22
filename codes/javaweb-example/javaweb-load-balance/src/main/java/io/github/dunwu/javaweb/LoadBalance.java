package io.github.dunwu.javaweb;

import java.util.Collection;

/**
 * 负载均衡策略接口
 *
 * @author <a href="mailto:forbreak@163.com">Zhang Peng</a>
 * @since 2020-01-21
 */
public interface LoadBalance<V> {

    void buildInList(Collection<V> collection);

    V next();

}
