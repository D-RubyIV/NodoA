package org.example.demo.exporter;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.example.demo.dto.product.response.ProductResponseDTO;
import org.example.demo.entity.Category;
import org.example.demo.entity.Product;

import java.io.IOException;
import java.util.List;

public class ProductExcelExporter {
    private XSSFWorkbook workbook;
    private XSSFSheet sheet;
    private List<ProductResponseDTO> listProducts;

    public ProductExcelExporter(List<ProductResponseDTO> listProducts) {
        this.listProducts = listProducts;
        workbook = new XSSFWorkbook();
    }


    private void writeHeaderLine() {
        sheet = workbook.createSheet("Sản phẩm");
        Row row = sheet.createRow(0);

        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setFontHeight(16);
        style.setFont(font);

        createCell(row, 0, "ID", style);
        createCell(row, 1, "Mã sản phẩm", style);
        createCell(row, 2, "Mã danh mục", style);
        createCell(row, 3, "Tên", style);
        createCell(row, 4, "Mô tả", style);
        createCell(row, 5, "Ảnh", style);
        createCell(row, 6, "Giá", style);
        createCell(row, 7, "Số lượng", style);
        createCell(row, 8, "Trạng thái", style);
        createCell(row, 9, "Ngày tạo", style);
        createCell(row, 10, "Ngày sửa", style);
        createCell(row, 11, "Người tạo", style);
        createCell(row, 12, "Người sửa", style);
    }

    private void createCell(Row row, int columnCount, Object value, CellStyle style) {
        sheet.autoSizeColumn(columnCount);
        Cell cell = row.createCell(columnCount);
        if (value instanceof Integer) {
            cell.setCellValue((Integer) value);
        } else if (value instanceof Boolean) {
            cell.setCellValue((Boolean) value);
        } else {
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

        for (ProductResponseDTO user : listProducts) {
            Row row = sheet.createRow(rowCount++);
            int columnCount = 0;

            createCell(row, columnCount++, user.getId() != null ? user.getId() : "", style);
            createCell(row, columnCount++, user.getProductCode() != null ? user.getProductCode() : "", style);
            createCell(row, columnCount++, user.getCategoryCodes() != null ? user.getCategoryCodes() : "", style);
            createCell(row, columnCount++, user.getName() != null ? user.getName() : "", style);
            createCell(row, columnCount++, user.getDescription() != null ? user.getDescription() : "", style);
            createCell(row, columnCount++, user.getImage() != null ? user.getImage() : "", style);
            createCell(row, columnCount++, user.getPrice() != null ? user.getPrice() : "", style);
            createCell(row, columnCount++, user.getQuantity() != null ? user.getQuantity() : "", style);
            createCell(row, columnCount++, user.getStatus() != null ? user.getStatus() : "", style);
            createCell(row, columnCount++, user.getCreatedDate() != null ? user.getCreatedDate() : "", style);
            createCell(row, columnCount++, user.getModifiedDate() != null ? user.getModifiedDate() : "", style);
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

