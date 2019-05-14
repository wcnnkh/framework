package scw.utils.ali.dayu;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import scw.beans.annotation.Destroy;
import scw.result.DataResult;
import scw.result.ResultFactory;

public final class AsyncAliDaYu implements AliDaYu {
	private final AliDaYu aliDaYu;
	private final ResultFactory resultFactory;
	
	private ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(5, 20, 0L, TimeUnit.MILLISECONDS,
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

	public DataResult<String> sendMessage(final MessageModel messageModel, final String sms_param,
			final String toPhones) {
		threadPoolExecutor.submit(new Runnable() {

			public void run() {
				aliDaYu.sendMessage(messageModel, sms_param, toPhones);
			}
		});
		
		return resultFactory.success();
	};

	@Destroy
	public void destory() {
		threadPoolExecutor.shutdownNow();
	}
}
