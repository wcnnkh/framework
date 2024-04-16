package io.basc.framework.autoconfigure.boot;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import io.basc.framework.autoconfigure.beans.factory.Configuration;
import io.basc.framework.autoconfigure.context.ImportResource;

/**
 * 启动类
 * 
 * @author wcnnkh
 *
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Configuration
@ImportResource({ "${application.properties:application.properties}", "${application.yaml:application.yaml}",
	"${application.yml:application.yml}", "${beans.xml:beans.xml}" })
public @interface BootApplication {
}
