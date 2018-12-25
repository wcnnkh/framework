package scw.id;

import java.nio.CharBuffer;
import java.text.SimpleDateFormat;

import scw.common.exception.ShuChaoWenRuntimeException;
import scw.common.utils.StringUtils;
import scw.common.utils.XTime;
import scw.memcached.Memcached;
import scw.redis.Redis;

public class NumberStringIdGenerator extends AbstractStringIdIdGenerator<TimeStampId> {
	private static final String TIME_FORMAT = "yyyyMMddHHmmssSSS";

	public NumberStringIdGenerator(Memcached memcached) {
		super(memcached);
	}

	public NumberStringIdGenerator(Redis redis) {
		super(redis);
	}

	public TimeStampId next() {
		long t = System.currentTimeMillis();
		CharBuffer charBuffer = CharBuffer.allocate(27);
		charBuffer.put(new SimpleDateFormat(TIME_FORMAT).format(t));
		charBuffer.put(StringUtils.complemented(Math.abs(idGenerator.next().intValue()) + "", '0', 10));
		return new TimeStampId(t, new String(charBuffer.array()));
	}

	/**
	 * 获取这个id的创建时间
	 * 
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
