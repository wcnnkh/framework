package run.soeasy.framework.beans;

import lombok.NonNull;
import run.soeasy.framework.core.transform.object.ObjectProperties;

public class BeanMapping extends ObjectProperties<BeanProperty, BeanTemplate> {

	public BeanMapping(@NonNull BeanTemplate template, Object target) {
		super(template, target);
	}
}