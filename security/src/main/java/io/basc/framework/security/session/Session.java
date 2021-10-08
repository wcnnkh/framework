package io.basc.framework.security.session;

import io.basc.framework.util.attribute.EditableAttributes;

public interface Session extends EditableAttributes<String, Object>{

	/**
	 * 创建时间
	 * @return
	 */
	long getCreationTime();

	String getId();

	/**
	 * 最后一次获取此session的时间
	 * @return
	 */
	long getLastAccessedTime();

	/**
	 * session过期时间 单位:秒
	 * @param maxInactiveInterval
	 */
	void setMaxInactiveInterval(int maxInactiveInterval);

	/**
	 * session过期时间
	 * @return
	 */
	int getMaxInactiveInterval();

	/**
	 * 使session失效
	 */
	void invalidate();

	/**
	 * 是否是新创建的session
	 * @return
	 */
	boolean isNew();
}
