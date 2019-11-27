package scw.orm.cast;

import java.lang.reflect.Type;

public interface ValueCastFilterChain {
	Object doFilter(Type type, Object value) throws ValueCastException;
}
