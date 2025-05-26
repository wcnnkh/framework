package run.soeasy.framework.core.lang;

import java.util.EnumSet;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import run.soeasy.framework.core.spi.ConfigurableServices;

@Getter
@Setter
public class ConfigurableInstanceFactory extends ConfigurableServices<InstanceFactory> implements InstanceFactory {
	@NonNull
	private EnumSet<SupportedInstanceFactory> extendFactoryTypes = EnumSet.allOf(SupportedInstanceFactory.class);

	public ConfigurableInstanceFactory() {
		setServiceClass(InstanceFactory.class);
	}

	@Override
	public boolean canInstantiated(@NonNull ResolvableType requiredType) {
		for (InstanceFactory instanceFactory : this) {
			if (instanceFactory.canInstantiated(requiredType)) {
				return true;
			}
		}

		for (SupportedInstanceFactory instanceFactory : extendFactoryTypes) {
			if (instanceFactory.canInstantiated(requiredType)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public Object newInstance(@NonNull ResolvableType requiredType) {
		for (InstanceFactory instanceFactory : this) {
			if (instanceFactory.canInstantiated(requiredType)) {
				return instanceFactory.newInstance(requiredType);
			}
		}

		for (SupportedInstanceFactory instanceFactory : extendFactoryTypes) {
			if (instanceFactory.canInstantiated(requiredType)) {
				return instanceFactory.newInstance(requiredType);
			}
		}
		throw new UnsupportedOperationException(String.valueOf(requiredType));
	}

}
