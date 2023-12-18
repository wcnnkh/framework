package io.basc.framework.boot.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import io.basc.framework.beans.factory.annotation.ImportResource;
import io.basc.framework.core.annotation.AliasFor;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@ImportResource({ "${application.properties:application.properties}", "${application.yaml:application.yaml}",
		"${application.yml:application.yml}", "${beans.xml:beans.xml}" })
public @interface ApplicationResource {
	@AliasFor(annotation = ImportResource.class)
	String[] value() default {};
}
