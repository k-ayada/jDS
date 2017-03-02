package pub.ayada.dataStructures.chararray;

import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import pub.ayada.dataStructures.utils.ArrayFuncs;

public class CharArrV1 {

	private char[] val;
	private int pos = 0;

	public CharArrV1() {
		this(16);
	}

	public CharArrV1(int InitialSize) {
		this.val = new char[InitialSize];
	}

	public CharArrV1(char[] initArr)throws Exception  {
		this(initArr, 0, initArr.length);
	}
	public CharArrV1(char[] initArr,int len) throws Exception {
		this(initArr, 0, len);
	}
	public CharArrV1(char[] initArr, int from, int len)throws Exception  {
		this(len);
		appendArr(initArr, from, 0, len);
	}


	public CharArrV1(CharArrV1 src)throws Exception  {
		this(src, 0, 0);
	}

	public CharArrV1(CharArrV1 src, int len) throws Exception {
		this(src, 0, len);
	}

	public CharArrV1(CharArrV1 src, int from, int len) throws Exception {
		this(len);
		appendArr(src.toArray(), from, 0, len);

	}

	public CharArrV1(String argExpr) {
		this.pos = argExpr.trim().length();
		this.val = new char[getPos()];
		argExpr.getChars(0, getPos(), this.val, 0);
	}

	 public CharArrV1( ByteBuffer buf ) throws Exception {
		 this(buf, java.nio.charset.Charset.defaultCharset().name());
	 }
    public CharArrV1(ByteBuffer buf,String charset) throws Exception {
    	CharsetDecoder csd = Charset.forName(charset).newDecoder();
    	CharBuffer cBuf = csd.decode(buf);
    	this.pos = cBuf.length();
        this.val = new char[this.pos];
        cBuf.get(this.val);
        while (this.val[this.pos-1] == '\0') this.pos--;
	}

	public void loadArrTillNull(char[] src, int srcStart, int destStart) throws Exception {

		int srcEndPos = src.length - 1;
		for (; srcEndPos > 0; srcEndPos--)
			if (src[srcEndPos] != '\0')
				break;

		srcEndPos = srcEndPos - srcStart + 1; // 1 is added to point die to
												// subtracting 1 earlier
        this.val = new char[srcEndPos];
		ArrayCopy(src, srcStart, this.val, destStart, (srcEndPos));
		this.pos = srcStart + (srcEndPos - srcStart);
		trimm();
	}

	public String getString() {
		String s = new String(this.val, 0, getPos());
		return s;
	}
	public String toString() {
		String s = new String(this.val, 0, getPos());
		return s;
	}

	public String subString(int from) {
		return subString(from, (getPos() - from));
	}

	public String subString(int from, int len) {
		StringBuilder b = new StringBuilder(len);
		if (getPos() < (from + len))
			return b.append(this.val, from, (this.pos - len)).toString();

		return b.append(this.val, from, len).toString().trim();
	}

	public char[] subArrayFrom(int from)throws Exception  {
		return subArray(from, (this.pos-from));
	}
	public char[] subArray(int from, int len)throws Exception  {

		char[] newA = new char[len - from];
		ArrayCopy(this.val, from, newA, 0, len);
		return newA;
	}


	public int getCapacity() {
		return this.val.length;
	}

	public int getPos() {
		return this.pos;
	}

	public char charAt(int inx) {
		return this.val[inx];
	}

	public void appendChar(char c) {

		if (getPos() < (getCapacity() - 1)) {
			this.val[this.pos++] = c;
			return;
		}
		char[] newA = ArrayFuncs
				.handleArraySize(this.val, (getPos() + 16));
		newA[this.pos++] = c;
		this.val = newA;
	}

	public void setValue(String src) throws Exception  {
		appendArr(src.toCharArray(), 0, 0);
	}

	public void setValue(CharArrV1 src) throws Exception {
		appendArr(src.toArray(), 0, 0);
	}

	public void setValue(char[] src) throws Exception {
		appendArr(src, 0, 0);
	}


	public void appendArr(CharArrV1 src, int srcFrom, int destFrom) throws Exception  {
		appendArr(src.toArray(), srcFrom, destFrom);
	}

	public void appendArr(char[] src) throws Exception  {
		appendArr(src, 0, getPos());
	}


