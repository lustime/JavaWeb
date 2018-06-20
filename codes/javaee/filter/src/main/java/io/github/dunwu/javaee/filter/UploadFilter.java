package io.github.dunwu.javaee.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import io.github.dunwu.javaee.filter.wrapper.UploadRequestWrapper;

/**
 * @author Zhang Peng
 * @date 2017/4/4.
 */
public class UploadFilter extends MyFilter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
                    throws IOException, ServletException {
        UploadRequestWrapper uploadRequest = new UploadRequestWrapper((HttpServletRequest) request);
        chain.doFilter(uploadRequest, response);
    }
}
