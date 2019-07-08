package scw.utils.tencent.weixin;

public abstract class AbstractTicketFactory implements TicketFactory {
	private final AccessTokenFactory accessTokenFactory;
	private String type;

	public AbstractTicketFactory(AccessTokenFactory accessTokenFactory, String type) {
		this.accessTokenFactory = accessTokenFactory;
		this.type = type;
	}

	public final AccessTokenFactory getAccessTokenFactory() {
		return accessTokenFactory;
	}
	
	public final String getType() {
		return type;
	}

	public final String getAppId() {
		return accessTokenFactory.getAppId();
	}

	public final String getAppSecret() {
		return accessTokenFactory.getAppSecret();
	}

	public final String getAccessToken() {
		return getAccessTokenFactory().getAccessToken();
	}

	public String getTicket() {
		Ticket ticket = getJsApiTicketByCache();
		if (ticket == null || ticket.isExpires()) {
			ticket = refreshJsApiTicket();
		}

		if (ticket == null) {
			throw new RuntimeException("无法获取ticket");
		}
		return ticket.getTicket();
	}

	protected boolean isExpires() {
		Ticket ticket = getJsApiTicketByCache();
		return ticket == null || ticket.isExpires();
	}

	protected abstract Ticket refreshJsApiTicket();

	/**
	 * 从缓存中获取ticket
	 * 
	 * @return
	 */
	protected abstract Ticket getJsApiTicketByCache();
}
