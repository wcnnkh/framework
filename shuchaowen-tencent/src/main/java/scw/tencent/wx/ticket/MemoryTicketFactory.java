package scw.tencent.wx.ticket;

import scw.core.annotation.ParameterName;
import scw.tencent.wx.Ticket;
import scw.tencent.wx.WeiXinUtils;
import scw.tencent.wx.token.AccessTokenFactory;
import scw.tencent.wx.token.MemoryAccessTokenFactory;

public final class MemoryTicketFactory extends AbstractTicketFactory {
	private volatile Ticket ticket;
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
	protected Ticket getJsApiTicketByCache() {
		return ticket;
	}

	@Override
	protected Ticket refreshJsApiTicket() {
		if (isExpires()) {
			synchronized (lock) {
				if (isExpires()) {
					Ticket ticket = WeiXinUtils.getTicket(getAccessToken(), getType());
					if (ticket.isSuccess()) {
						this.ticket = ticket;
					}
				}
			}
		}
		return ticket;
	}
}
