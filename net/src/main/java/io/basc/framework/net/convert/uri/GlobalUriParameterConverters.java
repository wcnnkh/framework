package io.basc.framework.net.convert.uri;

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
