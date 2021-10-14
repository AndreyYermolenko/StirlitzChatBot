package ru.yermolenko.filter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

public class ContentTypeRequestWrapper extends HttpServletRequestWrapper {

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
