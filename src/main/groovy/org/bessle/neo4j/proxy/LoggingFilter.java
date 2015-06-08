package org.bessle.neo4j.proxy;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

@Component
public class LoggingFilter implements Filter {

    private final Log logger = LogFactory.getLog(LoggingFilter.class);
    //private Auditor auditor;

    public void destroy() {
        // Nothing to do
    }

    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {
        ResettableStreamHttpServletRequest wrappedRequest = new ResettableStreamHttpServletRequest(
                (HttpServletRequest) request);
        // wrappedRequest.getInputStream().read();
        String body = IOUtils.toString(wrappedRequest.getReader());
        this.logger.debug("URI:"+wrappedRequest.getRequestURI()+" User:"+wrappedRequest.getUserPrincipal()+" body:"+body);
        wrappedRequest.resetInputStream();
        chain.doFilter(wrappedRequest, response);

    }

    public void init(FilterConfig arg0) throws ServletException {
        // Nothing to do
    }

    private class ResettableStreamHttpServletRequest extends
            HttpServletRequestWrapper {

        private byte[] rawData;
        private HttpServletRequest request;
        private ResettableServletInputStream servletStream;

        public ResettableStreamHttpServletRequest(HttpServletRequest request) {
            super(request);
            this.request = request;
            this.servletStream = new ResettableServletInputStream();
        }


        public void resetInputStream() {
            servletStream.stream = new ByteArrayInputStream(rawData);
        }

        @Override
        public ServletInputStream getInputStream() throws IOException {
            if (rawData == null) {
                rawData = IOUtils.toByteArray(this.request.getReader());
                servletStream.stream = new ByteArrayInputStream(rawData);
            }
            return servletStream;
        }

        @Override
        public BufferedReader getReader() throws IOException {
            if (rawData == null) {
                rawData = IOUtils.toByteArray(this.request.getReader());
                servletStream.stream = new ByteArrayInputStream(rawData);
            }
            return new BufferedReader(new InputStreamReader(servletStream));
        }


        private class ResettableServletInputStream extends ServletInputStream {

            private ByteArrayInputStream stream;

            @Override
            public int read() throws IOException {
                return stream.read();
            }

            @Override
            public boolean isReady() {
                return (stream.available() > 0);
            }

            @Override
            public void setReadListener(ReadListener listener) {
                logger.warn("ResettableServletInputStream.setReadListener() called but not implemented yet");
            }

            @Override
            public boolean isFinished() {
                return (stream.available() == 0);
            }
        }
    }

//    public void setAuditor(Auditor auditor) {
//        this.auditor = auditor;
//    }

}
