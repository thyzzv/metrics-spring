/*
 * Copyright 2012 Jonathan Pearlin and Dealer.com (developer.dealer.com).
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

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.PropertyValue;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.w3c.dom.Element;

import com.yammer.metrics.Metrics;

/**
 * Generic Yammer Metrics Reporter bean definition parser.  This parser assumes the following conventions in
 * order to create the Yammer Metrics reporter:
 *
 * <ul>
 * 	<li>The existence of a factory class in the same package as the reporter class.</li>
 *  <li>The existence of a factory class named after the reporter class with "Factory" appended to the end.</li>
 *  <li>The existence of a public, static method named "createInstance" in the factory that takes two parameters:  MetricsRegistry and Map<String, Object>.</li>
 *  <li>Any additional arguments (the Map<String,Object> parameter to the "createInstance" method) as Spring property values (the "p-namespace").</li>
 * </ul>
 *
 * An example of a Spring bean definition using this parser would take the following form:
 * <br />
 * <br />
 * <code>
 * 	&lt;metrics:reporter id="someId" class="com.yammer.metrics.reporting.SomeNewReporter" metrics="metrics" p:host="localhost" p:port="5555" /&gt;
 * </code>
 * <br />
 * <br />
 *
 * @author Jonathan Pearlin
 * @since 2.1.5
 */
public class ReporterBeanDefinitionParser extends AbstractSingleBeanDefinitionParser {

	/**
	 * Name of the XML attribute that contains the fully-qualified reporter class.
	 */
	protected static final String CLASS_ATTRIBUTE_NAME = "class";

	/**
	 * Format string used to create the conventional name of the factory class based on the reporter class.
	 */
	protected static final String FACTORY_FORMAT_STRING = "%sFactory";

	/**
	 * The name of the factory method used to create an instance of the requested reporter class.
	 */
	protected static final String FACTORY_METHOD_NAME = "createInstance";

	/**
	 * Name of the XML attribute that contains the reference to the Yammer Metrics metrics registry bean.
	 */
	protected static final String METRICS_REGISTRY_ATTRIBUTE_NAME = "metrics-registry";

	@Override
	protected String getBeanClassName(final Element element)  {
		return String.format(FACTORY_FORMAT_STRING, element.getAttribute(CLASS_ATTRIBUTE_NAME));
	}

	@Override
	protected boolean shouldGenerateIdAsFallback() {
		return true;
	}

	@Override
	protected void doParse(final Element element, final BeanDefinitionBuilder builder) {
		builder.setFactoryMethod(FACTORY_METHOD_NAME);
		final String metricsRegistryRef = element.getAttribute(METRICS_REGISTRY_ATTRIBUTE_NAME);
		if(metricsRegistryRef != null && !metricsRegistryRef.trim().isEmpty()) {
			builder.addConstructorArgReference(metricsRegistryRef);
		} else {
			builder.addConstructorArgValue(Metrics.defaultRegistry());
		}
		builder.addConstructorArgValue(getProperties(builder.getBeanDefinition().getPropertyValues().getPropertyValues()));
	}

	/**
	 * Copies the properties specified on the bean using the {@code p:} name space into a map, using the
	 * property's name as the key.
	 *
	 * @param propertyValues The array of {@code PropertyValue} instances from the bean's definition.
	 * @return A {@code Map} containing all the provided property values or an empty map if no such values
	 * 	exist.
	 */
	private Map<String, Object> getProperties(final PropertyValue[] propertyValues) {
		final Map<String, Object> properties = new HashMap<String, Object>();
		for(final PropertyValue propertyValue : propertyValues) {
			properties.put(propertyValue.getName(), propertyValue.getValue());
		}
		return properties;
	}
}
