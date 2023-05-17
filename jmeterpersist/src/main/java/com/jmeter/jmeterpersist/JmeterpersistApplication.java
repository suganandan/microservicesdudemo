package com.jmeter.jmeterpersist;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@SpringBootApplication
public class JmeterpersistApplication {
	@Value("${summarylocation}")
	private String resultsummaryloc;
	@Autowired
	private JdbcTemplate jdbcTemplate;

	public static void main(String[] args) {
		SpringApplication.run(JmeterpersistApplication.class, args);

	}

	@RequestMapping(value = "/", method = { RequestMethod.GET, RequestMethod.POST })
	public void insertData() {
		System.out.println("******************");
		BufferedReader lineReader = null;

		File directoryPath = new File(resultsummaryloc);
		FilenameFilter textFilefilter = new FilenameFilter() {
			public boolean accept(File dir, String name) {
				String lowercaseName = name.toLowerCase();
				if (lowercaseName.endsWith(".csv")) {
					return true;
				} else {
					return false;
				}
			}
		};
		// List of all the text files
		String filesList[] = directoryPath.list(textFilefilter);
		for (String val : filesList) {
			System.out.println("val ----" + val);
			try {
				lineReader = new BufferedReader(new FileReader(resultsummaryloc + "\\" + val));
				String lineText = null;

				lineReader.readLine(); // skip header line

				while ((lineText = lineReader.readLine()) != null) {
					String[] data = lineText.split(",");
					jdbcTemplate.batchUpdate(
							"insert into performancedata (serviceName, Elapsedtime,status,testdate,totalbytes,sentbytes,url,connecttime) values(?,?,?,?,?,?,?,?)",
							new BatchPreparedStatementSetter() {

								public void setValues(PreparedStatement ps, int i) throws SQLException {
									ps.setString(1, data[2]);// ts
									ps.setLong(2, Long.valueOf(data[1]));
									ps.setString(3, data[7]);
									ps.setString(4, new Timestamp(Long.valueOf(data[0])).toString());
									ps.setLong(5, Long.valueOf(data[9]));
									ps.setLong(6, Long.valueOf(data[10]));
									ps.setString(7, data[13]);
									ps.setLong(8, Long.valueOf(data[16]));

								}

								public int getBatchSize() {
									return data.length;
								}

							});
				}
				System.out.println("---Data Inserted Successfully----");
				PrintWriter pw = new PrintWriter(resultsummaryloc + "\\" + val);
				pw.close();
				Path file = Paths.get(resultsummaryloc + "\\" + val);
				
				Files.delete(file);
			} catch (IOException ioException) {

			} finally {
				if (null != lineReader) {
					try {
						lineReader.close();
					} catch (IOException ignored) {

					}
				}
			}
		}
	}
}
