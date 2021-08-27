package io.basc.framework.tomcat;

import io.basc.framework.boot.ConfigurableApplication;

import org.apache.catalina.Context;

public interface TomcatContextConfiguration {
	void configuration(ConfigurableApplication application, Context context);
}
