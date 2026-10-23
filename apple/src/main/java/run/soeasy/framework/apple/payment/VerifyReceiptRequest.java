package run.soeasy.framework.apple.payment;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

/**
 * <a href=
 * "https://developer.apple.com/documentation/appstorereceipts/requestbody">文档</a>
 * 
 * @author soeasy.run
 *
 */
@Data
public class VerifyReceiptRequest implements Serializable {
	private static final long serialVersionUID = 1L;
	/**
	 * Base64编码的收据数据。
	 */
	@JsonProperty("receipt-data")
	private String receiptData;
	/**
	 * 将此值设置为，true以使响应仅包括任何订阅的最新续订交易。仅对包含自动续订的应用收据使用此字段。
	 */
	@JsonProperty("exclude-old-transactions")
	private Boolean excludeOldTransactions;
}
