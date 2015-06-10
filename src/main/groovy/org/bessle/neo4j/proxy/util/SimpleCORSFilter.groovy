package org.bessle.neo4j.proxy.util;

import javax.servlet.Filter
import javax.servlet.FilterChain
import javax.servlet.FilterConfig
import javax.servlet.ServletException
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletResponse

import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

@Component
@Slf4j
public class SimpleCORSFilter implements Filter {

	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
		HttpServletResponse response = (HttpServletResponse) res
        chain.doFilter(req, res);
        if (! response.containsHeader("Access-Control-Allow-Origin")) {
            response.setHeader("Access-Control-Allow-Origin", "*")
            response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE")
            response.setHeader("Access-Control-Allow-Headers", "accept, content-type")
            log.debug("SimpleCORSFilter: CORS Header added to ${req.getLocalAddr()}")
        } else {
            log.debug("SimpleCORSFilter: CORS Header already included in response")
        }
	}

	public void init(FilterConfig filterConfig) {
        log.info("SimpleCORSFilter.init()")
    }

	public void destroy() {}

}
