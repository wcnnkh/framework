package io.basc.framework.rpc.http.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Expands the uri template supplied in the {@code value}, permitting path and query variables, or
 * just the http method. Templates should conform to
 * <a href="https://tools.ietf.org/html/rfc6570">RFC 6570</a>. Support is limited to Simple String
 * expansion and Reserved Expansion (Level 1 and Level 2) expressions.
 * <br/>
 * <br/>
 * @RequestLine("GET /")
 */
@Target({ ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequestLine {
	String value();
}
