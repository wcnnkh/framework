package scw.utils.tencent.weixin;

import scw.data.memcached.Memcached;
import scw.utils.locks.MemcachedLock;

public final class MemcachedTicketFactory extends AbstractTicketFactory {
	private final Memcached memcached;
	private final String key;
	private final String lockKey;

	public MemcachedTicketFactory(Memcached memcached, String appid, String appsecret, String type) {
		this(memcached, appid, new MemcachedAccessTokenFactory(memcached, appid, appsecret), type);
	}

	public MemcachedTicketFactory(Memcached memcached, String key, AccessTokenFactory accessTokenFactory,
			String type) {
		super(accessTokenFactory, type);
		this.memcached = memcached;
		this.key = this.getClass().getName() + "#" + key;
		this.lockKey = this.getClass().getName() + "#lock#" + key;
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
