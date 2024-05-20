package org.example.utils;

import org.example.data.LabWork;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

public class LabWorkDataBase {
    //CREATE TABLE works(id serial PRIMARY KEY, name varchar(80), x BIGINT NOT NULL CHECK(x < 268), y REAL CHECK(y < 55), creationDate varchar(80) NOT NULL, minimalPoint INT CHECK(minimalPoint > 0), maximumPoint INT CHECK(maximumPoint > 0), difficulty varchar(80) NOT NULL, disciplineName varchar(80), practiceHours INT, selfStudyHours int, author varchar(80))
    private DataBaseManipulator dataBaseManipulator;
    private final String SELECT_ALL = "SELECT * FROM works";
    private final String DELETE = "DELETE FROM works WHERE id = ?";
    private final String INSERT_NEW = "INSERT INTO works(name, x, y, creationdate, minimalpoint, maximumpoint, difficulty, disciplinename, practicehours, selfstudyhours, author) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private final String UPDATE_TABLE = "UPDATE works SET name=?, x=?, y=?, minimalpoint=?, maximumpoint=?, difficulty=?, disciplinename=?, practicehours=?, selfstudyhours=?, author=? WHERE id = ?";
    private final String CLEAR = "DELETE FROM works WHERE author = ?";
    private final String SELECT_CLEAR_IDS = "SELECT id FROM works WHERE author = ?";
    private final String FIND_MAX_ID = "SELECT MAX(id) AS ID FROM works";
    List<Long> ids = new ArrayList<>();

    public LabWorkDataBase(DataBaseManipulator dataBaseManipulator){
        this.dataBaseManipulator = dataBaseManipulator;
    }

    public LinkedHashSet<LabWork> OnAwake(String tablename) throws IOException {
        //read from db and write to cllection
        ReadFromDataBase dbreader = new ReadFromDataBase();
        //this.tablename = tablename;
        return dbreader.read(dataBaseManipulator);
    }

    public String selectAll(){
        PreparedStatement selectAllPreparedStatement = null;
        try {
            selectAllPreparedStatement =
                    dataBaseManipulator.getPreparedStatement(SELECT_ALL, false);
            ResultSet resultSet = selectAllPreparedStatement.executeQuery();
            String res = "";

            while (resultSet.next()){
                res += String.format("{\"id\":%s,\"name\":\"%s\",\"coordinates\":{\"x\":%s,\"y\":%s},\"creationDate\":\"%s\",\"minimalpoint\":%s,\"maximumpoint\":%s,\"difficulty\":\"%s\",\"discipline\":{\"name\":\"%s\",\"practiceHours\":%s,\"selfStudyHours\":%s,\"author\":%s}},,,",
                        resultSet.getString("id"),
                        resultSet.getString("name"),
                        resultSet.getString("x"),
                        resultSet.getString("y"),
                        resultSet.getString("creationDate"),
                        resultSet.getString("minimalpoint"),
                        resultSet.getString("maximumpoint"),
                        resultSet.getString("difficulty"),
                        resultSet.getString("disciplinename"),
                        resultSet.getString("practicehours"),
                        resultSet.getString("selfstudyhours"),
                        resultSet.getString("author"));
            }
            try {
                return res.substring(0, res.length() - 3);
            } catch (StringIndexOutOfBoundsException a){
                return null;
            }
        } catch (SQLException exception) {
            return null;
        }finally {
        dataBaseManipulator.closePreparedStatement(selectAllPreparedStatement);
    }
    }

    public boolean addElement(LabWork newLabWork){
        PreparedStatement addElementPreparedStatement = null;
        try {
            ArrayList<Object> elements = new ArrayList<>();
            elements.add(newLabWork.getName());
            elements.add(newLabWork.getCoordinates().getX());
            elements.add(newLabWork.getCoordinates().getY());
            elements.add(new SimpleDateFormat("EEE MMM dd hh:mm:ss zzz yyyy").format(newLabWork.getCreationDate()));
            elements.add(newLabWork.getMinimalPoint());
            elements.add(newLabWork.getMaximumPoint());
            elements.add(String.format("%s", newLabWork.getDifficulty()));
            elements.add(newLabWork.getDiscipline().getName());
            elements.add(newLabWork.getDiscipline().getPracticeHours());
            elements.add(newLabWork.getDiscipline().getSelfStudyHours());

            addElementPreparedStatement =
                    dataBaseManipulator.getPreparedStatement(INSERT_NEW, true);
            addElementPreparedStatement.setString(1, (String) elements.get(0));
            addElementPreparedStatement.setFloat(2, (Float) elements.get(1));
            addElementPreparedStatement.setDouble(3, (Double) elements.get(2));
            addElementPreparedStatement.setString(4, (String) elements.get(3));
            addElementPreparedStatement.setLong(5, (Long) elements.get(4));
            addElementPreparedStatement.setLong(6, (Long) elements.get(5));
            addElementPreparedStatement.setString(7, (String) elements.get(6));
            addElementPreparedStatement.setString(8, (String) elements.get(7));
            addElementPreparedStatement.setInt(9, (Integer) elements.get(8));
            addElementPreparedStatement.setInt(10, (Integer) elements.get(9));
            addElementPreparedStatement.setString(11,  dataBaseManipulator.getUserName());
            if (addElementPreparedStatement.executeUpdate() == 0) throw new SQLException();
            return true;
        } catch (SQLException exception) {
            exception.printStackTrace();
            return false;
        } finally {
            dataBaseManipulator.closePreparedStatement(addElementPreparedStatement);
        }
    }

