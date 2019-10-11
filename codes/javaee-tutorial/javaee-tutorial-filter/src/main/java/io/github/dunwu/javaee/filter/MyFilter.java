/*
 *
 */
package io.github.dunwu.javaee.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import java.io.IOException;

/**
 * @author <a href="mailto:forbreak@163.com">Zhang Peng</a>
 * @date 2017/3/27.
 */
public abstract class MyFilter implements Filter {

	protected final Logger logger = LoggerFactory.getLogger(this.getClass());

	private String filterName;

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		// 获取 Filter 的 name，配置在 web.xml 中
		filterName = filterConfig.getFilterName();
		logger.info("启动 Filter: {}", filterName);
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
	}

	@Override
	public void destroy() {
		logger.info("关闭 Filter: {}", filterName);
	}

}
