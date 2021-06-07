package scw.orm;

import java.util.Arrays;
import java.util.Collection;

import scw.aop.support.ProxyUtils;
import scw.core.utils.StringUtils;
import scw.lang.Ignore;
import scw.mapper.Field;
import scw.orm.annotation.Description;
import scw.orm.annotation.Entity;
import scw.orm.annotation.PrimaryKey;

public class DefaultObjectRelationalMapping implements ObjectRelationalMapping {

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

	@Override
	public String getEntityName(Class<?> entityClass) {
		return StringUtils.humpNamingReplacement(ProxyUtils.getFactory().getUserClass(entityClass).getSimpleName(),
				"_");
	}

	@Override
	public Collection<String> getSetterEntityNames(Class<?> entityClass) {
		String name = StringUtils
				.humpNamingReplacement(ProxyUtils.getFactory().getUserClass(entityClass).getSimpleName(), "_");
		return Arrays.asList(entityClass.getName(), name);
	}

}
