package io.basc.framework.execution;

import io.basc.framework.mapper.ParameterDescriptor;
import io.basc.framework.util.Elements;

/**
 * 定义一个处理器
 * 
 * @author wcnnkh
 *
 */
public interface Executor extends Executable {
	Elements<? extends ParameterDescriptor> getParameterDescriptors();
}