	public void appendArr(char[] src, int srcFrom, int destFrom) throws Exception {

		int srcEndPos = src.length - 1; // there will not be any char on the
										// index =length
		// reach to the last non null char.
		for (; srcEndPos > 0; srcEndPos--)
			if (src[srcEndPos] != '\0')
				break;

		// if the from index falls in the null char area, just retain the
		// portion till the destination start and null out remaining portion and return.
		if (srcFrom > srcEndPos) {
			trimm(destFrom);
			return;
		}

		// if we need to copy the bytes from non-zero index, get the new length
		// by
		// subtracting the initial portion to be ignored.
		int len = srcEndPos - srcFrom + 1; // here 1 is added to adjust the
											// length subtracted earlier
		appendArr(src, srcFrom, destFrom, len);

	}

	public void appendArr(char[] src, int srcFrom, int destFrom, int len) throws Exception  {

		int i=len-1;
		for (;i>0;i--) {
			if (src[i] != '\0') break;
		}
		len = i+1;
		if (getCapacity() < (destFrom + len)) {
			char[] newA = new char[destFrom + len];
			ArrayCopy(this.val, 0, newA, 0, destFrom);
			ArrayCopy(src, srcFrom, newA, destFrom, len);
			this.val = newA;
		} else
			ArrayCopy(src, srcFrom, this.val, destFrom, len);

		this.pos = destFrom + len;
	//trimm();
	}

	public boolean equalTo(CharArrV1 right) {
		return equalTo(right.toArray());
	}
	public boolean equalTo(String str) {
		return equalTo(str.toCharArray());
	}
	public boolean equalTo(char[] right) {
		int m = right.length;
		int n = getPos();

		int i = 0;

		if (n < m)
			while (n-- != 0) {
				if (this.val[i] != right[i])
					return false;
				i++;
			}
		else
			while (m-- != 0) {
				if (this.val[i] != right[i])
					return false;
				i++;
			}
		return true;
	}



	public void loadString(String str, int inx) throws Exception {

		this.pos = inx + str.length();
		if (inx > 0) {
			char[] newA = new char[inx];
			ArrayCopy(this.val, 0, newA, 0, inx);
			this.val = new char[getPos()];
			ArrayCopy(newA, 0, this.val, inx, str.length());
		} else
			this.val = new char[str.length()];
		str.getChars(0, str.length(), this.val, 0);

	}

	public void loadString(String str) {
		this.pos = str.length();
		this.val = new char[str.length()];
		str.getChars(0, getPos(), this.val, 0);
	}

	public char[] toArray() {
		return this.val;
	}

	public static char[] int2CharArr(int num) {
		return Integer.toString(num).toCharArray();
	}

	public static char[] long2CharArr(long num) {
		return Long.toString(num).toCharArray();
	}

	public static char[] flaot2CharArr(float f) {
		return Float.toString(f).toCharArray();
	}

	public static char[] double2CharArr(double num) {
		return Double.toString(num).toCharArray();
	}

	public static char[]dec2CharArr(BigDecimal num)throws Exception  {
		return num.toString().toCharArray();
	}

	public static char[] bool2CharArr(boolean flag) throws Exception {

		if (flag) {
			char[] flg = { 't', 'r', 'u', 'e' };
			return flg;
		} else {
			char[] flg = { 'f', 'a', 'l', 's', 'e' };
			return flg;
		}
	}

	public int indexOf(char c) {
		return indexOf(c, 0);
	}

	public int indexOf(char c, int fromIndex) {
		return indexOf(c, 0, getPos());
	}

	public int indexOf(char c, int fromIndex, int toIndex) {

		if (fromIndex < 0)
			fromIndex = 0;
		/*
		 * if the current CharArry is null or Start index is > length of the
		 * current array, return -1
		 */
		if (getPos() <= 0 || fromIndex > getPos())
			return -1;

		if (getPos() <= toIndex)
			toIndex = getPos();
		int res = 0;
		for (res = fromIndex; res < toIndex; res++)
			if (this.val[res] == c)
				break;

		if (res == toIndex)
			return -1;
		return res;
	}

	public int lastIndexOf(char c) {
		return lastIndexOf(c, 0);
	}

