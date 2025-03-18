package run.soeasy.framework.messaging.convert.support;

import run.soeasy.framework.messaging.convert.MessageConverters;

public class DefaultMessageConverters extends MessageConverters {

	public DefaultMessageConverters() {
		setLast(GlobalMessageConverters.getInstance());
	}
}
