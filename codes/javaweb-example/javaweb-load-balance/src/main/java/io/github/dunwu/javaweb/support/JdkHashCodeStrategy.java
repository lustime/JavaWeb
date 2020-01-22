package io.github.dunwu.javaweb.support;

public class JdkHashCodeStrategy implements HashStrategy {

    @Override
    public int hashCode(String origin) {
        return origin.hashCode();
    }

}
