package org.proof.recorder.service.jni;

public enum Encoding {
	 ULAW_8(false, 8, false, Format.ULAW),
	    ALAW_8(false, 8, false, Format.ALAW),
	    PCM_SIGNED_8(true, 8, false),
	    PCM_UNSIGNED_8(false, 8, false),
	    PCM_SIGNED_16(true, 16, false),
	    PCM_UNSIGNED_16(false, 16, false),
	    PCM_SIGNED_24(true, 24, false),
	    PCM_UNSIGNED_24(false, 24, false),
	    PCM_SIGNED_32(true, 32, false),
	    PCM_UNSIGNED_32(false, 32, false),
	    PCM_FLOAT_32(true, 32, true),
	    PCM_FLOAT_64(true, 64, true);
	 
	    public interface Ordinals
	    {
	        byte ULAW_8 = 0;
	        byte ALAW_8 = 1;
	        byte PCM_SIGNED_8 = 2;
	        byte PCM_UNSIGNED_8 = 3;
	        byte PCM_SIGNED_16 = 4;
	        byte PCM_UNSIGNED_16 = 5;
	        byte PCM_SIGNED_24 = 6;
	        byte PCM_UNSIGNED_24 = 7;
	        byte PCM_SIGNED_32 = 8;
	        byte PCM_UNSIGNED_32 = 9;
	        byte PCM_FLOAT_32 = 10;
	        byte PCM_FLOAT_64 = 11;
	    }

	    public enum Format
	    {
	        PCM, ULAW, ALAW;
	    }

	    private final boolean signed;
	    private final int bits;
	    private final boolean floatingPoint;
	    private final Format format;

	    private Encoding(boolean signed, int bits, boolean isFloatingPoint)
	    {
	        this(signed, bits, isFloatingPoint, Format.PCM);
	    }
	    private Encoding(boolean signed, int bits, boolean isFloatingPoint, Format format)
	    {
	        this.signed = signed;
	        this.bits = bits;
	        this.floatingPoint = isFloatingPoint;
	        this.format = format;
	    }

	    public int getBits()
	    {
	        return bits;
	    }

	    public Format getFormat()
	    {
	        return format;
	    }

	    public boolean isSigned()
	    {
	        return signed;
	    }

	    public boolean isFloatingPoint()
	    {
	        return floatingPoint;
	    }

}
