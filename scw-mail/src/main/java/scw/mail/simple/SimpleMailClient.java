package scw.mail.simple;

import java.io.UnsupportedEncodingException;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;

import scw.context.annotation.Provider;
import scw.core.Assert;
import scw.core.Constants;
import scw.core.Ordered;
import scw.core.utils.ArrayUtils;
import scw.core.utils.StringUtils;
import scw.mail.MailClient;
import scw.mail.MailException;
import scw.mail.MessageCallback;

@Provider(order = Ordered.LOWEST_PRECEDENCE)
public class SimpleMailClient implements MailClient {
	private SimpleProperties properties;

	public SimpleMailClient(SimpleProperties properties) {
		this.properties = properties;
	}

	@Override
	public void transport(String subject, String content, String... toAddress) throws MailException {
		Assert.requiredArgument(!ArrayUtils.isEmpty(toAddress), "toAddress");
		String charset = properties.getCharset();
		Address[] addresses = new Address[toAddress.length];
		for (int i = 0; i < toAddress.length; i++) {
			try {
				addresses[i] = new InternetAddress(toAddress[i], toAddress[i], charset);
			} catch (UnsupportedEncodingException e) {
				throw new MailException(e);
			}
		}

		transport(new MessageCallback() {

			@Override
			public void callback(Message message) throws MessagingException, MailException {
				message.setRecipients(RecipientType.TO, addresses);
				message.setSubject(subject);
				message.setContent(content,
						"text/html; charset=" + (StringUtils.isEmpty(charset) ? Constants.UTF_8_NAME : charset));
			}
		});
	}

	@Override
	public void transport(MessageCallback callback) throws MailException {
		SimpleAuthenticator authenticator = new SimpleAuthenticator(properties);
		Session session = Session.getDefaultInstance(properties, authenticator.isVerified() ? authenticator : null);
		SimpleMessage message = new SimpleMessage(session, properties);
		Address from = null;
		String user = properties.getUser();
		if (StringUtils.isNotEmpty(user)) {
			try {
				from = new InternetAddress(user, user, properties.getCharset());
			} catch (UnsupportedEncodingException e) {
				throw new MailException(e);
			}
		}

		try {
			message.setFrom(from);

			callback.callback(message);
			message.saveChanges();
		} catch (MessagingException e) {
			throw new MailException(e);
		}

		try {
			Transport.send(message);
		} catch (MessagingException e) {
			throw new MailException(e);
		}
	}
}
