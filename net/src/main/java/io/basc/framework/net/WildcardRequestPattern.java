package io.basc.framework.net;

import io.basc.framework.core.convert.transform.stereotype.Properties;

public class WildcardRequestPattern implements RequestPattern {
	private final MimeTypeRegistry consumes = new MimeTypeRegistry();
	private final MimeTypeRegistry produces = new MimeTypeRegistry();

	@Override
	public Properties apply(Request request) {
		return Properties.EMPTY_PROPERTIES;
	}

	@Override
	public MimeTypeRegistry getConsumes() {
		return consumes;
	}

	@Override
	public MimeTypeRegistry getProduces() {
		return produces;
	}

}
