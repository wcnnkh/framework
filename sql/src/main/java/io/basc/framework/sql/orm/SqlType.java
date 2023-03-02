package io.basc.framework.sql.orm;

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
