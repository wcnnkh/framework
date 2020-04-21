package scw.tcc.service;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import scw.beans.annotation.InitMethod;
import scw.core.GlobalPropertyFactory;
import scw.core.instance.InstanceFactory;
import scw.io.FileManager;
import scw.io.FileUtils;
import scw.io.SerializerUtils;
import scw.logger.Logger;
import scw.logger.LoggerUtils;
import scw.tcc.InvokeInfo;
import scw.tcc.StageType;
import scw.tcc.TCCService;
import scw.transaction.DefaultTransactionLifeCycle;
import scw.transaction.TransactionException;
import scw.transaction.TransactionManager;

public final class RetryTCCService implements TCCService, scw.core.Destroy {
	private static Logger logger = LoggerUtils.getLogger(RetryTCCService.class);

	private InstanceFactory instanceFactory;
	private FileManager fileManager;
	private final int retryTime;// 秒
	private final ScheduledExecutorService executorService = Executors
			.newScheduledThreadPool(4);

	public RetryTCCService(InstanceFactory instanceFactory) {
		this(instanceFactory, 30);
	}

	public RetryTCCService(InstanceFactory instanceFactory, int retryTime) {
		this.retryTime = retryTime;
		this.instanceFactory = instanceFactory;
	}

	@InitMethod
	public void init() throws UnsupportedEncodingException {
		String logPath = FileUtils.getTempDirectoryPath();
		logPath += File.separator + "TCC_"
				+ GlobalPropertyFactory.getInstance().getSystemLocalId();
		fileManager = new FileManager(logPath);
		logger.debug("logPath=" + logPath);

		File file = new File(logPath);
		if (!file.exists()) {
			file.mkdirs();
		} else {
			File[] files = file.listFiles();
			if (files != null) {
				for (File f : files) {
					try {
						TransactionInfo info = SerializerUtils.readObject(f);
						new RetryInvoker(info, retryTime, f.getPath()).start();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	public void destroy() {
		executorService.shutdownNow();
	}

	private void invoke(InvokeInfo invokeInfo, StageType stageType) {
		if (!invokeInfo.hasCanInvoke(stageType)) {
			return;
		}

		TransactionInfo info = new TransactionInfo(invokeInfo, stageType);
		try {
			File file = fileManager.createRandomFileWriteObject(info);
			new RetryInvoker(info, retryTime, file.getPath()).start();
		} catch (IOException e) {
			throw new TransactionException(e);
		}
	}

	public void service(final InvokeInfo invokeInfo) {
		TransactionManager
				.transactionLifeCycle(new DefaultTransactionLifeCycle() {
					@Override
					public void beforeProcess() {
						invoke(invokeInfo, StageType.Confirm);
					}

					@Override
					public void beforeRollback() {
						invoke(invokeInfo, StageType.Cancel);
					}
				});
	}

	class RetryInvoker extends TimerTask {
		private final TransactionInfo transactionInfo;
		private final int retryTime;
		private final String fileId;

		/**
		 * 
		 * @param retryTime
		 *            重试时间 毫秒
		 * @param obj
		 * @param method
		 * @param args
		 */
		public RetryInvoker(TransactionInfo transactionInfo, int retryTime,
				String fileId) {
			this.transactionInfo = transactionInfo;
			this.retryTime = retryTime;
			this.fileId = fileId;
		}

		@Override
		public void run() {
			try {
				transactionInfo.invoke(instanceFactory);
				File file = new File(fileId);
				if (file.exists()) {
					file.delete();
				}
			} catch (Exception e) {
				executorService.schedule(this, retryTime, TimeUnit.SECONDS);
				e.printStackTrace();
			}
		}

		public void start() {
			if (!transactionInfo.hasCanInvoke()) {
				return;
			}
			run();
		}
	}
}
