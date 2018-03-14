package com.sap.bulletinboard.statistics.util;

import java.lang.reflect.Field;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.ReflectionUtils.FieldCallback;

public class LoggerPostProcessor implements BeanPostProcessor {
    @Bean
    public Logger logger() {
        // real instance is set using below code
        return null;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        Logger logger = LoggerFactory.getLogger(bean.getClass());
        FieldCallback fieldSetter = field -> {
            field.setAccessible(true);
            field.set(bean, logger);
        };
        ReflectionUtils.doWithFields(bean.getClass(), fieldSetter, LoggerPostProcessor::isInjectLoggerField);
        return bean;
    }

    private static boolean isInjectLoggerField(Field field) {
        return (field.getAnnotation(Inject.class) != null) && (field.getType() == Logger.class);
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }
}