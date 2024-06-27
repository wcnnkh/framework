package io.basc.framework.generator.string;

import io.basc.framework.generator.Generator;

@FunctionalInterface
public interface StringGenerator extends Generator<String> {
	@Override
	String next();
}
