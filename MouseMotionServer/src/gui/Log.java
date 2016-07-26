package gui;

public class Log {
	
	public static void println(String str){
		GraphicalInterface.getText().append(str);
		GraphicalInterface.getText().append("\n");
	}

}
