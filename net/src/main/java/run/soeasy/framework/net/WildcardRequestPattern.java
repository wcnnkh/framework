package run.soeasy.framework.net;

import run.soeasy.framework.core.convert.transform.stereotype.Properties;

public class WildcardRequestPattern implements RequestPattern {
	private final MediaTypeRegistry consumes = new MediaTypeRegistry();
	private final MediaTypeRegistry produces = new MediaTypeRegistry();

	@Override
	public Properties apply(Request request) {
		return Properties.EMPTY_PROPERTIES;
	}

	@Override
	public MediaTypeRegistry getConsumes() {
		return consumes;
	}

	@Override
	public MediaTypeRegistry getProduces() {
		return produces;
	}

}
