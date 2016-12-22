package com.github.mgramin.sqlboot.script.aggregators;

import com.github.mgramin.sqlboot.exceptions.SqlBootException;
import com.github.mgramin.sqlboot.model.DBSchemaObject;

import java.util.List;

/**
 * Created by mgramin on 17.12.2016.
 */
public interface IAggregator {

    byte[] aggregate(List<DBSchemaObject> objects) throws SqlBootException;

}
