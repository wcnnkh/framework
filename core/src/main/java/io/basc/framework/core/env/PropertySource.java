package io.basc.framework.core.env;

import io.basc.framework.util.alias.Named;
import lombok.NonNull;

public interface PropertySource extends Properties, Named {

	default PropertySource rename(@NonNull String name) {
		return new NamedProperties<>(name, this);
	}
}
