package run.soeasy.framework.core.type;

import java.util.Arrays;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class MultiableInstanceFactory implements InstanceFactory {
	@NonNull
	private final Iterable<? extends InstanceFactory> iterable;

	public MultiableInstanceFactory(@NonNull InstanceFactory... instanceFactories) {
		this(Arrays.asList(instanceFactories));
	}

	@Override
	public boolean canInstantiated(@NonNull ResolvableType requiredType) {
		for (InstanceFactory instanceFactory : iterable) {
			if (instanceFactory.canInstantiated(requiredType)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public Object newInstance(@NonNull ResolvableType requiredType) {
		for (InstanceFactory instanceFactory : iterable) {
			if (instanceFactory.canInstantiated(requiredType)) {
				return instanceFactory.newInstance(requiredType);
			}
		}
		throw new UnsupportedOperationException(String.valueOf(requiredType));
	}
}
