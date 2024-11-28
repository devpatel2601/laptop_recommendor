package com.example.laptoprecommendationsystem.service;

import com.example.laptoprecommendationsystem.model.Laptop;
import com.example.laptoprecommendationsystem.repository.LaptopRepository;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.context.event.EventListener;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;

@Service
public class ExcelDataLoaderService {

    private static final Logger logger = LoggerFactory.getLogger(ExcelDataLoaderService.class);
    private static final String EXCEL_FILE_PATH = "src/main/resources/products_Final.xlsx"; // Update this path to your Excel file location

    @Autowired
    private LaptopRepository laptopRepository;

    @EventListener(ApplicationReadyEvent.class)
    public void loadExcelData() {
        // Check if the database already contains data
        if (laptopRepository.count() > 0) {
            logger.info("Data already exists in the database. Skipping Excel data loading.");
            return; // Do not load data if it already exists
        }

        logger.info("Loading data from Excel file: {}", EXCEL_FILE_PATH);

        try (FileInputStream fileInputStream = new FileInputStream(new File(EXCEL_FILE_PATH))) {
            Workbook workbook = WorkbookFactory.create(fileInputStream);
            Sheet sheet = workbook.getSheetAt(0);

            // Iterate over the rows in the Excel sheet
            Iterator<Row> rowIterator = sheet.iterator();
            boolean isHeaderRow = true;

            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();

                // Skip the header row
                if (isHeaderRow) {
                    isHeaderRow = false;
                    continue;
                }

                // Skip empty rows
                if (isRowEmpty(row)) {
                    continue;
                }

                // Read the cells from the current row
                Laptop laptop = new Laptop();
                laptop.setBrandName(getCellValueAsString(row.getCell(0))); // Column "Brand Name"
                laptop.setProductName(getCellValueAsString(row.getCell(1))); // Column "Product Name"

                // Clean price and handle non-digit characters
                Double price = cleanAndParsePrice(row.getCell(2));
                laptop.setPrice(price != null ? price : 0.0); // Default to 0 if price is null

                laptop.setImage(getCellValueAsString(row.getCell(3)));      // Column "Image"
                laptop.setOs(getCellValueAsString(row.getCell(4)));         // Column "OS"
                laptop.setProcessor(getCellValueAsString(row.getCell(5)));  // Column "Processor"
                laptop.setGraphics(getCellValueAsString(row.getCell(6)));   // Column "Graphics"
                laptop.setDisplay(getCellValueAsString(row.getCell(7)));    // Column "Display"
                laptop.setMemory(getCellValueAsString(row.getCell(8)));     // Column "Memory"
                laptop.setStorage(getCellValueAsString(row.getCell(9)));    // Column "Storage"
                laptop.setFilepath(getCellValueAsString(row.getCell(10)));  // Column "FilePath"

                // Save the laptop object to the database
                laptopRepository.save(laptop);
                logger.debug("Saved laptop: {}", laptop);
            }

            workbook.close();
            logger.info("Data loading from Excel file completed successfully.");
        } catch (IOException e) {
            logger.error("Error reading Excel file: {}", EXCEL_FILE_PATH, e);
        }
    }

    /**
     * Helper method to get the cell value as a String.
     *
     * @param cell The cell to read.
     * @return The cell's value as a String.
     */
    private String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return null;
        }
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                }
                return String.valueOf(cell.getNumericCellValue());
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                try {
                    return cell.getStringCellValue();
                } catch (IllegalStateException e) {
                    return String.valueOf(cell.getNumericCellValue());
                }
            default:
                return null;
        }
    }

    /**
     * Helper method to clean the price value and parse it as a Double.
     *
     * @param cell The cell to read.
     * @return The cleaned cell's value as a Double, or null if not applicable.
     */
    private Double cleanAndParsePrice(Cell cell) {
        if (cell == null) {
            return null;
        }
        String rawValue = getCellValueAsString(cell);
        if (rawValue != null) {
            // Remove non-digit characters except the decimal point
            String cleanedValue = rawValue.replaceAll("[^\\d.]", "");
            try {
                return Double.parseDouble(cleanedValue);
            } catch (NumberFormatException e) {
                logger.warn("Error parsing price value: {}", rawValue, e);
                return null;
            }
        }
        return null;
    }

    /**
     * Helper method to check if a row is empty.
     *
     * @param row The row to check.
     * @return True if the row is empty, false otherwise.
     */
    private boolean isRowEmpty(Row row) {
        if (row == null) {
            return true;
        }
        for (int cellIndex = 0; cellIndex < row.getLastCellNum(); cellIndex++) {
            Cell cell = row.getCell(cellIndex);
            if (cell != null && cell.getCellType() != CellType.BLANK && !getCellValueAsString(cell).trim().isEmpty()) {
                return false;
            }
        }
        return true;
    }
}
