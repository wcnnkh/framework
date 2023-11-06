package io.basc.framework.execution;

/**
 * 构造器
 * 
 * @author wcnnkh
 *
 */
public interface Constructor extends Constructable {

	default Object execute() throws Throwable {
		return execute(new Class<?>[0], new Object[0]);
	}

	Object execute(Class<?>[] types, Object[] args) throws Throwable;
}
