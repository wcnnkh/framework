package scw.orm;

import java.lang.reflect.Method;

public interface MethodColumn extends Column {
	Method getGetter();

	Method getSetter();
}
