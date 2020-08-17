package scw.core.parameter;


public interface ParameterDescriptors extends Iterable<ParameterDescriptor> {
	Class<?> getDeclaringClass();
	
	int size();
	
	Object getSource();
	
	Class<?>[] getTypes();
}
