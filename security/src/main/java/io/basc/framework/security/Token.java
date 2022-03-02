package io.basc.framework.security;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

import io.basc.framework.core.reflect.ReflectionUtils;
import io.basc.framework.util.StringUtils;

/**
 * 一个令牌的定义
 * 
 * @author shuchaowen
 *
 */
public class Token implements Serializable, Cloneable {
	private static final long serialVersionUID = 1L;
	private final String token;
	private final long createTime;
	private final long expireAtTime;
	private final boolean isNew;

	/**
	 * 序列化使用
	 */
	protected Token() {
		this(null, 0, 0);
	}

	public Token(String token, long expireDuration, TimeUnit expireUnit) {
		this(token, System.currentTimeMillis(), expireDuration, expireUnit);
	}

	/**
	 * @param token
	 * @param createTime
	 * @param expireDuration 过期时长
	 * @param expireUnit
	 */
	public Token(String token, long createTime, long expireDuration, TimeUnit expireUnit) {
		this(token, createTime, expireDuration, expireUnit, false);
	}

	/**
	 * @param token
	 * @param createTime
	 * @param expireDuration 过期时长
	 * @param expireUnit
	 * @param isNew
	 */
	public Token(String token, long createTime, long expireDuration, TimeUnit expireUnit, boolean isNew) {
		this(token, createTime, createTime + expireUnit.toMillis(expireDuration), isNew);
	}

	/**
	 * @param token
	 * @param createTime
	 * @param expireAtTime 到期时间
	 */
	public Token(String token, long createTime, long expireAtTime) {
		this(token, createTime, expireAtTime, true);
	}

	public Token(String token, long createTime, long expireAtTime, boolean isNew) {
		this.token = token;
		this.expireAtTime = expireAtTime;
		this.createTime = createTime;
		this.isNew = isNew;
	}

	public String getToken() {
		return token;
	}

	/**
	 * 创建时间
	 * 
	 * @return
	 */
	public long getCreateTime() {
		return Math.max(0, createTime);
	}

	/**
	 * 到期时间点
	 * 
	 * @return
	 */
	public long getExpireAtTime() {
		return expireAtTime;
	}

	/**
	 * 有效期(毫秒)
	 * 
	 * @return
	 */
	public long getPeriodOfValidity() {
		return expireAtTime - createTime;
	}

	/**
	 * 是否是新创建的
	 * 
	 * @return
	 */
	public boolean isNew() {
		return isNew;
	}

	public boolean isExpired() {
		return isExpired(0, TimeUnit.MILLISECONDS);
	}

	/**
	 * 是否已过期,如果expiresIn或createTime小于等于0那么始终返回false
	 * 
	 * @param ahead 提前多久过期
	 * @return
	 */
	public boolean isExpired(long ahead, TimeUnit timeUnit) {
		if (expireAtTime <= 0 || createTime <= 0) {
			return false;
		}

		return (expireAtTime - timeUnit.toMillis(ahead)) - System.currentTimeMillis() <= 0;
	}

	/**
	 * 克隆会将isNew设置为false
	 */
	@Override
	public Token clone() {
		return new Token(token, expireAtTime, createTime, false);
	}

	@Override
	public String toString() {
		return ReflectionUtils.toString(this);
	}

	@Override
	public int hashCode() {
		return token == null ? 0 : token.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (obj instanceof Token) {
			return StringUtils.equals(token, ((Token) obj).token);
		}

		return false;
	}
}
