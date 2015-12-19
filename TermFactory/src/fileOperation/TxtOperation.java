package fileOperation;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class TxtOperation {
	public static void writeToFile(String filePath, List<String> towrite, String type){
		try{
			FileWriter file;
			if(type.equals("append"))
				file = new FileWriter(filePath, true);
			else
				file = new FileWriter(filePath,false);
			for(String line:towrite){
				file.write(line);
			}
			file.close();
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	public static void writeToFile(String filePath, String towrite, String type){
		try{
			FileWriter file;
			if(type.equals("append"))
				file = new FileWriter(filePath, true);
			else
				file = new FileWriter(filePath,false);
			
			file.write(towrite);
			file.close();
		}catch(IOException e){
			e.printStackTrace();
		}
	}
}
