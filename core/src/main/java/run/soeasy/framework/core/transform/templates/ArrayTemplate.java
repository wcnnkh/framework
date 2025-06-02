package run.soeasy.framework.core.transform.templates;

import lombok.NonNull;
import run.soeasy.framework.core.collection.ArrayDictionary;
import run.soeasy.framework.core.convert.value.AccessibleDescriptor;
import run.soeasy.framework.core.domain.KeyValue;

public class ArrayTemplate<E extends AccessibleDescriptor, W extends Template<E>>
		extends ArrayDictionary<Object, E, KeyValue<Object, E>, W> implements TemplateWrapper<E, W> {

	public ArrayTemplate(@NonNull W source, boolean uniqueness) {
		super(source, uniqueness);
	}

	@Override
	public Template<E> asArray(boolean uniqueness) {
		return isUniqueness() == uniqueness ? this : getSource().asArray(uniqueness);
	}
}
