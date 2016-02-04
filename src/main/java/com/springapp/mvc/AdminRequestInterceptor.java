package com.springapp.mvc;

import entities.User;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import util.Constants;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by LogiX on 2015-12-17.
 */

public class AdminRequestInterceptor extends HandlerInterceptorAdapter implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {
        //Check for session
        User user = (User) httpServletRequest.getSession().getAttribute("user");
        if (user == null) {
            httpServletResponse.sendRedirect("/index");
            return false;
        } else {
            //Check admin user level
            if (user.getLevel() != Constants.ADMIN && user.getLevel() != Constants.TEACHER) {
                httpServletRequest.getSession().invalidate();
                httpServletResponse.sendRedirect("/index");
                return false;
            } else {
                return true;
            }
        }
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {

    }
}

