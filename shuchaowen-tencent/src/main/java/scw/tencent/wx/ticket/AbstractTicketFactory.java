package scw.tencent.wx.ticket;

import scw.security.Token;
import scw.tencent.wx.token.AccessTokenFactory;

public abstract class AbstractTicketFactory implements TicketFactory {
	private final AccessTokenFactory accessTokenFactory;
	private String type;

	/**
	 * @param accessTokenFactory
	 * @param type 一般使用jsapi
	 */
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
		Token ticket = getJsApiTicketByCache();
		if (ticket == null || ticket.isExpired()) {
			ticket = refreshJsApiTicket();
		}

		if (ticket == null) {
			throw new RuntimeException("无法获取ticket");
		}
		return ticket.getToken();
	}

	protected boolean isExpired() {
		Token ticket = getJsApiTicketByCache();
		return ticket == null || ticket.isExpired();
	}

	protected abstract Token refreshJsApiTicket();

	/**
	 * 从缓存中获取ticket
	 * 
	 * @return
	 */
	protected abstract Token getJsApiTicketByCache();
}
