package io.github.dunwu.javaee.listener;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionAttributeListener;
import javax.servlet.http.HttpSessionBindingEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyHttpSessionAttributeListener implements HttpSessionAttributeListener {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 添加属性
     */
    @Override
    public void attributeAdded(HttpSessionBindingEvent se) {
        String name = se.getName();
        logger.debug("新建session属性：key={}, value={}", name, se.getValue());
    }

    /**
     * 删除属性
     */
    @Override
    public void attributeRemoved(HttpSessionBindingEvent se) {
        String name = se.getName();
        logger.debug("新建session属性：key={}, value={}", name, se.getValue());
    }

    /**
     * 修改属性
     */
    @Override
    public void attributeReplaced(HttpSessionBindingEvent se) {
        HttpSession session = se.getSession();
        String name = se.getName();
        logger.debug("新建session属性：key={}, 原value={}, 现value={}", name, se.getValue(),
                        session.getAttribute(name));
    }
}
