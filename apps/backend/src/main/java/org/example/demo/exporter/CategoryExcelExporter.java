package org.example.demo.exporter;

import java.io.IOException;
import java.util.List;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.example.demo.entity.Category;

public class CategoryExcelExporter {
    private XSSFWorkbook workbook;
    private XSSFSheet sheet;
    private List<Category> listCategory;

    public CategoryExcelExporter(List<Category> listCategory) {
        this.listCategory = listCategory;
        workbook = new XSSFWorkbook();
    }


    private void writeHeaderLine() {
        sheet = workbook.createSheet("Users");

        Row row = sheet.createRow(0);

        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setFontHeight(16);
        style.setFont(font);

        createCell(row, 0, "ID", style);
        createCell(row, 1, "Mã", style);
        createCell(row, 2, "Tên", style);
        createCell(row, 3, "Mô tả", style);
        createCell(row, 4, "Ảnh", style);
        createCell(row, 5, "Trạng thái", style);
        createCell(row, 6, "Ngày tạo", style);
        createCell(row, 7, "Ngày sửa", style);
        createCell(row, 8, "Người tạo", style);
        createCell(row, 9, "Người sửa", style);

    }

    private void createCell(Row row, int columnCount, Object value, CellStyle style) {
        sheet.autoSizeColumn(columnCount);
        Cell cell = row.createCell(columnCount);
        if (value instanceof Integer) {
            cell.setCellValue((Integer) value);
        } else if (value instanceof Boolean) {
            cell.setCellValue((Boolean) value);
        }else {
            cell.setCellValue(value.toString());
        }
        cell.setCellStyle(style);
    }

    private void writeDataLines() {
        int rowCount = 1;

        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setFontHeight(14);
        style.setFont(font);

        for (Category user : listCategory) {
            Row row = sheet.createRow(rowCount++);
            int columnCount = 0;

            createCell(row, columnCount++, user.getId() != null ? user.getId() : "", style);
            createCell(row, columnCount++, user.getCategoryCode() != null ? user.getCategoryCode() : "", style);
            createCell(row, columnCount++, user.getName() != null ? user.getName() : "", style);
            createCell(row, columnCount++, user.getDescription() != null ? user.getDescription() : "", style);
            createCell(row, columnCount++, user.getImage() != null ? user.getImage() : "", style);
            createCell(row, columnCount++, user.getStatus() != null ? user.getStatus().toString() : "", style);
            createCell(row, columnCount++, user.getCreatedDate() != null ? user.getCreatedDate().toString() : "", style);
            createCell(row, columnCount++, user.getModifiedDate() != null ? user.getModifiedDate().toString() : "", style);
            createCell(row, columnCount++, user.getCreatedBy() != null ? user.getCreatedBy() : "", style);
            createCell(row, columnCount++, user.getModifiedBy() != null ? user.getModifiedBy() : "", style);
        }
    }

    public void export(HttpServletResponse response) throws IOException {
        writeHeaderLine();
        writeDataLines();

        ServletOutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        workbook.close();

        outputStream.close();

    }
}