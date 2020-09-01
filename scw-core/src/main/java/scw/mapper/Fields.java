package scw.mapper;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Set;

public interface Fields extends Iterable<Field> {
	Field find(FieldFilter... filters);

	Field find(String name, Type type);

	Field findGetter(String name, Type type);

	Field findSetter(String name, Type type);

	Field getFirst();

	List<Field> toList(FieldFilter... filters);

	Set<Field> toSet(FieldFilter... filters);
}
