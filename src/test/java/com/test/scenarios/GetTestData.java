package com.test.scenarios;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class GetTestData {

//	To Display Hashset Values
	
//	Hash.entrySet().forEach(entry -> {
//	    System.out.println(entry.getKey() + " " + entry.getValue());});

	
// newTS.forEach(arr -> System.out.println(Arrays.toString(arr)));
	
	// To get Total Number of Test Suite
	public List<String[]> NumberOfTestSuite(List<String[]> TestSuite){
		
		// 0=TestSutesName 1=Status
		Predicate<String[]> NoOfTest = x -> !x[0].isEmpty() &&  x[1].equalsIgnoreCase("Y");
		
		List<String[]> getNoOfTest = TestSuite.parallelStream().filter(NoOfTest).collect(Collectors.toList());
		
		List<String[]> newTS = new ArrayList<String[]>();
		 
		for (int i = 0; i < getNoOfTest.size(); i++) {
			
			String[] newStrings = new String[2];
			String[] temp = getNoOfTest.get(i);
		   
			newStrings[0] = temp[0];
			newStrings[1] = temp[2];
			newTS.add(newStrings);
		}
		
		return newTS;
	}
	
	// To get Total Number of Test Suite and Platform
	public List<String[]> getPlatform(List<String[]> TestSuite){
		
		// 0=TestSutesName 1=Status 2=Platform 3=TestCaseId
		Predicate<String[]> NoOfTest = x -> !x[0].isEmpty() &&  x[1].equalsIgnoreCase("Y");
		
		List<String[]> getNoOfTest = TestSuite.parallelStream().filter(NoOfTest).collect(Collectors.toList());
		
		List<String[]> newTS = new ArrayList<String[]>();
		 
		for (int i = 0; i < getNoOfTest.size(); i++) {
			
			String[] newStrings = new String[1];
			String[] temp = getNoOfTest.get(i);
		   
			newStrings[0] = temp[2];
			newTS.add(newStrings);
		}
	
		return newTS;
	}
	
	public HashMap<String, List<String>> TestCase(List<String[]> TestSuite){
		
		HashMap<String, List<String>> Hash = new HashMap<String, List<String>>();
			
		List<String> TC = new ArrayList<String>();
		String TSName = null;
		boolean found = false;
		
		// Index 0=TSName 1=Status 2=Platform 3=TCName 4=Description
		for (String[] data : TestSuite) {
			//System.out.println(Arrays.toString(data));
			if(data[1].equalsIgnoreCase("Y") && found==false) {
				
				TC = new ArrayList<String>();
				TSName = data[0];
				TC.add(data[3]);
				found=true;
				
			}else if(data[1].isEmpty() && found==true) {
				
				TC.add(data[3]);
				
			}else if(data[1].equalsIgnoreCase("Y") && found==true){
				
				Hash.put(TSName, TC);
				TC = new ArrayList<String>();
				TSName = data[0];
				TC.add(data[3]);
				
			}else if(data[1].equalsIgnoreCase("N") && found==true) {
				
				Hash.put(TSName, TC);
				found=false;
				
			}
		}
		// adding the last Test Suite
		Hash.put(TSName, TC);
		return Hash;
	}
	
	public HashMap<String, List<String>> TestProcedure(List<String[]> TestCase){
		
		HashMap<String, List<String>> Hash = new HashMap<String, List<String>>();
		
		List<String> TC = null;
		String TCName = null;
		
		// Index 0=TCName 1=TestProcedure
		for (String[] data : TestCase) {
	
			// TestSuite name is available, then create a key for hash map (first time)
			if(!data[0].isEmpty()&&TCName==null) {
				
				// Create new  
				TC = new  ArrayList<String>();
				
				TCName = data[0];
				TC.add(data[1]);
				
			// If not firstTime, can add into hash map
			}else if(!data[0].isEmpty()&&TCName!=null) {
				
				Hash.put(TCName, TC);
				
				// Reset List
				TC = new  ArrayList<String>();
				
				TCName = data[0];
				TC.add(data[1]);
				
			}else {
			
				// add TestCase name
				TC.add(data[1]);	
			}
			
		}
		// adding the last Test Suite
		Hash.put(TCName, TC);
		
		return Hash;
	}
	
	public HashMap<String, List<String>> Modules(List<String[]> Modules){
		
		HashMap<String, List<String>> Hash = new HashMap<String, List<String>>();
		
		List<String> TP = null;
		String TPName = null;
		
		// Index 0=TPName 1=Screenshot 2=Action 3=ObjectName 4=InputData 5=ExpectedValue
		for (String[] data : Modules) {
	
			// Create new  
			TP = new  ArrayList<String>();
		
			TPName = data[0];
			TP.add(data[1]);
			TP.add(data[2]);
			TP.add(data[3]);
			TP.add(data[4]);
			TP.add(data[5]);
			
			Hash.put(TPName, TP);
		}
		
		return Hash;
	}
	
	public HashMap<String, List<String>> ObjectRepository(List<String[]> ObjectRepository){
	
		HashMap<String, List<String>> Hash = new HashMap<String, List<String>>();
		
		List<String> OR = null;
		String ORName = null;
		
		// Index 0=ORName 1=ObjectType 2=ObjectValue
		for (String[] data : ObjectRepository) {
	
			// Create new  
			OR = new  ArrayList<String>();
			
			ORName = data[0];
			OR.add(data[1]);
			OR.add(data[2]);
			
			Hash.put(ORName, OR);
		}
		
		return Hash;
	}
	
	public HashMap<String, List<String>> InputData(List<String[]> InputData){
		
		HashMap<String, List<String>> Hash = new HashMap<String, List<String>>();
		
		List<String> ID = null;
		String IDName = null;
		
		// Index 0=InputID 1=InputValue 
		for (String[] data : InputData) {
	
			// Create new  
			ID = new ArrayList<String>();
			
			IDName = data[0];
			ID.add(data[1]);
			
			Hash.put(IDName, ID);
		}
		
		return Hash;
	}
	
	public List<String[]> MobileConfig(List<String[]> Config){
		
		Predicate<String[]> NoOfTest = x -> x[0].equalsIgnoreCase("Y");
		
		List<String[]> newConfig = Config.parallelStream().filter(NoOfTest).collect(Collectors.toList());
		
		return newConfig;
	}
	
	public List<String[]> BowserConfig(List<String[]> Config){
		
		Predicate<String[]> NoOfTest = x -> x[0].equalsIgnoreCase("Y");
		
		List<String[]> newConfig = Config.parallelStream().filter(NoOfTest).collect(Collectors.toList());
	
		return newConfig;
	}
}
