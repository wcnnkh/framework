package io.basc.framework.mapper;

import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import io.basc.framework.core.parameter.ParameterDescriptor;
import io.basc.framework.core.reflect.FieldHolder;
import io.basc.framework.core.reflect.MethodHolder;

public interface FieldDescriptor extends ParameterDescriptor, MethodHolder, FieldHolder, Member {
	/**
	 * 可能返回空, 但field和method必定存在一个
	 * 
	 * @return
	 */
	Field getField();

	/**
	 * getter/setter method <br/>
	 * 可能返回空, 但field和method必定存在一个
	 * 
	 * @return
	 */
	Method getMethod();

	@Override
	default boolean accept(ParameterDescriptor target) {
		if (ParameterDescriptor.super.accept(target)) {
			if (target instanceof FieldDescriptor) {
				int sourceModifiers = getModifiers();
				int targetModifiers = ((FieldDescriptor) target).getModifiers();
				if (Modifier.isStatic(sourceModifiers) ^ Modifier.isStatic(targetModifiers)) {
					return false;
				}
			}
			return true;
		}
		return false;
	}
}
