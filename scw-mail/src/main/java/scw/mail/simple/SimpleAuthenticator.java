package scw.mail.simple;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

import scw.core.utils.StringUtils;
import scw.util.Verify;

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
