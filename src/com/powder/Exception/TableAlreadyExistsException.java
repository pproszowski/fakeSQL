package com.powder.Exception;

public class TableAlreadyExistsException extends Exception {
    private String tableName;
    private String databaseName;
    public TableAlreadyExistsException(String _tableName, String _databaseName){
       tableName = _tableName;
       databaseName = _databaseName;
    }
    @Override
    public String getMessage() {
        return "Table already exists!";
    }
}
