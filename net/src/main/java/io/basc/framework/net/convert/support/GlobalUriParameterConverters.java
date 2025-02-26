package io.basc.framework.net.convert.support;

import io.basc.framework.net.convert.UriParameterConverters;

public class GlobalUriParameterConverters extends UriParameterConverters {
	private static volatile UriParameterConverters instance;

	public static UriParameterConverters getInstance() {
		if (instance == null) {
			synchronized (GlobalUriParameterConverters.class) {
				if (instance == null) {
					instance = new GlobalUriParameterConverters();
					instance.doNativeConfigure();
				}
			}
		}
		return instance;
	}
	
	private GlobalUriParameterConverters() {
	}
}
