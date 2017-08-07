package com.dhb.util.excell;

  
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
  
public class ExcelReader {  
  Workbook wb = null;  
  List<String[]> dataList = new ArrayList<String[]>(100);  
  public ExcelReader(String path){  
    try {  
      InputStream inp = new FileInputStream(path);  
      wb = WorkbookFactory.create(inp);        
    } catch (FileNotFoundException e) {  
      e.printStackTrace();  
    } catch (InvalidFormatException e) {  
      e.printStackTrace();  
    } catch (IOException e) {  
      e.printStackTrace();  
    }  
      
  }    
    
  /** 
   * 鍙朎xcel锟�锟斤拷鏁版嵁锛屽寘鍚玥eader 
   * @return  List<String[]> 
   */  
 public List<String[]> getAllData(int sheetIndex){  
    int columnNum = 0;  
    Sheet sheet = wb.getSheetAt(sheetIndex);  
   /* if(sheet.getRow(0)!=null){  
        columnNum = sheet.getRow(0).getLastCellNum()-sheet.getRow(0).getFirstCellNum();  
    }  */
    //鍘绘帀header
    if(sheet.getRow(1)!=null){  
        columnNum = sheet.getRow(1).getLastCellNum()-sheet.getRow(1).getFirstCellNum();  
    }  
    if(columnNum>0){  
      for(Row row:sheet){   
          String[] singleRow = new String[columnNum];  
          int n = 0;  
          for(int i=0;i<columnNum;i++){  
             Cell cell = row.getCell(i, Row.CREATE_NULL_AS_BLANK);  
             switch(cell.getCellType()){  
               case Cell.CELL_TYPE_BLANK:  
                 singleRow[n] = "";  
                 break;  
               case Cell.CELL_TYPE_BOOLEAN:  
                 singleRow[n] = Boolean.toString(cell.getBooleanCellValue());  
                 break;  
                //鏁帮拷?  
               case Cell.CELL_TYPE_NUMERIC:                 
                 if(DateUtil.isCellDateFormatted(cell)){  
                   singleRow[n] = String.valueOf(cell.getDateCellValue());  
                 }else{   
                   cell.setCellType(Cell.CELL_TYPE_STRING);  
                   String temp = cell.getStringCellValue();  
                   //鍒ゆ柇鏄惁鍖呭惈灏忔暟鐐癸紝濡傛灉涓嶅惈灏忔暟鐐癸紝鍒欎互瀛楃涓茶鍙栵紝濡傛灉鍚皬鏁扮偣锛屽垯杞崲涓篋ouble绫诲瀷鐨勫瓧绗︿覆  
                   if(temp.indexOf(".")>-1){  
                     singleRow[n] = String.valueOf(new Double(temp)).trim();  
                   }else{  
                     singleRow[n] = temp.trim();  
                   }  
                 }  
                 break;  
               case Cell.CELL_TYPE_STRING:  
                 singleRow[n] = cell.getStringCellValue().trim();  
                 break;  
               case Cell.CELL_TYPE_ERROR:  
                 singleRow[n] = "";  
                 break;    
               case Cell.CELL_TYPE_FORMULA:  
                 cell.setCellType(Cell.CELL_TYPE_STRING);  
                 singleRow[n] = cell.getStringCellValue();  
                 if(singleRow[n]!=null){  
                   singleRow[n] = singleRow[n].replaceAll("#N/A","").trim();  
                 }  
                 break;    
               default:  
                 singleRow[n] = "";  
                 break;  
             }  
             n++;  
          }   
          if("".equals(singleRow[0])){continue;}//濡傛灉绗竴琛屼负绌猴紝璺宠繃  
          dataList.add(singleRow);  
      }  
    }  
    return dataList;  
  }   
 
 
 public int getRowNums(int sheetIndex){
	  	int a = 0;
	    Sheet sheet = wb.getSheetAt(sheetIndex);
	    int j = getRowNum(0);
	    Cell cell;
	    
	    for(int i = 0;i<j;i++){
	    	List list = new ArrayList();
		    	Row row = sheet.getRow(i);
		    	for(int k=0;k<10;k++){
		    		cell = row.getCell(k);
		    		if(cell!=null){
		    			list.add(cell);
			    		
		    		}
		    	}
		    	if(list.size()==1){
		    		 a= a+1;
		    	}else{
		    		continue;
		    	}
	    }
	    return j-a;
	  }  
 /** 
  * 杩斿洖Excel锟�锟斤拷琛宨ndex鍊硷紝瀹為檯琛屾暟瑕佸姞1 
  * @return 
  */  
 public int getRowNum(int sheetIndex){  
   Sheet sheet = wb.getSheetAt(sheetIndex);  
   return sheet.getLastRowNum();  
 }  
  /** 
   * 杩斿洖Excel锟�锟斤拷琛宨ndex鍊硷紝瀹為檯琛屾暟瑕佸姞1 
   * @return 
   */  
 /* public int getRowNum(int sheetIndex){  
    Sheet sheet = wb.getSheetAt(sheetIndex);
    int num = sheet.getLastRowNum();
    Row rows = sheet.getRow(1000);
    
    for (int i = 0; i < rows; i++) {
        for(int j=0;j<columns;j++){
            //鑾峰彇鍗曞厓鏍奸渶瑕佹敞鎰忕殑鏄畠鐨勪袱涓弬鏁帮紝绗竴涓槸鍒楁暟锛岀浜屼釜鏄鏁帮紝杩欎笌閫氬父鐨勮銆佸垪缁勫悎鏈変簺涓嶅悓锟�
            Cell cell=sheet.getCell(0, i);
            //鍗曞厓鏍硷拷?
            String cellValue=cell.getContents();
            System.out.print(cellValue+"  ");
        }
        System.out.println();
    }
   int columnNum = sheet.getRow(0).getLastCellNum()-sheet.getRow(0).getFirstCellNum();  
    int i = sheet.getLastRowNum();
    	Row tempRow;
    		for(i=0; i>0; i--){
	    		tempRow = sheet.getRow(i);
	 		    if(tempRow == null){
	 		    sheet.shiftRows(i+1, sheet.getLastRowNum(), -1);
	 		    }
	    	}
    for (int k = 0; k <= sheet.getLastRowNum(); k++) {
		Row hRow = sheet.getRow(k);
		//System.out.println((k + 1) + "锟�);
		if (isBlankRow(hRow)) // 鎵惧埌绌鸿绱㈠紩
		{
			int m = 0;
			for (m = k + 1; m <= sheet.getLastRowNum(); m++) {
				Row nhRow = sheet.getRow(m);
				if (!isBlankRow(nhRow)) {
					//System.out.println("涓嬩竴涓潪绌鸿" + (m + 1));
					sheet.shiftRows(m, sheet.getLastRowNum(), k - m);
					break;
				}
				if (m > sheet.getLastRowNum())
					break; // 姝ゅ伐浣滅翱瀹屾垚
				} 
			}
		}
    	return sheet.getLastRowNum(); 
  }  */
    
/*  private boolean isBlankRow(Row nhRow) {
	// TODO Auto-generated method stub
	return false;
}
*/
/** 
   * 杩斿洖鏁版嵁鐨勫垪锟�
   * @return  
   */  
  public int getColumnNum(int sheetIndex){  
    Sheet sheet = wb.getSheetAt(sheetIndex);  
    Row row = sheet.getRow(0);  
    if(row!=null&&row.getLastCellNum()>0){  
       return row.getLastCellNum();  
    }  
    return 0;  
  }  
    
