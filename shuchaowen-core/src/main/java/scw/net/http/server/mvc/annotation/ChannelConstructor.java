package scw.net.http.server.mvc.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 当一个类中存在多个构造方法时指定使用的构造方法
 * @author asus1
 *
 */
@Target({ElementType.CONSTRUCTOR })
@Retention(RetentionPolicy.RUNTIME)
public @interface ChannelConstructor {
}
