package org.proof.recorder.simplexfb;

import java.text.DecimalFormat;

public class FileSizeFormatter {

	 public String format(long byteSize) {

	        String sizeUnit = "bytes";
	        float size = byteSize;

	        if (byteSize > 1000000000) {
	            size = size / 1000000000;
	            sizeUnit = "Gb";


	        } else if (byteSize > 1000000) {
	            size = size / 1000000;
	            sizeUnit = "MB";

	        } else if (byteSize > 1000) {
	            size = size / 1000;
	            sizeUnit = "Kb";
	        }

	        return (new DecimalFormat("#.##")).format(size) + " " + sizeUnit;

	    }
	}
