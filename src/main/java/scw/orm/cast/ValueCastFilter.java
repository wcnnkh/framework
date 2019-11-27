package scw.orm.cast;

import java.lang.reflect.Type;

public interface ValueCastFilter {
	Object doFilter(Type type, Object value, ValueCastFilterChain chain) throws ValueCastException;
}
