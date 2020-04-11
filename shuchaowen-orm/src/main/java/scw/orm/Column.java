package scw.orm;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Type;

public interface Column {
	AnnotatedElement getAnnotatedElement();
	
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
