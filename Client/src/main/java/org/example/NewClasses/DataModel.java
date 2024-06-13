package org.example.NewClasses;

import java.util.function.BiConsumer;
import java.util.function.Function;

public class DataModel {
    private String column1;
    private String column2;
    private String column3;
    private String column4;
    private String column5;
    private String column6;
    private String column7;
    private String column8;
    private String column9;
    private String column10;
    private String column11;
    private String column12;

    public DataModel(String column1, String column2, String column3, String column4, String column5,
                     String column6, String column7, String column8, String column9, String column10,
                     String column11, String column12) {
        this.column1 = column1;
        this.column2 = column2;
        this.column3 = column3;
        this.column4 = column4;
        this.column5 = column5;
        this.column6 = column6;
        this.column7 = column7;
        this.column8 = column8;
        this.column9 = column9;
        this.column10 = column10;
        this.column11 = column11;
        this.column12 = column12;
    }

    public String getColumn2() {
        return column2;
    }

    public void setColumn2(String column2) {
        this.column2 = column2;
    }

    public String getColumn12() {
        return column12;
    }

    public void setColumn12(String column12) {
        this.column12 = column12;
    }

    public String getColumn11() {
        return column11;
    }

    public void setColumn11(String column11) {
        this.column11 = column11;
    }

    public String getColumn10() {
        return column10;
    }

    public void setColumn10(String column10) {
        this.column10 = column10;
    }

    public String getColumn9() {
        return column9;
    }

    public void setColumn9(String column9) {
        this.column9 = column9;
    }

    public String getColumn8() {
        return column8;
    }

    public void setColumn8(String column8) {
        this.column8 = column8;
    }

    public String getColumn7() {
        return column7;
    }

    public void setColumn7(String column7) {
        this.column7 = column7;
    }

    public String getColumn6() {
        return column6;
    }

    public void setColumn6(String column6) {
        this.column6 = column6;
    }

    public String getColumn5() {
        return column5;
    }

    public void setColumn5(String column5) {
        this.column5 = column5;
    }

    public String getColumn4() {
        return column4;
    }

    public void setColumn4(String column4) {
        this.column4 = column4;
    }

    public String getColumn3() {
        return column3;
    }

    public void setColumn3(String column3) {
        this.column3 = column3;
    }

    public String getColumn1() {
        return column1;
    }

    public void setColumn1(String column1) {
        this.column1 = column1;
    }

    public Function[] getGetters(){
        Function<DataModel, String>[] columnGetters = new Function[12];
        columnGetters[0] = DataModel::getColumn1;
        columnGetters[1] = DataModel::getColumn2;
        columnGetters[2] = DataModel::getColumn3;
        columnGetters[3] = DataModel::getColumn4;
        columnGetters[4] = DataModel::getColumn5;
        columnGetters[5] = DataModel::getColumn6;
        columnGetters[6] = DataModel::getColumn7;
        columnGetters[7] = DataModel::getColumn8;
        columnGetters[8] = DataModel::getColumn9;
        columnGetters[9] = DataModel::getColumn10;
        columnGetters[10] = DataModel::getColumn11;
        columnGetters[11] = DataModel::getColumn12;
        return columnGetters;
    }

    public BiConsumer[] getSetters(){
        BiConsumer<DataModel, String>[] columnSetters = new BiConsumer[12];
        columnSetters[1] = DataModel::setColumn2;
        columnSetters[2] = DataModel::setColumn3;
        columnSetters[3] = DataModel::setColumn4;
        columnSetters[5] = DataModel::setColumn6;
        columnSetters[6] = DataModel::setColumn7;
        columnSetters[7] = DataModel::setColumn8;
        columnSetters[8] = DataModel::setColumn9;
        columnSetters[9] = DataModel::setColumn10;
        columnSetters[10] = DataModel::setColumn11;
        return columnSetters;
    }

}
