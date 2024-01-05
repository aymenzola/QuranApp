package com.app.dz.quranapp.quran.quranParte;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
created by mhashim6 (Muhammad Hashim) on 26/06/2017
*/
public class RemoveTashkeel {

	//Unicode values of Tashkeel
	private final static char[] tashkeel  = { (char) Integer.parseInt("064F", 16), (char) Integer.parseInt("0650", 16),
		(char) Integer.parseInt("0651", 16), (char) Integer.parseInt("0652", 16),
		(char) Integer.parseInt("064B", 16), (char) Integer.parseInt("064C", 16),
		(char) Integer.parseInt("064D", 16), (char) Integer.parseInt("064E", 16) };

	public static void main(String[] args) {
		
		if (args.length != 2){
			System.out.println("Usage:\njava -Dfile.encoding=UTF-8 RemoveTashkeel sourceFile targetFile\n");
			System.exit(1);
		}

		//check if sourceFile exists
		File sourceFile = new File(args[0]);
		if (!sourceFile.exists()) {
			System.out.println(args[0] + " doesn't exist.\nterminated.\n\n");
			System.exit(2);
		}
		long start = System.currentTimeMillis();
		removeTashkeel(args[0], args[1]);
		long end = System.currentTimeMillis();
		System.out.printf("\nTime taken: %d ms.\n", (end - start));
	}

	private static void removeTashkeel(String in, String out) {
		FileReader reader = null;	
		FileWriter writer = null;
		
		try {
			reader = new FileReader(in);
			writer = new FileWriter(out);

			//traverse each character
			int c;
			while ((c = reader.read()) != -1)
			if (!isTashkeel((char) c))
			writer.append((char) c);
			System.out.println("\n-----PROCESS FINISHED SUCCESSFULLY!");
		}
		catch (Exception e) {
			System.out.println("\n-----AN ERROR OCCURED:");
			e.printStackTrace();
		}
		finally {
			try {
				reader.close();
				writer.flush();
				writer.close();
			}
			catch (IOException e) {
				System.out.println("ERROR WHILE FLUSHING/CLOSING FILES:\n\n");
				e.printStackTrace();
			}
		}
	}

	public static boolean isTashkeel(char c) {
		for (int i = 0; i < tashkeel.length; i++) {
			if (c == tashkeel[i]) { return true; }
		}
		return false;
	}

}