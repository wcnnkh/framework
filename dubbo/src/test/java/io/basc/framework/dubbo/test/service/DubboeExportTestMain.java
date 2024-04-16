package io.basc.framework.dubbo.test.service;

import io.basc.framework.autoconfigure.boot.BootApplication;
import io.basc.framework.beans.factory.annotation.ImportResource;
import io.basc.framework.boot.support.MainApplication;

@ImportResource("export.xml")
@BootApplication
public class DubboeExportTestMain {
	public static void main(String[] args) {
		MainApplication.run(DubboeExportTestMain.class, args);
	}
}
