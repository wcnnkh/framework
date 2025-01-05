package io.basc.framework.beans;

import java.beans.BeanInfo;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Stream;

import io.basc.framework.util.Elements;
import io.basc.framework.util.KeyValue;
import io.basc.framework.util.KeyValues;
import io.basc.framework.util.Listable;
import io.basc.framework.util.Reloadable;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PropertyDescriptors
		implements KeyValues<String, PropertyDescriptor>, Reloadable, Listable<PropertyDescriptor> {
	@NonNull
	private final BeanInfo beanInfo;
	private volatile LinkedHashMap<String, List<PropertyDescriptor>> cacheMap;

	public BeanInfo getBeanInfo() {
		return beanInfo;
	}

	@Override
	public Elements<PropertyDescriptor> getElements() {
		reload(false);
		return Elements.of(() -> cacheMap.values().stream().flatMap((e) -> e.stream()));
	}

	@Override
	public boolean isEmpty() {
		reload(false);
		return cacheMap.isEmpty();
	}

	@Override
	public Iterator<KeyValue<String, PropertyDescriptor>> iterator() {
		return getElements().map((e) -> KeyValue.of(e.getName(), e)).iterator();
	}

	@Override
	public void reload() {
		reload(true);
	}

	public void reload(boolean force) {
		if (cacheMap == null || force) {
			synchronized (this) {
				if (cacheMap == null || force) {
					PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
					LinkedHashMap<String, List<PropertyDescriptor>> map = new LinkedHashMap<>(
							propertyDescriptors.length, 1);
					for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
						List<PropertyDescriptor> list = map.get(propertyDescriptor.getName());
						if (list == null) {
							list = new ArrayList<>(propertyDescriptors.length);
							map.put(propertyDescriptor.getName(), list);
						}
						list.add(propertyDescriptor);
					}

					for (Entry<String, List<PropertyDescriptor>> entry : map.entrySet()) {
						entry.setValue(Arrays.asList(entry.getValue().toArray(new PropertyDescriptor[0])));
					}
					this.cacheMap = map;
				}
			}
		}
	}

	@Override
	public Elements<PropertyDescriptor> getValues(String key) {
		reload(false);
		List<PropertyDescriptor> list = cacheMap.get(key);
		return list == null ? Elements.empty() : Elements.of(list);
	}

	@Override
	public Stream<KeyValue<String, PropertyDescriptor>> stream() {
		return getElements().map((e) -> KeyValue.of(e.getName(), e)).stream();
	}
}
