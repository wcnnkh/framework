package io.basc.framework.jdbc.template.dialect;

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
