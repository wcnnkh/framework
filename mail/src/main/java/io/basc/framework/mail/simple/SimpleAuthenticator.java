package io.basc.framework.mail.simple;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

public class SimpleAuthenticator extends Authenticator {
	private final SimpleProperties properties;

	public SimpleAuthenticator(SimpleProperties properties) {
		this.properties = properties;
	}

	@Override
	protected PasswordAuthentication getPasswordAuthentication() {
		return new PasswordAuthentication(properties.getUser(), properties.getPassword());
	}
}
