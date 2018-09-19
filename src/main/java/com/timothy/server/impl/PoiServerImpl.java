package com.timothy.server.impl;

import com.commons.utils.Column;
import com.commons.utils.FileUtils;
import com.commons.utils.TableUtils;
import com.commons.utils.UploadException;
import com.timothy.mapper.PoiTestMapper;
import com.timothy.server.PoiServer;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.stereotype.Service;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @ author: tzx
 * @ Description:
 * @ Date: Created in 10:42 2018/6/13
 */
@Service
public class PoiServerImpl implements PoiServer {

    @Resource
    private JdbcTemplate jdbcTemplate;

    @Resource
    private PoiTestMapper poiTestMapper;

    @Override
    public SXSSFWorkbook exportDataByExcel(String tableName) {
        //定义数据库名称
        String  dbName = "test";
        //查询需要导出的表属否存在此数据库中
        Integer isTrue = poiTestMapper.isTable(dbName,tableName);
        //存在次表则获取所有数据
        if(isTrue == 1){
            List<HashMap<String,Object>> datas = this.poiTestMapper.getAllDate(tableName);
            //利用JDBC查询出表结构(类型),并赋值返回
            Column column = TableUtils.columnTypes(jdbcTemplate,tableName);
            //利用数据和类型写入excel中并返回
            return FileUtils.exportExcel(column,datas);
        }
        return null;
    }

    /**
     * 导入前提有表存在
     * 根据上传的文件批量添加数据(导入)
     * @param file
     * @param tableName
     * @throws UploadException
     * @throws IOException
     */
    @Override
    public void addDataByExcel(MultipartFile file, String tableName) throws UploadException, IOException {
        //是否存在表名
        /*if(this.poiTestMapper.checkSheetExists("test",tableName)==0){
            throw new UploadException("数据表不存在了");
        }*/
        //分析此表的数据结构(返回列名称和类型)
        Column column = TableUtils.columnTypes(jdbcTemplate,tableName);
        //获取上传文件名
        String fileName = file.getOriginalFilename();
        //截取名称后缀
        String ex = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length());
        //判断上传文件的类型
        List<HashMap<String,Object>> datas = null;
        if ("xls".equals(ex)){
            //2003年版本使用 HSSF解析
            datas = FileUtils.readForHssf(column,file.getInputStream());
        }
        if ("xlsx".equals(ex)){
            //2007以后使用 XSSF解析
            datas =  FileUtils.readForXssf(column,file.getInputStream());
        }
        String columns = "";
        String values = "";
        List<String> columnNames = new ArrayList<>();
        for (String columnName : column.getColoumnNames()){
            if (!"id".equalsIgnoreCase(columnName)){
                columnNames.add(columnName);
                columns = columns+ columnName+",";
                values += "?,";
            }
        }
        /**
         * 使用jdbc批量插入
         */
        String sql="insert into "+tableName+" ("+columns.substring(0,columns.length()-1)+")" +
                "  values("+values.substring(0,values.length()-1)+")";
        final List<HashMap<String,Object>> addList = datas;
        final List<String> addColumns = columnNames;
        jdbcTemplate.batchUpdate(sql,new BatchPreparedStatementSetter() {
            @Override
            public int getBatchSize() {
                return addList.size();
            }
            @Override
            public void setValues(PreparedStatement ps, int i)
                    throws SQLException {
                for (int j=0;j<addColumns.size();j++){
                    ps.setObject(j+1, addList.get(i).get(addColumns.get(j)));
                }
            }
        });

    }
}

