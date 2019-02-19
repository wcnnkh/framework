package scw.utils.tencent.weixin;

import scw.memcached.Memcached;
import scw.utils.locks.MemcachedLock;
import scw.utils.tencent.weixin.bean.JsApiTicket;
import scw.utils.tencent.weixin.process.GetJsApiTicket;

public final class MemcachedJsApiTicketFactory extends AbstractJsApiTicketFactory {
	private final Memcached memcached;
	private final String key;
	private final String lockKey;

	public MemcachedJsApiTicketFactory(Memcached memcached, String appid, String appsecret) {
		this(memcached, appid, new MemcachedAccessTokenFactory(memcached, appid, appsecret));
	}

	public MemcachedJsApiTicketFactory(Memcached memcached, String key, AccessTokenFactory accessTokenFactory) {
		super(accessTokenFactory);
		this.memcached = memcached;
		this.key = this.getClass().getName() + "#" + key;
		this.lockKey = this.getClass().getName() + "#lock#" + key;
	}

	@Override
	protected JsApiTicket getJsApiTicketByCache() {
		return memcached.get(key);
	}

	@Override
	protected JsApiTicket refreshJsApiTicket() {
		if (!isExpires()) {
			return getJsApiTicketByCache();
		}

		MemcachedLock lock = new MemcachedLock(memcached, lockKey);
		if (lock.lock()) {
			try {
				if (isExpires()) {
					GetJsApiTicket getJsApiTicket = new GetJsApiTicket(getAccessToken());
					if (getJsApiTicket.isSuccess()) {
						JsApiTicket jsApiTicket = getJsApiTicket.getTicket();
						memcached.set(key, jsApiTicket.getExpires_in(), jsApiTicket);
						return jsApiTicket;
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
