package io.basc.framework.net.convert.support;

import io.basc.framework.net.convert.UriParameterConverters;

public class DefaultUriParameterConverters extends UriParameterConverters {

	public DefaultUriParameterConverters() {
		setLast(GlobalUriParameterConverters.getInstance());
	}
}
