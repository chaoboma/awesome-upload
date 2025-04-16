package com.application.utils.excel;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.fastjson.JSONObject;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.*;

public class ExcelDynamicExport {
    /**
     * 列表
     */
    public static void dynamicExport(HttpServletResponse response,
                                     LinkedHashMap<String,DynamicExcelData> nameMap,
                                     List<Map<String, Object>> list,
                                     String sheetName) throws IOException {
        if(CollUtil.isEmpty(list)){
            return;
        }
        if(nameMap==null){
            throw new RuntimeException("请填写好映射表数据");
        }

        int size = list.size();
        List<List<String>> dataList = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            dataList.add(new ArrayList<>());
        }

        //获取表头
        ArrayList<List<String>> head = new ArrayList<>();
        for (Map.Entry<String, DynamicExcelData> titleMap : nameMap.entrySet()) {
            DynamicExcelData data = titleMap.getValue();
            head.add(Collections.singletonList(data.getName()));
        }

        //数据重组
        for (int i = 0; i < list.size(); i++) {
            Map<String, Object> map = list.get(i);
            List<String> columns = dataList.get(i);
            for (Map.Entry<String, DynamicExcelData> sortNameEntry : nameMap.entrySet()) {
                String key = sortNameEntry.getKey();
                Object value = map.get(key);
                columns.add(value!=null?String.valueOf(value):sortNameEntry.getValue().getDefaultValue());
            }

        }

        response.setHeader("Access-Control-Expose-Headers", "Content-Disposition");
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");

        EasyExcel.write(response.getOutputStream()).head(head)
                .sheet(sheetName).doWrite(dataList);
    }
    /**
     * 列表
     */
    public static void dynamicExport2(HttpServletResponse response,
                                     LinkedHashMap<String,DynamicExcelData> nameMap,
                                     List<JSONObject> list,
                                     String sheetName,String fileName) throws IOException {
        if(CollUtil.isEmpty(list)){
            return;
        }
        if(nameMap==null){
            throw new RuntimeException("请填写好映射表数据");
        }

        int size = list.size();
        List<List<String>> dataList = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            dataList.add(new ArrayList<>());
        }

        //获取表头
        ArrayList<List<String>> head = new ArrayList<>();
        for (Map.Entry<String, DynamicExcelData> titleMap : nameMap.entrySet()) {
            DynamicExcelData data = titleMap.getValue();
            head.add(Collections.singletonList(data.getName()));
        }

        //数据重组
        for (int i = 0; i < list.size(); i++) {
            JSONObject map = list.get(i);
            List<String> columns = dataList.get(i);
            for (Map.Entry<String, DynamicExcelData> sortNameEntry : nameMap.entrySet()) {
                String key = sortNameEntry.getKey();
                Object value = map.get(key);
                columns.add(value!=null?String.valueOf(value):sortNameEntry.getValue().getDefaultValue());
            }

        }

        response.setHeader("Access-Control-Expose-Headers", "Content-Disposition");
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");

        EasyExcel.write(response.getOutputStream()).head(head)
                .sheet(sheetName).doWrite(dataList);
    }
    @PostMapping("/export")
    public void export(HttpServletResponse response) throws IOException {

        //模拟数据
        //一般动态数据使用的是List，然后内部使用Map进行数据的接受
        List<Map<String, Object>> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            HashMap<String, Object> map = new HashMap<>();
            for (int j = 0; j < 5; j++) {
                map.put("title"+j,i+","+j);
            }
            //这个用于测试值如果为null时，能否进行默认值填充
            map.put("title5",null);
            list.add(map);
        }


        //使用LinkedHashMap进行表头字段映射
        LinkedHashMap<String, DynamicExcelData> nameMap = new LinkedHashMap<>();
        nameMap.put("title1",new DynamicExcelData("年龄","0"));
        nameMap.put("title0",new DynamicExcelData("姓名","0"));
        nameMap.put("title2",new DynamicExcelData("职业","0"));
        nameMap.put("title3",new DynamicExcelData("爱好","0"));
        nameMap.put("title4",new DynamicExcelData("小名","0"));
        nameMap.put("title5",new DynamicExcelData("空白字段","0"));

        //调用方法,方法已在步骤3进行介绍
        ExcelDynamicExport.dynamicExport(response,nameMap, list,"模板");

    }
}
