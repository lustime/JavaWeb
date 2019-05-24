package io.github.dunwu.javaee.filter;

import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

/**
 * @author Zhang Peng
 * @date 2017/3/28.
 */
public class XSLTFilter extends MyFilter {

	private ServletContext servletContext;

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		super.init(filterConfig);
		servletContext = filterConfig.getServletContext();
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		logger.info("{} 开始做过滤处理", this.getClass().getName());

		HttpServletRequest httpServletRequest = (HttpServletRequest) request;
		HttpServletResponse httpServletResponse = (HttpServletResponse) response;

		// 格式样本文件：/book.xsl
		Source styleSource = new StreamSource(servletContext.getRealPath("/views/xml/messageLog.xsl"));

		// 请求的 xml 文件
		Source xmlSource = new StreamSource(servletContext
				.getRealPath(httpServletRequest.getRequestURI().replace(httpServletRequest.getContextPath() + "", "")));
		try {

			// 转换器工厂
			TransformerFactory transformerFactory = TransformerFactory.newInstance();

			// 转换器
			Transformer transformer = transformerFactory.newTransformer(styleSource);

			// 将转换的结果保存到该对象中
			CharArrayWriter charArrayWriter = new CharArrayWriter();
			StreamResult result = new StreamResult(charArrayWriter);

			// 转换
			transformer.transform(xmlSource, result);

			// 输出转换后的结果
			httpServletResponse.setContentType("text/html");
			httpServletResponse.setContentLength(charArrayWriter.toString().length());
			PrintWriter out = httpServletResponse.getWriter();
			out.write(charArrayWriter.toString());

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
