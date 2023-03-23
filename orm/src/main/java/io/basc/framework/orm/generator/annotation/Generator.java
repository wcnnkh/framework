package io.basc.framework.orm.generator.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import io.basc.framework.core.annotation.AliasFor;
import io.basc.framework.orm.annotation.InvalidBaseTypeValue;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@InvalidBaseTypeValue
public @interface Generator {
	@AliasFor(annotation = InvalidBaseTypeValue.class)
	double[] value() default { 0 };
}
