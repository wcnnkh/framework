package io.basc.framework.console;

import io.basc.framework.util.Assert;

public class ConsoleBootstrap extends Thread {
	private final ConsoleProcessorManager processorManager;

	public ConsoleBootstrap(ConsoleProcessorManager processorManager) {
		Assert.requiredArgument(processorManager != null, "processorManager");
		this.processorManager = processorManager;
	}

	public ConsoleProcessorManager getProcessorManager() {
		return processorManager;
	}

	@Override
	public void run() {
		super.run();
	}
}
