package run.soeasy.framework.data.orm;

import lombok.NonNull;
import run.soeasy.framework.beans.BeanMapper;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.mapping.MappingContext;
import run.soeasy.framework.core.mapping.MappingFilter;
import run.soeasy.framework.core.mapping.property.PropertyAccessor;
import run.soeasy.framework.core.mapping.property.PropertyDescriptor;
import run.soeasy.framework.core.mapping.property.PropertyMapping;
import run.soeasy.framework.core.streaming.Streamable;
import run.soeasy.framework.data.orm.annotation.AnnotationColumnResolver;
import run.soeasy.framework.data.orm.annotation.AnnotationEntityResolver;
import run.soeasy.framework.data.orm.annotation.AnnotationPrimaryKeyResolver;
import run.soeasy.framework.data.orm.resolver.ColumnResolver;
import run.soeasy.framework.data.orm.resolver.EntityResolver;
import run.soeasy.framework.data.orm.resolver.PrimaryKeyResolver;

public class ObjectRelationalMapper extends BeanMapper implements EntityResolver, ColumnResolver, PrimaryKeyResolver {
	@NonNull
	private EntityResolver entityResolver = new AnnotationEntityResolver();
	@NonNull
	private ColumnResolver columnResolver = new AnnotationColumnResolver();
	@NonNull
	private PrimaryKeyResolver primaryKeyResolver = new AnnotationPrimaryKeyResolver();

	@Override
	protected boolean doFilterableMapping(
			@NonNull MappingContext<String, PropertyAccessor, PropertyMapping<PropertyAccessor>> sourceContext,
			@NonNull MappingContext<String, PropertyAccessor, PropertyMapping<PropertyAccessor>> targetContext,
			@NonNull Iterable<MappingFilter<String, PropertyAccessor, PropertyMapping<PropertyAccessor>>> filters) {
		if (sourceContext.hasKeyValue() && targetContext.hasKeyValue()) {
			return super.doFilterableMapping(sourceContext, targetContext, filters);
		}

		return super.doFilterableMapping(sourceContext, targetContext, filters);
	}

	@Override
	public boolean isPrimaryKey(@NonNull TypeDescriptor entityDescriptor,
			@NonNull PropertyDescriptor propertyDescriptor) {
		return primaryKeyResolver.isPrimaryKey(entityDescriptor, propertyDescriptor);
	}

	@Override
	public String getColumnName(@NonNull TypeDescriptor entityDescriptor,
			@NonNull PropertyDescriptor propertyDescriptor) {
		return columnResolver.getColumnName(entityDescriptor, propertyDescriptor);
	}

	@Override
	public Streamable<String> getColumnAlias(@NonNull TypeDescriptor entityDescriptor,
			@NonNull PropertyDescriptor propertyDescriptor) {
		return columnResolver.getColumnAlias(entityDescriptor, propertyDescriptor);
	}

	@Override
	public String getEntityName(@NonNull TypeDescriptor entityDescriptor) {
		return entityResolver.getEntityName(entityDescriptor);
	}

	@Override
	public Streamable<String> getEntityAlias(@NonNull TypeDescriptor entityDescriptor) {
		return entityResolver.getEntityAlias(entityDescriptor);
	}

	@Override
	public boolean isExclude(@NonNull TypeDescriptor entityDescriptor, @NonNull PropertyDescriptor propertyDescriptor) {
		return columnResolver.isExclude(entityDescriptor, propertyDescriptor);
	}

	public Streamable<PropertyDescriptor> getColumns(@NonNull TypeDescriptor entityDescriptor) {
		return null;
	}

	public Streamable<PropertyAccessor> getProperties(@NonNull Object entity,
			@NonNull TypeDescriptor entityDescriptor) {
		return null;
	}
}
