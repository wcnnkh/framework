package scw.utils.ali.dayu;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import scw.common.ProcessResult;

public final class AsyncAliDaYu implements AliDaYu {
	private AliDaYu aliDaYu;
	private ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(5, 20, 0L, TimeUnit.MILLISECONDS,
			new LinkedBlockingQueue<Runnable>(512));

	public AsyncAliDaYu(String appKey, String appSecret) {
		this.aliDaYu = new DefaultAliDaYu(appKey, appSecret);
	}

	public AsyncAliDaYu(String host, String appKey, String version, String format, String sign_method,
			String appSecret) {
		this.aliDaYu = new DefaultAliDaYu(host, appKey, version, format, sign_method, appSecret);
	}

	public ProcessResult<String> sendMessage(final MessageModel messageModel, final String sms_param,
			final String toPhones) {
		threadPoolExecutor.submit(new Runnable() {

			public void run() {
				aliDaYu.sendMessage(messageModel, sms_param, toPhones);
			}
		});
		return ProcessResult.success();
	};

	public void destory() {
		threadPoolExecutor.shutdownNow();
	}
}
