package scw.beans.tcc.service;

import scw.beans.tcc.InvokeInfo;
import scw.beans.tcc.StageType;
import scw.beans.tcc.TCCService;
import scw.core.Consumer;
import scw.core.instance.InstanceFactory;
import scw.mq.MQ;
import scw.transaction.DefaultTransactionLifeCycle;
import scw.transaction.TransactionManager;

public final class MQTccService implements TCCService, Consumer<TransactionInfo> {
	private MQ<TransactionInfo> mq;
	private String productName;
	private InstanceFactory instanceFactory;

	public MQTccService(InstanceFactory instanceFactory, MQ<TransactionInfo> mq, String productName) {
		this.instanceFactory = instanceFactory;
		this.mq = mq;
		this.productName = productName;
		mq.bindConsumer(productName, this);
	}

	private void invoke(InvokeInfo invokeInfo, StageType stageType) {
		if (!invokeInfo.hasCanInvoke(stageType)) {
			return;
		}

		mq.push(productName, new TransactionInfo(invokeInfo, stageType));
	}

	public void service(final InvokeInfo invokeInfo) {
		TransactionManager.transactionLifeCycle(new DefaultTransactionLifeCycle() {
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

	public void consume(TransactionInfo message) throws Exception {
		message.invoke(instanceFactory);
	}
}
