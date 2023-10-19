package com.serezka.jpt.telegram.utils;

import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Read {
    public static final int width_limit = 20;
    public static final int height_limit = 200;
    public static final int max_sheets = 10;

    public static String getData(InputStream is, String filePath) {
        if (filePath.endsWith(".xls")) return excel(is);
        if (filePath.endsWith(".docx")) return word(is);
        return file(is);
    }

    @SneakyThrows // < todo
    public static String excel(InputStream is) {
        Workbook workbook = new HSSFWorkbook(is);

        List<Sheet> sheets = IntStream.range(0, Math.min(workbook.getNumberOfSheets(), max_sheets)).mapToObj(workbook::getSheetAt).toList();
        Map<String, String> parsedSheets = new HashMap<>();
        for (Sheet sheet : sheets) {
            StringBuilder result = new StringBuilder();

            Iterator<Row> rowIterator = sheet.iterator();
            sheet.forEach(row -> {
                q: for (int limH = 0; limH < width_limit && rowIterator.hasNext(); limH++) {
                    Iterator<Cell> cellIterator = rowIterator.next().iterator();
                    for (int limW = 0; limW < height_limit && cellIterator.hasNext(); limW++) {
                        Cell cell = cellIterator.next();
                        if (cell.toString().isBlank()) continue q;

                        result.append(switch (cell.getCellType()) {
                            case NUMERIC -> cell.getNumericCellValue();
                            case STRING -> cell.getStringCellValue();
                            case null, default -> "";
                        }).append("\t");
                    }
                    result.append("\n");
                }
            });

            parsedSheets.put(sheet.getSheetName(), result.toString());
        }

        workbook.close();
        return parsedSheets.entrySet().stream().map(e -> (e.getKey() + ":\n" + e.getValue() + "\n\n")).collect(Collectors.joining());
    }

    @SneakyThrows
    public static String word(InputStream is) {
        XWPFDocument document = new XWPFDocument(is);
        XWPFWordExtractor wordExtractor = new XWPFWordExtractor(document);
        document.close();
        return wordExtractor.getText();
    }

    @SneakyThrows
    public static String file(InputStream is) {
        return IOUtils.toString(is, StandardCharsets.UTF_8);
    }
}
