package zoneidupdate;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;

import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ZoneIdUpdates {
	private static final Logger LOGGER = Logger.getLogger(ZoneIdUpdates.class.getName());

	public static void main(String[] args) throws IOException {

		FileReader reader = new FileReader("db.properties");
		Properties prop = new Properties();
		prop.load(reader);
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		Connection con1 = null;
		PreparedStatement ps1 = null;
		ResultSet rs2 = null;
		Connection con2 = null;
		PreparedStatement ps2 = null;

		List<ZonePojo> zonelist = new ArrayList<ZonePojo>();
		Scanner scanner = null;
		String userName = null;
		String password = null;
		int sequenceNo = 0;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			scanner = new Scanner(System.in);
			LOGGER.info("Enter username");
			userName = scanner.nextLine();
			LOGGER.info("Enter password");
			password = scanner.nextLine();

			con2 = DriverManager.getConnection(prop.getProperty("url"), userName, password);
			ps2 = con2.prepareStatement("select max(sequenceNumber) from MobileLocationSlot");
			rs2 = ps2.executeQuery();

			while (rs2.next()) {
				sequenceNo = rs2.getInt(1);
			}
			LOGGER.info("Last sequence number :" +sequenceNo );
			con = DriverManager.getConnection(prop.getProperty("url"), userName, password);

			ps = con.prepareStatement(
					"select nextFeatureId,label,sequenceNumber,outletId,featureId  from MobileLocationSlot where locationId=? ");
			ps.setString(1, prop.getProperty("locationId"));
			rs = ps.executeQuery();
			while (rs.next()) {
				ZonePojo zone = new ZonePojo();
				zone.setNextFeatureId(rs.getString("nextFeatureId"));
				zone.setSequenceNumber(sequenceNo);
				zone.setOutletId(rs.getString("outletId"));
				zone.setFeatureId(rs.getString("featureId"));
				zone.setLabel(rs.getString("label"));
				zonelist.add(zone);
			}
		} catch (final ClassNotFoundException classNotFoundException) {
			LOGGER.error(classNotFoundException.getMessage());
		} catch (final SQLException sqlException) {
			LOGGER.error(sqlException.getMessage());
		} finally {
			if (null != rs) {
				try {
					rs.close();
				} catch (final SQLException ignore) {

				}
			}
			if (null != rs2) {
				try {
					rs2.close();
				} catch (final SQLException ignore) {

				}
			}
			if (null != ps) {
				try {
					ps.close();
				} catch (final SQLException ignore) {

				}
			}
			if (null != ps2) {
				try {
					ps2.close();
				} catch (final SQLException ignore) {

				}
			}
			if (null != con) {
				try {
					con.close();
				} catch (final SQLException ignore) {

				}
			}
			if (null != con2) {
				try {
					con2.close();
				} catch (final SQLException ignore) {

				}
			}
			if (null != scanner) {
				try {
					scanner.close();
				} catch (final Exception ignore) {

				}
			}
		}
		LOGGER.info("List of records fectched for the last zone id :" + zonelist.size());

		XSSFWorkbook workbook = null;
		FileInputStream file = null;
		try {

			file = new FileInputStream(new File(prop.getProperty("filename")));

			workbook = new XSSFWorkbook(file);

			XSSFSheet sheet = workbook.getSheetAt(0);

			Iterator<Row> rowIterator = sheet.iterator();
			while (rowIterator.hasNext()) {
				Row row = rowIterator.next();
				if (row.getRowNum() != 0) {
					sequenceNo++;
					Iterator<Cell> cellIterator = row.cellIterator();

					while (cellIterator.hasNext()) {
						Cell cell = cellIterator.next();
						String locationIdVal = null;
						if (cell.getCellType() != 0 && cell.getColumnIndex() == 0) {
							locationIdVal = cell.getStringCellValue();

						}

						LOGGER.info("Location Id Value :" + locationIdVal);
						if (null != zonelist && null != locationIdVal) {
							int count = 0;
							try {

								Class.forName("com.mysql.jdbc.Driver");
								con1 = DriverManager.getConnection(prop.getProperty("url"), userName, password);
								for (ZonePojo val : zonelist) {
									ps1 = con1.prepareStatement(
											"Insert into MobileLocationSlot (locationId , label , nextFeatureId ,sequenceNumber, outletId, featureId) VALUES ('"
													+ locationIdVal + "' , '" + val.getLabel() + "' ,'"
													+ val.getNextFeatureId() + "', " + sequenceNo + " , '"
													+ val.getOutletId() + "' ,'" + val.getFeatureId() + "')");
									ps1.executeUpdate();
									LOGGER.info("New location Id inserted for : " + val.getNextFeatureId());
									count++;
								}
								LOGGER.info("Total Number of records inserted : " + count);
							} catch (final ClassNotFoundException classNotFoundException) {
								LOGGER.error(classNotFoundException.getMessage());
							} catch (final SQLException sqlException) {
								LOGGER.error(sqlException.getMessage());
							} finally {

								if (null != ps1) {
									try {
										ps1.close();
									} catch (final SQLException ignore) {

									}
								}
								if (null != con1) {
									try {
										con1.close();
									} catch (final SQLException ignore) {

									}
								}
							}
						}
					}
				}

			}
			file.close();
		} catch (final Exception exception) {
			LOGGER.error(exception.getMessage());
		} finally {
			if (null != file) {
				try {
					file.close();
				} catch (final IOException ignored) {

				}
			}
			if (null != workbook) {
				try {
					workbook.close();
				} catch (final IOException ignored) {

				}
			}
		}

	}

}
