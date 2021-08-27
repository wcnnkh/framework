package io.basc.framework.mail.simple;

import io.basc.framework.core.utils.StringUtils;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;

public class SimpleMessage extends MimeMessage {
	private final SimpleProperties properties;
	
	public SimpleMessage(Session session, SimpleProperties properties) {
		super(session);
		this.properties = properties;
	}

	public SimpleProperties getProperties() {
		return properties;
	}
	
	@Override
	public void setSubject(String subject) throws MessagingException {
		String charset = properties.getCharset();
		if(StringUtils.isEmpty(charset)) {
			super.setSubject(subject);
		}else {
			setSubject(subject, charset);
		}
	}
}
