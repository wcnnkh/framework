package io.basc.framework.execution;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.util.Elements;

/**
 * 定义一个可执行的
 * 
 * @author wcnnkh
 *
 */
public interface Executable {
	/**
	 * 来源
	 * 
	 * @return
	 */
	TypeDescriptor getSource();

	/**
	 * 可以被执行的执行器
	 * 
	 * @return
	 */
	Elements<? extends Executor> getExecutors();
}
