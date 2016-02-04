package util;


import entities.Invite;
import entities.User;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.WorkbookUtil;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by robin on 2016-01-18.
 */
public class ExcelHandler {

    public static Workbook exportToExcel(ArrayList<User> students, ArrayList<int[]> attendanceArrayList) {
        Workbook wb = new XSSFWorkbook();  // or new HSSFWorkbook();
        CreationHelper createHelper = wb.getCreationHelper();

        // create a new sheet
        Sheet s = wb.createSheet(WorkbookUtil.createSafeSheetName("Närvarorapport"));
        // declare a row object reference
        Row r = null;
        // declare a cell object reference
        Cell c = null;
        // cell style
        CellStyle cs = wb.createCellStyle();
        //CellStyle cs2 = wb.createCellStyle();
        DataFormat df = wb.createDataFormat();

        // create 2 fonts objects
        Font f = wb.createFont();
        //Font f2 = wb.createFont();

        // Set font 1 to 12 point type, blue and bold
        f.setBoldweight(Font.BOLDWEIGHT_BOLD);

        // Set font 2 to 10 point type, red and bold
        //f2.setFontHeightInPoints((short) 10);
        //f2.setColor( IndexedColors.RED.getIndex() );
        //f2.setBoldweight(Font.BOLDWEIGHT_BOLD);

        // Set cell style and formatting
        cs.setFont(f);
        //cs.setDataFormat(df.getFormat("#,##0.0"));

        // Set the other cell style and formatting
        //cs2.setBorderBottom(cs2.BORDER_THIN);
        //cs2.setDataFormat(df.getFormat("text"));
        //cs2.setFont(f2);

        //Skapa rubriker
        r = s.createRow(0);
        String[] headers = {"Namn", "E-postadress", "Personnummer", "Närvaro", "Sjuk", "VAB", "Övrig frånvaro", "Ogiltig frånvaro"};
        int i = 0;
        for (String header : headers) {
            c = r.createCell(i);
            c.setCellStyle(cs);
            c.setCellValue(header);
            i++;
        }

        // Fyll tabellen med värden
        for(int rownum = 0; rownum < students.size(); rownum++) {
            r = s.createRow(rownum + 1);
            c = r.createCell(0);
            c.setCellValue(students.get(rownum).getName());
            c = r.createCell(1);
            c.setCellValue(students.get(rownum).getEmail());
            c = r.createCell(2);
            c.setCellValue(students.get(rownum).getPersonNumber());
            c = r.createCell(3);
            c.setCellValue(attendanceArrayList.get(rownum)[0]);
            c = r.createCell(4);
            c.setCellValue(attendanceArrayList.get(rownum)[1]);
            c = r.createCell(5);
            c.setCellValue(attendanceArrayList.get(rownum)[2]);
            c = r.createCell(6);
            c.setCellValue(attendanceArrayList.get(rownum)[3]);
            c = r.createCell(7);
            c.setCellValue(attendanceArrayList.get(rownum)[4]);
            }

        return wb;
        }

    public static ArrayList<Invite> importExcelFile(int classId, File file) {
        //Get the workbook instance for XLS file
        ArrayList<Invite> inviteList = new ArrayList<Invite>();
        try {

            Workbook workbook = new XSSFWorkbook(file);

            //Get first sheet from the workbook
            Sheet sheet = workbook.getSheetAt(0);

            //Get iterator to all the rows in current sheet
            Iterator<Row> rowIterator = sheet.iterator();

            //Hoppa över rad 1
            rowIterator.next();

            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                //Hämta epost och skapa ny invite i listan
                inviteList.add(new Invite(row.getCell(3).getStringCellValue(), classId));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return inviteList;
    }

}
