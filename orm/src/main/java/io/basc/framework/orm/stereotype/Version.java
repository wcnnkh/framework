package io.basc.framework.orm.stereotype;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 声明该字段是一个版本号，应该和存储中的版本号一致,如果一致应该写入新值(CAS)
 * 
 * @author wcnnkh
 *
 */
@Target({ ElementType.FIELD, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface Version {
}
