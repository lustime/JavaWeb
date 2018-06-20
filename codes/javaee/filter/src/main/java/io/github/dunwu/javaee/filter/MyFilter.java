/*
 *
 */
package io.github.dunwu.javaee.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Zhang Peng
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
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
	}

	@Override
	public void destroy() {
		logger.info("关闭 Filter: {}", filterName);
	}
}
