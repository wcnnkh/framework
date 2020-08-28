package scw.tencent.qq;

import java.io.Serializable;

public class Userinfo implements Serializable{
	private static final long serialVersionUID = 1L;
	private String nickname;//用户在QQ空间的昵称。
	private String figureurl;//大小为30×30像素的QQ空间头像URL。
	private String figureurl_1;//大小为50×50像素的QQ空间头像URL。
	private String figureurl_2;//大小为100×100像素的QQ空间头像URL。
	private String figureurl_qq_1;//大小为40×40像素的QQ头像URL。
	private String figureurl_qq_2;//大小为100×100像素的QQ头像URL。需要注意，不是所有的用户都拥有QQ的100x100的头像，但40x40像素则是一定会有。
	private String gender;//性别。 如果获取不到则默认返回"男"
	
	public Userinfo(){
	}
	
	public Userinfo(String nickname, String figureurl, String figureurl_1, String figureurl_2, String figureurl_qq_1, String figureurl_qq_2, String gender){
		this.nickname = nickname;
		this.figureurl = figureurl;
		this.figureurl_1 = figureurl_1;
		this.figureurl_2 = figureurl_2;
		this.figureurl_qq_1 = figureurl_qq_1;
		this.figureurl_qq_2 = figureurl_qq_2;
		this.gender = gender;
	}
	
	public String getNickname() {
		return nickname;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	public String getFigureurl() {
		return figureurl;
	}
	public void setFigureurl(String figureurl) {
		this.figureurl = figureurl;
	}
	public String getFigureurl_1() {
		return figureurl_1;
	}
	public void setFigureurl_1(String figureurl_1) {
		this.figureurl_1 = figureurl_1;
	}
	public String getFigureurl_2() {
		return figureurl_2;
	}
	public void setFigureurl_2(String figureurl_2) {
		this.figureurl_2 = figureurl_2;
	}
	public String getFigureurl_qq_1() {
		return figureurl_qq_1;
	}
	public void setFigureurl_qq_1(String figureurl_qq_1) {
		this.figureurl_qq_1 = figureurl_qq_1;
	}
	public String getFigureurl_qq_2() {
		return figureurl_qq_2;
	}
	public void setFigureurl_qq_2(String figureurl_qq_2) {
		this.figureurl_qq_2 = figureurl_qq_2;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
}
