/**
 * The Apache License 2.0 Copyright (c) 2017 Zhang Peng
 */
package io.github.dunwu.javaee.taglib;

/**
 * @author <a href="mailto:forbreak@163.com">Zhang Peng</a>
 * @date 2017/4/3.
 */

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.SimpleTagSupport;
import java.io.IOException;

public class HelloTag extends SimpleTagSupport {

	@Override
	public void doTag() throws JspException, IOException {
		JspWriter out = getJspContext().getOut();
		out.println("Hello Custom Tag!");
	}

}
