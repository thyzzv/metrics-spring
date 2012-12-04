package com.ryantenney.metrics.spring;

import java.util.Map;

import com.yammer.metrics.core.MetricsRegistry;
import com.yammer.metrics.reporting.JmxReporter;

public class JmxReporterFactory {

	public static JmxReporter createInstance(final MetricsRegistry metrics) {
		final JmxReporter reporter = new JmxReporter(metrics);
		reporter.start();
		return reporter;
	}

	public static JmxReporter createInstance(final MetricsRegistry metrics, final Map<String, Object> properties) {
		return createInstance(metrics);
	}
}
