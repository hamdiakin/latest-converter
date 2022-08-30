package xml2xlsx;

import java.util.Set;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.*;

public class ValidationGenerator {

    public static CellRangeAddressList getCellRangeAddress(String[] headerRow, String columnName) {
        // get the index of a string in an array
        int index = 0;
        for (int i = 0; i < headerRow.length; i++) {
            if (headerRow[i].equals(columnName)) {
                index = i;
            }
        }
        // create a range of cells
        // row values choosen as "-1" to taget out entirity of the column
        CellRangeAddressList addressList = new CellRangeAddressList(-1, -1, index, index);
        return addressList;
    }

    public static String[] getDataValidation(Set<String> set) {

        String[] dataValidatinlist = new String[10];
        dataValidatinlist = set.toArray(dataValidatinlist);
        String[] dataTypesArray = set.toArray(new String[0]);
        return dataTypesArray;
    }

    public static void setDataValidationWithList(XSSFSheet sheet, CellRangeAddressList addressList,
            String[] dropDownList) {

        DataValidationHelper validationHelper = new XSSFDataValidationHelper(sheet);
        DataValidationConstraint constraint = validationHelper.createExplicitListConstraint(dropDownList);
        DataValidation dataValidation = validationHelper.createValidation(constraint, addressList);
        dataValidation.setSuppressDropDownArrow(true);
        dataValidation.getEmptyCellAllowed();
        dataValidation.setShowErrorBox(true);
        sheet.addValidationData(dataValidation);

    }
}
