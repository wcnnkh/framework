package scw.integration.tencent.wx.ticket;

import scw.core.annotation.ParameterName;
import scw.data.memcached.Memcached;
import scw.integration.tencent.wx.Ticket;
import scw.integration.tencent.wx.WeiXinUtils;
import scw.integration.tencent.wx.token.AccessTokenFactory;
import scw.integration.tencent.wx.token.MemcachedAccessTokenFactory;
import scw.locks.Lock;
import scw.locks.LockFactory;
import scw.locks.MemcachedLockFactory;

public final class MemcachedTicketFactory extends AbstractTicketFactory {
	private final Memcached memcached;
	private final String key;
	private final String lockKey;
	private final LockFactory lockFactory;

	public MemcachedTicketFactory(Memcached memcached, @ParameterName(WX_APPID_KEY) String appid,
			@ParameterName(WX_APPSECRET_KEY) String appsecret) {
		this(memcached, appid, appsecret, "jsapi");
	}

	public MemcachedTicketFactory(Memcached memcached, @ParameterName(WX_APPID_KEY) String appid,
			@ParameterName(WX_APPSECRET_KEY) String appsecret, @ParameterName(WX_TICKET_TYPE) String type) {
		this(memcached, appid, new MemcachedAccessTokenFactory(memcached, appid, appsecret), type);
	}

	public MemcachedTicketFactory(Memcached memcached, String key, AccessTokenFactory accessTokenFactory) {
		this(memcached, key, accessTokenFactory, "jsapi");
	}

	public MemcachedTicketFactory(Memcached memcached, String key, AccessTokenFactory accessTokenFactory, String type) {
		super(accessTokenFactory, type);
		this.memcached = memcached;
		this.key = "wx_ticket:#" + type + "#" + key;
		this.lockKey = "wx_ticket:#lock#" + type + "#" + key;
		this.lockFactory = new MemcachedLockFactory(memcached);
	}

	@Override
	protected Ticket getJsApiTicketByCache() {
		return (Ticket) memcached.get(key);
	}

	@Override
	protected Ticket refreshJsApiTicket() {
		if (!isExpires()) {
			return getJsApiTicketByCache();
		}

		Lock lock = lockFactory.getLock(lockKey);
		if (lock.tryLock()) {
			try {
				if (isExpires()) {
					Ticket ticket = WeiXinUtils.getTicket(getAccessToken(), getType());
					if (ticket.isSuccess()) {
						memcached.set(key, ticket.getExpires_in(), ticket);
						return ticket;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				lock.unlock();
			}
		}

		// 没有拿到锁
		try {
			Thread.sleep(50L);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return refreshJsApiTicket();
	}
}
