package run.soeasy.framework.data.orm;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lombok.Getter;
import lombok.NonNull;
import run.soeasy.framework.beans.BeanMapper;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.domain.KeyValue;
import run.soeasy.framework.core.exchange.Signal;
import run.soeasy.framework.core.mapping.MappingContext;
import run.soeasy.framework.core.mapping.MappingFilter;
import run.soeasy.framework.core.mapping.property.PropertyAccessor;
import run.soeasy.framework.core.mapping.property.PropertyDescriptor;
import run.soeasy.framework.core.mapping.property.PropertyMapping;
import run.soeasy.framework.core.spi.Configurable;
import run.soeasy.framework.core.spi.ServiceDiscoverer;
import run.soeasy.framework.core.streaming.Streamable;
import run.soeasy.framework.core.strings.StringUtils;
import run.soeasy.framework.data.orm.resolver.ConfigurableEntityResolver;
import run.soeasy.framework.data.orm.resolver.EntityResolver;

@Getter
public class ObjectRelationalMapper extends BeanMapper implements Configurable, EntityResolver {
	private final ConfigurableEntityResolver entityResolver = new ConfigurableEntityResolver();

	public ObjectRelationalMapper() {
		this.configure();
	}

	@Override
	public Signal configure(@NonNull ServiceDiscoverer discovery) {
		return entityResolver.configure(discovery);
	}

	@Override
	public Streamable<String> getColumnAlias(@NonNull PropertyDescriptor propertyDescriptor) {
		return entityResolver.getColumnAlias(propertyDescriptor);
	}

	@Override
	public String getColumnName(@NonNull PropertyDescriptor propertyDescriptor) {
		return entityResolver.getColumnName(propertyDescriptor);
	}

	@Override
	public Streamable<String> getEntityAlias(@NonNull TypeDescriptor typeDescriptor) {
		return entityResolver.getEntityAlias(typeDescriptor);
	}

	@Override
	public String getEntityName(@NonNull TypeDescriptor typeDescriptor) {
		return entityResolver.getEntityName(typeDescriptor);
	}

	@Override
	public boolean isExclude(@NonNull TypeDescriptor typeDescriptor, @NonNull PropertyDescriptor propertyDescriptor) {
		return entityResolver.isExclude(typeDescriptor, propertyDescriptor);
	}

	@Override
	public boolean isPrimaryKey(@NonNull TypeDescriptor typeDescriptor,
			@NonNull PropertyDescriptor propertyDescriptor) {
		return entityResolver.isPrimaryKey(typeDescriptor, propertyDescriptor);
	}

	protected Signal doObjectRelationalMapping(
			@NonNull MappingContext<String, PropertyAccessor, PropertyMapping<PropertyAccessor>> sourceContext,
			@NonNull MappingContext<String, PropertyAccessor, PropertyMapping<PropertyAccessor>> targetContext,
			@NonNull Iterable<MappingFilter<String, PropertyAccessor, PropertyMapping<PropertyAccessor>>> filters,
			KeyValue<String, PropertyAccessor> source, PropertyAccessor targetPropertyAccessor) {
		String column = getColumnName(targetPropertyAccessor);
		if (StringUtils.equals(source.getKey(), column)) {
			Signal signal = doMapping(sourceContext.current(source),
					targetContext.current(KeyValue.of(column, targetPropertyAccessor)), filters);
			if (signal.isSuccess()) {
				return signal;
			}
		}

		List<String> alias = getColumnAlias(targetPropertyAccessor).toList();
		if (alias.isEmpty()) {
			return Signal.failure();
		}

		for (String name : alias) {
			if (StringUtils.equals(source.getKey(), name)) {
				Signal signal = doMapping(sourceContext.current(source),
						targetContext.current(KeyValue.of(name, targetPropertyAccessor)), filters);
				if (signal.isSuccess()) {
					return signal;
				}
			}
		}
		return Signal.failure();
	}

	@Override
	protected final Signal doStructureMapping(
			@NonNull MappingContext<String, PropertyAccessor, PropertyMapping<PropertyAccessor>> sourceContext,
			@NonNull MappingContext<String, PropertyAccessor, PropertyMapping<PropertyAccessor>> targetContext,
			@NonNull Iterable<MappingFilter<String, PropertyAccessor, PropertyMapping<PropertyAccessor>>> filters) {
		List<PropertyAccessor> targetElements = targetContext.getMapping().elements().collect(Collectors.toList());
		if (!targetElements.isEmpty()) {
			List<Signal> signals = new ArrayList<Signal>(targetElements.size());
			try (Stream<KeyValue<String, PropertyAccessor>> sourceStream = sourceContext.getMapping().stream()) {
				Iterator<KeyValue<String, PropertyAccessor>> sourceIterator = sourceStream.iterator();
				while (sourceIterator.hasNext()) {
					KeyValue<String, PropertyAccessor> source = sourceIterator.next();
					Iterator<PropertyAccessor> targetIterator = targetElements.iterator();
					while (targetIterator.hasNext()) {
						PropertyAccessor targetPropertyAccessor = targetIterator.next();
						Signal signal = doObjectRelationalMapping(sourceContext, targetContext, filters, source,
								targetPropertyAccessor);
						if (signal.isSuccess()) {
							signals.add(signal);
							targetIterator.remove();
							break;
						}
					}
				}
			}
			Signal signal = Signal.all(Streamable.of(signals));
			if (signal.isSuccess()) {
				return signal;
			}
		}
		return super.doStructureMapping(sourceContext, targetContext, filters);
	}

	public EntityDescriptor<PropertyDescriptor> getEntityDescriptor(@NonNull TypeDescriptor typeDescriptor) {
		return new EntityDescriptor<>(getEntityName(typeDescriptor), getEntityAlias(typeDescriptor), typeDescriptor,
				getObjectTemplate(typeDescriptor.getType()).elements()
						.filter((property) -> !isExclude(typeDescriptor, property))
						.map((property) -> toColumn(typeDescriptor, property)));
	}

	public EntityDescriptor<PropertyAccessor> getEntityDescriptor(@NonNull TypeDescriptor typeDescriptor,
			@NonNull Object entity) {
		return new EntityDescriptor<>(getEntityName(typeDescriptor), getEntityAlias(typeDescriptor), typeDescriptor,
				getMapping(entity, typeDescriptor).elements().filter((property) -> !isExclude(typeDescriptor, property))
						.map((property) -> toColumn(typeDescriptor, property)));
	}

	protected <T extends PropertyDescriptor> ColumnDescriptor<T> toColumn(@NonNull TypeDescriptor typeDescriptor,
			T property) {
		return new ColumnDescriptor<>(property, getColumnName(property), isPrimaryKey(typeDescriptor, property),
				getColumnAlias(property));
	}
}
