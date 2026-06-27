package util;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import java.io.FileOutputStream;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ReportManager {

    public static void generateBusinessReport(String reportTitle, String sqlQuery, String outputFileName) {
        Document document = new Document(PageSize.A4, 36, 36, 54, 36);
        try {
            // Force file path to be absolute to avoid confusion
            PdfWriter.getInstance(document, new FileOutputStream(outputFileName));
            document.open();

            // ... Styles wahi rahengi ...
            Font titleFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD, new BaseColor(0, 210, 211));
            Font headerFont = new Font(Font.FontFamily.HELVETICA, 11, Font.BOLD, BaseColor.WHITE);
            Font cellFont = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL, BaseColor.DARK_GRAY);

            // Add Header Title
            document.add(new Paragraph("IRON PULSE FITNESS CLUB", titleFont));
            document.add(new Paragraph(reportTitle.toUpperCase()));

            try (Connection conn = DBConnection.getConnection();
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sqlQuery)) {

                if (!rs.isBeforeFirst()) {
                    System.out.println("WARNING: Query returned no results: " + sqlQuery);
                }

                ResultSetMetaData metaData = rs.getMetaData();
                int columnCount = metaData.getColumnCount();
                PdfPTable table = new PdfPTable(columnCount);
                table.setWidthPercentage(100);

                // Add Headers
                for (int i = 1; i <= columnCount; i++) {
                    PdfPCell cell = new PdfPCell(new Phrase(metaData.getColumnLabel(i).toUpperCase(), headerFont));
                    cell.setBackgroundColor(new BaseColor(47, 53, 66));
                    table.addCell(cell);
                }

                // Add Data
                while (rs.next()) {
                    for (int i = 1; i <= columnCount; i++) {
                        table.addCell(new Phrase(rs.getString(i) != null ? rs.getString(i) : "N/A", cellFont));
                    }
                }
                document.add(table);
            }
        } catch (Exception e) {
            System.err.println("PDF Generation Error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (document.isOpen()) document.close();
            System.out.println("PDF Process Finished for: " + outputFileName);
        }
    }
}