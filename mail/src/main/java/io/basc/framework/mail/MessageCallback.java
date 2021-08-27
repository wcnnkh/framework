package io.basc.framework.mail;

import javax.mail.Message;
import javax.mail.MessagingException;

@FunctionalInterface
public interface MessageCallback {
	void callback(Message message) throws MessagingException, MailException;
}
