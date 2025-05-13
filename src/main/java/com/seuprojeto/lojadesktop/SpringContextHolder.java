package com.seuprojeto.lojadesktop;

import org.springframework.context.ConfigurableApplicationContext;
public class SpringContextHolder {
    private static ConfigurableApplicationContext context;

    public static void setContext(ConfigurableApplicationContext ctx) {
        context = ctx;
    }

    public static ConfigurableApplicationContext getContext() {
        return context;
    }
}

