package com.du.zoneupdates;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class ZoneupdatesApplication {
	private static final Logger LOGGER = LoggerFactory.getLogger(ZoneupdatesApplication.class);
	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Value("${urluname}")
	private String url;

	public static void main(String[] args) {
		SpringApplication.run(ZoneupdatesApplication.class, args);

	}

	@RequestMapping(value = "/", method = { RequestMethod.GET, RequestMethod.POST }, consumes = { "text/plain",
			"application/*" })
	public void getData() {

		XSSFWorkbook workbook = null;
		FileInputStream file = null;
		try {

			file = new FileInputStream(new File(url));

			workbook = new XSSFWorkbook(file);

			String tableName = "PREPAID_BUNDLE_BAL_TEST ";
			XSSFSheet sheet = workbook.getSheetAt(workbook.getSheetIndex("Sheet1"));
			List<String> coVal = new ArrayList<>();
			Map<Integer, String> columnMap = new HashMap<>();

			List<Map<String, String>> sheetVal = new ArrayList<>();

			Iterator<Row> rowIterator = sheet.iterator();
			while (rowIterator.hasNext()) {
				Map<String, String> columnValues = new HashMap<>();
				Row row = rowIterator.next();
				Iterator<Cell> cellIterator = row.cellIterator();

				while (cellIterator.hasNext()) {
					Cell cell = cellIterator.next();

					if (cell.getCellType() != null && cell.getRowIndex() == 0 
							&& cell.getCellType().toString().equalsIgnoreCase("STRING")) {
						columnMap.put(cell.getColumnIndex(), cell.getStringCellValue());
						coVal.add(cell.getStringCellValue());
					}
					if (cell.getCellType() != null && cell.getRowIndex() > 0
							&& cell.getCellType().toString().equalsIgnoreCase("STRING")) {
						columnValues.put(columnMap.get(cell.getColumnIndex()), cell.getStringCellValue());

					}
					if (cell.getCellType() != null && cell.getRowIndex() >0
							&& cell.getCellType().toString().equalsIgnoreCase("NUMERIC")) {
						columnValues.put(columnMap.get(cell.getColumnIndex()),
								String.valueOf(cell.getNumericCellValue()));
					}

				}

				sheetVal.add(columnValues);
			}
			
			 
			 
			for (Map<String, String> shtvalues : sheetVal) {
				if (!shtvalues.isEmpty()) {
					StringBuilder sb = new StringBuilder();

					sb.append("insert into " + tableName + " set ");
					for (String str : coVal) {
						if (null != shtvalues.get(str)) {
							sb.append(str + "='" + shtvalues.get(str) + "',");
						}
					}
					sb.setLength(sb.length() - 1);

					System.out.println( sb.toString());
					sb.setLength(0);
				}
			}

		} catch (final Exception exception) {
			LOGGER.error(exception.getMessage());
		} finally {
			if (null != file) {
				try {
					file.close();
				} catch (IOException ignored) {

				}
			}
			if (null != workbook) {
				try {
					workbook.close();
				} catch (IOException ignored) {

				}
			}
		}

	}

}
