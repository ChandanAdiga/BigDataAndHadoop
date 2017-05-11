package org.chandan.hadoop.hbase.sample;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.MasterNotRunningException;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.ZooKeeperConnectionException;
import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;

@SuppressWarnings("resource")
public class MyHBaseSample {

    private static final String TABLE_NAME = "employee_table";
    private static final String ROW_1 = "row_1";
    private static final String ROW_2 = "row_2";
    private static final String COLUMN_1 = "official";
    private static final String COLUMN_1_KEY = "name";
    private static final String COLUMN_2 = "personal";
    private static final String COLUMN_2_KEY = "nick_name";
    
    private static void performTableCreation(Configuration conf)
            throws MasterNotRunningException, ZooKeeperConnectionException, IOException {
        System.out.println("-------------------------------performTableCreation() - START-------------------------------");
        HBaseAdmin admin = new HBaseAdmin(conf);
        
        HTableDescriptor tableDescriptor = new HTableDescriptor(TableName.valueOf(TABLE_NAME));
        
        tableDescriptor.addFamily(new HColumnDescriptor(COLUMN_1));
        tableDescriptor.addFamily(new HColumnDescriptor(COLUMN_2));
        
        admin.createTable(tableDescriptor);
        System.out.println("-------------------------------performTableCreation() - END-------------------------------");
    }

    private static void performDataListTables(Configuration conf)
            throws MasterNotRunningException, ZooKeeperConnectionException, IOException {
        System.out.println("-------------------------------performDataListTables() - START-------------------------------");
        
        HBaseAdmin admin = new HBaseAdmin(conf);

        HTableDescriptor[] tablesDescriptors = admin.listTables();
        System.out.println((tablesDescriptors == null ? "No" : ("" + tablesDescriptors.length)) + " Tables found!");
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~");
        for(HTableDescriptor tableDescriptor : tablesDescriptors) {
            System.out.println(tableDescriptor.getNameAsString());
         }
        System.out.println("-------------------------------performDataListTables() - END-------------------------------");
    }

    private static void performDataInsertion(Configuration conf)
            throws MasterNotRunningException, ZooKeeperConnectionException, IOException {
        System.out.println("-------------------------------performDataInsertion() - START-------------------------------");
        HTable table = new HTable(conf, TABLE_NAME);
        
        Put setter = new Put(ROW_1.getBytes());
        setter.add(COLUMN_1.getBytes(),COLUMN_1_KEY.getBytes(),"Chandan Adiga".getBytes());
        setter.add(COLUMN_2.getBytes(),COLUMN_2_KEY.getBytes(),"Chandu".getBytes());
        table.put(setter);
        System.out.println("Inserted row_1 successfully!");
        Put setter2 = new Put(ROW_2.getBytes());
        setter2.add(COLUMN_1.getBytes(),COLUMN_1_KEY.getBytes(),"Anthony GP".getBytes());
        setter2.add(COLUMN_2.getBytes(),COLUMN_2_KEY.getBytes(),"Antz".getBytes());
        table.put(setter2);
        System.out.println("Inserted row_2 successfully!");
        
        table.close();
        System.out.println("-------------------------------performDataInsertion() - END-------------------------------");
    }

    private static void performRowDump(Configuration conf)
            throws MasterNotRunningException, ZooKeeperConnectionException, IOException {
        System.out.println("-------------------------------performRowDump() - START-------------------------------");
        HTable table = new HTable(conf, TABLE_NAME);
        
        Get getter = new Get(ROW_1.getBytes());
        Result result = table.get(getter);
        final String rowOfficial = Bytes.toString(result.getValue(COLUMN_1.getBytes(), COLUMN_1_KEY.getBytes()));
        final String rowPersonal = Bytes.toString(result.getValue(COLUMN_2.getBytes(), COLUMN_2_KEY.getBytes()));
        System.out.println("row_1 dump: " + rowOfficial +" , " + rowPersonal);
        
        Get getter2 = new Get(ROW_2.getBytes());
        Result result2 = table.get(getter2);
        final String rowOfficial2 = Bytes.toString(result2.getValue(COLUMN_1.getBytes(), COLUMN_1_KEY.getBytes()));
        final String rowPersonal2 = Bytes.toString(result2.getValue(COLUMN_2.getBytes(), COLUMN_2_KEY.getBytes()));
        System.out.println("row_2 dump: " + rowOfficial2 +" , " + rowPersonal2);

        table.close();
        System.out.println("-------------------------------performRowDump() - END-------------------------------");
    }

    private static void performRowDelete(Configuration conf)
            throws MasterNotRunningException, ZooKeeperConnectionException, IOException {
        System.out.println("-------------------------------performRowDelete() - START-------------------------------");
        HTable table = new HTable(conf, TABLE_NAME);
        try {
            Delete deleteRow2 = new Delete(ROW_2.getBytes());
            deleteRow2.deleteColumn(COLUMN_2.getBytes(), COLUMN_2_KEY.getBytes());
            deleteRow2.deleteFamily(COLUMN_1.getBytes());
            table.delete(deleteRow2);
            System.out.println("row_2 deleted successfully!");
        }catch(Exception e) {
            System.out.println(e.getMessage());
        }
        table.close();
        System.out.println("-------------------------------performRowDelete() - END-------------------------------");
    }

    public static void main(String[] args) throws MasterNotRunningException, ZooKeeperConnectionException, IOException {
        Configuration conf = HBaseConfiguration.create();

//        performTableCreation(conf);

//        performDataInsertion(conf);

//        performDataListTables(conf);

//        performRowDump(conf);

//        performRowDelete(conf);

        performRowDump(conf);
    }
}
