package org.proof.recorder.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexUtils {

	private RegexUtils() {

	}

	/**
	 * isPhoneNumberValid: Validate phone number using Java reg ex. This method
	 * checks if the input string is a valid phone number.
	 * 
	 * @param email
	 *            String. Phone number to validate
	 * @return boolean: true if phone number is valid, false otherwise.
	 */
	public static boolean isPhoneNumberValid(String phoneNumber) {
		boolean isValid = false;
		/*
		 * Phone Number formats: (nnn)nnn-nnnn; nnnnnnnnnn; nnn-nnn-nnnn ^\\(? :
		 * May start with an option "(" . (\\d{3}): Followed by 3 digits. \\)? :
		 * May have an optional ")" [- ]? : May have an optional "-" after the
		 * first 3 digits or after optional ) character. (\\d{3}) : Followed by
		 * 3 digits. [- ]? : May have another optional "-" after numeric digits.
		 * (\\d{4})$ : ends with four digits.
		 * 
		 * Examples: Matches following phone numbers: (123)456-7890,
		 * 123-456-7890, 1234567890, (123)-456-7890
		 */
		// Initialize reg ex for phone number.
		
		//String expression = "^\\+?(\\d{3})\\)?[- ]?(\\d{3})[- ]?(\\d{4})$";
		String expression = "^([\\+][0-9]{1,3}([ \\.\\-])?)?([\\(]{1}[0-9]{3}[\\)])?([0-9A-Z \\.\\-]{1,32})((x|ext|extension)?[0-9]{1,4}?)$";
		CharSequence inputStr = phoneNumber;
		Pattern pattern = Pattern.compile(expression);
		Matcher matcher = pattern.matcher(inputStr);
		if (matcher.matches()) {
			isValid = true;
		}
		return isValid;
	}

}
