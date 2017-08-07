package com.dhb.util.excell;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.model.SharedStringsTable;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

public class Excel2007Reader {
	private static final Log logger = LogFactory.getLog(Excel2007Reader.class);
	
	private static IRowReader rowReader;
	
	private static int sheetIndex = -1;
	private static List<String> rowlist = new ArrayList<String>();
	// 当前行
	private static int curRow = 0;
	// 当前列
	private static int curCol = 0;
	// 日期标志
	private static boolean dateFlag;
	// 数字标志
	private static boolean numberFlag;

	private static boolean isTElement;


	public void setRowReader(IRowReader rowReader) {
		this.rowReader = rowReader;
	}
	public void processOneSheet(String filename) throws Exception {
		OPCPackage pkg = OPCPackage.open(filename);
		XSSFReader r = new XSSFReader( pkg );
		SharedStringsTable sst = r.getSharedStringsTable();

		XMLReader parser = fetchSheetParser(sst);

		// rId2 found by processing the Workbook
		// Seems to either be rId# or rSheet#
		InputStream sheet2 = r.getSheet("rId2");
		InputSource sheetSource = new InputSource(sheet2);
		parser.parse(sheetSource);
		sheet2.close();
	}

	public void processAllSheets(String filename) throws Exception {
		OPCPackage pkg = OPCPackage.open(filename);
		XSSFReader r = new XSSFReader( pkg );
		SharedStringsTable sst = r.getSharedStringsTable();		
		XMLReader parser = fetchSheetParser(sst);
		Iterator<InputStream> sheets = r.getSheetsData();
		while(sheets.hasNext()) {
			System.out.println("Processing new sheet:\n");
			InputStream sheet = sheets.next();
			InputSource sheetSource = new InputSource(sheet);
			parser.parse(sheetSource);
			sheet.close();
			System.out.println("");
		}
	}
	/**
	 * 遍历工作簿中所有的电子表格
	 * 
	 * @param filename
	 * @throws Exception
	 */
	public void process(String filename) throws Exception {
		OPCPackage pkg = OPCPackage.open(filename);
		XSSFReader r = new XSSFReader(pkg);
		SharedStringsTable sst = r.getSharedStringsTable();
		XMLReader parser = fetchSheetParser(sst);
		Iterator<InputStream> sheets = r.getSheetsData();
		while (sheets.hasNext()) {
			curRow = 0;
			sheetIndex++;
			InputStream sheet = sheets.next();
			InputSource sheetSource = new InputSource(sheet);
			parser.parse(sheetSource);
			sheet.close();
		}
	}
	/**
	 * 遍历工作簿中所有的电子表格
	 * 
	 * @param inputStream
	 * @throws Exception
	 */
	public void process(InputStream inputStream) throws Exception {
		OPCPackage pkg = OPCPackage.open(inputStream);
		XSSFReader r = new XSSFReader(pkg);
		SharedStringsTable sst = r.getSharedStringsTable();
		XMLReader parser = fetchSheetParser(sst);
		Iterator<InputStream> sheets = r.getSheetsData();
		while (sheets.hasNext()) {
			curRow = 0;
			sheetIndex++;
			InputStream sheet = sheets.next();
			InputSource sheetSource = new InputSource(sheet);
			parser.parse(sheetSource);
			sheet.close();
		}
	}
	public XMLReader fetchSheetParser(SharedStringsTable sst) throws SAXException {
		//李真河 2014年09月25日  15:56 注释掉该代码，替换成下面用JDK自带的工厂类
//		XMLReader parser =
//			XMLReaderFactory.createXMLReader(
//					"org.apache.xerces.parsers.SAXParser"
//			);
//		ContentHandler handler = new SheetHandler(sst);
//		parser.setContentHandler(handler);
//		return parser;
		
		XMLReader parser = XMLReaderFactory.createXMLReader(); 
		ContentHandler handler = new SheetHandler(sst);
        parser.setContentHandler(handler); 
        return parser; 
	}

	/** 
	 * See org.xml.sax.helpers.DefaultHandler javadocs 
	 */
	private static class SheetHandler extends DefaultHandler {
	
		private SharedStringsTable sst;
		private String lastContents;
		private boolean nextIsString;
		
		
		
		
		private SheetHandler(SharedStringsTable sst) {
			this.sst = sst;
		}
		
		public void startElement(String uri, String localName, String name,
				Attributes attributes) throws SAXException {

			// c => 单元格
			if ("c".equals(name)) {
				// 如果下一个元素是 SST 的索引，则将nextIsString标记为true
				String cellType = attributes.getValue("t");
				if ("s".equals(cellType)) {
					nextIsString = true;
				} else {
					nextIsString = false;
				}
				// 日期格式
				String cellDateType = attributes.getValue("s");
				if ("1".equals(cellDateType)) {
					dateFlag = true;
				} else {
					dateFlag = false;
				}
				String cellNumberType = attributes.getValue("s");
				if ("2".equals(cellNumberType)) {
					numberFlag = true;
				} else {
					numberFlag = false;
				}

			}
			// 当元素为t时
			if ("t".equals(name)) {
				isTElement = true;
			} else {
				isTElement = false;
			}

			// 置空
			lastContents = "";
		}

		public void endElement(String uri, String localName, String name)
				throws SAXException {

			// 根据SST的索引值的到单元格的真正要存储的字符串
			// 这时characters()方法可能会被调用多次
			if (nextIsString) {
				try {
					int idx = Integer.parseInt(lastContents);
					lastContents = new XSSFRichTextString(sst.getEntryAt(idx))
							.toString();
				} catch (Exception e) {

				}
			}
			// t元素也包含字符串
			if (isTElement) {
				String value = lastContents.trim();
				rowlist.add(curCol, value);
				curCol++;
				isTElement = false;
				// v => 单元格的值，如果单元格是字符串则v标签的值为该字符串在SST中的索引
				// 将单元格内容加入rowlist中，在这之前先去掉字符串前后的空白符
			} else if ("v".equals(name)) {
				String value = lastContents.trim();
				value = value.equals("") ? " " : value;
				
				rowlist.add(curCol, value);
				curCol++;
			} else {
				// 如果标签名称为 row ，这说明已到行尾，调用 optRows() 方法
				if (name.equals("row")) {
					try {
						rowReader.getRows(sheetIndex, curRow, rowlist);
					} catch (Exception e) {
						logger.error("", e);
						throw new SAXException(e);
					}
					rowlist.clear();
					curRow++;
					curCol = 0;
				}
			}

		}

		public void characters(char[] ch, int start, int length)
				throws SAXException {
			// 得到单元格内容的值
			lastContents += new String(ch, start, length);
		}
	}
	
}