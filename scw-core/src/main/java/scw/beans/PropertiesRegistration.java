package scw.beans;

import java.util.Properties;

import scw.event.Observable;

public interface PropertiesRegistration {
	String getPrefix();

	boolean isFormat();

	Observable<Properties> getProperties();
}
