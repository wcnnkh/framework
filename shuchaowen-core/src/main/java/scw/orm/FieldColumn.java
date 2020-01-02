package scw.orm;

import java.lang.reflect.Field;

public interface FieldColumn extends Column {
	Field getField();
}
