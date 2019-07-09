package scw.utils.tencent.weixin.ticket;

import scw.data.redis.Redis;
import scw.utils.locks.RedisLock;
import scw.utils.tencent.weixin.WeiXinUtils;
import scw.utils.tencent.weixin.token.AccessTokenFactory;
import scw.utils.tencent.weixin.token.RedisAccessTokenFactory;

public final class RedisTicketFactory extends AbstractTicketFactory {
	private final Redis redis;
	private final String key;
	private final String lockKey;

	public RedisTicketFactory(Redis redis, String appId, String appSecret) {
		this(redis, appId, appSecret, "jsapi");
	}

	public RedisTicketFactory(Redis redis, String appId, String appSecret, String type) {
		this(redis, appId, new RedisAccessTokenFactory(redis, appId, appSecret), type);
	}

	public RedisTicketFactory(Redis redis, String key, AccessTokenFactory accessTokenFactory) {
		this(redis, key, accessTokenFactory, "jsapi");
	}

	public RedisTicketFactory(Redis redis, String key, AccessTokenFactory accessTokenFactory, String type) {
		super(accessTokenFactory, type);
		this.redis = redis;
		this.key = "wx_ticket:#" + type + "#" + key;
		this.lockKey = "wx_ticket:#lock#" + type + "#" + key;
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
