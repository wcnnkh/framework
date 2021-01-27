package scw.tomcat;

import org.apache.catalina.Context;

import scw.boot.ConfigurableApplication;

public interface TomcatContextConfiguration {
	void configuration(ConfigurableApplication application, Context context);
}
