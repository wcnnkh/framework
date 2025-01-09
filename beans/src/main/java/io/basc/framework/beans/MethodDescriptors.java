package io.basc.framework.beans;

import java.beans.BeanInfo;
import java.beans.MethodDescriptor;

import io.basc.framework.util.Elements;
import io.basc.framework.util.Elements.ElementsWrapper;
import io.basc.framework.util.KeyValue;
import io.basc.framework.util.KeyValues;
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
