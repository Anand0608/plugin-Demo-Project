package com.prakash.refsearchpoc.util;

public class ValidateSearch {
	
	public static boolean getValidTypes(String searchString){
		String[] resevedWord = JavaReservedConstants.getReservedWords();
		boolean isValid = true;
		for (int i = 0; i < resevedWord.length; i++) {
			if(searchString.equals(resevedWord[i])) {
				isValid = false;
				break;
			}
		}
		return isValid;
	}

}
