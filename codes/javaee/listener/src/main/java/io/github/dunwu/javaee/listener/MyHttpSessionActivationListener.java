/**
 * The Apache License 2.0
 * Copyright (c) 2017 Zhang Peng
 */
package io.github.dunwu.javaee.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpSessionActivationListener;
import javax.servlet.http.HttpSessionEvent;
import java.io.Serializable;

/**
 * @author Zhang Peng
 * @date 2017/4/4.
 */
public class MyHttpSessionActivationListener implements HttpSessionActivationListener, Serializable {


    private static final long serialVersionUID = 1L;
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private String name;

    @Override
    public void sessionWillPassivate(HttpSessionEvent se) {

        logger.debug(name + "和session一起被序列化(钝化)到硬盘了，session的id是：" + se.getSession().getId());
    }

    @Override
    public void sessionDidActivate(HttpSessionEvent se) {
        logger.debug(name + "和session一起从硬盘反序列化(活化)回到内存了，session的id是：" + se.getSession().getId());
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public MyHttpSessionActivationListener(String name) {
        this.name = name;
    }
}
