package com.morgan.design.android.util;

import java.util.HashMap;
import java.util.Map;

import org.acra.CrashReportData;
import org.acra.sender.GoogleFormSender;
import org.acra.sender.ReportSender;
import org.acra.sender.ReportSenderException;

public class CustomReportSender implements ReportSender {

	public static final String CUSTOME_ERROR_KEY = "CUSTOME_ERROR_KEY";

	// Keep list of all maps in order to specify the form key
	public static final Map<String, String> KEYS = new HashMap<String, String>();
	static {
		KEYS.put(CUSTOME_ERROR_KEY, "abcdfe1234567");
	}

	private final GoogleFormSender customFormSender;

	public CustomReportSender() {
		this.customFormSender = new GoogleFormSender("");
	}

	@Override
	public void send(CrashReportData errorContent) throws ReportSenderException {
		if (errorContent.containsKey(CUSTOME_ERROR_KEY)) {
			customFormSender.send(errorContent);
		}
	}
}
