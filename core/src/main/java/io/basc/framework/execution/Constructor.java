package io.basc.framework.execution;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.util.Elements;

/**
 * 构造器
 * 
 * @author wcnnkh
 *
 */
public interface Constructor extends Constructable {

	default Object execute() throws Throwable {
		return execute(Elements.empty(), Elements.empty());
	}

	Object execute(Elements<? extends TypeDescriptor> types, Elements<? extends Object> args) throws Throwable;
}
