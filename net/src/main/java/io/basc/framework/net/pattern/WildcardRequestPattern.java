package io.basc.framework.net.pattern;

import io.basc.framework.core.convert.transform.stereotype.Properties;
import io.basc.framework.net.MimeTypeRegistry;
import io.basc.framework.net.Request;

public class WildcardRequestPattern implements RequestPattern {
	private final MimeTypeRegistry consumes = new MimeTypeRegistry();
	private final MimeTypeRegistry produces = new MimeTypeRegistry();

	@Override
	public Properties apply(Request request) {
		// TODO
		return null;
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
