package scw.utils.tencent.weixin;

public final class MemoryTicketFactory extends AbstractTicketFactory {
	private volatile Ticket ticket;
	private volatile Object lock = new Object();

	public MemoryTicketFactory(String appid, String appsecret, String type) {
		this(new MemoryAccessTokenFactory(appid, appsecret), type);
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
