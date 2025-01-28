package com.dream.six.utils;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

public class ExcelUtils {

    public static void writeDTOInfoToExcel(Map<String, List<String>> columnValues, OutputStream outputStream, String sheetName) throws IOException {
        try (Workbook workbook = new SXSSFWorkbook()) {
            Sheet sheet = workbook.createSheet(sheetName);

            // Create header row with column names in the order of keys in columnValues map
            Row headerRow = sheet.createRow(0);
            int headerIndex = 0;
            for (Map.Entry<String, List<String>> entry : columnValues.entrySet()) {
                headerRow.createCell(headerIndex).setCellValue(entry.getKey());
                headerIndex++;
            }


            // Add values to the corresponding columns based on the order of keys
            int rowIndex = 1;
            for (int i = 0; i < getMaxColumnValueSize(columnValues); i++) {
                Row row = sheet.createRow(rowIndex);
                int colIndex = 0;
                for (Map.Entry<String, List<String>> entry : columnValues.entrySet()) {
                    List<String> values = entry.getValue();
                    String cellValue = i < values.size() ? values.get(i) : ""; // Get value or empty string if not available
                    row.createCell(colIndex).setCellValue(cellValue);
                    colIndex++;
                }
                rowIndex++;
            }

            workbook.write(outputStream);
        }
    }


    private static int getMaxColumnValueSize(Map<String, List<String>> columnValues) {
        return columnValues.values().stream()
                .mapToInt(List::size)
                .max()
                .orElse(0);
    }


}

