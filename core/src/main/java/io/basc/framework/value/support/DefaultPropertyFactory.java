package io.basc.framework.value.support;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import io.basc.framework.env.PropertyWrapper;
import io.basc.framework.event.ChangeEvent;
import io.basc.framework.event.NamedEventDispatcher;
import io.basc.framework.event.support.SimpleStringNamedEventDispatcher;
import io.basc.framework.lang.Nullable;
import io.basc.framework.util.Assert;
import io.basc.framework.util.MultiIterator;
import io.basc.framework.value.ConfigurablePropertyFactory;
import io.basc.framework.value.PropertyFactory;
import io.basc.framework.value.Value;

public class DefaultPropertyFactory extends DefaultValueFactory<String, PropertyFactory>
		implements ConfigurablePropertyFactory {
	private final PropertyWrapper propertyWrapper;

	public DefaultPropertyFactory() {
		this(null);
	}

	public DefaultPropertyFactory(@Nullable PropertyWrapper propertyWrapper) {
		this(null, new SimpleStringNamedEventDispatcher<>(), propertyWrapper);
	}

	public DefaultPropertyFactory(@Nullable NamedEventDispatcher<String, ChangeEvent<String>> eventDispatcher,
			@Nullable PropertyWrapper propertyWrapper) {
		this(null, eventDispatcher, propertyWrapper);
	}

	public DefaultPropertyFactory(@Nullable Map<String, Value> valueMap, @Nullable PropertyWrapper propertyWrapper) {
		this(valueMap, new SimpleStringNamedEventDispatcher<>(), propertyWrapper);
	}

	public DefaultPropertyFactory(@Nullable Map<String, Value> valueMap,
			@Nullable NamedEventDispatcher<String, ChangeEvent<String>> eventDispatcher,
			@Nullable PropertyWrapper propertyWrapper) {
		super(valueMap, eventDispatcher);
		this.propertyWrapper = propertyWrapper;
	}

	public Iterator<String> iterator() {
		List<Iterator<String>> iterators = new LinkedList<Iterator<String>>();
		iterators.add(getValueMap().keySet().iterator());
		for (PropertyFactory factory : getTandemFactories()) {
			iterators.add(factory.iterator());
		}
		return new MultiIterator<String>(iterators);
	}

	public boolean put(String key, Object value) {
		if (propertyWrapper == null) {
			return super.put(key, value);
		}

		Assert.requiredArgument(key != null, "key");
		Assert.requiredArgument(value != null, "value");
		return super.put(key, propertyWrapper.wrap(key, value));
	}

	public boolean putIfAbsent(String key, Object value) {
		if (propertyWrapper == null) {
			return super.putIfAbsent(key, value);
		}
		Assert.requiredArgument(key != null, "key");
		Assert.requiredArgument(value != null, "value");
		return super.putIfAbsent(key, propertyWrapper.wrap(key, value));
	}
}
