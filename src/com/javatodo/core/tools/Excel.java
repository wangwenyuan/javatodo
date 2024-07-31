package com.javatodo.core.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFDataFormatter;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class Excel {

	public static List<Map<String, Object>> read(String path) throws IOException {
		if (path.contains(".xlsx")) {
			return Read2007(path);
		} else {
			return Read2003(path);
		}
	}

	public static void write(List<Map<String, Object>> list, String filePath) throws IOException {
		if (list.size() < 1) {
			return;
		}
		// 判断路径是否存在
		File file = new File(filePath);
		if (!file.getParentFile().isDirectory()) {
			new File(file.getParent()).mkdirs();
		}

		List<String> keyList = new ArrayList();
		Map<String, Object> keyMap = list.get(0);
		for (String key : keyMap.keySet()) {
			keyList.add(key);
			// System.out.println(keyMap.get(key).getClass().toString());
		}

		Workbook wb = new XSSFWorkbook();
		CreationHelper createHelper = wb.getCreationHelper();
		Sheet sheet = wb.createSheet("sheet0");
		// 创建Rows
		int rowIndex = 0; // 当前行索引
		// 创建表头
		Row headerRow = sheet.createRow(rowIndex);
		for (Integer i = 0; i < keyList.size(); i = i + 1) {
			headerRow.createCell(i).setCellValue(keyList.get(i));
		}
		// 写入数据
		for (Integer i = 0; i < list.size(); i = i + 1) { // 遍历所有数据
			rowIndex = rowIndex + 1;
			Row row = sheet.createRow(rowIndex);
			for (Integer n = 0; n < keyList.size(); n = n + 1) {
				Map<String, Object> map = list.get(i);
				String key = keyList.get(n);

				if (getValueClass(map, key).contains("String")) {
					row.createCell(n).setCellValue(getValue(createHelper, map, key));
				}

				if (getValueClass(map, key).contains("BigDecimal")) {
					row.createCell(n).setCellValue(Double.valueOf(map.get(key).toString()));
				}

				if (getValueClass(map, key).contains("Integer")) {
					row.createCell(n).setCellValue(Integer.valueOf(map.get(key).toString()));
				}

				if (getValueClass(map, key).contains("Timestamp")) {
					String dateString = map.get(key).toString();
					dateString = dateString.replace(" 00:00:00.0", "");
					dateString = dateString.replace(".0", "");
					row.createCell(n).setCellValue(dateString);
				}
			}
		}
		OutputStream fileOut = new FileOutputStream(filePath);
		wb.write(fileOut);
		fileOut.close();
	}

	private static String getValueClass(Map<String, Object> map, String key) {
		if (map.get(key) == null) {
			return "java.lang.String";
		} else {
			// System.out.println(map.get(key).getClass().toString());
			return map.get(key).getClass().toString();
		}
	}

	private static RichTextString getValue(CreationHelper createHelper, Map<String, Object> map, String key) {
		if (map.get(key) == null) {
			return createHelper.createRichTextString("");
		} else {
			return createHelper.createRichTextString(map.get(key).toString());
		}
	}

	private static List<Map<String, Object>> Read2003(String Excell文件路径) throws IOException {
		FileInputStream fileInputStream = new FileInputStream(Excell文件路径);
		Workbook workbook = new HSSFWorkbook(fileInputStream);// 获取工作簿
		List<Map<String, Object>> list = getData(workbook);
		fileInputStream.close();// 关闭流
		return list;
	}

	private static List<Map<String, Object>> Read2007(String Excell文件路径) throws IOException {
		FileInputStream fileInputStream = new FileInputStream(Excell文件路径);
		Workbook workbook = new XSSFWorkbook(fileInputStream);// 获取工作簿
		List<Map<String, Object>> list = getData(workbook);
		fileInputStream.close();// 关闭流
		return list;
	}

	private static List<Map<String, Object>> getData(Workbook workbook) {
		List<Map<String, Object>> list = new ArrayList<>();
		Sheet sheetAt = workbook.getSheetAt(0);
		Map<Integer, String> header = new LinkedHashMap<>();
		Row titleRow = sheetAt.getRow(0);
		Integer n = 0;
		while (true) {
			Cell cell = titleRow.getCell(n);
			if (cell == null) {
				break;
			}
			header.put(n, titleRow.getCell(n).getStringCellValue());
			n = n + 1;
		}

		Integer i = 0;
		while (true) {
			i = i + 1;
			Row row = sheetAt.getRow(i);
			if (row == null) {
				break;
			}
			boolean flag = false;
			for (Integer key : header.keySet()) {
				if (row.getCell(key) != null) {
					flag = true;
				}
			}
			if (flag) {
				Map<String, Object> map = new LinkedHashMap<>();
				for (Integer k : header.keySet()) {
					map.put(header.get(k), getCellValue(workbook, row.getCell(k)));
				}
				list.add(map);
			} else {
				break;
			}
		}
		return list;
	}

	private static String getCellValue(Workbook workbook, Cell cell) {
		if (cell == null) {
			return "";
		}
		switch (cell.getCellType()) {

		case Cell.CELL_TYPE_BLANK:
			return "";

		case Cell.CELL_TYPE_BOOLEAN:
			if (cell.getBooleanCellValue()) {
				return "true";
			} else {
				return "false";
			}

		case Cell.CELL_TYPE_ERROR:
			return "";

		case Cell.CELL_TYPE_FORMULA:
			FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
			CellValue cellValue = evaluator.evaluate(cell);
			switch (cellValue.getCellType()) {
			case Cell.CELL_TYPE_STRING:
				return cellValue.getStringValue();
			case Cell.CELL_TYPE_NUMERIC:
				return cellValue.getNumberValue() + "";
			case Cell.CELL_TYPE_BLANK:
				return "";
			case Cell.CELL_TYPE_BOOLEAN:
				if (cell.getBooleanCellValue()) {
					return "true";
				} else {
					return "false";
				}
			case Cell.CELL_TYPE_ERROR:
				return "";
			}
			return "";
		case Cell.CELL_TYPE_NUMERIC:
			Object value = "";
			if (HSSFDateUtil.isCellDateFormatted(cell)) {// 日期类型
				// 短日期转化为字符串
				Date date = cell.getDateCellValue();
				if (date != null) {
					// 标准0点 1970/01/01 08:00:00
					if (date.getTime() % 86400000 == 16 * 3600 * 1000 && cell.getCellStyle().getDataFormat() == 14) {
						value = new SimpleDateFormat("yyyy-MM-dd").format(date);
					} else {
						value = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
					}
					return value.toString();
				}
			} else {// 数值
				// System.out.println("Value:"+cell.getNumericCellValue());
				String numberStr = new HSSFDataFormatter().formatCellValue(cell);
				// 货币格式，如：1,200.00
				if (numberStr.contains(",")) {
					numberStr = numberStr.replace(",", "");
				}
				if (numberStr.contains("E")) { // 科学计算法
					numberStr = new DecimalFormat("0").format(cell.getNumericCellValue()); // 4.89481368464913E14还原为长整数
					value = Long.parseLong(numberStr);
				} else {
					if (numberStr.contains(".")) { // 小数
						value = Double.parseDouble(numberStr);
					} else { // 转换为整数
						value = Long.parseLong(numberStr);
					}
				}
				return value.toString();
			}
			return value.toString();
		case Cell.CELL_TYPE_STRING:
			return cell.getStringCellValue();
		default:
			return cell.getStringCellValue();
		}
	}
}
