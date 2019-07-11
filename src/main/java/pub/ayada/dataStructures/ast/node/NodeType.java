package pub.ayada.commons.ds.ast.node;

import java.io.Serializable;

public enum NodeType implements Cloneable, Serializable{
	LITERAL_STR,
	LITERAL_NUM,
	LITERAL_CHR,
	LITERAL_BOOL,
	LITERAL_DATE,
	GROUP,
	VARIABLE,
	OPERATOR,
	OPERATOR_UNARY,
	FUNCTION,
	EXPRESSION,
	UNKNOWN;
}
