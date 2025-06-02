package run.soeasy.framework.core.transform.templates;

import run.soeasy.framework.core.collection.MapDictionary;
import run.soeasy.framework.core.convert.value.AccessibleDescriptor;
import run.soeasy.framework.core.domain.KeyValue;

public class MapTemplate<E extends AccessibleDescriptor, W extends Template<E>>
		extends MapDictionary<Object, E, KeyValue<Object, E>, W> implements TemplateWrapper<E, W> {

	public MapTemplate(W source, boolean uniqueness) {
		super(source, false, uniqueness);
	}

	@Override
	public Template<E> asMap(boolean uniqueness) {
		return isUniqueness() == uniqueness ? this : getSource().asMap(uniqueness);
	}
}
