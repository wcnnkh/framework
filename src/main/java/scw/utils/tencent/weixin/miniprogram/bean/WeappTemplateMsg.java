package scw.utils.tencent.weixin.miniprogram.bean;

import java.io.Serializable;

/**
 * 小程序模板消息相关的信息
 * @author shuchaowen
 *
 */
public final class WeappTemplateMsg implements Serializable{
	private static final long serialVersionUID = 1L;
	private String template_id;//小程序模板ID
	private String page;//点击模板卡片后的跳转页面，仅限本小程序内的页面。支持带参数,（示例index?foo=bar）。该字段不填则模板无跳转。
	private String form_id;//小程序模板消息formid
	private String data;//小程序模板数据
	private String emphasis_keyword;//小程序模板放大关键词
	
	public WeappTemplateMsg(){};
	
	public WeappTemplateMsg(String template_id, String page, String form_id, String data, String emphasis_keyword){
		this.template_id = template_id;
		this.page = page;
		this.form_id = form_id;
		this.data = data;
		this.emphasis_keyword = emphasis_keyword;
	}
	
	public String getTemplate_id() {
		return template_id;
	}
	public void setTemplate_id(String template_id) {
		this.template_id = template_id;
	}
	public String getPage() {
		return page;
	}
	public void setPage(String page) {
		this.page = page;
	}
	public String getForm_id() {
		return form_id;
	}
	public void setForm_id(String form_id) {
		this.form_id = form_id;
	}
	public String getData() {
		return data;
	}
	public void setData(String data) {
		this.data = data;
	}
	public String getEmphasis_keyword() {
		return emphasis_keyword;
	}
	public void setEmphasis_keyword(String emphasis_keyword) {
		this.emphasis_keyword = emphasis_keyword;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
}
