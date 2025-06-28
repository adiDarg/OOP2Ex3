import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.io.FilenameUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.swing.*;
import java.io.*;
import java.util.*;

public class CSVReader {
    private static final String COMMA_DELIMITER = ",";
    private String path;
    List<List<String>> records;
    public CSVReader() {
        records = new ArrayList<>();
        path = "";
    }
    public void chooseFile(){
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setMultiSelectionEnabled(false);
        fileChooser.setCurrentDirectory(new File(path));
        int returnVal = fileChooser.showOpenDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            path = fileChooser.getSelectedFile().getAbsolutePath();
        }
    }
    public void read() throws Exception {
        File file = new File(path);
        if (!file.exists()) {
            throw new Exception("No File Inputted");
        }
        if (FilenameUtils.getExtension(path).equals("xlsx")){
            Workbook workbook = new XSSFWorkbook(file);
            Sheet sheet = workbook.getSheetAt(0);
            String csvFileName = "ConvertedCSVFiles/" + FilenameUtils.getBaseName(path) + ".csv";
            try (
                    BufferedWriter writer = new BufferedWriter(new FileWriter(csvFileName));
                    CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT)
            ) {
                for (Row row : sheet) {
                    String[] cells = new String[row.getLastCellNum()];
                    for (int i = 0; i < row.getLastCellNum(); i++) {
                        Cell cell = row.getCell(i);
                        cells[i] = (cell == null) ? "" : getCellValue(cell);
                    }
                    csvPrinter.printRecord((Object[]) cells);
                }
            }
            workbook.close();
            path = csvFileName;
        }
        else if (!FilenameUtils.getExtension(path).equals("csv")){
            throw new Exception("File type not supported - Input CSV or XLSX");
        }

        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(COMMA_DELIMITER);
                records.add(Arrays.asList(values));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public Metadata convertToMetadata() throws Exception{
        try {
            Metadata result = new Metadata();
            boolean readComponents = false;
            boolean readAttributes = false;
            boolean readRelations = false;
            boolean attOfComponents = true;
            boolean skipCheck = false;
            for (List<String> record: records) {
                if (record.isEmpty()) {
                    continue;
                }
                if (!skipCheck){
                    readComponents = record.get(0).equals("Components");
                    readAttributes = record.get(0).equals("Attributes");
                    readRelations = record.get(0).equals("Relations");
                    skipCheck = readComponents || readAttributes || readRelations;
                    continue;
                }
                else {
                    skipCheck = false;
                }
                if (readComponents){
                    for (int i = 0; i < record.size(); i+=2) {
                        Component component = new Component(record.get(i),record.get(i+1));
                        result.addComponent(component);
                    }
                    continue;
                }
                if (readAttributes && attOfComponents){
                    int bound = calcBoundOfAttributes(record);
                    for (int i = records.indexOf(record); i < bound; i++) {
                        List<String> curr = records.get(i);
                        for (int j = 0; j < curr.size(); j+=2) {
                            if (curr.get(j).isEmpty()){
                                continue;
                            }
                            result.getComponents().get(j/2).addAttribute(new Attribute(curr.get(j),curr.get(j+1)));
                        }
                    }
                    attOfComponents = false;
                    continue;
                }
                if (readRelations){
                    for (int i = 0; i < record.size(); i+=2) {
                        RelationType type = record.get(i + 1).equals("Associative") ? RelationType.Association : RelationType.Grouping;

                        List<String> nextRecord = records.get(records.indexOf(record) + 1);
                        Component source = result.getComponent(nextRecord.get(i));
                        Component target = result.getComponent(nextRecord.get(i+1));

                        Relation relation = new Relation(record.get(i), type,source,target);
                        result.addRelation(relation);
                    }
                }
                if (readAttributes){
                    int bound = calcBoundOfAttributes(record);
                    for (int i = records.indexOf(record); i < bound; i++) {
                        List<String> curr = records.get(i);
                        for (int j = 0; j < curr.size(); j+=2) {
                            if (curr.get(j).isEmpty() || curr.get(j+1).isEmpty()){
                                continue;
                            }
                            result.getRelations().get(j/2).addAttribute(new Attribute(curr.get(j),curr.get(j+1)));
                        }
                    }
                }
            }
            return result;
        }
        catch (Exception e) {
            throw new Exception("Invalid CSV file");
        }
    }

    private int calcBoundOfAttributes(List<String> record) {
        int bound = records.indexOf(record);
        while (bound < records.size() &&
                !records.get(bound).get(0).equals("Components") &&
                !records.get(bound).get(0).equals("Relations")) {
            bound++;
        }
        return bound;
    }

    private static String getCellValue(Cell cell) {
        if (cell == null) return "";

        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue();
            case NUMERIC -> DateUtil.isCellDateFormatted(cell)
                    ? cell.getDateCellValue().toString()
                    : String.valueOf(cell.getNumericCellValue());
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            case FORMULA -> {
                // Safely evaluate formulas
                try {
                    FormulaEvaluator evaluator = cell.getSheet().getWorkbook().getCreationHelper().createFormulaEvaluator();
                    CellValue evaluatedValue = evaluator.evaluate(cell);
                    yield switch (evaluatedValue.getCellType()) {
                        case STRING -> evaluatedValue.getStringValue();
                        case NUMERIC -> String.valueOf(evaluatedValue.getNumberValue());
                        case BOOLEAN -> String.valueOf(evaluatedValue.getBooleanValue());
                        default -> "";
                    };
                } catch (Exception e) {
                    yield cell.getCellFormula(); // fallback
                }
            }
            default -> "";
        };
    }

}
