package io.basc.framework.spring.context;

import java.util.LinkedHashMap;

import org.springframework.core.env.MapPropertySource;

public class EditablePropertySource extends MapPropertySource {

	public EditablePropertySource(String name) {
		super(name, new LinkedHashMap<>());
	}
}
