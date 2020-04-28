package scw.integration.verification;

import scw.core.Constants;
import scw.core.utils.RandomUtils;
import scw.core.utils.StringUtils;
import scw.data.TemporaryCache;
import scw.util.result.Result;

public abstract class AbstractVerificationCodeService<U> implements
		VerificationCodeService<U> {
	private final TemporaryCache temporaryCache;
	private final int maxCount;// 最大发送次数
	private final int maxCountTimeout;// 发送限制时间间隔
	private final int maxTimeInterval;// 两次发送时间限制
	private final int maxActiveTime;// 验证码有效时间
	private final String prefix;
	private final int maxCheckCount;

	/**
	 * @param temporaryCache
	 * @param maxCount
	 *            最大发送次数
	 * @param maxCountTimeout
	 *            发送限制时间间隔
	 * @param maxTimeInterval
	 *            两次发送时间限制
	 * @param maxActiveTime
	 *            验证码有效时间
	 * @param prefix
	 *            缓存前缀
	 * @param maxCheckCount
	 *            默认的最多可以检查的次数
	 */
	public AbstractVerificationCodeService(TemporaryCache temporaryCache,
			int maxCount, int maxCountTimeout, int maxTimeInterval,
			int maxActiveTime, String prefix, int maxCheckCount) {
		this.prefix = prefix;
		this.temporaryCache = temporaryCache;
		this.maxActiveTime = maxActiveTime;
		this.maxCount = maxCount;
		this.maxCountTimeout = maxCountTimeout;
		this.maxTimeInterval = maxTimeInterval;
		this.maxCheckCount = maxCheckCount;
	}

	protected abstract void sendVerificationCode(int type, U user, String code);

	protected String generatorCode() {
		return RandomUtils.getRandomStr(6);
	}

	public Result send(int type, U user) throws VerificationCodeException {
		SimpleVerificationCode v = getVerificationCode(type, user);
		if (v == null) {
			v = new SimpleVerificationCode();
			v.setCheckCount(0);
		}
		v.setType(type);
		v.setLastSendTime(System.currentTimeMillis());
		v.setSendCount(v.getSendCount() + 1);

		if (System.currentTimeMillis() - v.getLastSendTime() > maxTimeInterval) {
			return new Result(1, "发送过于频繁");
		}

		if (v.getSendCount() > maxCount) {
			return new Result(2, "已达到最大发送次数");
		}

		String code = generatorCode();
		v.setCode(code);
		temporaryCache.set(getCacheKey(type, user), maxCountTimeout, v);
		sendVerificationCode(type, user, code);
		return new Result();
	}

	public SimpleVerificationCode getVerificationCode(int type, U user) {
		return temporaryCache.get(getCacheKey(type, user));
	}

	public Result verification(int type, U user, String code)
			throws VerificationCodeException {
		return verification(type, user, code, maxCheckCount);
	}

	public Result verification(int type, U user, String code, int maxCheckCount)
			throws VerificationCodeException {
		if (StringUtils.isEmpty(code) || user == null) {
			return new Result(1, "验证码不能为空");
		}

		SimpleVerificationCode v = getVerificationCode(type, user);
		if (v == null) {
			return new Result(2, "验证码错误");
		}

		if (System.currentTimeMillis() - v.getLastSendTime() > maxActiveTime) {
			return new Result(3, "验证码已过期");
		}

		boolean b = code.equals(v.getCode());
		if (b) {
			if (v.getCheckCount() > maxCheckCount) {
				return new Result(4, "验证码错误");
			}

			v.setCheckCount(v.getCheckCount() + 1);
			temporaryCache.set(getCacheKey(type, user), maxCountTimeout, v);
			return new Result();
		}
		return new Result(5, "验证码错误");
	}

	protected String getCacheKey(int type, U user) {
		StringBuilder sb = new StringBuilder();
		if (prefix != null) {
			sb.append(prefix);
		}

		if (Constants.DEFAULT_PREFIX != null) {
			sb.append(Constants.DEFAULT_PREFIX);
		}

		sb.append(user.toString());
		sb.append(":");
		sb.append(type);
		return sb.toString();
	}
}
