package scw.orm.sql.annotation;

import scw.core.annotation.AnnotatedElementUtils;
import scw.core.utils.ClassUtils;
import scw.core.utils.StringUtils;
import scw.orm.annotation.FieldPropertyDescriptor;
import scw.orm.sql.Column;

public class FieldColumn extends FieldPropertyDescriptor implements Column{

	public FieldColumn(scw.mapper.Field field) {
		super(field);
	}

	@Override
	public boolean isAutoIncrement() {
		return AnnotatedElementUtils.isAnnotated(field, AutoIncrement.class);
	}

	@Override
	public String getComment() {
		return AnnotatedElementUtils.getDescription(field);
	}

	@Override
	public boolean isUnique() {
		return AnnotatedElementUtils.isAnnotated(field, Unique.class);
	}

	@Override
	public String getIndexName() {
		Index index = AnnotatedElementUtils.getMergedAnnotation(field, Index.class);
		if(index == null) {
			return null;
		}
		
		if(StringUtils.isNotEmpty(index.name())) {
			return index.name();
		}
		return getName();
	}
	
	@Override
	public String getCharsetName() {
		return AnnotatedElementUtils.getCharsetName(field, null);
	}

	@Override
	public boolean isNullable() {
		return AnnotatedElementUtils.isNullable(field, ()-> !isPrimaryKey());
	}

	@Override
	public Class<?> getType() {
		Class<?> type = field.getGetter().getType();
		if(ClassUtils.isPrimitiveOrWrapper(type) || type == String.class || type.getName().startsWith("java.sql")) {
			return type;
		}else {
			return String.class;
		}
	}

	@Override
	public Long getMaxLength() {
		return null;
	}
}
