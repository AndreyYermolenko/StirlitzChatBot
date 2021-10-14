package ru.yermolenko.filter;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ForcedApplicationJsonContentTypeFilter implements Filter {

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
        chain.doFilter(new ContentTypeRequestWrapper((HttpServletRequest) req), resp);
    }

    private static class ContentTypeRequestWrapper extends HttpServletRequestWrapper {

        public ContentTypeRequestWrapper(HttpServletRequest request) {
            super(request);
        }

        @Override
        public String getContentType() {
            return "application/json";
        }

        @Override
        public String getHeader(String name) {
            if (name.equalsIgnoreCase("content-type")) {
                return "application/json";
            }
            return super.getHeader(name);
        }

        @Override
        public Enumeration<String> getHeaderNames() {
            List<String> headerNames = Collections.list(super.getHeaderNames());
            if (!headerNames.contains("content-type")) {
                headerNames.add("content-type");
                return Collections.enumeration(headerNames);
            }
            return super.getHeaderNames();
        }

        @Override
        public Enumeration <String> getHeaders(String name) {
            if (name.equalsIgnoreCase("content-type")) {
                return Collections.enumeration(Collections.singletonList("application/json"));
            }
            return super.getHeaders(name);
        }
    }
}
