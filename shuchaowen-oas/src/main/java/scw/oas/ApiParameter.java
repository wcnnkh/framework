package scw.oas;

import java.util.List;

public interface ApiParameter extends ApiDescription {
	boolean isRequired();

	String getType();

	Object getDefaultValue();

	int getMaxLength();

	List<? extends ApiParameter> getSubList();
}
