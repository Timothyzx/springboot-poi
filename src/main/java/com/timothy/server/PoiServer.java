package com.timothy.server;

import com.commons.utils.UploadException;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * @ author: tzx
 * @ Description:
 * @ Date: Created in 10:42 2018/6/13
 */
@Repository
public interface PoiServer {
    /**
     *
     * @param tableName
     * @return
     */
    SXSSFWorkbook exportDataByExcel(String tableName);


    void addDataByExcel(MultipartFile file, String tableName) throws UploadException, IOException;
}
