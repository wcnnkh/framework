package io.basc.framework.mapper;

import io.basc.framework.core.parameter.ParameterDescriptor;
import io.basc.framework.core.reflect.FieldHolder;
import io.basc.framework.core.reflect.MethodHolder;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public interface FieldDescriptor extends ParameterDescriptor, MethodHolder, FieldHolder{
	int getModifiers();
	
	/**
	 * 可能返回空, 但field和method必定存在一个
	 * @return
	 */
	Field getField();
	 
	/**
	 * getter/setter method
	 * <br/>可能返回空, 但field和method必定存在一个
	 * @return
	 */
	Method getMethod();
}
