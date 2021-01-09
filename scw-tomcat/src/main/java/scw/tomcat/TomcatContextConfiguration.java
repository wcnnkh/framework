package scw.tomcat;

import org.apache.catalina.Context;

import scw.boot.MainApplication;

public interface TomcatContextConfiguration {
	void configuration(MainApplication application, Context context);
}
