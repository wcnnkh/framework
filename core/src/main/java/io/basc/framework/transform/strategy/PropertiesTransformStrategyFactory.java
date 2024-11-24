package io.basc.framework.transform.strategy;

import io.basc.framework.core.convert.TypeDescriptor;

public interface PropertiesTransformStrategyFactory {
	/**
	 * 获取策略
	 * 
	 * @param requiredTypeDescriptor
	 * @return
	 */
	PropertiesTransformStrategy getPropertiesTransformStrategy(TypeDescriptor requiredTypeDescriptor);
}
