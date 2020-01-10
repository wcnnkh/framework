package scw.orm;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

import scw.core.reflect.AnnotationFactory;

public interface Column extends AnnotationFactory {
	Class<?> getType();

	Type getGenericType();

	Class<?> getDeclaringClass();

	String getName();

	/**
	 * 是否支持获取数据
	 * 
	 * @return
	 */
	boolean isSupportGet();

	Object get(Object obj) throws ORMException;

	/**
	 * 是否支持插入数据
	 * 
	 * @return
	 */
	boolean isSupportSet();

	void set(Object obj, Object value) throws ORMException;

	String getDescription();
	
	Field getField();
}
