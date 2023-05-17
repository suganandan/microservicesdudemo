package com.eai.eaifinalval;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class EaifinalvalApplication {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Value("${urluname}")
	private String url;

	@Value("${query1}")
	private String sql1;
	@Value("${query2}")
	private String sql2;
	@Value("${query1Index}")
	private int sql1Index;
	@Value("${query2Index}")
	private int sql2Index;

	@Value("${query3}")
	private String sql3;
	@Value("${query4}")
	private String sql4;
	@Value("${query3Index}")
	private int sql3Index;
	@Value("${query4Index}")
	private int sql4Index;

	@Value("${outfile}")
	private String fileName;
	
	@Value("${eai.sheetName}")
	private String sheetName;
	
	@Value("${eai.rowIndex}")
	private int rowIndex;


	public static void main(String[] args) {
		SpringApplication.run(EaifinalvalApplication.class, args);
	}

	@RequestMapping(value = "/uat", method = { RequestMethod.GET, RequestMethod.POST }, consumes = { "text/plain",
			"application/*" })
	public void getuat() throws IOException {

		File path = new File(fileName);
		PrintWriter pw = new PrintWriter(path);
		StringBuilder sb1 = new StringBuilder();
		StringBuilder sb2 = new StringBuilder();

		try (FileInputStream file = new FileInputStream(new File(url));
				XSSFWorkbook workbook = new XSSFWorkbook(file)) {
			XSSFSheet sheet = workbook.getSheetAt(workbook.getSheetIndex(sheetName));

			Iterator<Row> rowIterator = sheet.iterator();

			while (rowIterator.hasNext()) {
				StringBuilder sb3 = new StringBuilder();
				StringBuilder sb4 = new StringBuilder();
				Row row = rowIterator.next();
				Iterator<Cell> cellIterator = row.cellIterator();

				while (cellIterator.hasNext()) {
					Cell cell = cellIterator.next();

					if (cell.getCellType() != null && cell.getRowIndex() > rowIndex && cell.getColumnIndex() == sql1Index
							&& cell.getCellType().toString().equalsIgnoreCase("STRING")) {
						String val = sql1.replaceAll("cell.getStringCellValue", cell.getStringCellValue());
						sb1.append(val);
						sb1.append("\n");
						String val1 = sql3.replaceAll("cell.getStringCellValue", cell.getStringCellValue());
						sb4.append(val1);

					}
					if (cell.getCellType() != null && cell.getRowIndex() > rowIndex && cell.getColumnIndex() == sql2Index
							&& cell.getCellType().toString().equalsIgnoreCase("STRING")) {
						String val = sql2.replaceAll("cell.getStringCellValue", cell.getStringCellValue());
						sb1.append(val);
						sb1.append("\n");

					}

					if (cell.getCellType() != null && cell.getRowIndex() > rowIndex && cell.getColumnIndex() == sql4Index
							&& cell.getCellType().toString().equalsIgnoreCase("NUMERIC")) {

						sb3.append(String.valueOf((int) cell.getNumericCellValue()));

					}

					if (cell.getCellType() != null && cell.getRowIndex() > rowIndex && cell.getColumnIndex() == 5
							&& cell.getCellType().toString().equalsIgnoreCase("STRING")
							&& null != cell.getStringCellValue()
							&& !cell.getStringCellValue().equalsIgnoreCase("minutes")) {

						sb3.append(" " + cell.getStringCellValue());

					}

				}
				if (sb3.length() > 0) {

					List<Map<String, Object>> result = jdbcTemplate.queryForList(
							"select * from EAI_DATA_MAPPING where  ENTITY='Bundle Size' and SYSTEM='Siebel' and SYSTEM_ID='"
									+ sb3.toString() + "'");
					if (null != result && !result.isEmpty()) {
						List<Map<String, Object>> rows = jdbcTemplate.queryForList(
								"select EAI_ID from EAI_DATA_MAPPING where ENTITY='Bundle Size' and SYSTEM='Siebel' and SYSTEM_ID='"
										+ sb3.toString() + "'");

						for (Map row1 : rows) {
							int idVal = Integer.parseInt(row1.get("EAI_ID").toString());

							String tempStr = sb4.toString();
							String replaceVal = tempStr.replaceAll(
									"\\(SELECT MAX\\(CAST\\(EAI\\_ID AS Int\\)\\) \\+ 1 FROM EAI\\_DATA\\_MAPPING\\)",
									String.valueOf(idVal));
							sb2.append(replaceVal);
							sb2.append("\n");
							sb3.setLength(0);
							sb4.setLength(0);
						}

					} else {
						sb2.append(sb4);
						sb2.append("\n");
						String val = sql4.replaceAll("cell.getStringCellValue", String.valueOf(sb3.toString()));
						sb2.append(val);
						sb2.append("\n");
						sb3.setLength(0);
						sb4.setLength(0);
					}

				}

			}

		}
		pw.write(sb1.toString());
		pw.write("\n");
		pw.write(sb2.toString());
		pw.flush();
		pw.close();
		sb1.setLength(0);
		sb2.setLength(0);

	}

	@RequestMapping(value = "/fv", method = { RequestMethod.GET, RequestMethod.POST }, consumes = { "text/plain",
			"application/*" })
	public void getfv() throws IOException {

		File path = new File(fileName);
		PrintWriter pw = new PrintWriter(path);
		StringBuilder sb1 = new StringBuilder();
		StringBuilder sb2 = new StringBuilder();

		try (FileInputStream file = new FileInputStream(new File(url));
				XSSFWorkbook workbook = new XSSFWorkbook(file)) {
			XSSFSheet sheet = workbook.getSheetAt(workbook.getSheetIndex(sheetName));

			Iterator<Row> rowIterator = sheet.iterator();

			while (rowIterator.hasNext()) {
				StringBuilder sb3 = new StringBuilder();
				StringBuilder sb4 = new StringBuilder();
				Row row = rowIterator.next();
				Iterator<Cell> cellIterator = row.cellIterator();

				while (cellIterator.hasNext()) {
					Cell cell = cellIterator.next();

					if (cell.getCellType() != null && cell.getRowIndex() > rowIndex && cell.getColumnIndex() == 12
							&& cell.getCellType().toString().equalsIgnoreCase("STRING")) {
						String val = sql1.replaceAll("cell.getStringCellValue", cell.getStringCellValue());
						sb1.append(val);
						sb1.append("\n");
						String val1 = sql3.replaceAll("cell.getStringCellValue", cell.getStringCellValue());
						sb4.append(val1);

					}
					if (cell.getCellType() != null && cell.getRowIndex() > rowIndex && cell.getColumnIndex() == 13
							&& cell.getCellType().toString().equalsIgnoreCase("STRING")) {
						String val = sql2.replaceAll("cell.getStringCellValue", cell.getStringCellValue());
						sb1.append(val);
						sb1.append("\n");

					}

					if (cell.getCellType() != null && cell.getRowIndex() > rowIndex && cell.getColumnIndex() == 15
							&& cell.getCellType().toString().equalsIgnoreCase("NUMERIC")) {

						sb3.append(String.valueOf((int) cell.getNumericCellValue()));

					}

					if (cell.getCellType() != null && cell.getRowIndex() > rowIndex && cell.getColumnIndex() == 16
							&& cell.getCellType().toString().equalsIgnoreCase("STRING")
							&& null != cell.getStringCellValue()
							&& !cell.getStringCellValue().equalsIgnoreCase("minutes")) {

						sb3.append(" " + cell.getStringCellValue());

					}

				}
				if (sb3.length() > 0) {

					List<Map<String, Object>> result = jdbcTemplate.queryForList(
							"select * from EAI_DATA_MAPPING where  ENTITY='Bundle Size' and SYSTEM='Siebel' and SYSTEM_ID='"
									+ sb3.toString() + "'");
					if (null != result && !result.isEmpty()) {
						List<Map<String, Object>> rows = jdbcTemplate.queryForList(
								"select EAI_ID from EAI_DATA_MAPPING where ENTITY='Bundle Size' and SYSTEM='Siebel' and SYSTEM_ID='"
										+ sb3.toString() + "'");

						for (Map row1 : rows) {
							int idVal = Integer.parseInt(row1.get("EAI_ID").toString());

							String tempStr = sb4.toString();
							String replaceVal = tempStr.replaceAll(
									"\\(SELECT MAX\\(CAST\\(EAI\\_ID AS Int\\)\\) \\+ 1 FROM EAI\\_DATA\\_MAPPING\\)",
									String.valueOf(idVal));
							sb2.append(replaceVal);
							sb2.append("\n");
							sb3.setLength(0);
							sb4.setLength(0);
						}

					} else {
						sb2.append(sb4);
						sb2.append("\n");
						String val = sql4.replaceAll("cell.getStringCellValue", String.valueOf(sb3.toString()));
						sb2.append(val);
						sb2.append("\n");
						sb3.setLength(0);
						sb4.setLength(0);
					}

				}

			}

		}
		pw.write(sb1.toString());
		pw.write("\n");
		pw.write(sb2.toString());
		pw.flush();
		pw.close();
		sb1.setLength(0);
		sb2.setLength(0);

	}

}
