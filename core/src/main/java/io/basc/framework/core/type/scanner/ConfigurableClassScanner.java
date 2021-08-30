package io.basc.framework.core.type.scanner;

public interface ConfigurableClassScanner extends ClassScanner {
	void addClassScanner(ClassScanner classScanner);
}