    public Long getMaxId(){
        PreparedStatement getMaxIdPreparedStatement = null;
        try {
            getMaxIdPreparedStatement = dataBaseManipulator.getPreparedStatement(FIND_MAX_ID, false);
            ResultSet result = getMaxIdPreparedStatement.executeQuery();
            if (result.next()){
                return result.getLong("id");
            }
            return 1L;
        } catch (SQLException exception) {
            exception.printStackTrace();
            return 1L;
        } finally {
            dataBaseManipulator.closePreparedStatement(getMaxIdPreparedStatement);
        }
    }

    public boolean clear(){
        PreparedStatement clearPreparedStatement = null;
        try {
            clearPreparedStatement = dataBaseManipulator.getPreparedStatement(CLEAR, false);
            clearPreparedStatement.setString(1, dataBaseManipulator.getUserName());
            if (clearPreparedStatement.executeUpdate() == 0) throw new SQLException();
            return true;
        } catch (SQLException exception) {
            exception.printStackTrace();
            return false;
        } finally {
            dataBaseManipulator.closePreparedStatement(clearPreparedStatement);
        }
    }

    public boolean remove(long id){
        PreparedStatement deletePreparedStatement = null;
        try {
            deletePreparedStatement = dataBaseManipulator.getPreparedStatement(DELETE, false);
            deletePreparedStatement.setLong(1, id);
            if (deletePreparedStatement.executeUpdate() == 0) throw new SQLException();
            return true;
        } catch (SQLException exception) {
            exception.printStackTrace();
            return false;
        } finally {
            dataBaseManipulator.closePreparedStatement(deletePreparedStatement);
        }
    }

    public boolean update(long id, LabWork labwork){
        PreparedStatement updatePreparedStatement = null;
        try {
            updatePreparedStatement = dataBaseManipulator.getPreparedStatement(UPDATE_TABLE, false);
            updatePreparedStatement.setString(1, labwork.getName());
            updatePreparedStatement.setFloat(2, labwork.getCoordinates().getX());
            updatePreparedStatement.setDouble(3, labwork.getCoordinates().getY());
            updatePreparedStatement.setLong(4, labwork.getMinimalPoint());
            updatePreparedStatement.setLong(5, labwork.getMaximumPoint());
            updatePreparedStatement.setString(6, String.valueOf(labwork.getDifficulty()));
            updatePreparedStatement.setString(7, labwork.getDiscipline().getName());
            updatePreparedStatement.setInt(8, labwork.getDiscipline().getPracticeHours());
            updatePreparedStatement.setInt(9, labwork.getDiscipline().getSelfStudyHours());
            updatePreparedStatement.setString(10,  dataBaseManipulator.getUserName());
            updatePreparedStatement.setLong(11, id);

            if (updatePreparedStatement.executeUpdate() == 0) throw new SQLException();
            return true;
        } catch (SQLException exception) {
            exception.printStackTrace();
            return false;
        } finally {
            dataBaseManipulator.closePreparedStatement(updatePreparedStatement);
        }

    }

    public List<Long> getIds(){
        ids = new ArrayList<>();
        PreparedStatement selectClearIdsPreparedStatement = null;
        try {
            selectClearIdsPreparedStatement = dataBaseManipulator.getPreparedStatement(SELECT_CLEAR_IDS, false);
            selectClearIdsPreparedStatement.setString(1, dataBaseManipulator.getUserName());
            ResultSet result = selectClearIdsPreparedStatement.executeQuery();
            while (result.next()){
                ids.add(result.getLong("id"));
            }
            return ids;
        } catch (SQLException exception) {
            exception.printStackTrace();
            return ids;
        } finally {
            dataBaseManipulator.closePreparedStatement(selectClearIdsPreparedStatement);
        }
    }
}
