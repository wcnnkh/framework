package run.soeasy.framework.core.transform.clone;

import java.util.function.BiPredicate;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.transform.IdentityTransformer;
import run.soeasy.framework.core.transform.TransformingConverter;

@Getter
@Setter
@RequiredArgsConstructor
public class CloneTransformer<T> extends TransformingConverter<T, T> implements IdentityTransformer<T> {
	@NonNull
	private final BiPredicate<? super TypeDescriptor, ? super TypeDescriptor> typePredicate;

	public CloneTransformer() {
		this((s, t) -> true);
	}

	@Override
	public boolean canTransform(@NonNull TypeDescriptor sourceTypeDescriptor,
			@NonNull TypeDescriptor targetTypeDescriptor) {
		return typePredicate.test(sourceTypeDescriptor, targetTypeDescriptor)
				&& IdentityTransformer.super.canTransform(sourceTypeDescriptor, targetTypeDescriptor)
				&& super.canTransform(sourceTypeDescriptor, targetTypeDescriptor);
	}
}
