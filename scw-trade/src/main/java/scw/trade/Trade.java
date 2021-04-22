package scw.trade;

import java.io.Serializable;
import java.util.Collections;
import java.util.Map;

import scw.core.utils.StringUtils;
import scw.lang.Nullable;

public class Trade implements Serializable {
	private static final long serialVersionUID = 1L;
	/**
	 * 交易id(一般为流水号或订单号)
	 */
	private String tradeNo;
	
	/**
	 * 交易金额
	 */
	private int tradeAmount;
	/**
	 * 交易方式
	 */
	private String tradeMethod;
	
	/**
	 * 交易发起者的ip
	 */
	@Nullable
	private String ip;
	
	/**
	 * 交易发起时间
	 */
	private long createTime;
	
	/**
	 * 主体,一般是交易描述
	 */
	private String subject;
	
	/**
	 * 扩展数据
	 */
	private Map<String, String> extended;

	public String getTradeNo() {
		return tradeNo;
	}

	public void setTradeNo(String tradeNo) {
		this.tradeNo = tradeNo;
	}

	public int getTradeAmount() {
		return tradeAmount;
	}

	public void setTradeAmount(int tradeAmount) {
		this.tradeAmount = tradeAmount;
	}

	public String getTradeMethod() {
		return tradeMethod;
	}

	public void setTradeMethod(String tradeMethod) {
		this.tradeMethod = tradeMethod;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public Map<String, String> getExtended() {
		if(extended == null){
			return Collections.emptyMap();
		}
		return extended;
	}

	public void setExtended(Map<String, String> extended) {
		this.extended = extended;
	}
	
	public String getTradeAmountDescribe(){
		return StringUtils.formatNothingToYuan(tradeAmount);
	}
}
