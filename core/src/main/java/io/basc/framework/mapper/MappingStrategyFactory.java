package io.basc.framework.mapper;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.mapper.support.SetFilter;
import io.basc.framework.util.Elements;

/**
 * 映射策略工厂
 * 
 * @author wcnnkh
 *
 * @param <E>
 */
public interface MappingStrategyFactory {
	/**
	 * 获取映射策略
	 * 
	 * @param typeDescriptor
	 * @return
	 */
	MappingStrategy getMappingStrategy(Class<?> clazz);

	Elements<SetFilter> getSetFilters(TypeDescriptor typeDescriptor);
}
