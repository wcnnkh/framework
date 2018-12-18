package scw.tencent.weixin.miniprogram.bean;

import java.io.Serializable;

public final class Template implements Serializable{
	private static final long serialVersionUID = 1L;
	private String template_id;//	添加至帐号下的模板id，发送小程序模板消息时所需
	private String title;//	模板标题
	private String content;//模板内容
	private String example;//模板内容示例
	public String getTemplate_id() {
		return template_id;
	}
	public void setTemplate_id(String template_id) {
		this.template_id = template_id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getExample() {
		return example;
	}
	public void setExample(String example) {
		this.example = example;
	}
}
