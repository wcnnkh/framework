package io.basc.framework.mail;

public interface MailClient {
	void transport(String subject, String content, String... toAddress) throws MailException;

	void transport(MessageCallback callback) throws MailException;
}
