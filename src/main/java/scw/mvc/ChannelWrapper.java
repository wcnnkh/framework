package scw.mvc;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;

import scw.logger.Logger;

public class ChannelWrapper implements Channel {
	private final Channel channel;

	public ChannelWrapper(Channel channel) {
		this.channel = channel;
	}

	public Logger getLogger() {
		return channel.getLogger();
	}

	public boolean isLogEnabled() {
		return channel.isLogEnabled();
	}

	public void log(String format, Object... args) {
		channel.log(format, args);
	}

	public Object getAttribute(String name) {
		return channel.getAttribute(name);
	}

	public Enumeration<String> getAttributeNames() {
		return channel.getAttributeNames();
	}

	public void setAttribute(String name, Object o) {
		channel.setAttribute(name, o);
	}

	public void removeAttribute(String name) {
		channel.removeAttribute(name);
	}

	public long getCreateTime() {
		return channel.getCreateTime();
	}

	public Object getParameter(ParameterDefinition parameterDefinition) {
		return channel.getParameter(parameterDefinition);
	}

	public void write(Object obj) throws Throwable {
		channel.write(obj);
	}

	public OutputStream getOutputStream() throws IOException {
		return channel.getOutputStream();
	}

	public InputStream getInputStream() throws IOException {
		return channel.getInputStream();
	}

	public String getController() {
		return channel.getController();
	}

	public <T> T getBean(Class<T> type) {
		return channel.getBean(type);
	}

}
