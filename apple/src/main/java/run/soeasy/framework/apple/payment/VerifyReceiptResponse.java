package run.soeasy.framework.apple.payment;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

/**
 * <a href="https://developer.apple.com/documentation/appstorereceipts/responsebody">Apple 收据验证响应</a>
 * 
 * 封装Apple应用内购买收据验证服务的响应结构，提供对验证结果的类型安全访问
 * 
 * @author soeasy.run
 */
@Data
public class VerifyReceiptResponse {
	/**
	 * <a href="https://developer.apple.com/documentation/appstorereceipts/status">验证状态码</a>
	 * 
	 * <p>状态码说明：</p>
	 * <ul>
	 * <li>0 - 验证成功</li>
	 * <li>21000 - App Store无法解析提供的JSON对象</li>
	 * <li>21002 - receipt-data字段数据格式错误</li>
	 * <li>21003 - 收据签名验证失败</li>
	 * <li>21004 - shared secret不匹配开发者账号配置</li>
	 * <li>21005 - 收据验证服务临时不可用</li>
	 * <li>21006 - 收据有效但订阅已过期（仍会返回收据详情）</li>
	 * <li>21007 - 沙盒环境收据提交至生产验证服务</li>
	 * <li>21008 - 生产环境收据提交至沙盒验证服务</li>
	 * </ul>
	 */
	private int status;

	/**
	 * 收据生成的环境类型
	 * 
	 * <p>可能取值：</p>
	 * <ul>
	 * <li>Sandbox - 沙盒测试环境</li>
	 * <li>Production - 生产环境</li>
	 * </ul>
	 */
	private String environment;

	/**
	 * 判断是否为沙盒环境收据
	 * @return 当environment为"Sandbox"时返回true
	 */
	public boolean isSandbox() {
		return "Sandbox".equals(getEnvironment());
	}
	
	/**
	 * 原始收据内容的JSON解析对象
	 * <p>包含应用内购买的基础信息</p>
	 */
	private Receipt receipt;

	/**
	 * 错误可重试标识（仅在状态码21100-21199时有效）
	 * <p>true表示暂时性错误，建议稍后重试；false表示永久性错误，无需重试</p>
	 */
	@JsonProperty("is_retryable")
	private boolean retryable;

	/**
	 * 判断是否可使用retryable字段
	 * @return 当状态码在21100-21199区间时返回true
	 */
	public boolean isUseRetryable() {
		int status = getStatus();
		return status >= 21100 && status <= 21199;
	}

	/**
	 * 最新的Base64编码应用收据（仅自动续订订阅场景返回）
	 * <p>包含最新的订阅状态信息</p>
	 */
	private String latestReceipt;

	/**
	 * 最新的应用内购买交易列表（仅自动续订订阅场景返回）
	 * <p>不包含已标记为完成的消耗品交易</p>
	 */
	private List<LatestReceiptInfo> latestReceiptInfos;

	/**
	 * 自动续订订阅的挂起更新信息（仅自动续订订阅场景返回）
	 * <p>每个元素包含产品ID对应的订阅续费状态</p>
	 */
	private List<PendingRenewalInfo> pendingRenewalInfos;

	/**
	 * 判断验证是否成功
	 * @return 当status为0时返回true
	 */
	public boolean isSuccess() {
		return getStatus() == 0;
	}

	/**
	 * 判断验证是否失败
	 * @return 当status不为0时返回true
	 */
	public boolean isError() {
		return !isSuccess();
	}

	/**
	 * 判断是否为环境模式错误
	 * @return 当状态码为21007（沙盒收据提交至生产环境）或21008（生产收据提交至沙盒环境）时返回true
	 */
	public boolean isOperationModeError() {
		int status = getStatus();
		return status == 21007 || status == 21008;
	}
}