package com.powder.Exception;

public class TableNotFoundException extends Throwable {
    private String databaseName;
    private String tableName;
    public TableNotFoundException(String _databaseName, String _tableName){
        databaseName = _databaseName;
        tableName = _tableName;
    }
    @Override
    public String getMessage() {
        return "Error: unable to find table " + tableName + " in database " + databaseName;
    }
}
