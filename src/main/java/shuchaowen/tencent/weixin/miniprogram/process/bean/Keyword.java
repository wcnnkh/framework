package shuchaowen.tencent.weixin.miniprogram.process.bean;

public class Keyword {
	private String keyword_id;//keyword_id
	private String name;//name
	private String example;//example
	
	public Keyword(){};
	
	public Keyword(String keyword_id, String name, String example){
		this.keyword_id = keyword_id;
		this.name = name;
		this.example = example;
	}
	
	public String getKeyword_id() {
		return keyword_id;
	}
	public void setKeyword_id(String keyword_id) {
		this.keyword_id = keyword_id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getExample() {
		return example;
	}
	public void setExample(String example) {
		this.example = example;
	}
}
