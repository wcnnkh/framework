package scw.orm.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 声明该字段是一个版本号，应该和存储中的版本号一致,如果一致应该写入新值<br/>
 * orm框架会尽可能的实现此功能，如果无法实现推荐在日志中体现
 * @author shuchaowen
 *
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Version {
}
