package io.github.dunwu.javaee.listener;

import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyHttpSessionBindingListener implements HttpSessionBindingListener {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void valueBound(HttpSessionBindingEvent event) {
        logger.debug("HttpSessionBinding valueBound");
    }

    @Override
    public void valueUnbound(HttpSessionBindingEvent event) {
        logger.debug("HttpSessionBinding valueUnbound");
    }

}