  /** 
   * 鑾峰彇鏌愪竴琛屾暟锟�
   * @param rowIndex 璁℃暟锟�锟�锟斤拷锛宺owIndex锟�浠ｈ〃header锟�
   * @return 
   */  
    public List<String[]> getRowData(int sheetIndex,int rowIndex,int max){ 
    	int rowNum = getRowNum(0);
    	if((rowNum-rowIndex*max)<max){
    		max=rowNum-rowIndex*max;
    	}
    	List<String[]> dataList = new ArrayList<String[]>(max);
      if(rowIndex>this.getRowNum(sheetIndex)){  
    	  return dataList;  
      }else{  
    	  String[] dataArray = null;
    	 int columnNum = 0;  
    	 Sheet sheet = wb.getSheetAt(sheetIndex); 
    	 if(sheet.getRow(0)!=null){  
    	        columnNum = sheet.getRow(0).getLastCellNum()-sheet.getRow(0).getFirstCellNum();  
    	    } 
			for (int i = rowIndex*1000 ; i < max+rowIndex*max+1; i++) {
				dataArray = new String[this.getColumnNum(sheetIndex)];
				 Row row = sheet.getRow(i);
				 if(rowIndex==0&&i==0){
					 continue;
				 }
				for (int n = 0; n < columnNum; n++) {
					Cell cell = row.getCell(n, Row.CREATE_NULL_AS_BLANK);
					switch (cell.getCellType()) {
					case Cell.CELL_TYPE_BLANK:
						dataArray[n] = "";
						break;
					case Cell.CELL_TYPE_BOOLEAN:
						dataArray[n] = Boolean.toString(cell
								.getBooleanCellValue());
						break;
					// 鏁帮拷?
					case Cell.CELL_TYPE_NUMERIC:
						if (DateUtil.isCellDateFormatted(cell)) {
							dataArray[n] = String.valueOf(cell
									.getDateCellValue());
						} else {
							cell.setCellType(Cell.CELL_TYPE_STRING);
							String temp = cell.getStringCellValue();
							// 鍒ゆ柇鏄惁鍖呭惈灏忔暟鐐癸紝濡傛灉涓嶅惈灏忔暟鐐癸紝鍒欎互瀛楃涓茶鍙栵紝濡傛灉鍚皬鏁扮偣锛屽垯杞崲涓篋ouble绫诲瀷鐨勫瓧绗︿覆
							if (temp.indexOf(".") > -1) {
								dataArray[n] = String.valueOf(new Double(temp))
										.trim();
							} else {
								dataArray[n] = temp.trim();
							}
						}
						break;
					case Cell.CELL_TYPE_STRING:
						dataArray[n] = cell.getStringCellValue().trim();
						break;
					case Cell.CELL_TYPE_ERROR:
						dataArray[n] = "";
						break;
					case Cell.CELL_TYPE_FORMULA:
						cell.setCellType(Cell.CELL_TYPE_STRING);
						dataArray[n] = cell.getStringCellValue();
						if (dataArray[n] != null) {
							dataArray[n] = dataArray[n].replaceAll("#N/A", "")
									.trim();
						}
						break;
					default:
						dataArray[n] = "";
						break;
					}
					
				}
				dataList.add(dataArray);
			}
		}
	return dataList;  
    }  
    
  /** 
   * 鑾峰彇鏌愪竴鍒楁暟锟�
   * @param colIndex 
   * @return 
   */  
  public String[] getColumnData(int sheetIndex,int colIndex){  
    String[] dataArray = null;  
    if(colIndex>this.getColumnNum(sheetIndex)){  
      return dataArray;  
    }else{     
      if(this.dataList!=null&&this.dataList.size()>0){  
        dataArray = new String[this.getRowNum(sheetIndex)+1];  
        int index = 0;  
        for(String[] rowData:dataList){  
          if(rowData!=null){  
             dataArray[index] = rowData[colIndex];  
             index++;  
          }  
        }  
      }  
    }  
    return dataArray;  
      
  }  
 }  