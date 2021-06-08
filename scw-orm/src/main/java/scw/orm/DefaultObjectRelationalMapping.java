package scw.orm;

import java.lang.reflect.AnnotatedElement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import scw.aop.support.ProxyUtils;
import scw.core.annotation.Named;
import scw.core.utils.StringUtils;
import scw.lang.Ignore;
import scw.mapper.FieldDescriptor;
import scw.orm.annotation.Description;
import scw.orm.annotation.Entity;
import scw.orm.annotation.PrimaryKey;

public class DefaultObjectRelationalMapping implements ObjectRelationalMapping {
	/**
	 * 是否将驼峰命名转换为下划线的名称 myAbc-> my_abc
	 */
	private boolean humpNamingReplacement = false;

	public boolean isHumpNamingReplacement() {
		return humpNamingReplacement;
	}

	public void setHumpNamingReplacement(boolean humpNamingReplacement) {
		this.humpNamingReplacement = humpNamingReplacement;
	}

	private String getDefaultName(FieldDescriptor fieldDescriptor) {
		return humpNamingReplacement ? StringUtils.humpNamingReplacement(fieldDescriptor.getName(), "_")
				: fieldDescriptor.getName();
	}

	@Override
	public String getName(FieldDescriptor fieldDescriptor) {
		Named named = fieldDescriptor.getAnnotation(Named.class);
		if (named == null) {
			return getDefaultName(fieldDescriptor);
		}

		if (StringUtils.isEmpty(named.value())) {
			return getDefaultName(fieldDescriptor);
		}
		return named.value();
	}

	private String getDefaultEntityName(Class<?> entityClass) {
		String className = ProxyUtils.getFactory().getUserClass(entityClass).getSimpleName();
		return humpNamingReplacement ? StringUtils.humpNamingReplacement(className, "_") : className;
	}

	@Override
	public Collection<String> getAliasNames(FieldDescriptor fieldDescriptor) {
		List<String> names = new ArrayList<String>(8);
		names.add(fieldDescriptor.getName());
		names.add(StringUtils.humpNamingReplacement(fieldDescriptor.getName(), "_"));
		if (isEntity(fieldDescriptor)) {
			names.addAll(getAliasNames(fieldDescriptor.getType()));
		}
		return names;
	}

	@Override
	public String getDescription(FieldDescriptor fieldDescriptor) {
		Description description = fieldDescriptor.getAnnotation(Description.class);
		return description == null ? null : description.value();
	}

	@Override
	public boolean isPrimaryKey(FieldDescriptor fieldDescriptor) {
		return fieldDescriptor.isAnnotationPresent(PrimaryKey.class);
	}

	@Override
	public boolean isEntity(FieldDescriptor fieldDescriptor) {
		return fieldDescriptor.isAnnotationPresent(Entity.class)
				|| fieldDescriptor.getType().isAnnotationPresent(Entity.class);
	}

	@Override
	public boolean ignore(FieldDescriptor fieldDescriptor) {
		return fieldDescriptor.isAnnotationPresent(Ignore.class);
	}
	
	private String getEntityNameByAnnotatedElement(AnnotatedElement annotatedElement) {
		Entity entity = annotatedElement.getAnnotation(Entity.class);
		if (entity == null) {
			return null;
		}

		String name = entity.name();
		if (StringUtils.isEmpty(name)) {
			name = entity.value();
		}
		return StringUtils.isEmpty(name) ? null : name;
	}

	@Override
	public String getName(Class<?> entityClass) {
		String name = getEntityNameByAnnotatedElement(entityClass);
		if (name == null) {
			return getDefaultEntityName(entityClass);
		}
		return name;
	}

	@Override
	public Collection<String> getAliasNames(Class<?> entityClass) {
		String name = getEntityNameByAnnotatedElement(entityClass);
		String simpleName = ProxyUtils.getFactory().getUserClass(entityClass).getSimpleName();
		String humpName = StringUtils.humpNamingReplacement(simpleName, "_");
		if (name == null) {
			return Arrays.asList(simpleName, humpName);
		} else {
			return Arrays.asList(name, simpleName, humpName);
		}
	}
}
