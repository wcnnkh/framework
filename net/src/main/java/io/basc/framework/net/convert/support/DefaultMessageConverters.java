package io.basc.framework.net.convert.support;

import io.basc.framework.net.convert.MessageConverters;

public class DefaultMessageConverters extends MessageConverters {

	public DefaultMessageConverters() {
		setLast(GlobalMessageConverters.getInstance());
	}
}
