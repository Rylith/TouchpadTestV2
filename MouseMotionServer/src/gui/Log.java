package gui;

public class Log {
	
	public static void println(String str){
		OptionsInterface.getText().append(str);
		OptionsInterface.getText().append("\n");
	}
	
	public static void print(String str){
		OptionsInterface.getText().append(str);
	}
}
