package run.soeasy.framework.core.transform.indexed;

import lombok.NonNull;
import run.soeasy.framework.core.KeyValue;
import run.soeasy.framework.core.collection.RandomAccessKeyValues;
import run.soeasy.framework.core.transform.indexed.IndexedTemplate.IndexedTemplateWrapper;

public class RandomAccessIndexedTemplate<T extends IndexedDescriptor, W extends IndexedTemplate<T>>
		extends RandomAccessKeyValues<Object, T, KeyValue<Object, T>, W> implements IndexedTemplateWrapper<T, W> {

	public RandomAccessIndexedTemplate(@NonNull W source) {
		super(source);
	}

	@Override
	public IndexedTemplate<T> randomAccess() {
		return this;
	}
}
