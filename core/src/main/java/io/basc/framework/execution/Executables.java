package io.basc.framework.execution;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.util.Elements;

public interface Executables extends Executor {
	/**
	 * 来源描述
	 * 
	 * @return
	 */
	TypeDescriptor getSource();

	/**
	 * 可以被执行的成员
	 * 
	 * @return
	 */
	Elements<? extends Executable> getMembers();

	default boolean isExecuted(Elements<? extends TypeDescriptor> types) {
		for (Executable executable : getMembers()) {
			if (executable.getParameterDescriptors().equals(types,
					(param, type) -> type.isAssignableTo(param.getTypeDescriptor()))) {
				return true;
			}
		}
		return false;
	}

	default Object execute(Elements<? extends TypeDescriptor> types, Elements<? extends Object> args) throws Throwable {
		for (Executable executable : getMembers()) {
			if (executable.getParameterDescriptors().equals(types,
					(param, type) -> type.isAssignableTo(param.getTypeDescriptor()))) {
				return executable.execute(args);
			}
		}
		return false;
	}
}
