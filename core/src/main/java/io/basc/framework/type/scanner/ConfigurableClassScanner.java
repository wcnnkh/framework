package io.basc.framework.type.scanner;

public interface ConfigurableClassScanner extends ClassScanner {
	void addClassScanner(ClassScanner classScanner);
}
