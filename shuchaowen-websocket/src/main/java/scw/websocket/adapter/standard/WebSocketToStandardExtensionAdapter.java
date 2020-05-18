package scw.websocket.adapter.standard;

import java.util.ArrayList;
import java.util.List;

import javax.websocket.Extension;

import scw.websocket.WebSocketExtension;

public class WebSocketToStandardExtensionAdapter implements Extension {
	private final String name;

	private final List<Parameter> parameters = new ArrayList<Parameter>();

	public WebSocketToStandardExtensionAdapter(final WebSocketExtension extension) {
		this.name = extension.getName();
		for (final String paramName : extension.getParameters().keySet()) {
			this.parameters.add(new Parameter() {
				public String getName() {
					return paramName;
				}

				public String getValue() {
					return extension.getParameters().get(paramName);
				}
			});
		}
	}

	public String getName() {
		return this.name;
	}

	public List<Parameter> getParameters() {
		return this.parameters;
	}
}
