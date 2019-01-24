package com.cc.wechatmanager.log;

import com.cc.wechatmanager.utils.FileUtil;
import com.cc.wechatmanager.utils.NTTimeUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Write the LogItem to the file
 * 
 * Created by snowdream on 10/20/13.
 */
public class Log2File {
	private static final String LOG_FILE_NAME = "SP-";
	private static ExecutorService executor = Executors.newSingleThreadExecutor();
	private static String log_file_suffix = NTTimeUtils.formatDate(NTTimeUtils.now(), NTTimeUtils.DATE_FORMATTER);
	private static boolean isCleaned = false;
	private static final long TEN_DAYS_IN_MILLIS = 7 * 24 * 60 * 60 * 1000;
	private static final long MAX_LOG_CACHE_SIZE = 100 * 1024 * 1024; // 100M
	private static File logFile;
	/**
	 * Set the ExecutorService
	 * 
	 * @param executor
	 *            the ExecutorService
	 */
	protected static void setExecutor(ExecutorService executor) {
		Log2File.executor = executor;
	}

	protected static void log2file(final String str) {
		if (executor != null) {
			cleanLogs();
			executor.execute(new Runnable() {
				@Override
				public void run() {
					PrintWriter out = null;
					File file = getLogFilePath();

					try {
						out = new PrintWriter(new BufferedWriter(
								new FileWriter(file, true)));
						out.println(str);
						out.flush();
					} catch (IOException e) {
						e.printStackTrace();
					} finally {
						try {
							out.close();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			});
		}
	}

	static final File getLogFilePath() {
		if (logFile != null) {
			return logFile;
		}

		File logDirect = FileUtil.getExternalLogDir();
		logFile = new File(logDirect, LOG_FILE_NAME + log_file_suffix + ".log");
		return logFile;
	}

	/**
	 * Clean the logs files before ten days
	 */
	private static void cleanLogs() {
		if (isCleaned) {
			return;
		}
		File logDirect = FileUtil.getExternalLogDir();
		boolean isExceed  = FileUtil.getFolderSize(logDirect) > MAX_LOG_CACHE_SIZE;
		if (logDirect != null && logDirect.isDirectory()) {
			File[] logs = logDirect.listFiles();
			if (logs != null) {
				for (File log : logs) {
					if (log.lastModified() + TEN_DAYS_IN_MILLIS < System
							.currentTimeMillis()) {
						log.delete();
					} else if (isExceed) {
						if (log.lastModified() + (24 * 60 * 60 * 1000) < System.currentTimeMillis()) {
							log.delete();
						}
					}
				}
			}
		}

		isCleaned = true;
	}
}
