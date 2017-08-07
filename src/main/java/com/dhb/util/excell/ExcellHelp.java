package com.dhb.util.excell;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;

public class ExcellHelp {
	
	public static void writeExcell(String excellPath,List<List<String>> datas) throws IOException{
		if(excellPath.endsWith(".xlsx")){
			FileOutputStream output = new FileOutputStream(new File(excellPath));  //读取的文件路径   
	        XSSFWorkbook wb = new XSSFWorkbook();//(new BufferedInputStream(output));         
	        XSSFSheet sheet = wb.createSheet("0");  
	        wb.setSheetName(0, "data");          
	            for(int i=0;i<datas.size();i++){  
	                XSSFRow row = sheet.createRow(i);           
	                List<String> s = datas.get(i);                  
	                for(int cols=0;cols<s.size();cols++){  
	                    XSSFCell cell = row.createCell(cols);                     
	                    cell.setCellType(XSSFCell.CELL_TYPE_STRING);//文本格式  
	                    cell.setCellValue(s.get(cols));//写入内容  
	                }  
	            }              
	            
	        wb.write(output);  
	        output.close();   
	        System.out.println("-------【完成写入】-------");
		}
	}
	public static List<List<String>> readExcell(String excellPath)
			throws IOException {
		if (Strings.isNullOrEmpty(excellPath)) {
			return null;
		}
		if (excellPath.endsWith(".xls")) {
			return readXls(excellPath);
		}
		if(excellPath.endsWith(".xlsx")){
			readXlsx(excellPath);
		}
		return null;
	}
	
	public static List<List<String>> readExcell(String excellName,InputStream input) throws IOException{
		if (excellName.endsWith(".xls")) {
			return readXls(input);
		}
		if(excellName.endsWith(".xlsx")){
			return readXlsx(input);
		}
		return null;
	}
	private static List<List<String>> readXls(InputStream is) throws IOException{   
	    HSSFWorkbook hssfWorkbook = new HSSFWorkbook( is);   
	      
	    // 循环工作表Sheet  
	  //  for(int numSheet = 0; numSheet < hssfWorkbook.getNumberOfSheets(); numSheet++){  
	      HSSFSheet hssfSheet = hssfWorkbook.getSheetAt( 0);  
	      if(hssfSheet == null){  
	        return null;  
	      }  
	      List<List<String>> lists = Lists.newArrayList();
	      // 循环行Row   
	      for(int rowNum = 0; rowNum <= hssfSheet.getLastRowNum(); rowNum++){  
	        HSSFRow hssfRow = hssfSheet.getRow( rowNum);  
	        if(hssfRow == null){  
	          continue;  
	        }  
	        List<String> oneRow = Lists.newArrayList();
	        lists.add(oneRow);
	        // 循环列Cell    
	        for(int cellNum = 0; cellNum <= hssfRow.getLastCellNum(); cellNum++){  
	          HSSFCell hssfCell = hssfRow.getCell( cellNum);  
	          if(hssfCell == null){  
	            continue;  
	          }  
	          String cellVal = getXlsValue( hssfCell);
	          oneRow.add(cellVal);
	          System.out.print("    " + cellVal);  
	        }  
	        System.out.println();  
	      }  
	      return lists;
	    } 
	private static List<List<String>> readXls(String excellPath) throws IOException{  
	    InputStream is = new FileInputStream(excellPath);  
	     return readXls(is);
	    }  
	//  }  
	    
	  @SuppressWarnings("static-access")  
	  private static String getXlsValue(HSSFCell hssfCell){  
	    if(hssfCell.getCellType() == hssfCell.CELL_TYPE_BOOLEAN){  
	      return String.valueOf( hssfCell.getBooleanCellValue());  
	    }else if(hssfCell.getCellType() == hssfCell.CELL_TYPE_NUMERIC){  
	      return String.valueOf( hssfCell.getNumericCellValue());  
	    }else{  
	      return String.valueOf(hssfCell.getStringCellValue());  
	    }  
	  }  
	  private static List<List<String>> readXlsx(InputStream is) throws IOException {
			XSSFWorkbook xssfWorkbook = new XSSFWorkbook(is);

			// 循环工作表Sheet
			// for(int numSheet = 0; numSheet < xssfWorkbook.getNumberOfSheets();
			// numSheet++){
			XSSFSheet xssfSheet = xssfWorkbook.getSheetAt(0);

			List<List<String>> lists = Lists.newArrayList();
			// 循环行Row
			for (int rowNum = 0; rowNum <= xssfSheet.getLastRowNum(); rowNum++) {
				XSSFRow xssfRow = xssfSheet.getRow(rowNum);
				if (xssfRow == null) {
					continue;
				}
				List<String> oneRow = Lists.newArrayList();
				lists.add(oneRow);
				// 循环列Cell
				for (int cellNum = 0; cellNum <= xssfRow.getLastCellNum(); cellNum++) {
					XSSFCell xssfCell = xssfRow.getCell(cellNum);
					if (xssfCell == null) {
						continue;
					}
					String cellVal = getXlsxValue(xssfCell);
					oneRow.add(cellVal);
					System.out.print("   " + cellVal);
				}
				System.out.println();
			}
			return lists;
		}  
	  private static List<List<String>> readXlsx(String excellPath) throws IOException {
		 InputStream is = new FileInputStream(excellPath); 
		return readXlsx(is);
	}  
		 // }  
		    
		  @SuppressWarnings("static-access")  
		  private static String getXlsxValue(XSSFCell xssfCell){  
		    if(xssfCell.getCellType() == xssfCell.CELL_TYPE_BOOLEAN){  
		      return String.valueOf( xssfCell.getBooleanCellValue());  
		    }else if(xssfCell.getCellType() == xssfCell.CELL_TYPE_NUMERIC){  
		      return String.valueOf( xssfCell.getNumericCellValue());  
		    }else{  
		      return String.valueOf( xssfCell.getStringCellValue());  
		    }  
		  }  
		    
	
}
