package com.application.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * @PACKAGE_NAME: com.spike.util.controller
 * @USER: spike
 * @DATE: 2023/4/27 15:46
 * @PROJECT_NAME: Springcolud_Spike
 */
@Slf4j
public class ExcelUtil {

    /**
     * 判断excel文件类型正则表达式
     */
    private static final String IS_EXCEL = "^.+\\.(?i)((xls)|(xlsx))$";

    /**
     * 判断是否为 xls后缀版本的excel
     */
    private static final String IS_XLS_EXCEL = "^.+\\.(?i)(xls)$";

    /**
     * 存放excel模板的目录
     */
    private static final String CLASS_PATH = "templates/excel/";

    /**
     * 动态表头
     */
    public static final String TABLE_NAME_HEAD = "table_name_head";

    public static final String LABEL = "label";

    public static final String KEY = "key";

    public static void exportExcel(String execlTempleName, Map<String, Object> map, HttpServletResponse response) throws Exception {
        ExcelTemplate excelTemplate = ExcelTemplate.init(execlTempleName + ".xlsx");
        Workbook workbook = excelTemplate.getWorkbook();
        Sheet sheetAt = workbook.getSheetAt(0);
        //获取动态表头
        if (map.get(TABLE_NAME_HEAD) == null || !(map.get(TABLE_NAME_HEAD) instanceof List)) {
            log.info("动态标题格式异常，导出失败！");
            return;
        }
        //动态表头
        List<Map<String, Object>> tableHead = (List<Map<String, Object>>) map.get(TABLE_NAME_HEAD);
        //设置表头
        Row tempRow = sheetAt.createRow(0);
        int col = 0;
        for (Map<String, Object> tableHeadMap: tableHead) {
            String tableName = tableHeadMap.get(LABEL).toString();
            tempRow.createCell(col++).setCellValue(tableName);
        }
        //填充表内容
        int rowValue = 1;
        //创建数据行
        if (map.get("data") == null || !(map.get("data") instanceof List)) {
            log.info("数据格式异常，导出失败！");
            return;
        }
        List<Map> dataList = (List<Map>) map.get("data");
        Row row = sheetAt.createRow(rowValue);
        for (Map dataMap : dataList) {
            row = sheetAt.createRow(rowValue);
            col = 0;
            for (Map<String, Object> tableHeadMap : tableHead) {
                String fileName =  tableHeadMap.get(KEY).toString();
                String value = dataMap.get(fileName) != null ? dataMap.get(fileName).toString() : "";
                if(!value.isEmpty()&& Pattern.matches("[-]?[0-9]*[.]?[0-9]*",value)){
                    row.createCell(col).setCellType(CellType.NUMERIC);
                    double dobValue= Double.valueOf(value);
                    row.createCell(col++).setCellValue(dobValue);
                }else {
                    row.createCell(col).setCellType(CellType.STRING);
                    row.createCell(col++).setCellValue(value);
                }
            }
            rowValue = rowValue + 1;
        }
        String fileName = "DFlow" + System.currentTimeMillis();
        excelTemplate.export(response, fileName);
        return;
    }

    /**
     * 获取response输出流
     *
     * @param fileName 文件名
     * @param response 响应体
     * @return OutputStream
     */
    private static OutputStream getOutputStream(String fileName, HttpServletResponse response) throws Exception {
        try {
            fileName = URLEncoder.encode(fileName + ".xlsx", "UTF-8");
            response.setContentType("octets/stream");
            response.setHeader("Content-disposition", "attachment;filename=" + fileName + ";" + "filename*=utf-8''" + fileName);
            return response.getOutputStream();
        } catch (IOException e) {
            log.error("设置excel输出流异常！文件名：{}", fileName, e);
            throw e;
        }
    }

    /**
     * 构建 Workbook 对象
     *
     * @param templateName 模板文件名
     * @return Workbook
     */
    public static Workbook buildWorkbook(String templateName) throws Exception {
        Workbook workbook;
        try {
            if (templateName.matches(IS_EXCEL)) {
                // 得到文件输入流对象
                InputStream inputStream = ExcelUtil.class.getClassLoader().getResourceAsStream(CLASS_PATH + templateName);
                if (inputStream == null) {
                    throw new Exception("找不到excel模板文件");
                }

                boolean isXlsExcel = templateName.matches(IS_XLS_EXCEL);
                // 创建工作簿，并传递要读取的文件
                workbook = isXlsExcel ? new HSSFWorkbook(inputStream) : new XSSFWorkbook(inputStream);
                inputStream.close();
            } else {
                throw new Exception("文件格式不是xls或xlsx");
            }
            return workbook;
        } catch (Exception e) {
            log.info("获取Workbook对象异常！msg：{}", e.getMessage(), e);
            throw e;
        }

    }

    /**
     * excel 模板类
     */
    public static class ExcelTemplate {

        private Workbook workbook;

        /**
         * 通过文件名称读取模板文件并初始化ExcelTemplate对象
         *
         * @param fileName 模板文件名
         * @return ExcelTemplate
         */
        public static ExcelTemplate init(String fileName) throws Exception {
            ExcelTemplate excelTemplate = new ExcelTemplate();
            Workbook workbook = ExcelUtil.buildWorkbook(fileName);
            excelTemplate.setWorkbook(workbook);
            return excelTemplate;
        }

        /**
         * 导出excel
         *
         * @param response     响应体
         * @param newExcelName 自定义导出excel的文件名
         */
        public void export(HttpServletResponse response, String newExcelName) throws Exception {
            OutputStream out = null;
            try {
                out = getOutputStream(newExcelName, response);
                ByteArrayOutputStream ops = new ByteArrayOutputStream();
                workbook.write(ops);
                out.write(ops.toByteArray());
            } finally {

                try {
                    if (out != null) {
                        out.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }

        public void setWorkbook(Workbook workbook) {
            this.workbook = workbook;
        }

        public Workbook getWorkbook() {
            return workbook;
        }
    }
}
