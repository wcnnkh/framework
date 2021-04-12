package scw.mail;


public class SmtpProperties extends SimpleProperties {
	private static final long serialVersionUID = 1L;
	private static final String TRANSPORT_PROTOCOL_NAME = "smtp";
	
	public static final String SMTP_AUTH = "mail.smtp.auth";
	public static final String SMTP_SSL_ENABLE = "mail.smtp.ssl.enable";
	public static final String SMTP_SSL_SOCKET_FACTORY = "mail.smtp.ssl.socketFactory";
	
	public SmtpProperties(){
		setTransportProtocol(TRANSPORT_PROTOCOL_NAME);
	}

	public Boolean getAuth() {
		return (Boolean) get(SMTP_AUTH);
	}

	public void setAuth(Boolean smtpAuth) {
		put(SMTP_AUTH, smtpAuth);
	}

	public Boolean getSslEnable(){
		return (Boolean) get(SMTP_SSL_ENABLE);
	}
	
	public void setSslEnable(Boolean sslEnable){
		put(SMTP_SSL_ENABLE, sslEnable);
	}
	
	public Object getSslSocketFactory(){
		return get(SMTP_SSL_SOCKET_FACTORY);
	}
	
	public void setSslSocketFactory(Object sslSocketFactory){
		put(SMTP_SSL_SOCKET_FACTORY, sslSocketFactory);
	}
}
