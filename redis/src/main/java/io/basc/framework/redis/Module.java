package io.basc.framework.redis;

import java.io.Serializable;

public class Module implements Serializable {
	private static final long serialVersionUID = 1L;
	/**
	 * Name of the module
	 */
	private String name;
	/**
	 * Version of the module
	 */
	private int ver;

	public Module() {
	}

	public Module(String name, int ver) {
		this.name = name;
		this.ver = ver;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getVer() {
		return ver;
	}

	public void setVer(int ver) {
		this.ver = ver;
	}
}
