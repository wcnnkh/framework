package io.basc.framework.execution.support;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.core.annotation.MergedAnnotations;
import io.basc.framework.execution.Constructor;
import io.basc.framework.execution.Service;
import io.basc.framework.execution.param.Parameters;
import io.basc.framework.util.element.Elements;
import lombok.Data;
import lombok.NonNull;

@Data
public class DefaultService<E extends Constructor> implements Service<E> {
	@NonNull
	private TypeDescriptor returnTypeDescriptor = TypeDescriptor.valueOf(Object.class);
	@NonNull
	private Elements<E> constructors = Elements.empty();
	@NonNull
	private Parameters parameters = new Parameters();

	private volatile MergedAnnotations annotations;
	
	@Override
	public MergedAnnotations getAnnotations() {
		if(annotations == null) {
			synchronized (this) {
				if(annotations == null) {
					annotations = createAnnotations();
				}
			}
		}
		return annotations;
	}
	
	protected MergedAnnotations createAnnotations() {
		//TODO 还未开发
		return null;
	}
}
