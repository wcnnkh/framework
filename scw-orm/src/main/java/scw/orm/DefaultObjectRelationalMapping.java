package scw.orm;

import java.lang.reflect.AnnotatedElement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import scw.aop.support.ProxyUtils;
import scw.core.annotation.AnnotatedElementUtils;
import scw.core.annotation.AnnotationAttributes;
import scw.core.annotation.Named;
import scw.core.utils.StringUtils;
import scw.lang.Ignore;
import scw.mapper.FieldDescriptor;
import scw.orm.annotation.Entity;
import scw.orm.annotation.PrimaryKey;
import scw.orm.annotation.Version;

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
		return humpNamingReplacement ? StringUtils.humpNamingReplacement(
				fieldDescriptor.getName(), "_") : fieldDescriptor.getName();
	}

	private String getAnnotationFeldName(AnnotatedElement annotatedElement) {
		AnnotationAttributes annotationAttributes = AnnotatedElementUtils
				.getMergedAnnotationAttributes(annotatedElement, Named.class);
		if (annotationAttributes == null) {
			return null;
		}

		String name = annotationAttributes.getString("value");
		return StringUtils.isEmpty(name) ? null : name;
	}

	@Override
	public String getName(FieldDescriptor fieldDescriptor) {
		String name = getAnnotationFeldName(fieldDescriptor);
		if (name == null) {
			return getDefaultName(fieldDescriptor);
		}
		return name;
	}

	private String getDefaultEntityName(Class<?> entityClass) {
		String className = ProxyUtils.getFactory().getUserClass(entityClass)
				.getSimpleName();
		return StringUtils.humpNamingReplacement(className, "_");
	}

	@Override
	public Collection<String> getAliasNames(FieldDescriptor fieldDescriptor) {
		List<String> names = new ArrayList<String>(8);
		String name = getAnnotationFeldName(fieldDescriptor);
		if (name != null) {
			names.add(name);
		}
		names.add(fieldDescriptor.getName());
		names.add(StringUtils.humpNamingReplacement(fieldDescriptor.getName(),
				"_"));
		if (isEntity(fieldDescriptor)) {
			names.addAll(getAliasNames(fieldDescriptor.getType()));
		}
		return names;
	}

	@Override
	public String getDescription(FieldDescriptor fieldDescriptor) {
		return AnnotatedElementUtils.getDescription(fieldDescriptor);
	}

	@Override
	public boolean isPrimaryKey(FieldDescriptor fieldDescriptor) {
		return AnnotatedElementUtils.isAnnotated(fieldDescriptor,
				PrimaryKey.class);
	}

	@Override
	public boolean isEntity(Class<?> clazz) {
		return AnnotatedElementUtils.isAnnotated(clazz, Entity.class);
	}

	@Override
	public boolean isEntity(FieldDescriptor fieldDescriptor) {
		return AnnotatedElementUtils.isAnnotated(fieldDescriptor, Entity.class)
				|| isEntity(fieldDescriptor.getType());
	}

	@Override
	public boolean ignore(FieldDescriptor fieldDescriptor) {
		return fieldDescriptor.isAnnotationPresent(Ignore.class);
	}

	private String getEntityNameByAnnotatedElement(
			AnnotatedElement annotatedElement) {
		AnnotationAttributes annotationAttributes = AnnotatedElementUtils
				.getMergedAnnotationAttributes(annotatedElement, Entity.class);
		if (annotationAttributes == null) {
			return null;
		}

		String name = annotationAttributes.getString("name");
		if (StringUtils.isEmpty(name)) {
			return null;
		}
		return name;
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
		String simpleName = ProxyUtils.getFactory().getUserClass(entityClass)
				.getSimpleName();
		String humpName = StringUtils.humpNamingReplacement(simpleName, "_");
		if (name == null) {
			return Arrays.asList(simpleName, humpName);
		} else {
			return Arrays.asList(name, simpleName, humpName);
		}
	}

	@Override
	public boolean isVersionField(FieldDescriptor fieldDescriptor) {
		return AnnotatedElementUtils
				.isAnnotated(fieldDescriptor, Version.class);
	}
}