	public int lastIndexOf(char c, int fromIndex) {
		return lastIndexOf(c, 0, getPos());
	}

	public int lastIndexOf(char c, int fromIndex, int toIndex) {

		if (fromIndex < 0)
			fromIndex = 0;
		/*
		 * if the current CharArry is null or Start index is > length of the
		 * current array, return -1
		 */
		if (getPos() <= 0 || fromIndex > getPos())
			return -1;

		if (getPos() <= toIndex)
			toIndex = getPos();
		int res = 0;
		for (res = toIndex - 1; res >= fromIndex; res--)
			if (this.val[res] == c)
				break;

		if (res < fromIndex)
			return -1;
		return res;
	}


    public int indexOfCharBeforeChar(char c, char endChar) {

        return indexOfCharBeforeChar(c, 0,endChar);
    }
    public int indexOfCharBeforeChar(char c, int fromIndex, char endChar) {
    	if (fromIndex < 0)
			fromIndex = 0;

    	/*
		 * if the current CharArry is null or Start index is > length of the
		 * current array, return -1
		 */
		if (getPos() <= 0 || fromIndex > getPos())
			return -1;

		int res=-1;

		for (int i=0;i<this.pos;i++) {
			if (this.val[i] == endChar) break;
			if (this.val[i] == c) break;
		}

		return res;
	}

    public int lastIndexOfCharBeforeChar(char c, char endChar) {

        return lastIndexOfCharBeforeChar(c, 0,endChar);
    }
    public int lastIndexOfCharBeforeChar(char c, int fromIndex, char endChar) {
    	if (fromIndex < 0)
			fromIndex = 0;

    	/*
		 * if the current CharArry is null or Start index is > length of the
		 * current array, return -1
		 */
		if (getPos() <= 0 || fromIndex > getPos())
			return -1;

		int res=-1;

		for (int i=0;i<this.pos;i++) {
			if (this.val[i] == endChar) break;
			if (this.val[i] == c) res=i;
		}

		return res;
	}



	public int occuranceCount(char c) {
		return occuranceCount(c, 0);
	}

	public int occuranceCount(char c, int fromIndex) {
		return occuranceCount(c, 0, getPos());
	}

	public int occuranceCount(char c, int fromIndex, int toIndex) {

		if (fromIndex < 0)
			fromIndex = 0;
		/*
		 * if the current CharArry is null or Start index is > length of the
		 * current array, return -1
		 */
		if (getPos() <= 0 || fromIndex > getPos())
			return -1;

		if (getPos() <= toIndex)
			toIndex = getPos();
		int res = 0;
		for (int i = toIndex - 1; i >= fromIndex; i--)
			if (this.val[i] == c)
				res++;
		return res;
	}

	public int indexOf(char[] srchFor) {
		return indexOf(srchFor, 0);
	}

	public int indexOf(char[] srchFor, int fromIndex) {
		return indexOf(srchFor, fromIndex, getPos());
	}

	public int indexOf(CharArrV1 srchFor) {
		return indexOf(srchFor, 0);
	}

	public int indexOf(CharArrV1 srchFor, int fromIndex) {
		return indexOf(srchFor, fromIndex, getPos());
	}

	public int indexOf(CharArrV1 srchFor, int fromIndex, int toIndex) {

		if (fromIndex > toIndex)
			return -1;

		final char[] arr = srchFor.toArray();
		return indexOf(arr, fromIndex, toIndex);
	}

	public int indexOf(String srchFor) {
		return indexOf(srchFor, 0);
	}

	public int indexOf(String srchFor, int fromIndex) {
		return indexOf(srchFor, fromIndex, getPos());
	}

	public int indexOf(String srchFor, int fromIndex, int toIndex) {
		if (fromIndex >= toIndex)
			return -1;
		return indexOf(srchFor.toCharArray(), fromIndex, toIndex);
	}

