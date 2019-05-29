package scw.utils.ali.dayu;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import scw.result.Result;
import scw.result.ResultFactory;

public final class AsyncAliDaYu implements AliDaYu, scw.core.Destroy{
	private final AliDaYu aliDaYu;
	private final ResultFactory resultFactory;
	
	private ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(1, 100, 10, TimeUnit.MINUTES,
			new LinkedBlockingQueue<Runnable>(512));

	public AsyncAliDaYu(String appKey, String appSecret, ResultFactory resultFactory) {
		this.aliDaYu = new DefaultAliDaYu(appKey, appSecret, resultFactory);
		this.resultFactory = resultFactory;
	}

	public AsyncAliDaYu(String host, String appKey, String version, String format, String sign_method,
			String appSecret, ResultFactory resultFactory) {
		this.aliDaYu = new DefaultAliDaYu(host, appKey, version, format, sign_method, appSecret, resultFactory);
		this.resultFactory = resultFactory;
	}

	public Result sendMessage(final MessageModel messageModel, final String sms_param,
			final String toPhones) {
		threadPoolExecutor.submit(new Runnable() {

			public void run() {
				aliDaYu.sendMessage(messageModel, sms_param, toPhones);
			}
		});
		
		return resultFactory.success();
	};

	public void destroy() {
		threadPoolExecutor.shutdownNow();
	}
}
