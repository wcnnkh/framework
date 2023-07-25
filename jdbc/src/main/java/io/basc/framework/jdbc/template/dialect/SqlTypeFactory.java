package io.basc.framework.jdbc.template.dialect;

/**
 * 通过java类型得到sql类型
 * 
 * @author wcnnkh
 *
 */
public interface SqlTypeFactory {
	SqlType getSqlType(Class<?> javaType);
}
