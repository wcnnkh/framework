package scw.tencent.weixin.bean;

import java.io.Serializable;

/**
 * 公众号模板消息相关的信息
 * @author shuchaowen
 *
 */
public final class MpTemplateMsg implements Serializable{
	private static final long serialVersionUID = 1L;
	private String appid;//公众号appid，要求与小程序有绑定且同主体
	private String template_id;//公众号模板id
	private String url;//公众号模板消息所要跳转的url
	private String miniprogram;//公众号模板消息所要跳转的小程序，小程序的必须与公众号具有绑定关系
	private String data;//	公众号模板消息的数据
	public String getAppid() {
		return appid;
	}
	public void setAppid(String appid) {
		this.appid = appid;
	}
	public String getTemplate_id() {
		return template_id;
	}
	public void setTemplate_id(String template_id) {
		this.template_id = template_id;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getMiniprogram() {
		return miniprogram;
	}
	public void setMiniprogram(String miniprogram) {
		this.miniprogram = miniprogram;
	}
	public String getData() {
		return data;
	}
	public void setData(String data) {
		this.data = data;
	}
}
