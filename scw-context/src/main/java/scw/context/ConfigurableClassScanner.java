package scw.context;

public interface ConfigurableClassScanner extends ClassScanner {
	void addClassScanner(ClassScanner classScanner);
}
