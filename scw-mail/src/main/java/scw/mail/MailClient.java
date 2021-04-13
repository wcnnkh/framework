package scw.mail;

public interface MailClient {

	/**
	 * 传输邮件
	 * 
	 * @param subject   标题
	 * @param content   内容
	 * @param toAddress 收件人
	 * @throws MailException
	 */
	void transport(String subject, String content, String... toAddress) throws MailException;

	void transport(MessageCallback callback) throws MailException;
}
