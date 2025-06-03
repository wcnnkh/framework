package run.soeasy.framework.beans;

import lombok.NonNull;
import run.soeasy.framework.core.transform.object.ObjectProperties;

public class BeanProperties extends ObjectProperties<BeanProperty, BeanPropertyTemplate> {

	public BeanProperties(@NonNull BeanPropertyTemplate template, Object target) {
		super(template, target);
	}
}