	public int indexOf(char[] srchFor, int fromIndex, int toIndex) {
		int targetCount = srchFor.length - 1;
		int targetOffset = 0;
		if (fromIndex < 0)
			fromIndex = 0;

		/*
		 * if the current CharArry is null or Start
		 * index is > length of the current array, return -1
		 */
		if (getPos() <= 0 ||
		    (fromIndex >= getPos() && targetCount > 0))
			return -1;
		// make sure we are looking only for non-null chars
		for (; targetCount > 0; targetCount--)
			if (srchFor[targetCount] != '\0')
				break;

		// Since the this.val can be modified by some other threads,
		// make a final copy of it before proceeding.
		final char[] source = new char[getPos()];
		System.arraycopy(this.val, 0, source, 0, getPos());

		char first = srchFor[targetOffset];
		int max = getPos() - targetCount;

		if (max >= toIndex)
			max = toIndex;

		for (int i = fromIndex; i < max; i++) {
			/* Look for first character. */
			if (source[i] != first) {
				while (++i <= max && source[i] != first)
					;
			}
			/* Found first character, now look at the rest of v2 */
			if (i <= max) {
				int j = i + 1; // points to the second character
				int end = j + targetCount - 1;
				for (int k = targetOffset + 1; j < end
						&& source[j] == srchFor[k]; j++, k++)
					;

				if (j == end) /* Found all chars. */
					return i;
			}
		}

		return -1;
	}

	private void ArrayCopy(char[] src, int srcPos, char[] dest, int destPos,
			int length) throws Exception {
		try {
		   System.arraycopy(src, srcPos, dest, destPos, length);
		}
		catch (Exception e) {
			ShowParentException(e);
			throw new Exception("Failed while executing System.arraycopy \n" +
		    		           "Source        :- Capacity : " + src.length +
		    		           " Start Position : " + srcPos +
		    		           " Length to Copy : " + length +
		    		           "\nDestination :- Capacity : " + dest.length +
		    		           " Start Position : " + destPos
		    		          );
		}
	}

	public void trimm() throws Exception {
		if (getCapacity() == getPos())
			return;
		char[] newA = new char[getPos()];
		ArrayCopy(this.val, 0, newA, 0, getPos());
		this.val = newA;
	}

	public void trimm(int pos) throws Exception {

		if (getCapacity() == pos)
			return;

		if (getPos() > pos) {
			char[] newA = new char[pos];
			ArrayCopy(this.val, 0, newA, 0, pos);
			this.pos = pos;
			this.val = newA;
		} else {
			char[] newA = new char[pos];
			ArrayCopy(this.val, 0, newA, 0, getPos());
			this.val = newA;
		}
	}

	public void clearBytes() {
		clearBytes(getPos());
	}

	public void clearBytesnResetPos(int pos) {
		clearBytes(pos);
		if (pos==0) this.pos=-1;
		this.pos = pos;
	}

	public void clearBytes(int pos) {
		for (; pos < getCapacity(); pos++)
			this.val[pos] = '\0';
	}

	public int getIntValue() {

		int res = 0;
		if (getPos() == 0) {
			throw new NumberFormatException("Can't parse to int. value:"
					+ getString());
		}

		int i = 0;
		boolean negative = false;
		switch (this.val[0]) {
		case '-':
			negative = true;
			// don't break continue..
		case '+':
			i = 1;
			if (getPos() == 1)
				throw new NumberFormatException("Can't parse to int. value:"
						+ getString());
			break;
		default:
			break;
		}
		int max = Integer.MIN_VALUE, limit = max / 10;
		int digit = 0;
		for (; i < getPos(); i++) {
			if (this.val[i] == ',')
				continue;
			if (this.val[i] == '.')
				break;
			if (this.val[i] < '0' || this.val[i] > '9')
				throw new NumberFormatException("Can't parse to int. value:"
						+ getString() + "Failed to parse the char @" + i + " ("
						+ this.val[i] + ")");
			if (res < limit) {
				if (negative)
					throw new NumberFormatException(
							"Can't parse to int. \n value:" + getString()
									+ ". Value smaller than Integer.MIN_VALUE");
				else
					throw new NumberFormatException(
							"Can't parse to int. \n value:" + getString()
									+ ". Value bigger than Integer.MAX_VAUE");
			}
			res *= 10;
			digit = ((int) this.val[i] & 0xF);
			if (res < max + digit) {
				if (negative)
					throw new NumberFormatException(
							"Can't parse to int. \n value:" + getString()
									+ ". Value smaller than Integer.MIN_VALUE"
									+ " res : " + res + "  max :" + max
									+ "  digit :" + digit

					);
				else
					throw new NumberFormatException(
							"Can't parse to int. \n value:" + getString()
									+ ". Value bigger than Integer.MAX_VAUE");
			}
			res -= digit;
		}
		return (negative ? res : -res);

	}

