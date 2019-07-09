package scw.utils.tencent.weixin.miniprogram;

import java.io.Serializable;

public final class Keyword implements Serializable {
	private static final long serialVersionUID = 1L;
	private String keyword_id;// keyword_id
	private String name;// name
	private String example;// example

	Keyword() {
	};

	public Keyword(String keyword_id, String name, String example) {
		this.keyword_id = keyword_id;
		this.name = name;
		this.example = example;
	}

	public String getKeyword_id() {
		return keyword_id;
	}

	public String getName() {
		return name;
	}

	public String getExample() {
		return example;
	}
}
