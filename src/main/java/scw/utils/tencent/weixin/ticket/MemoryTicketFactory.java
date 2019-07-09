package scw.utils.tencent.weixin.ticket;

import scw.utils.tencent.weixin.Ticket;
import scw.utils.tencent.weixin.WeiXinUtils;
import scw.utils.tencent.weixin.token.AccessTokenFactory;
import scw.utils.tencent.weixin.token.MemoryAccessTokenFactory;

public final class MemoryTicketFactory extends AbstractTicketFactory {
	private volatile Ticket ticket;
	private volatile Object lock = new Object();

	public MemoryTicketFactory(String appId, String appSecret) {
		this(appId, appSecret, "jsapi");
	}

	public MemoryTicketFactory(String appId, String appSecret, String type) {
		this(new MemoryAccessTokenFactory(appId, appSecret), type);
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
