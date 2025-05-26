package run.soeasy.framework.beans;

import java.beans.BeanInfo;
import java.beans.MethodDescriptor;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import run.soeasy.framework.core.collection.Elements;
import run.soeasy.framework.core.collection.KeyValues;
import run.soeasy.framework.core.collection.Elements.ElementsWrapper;
import run.soeasy.framework.core.domain.KeyValue;

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
