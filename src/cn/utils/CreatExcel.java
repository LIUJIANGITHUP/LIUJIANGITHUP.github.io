package cn.utils;
/*
 *  创建excel并将数据存入excel，最后导出excel
 */

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

import cn.pojo.Information;

public class CreatExcel {
	//向已有表中追加数据
	public static void creat1(List<Information> dataFromExcel) throws Exception {
		FileInputStream fs=new FileInputStream("D:" + File.separator+"output.xls"); 
		//FileInputStream fs=new FileInputStream(System.getProperty("user.dir") + File.separator+"workorder.xls"); //获取d://test.xls  
        POIFSFileSystem ps=new POIFSFileSystem(fs);  //使用POI提供的方法得到excel的信息  
        HSSFWorkbook wb=new HSSFWorkbook(ps);    
        HSSFSheet sheet=wb.getSheetAt(0);  //获取到工作表，因为一个excel可能有多个工作表  
        HSSFRow row=sheet.getRow(0);  //获取第一行（excel中的行默认从0开始，所以这就是为什么，一个excel必须有字段列头），即，字段列头，便于赋值  
        //System.out.println(sheet.getLastRowNum()+" "+row.getLastCellNum());  //分别得到最后一行的行号，和一条记录的最后一个单元格  
        FileOutputStream out=new FileOutputStream("D:" + File.separator+"output.xls");
       // FileOutputStream out=new FileOutputStream(System.getProperty("user.dir") + File.separator+"output.xls");  //向d://test.xls中写数据  
        for (int i = 0; i < dataFromExcel.size(); i++) {
        	row=sheet.createRow((short)(sheet.getLastRowNum()+1)); //在现有行号后追加数据  
            Information user = dataFromExcel.get(i);
            //创建单元格设值
            row.createCell(0).setCellValue(user.getBUG_ID());
            row.createCell(1).setCellValue(user.getHEAD_PERSON());
            row.createCell(2).setCellValue(user.getREOPEN_NUMBER());
            row.createCell(3).setCellValue(user.getVERSION_NAME());
        }  
        out.flush();  
        wb.write(out);    
        out.close();    
	}
	//分类写入
	public static void creat2(Map<Integer, List<Information>> dataFromExcel) throws Exception {
		//FileInputStream fs=new FileInputStream("D:" + File.separator+"output.xls");  //获取d://test.xls 
		FileInputStream fs=new FileInputStream(System.getProperty("user.dir")+ File.separator+"output.xls"); 
        POIFSFileSystem ps=new POIFSFileSystem(fs);  //使用POI提供的方法得到excel的信息  
        HSSFWorkbook wb=new HSSFWorkbook(ps); 
        FileOutputStream out=null;
        for(int n=0;n<dataFromExcel.size();n++) {
        HSSFSheet sheet=wb.getSheetAt(n);  //获取到工作表，因为一个excel可能有多个工作表  
        HSSFRow row=sheet.getRow(0);  //获取第一行（excel中的行默认从0开始，所以这就是为什么，一个excel必须有字段列头），即，字段列头，便于赋值  
        System.out.println(sheet.getLastRowNum()+" "+row.getLastCellNum());  //分别得到最后一行的行号，和一条记录的最后一个单元格  
      
        //out=new FileOutputStream("D:" + File.separator+"output.xls");  //向d://test.xls中写数据  
       out=new FileOutputStream(System.getProperty("user.dir") + File.separator+"output.xls");
        for (int i = 0; i < dataFromExcel.get(n).size(); i++) {
        	row=sheet.createRow((short)(sheet.getLastRowNum()+1)); //在现有行号后追加数据  
        	Information user = dataFromExcel.get(n).get(i);
            //创建单元格设值
        	row.createCell(0).setCellValue(user.getBUG_ID());
            row.createCell(1).setCellValue(user.getHEAD_PERSON());
            row.createCell(2).setCellValue(user.getREOPEN_NUMBER());
            row.createCell(3).setCellValue(user.getVERSION_NAME());
        }  
        }
        out.flush();  
        wb.write(out);    
        out.close();    
	}

}
