package pub.ayada.dataStructures.utils;

import pub.ayada.dataStructures.chararray.CharArr;

public class ArrayFuncs {

	public static byte[] handleArraySize(byte[] src, int length) {
		byte[] dest = new byte[length];
		if (length <= src.length)
			 System.arraycopy(src, 0, dest, 0, length);
		else System.arraycopy(src, 0, dest, 0, src.length);
		
		return dest;
	}
	public static char[] handleArraySize(char[] src, int length) {
	
		if  (length == src.length) return src; 
		
		char[] dest = new char[length];

		if (length <= src.length)
			 System.arraycopy(src, 0, dest, 0, length);
		else System.arraycopy(src, 0, dest, 0, src.length);
		return dest;
	 
	}
	public static StringBuilder[] handleArraySize(StringBuilder[] src, int length) {	
		
		if  (length == src.length) return src; 
		
		StringBuilder[] dest = new StringBuilder[length];
		if (length <= src.length)
			 System.arraycopy(src, 0, dest, 0, length);
		else System.arraycopy(src, 0, dest, 0, src.length);
		return dest;
	}	
	
	public static CharArr[] handleArraySize(CharArr[] src, int length) {
		if  (length == src.length) return src; 
		CharArr[] dest = new CharArr[length];
		if (length <= src.length)
			 System.arraycopy(src, 0, dest, 0, length);
		else System.arraycopy(src, 0, dest, 0, src.length);
		return dest;
	}
	
	
	
	
	public static byte[] ArrayCopy(byte[] src, int startPos, int length) throws Exception {
		if (src==null) return null; 
		
		byte[] dest= new byte[length - startPos+1];
		try {
			System.arraycopy(src, startPos, dest, 0, length);
		} 
   	    catch (ArrayIndexOutOfBoundsException e) {
   	    	ShowParentExceptio(e);
   	    	throw new Exception("Failed to copy the Data from Source Array to Destination Array\n" 
   				 +"Source Start Position : " + startPos + "  length : " + length + "\n"
   				 +disp(src));
   	    }
		return dest;
		
	}

	
	private static String disp(byte[] BufArr) {	
		if (BufArr == null) return "(null)";
	    
		StringBuilder sb = new StringBuilder(); 
	       
	    for (int i=0;i< BufArr.length; i++)
	    	   sb.append((char) (BufArr[i] & 0xFF) ); 	
	    return sb.toString();
    }
	
	private static void ShowParentExceptio(Exception e) {
		   System.err.println(e.getMessage()); 
		   e.printStackTrace(System.err);
	}	

}