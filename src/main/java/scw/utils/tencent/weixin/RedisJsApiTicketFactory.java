package scw.utils.tencent.weixin;

import scw.data.redis.Redis;
import scw.locks.RedisLock;
import scw.utils.tencent.weixin.bean.JsApiTicket;
import scw.utils.tencent.weixin.process.GetJsApiTicket;

public final class RedisJsApiTicketFactory extends AbstractJsApiTicketFactory {
	private final Redis redis;
	private final String key;
	private final String lockKey;

	public RedisJsApiTicketFactory(Redis redis, String appid, String appsecret) {
		this(redis, appid, new RedisAccessTokenFactory(redis, appid, appsecret));
	}

	public RedisJsApiTicketFactory(Redis redis, String key, AccessTokenFactory accessTokenFactory) {
		super(accessTokenFactory);
		this.redis = redis;
		this.key = this.getClass().getName() + "#" + key;
		this.lockKey = this.getClass().getName() + "#lock#" + key;
	}

	@Override
	protected JsApiTicket getJsApiTicketByCache() {
		return (JsApiTicket) redis.getObjectOperations().get(key);
	}

	@Override
	protected JsApiTicket refreshJsApiTicket() {
		if (!isExpires()) {
			return getJsApiTicketByCache();
		}

		RedisLock lock = new RedisLock(redis, lockKey);
		if (lock.lock()) {
			try {
				if (isExpires()) {
					GetJsApiTicket getJsApiTicket = new GetJsApiTicket(getAccessToken());
					if (getJsApiTicket.isSuccess()) {
						JsApiTicket jsApiTicket = getJsApiTicket.getTicket();
						redis.getObjectOperations().setex(key, jsApiTicket.getExpires_in(), jsApiTicket);
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
