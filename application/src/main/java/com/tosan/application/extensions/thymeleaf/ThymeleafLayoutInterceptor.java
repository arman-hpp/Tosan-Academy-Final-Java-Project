package com.tosan.application.extensions.thymeleaf;

import jakarta.servlet.http.*;
import org.springframework.lang.Nullable;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.*;

public class ThymeleafLayoutInterceptor implements HandlerInterceptor {
    private static final String DEFAULT_LAYOUT = "layouts/default";
    private static final String DEFAULT_VIEW_ATTRIBUTE_NAME = "view";
    private static final String DEFAULT_TITLE_ATTRIBUTE_NAME = "title";

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable ModelAndView modelAndView) {
        if (modelAndView == null || !modelAndView.hasView()) {
            return;
        }

        var originalViewName = modelAndView.getViewName();
        if(originalViewName == null) {
            return;
        }

        if(originalViewName.equals("error")) {
            modelAndView.setViewName(originalViewName);
            return;
        }

        if (isRedirectOrForward(originalViewName)) {
            return;
        }

        var layoutName = getLayoutName(handler);
        if(layoutName == null) {
            return;
        }

        if (Layout.NONE.equals(layoutName)) {
            return;
        }

        var layoutTitle = getLayoutTitle(handler);

        modelAndView.setViewName(layoutName);
        modelAndView.addObject(DEFAULT_VIEW_ATTRIBUTE_NAME, originalViewName);
        modelAndView.addObject(DEFAULT_TITLE_ATTRIBUTE_NAME, layoutTitle);
    }

    private boolean isRedirectOrForward(String viewName) {
        return viewName.startsWith("redirect:") || viewName.startsWith("forward:");
    }

    private String getLayoutName(Object handler) {
        if (handler instanceof HandlerMethod handlerMethod) {
            var layout = getMethodOrTypeAnnotation(handlerMethod);
            if (layout != null) {
                return layout.value();
            }
        }

        return null;
    }

    private String getLayoutTitle(Object handler) {
        if (handler instanceof HandlerMethod handlerMethod) {
            var layout = getMethodOrTypeAnnotation(handlerMethod);
            if (layout != null) {
                return layout.title();
            }
        }

        return DEFAULT_LAYOUT;
    }

    private Layout getMethodOrTypeAnnotation(HandlerMethod handlerMethod) {
        var layout = handlerMethod.getMethodAnnotation(Layout.class);
        if (layout == null) {
            return handlerMethod.getBeanType().getAnnotation(Layout.class);
        }

        return layout;
    }
}