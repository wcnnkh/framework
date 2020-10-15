package scw.tencent.wx.pay;

import java.io.Serializable;

public class GethbinfoRequest implements Serializable{
	private static final long serialVersionUID = 1L;
	private String appid;
	private BillType bill_type;
	private String mch_billno;
	public String getAppid() {
		return appid;
	}
	public void setAppid(String appid) {
		this.appid = appid;
	}
	public BillType getBill_type() {
		return bill_type;
	}
	public void setBill_type(BillType bill_type) {
		this.bill_type = bill_type;
	}
	public String getMch_billno() {
		return mch_billno;
	}
	public void setMch_billno(String mch_billno) {
		this.mch_billno = mch_billno;
	}
}
