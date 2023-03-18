package io.basc.framework.dubbo.test.service;

import io.basc.framework.boot.annotation.BootApplication;
import io.basc.framework.boot.support.MainApplication;
import io.basc.framework.context.annotation.ImportResource;

@ImportResource("export.xml")
@BootApplication
public class DubboeExportTestMain {
	public static void main(String[] args) {
		MainApplication.run(DubboeExportTestMain.class, args);
	}
}
