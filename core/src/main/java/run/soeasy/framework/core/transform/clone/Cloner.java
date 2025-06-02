package run.soeasy.framework.core.transform.clone;

import lombok.NonNull;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.transform.TransformerRegistry;

public class Cloner extends TransformerRegistry{
	@Override
	public boolean canTransform(@NonNull TypeDescriptor sourceTypeDescriptor,
			@NonNull TypeDescriptor targetTypeDescriptor) {
		// TODO Auto-generated method stub
		return super.canTransform(sourceTypeDescriptor, targetTypeDescriptor);
	}
}
