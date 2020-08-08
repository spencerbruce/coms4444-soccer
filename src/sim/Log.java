package sim;

import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Log {

	private static FileWriter fileWriter;
	
	public static void setLogFile(String filename) {
		try {
			fileWriter = new FileWriter(filename, true);
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void writeToLogFile(String content) {
		DateFormat dateFormat = new SimpleDateFormat("[yyyy/MM/dd HH:mm:ss.SSS] ");
		Date date = new Date();
		
		String datedContent = dateFormat.format(date) + content + "\n";
		System.out.println(datedContent);

		if(fileWriter == null)
			return;

		try {
			fileWriter.append(datedContent);
		} catch(IOException e) {
			e.printStackTrace();
		}		
	}

	public static void closeLogFile() {
		if(fileWriter == null)
			return;

		try {
			fileWriter.close();
		} catch(IOException e) {
			e.printStackTrace();
		}

		fileWriter = null;
	}
}