package org.example.demo.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.tomcat.util.buf.ByteChunk;
import org.apache.tomcat.util.buf.UDecoder;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.logging.Logger;

//@Component
//public class SimpleFilter implements Filter {
//
//    private final UDecoder urlDecoder = new UDecoder();
//
//    @Override
//    public void init(FilterConfig filterConfig) throws ServletException {
//    }
//
//    @Override
//    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
//            throws IOException, ServletException {
//        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
//        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
//
//        String queryString = httpServletRequest.getQueryString();
//        if (queryString != null) {
//            ByteChunk byteChunk = new ByteChunk();
//            byteChunk.setBytes(queryString.getBytes(), 0, queryString.length());
//            try {
//                urlDecoder.convert(byteChunk, true);
//            } catch (IOException ioException) {
//                System.out.println("Hazarduos character found in request parameter.");
//                httpServletResponse.setStatus(HttpStatus.BAD_REQUEST.value());
//                return;
//            }
//        }
//        chain.doFilter(request, response);
//    }
//
//    @Override
//    public void destroy() {
//    }
//
//}