package com.commons.utils;

import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.sql.Types; //类型对应数值
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 文件操作工具类
 * Created by Administrator on 2018/6/8.
 */
public class FileUtils {
    /**
     * 是否是指定的允许上传Excel的格式
     * @param fileName  文件名
     * @return
     */
    public static boolean checkExcelAllow(String fileName){
        fileName = fileName.toLowerCase();
        String allowTYpe = "xls,xlsx";
        if (!fileName.trim().equals("") && fileName.length() > 0) {
            String ex = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length());
            return allowTYpe.toUpperCase().indexOf(ex.toUpperCase()) >= 0;
        } else {
            return false;
        }
    }

    /**
     * 读取xls格式的文件
     * @param column
     * @param inputStream
     * @return
     * @throws IOException
     * @throws UploadException
     */
    public static List<HashMap<String,Object>> readForHssf(Column column, InputStream inputStream) throws IOException,UploadException{
        POIFSFileSystem fs = new POIFSFileSystem(inputStream);
        HSSFWorkbook wb = new HSSFWorkbook(fs);
        List<HashMap<String,Object>> datas = new ArrayList<>();
        read(wb,column,datas);
        return datas;
    }

    /**
     * 读取xlsx个的文件
     * @param column
     * @param inputStream
     * @return
     * @throws UploadException
     * @throws IOException
     */
    public static List<HashMap<String,Object>> readForXssf(Column column, InputStream inputStream) throws UploadException,IOException{
        XSSFWorkbook wb = new XSSFWorkbook(inputStream);
        List<HashMap<String,Object>> datas = new ArrayList<>();
        read(wb,column,datas);
        return datas;
    }

    /**
     * 真正读取Excel的方法
     * @param wb
     * @param column
     * @param datas
     * @throws IOException
     * @throws UploadException
     */
    public static void read(Workbook wb,Column column,List<HashMap<String,Object>> datas) throws IOException,UploadException{
        Sheet sheet = wb.getSheetAt(0);
        if (sheet != null){
            List<String> columnNames = new ArrayList<>();
            List<Integer> columnTypes = new ArrayList<>();
            for (int i = 0;i<column.getColoumnNames().length;i++){
                if (!"id".equalsIgnoreCase(column.getColoumnNames()[i])){
                    columnNames.add(column.getColoumnNames()[i]);
                    columnTypes.add(column.getColoumnTypes()[i]);
                }
            }
            Row row = sheet.getRow(sheet.getFirstRowNum());
            if (columnNames.size() !=row.getLastCellNum() ){
                throw new UploadException("结构与数据表不符");
            }
            Cell cell = null;
            boolean flag = false;
            int firstRowCellSize = row.getLastCellNum();
            for (int i =0;i<firstRowCellSize;i++){
                cell = row.getCell(i);
                if (cell==null){
                    continue;
                }
                switch (cell.getCellTypeEnum()){
                    case STRING:
                        flag = true;
                }
                if (!flag || !columnNames.get(i).equals(cell.getStringCellValue())){
                    throw new UploadException("结构与数据表不符");
                }
            }
            HashMap<String,Object> data = null;
            for (int i = sheet.getFirstRowNum()+1;i<=sheet.getLastRowNum();i++){
                row = sheet.getRow(i);
                if (row == null){
                    continue;
                }
                data = new HashMap<>();
                //使用firstRowCellSize是避免当前行最后一列未编辑过则读不到 ，这样则可以读到并设置为NULL
                for (int j=row.getFirstCellNum();j<firstRowCellSize;j++){
                    cell = row.getCell(j);
                    if (cell == null){
                        data.put(columnNames.get(j),null);
                        continue;
                    }
                    switch (cell.getCellTypeEnum()){
                        case STRING:
                            data.put(columnNames.get(j),cell.getStringCellValue());
                            break;
                        case NUMERIC:
                            if (HSSFDateUtil.isCellDateFormatted(cell)) {// 日期类型
                                Date date = cell.getDateCellValue();
                                if (columnTypes.get(j).equals(12)){
                                    data.put(columnNames.get(j),new SimpleDateFormat("yyyy-MM-dd").format(date));
                                }else {
                                    data.put(columnNames.get(j),date);
                                }
                            } else {// 数值类型
                                data.put(columnNames.get(j),cell.getNumericCellValue());
                            }
                            break;
                        case _NONE:
                            data.put(columnNames.get(j),null);
                            break;
                        case BLANK:
                            data.put(columnNames.get(j),null);
                            break;
                        case BOOLEAN:
                            data.put(columnNames.get(j),cell.getBooleanCellValue());
                            break;
                        case ERROR:
                            data.put(columnNames.get(j),null);
                            break;
                        default:
                            data.put(columnNames.get(j),null);
                    }
                }
                datas.add(data);
            }
        }
    }
    /**
     * 导出Excel
     * @param column 列集合对象
     * @param datas 数据
     * @return
     */
    public static SXSSFWorkbook exportExcel( Column column, List<HashMap<String,Object>> datas){
        XSSFWorkbook xssfWorkbook = new XSSFWorkbook();
        //导出excel时1000行flush
        SXSSFWorkbook wb = new SXSSFWorkbook(xssfWorkbook,1000);
        Sheet sheet = wb.createSheet();
        //创建第一行
        Row row = sheet.createRow(0);
        CreationHelper creationHelper = wb.getCreationHelper();
        CellStyle cellStyle = wb.createCellStyle(); //列类型
        List<String> columnNames = new ArrayList<>();
        List<Integer> columnTypes = new ArrayList<>();
        //祛除id后的列名和类型分别放入集合
        for (int i = 0;i<column.getColoumnNames().length;i++){
            //去掉主键列(对比不考虑大小写)
            if (!"id".equalsIgnoreCase(column.getColoumnNames()[i])){
                columnNames.add(column.getColoumnNames()[i]);
                columnTypes.add(column.getColoumnTypes()[i]);
            }
        }
        //第一行是列标题
        for (int i=0;i<columnNames.size();i++){
            row.createCell(i).setCellValue(columnNames.get(i));
        }
        if (datas.size()>0){
            //装填数据
            Cell cell = null; //列
            for (int i=0;i<datas.size();i++){
                row = sheet.createRow(i+1); //行
                Map<String,Object> data = datas.get(i);
                for (int j=0;j<columnTypes.size();j++){
                    cell = row.createCell(j);
                    setCellValue(cell,cellStyle,creationHelper,data.get(columnNames.get(j)),columnTypes.get(j));
                }
            }
        }
        return wb;
    }
    /**
     * 判断值的类型并装进cell内
     * 取自java.sql.Types类
     * VARCHAR = 12;
     * int BIT = -7;
     * SMALLINT = 5;
     * INTEGER = 4;
     * BIGINT = -5;
     * DECIMAL = 3;
     * CHAR = 1;
     * LONGVARCHAR = -1;
     * DATE = 91;
     * TIMESTAMP = 93;
     * @param cell 单元格
     * @param cellStyle 单元格样式
     * @param creationHelper
     * @param value 值
     * @param sqltype 列类型
     */
    public static void setCellValue(Cell cell, CellStyle cellStyle, CreationHelper creationHelper, Object value, int sqltype){
        switch (sqltype){
            case 12:
                cell.setCellValue((String) value);
                break;
            case 4:
                cell.setCellValue((Integer)value);
                break;
            case -5:
                cell.setCellValue((Long)value);
                break;
            case 3:
                cell.setCellValue(((BigDecimal)value).toString());
                break;
            case 91:
                cell.setCellValue((Date)value);
                cellStyle.setDataFormat(creationHelper.createDataFormat().getFormat("yyyy-mm-dd"));
                cell.setCellStyle(cellStyle);
                break;
            case 93:
                cell.setCellValue((Timestamp)value);
                cellStyle.setDataFormat(creationHelper.createDataFormat().getFormat("yyyy-mm-dd hh:mm:ss"));
                cell.setCellStyle(cellStyle);
                break;
            case -7:
                cell.setCellValue((Boolean)value);
                break;
            case 5:
                cell.setCellValue((Integer)value);
                break;
            case 1:
                cell.setCellValue((String) value);
                break;
            case -1:
                cell.setCellValue((String) value);
                break;
            default:
                cell.setCellValue((String)value);
        }
    }
}
