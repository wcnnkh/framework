package io.basc.framework.env;

import java.util.Collection;

import io.basc.framework.util.StringUtils;
import io.basc.framework.value.ValueFactory;

/**
 * profiles 解析器
 * 
 * @see DefaultProfilesResolver
 * @author shuchaowen
 *
 */
public interface ProfilesResolver {
	/**
	 * 解析出可用的结果，使用优先级从高到低
	 * 
	 * @param factory
	 * @param name
	 * @return
	 */
	Collection<String> resolve(ValueFactory<String> factory, String name);

	/**
	 * 返回profiles，优先级从高到低
	 * 
	 * @param factory
	 * @return 不存在就返回{@link StringUtils#EMPTY_ARRAY}
	 */
	String[] getProfiles(ValueFactory<String> factory);
}
