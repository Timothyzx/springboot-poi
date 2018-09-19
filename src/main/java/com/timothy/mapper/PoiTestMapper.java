package com.timothy.mapper;


import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;

/**
 * @author: tzx
 * @ Description:
 * @ Date: Created in 11:25 2018/6/13
 */
@Repository
public  interface PoiTestMapper {
    /**
     *
     * @param dbName
     * @param tableName
     * @return
     */
    Integer isTable(@Param("dbName")String dbName, @Param("tableName")String tableName);

    /**
     *
     * @param tableName
     * @return
     */
    List<HashMap<String,Object>> getAllDate(@Param("tableName")String tableName);
}
