package com.du.jmeter.jmetertesting;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Timestamp;

public class Test {

	public static void main(String[] args) {
		BufferedReader lineReader=null;
		try {
			 lineReader = new BufferedReader(new FileReader(
					"C:\\Users\\a786218\\Downloads\\apache-jmeter-5.4.1\\apache-jmeter-5.4.1\\bin\\duapp_read_services_agg_report.csv"));
			String lineText = null;

			lineReader.readLine(); // skip header line

			while ((lineText = lineReader.readLine()) != null) {
				String[] data = lineText.split(",");
				String timeStamp = data[0];
				String elapsed = data[1];
				String label = data[2];
				String responseCode = data[3];
				System.out.println(new Timestamp(Long.valueOf(timeStamp)) + "----" + elapsed + "======" + label
						+ "=======" + responseCode);
			}
		} catch (IOException ioException) {

		}finally {
			if(null!=lineReader) {
				try {
					lineReader.close();
				} catch (IOException ignored) {
					
				}
			}
		}
	}
}
