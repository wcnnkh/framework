package run.soeasy.framework.beans;

import lombok.NonNull;
import run.soeasy.framework.core.transform.property.ObjectMapping;

public class BeanMapping extends ObjectMapping<BeanProperty, BeanTemplate> {

	public BeanMapping(@NonNull BeanTemplate template, Object target) {
		super(template, target);
	}
}