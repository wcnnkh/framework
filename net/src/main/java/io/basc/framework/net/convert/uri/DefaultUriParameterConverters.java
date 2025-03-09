package io.basc.framework.net.convert.uri;

public class DefaultUriParameterConverters extends UriParameterConverters {

	public DefaultUriParameterConverters() {
		setLast(GlobalUriParameterConverters.getInstance());
	}
}
