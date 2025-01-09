package io.basc.framework.beans;

import java.beans.BeanInfo;
import java.beans.MethodDescriptor;

import io.basc.framework.util.KeyValue;
import io.basc.framework.util.collection.Elements;
import io.basc.framework.util.collection.KeyValues;
import io.basc.framework.util.collection.Elements.ElementsWrapper;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MethodDescriptors implements KeyValues<String, MethodDescriptor>,
		ElementsWrapper<KeyValue<String, MethodDescriptor>, Elements<KeyValue<String, MethodDescriptor>>> {
	@NonNull
	private final BeanInfo beanInfo;

	@Override
	public Elements<KeyValue<String, MethodDescriptor>> getSource() {
		// TODO Auto-generated method stub
		return null;
	}
}
