package run.soeasy.framework.core.transform.clone;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import run.soeasy.framework.core.convert.TypeDescriptor;

@Getter
@Setter
public class PackageCloneTransformer<T> extends CloneTransformer<T> {
	
	@Override
	public boolean canTransform(@NonNull TypeDescriptor sourceTypeDescriptor,
			@NonNull TypeDescriptor targetTypeDescriptor) {
		return (sourceTypeDescriptor.getName().startsWith("java.")
				|| targetTypeDescriptor.getName().startsWith("java."))
				&& super.canTransform(sourceTypeDescriptor, targetTypeDescriptor);
	}
}
