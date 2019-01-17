package scw.utils.tencent.weixin.miniprogram.bean;

import java.io.Serializable;

public final class TemplateLibrary implements Serializable{
	private static final long serialVersionUID = 1L;
	private String id;//模板标题id（获取模板标题下的关键词库时需要）
	private String title;//模板标题内容
	
	public TemplateLibrary(){};
	
	public TemplateLibrary(String id, String title){
		this.id = id;
		this.title = title;
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
}
