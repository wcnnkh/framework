package io.basc.framework.orm.support;

import io.basc.framework.orm.EntityMetadata;

public class StandardEntityMetadata implements EntityMetadata {
	private String name;
	private String charsetName;
	private String comment;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCharsetName() {
		return charsetName;
	}

	public void setCharsetName(String charsetName) {
		this.charsetName = charsetName;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}
}
