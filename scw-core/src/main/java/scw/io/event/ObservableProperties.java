package scw.io.event;

import java.util.Properties;

import scw.convert.EmptyConverter;

public class ObservableProperties extends ConvertibleObservableProperties<Properties>{

	public ObservableProperties() {
		super(new EmptyConverter<Properties>());
	}

}
