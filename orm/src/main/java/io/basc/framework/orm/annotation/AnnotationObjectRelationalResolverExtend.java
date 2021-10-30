package io.basc.framework.orm.annotation;

import java.lang.reflect.AnnotatedElement;
import java.util.Collection;
import java.util.LinkedHashSet;

import io.basc.framework.core.annotation.AnnotatedElementUtils;
import io.basc.framework.core.annotation.AnnotationAttributes;
import io.basc.framework.data.domain.Range;
import io.basc.framework.lang.Ignore;
import io.basc.framework.mapper.FieldDescriptor;
import io.basc.framework.orm.ObjectRelationalResolver;
import io.basc.framework.orm.support.ObjectRelationalResolverExtend;
import io.basc.framework.util.StringUtils;

public class AnnotationObjectRelationalResolverExtend implements ObjectRelationalResolverExtend {

	@Override
	public Boolean isIgnore(Class<?> entityClass, ObjectRelationalResolver chain) {
		Ignore ignore = entityClass.getAnnotation(Ignore.class);
		if (ignore == null) {
			return chain.isIgnore(entityClass);
		}
		return ignore.value();
	}

	@Override
	public Boolean isIgnore(Class<?> entityClass, FieldDescriptor fieldDescriptor, ObjectRelationalResolver chain) {
		Ignore ignore = fieldDescriptor.getAnnotation(Ignore.class);
		if (ignore == null) {
			return chain.isIgnore(entityClass, fieldDescriptor);
		}
		return ignore.value();
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
	public String getName(Class<?> entityClass, FieldDescriptor fieldDescriptor, ObjectRelationalResolver chain) {
		String name = getAnnotationFeldName(fieldDescriptor);
		if (StringUtils.isEmpty(name)) {
			return chain.getName(entityClass, fieldDescriptor);
		}
		return name;
	}

	private String[] getAnnotatedAlias(AnnotatedElement annotatedElement) {
		Alias alias = AnnotatedElementUtils.getMergedAnnotation(annotatedElement, Alias.class);
		return alias == null ? null : alias.value();
	}

	@Override
	public Collection<String> getAliasNames(Class<?> entityClass, FieldDescriptor fieldDescriptor,
			ObjectRelationalResolver chain) {
		Collection<String> names = chain.getAliasNames(entityClass, fieldDescriptor);
		if (names == null) {
			names = new LinkedHashSet<String>();
		}
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
		return names;
	}

	@Override
	public String getName(Class<?> entityClass, ObjectRelationalResolver chain) {
		AnnotationAttributes annotationAttributes = AnnotatedElementUtils.getMergedAnnotationAttributes(entityClass,
				Entity.class);
		if (annotationAttributes == null) {
			return chain.getName(entityClass);
		}

		String name = annotationAttributes.getString("name");
		if (StringUtils.isEmpty(name)) {
			return chain.getName(entityClass);
		}
		return name;
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
	public Collection<String> getAliasNames(Class<?> entityClass, ObjectRelationalResolver chain) {
		Collection<String> list = chain.getAliasNames(entityClass);
		if (list == null) {
			list = new LinkedHashSet<String>(8);
		}
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
		return list;
	}

	@Override
	public Boolean isPrimaryKey(Class<?> entityClass, FieldDescriptor fieldDescriptor, ObjectRelationalResolver chain) {
		if (AnnotatedElementUtils.isAnnotated(fieldDescriptor, PrimaryKey.class)) {
			return true;
		}

		Class<?> clazz = entityClass;
		while (clazz != null && clazz != Object.class) {
			Entity entity = AnnotatedElementUtils.getMergedAnnotation(clazz, Entity.class);
			if (entity != null) {
				for (String name : entity.primaryKeys()) {
					if (fieldDescriptor.getName().equals(name)) {
						return true;
					}
				}
			}
			clazz = clazz.getSuperclass();
		}
		return chain.isPrimaryKey(entityClass, fieldDescriptor);
	}

	@Override
	public Boolean isNullable(Class<?> entityClass, FieldDescriptor fieldDescriptor, ObjectRelationalResolver chain) {
		return AnnotatedElementUtils.isNullable(fieldDescriptor, () -> chain.isNullable(entityClass, fieldDescriptor));
	}

	@Override
	public Boolean isEntity(Class<?> entityClass, FieldDescriptor fieldDescriptor, ObjectRelationalResolver chain) {
		if (AnnotatedElementUtils.isAnnotated(fieldDescriptor, Entity.class)) {
			return true;
		}
		return chain.isEntity(entityClass, fieldDescriptor);
	}

	@Override
	public Boolean isEntity(Class<?> entityClass, ObjectRelationalResolver chain) {
		Class<?> clazz = entityClass;
		while (clazz != null && clazz != Object.class) {
			if (AnnotatedElementUtils.isAnnotated(clazz, Entity.class)) {
				return true;
			}
			clazz = clazz.getSuperclass();
		}
		return chain.isEntity(entityClass);
	}

	@Override
	public Boolean isVersionField(Class<?> entityClass, FieldDescriptor fieldDescriptor,
			ObjectRelationalResolver chain) {
		if (AnnotatedElementUtils.isAnnotated(fieldDescriptor, Version.class)) {
			return true;
		}
		return null;
	}

	@Override
	public Collection<Range<Double>> getNumberRanges(Class<?> entityClass, FieldDescriptor fieldDescriptor,
			ObjectRelationalResolver chain) {
		Collection<Range<Double>> ranges = chain.getNumberRanges(entityClass, fieldDescriptor);
		NumberRange range = AnnotatedElementUtils.getMergedAnnotation(fieldDescriptor, NumberRange.class);
		if (range != null) {
			if (ranges == null) {
				ranges = new LinkedHashSet<>(4);
				ranges.add(Range.closed(range.min(), range.max()));
			}
		}
		return ranges;
	}

	@Override
	public Boolean isAutoIncrement(Class<?> entityClass, FieldDescriptor fieldDescriptor,
			ObjectRelationalResolver chain) {
		if (AnnotatedElementUtils.hasAnnotation(fieldDescriptor, AutoIncrement.class)) {
			return true;
		}

		return chain.isAutoIncrement(entityClass, fieldDescriptor);
	}

	@Override
	public String getComment(Class<?> entityClass, ObjectRelationalResolver chain) {
		Entity entity = AnnotatedElementUtils.getMergedAnnotation(entityClass, Entity.class);
		if (entity != null && StringUtils.hasText(entity.comment())) {
			return entity.comment();
		}

		Comment comment = AnnotatedElementUtils.getMergedAnnotation(entityClass, Comment.class);
		if (comment != null && StringUtils.hasText(comment.value())) {
			return comment.value();
		}
		return chain.getComment(entityClass);
	}

	@Override
	public String getComment(Class<?> entityClass, FieldDescriptor fieldDescriptor, ObjectRelationalResolver chain) {
		Comment comment = AnnotatedElementUtils.getMergedAnnotation(fieldDescriptor, Comment.class);
		if (comment != null && StringUtils.hasText(comment.value())) {
			return comment.value();
		}
		return chain.getComment(entityClass, fieldDescriptor);
	}

	@Override
	public String getCharsetName(Class<?> entityClass, ObjectRelationalResolver chain) {
		return AnnotatedElementUtils.getCharsetName(entityClass, () -> chain.getCharsetName(entityClass));
	}

	@Override
	public String getCharsetName(Class<?> entityClass, FieldDescriptor fieldDescriptor,
			ObjectRelationalResolver chain) {
		return AnnotatedElementUtils.getCharsetName(fieldDescriptor,
				() -> chain.getCharsetName(entityClass, fieldDescriptor));
	}

	@Override
	public Boolean isUnique(Class<?> entityClass, FieldDescriptor fieldDescriptor, ObjectRelationalResolver chain) {
		if (AnnotatedElementUtils.hasAnnotation(fieldDescriptor, Unique.class)) {
			return true;
		}
		return chain.isUnique(entityClass, fieldDescriptor);
	}

	@Override
	public Boolean isIncrement(Class<?> entityClass, FieldDescriptor fieldDescriptor, ObjectRelationalResolver chain) {
		if(AnnotatedElementUtils.hasAnnotation(fieldDescriptor, Increment.class)) {
			return true;
		}
		return chain.isUnique(entityClass, fieldDescriptor);
	}
}
