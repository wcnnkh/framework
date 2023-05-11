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
public interface BeanExecutor {
	TypeDescriptor getTypeDescriptor();

	Elements<? extends ParameterDescriptor> getParameterDescriptors();

	/**
	 * 是否可以无参数执行
	 * 
	 * @return
	 */
	boolean isExecutable();

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
	boolean isExecutable(Elements<? extends TypeDescriptor> types);

	/**
	 * 根据参数类型执行
	 * 
	 * @param args
	 * @return
	 */
	Object execute(Elements<? extends Value> args);

	/**
	 * 根据参数判断是否可以执行
	 * 
	 * @param parameters
	 * @return
	 * @see Value
	 * @see Parameter
	 */
	boolean isExecutableByParameters(Elements<? extends Value> parameters);

	/**
	 * 根据参数执行
	 * 
	 * @param parameters
	 * @return
	 * @see Value
	 * @see Parameter
	 */
	Object executeByParameters(Elements<? extends Value> parameters);
}
