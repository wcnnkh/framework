package scw.mapper;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import scw.core.parameter.ParameterDescriptor;
import scw.core.reflect.FieldHolder;
import scw.core.reflect.MethodHolder;

public interface FieldDescriptor extends ParameterDescriptor, MethodHolder, FieldHolder, Serializable{
	Class<?> getDeclaringClass();
	
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