	public long getLongValue() {

		long res = 0L;
		if (getPos() == 0) {
			throw new NumberFormatException("Can't parse to long. value:"
					+ toString());
		}

		int i = 0, digit = 0;
		boolean negative = false;
		switch (this.val[i]) {
		case '-':
			negative = true;
			// don't break continue..
		case '+':
			i = 1;
			if (getPos() == 1)
				throw new NumberFormatException("Can't parse to long. value:"
						+ getString());
			break;
		default:
			break;
		}
		long max = Long.MIN_VALUE, limit = max / 10;
		for (; i < getPos(); i++) {
			if (this.val[i] == ',')
				continue;
			if (this.val[i] < '0' || this.val[i] > '9')
				throw new NumberFormatException("Can't parse to long. value:"
						+ getString() + "Failed to parse the char @" + i + " ("
						+ this.val[i] + ")");
			if (res < limit) {
				if (negative)
					throw new NumberFormatException(
							"Can't parse to long. \n value:" + getString()
									+ ". Value smaller than Long.MIN_VALUE");
				else
					throw new NumberFormatException(
							"Can't parse to long. \n value:" + getString()
									+ ". Value bigger than Long.MAX_VAUE");
			}
			digit = ((int) this.val[i] & 0xF);
			res *= 10;
			if (res < max + digit) {
				if (negative)
					throw new NumberFormatException(
							"Can't parse to long. \n value:" + getString()
									+ ". Value smaller than Long.MIN_VALUE");
				else
					throw new NumberFormatException(
							"Can't parse to long. \n value:" + getString()
									+ ". Value bigger than Long.MAX_VAUE");
			}
			res -= digit;
		}
		return (negative ? res : -res);

	}

	public float getFloatValue() {

		if (occuranceCount('.') > 1)
			throw new NumberFormatException(
					"Can't parse to float. More than one decimal point foundvalue:"
							+ getString() + " count: " + occuranceCount('.'));

		if (indexOf('E') > 0)
			return Float.parseFloat(getString());

		int i = 0;
		boolean negative = false;
		switch (this.val[i]) {
		case '-':
			negative = true;
			// don't break continue..
		case '+':
			i = 1;
			if (getPos() == 1)
				throw new NumberFormatException("Can't parse to float. value:"
						+ getString());
			break;
		default:
			break;
		}

		int loopEnd, decInx = indexOf('.');
		float intPart = 0.0F;

		// decide the loop end counter value
		if (decInx < 0)
			loopEnd = getPos();
		else
			loopEnd = decInx;

		// Get the float value of the integer part
		for (; i < loopEnd; i++) {
			if (this.val[i] == ',')
				continue;
			if (this.val[i] < '0' || this.val[i] > '9')
				throw new NumberFormatException("Can't parse to float. value:"
						+ getString() + "Failed to parse the char @" + i + " ("
						+ this.val[i] + ")");
			intPart = (intPart * 10.0F) + (float) ((int) this.val[i] & 0xF);
		}
		if (i != decInx && i < getPos())
			throw new NumberFormatException("Can't parse to flaot. value:"
					+ getString() + "Failed to parse the char @" + i + " ("
					+ this.val[i] + ")");
		else
			i++;
		// Get the float value of the
		float decPart = 0.0F;
		float divBy = 1F;
		for (; i < getPos(); i++) {
			if (this.val[i] < '0' || this.val[i] > '9')
				throw new NumberFormatException("Can't parse to long. value:"
						+ getString() + ". Failed to parse the char @" + i
						+ " (" + this.val[i] + ")");

			decPart = (decPart * 10.0F) + (float) ((int) this.val[i] & 0xF);
			divBy *= 10.0F;
		}

		float res = intPart + (decPart / divBy);
		return (negative ? -res : res);
	}

