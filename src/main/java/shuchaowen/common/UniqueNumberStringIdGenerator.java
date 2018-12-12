package shuchaowen.common;

import java.nio.CharBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;

import shuchaowen.common.exception.ShuChaoWenRuntimeException;
import shuchaowen.common.utils.StringUtils;
import shuchaowen.common.utils.XTime;
import shuchaowen.memcached.Memcached;
import shuchaowen.memcached.MemcachedIdGenerator;
import shuchaowen.redis.Redis;
import shuchaowen.redis.RedisIdGernerator;

/**
 * 这是一个定长的27位字符串
 * 字符串由数字组成
 * 一般用于生成订单号
 * @author shuchaowen
 *
 */
public final class UniqueNumberStringIdGenerator implements IdGenerator<String> {
	private static final String TIME_FORMAT = "yyyyMMddHHmmssSSS";
	private final IdGenerator<Long> idGenerator;

	public UniqueNumberStringIdGenerator(Memcached memcached) {
		this.idGenerator = new MemcachedIdGenerator(memcached, this.getClass().getName(), 0);
	}

	public UniqueNumberStringIdGenerator(Redis redis) {
		this.idGenerator = new RedisIdGernerator(redis, this.getClass().getName(), 0);
	}

	public String next() {
		CharBuffer charBuffer = CharBuffer.allocate(27);
		charBuffer.put(new SimpleDateFormat(TIME_FORMAT).format(new Date()));
		charBuffer.put(StringUtils.complemented(Math.abs(idGenerator.next().intValue()) + "", '0', 10));
		return new String(charBuffer.array());
	}
	
	/**
	 * 获取这个id的创建时间
	 * @param id
	 * @return
	 */
	public static long getCts(String id) {
		if (id.length() != 27) {
			throw new ShuChaoWenRuntimeException(id);
		}
		return XTime.getTime(id.substring(0, 17), TIME_FORMAT);
	}
}
