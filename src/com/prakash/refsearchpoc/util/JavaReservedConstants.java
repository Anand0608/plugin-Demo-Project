package com.prakash.refsearchpoc.util;

public class JavaReservedConstants {

	/**
	 * @return the reservedWords
	 */
	public static String[] getReservedWords() {
		return RESERVED_WORDS;
	}

	private static final String[] RESERVED_WORDS = { "abstract", "assert", "boolean", "break", "byte", "case", "catch",
			"char", "class", "const", "continue", "default", "double", "do", "else", "enum", "extends", "false",
			"final", "finally", "float", "for", "goto", "if", "implements", "import", "instanceof", "int", "interface",
			"long", "native", "new", "null", "package", "private", "protected", "public", "return", "short", "static",
			"strictfp", "super", "switch", "synchronized", "this", "throw", "throws", "transient", "true", "try",
			"void", "volatile", "while" };
}
