package io.basc.framework.ibatis.beans.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import io.basc.framework.beans.factory.annotation.Indexed;

@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Indexed
public @interface TypeAliaseScan {
	String[] value();
}
