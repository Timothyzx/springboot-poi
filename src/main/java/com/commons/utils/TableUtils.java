package com.commons.utils;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.jdbc.support.rowset.SqlRowSetMetaData;

/**
 * 数据
 * Created by Administrator on 2018/6/9.
 */
public class TableUtils {
    /**
     * 使用jdbcTemplate 查询表的列名和类型
     * @param jdbcTemplate
     * @param tableName 表名
     * @return
     */
    public static Column columnTypes(JdbcTemplate jdbcTemplate, String tableName){
        //获取(列名和)第一行数据
        String sql = "select * from "+ tableName +" limit 1";
        //jdbc执行
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql);
        //获取表结构
        /**
         *
         * getMetaData().getTableName(1)  就可以返回表名
         * getMetaData().getColumnCount() 字段数
         * getMetaData().getColumnName(i) 字段名
         * getMetaData().getColumnType(i) 字段类型
         *
         */
        SqlRowSetMetaData metaData = rowSet.getMetaData();
        //声明数组(存放列名)
        String[] coloumnName = new String[metaData.getColumnCount()];
        //声明数组(存放列名数)
        int[] coloumnType = new int[metaData.getColumnCount()];
        //存值
        for (int i = 1; i <= metaData.getColumnCount(); i++) {
            //Map<String,String> fieldMap = new HashMap<String,String>();
            //fieldMap.put("ColumnName", metaData.getColumnName(i));
            //fieldMap.put("ColumnType", String.valueOf(metaData.getColumnType(i)));
            //ColumnTypeName=VARCHAR
            //fieldMap.put("ColumnTypeName", metaData.getColumnTypeName(i));
            //ColumnClassName=java.lang.String
            //fieldMap.put("ColumnClassName", metaData.getColumnClassName(i));
            //Precision=225
            //fieldMap.put("Precision", String.valueOf(metaData.getPrecision(i)));
            //System.out.println(fieldMap);
            coloumnName[i-1] = metaData.getColumnName(i);
            coloumnType[i-1] = metaData.getColumnType(i);
        }
        /*
            使用RowCountCallbackHandler 对于空表查出来的数据是null，不使用此方式
            RowCountCallbackHandler rcch = new RowCountCallbackHandler();
            jdbcTemplate.query(sql, rcch);
            String[] coloumnName = rcch.getColumnNames();
            int[] coloumnType = rcch.getColumnTypes();
            for (int i=0;i<coloumnName.length;i++){
                System.out.println(coloumnName[i]+","+coloumnType[i]);
            }
        */
        return new Column(coloumnName,coloumnType);
    }
}
