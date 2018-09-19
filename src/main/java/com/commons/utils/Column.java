package com.commons.utils;

/**
 * 封装表的列名和列的类型
 * Created by Administrator on 2018/6/9.
 */
public class Column {
    /**
     * 列名数组
     */
    private String[] coloumnNames;
    /**
     * 列的类型数组
     */
    private int[] coloumnTypes;

    public Column() {
    }

    public Column(String[] coloumnNames, int[] coloumnTypes) {
        this.coloumnNames = coloumnNames;
        this.coloumnTypes = coloumnTypes;
    }

    public String[] getColoumnNames() {
        return coloumnNames;
    }

    public void setColoumnNames(String[] coloumnNames) {
        this.coloumnNames = coloumnNames;
    }

    public int[] getColoumnTypes() {
        return coloumnTypes;
    }

    public void setColoumnTypes(int[] coloumnTypes) {
        this.coloumnTypes = coloumnTypes;
    }
}