	public double getDoubleValue() {

		if (occuranceCount('.') > 1)
			throw new NumberFormatException(
					"Can't parse to double. More than one decimal point foundvalue:"
							+ getString() + " count: " + occuranceCount('.'));
		if (indexOf('E') > 0)
			return Double.parseDouble(getString());

		int i = 0;
		boolean negative = false;
		switch (this.val[i]) {
		case '-':
			negative = true;
			// don't break continue..
		case '+':
			i = 1;
			if (getPos() == 1)
				throw new NumberFormatException("Can't parse to double. value:"
						+ getString());
			break;
		default:
			break;
		}

		int loopEnd, decInx = indexOf('.');
		double intPart = 0.0;

		// decide the loop end counter value
		if (decInx < 0)
			loopEnd = getPos();
		else
			loopEnd = decInx;

		// Get the double value of the integer part
		for (; i < loopEnd; i++) {
			if (this.val[i] == ',')
				continue;
			if (this.val[i] < '0' || this.val[i] > '9')
				throw new NumberFormatException("Can't parse to long. value:"
						+ getString() + "Failed to parse the char @" + i + " ("
						+ this.val[i] + ")");
			intPart = (intPart * 10.0) + (double) ((int) this.val[i] & 0xF);
		}
		if (i != decInx && i < getPos())
			throw new NumberFormatException("Can't parse to long. value:"
					+ getString() + "Failed to parse the char @" + i + " ("
					+ this.val[i] + ")");
		else
			i++;
		// Get the double value of the
		double decPart = 0.00;
		double divBy = 1;
		for (; i < getPos(); i++) {
			if (this.val[i] < '0' || this.val[i] > '9')
				throw new NumberFormatException("Can't parse to long. value:"
						+ getString() + ". Failed to parse the char @" + i
						+ " (" + this.val[i] + ")");

			decPart = (decPart * 10.0) + (double) ((int) this.val[i] & 0xF);
			divBy *= 10.0;
		}

		double res = intPart + (decPart / divBy);
		return (negative ? -res : res);
	}

	public BigDecimal getDecimalValue() {
		return new BigDecimal(this.val, 0, getPos());
	}
    public boolean getBoolean() throws Exception {

		if ((this.val[0] == 't') &&
		    (this.val[1] == 'r') &&
		    (this.val[2] == 'u') &&
		    (this.val[3] == 'e'))
			 return true;
		else
		   if ((this.val[0] == 'f') &&
			   (this.val[1] == 'a') &&
			   (this.val[2] == 'l') &&
			   (this.val[3] == 's') &&
			   (this.val[4] == 'e'))
			 return false;
       throw new Exception("Value :" + getString() +
    		                     " Can't be parsed to boolean");
	}
	public static void main(String arg[]) throws Exception {

		CharArrV1 source = new CharArrV1("ABCDEFGHIJKLMNOPQRSTUVWXYZA");
		CharArrV1 tgt = new CharArrV1("12345");
		System.out.println(source.indexOf('A', 0, 27));
		System.out.println(source.lastIndexOf('A', 0, 27) + "   "+ source.occuranceCount('A', 0, 27));
		System.out.println(source.indexOf("EFG", 0));
		System.out.println(source.indexOf(tgt, 21, 26));

		source.replace(tgt.toArray(), 0,5);
		System.out.println("source after Replace  >" + source.getString() + "<.  New Pos =" + source.getPos());


		CharArrV1 LONG = new CharArrV1("-9,223,37203,685,4775,808"); // 9223372036854775807
																	// Main
																	// :-9223372036854775808
		System.out.println("for Long: " + LONG.getLongValue());

		CharArrV1 INT = new CharArrV1("-214748.3648"); // Max :2147483647 Main
													// :-2147483648
		System.out.println("for Int: " + INT.getIntValue());

		CharArrV1 decimal = new CharArrV1("99919191469999987.99999999");
		System.out.println("for BigDecimal: " + decimal.getDecimalValue()
				+ " testing "
				+ (decimal.getDecimalValue().add(decimal.getDecimalValue())));

		CharArrV1 FLOAT = new CharArrV1("419191469999999");
		System.out
				.println("for Flaot: "
						+ FLOAT.getFloatValue()
						+ " Value after parsing : "
						+ Float.parseFloat(FLOAT.getString())
						+ "  Difference :"
						+ (FLOAT.getFloatValue() - Float.parseFloat(FLOAT
								.getString())));

		CharArrV1 DOUBLE = new CharArrV1("99919191469999987");
		System.out.println("for Double: "
				+ DOUBLE.getDoubleValue()
				+ " Value after parsing : "
				+ Double.parseDouble(DOUBLE.getString())
				+ "  Difference :"
				+ (DOUBLE.getDoubleValue() - Double.parseDouble(DOUBLE
						.getString())));

		System.out
				.println("for Double: " + (new Integer(10) + new Integer(10)));
	}

