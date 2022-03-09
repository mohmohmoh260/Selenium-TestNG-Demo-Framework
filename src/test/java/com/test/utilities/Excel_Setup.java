package com.test.utilities;

import java.util.ArrayList;
import java.util.List;

public class Excel_Setup {
	
	static Xls_Reader reader;
	
	public static List<String[]> GetDataFromExcel(String ExcelPath, String SheetName){
		
		List<String[]> myData = new ArrayList<String[]>();
		
		try {
			
			// select which excel
			reader = new Xls_Reader(ExcelPath);
			
		}catch (Exception e) {
			e.getLocalizedMessage();
		}
		
		// get the data and store in String
		for(int row=2; row<= reader.getRowCount(SheetName); row++) {
			
			// create temporary array to store data
			String[] temp = new String[reader.getColumnCount(SheetName)];
			
			for(int col=1; col<=reader.getColumnCount(SheetName); col++) {
			
				// get the cell data
				String data = reader.getCellData(SheetName, col, row);

				temp[col-1] = data;
			}
		
			if (isEmptyStringArray(temp)==false) {
				
				myData.add(temp);
			}
		}
		
		return myData;
	}
	
	public static List<String[]> GetModulesFromExcel(String ExcelPath){
		
		List<String[]> myData = new ArrayList<String[]>();
		
		try {
			
			// select which excel
			reader = new Xls_Reader(ExcelPath);
			
		}catch (Exception e) {
			e.getLocalizedMessage();
		}
		
		
		for(int i=0; i<reader.getTotalSheet(); i++) {
			
			if(!reader.getSheetName().get(i).equalsIgnoreCase("Object Repository") && !reader.getSheetName().get(i).equalsIgnoreCase("Input Data") 
					&& !reader.getSheetName().get(i).equalsIgnoreCase("Configuration") && !reader.getSheetName().get(i).equalsIgnoreCase("List")) {
				
				String tempSheet = reader.getSheetName().get(i);
				// get the data and store in String
				for(int row=2; row<= reader.getRowCount(tempSheet); row++) {
					
					// create temporary array to store data
					String[] temp = new String[reader.getColumnCount(tempSheet)];
					
					for(int col=1; col<=reader.getColumnCount(tempSheet); col++) {
					
						// get the cell data
						String data = reader.getCellData(tempSheet, col, row);

						temp[col-1] = data; 
					}
				
					if (isEmptyStringArray(temp)==false) {
						
						myData.add(temp);
					}
					
				}
				
			}
			
		}
		return myData;
	
	}
	
	public static boolean isEmptyStringArray(String [] array){
		
		 for(int i=0; i<array.length; i++){ 
			 
			  if(array[i]!=null && array[i]!=""){
				  
			   return false;
			  }
		  }
		 
		  return true;
	}

}


