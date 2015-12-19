package fileOperation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import DBConnection.MongoDBJDBC;
import model.LocalTerm;

public class ExcelOperation {
	/***
	 * 获取指定目录下的所有的文件（不包括文件夹），采用了递归
	 * 
	 * @param obj
	 * @return
	 */
	public static ArrayList<File> getListFiles(Object obj) {
		File directory = null;
		if (obj instanceof File) {
			directory = (File) obj;
		} else {
			directory = new File(obj.toString());
		}
		ArrayList<File> files = new ArrayList<File>();
		if (directory.isFile()) {
			files.add(directory);
			return files;
		} else if (directory.isDirectory()) {
			File[] fileArr = directory.listFiles();
			for (int i = 0; i < fileArr.length; i++) {
				File fileOne = fileArr[i];
				files.addAll(getListFiles(fileOne));
			}
		}
		return files;
	}
	
	public void test_getListFiles(){
		ArrayList<File> files= getListFiles("d:\\Temp\\a\\a");
		for(File file: files){
			System.out.println(file.getAbsolutePath());
		}
	}
	
	public static void readCSV(String path){
		MongoDBJDBC database = new MongoDBJDBC();
		database.createConnection("localhost", 27017, null, null);
		
		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = ",";
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(path),"GBK"));
			while ((line = br.readLine()) != null) {
			        // use comma as separator
				String[] country = line.split(cvsSplitBy);
				if (country.length < 2)
					continue;
				LocalTerm lt;
				if (country.length > 2){
					System.out.println("code= " + country[0] + " , name_zh=" + country[1] + " , modifier=" + country[2]);
					lt = new LocalTerm(null, country[1]);
					lt.addSource("ICD10",country[0], "data/icd10all.csv", "file");
					lt.addSource("ICD10Modifier",country[2], "data/icd10all.csv", "file");
				}
				else{
					System.out.println("code= " + country[0] + " , name_zh=" + country[1]);
					lt = new LocalTerm(null, country[1]);
					lt.addSource("ICD10",country[0], "data/icd10all.csv", "file");
				}
				String document = lt.TermToJson();
				database.insertData("ICDChineseInfo", document);
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		System.out.println("Done");
	}
	
	public static void main(String[] args){
		readCSV("data/icd10all.csv");
	}
}
