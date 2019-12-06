package scw.orm;

import java.util.Collection;
import java.util.Map;

import scw.core.instance.CannotInstantiateException;
import scw.core.instance.NoArgsInstanceFactory;

public class DefaultMapper extends AbstractMapper {
	private final ColumnFactory columnFactory;
	private final Collection<? extends SetterFilter> setterFilters;
	private final Collection<? extends GetterFilter> getterFilters;
	private final NoArgsInstanceFactory instanceFactory;

	public DefaultMapper(ColumnFactory columnFactory, Collection<? extends SetterFilter> setterFilters,
			Collection<? extends GetterFilter> getterFilters, NoArgsInstanceFactory instanceFactory) {
		this.columnFactory = columnFactory;
		this.setterFilters = setterFilters;
		this.getterFilters = getterFilters;
		this.instanceFactory = instanceFactory;
	}

	@Override
	public char getPrimaryKeyConnectorCharacter() {
		return ORMUtils.PRIMARY_KEY_CONNECTOR_CHARACTER;
	}

	public <T> T newInstance(Class<T> clazz) {
		if (!instanceFactory.isInstance(clazz)) {
			throw new CannotInstantiateException("无法实例化：" + clazz);
		}

		return instanceFactory.getInstance(clazz);
	}

	public Map<String, Column> getColumnMap(Class<?> clazz) {
		return columnFactory.getColumnMap(clazz);
	}

	@Override
	public Collection<? extends SetterFilter> getSetterFilters() {
		return setterFilters;
	}

	@Override
	public Collection<? extends GetterFilter> getGetterFilters() {
		return getterFilters;
	}
}
