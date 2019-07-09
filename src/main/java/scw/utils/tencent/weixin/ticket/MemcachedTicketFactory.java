package scw.utils.tencent.weixin.ticket;

import scw.data.memcached.Memcached;
import scw.utils.locks.MemcachedLock;
import scw.utils.tencent.weixin.WeiXinUtils;
import scw.utils.tencent.weixin.token.AccessTokenFactory;
import scw.utils.tencent.weixin.token.MemcachedAccessTokenFactory;

public final class MemcachedTicketFactory extends AbstractTicketFactory {
	private final Memcached memcached;
	private final String key;
	private final String lockKey;

	public MemcachedTicketFactory(Memcached memcached, String appId, String appSecret) {
		this(memcached, appId, appSecret, "jsapi");
	}

	public MemcachedTicketFactory(Memcached memcached, String appId, String appSecret, String type) {
		this(memcached, appId, new MemcachedAccessTokenFactory(memcached, appId, appSecret), type);
	}

	public MemcachedTicketFactory(Memcached memcached, String key, AccessTokenFactory accessTokenFactory) {
		this(memcached, key, accessTokenFactory, "jsapi");
	}

	public MemcachedTicketFactory(Memcached memcached, String key, AccessTokenFactory accessTokenFactory, String type) {
		super(accessTokenFactory, type);
		this.memcached = memcached;
		this.key = "wx_ticket:#" + type + "#" + key;
		this.lockKey = "wx_ticket:#lock#" + type + "#" + key;
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

		MemcachedLock lock = new MemcachedLock(memcached, lockKey);
		if (lock.lock()) {
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
