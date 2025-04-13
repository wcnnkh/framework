package run.soeasy.framework.beans;

import java.awt.Image;
import java.beans.BeanDescriptor;
import java.beans.BeanInfo;
import java.beans.EventSetDescriptor;
import java.beans.MethodDescriptor;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import run.soeasy.framework.core.KeyValue;
import run.soeasy.framework.core.collection.Elements;
import run.soeasy.framework.core.collection.Reloadable;
import run.soeasy.framework.core.transform.mapping.MappingDescriptor;

@Getter
public class BeanMapping implements BeanInfo, MappingDescriptor<BeanPropertyDescriptor>, Reloadable {
	private final Class<?> beanClass;
	private final BeanInfo beanInfo;
	@Getter(AccessLevel.NONE)
	private volatile LinkedHashMap<String, List<BeanPropertyDescriptor>> propertyMap;

	public BeanMapping(@NonNull Class<?> beanClass, @NonNull BeanInfo beanInfo) {
		this.beanClass = beanClass;
		this.beanInfo = beanInfo;
	}

	@Override
	public String getName() {
		return beanInfo.getBeanDescriptor().getName();
	}

	@Override
	public void reload() {
		reloadPropertyDescriptors(false);
	}

	public boolean reloadPropertyDescriptors(boolean force) {
		if (propertyMap == null || force) {
			synchronized (this) {
				if (propertyMap == null || force) {
					PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
					LinkedHashMap<String, List<BeanPropertyDescriptor>> map = new LinkedHashMap<>(
							propertyDescriptors.length, 1);
					for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
						List<BeanPropertyDescriptor> list = map.get(propertyDescriptor.getName());
						if (list == null) {
							list = new ArrayList<>(propertyDescriptors.length);
							map.put(propertyDescriptor.getName(), list);
						}

						BeanPropertyDescriptor beanFieldDescriptor = new BeanPropertyDescriptor(beanClass,
								propertyDescriptor);
						list.add(beanFieldDescriptor);
					}

					for (Entry<String, List<BeanPropertyDescriptor>> entry : map.entrySet()) {
						entry.setValue(Arrays.asList(entry.getValue().toArray(new BeanPropertyDescriptor[0])));
					}
					this.propertyMap = map;
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public Elements<KeyValue<String, BeanPropertyDescriptor>> getKeyValues(String key) {
		reloadPropertyDescriptors(false);
		List<BeanPropertyDescriptor> list = propertyMap.get(key);
		if (list == null) {
			return Elements.empty();
		}

		return Elements.of(list).map((e) -> KeyValue.of(key, e));
	}

	@Override
	public Elements<BeanPropertyDescriptor> getValues(String key) {
		reloadPropertyDescriptors(false);
		List<BeanPropertyDescriptor> list = propertyMap.get(key);
		return list == null ? Elements.empty() : Elements.of(list);
	}

	@Override
	public Elements<BeanPropertyDescriptor> getElements() {
		reloadPropertyDescriptors(false);
		return Elements.of(() -> propertyMap.values().stream().flatMap((e) -> e.stream()));
	}

	@Override
	public BeanDescriptor getBeanDescriptor() {
		return beanInfo.getBeanDescriptor();
	}

	@Override
	public EventSetDescriptor[] getEventSetDescriptors() {
		return beanInfo.getEventSetDescriptors();
	}

	@Override
	public int getDefaultEventIndex() {
		return beanInfo.getDefaultEventIndex();
	}

	@Override
	public PropertyDescriptor[] getPropertyDescriptors() {
		return getElements().map((e) -> e.getPropertyDescriptor()).toArray(new PropertyDescriptor[0]);
	}

	@Override
	public int getDefaultPropertyIndex() {
		return beanInfo.getDefaultEventIndex();
	}

	@Override
	public MethodDescriptor[] getMethodDescriptors() {
		return beanInfo.getMethodDescriptors();
	}

	@Override
	public BeanInfo[] getAdditionalBeanInfo() {
		return beanInfo.getAdditionalBeanInfo();
	}

	@Override
	public Image getIcon(int iconKind) {
		return beanInfo.getIcon(iconKind);
	}
}
