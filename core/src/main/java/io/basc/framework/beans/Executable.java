package io.basc.framework.beans;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.mapper.Parameter;
import io.basc.framework.mapper.ParameterDescriptor;
import io.basc.framework.util.Elements;
import io.basc.framework.value.Value;

/**
 * 可执行的
 * 
 * @author wcnnkh
 *
 */
public interface Executable {
	TypeDescriptor getTypeDescriptor();

	Elements<? extends ParameterDescriptor> getParameterDescriptors();

	/**
	 * 是否可以无参数执行
	 * 
	 * @return
	 */
	boolean isExecuted();

	/**
	 * 是否可以无参数执行
	 * 
	 * @return
	 */
	Object execute();

	/**
	 * 根据参数类型判断是否可以执行
	 * 
	 * @param types
	 * @return
	 */
	boolean isExecutedByTypes(Elements<? extends TypeDescriptor> types);

	/**
	 * 根据参数类型执行
	 * 
	 * @param args
	 * @return
	 */
	Object executeByTypes(Elements<? extends Value> args);

	/**
	 * 根据参数判断是否可以执行
	 * 
	 * @param parameters
	 * @return
	 */
	boolean isExecuteByParameters(Elements<? extends Parameter> parameters);

	/**
	 * 根据参数执行
	 * 
	 * @param parameters
	 * @return
	 */
	Object executeByParameters(Elements<? extends Parameter> parameters);
}
