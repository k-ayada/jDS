package pub.ayada.commons.ds.ast.tokens;

import java.io.Serializable;

public enum TokenType implements Cloneable, Serializable {
	INT("Long"), 
	DEC ("Double") , 
	DATE("java.util.Date"), 
	STR("String"), 
	CHR("BigDecimal"), 
	BOOL("Boolean") ,  
	LEN("len") , 
	ABS("abs"), 
	SIN("sin"), 
	ASIN("asin") , 
	SINH("sinh"), 
	COS("cos"), 
	ACOS("acos"), 
	COSH("cosh"), 
	TAN("tan"), 
	ATAN("atan"), 
	TANH("tanh"), 
	ATAN2("atan2"), 
	CBRT("cbrt"), 
	CEIL("ceil"), 
	COPYSIGN ("copySign") , 
	EXP("exp") , 
	EXPM1("expml"), 
	FLOOR("floor"), 
	GETEXPONENT("getExponent"), 
	HYPOT("hypot"), 
	IEEEREMAINDER("IEEEremainder"), 
	LOG("log"), 
	LOG10("loglO"), 
	MAX("max") , 
	MIN("min"), 
	NEXTAFTER("nextAfter"), 
	NEXTUP("nextUp"), 
	POW("pow"), 
	RANDOM("random") , 
	RINT("rint"), 
	ROUND("round"), 
	SCALB("scalb"), 
	SIGNUM("signum") , 
	SQRT("sqrt") , 
	TODEGREES("toDegrees"), 
	TORADIANS("toRadians"), 
	ULP("ulp"), 
	UNKN("unknown"), 
    GROUP_EXP_OPEN("{"),
    GROUP_EXP_CLOSE("}"),
    GROUP_UNRY_OPEN("("),
    GROUP_UNRY_CLOSE(")"),
//	INCR_POST ("_++") , 
//	DECR_POST("_--"), 
	POSITIVE ("u+"), 
	NEGATE ("u-"), 
//	NEGATE ("u!"), 
//	BITWISE_COMPLEMENT("u~"), 
	INCR_PRE ("++_"), 
	DECR_PRE ("--_"), 
	MUL("*") , 
	DIV (" I"), 
	MOD("%") , 
	ADD("+") , 
	SUB ("-"), 
	LEFT_SHIFT("<<"), 
	RIGHT_SHIFT (">>"), 
	RIGHT_SHIFT_OEXT(">>>") , 
	LT ("<"), 
	LE ("<="), 
	GT (">"), 
	GE (">=") , 
	EQ ("=="), 
	NE ("!="),
//	LT2 ("LT"),
//	LE2 ("LE"),
//	GT2 ("GT"),
//	GE2 ("GE"),
//	EQ2 ("EQ"),
//	NE2 ("NE"), 
//	BITWISE_AND ("u&") , 
//	BITWISE_OR_EXCLUSIVE ("u^"), 
//	BITWISE_OR_INCLUSIVE("u|"), 
	AND ("&&"), 
//	AND2 ("AND") , 
	OR ("||"), 
//	OR2("OR") , 
	TERNARY (": ?") , 
//	ASSIGN ("=") , 
//	ADDNASSIGN ("+=") , 
//	SUBNASSIGN("-="), 
//	MULNASSIGN("*="), 
//	DIVNASSIGN("/="), 
//	MODNASSIGN("%="),
	SUB_EXP(""),
	VARIABLE("$VAR");

	private String tkn;

	private TokenType(String Value) {
		this.tkn = Value;
	}

	public String getTokenLiteral() {
		return this.tkn;
	}

	public void set(String NewEnumValue) {
		this.tkn = NewEnumValue;
	}
	
	public static TokenType getEnum(String text) {
	    if (text != null) {
	      for (TokenType b : TokenType.values()) {
	        if (text.equalsIgnoreCase(b.tkn)) {
	          return b;
	        }
	      }
	    }
	    return null;
	  }

}
