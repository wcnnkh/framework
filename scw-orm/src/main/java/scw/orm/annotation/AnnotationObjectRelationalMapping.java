package scw.orm.annotation;

import java.lang.reflect.AnnotatedElement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import scw.aop.support.ProxyUtils;
import scw.core.annotation.AnnotatedElementUtils;
import scw.core.annotation.AnnotationAttributes;
import scw.core.annotation.Named;
import scw.core.utils.ArrayUtils;
import scw.core.utils.StringUtils;
import scw.lang.Ignore;
import scw.mapper.FieldDescriptor;
import scw.orm.ObjectRelationalMapping;

public class AnnotationObjectRelationalMapping implements ObjectRelationalMapping {
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
		String className = ProxyUtils.getFactory().getUserClass(entityClass).getSimpleName();
		return StringUtils.humpNamingReplacement(className, "_");
	}

	@Override
	public Collection<String> getAliasNames(FieldDescriptor fieldDescriptor) {
		List<String> names = new ArrayList<String>(8);
		String name = getAnnotationFeldName(fieldDescriptor);
		if (name != null) {
			names.add(name);
		}

		String[] aliasArray = getAnnotatedAlias(fieldDescriptor);
		if (aliasArray != null) {
			for (String alias : aliasArray) {
				if (StringUtils.isNotEmpty(alias)) {
					names.add(alias);
				}
			}
		}
		
		if(StringUtils.isEmpty(name) && ArrayUtils.isEmpty(aliasArray)) {
			//如果没有设置过别名
			String defaultName = fieldDescriptor.getName();
			names.add(defaultName);
			String humpName = StringUtils.humpNamingReplacement(defaultName, "_");
			if(!humpName.equals(defaultName)) {
				names.add(humpName);
			}
		}
		
		if (isEntity(fieldDescriptor)) {
			names.addAll(getAliasNames(fieldDescriptor.getType()));
		}
		return names;
	}

	@Override
	public boolean isPrimaryKey(FieldDescriptor fieldDescriptor) {
		return AnnotatedElementUtils.isAnnotated(fieldDescriptor, PrimaryKey.class);
	}

	@Override
	public boolean isEntity(Class<?> clazz) {
		return AnnotatedElementUtils.isAnnotated(clazz, Entity.class);
	}

	@Override
	public boolean isEntity(FieldDescriptor fieldDescriptor) {
		return AnnotatedElementUtils.isAnnotated(fieldDescriptor, Entity.class) || isEntity(fieldDescriptor.getType());
	}

	@Override
	public boolean ignore(FieldDescriptor fieldDescriptor) {
		return fieldDescriptor.isAnnotationPresent(Ignore.class);
	}

	private String getEntityNameByAnnotatedElement(AnnotatedElement annotatedElement) {
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

	private String[] getAnnotatedAlias(AnnotatedElement annotatedElement) {
		Alias alias = AnnotatedElementUtils.getMergedAnnotation(annotatedElement, Alias.class);
		return alias == null ? null : alias.value();
	}

	@Override
	public Collection<String> getAliasNames(Class<?> entityClass) {
		List<String> list = new ArrayList<String>(8);
		String name = getEntityNameByAnnotatedElement(entityClass);
		if (StringUtils.isNotEmpty(name)) {
			list.add(name);
		}

		String[] aliasArray = getAnnotatedAlias(entityClass);
		if (aliasArray != null) {
			for (String alias : aliasArray) {
				if (StringUtils.isNotEmpty(alias)) {
					list.add(alias);
				}
			}
		}
		
		if(StringUtils.isEmpty(name) && ArrayUtils.isEmpty(aliasArray)) {
			//如果没有使用过别名，那就设置默认名称
			String simpleName = ProxyUtils.getFactory().getUserClass(entityClass).getSimpleName();
			list.add(simpleName);
			String humpName = StringUtils.humpNamingReplacement(simpleName, "_");
			if(!simpleName.endsWith(humpName)) {
				list.add(humpName);
			}
		}
		return list;
	}

	@Override
	public boolean isVersionField(FieldDescriptor fieldDescriptor) {
		return AnnotatedElementUtils.isAnnotated(fieldDescriptor, Version.class);
	}
}
