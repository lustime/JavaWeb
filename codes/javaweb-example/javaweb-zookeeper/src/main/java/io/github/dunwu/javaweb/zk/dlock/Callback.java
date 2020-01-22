package io.github.dunwu.javaweb.zk.dlock;

/**
 * Created by sunyujia@aliyun.com on 2016/2/23.
 */
public interface Callback<V> {

    V onGetLock() throws InterruptedException;

    V onTimeout() throws InterruptedException;

}
