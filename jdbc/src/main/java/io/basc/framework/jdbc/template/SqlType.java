package io.basc.framework.jdbc.template;

/**
 * sql类型
 * 
 * @author wcnnkh
 *
 */
public interface SqlType {
	String getName();

	Class<?> getType();

	int getLength();
}
