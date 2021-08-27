package io.basc.framework.mail.simple;

import io.basc.framework.core.utils.StringUtils;
import io.basc.framework.util.Verify;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

public class SimpleAuthenticator extends Authenticator implements Verify {
	private final SimpleProperties properties;

	public SimpleAuthenticator(SimpleProperties properties) {
		this.properties = properties;
	}

	@Override
	protected PasswordAuthentication getPasswordAuthentication() {
		return new PasswordAuthentication(properties.getUser(), properties.getPassword());
	}

	@Override
	public boolean isVerified() {
		return StringUtils.isNotEmpty(properties.getUser());
	}
}
