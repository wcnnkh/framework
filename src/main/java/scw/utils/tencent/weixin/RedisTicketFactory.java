package scw.utils.tencent.weixin;

import scw.data.redis.Redis;
import scw.utils.locks.RedisLock;

public final class RedisTicketFactory extends AbstractTicketFactory {
	private final Redis redis;
	private final String key;
	private final String lockKey;

	public RedisTicketFactory(Redis redis, String appid, String appsecret, String type) {
		this(redis, appid, new RedisAccessTokenFactory(redis, appid, appsecret), type);
	}

	public RedisTicketFactory(Redis redis, String key, AccessTokenFactory accessTokenFactory, String type) {
		super(accessTokenFactory, type);
		this.redis = redis;
		this.key = this.getClass().getName() + "#" + key;
		this.lockKey = this.getClass().getName() + "#lock#" + key;
	}

	@Override
	protected Ticket getJsApiTicketByCache() {
		return (Ticket) redis.getObjectOperations().get(key);
	}

	@Override
	protected Ticket refreshJsApiTicket() {
		if (!isExpires()) {
			return getJsApiTicketByCache();
		}

		RedisLock lock = new RedisLock(redis, lockKey);
		if (lock.lock()) {
			try {
				if (isExpires()) {
					Ticket ticket = WeiXinUtils.getTicket(getAccessToken(), getType());
					if (ticket.isSuccess()) {
						redis.getObjectOperations().setex(key, ticket.getExpires_in(), ticket);
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
