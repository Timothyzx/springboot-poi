package com.timothy.controller;

import com.commons.utils.UploadException;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.timothy.server.PoiServer;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @author: tzx
 * @ Description:
 * @ Date: Created in 10:40 2018/6/13
 */

@RestController
public class PoiController {

    @Autowired
    PoiServer poiServer;

    /**
     * POI对于数据库表格的导出
     * 参数 ： tableName 真实表名
     *         showName  展示表名
     * @return
     */
    @GetMapping("/download")
    public void download(HttpServletResponse response,
                           @RequestParam(value = "tableName",required = true) String tableName,
                           @RequestParam(value = "showName",required = true) String showName){
        //输出流
        OutputStream out = null;
        try {
            //获取表数据
            SXSSFWorkbook wb = this.poiServer.exportDataByExcel(tableName);
            if (wb!=null){
                //创建Excle文件
                response.setHeader("Content-Disposition", "attachment;filename="
                                   +new String((showName+".xlsx").getBytes("utf-8"),"iso8859-1"));
                response.setContentType("application/force-download;charset=UTF-8");
                out=response.getOutputStream();
                wb.write(out);
                out.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (out != null){
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }

    }

    /**
     * POI对于Excel的导入
     * @param tableName 数据库表名
     * @param table     excel文件
     */
    @PostMapping("/upload")
    public void upload(@RequestParam(value = "tableName",required = false) String tableName,
                       @RequestParam(value = "table",required = true)MultipartFile table){
        try {
            this.poiServer.addDataByExcel(table,tableName);
            return ;
        } catch (UploadException e) {
            e.printStackTrace();
            return ;
        } catch (Exception e){
            e.printStackTrace();
            return ;
        }
    }

}
