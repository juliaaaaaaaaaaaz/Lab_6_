package org.example.utils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.example.data.LabWork;
import java.util.LinkedHashSet;

public class ReadFromDataBase {
    public static String fileName;
    public LinkedHashSet<LabWork> read(DataBaseManipulator dataBaseManipulator) {
        
        LinkedHashSet<LabWork> res = new LinkedHashSet<>();
        LabWorkDataBase labWorkDataBase = new LabWorkDataBase(dataBaseManipulator);
        String fromDB;
        String jsonTypeStr = "";

        while (true) {
            fromDB = labWorkDataBase.selectAll();
            if (fromDB != null)
                break;
            else
                return null;
        }
        jsonTypeStr += fromDB.replace(": ", "\":").replace("\" ", "\",\"").replace("} ", "},\"")
                .replace(" minimalpoint", ",\"minimalpoint").replace(" maximumpoint", ",\"maximumpoint")
                .replace(" difficulty", ",\"difficulty")
                .replace(" practiceHours", ",\"practiceHours");

        Gson gson = new GsonBuilder()
                .setDateFormat("EEE MMM dd hh:mm:ss zzz yyyy")
                .create();
        for (String elem: jsonTypeStr.split(",,,")){

            Long minimalpoint = Long.valueOf(elem.substring(elem.indexOf(",\"minimalpoint\":") + 16, elem.indexOf(",\"maximumpoint\":"))); //потому что gson не делает это нормально, а пишет null
            long maximumpoint = Long.parseLong(elem.substring(elem.indexOf(",\"maximumpoint\":") + 16, elem.indexOf(",\"difficulty\":"))); //потому что gson не делает это нормально, а пишет 0
            String author = elem.substring(elem.indexOf(",\"author\":") + 10, elem.length() - 2); //потому что gson не делает это нормально, а пишет null

            LabWork myMap = gson.fromJson(elem, LabWork.class);

            myMap.setMaximumPoint(maximumpoint);
            myMap.setMinimalPoint(minimalpoint);
            myMap.setAuthor(author);
            res.add(myMap);
            res.remove(null);
        }
        return res;
    }

    public String getFileName(){
        return ReadFromDataBase.fileName;
    }

}