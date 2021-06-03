package scw.orm.annotation;

import java.util.Arrays;
import java.util.Collection;

import scw.lang.Ignore;
import scw.mapper.Field;
import scw.orm.ObjectRelationalMapping;

public class AnnotationObjectRelationalMapping implements ObjectRelationalMapping {

	@Override
	public String getName(Field field) {
		return field.getGetter().getName();
	}

	@Override
	public Collection<String> getSetterNames(Field field) {
		return Arrays.asList(field.getSetter().getName());
	}

	@Override
	public String getDescription(Field field) {
		Description description = field.getAnnotation(Description.class);
		return description == null ? null : description.value();
	}

	@Override
	public boolean isPrimaryKey(Field field) {
		return field.isAnnotationPresent(PrimaryKey.class);
	}

	@Override
	public boolean isEntity(Field field) {
		return field.isAnnotationPresent(Entity.class);
	}

	@Override
	public boolean ignore(Field field) {
		return field.isAnnotationPresent(Ignore.class);
	}

}
