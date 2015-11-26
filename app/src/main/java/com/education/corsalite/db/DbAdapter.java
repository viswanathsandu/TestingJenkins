package com.education.corsalite.db;

import android.content.Context;

import com.education.corsalite.utils.L;

import java.io.File;
import java.io.FileNotFoundException;

public class DbAdapter {
	public static Context context;
	public static final String dbFileName = "corsalite.db";
	private static String dbDirectoryPath = "db/v1";

	public static String createDbFile() {
		String fqFilename = null;
		try {
			File localDirectoryPath = getLocalDirectoryPath();
//			createDbDirectory(getFullQualifiedDirectoryName(localDirectoryPath));
			fqFilename = getFullyQualifiedDbFilename(localDirectoryPath);
//			if (new File(fqFilename).exists()) return fqFilename;
			context.getDir(fqFilename, Context.MODE_PRIVATE);
//			createTheDbFile(fqFilename);
		}
		catch (Exception e) {
			L.error(e.getMessage(), e);
		}
		return fqFilename;
	}

	private static String getFullyQualifiedDbFilename(File localDirectoryPath) {
		return String.format("%s/%s/%s", localDirectoryPath, dbDirectoryPath, dbFileName);
	}

	private static String getFullQualifiedDirectoryName(File localDirectoryPath) {
		return String.format("%s/%s", localDirectoryPath, dbDirectoryPath);
	}

	/**
	 * This method should only be used by test classes.
	 */
	public static void deleteDbFile() {
		try {
			File file = new File(getFullyQualifiedDbFilename(getLocalDirectoryPath()));
			boolean result = file.delete();
			L.info(String.format("Was file *%s* deleted: %s", file == null ? "null" : file.toString(), String.valueOf(result)));
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void createDbDirectory(String fqDirectoryName) {
		File dbDirectory = new File(fqDirectoryName);
		boolean result = false;
		if (!dbDirectory.exists()) {
			result = dbDirectory.mkdirs();
		}
		L.info(String.format("Directory named *%s* created: %s", fqDirectoryName, String.valueOf(result)));
		return;
	}

	private static void createTheDbFile(String fqFilename) throws FileNotFoundException {
		context.openFileOutput(fqFilename, Context.MODE_PRIVATE);
	}

	private static File getLocalDirectoryPath() throws Exception {
		if (context == null) throw new Exception(String.format("%s: CurrentContext field was not set and is currently null.  Make sure to set this from your Activity.  ", DbAdapter.class.getName()));
		L.info("FilesDirPath : "+context.getFilesDir());
		return context.getFilesDir();
	}
}
