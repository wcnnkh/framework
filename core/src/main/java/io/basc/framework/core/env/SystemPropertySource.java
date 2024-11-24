package io.basc.framework.core.env;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class SystemPropertySource extends SystemProperties implements PropertySource {
	@NonNull
	private final String name;
}
