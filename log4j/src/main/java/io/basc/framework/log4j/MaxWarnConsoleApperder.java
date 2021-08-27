package io.basc.framework.log4j;

public class MaxWarnConsoleApperder extends MaxLevelConsoleAppender {

	public MaxWarnConsoleApperder() {
		super(org.apache.log4j.Level.WARN);
	}
}