	public char getChar(int i) {
		return this.val[i];
	}

	public void put(int inx, char c) {
		this.val[inx] = c;
	}

	public void replaceBlock(char[] replaceBy, int BlockstartByte, int BlockEndByte) throws Exception {
	try{
	   //identify the last non null car position.
		int repLen = replaceBy.length-1;
		for (;repLen>0;repLen--)
			if (replaceBy[repLen] != '\0') break;

      //If we are replacing a bigger chunk with small chunk,
		if (repLen < (BlockEndByte- BlockstartByte)) {

			int loopLim = BlockstartByte + repLen;
			for (int i=BlockstartByte+1;i <= loopLim;i++) {
				this.val[i] = replaceBy[i-BlockstartByte];
			}
			loopLim++;
			int savedBytesLen = BlockEndByte - loopLim;
			for (;;loopLim++) {
				if ((loopLim+savedBytesLen) == getPos()) break;
				this.val[loopLim] = this.val[loopLim+savedBytesLen];
			}
			trimm(loopLim);
			this.pos = loopLim;
			return;
		}
	  //We are replacing small chunk in the source by a bigger chunk
	    int newLen = this.pos + repLen - (BlockEndByte- BlockstartByte);
	    char[] newA = new char[newLen];
	    ArrayCopy(this.val, 0, newA, 0, BlockstartByte);
	    ArrayCopy(replaceBy, 0, newA,BlockstartByte+1,repLen);
	    ArrayCopy(this.val, BlockEndByte+1, newA,BlockstartByte+1+repLen, (getPos()-(BlockEndByte+1)));
	    this.val = newA;
	    this.pos = newLen;
	}
	    catch (Exception e) {
		      System.err.println("Failed to replace characters"
				          +"in Source:" + getString()
				          +"\nFrom Byte :" + BlockstartByte + ". Value :" + charAt(BlockstartByte)
				          + ". BlockEnd Byte :" + BlockEndByte + ". Value :" + charAt(BlockEndByte)
				          +"ReplaceBy:" + new String(replaceBy)
				          );
		       throw new Exception(e.toString());
	    }

	}
	public void replace(char[] replaceBy, int from, int len) throws Exception {

		   try{
		   //identify the last non null car position.
			int repLen = replaceBy.length-1;
			for (;repLen>0;repLen--)
				if (replaceBy[repLen] != '\0') break;

	      //If we are replacing a bigger chunk with small chunk,
			if (repLen < len) {

				int loopLim = from + repLen;
				for (int i=from;i <= loopLim;i++) {
					this.val[i] = replaceBy[i-from];
				}
				int savedBytesLen = len - loopLim;
				loopLim++;
				for (;;loopLim++) {
					if ((loopLim+savedBytesLen) == getPos()) break;
					this.val[loopLim] = this.val[loopLim+savedBytesLen];
				}
				trimm(loopLim);
				this.pos = loopLim;
				return;
			}
		  //We are replacing small chunk in the source by a bigger chunk
		    int newLen = this.pos + repLen - len;
		    char[] newA = new char[newLen];
		    ArrayCopy(this.val, 0, newA, 0, from);
		    ArrayCopy(replaceBy, 0, newA,from+1,repLen);
		    ArrayCopy(this.val, from+len, newA,from+1+repLen, (getPos()-(len+1)));
		    this.val = newA;
		    this.pos = newLen;
		}
		catch (Exception e) {
			ShowParentException(e);
			throw new Exception("Failed to replace characters"
					          +"in Source:" + getString()
					          +"\nFrom Byte :" + from + ". Value :" + charAt(from) + ". Len :" + len
					          +"ReplaceBy:" + new String(replaceBy)
					          );
		}
	   }

	private void ShowParentException(Exception e) {
		   System.err.println(e.getMessage());
		   e.printStackTrace(System.err);
	}


}
