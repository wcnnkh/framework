package scw.tencent.wx.ticket;

import scw.core.parameter.annotation.ParameterName;
import scw.security.Token;
import scw.tencent.wx.WeiXinUtils;
import scw.tencent.wx.token.AccessTokenFactory;
import scw.tencent.wx.token.MemoryAccessTokenFactory;

public final class MemoryTicketFactory extends AbstractTicketFactory {
	private volatile Token ticket;
	private volatile Object lock = new Object();

	public MemoryTicketFactory(@ParameterName(WX_APPID_KEY) String appid,
			@ParameterName(WX_APPSECRET_KEY) String appsecret) {
		this(appid, appsecret, "jsapi");
	}

	public MemoryTicketFactory(@ParameterName(WX_APPID_KEY) String appid,
			@ParameterName(WX_APPSECRET_KEY) String appsecret, @ParameterName(WX_TICKET_TYPE) String type) {
		this(new MemoryAccessTokenFactory(appid, appsecret), type);
	}

	public MemoryTicketFactory(AccessTokenFactory accessTokenFactory) {
		this(accessTokenFactory, "jsapi");
	}

	public MemoryTicketFactory(AccessTokenFactory accessTokenFactory, String type) {
		super(accessTokenFactory, type);
	}

	@Override
	protected Token getJsApiTicketByCache() {
		return ticket;
	}

	@Override
	protected Token refreshJsApiTicket() {
		if (isExpired()) {
			synchronized (lock) {
				if (isExpired()) {
					this.ticket = WeiXinUtils.getTicket(getAccessToken(), getType());
				}
			}
		}
		return ticket;
	}
}
