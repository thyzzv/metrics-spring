/*
 * Copyright 2012 Ryan W Tenney (http://ryan.10e.us)
 *            and Martello Technologies (http://martellotech.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ryantenney.metrics.spring.config;

import com.yammer.metrics.MetricRegistry;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

import java.beans.SimpleBeanInfo;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

class ReporterFactory<T> implements FactoryBean<T> {

    private final MetricRegistry metricRegistry;
    private final Class<T> clazz;
    private final MutablePropertyValues propertyValues;

    private volatile T instance;

    public ReporterFactory(final MetricRegistry metricRegistry, final Class<T> clazz, final MutablePropertyValues propertyValues) {
        this.metricRegistry = metricRegistry;
        this.clazz = clazz;
        this.propertyValues = propertyValues;
    }

    @Override
    @SuppressWarnings("unchecked")
    public synchronized T getObject() throws Exception {
        if (instance == null) {
            Method forRegistry = clazz.getMethod("forRegistry", MetricRegistry.class);
            Object builder = forRegistry.invoke(null, metricRegistry);
            Class<?> builderClass = builder.getClass();
            for (Method method : builderClass.getMethods()) {
                String name = method.getName();
                if (Modifier.isStatic(method.getModifiers()) || name.equals("build")) continue;
                PropertyValue value = propertyValues.getPropertyValue(name);
                if (value != null) {
                    method.invoke(builder, value.getConvertedValue());
                }
            }
            Method buildMethod = builderClass.getMethod("build");
            instance = (T) buildMethod.invoke(builder);
        }
        return instance;
    }

    @Override
    public Class<?> getObjectType() {
        return clazz;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

}