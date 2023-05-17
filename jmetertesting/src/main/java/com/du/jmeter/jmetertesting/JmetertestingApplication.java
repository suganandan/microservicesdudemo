package com.du.jmeter.jmetertesting;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.RestController;

import com.opencsv.CSVWriter;

@SpringBootApplication
@RestController
@EnableScheduling
public class JmetertestingApplication {
	@Value("${concurrentusers}")
	private int users;

	@Value("${batchlocation}")
	private String batlocation;

	@Value("${csvlocation}")
	private String resultloc;
	@Value("${csvcollimit}")
	private int inputsize;
	static ConfigurableApplicationContext ctx;

	public static void main(String[] args) {
		ctx = SpringApplication.run(JmetertestingApplication.class, args);

	}

	@Scheduled(cron = "${my.cron.expression}")
	public void scheduleJobs() throws IOException {
		int n = 30;

		try (CSVWriter writer = new CSVWriter(new FileWriter(resultloc))) {
			List<String[]> csvData = new ArrayList<>();
			String[] header = new String[inputsize];
			for (int k = 0; k < inputsize; k++) {
				header[k] = "id" + (k + 1);
			}
			csvData.add(header);
			for (int i = 0; i < users; i++) {
				String[] result = new String[inputsize];
				for (int j = 0; j < inputsize; j++) {
					result[j] = getAlphaNumericString(n);
				}
				csvData.add(result);
			}
			writer.writeAll(csvData);

			String[] command = { "cmd.exe", "/C", "Start", batlocation };
			Runtime.getRuntime().exec(command);

			System.out.println("--------Done----------");
			ctx.close();

		}

	}

	static String getAlphaNumericString(int n) {

		// length is bounded by 256 Character
		byte[] array = new byte[256];
		new Random().nextBytes(array);

		String randomString = new String(array, Charset.forName("UTF-8"));

		// Create a StringBuffer to store the result
		StringBuilder r = new StringBuilder();

		// Append first 20 alphanumeric characters
		// from the generated random String into the result
		for (int k = 0; k < randomString.length(); k++) {

			char ch = randomString.charAt(k);

			if (((ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z') || (ch >= '0' && ch <= '9')) && (n > 0)) {

				r.append(ch);

				n--;
			}
		}

		// return the resultant string
		return r.toString();
	}

}
