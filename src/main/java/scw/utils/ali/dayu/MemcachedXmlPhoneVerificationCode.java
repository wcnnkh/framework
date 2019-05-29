package scw.utils.ali.dayu;

import java.lang.reflect.InvocationTargetException;

import scw.data.memcached.Memcached;
import scw.result.ResultFactory;

public final class MemcachedXmlPhoneVerificationCode extends AbstractXmlPhoneVerificationCode {
	private final Memcached memcached;
	private final long tempSuffix = System.currentTimeMillis();

	public MemcachedXmlPhoneVerificationCode(String xmlPath, Memcached memcached, ResultFactory resultFactory)
			throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		super(xmlPath, resultFactory);
		this.memcached = memcached;
	}

	public PhoneVerificationCode getCacheData(int configIndex, String phone) {
		StringBuilder sb = new StringBuilder();
		sb.append(phone);
		sb.append("&").append(configIndex);
		sb.append("&").append(tempSuffix);
		return memcached.get(sb.toString());
	}

	public void setCacheData(int configIndex, String phone, PhoneVerificationCode json) {
		if (json == null) {
			return;
		}

		StringBuilder sb = new StringBuilder();
		sb.append(phone);
		sb.append("&").append(configIndex);
		sb.append("&").append(tempSuffix);
		memcached.set(sb.toString(), getMaxActiveTime(), json);
	}
}
