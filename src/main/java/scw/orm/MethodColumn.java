package scw.orm;

import java.lang.reflect.Method;

public interface MethodColumn extends FieldColumn {
	Method getGetter();

	Method getSetter();
}
