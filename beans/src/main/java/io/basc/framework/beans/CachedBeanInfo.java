package io.basc.framework.beans;

import java.awt.Image;
import java.beans.BeanDescriptor;
import java.beans.BeanInfo;
import java.beans.EventSetDescriptor;
import java.beans.MethodDescriptor;
import java.beans.PropertyDescriptor;

import lombok.Data;
import lombok.NonNull;

@Data
public class CachedBeanInfo implements BeanInfo {
	@NonNull
	private final BeanInfo source;
	private final BeanPropertyDescriptors propertyDescriptors;

	public CachedBeanInfo(@NonNull BeanInfo source) {
		this.source = source;
		this.propertyDescriptors = new BeanPropertyDescriptors(source);
	}

	public BeanPropertyDescriptors getSharedPropertyDescriptors() {
		return propertyDescriptors;
	}

	@Override
	public BeanDescriptor getBeanDescriptor() {
		return source.getBeanDescriptor();
	}

	@Override
	public EventSetDescriptor[] getEventSetDescriptors() {
		return source.getEventSetDescriptors();
	}

	@Override
	public int getDefaultEventIndex() {
		return source.getDefaultEventIndex();
	}

	@Override
	public PropertyDescriptor[] getPropertyDescriptors() {
		return propertyDescriptors.getElements().toArray(PropertyDescriptor[]::new);
	}

	@Override
	public int getDefaultPropertyIndex() {
		return source.getDefaultEventIndex();
	}

	@Override
	public MethodDescriptor[] getMethodDescriptors() {
		return source.getMethodDescriptors();
	}

	@Override
	public BeanInfo[] getAdditionalBeanInfo() {
		return source.getAdditionalBeanInfo();
	}

	@Override
	public Image getIcon(int iconKind) {
		return source.getIcon(iconKind);
	}
}
