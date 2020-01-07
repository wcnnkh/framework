package scw.net.header;

import scw.util.MultiValueMap;

public abstract class AbstractMultiValueHeadersWriteOnly implements MultiValueHeadersWriteOnly {

	protected abstract MultiValueMap<String, String> getHeaderMap();

	public void setHeader(String name, String value) {
		getHeaderMap().set(name, value);
	}

	public void addHeader(String name, String value) {
		getHeaderMap().add(name, value);
	}

}
