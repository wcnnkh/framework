package scw.transaction.tcc.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import scw.beans.BeanFactory;
import scw.beans.annotaion.Autowrite;
import scw.beans.annotaion.Destroy;
import scw.beans.annotaion.InitMethod;
import scw.common.Base64;
import scw.common.Logger;
import scw.common.utils.ConfigUtils;
import scw.common.utils.StringUtils;
import scw.common.utils.XTime;
import scw.common.utils.XUtils;
import scw.transaction.DefaultTransactionLifeCycle;
import scw.transaction.TransactionException;
import scw.transaction.TransactionManager;
import scw.transaction.tcc.InvokeInfo;
import scw.transaction.tcc.StageType;
import scw.transaction.tcc.TCCService;

public final class RetryTCCService implements TCCService {
	@Autowrite
	private BeanFactory beanFactory;

	private final int retryTime;// 秒
	private String logPath;
	private final AtomicLong atomicLong = new AtomicLong();
	private final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(4);

	public RetryTCCService() {
		this(30);
	}

	public RetryTCCService(int retryTime) {
		this(retryTime, null);
	}

	public RetryTCCService(int retryTime, String logPath) {
		this.retryTime = retryTime;
		this.logPath = logPath;
	}

	@InitMethod
	public void init() {
		if (StringUtils.isNull(logPath)) {
			this.logPath = System.getProperty("java.io.tmpdir");
			String classPath = ConfigUtils.getClassPath();
			try {
				logPath += File.separator + "TCC_" + Base64.encode(classPath.getBytes("UTF-8"));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}

		Logger.info(this.getClass().getName(), "logPath=" + logPath);
		File file = new File(logPath);
		if (!file.exists()) {
			file.mkdirs();
		} else {
			File[] files = file.listFiles();
			if (files != null) {
				for (File f : files) {
					try {
						TransactionInfo info = readTransactionInfoByFile(f);
						new RetryInvoker(beanFactory, info, retryTime, f.getName());
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	@Destroy
	public void destory() {
		executorService.shutdownNow();
	}

	private TransactionInfo readTransactionInfoByFile(File f) throws IOException, ClassNotFoundException {
		FileInputStream fis = null;
		ObjectInputStream ois = null;
		try {
			fis = new FileInputStream(f);
			ois = new ObjectInputStream(fis);
			return (TransactionInfo) ois.readObject();
		} finally {
			XUtils.close(ois, fis);
		}
	}

	private void deleteLog(String logId) {
		File file = new File(logPath + File.separator + logId);
		file.deleteOnExit();
		return;
	}

	private String writeLog(TransactionInfo transactionInfo) throws IOException {
		long number = atomicLong.incrementAndGet();
		if (number < 0) {
			number = Long.MAX_VALUE + number;
		}

		StringBuilder sb = new StringBuilder();
		sb.append(logPath);
		sb.append(File.separator);
		sb.append(XTime.format(System.currentTimeMillis(), "yyyyMMddHHmmss"));
		sb.append(number);
		sb.append(".");
		sb.append(transactionInfo.getStageType().name());
		File file = new File(sb.toString());
		if (!file.exists()) {
			file.createNewFile();
		}

		FileOutputStream fos = null;
		ObjectOutputStream oos = null;
		try {
			fos = new FileOutputStream(file);
			oos = new ObjectOutputStream(fos);
			oos.writeObject(transactionInfo);
		} finally {
			XUtils.close(oos, fos);
		}
		return file.getName();
	}

	private void invoke(InvokeInfo invokeInfo, StageType stageType) {
		if (!invokeInfo.hasCanInvoke(stageType)) {
			return;
		}

		TransactionInfo info = new TransactionInfo(invokeInfo, stageType);
		try {
			String fileId = writeLog(info);
			new RetryInvoker(beanFactory, info, retryTime, fileId).start();
		} catch (IOException e) {
			throw new TransactionException(e);
		}
	}

	public void service(final Object obj, final InvokeInfo invokeInfo) {
		TransactionManager.transactionLifeCycle(new DefaultTransactionLifeCycle() {
			@Override
			public void beforeProcess() {
				invoke(invokeInfo, StageType.Confirm);
			}

			@Override
			public void beforeRollback() {
				invoke(invokeInfo, StageType.Cancel);
			}

			@Override
			public void complete() {
				invoke(invokeInfo, StageType.Complate);
			}
		});
	}

	class RetryInvoker extends TimerTask {
		private final BeanFactory beanFactory;
		private final TransactionInfo transactionInfo;
		private final int retryTime;
		private final String fileId;
		private ScheduledFuture<?> scheduledFuture;

		/**
		 * 
		 * @param retryTime
		 *            重试时间 毫秒
		 * @param obj
		 * @param method
		 * @param args
		 */
		public RetryInvoker(BeanFactory beanFactory, TransactionInfo transactionInfo, int retryTime, String fileId) {
			this.beanFactory = beanFactory;
			this.transactionInfo = transactionInfo;
			this.retryTime = retryTime;
			this.fileId = fileId;
		}

		@Override
		public void run() {
			try {
				transactionInfo.invoke(beanFactory);
				deleteLog(fileId);
				scheduledFuture.cancel(false);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		public void start() {
			if (!transactionInfo.hasCanInvoke()) {
				return;
			}

			scheduledFuture = executorService.scheduleAtFixedRate(this, 0, retryTime, TimeUnit.SECONDS);
		}
	}
}
