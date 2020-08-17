package scw.core.parameter;



public interface ParameterFactory{
	boolean isAccept(ParameterDescriptors parameterDescriptors);
	
	Object[] getParameters(ParameterDescriptors parameterDescriptors);
}
