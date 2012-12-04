package com.ryantenney.metrics.spring.config;

import org.w3c.dom.Element;

import com.ryantenney.metrics.spring.JmxReporterFactory;

class JmxReporterBeanDefinitionParser extends ReporterBeanDefinitionParser {

	@Override
	protected String getBeanClassName(final Element element) {
		return JmxReporterFactory.class.getName();
	}
}
