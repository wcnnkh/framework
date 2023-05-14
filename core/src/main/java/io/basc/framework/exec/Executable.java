package io.basc.framework.exec;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.util.Elements;
import io.basc.framework.value.Value;

/**
 * 定义一个可执行的
 * 
 * @author wcnnkh
 *
 */
public interface Executable {
	TypeDescriptor getTypeDescriptor();

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
	Object execute() throws ExecutionException;

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
	Object execute(Elements<? extends Value> args) throws ExecutionException;
}
