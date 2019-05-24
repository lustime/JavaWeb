/**
 * The Apache License 2.0 Copyright (c) 2017 Zhang Peng
 */
package io.github.dunwu.javaee.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;

import javax.servlet.ServletContextAttributeEvent;
import javax.servlet.ServletContextAttributeListener;

/**
 * @author Zhang Peng
 * @date 2017/4/4.
 */
public class MyServletContextAttributeListener implements ServletContextAttributeListener {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void attributeAdded(ServletContextAttributeEvent scab) {
        logger.debug("ServletContext域对象中添加了属性:{}，属性值是:{}", scab.getName(), scab.getValue());
    }

    @Override
    public void attributeRemoved(ServletContextAttributeEvent scab) {
        logger.debug("ServletContext域对象中删除了属性:{}，属性值是:{}", scab.getName(), scab.getValue());
    }

    @Override
    public void attributeReplaced(ServletContextAttributeEvent scab) {
        logger.debug("ServletContext域对象中替换了属性:{}，原值是:{}， 现值是:{}",
                scab.getName(), scab.getSource(), scab.getValue());
    }
}
