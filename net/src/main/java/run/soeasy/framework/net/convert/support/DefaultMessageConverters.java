package run.soeasy.framework.net.convert.support;

import run.soeasy.framework.net.convert.MessageConverters;

public class DefaultMessageConverters extends MessageConverters {

	public DefaultMessageConverters() {
		setLast(GlobalMessageConverters.getInstance());
	}
}
