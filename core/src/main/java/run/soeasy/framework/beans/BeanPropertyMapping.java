package run.soeasy.framework.beans;

import lombok.NonNull;
import run.soeasy.framework.core.transform.property.ObjectProperties;

public class BeanPropertyMapping extends ObjectProperties<BeanProperty, BeanPropertyTemplate> {

	public BeanPropertyMapping(@NonNull BeanPropertyTemplate template, Object target) {
		super(template, target);
	}
}