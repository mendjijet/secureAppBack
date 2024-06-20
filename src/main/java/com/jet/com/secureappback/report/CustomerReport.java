package com.jet.com.secureappback.report;

import static java.util.stream.IntStream.range;
import static org.apache.commons.lang3.time.DateFormatUtils.format;

import com.jet.com.secureappback.domain.Customer;
import com.jet.com.secureappback.exception.ApiException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.InputStreamResource;

/**
 * @project secureCapita
 * @version 1.0
 * @author MENDJIJET
 * @since 19/06/2024
 */
@Slf4j
public class CustomerReport {
  public static final String DATE_FORMATTER = "yyyy-MM-dd hh:mm:ss";
  private static String[] HEADERS = {
    "ID", "Name", "Email", "Type", "Status", "Address", "Phone", "Created At"
  };
  private XSSFWorkbook workbook;
  private XSSFSheet sheet;
  private List<Customer> customers;

  public CustomerReport(List<Customer> customers) {
    this.customers = customers;
    workbook = new XSSFWorkbook();
    sheet = workbook.createSheet("Customers");
    setHeaders();
  }

  private void setHeaders() {
    Row headerRow = sheet.createRow(0);
    CellStyle style = workbook.createCellStyle();
    XSSFFont font = workbook.createFont();
    font.setBold(true);
    font.setFontHeight(14);
    style.setFont(font);
    range(0, HEADERS.length)
        .forEach(
            index -> {
              Cell cell = headerRow.createCell(index);
              cell.setCellValue(HEADERS[index]);
              cell.setCellStyle(style);
            });
  }

  public InputStreamResource export() {
    return generateReport();
  }

  private InputStreamResource generateReport() {
    try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
      CellStyle style = workbook.createCellStyle();
      XSSFFont font = workbook.createFont();
      font.setFontHeight(10);
      style.setFont(font);
      int rowIndex = 1;
      for (Customer customer : customers) {
        Row row = sheet.createRow(rowIndex++);
        row.createCell(0).setCellValue(customer.getId());
        row.createCell(1).setCellValue(customer.getName());
        row.createCell(2).setCellValue(customer.getEmail());
        row.createCell(3).setCellValue(customer.getType());
        row.createCell(4).setCellValue(customer.getStatus());
        row.createCell(5).setCellValue(customer.getAddress());
        row.createCell(6).setCellValue(customer.getPhone());
        row.createCell(7).setCellValue(format(customer.getCreatedAt(), DATE_FORMATTER));
      }
      workbook.write(out);
      return new InputStreamResource(new ByteArrayInputStream(out.toByteArray()));
    } catch (Exception exception) {
      log.error(exception.getMessage());
      throw new ApiException("Unable to export report file");
    }
  }
}
