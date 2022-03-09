package com.test.utilities;
import  java.io.*;

public class CreateExlFile{
	
	String FilePath;
	
	public CreateExlFile(String FilePath) {
		this.FilePath = FilePath+"\\Logs.csv";
	}
	
    public void CreateLog() {
        
    	String newFileName = FilePath;
    	File newFile = new File(newFileName);
    	BufferedWriter writer;
    	try {
			writer = new BufferedWriter(new FileWriter(newFile));
			
			writer.append("Status");
			writer.append(",");
			writer.append("Test Suites");
			writer.append(",");
			writer.append("Test Case");
			writer.append(",");
			writer.append("Test Procedure");
			writer.append(",");
			writer.append("Input Value");
			writer.append(",");
			writer.append("Expected Result");
			writer.append(",");
			writer.append("Actual Result");
			writer.append(",");
			writer.append("Action");
			writer.append(",");
			writer.append("Object Name");
			writer.append(",");
			writer.append("Object Type");
			writer.append(",");
			writer.append("Object Value");
			writer.append(",");
			writer.append("Platform");
			writer.append(",");
			writer.append("Screenshot");
			
			writer.flush();
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public void WriteLog(String[] DataArray) {
        
    	FileWriter write;
    	
        try {
        	 write = new FileWriter(FilePath);

        	for(String data: DataArray ) {
		    	write.append(data);
		    	write.append(",");
        	}
        	
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();

        }

    }
}