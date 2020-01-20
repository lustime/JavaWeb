package io.github.dunwu.javaweb.zk.sequence;

/**
 * Created by sunyujia@aliyun.com on 2016/2/25.
 */
public interface DistributedSequence {

    public Long sequence(String sequenceName);
}
