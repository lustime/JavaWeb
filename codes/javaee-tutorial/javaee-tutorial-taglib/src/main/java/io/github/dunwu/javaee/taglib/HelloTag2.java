/**
 * The Apache License 2.0 Copyright (c) 2017 Zhang Peng
 */
package io.github.dunwu.javaee.taglib;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.SimpleTagSupport;
import java.io.IOException;
import java.io.StringWriter;

/**
 * @author <a href="mailto:forbreak@163.com">Zhang Peng</a>
 * @date 2017/4/3.
 */
public class HelloTag2 extends SimpleTagSupport {

	StringWriter sw = new StringWriter();

	public void doTag() throws JspException, IOException {
		getJspBody().invoke(sw);
		getJspContext().getOut().println(sw.toString());
	}

}
