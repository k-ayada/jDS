package pub.ayada.commons.ds.ast;

import java.io.Serializable;
import java.util.ArrayList;

import pub.ayada.commons.ds.ast.node.Node;
import pub.ayada.commons.ds.ast.node.NodeType;
import pub.ayada.commons.ds.ast.tokens.Token;
import pub.ayada.commons.ds.ast.tokens.TokenType;
import pub.ayada.commons.utils.StringUtils;

public class ExpTokanizer implements Cloneable, Serializable {
	private static final long serialVersionUID = 5662729281152519242L;
	private ArrayList<String> operatorList = new ArrayList<String>();
	private ArrayList<Node<Token<?>>> operandsList = new ArrayList<Node<Token<?>>>();
	private ArrayList<Node<Token<?>>> tokens = new ArrayList<Node<Token<?>>>();

	private int inx = 0, max = 0;
	private int openBr = 0, openFlrBr = 0;
	private String exp;

	public ExpTokanizer(String ExprString) {
		this.exp = ExprString;
		this.max = this.exp.length();
		BuildExpTokens();
	}

	public ArrayList<String> getOperatorList() {
		return operatorList;
	}

	public String getNextOperator() {
		return this.operatorList.remove(0);
	}

	public ArrayList<Node<Token<?>>> getOperandsList() {
		return operandsList;
	}

	public ArrayList<Node<Token<?>>> getExpTokens() {
		return tokens;
	}

	public String printOperandsList() {
		// return operandsList;

		StringBuilder sb = new StringBuilder("[");
		for (Node<Token<?>> node : this.operandsList) {
			sb.append(node.getValue().getValue() + ":"
					+ node.getValue().getTokenTyp() + " , ");
		}

		sb.setLength(sb.length() - 3);
		return sb.append("]").toString();
	}

	public String printExpTokens() {
		// return operandsList;

		StringBuilder sb = new StringBuilder("[\n");
		for (Node<Token<?>> node : this.tokens) {
			sb.append("  "
					+ (node.getNodeType() + StringUtils.repeat(" ", 15))
							.substring(0, 15)
					+ " :  "
					+ (node.getValue().getTokenTyp() + StringUtils.repeat(" ",
							15)).substring(0, 15)
					+ " : '"
					+ (node.getValue().getValue() + "'" + StringUtils.repeat(
							" ", 30)).substring(0, 30) + "\n");
		}

		sb.setLength(sb.length() - 1);
		return sb.append("\n]").toString();
	}

	public Node<Token<?>> getNextOperand() {
		return this.operandsList.remove(0);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void BuildExpTokens() {
		while (true) {
			// Break the parsing if we have crossed the expression length.
			if (this.inx >= this.max)
				break;
			if (Character.isWhitespace(this.exp.charAt(this.inx))) {
				this.inx++;
				continue;
			}
			Token<String> opTkns = null;
			switch (getCurChar()) {
			case '{':
				operatorList.add("{");
				opTkns = new Token<String>("{", TokenType.getEnum("{"));
				tokens.add(new Node(opTkns, NodeType.GROUP));
				openFlrBr++;
				break;
			case '}':// If count of open brackets is zero or -ve, its an
						// unbalanced expression.
				if (openFlrBr <= 0) {
					throw new RuntimeException("Unbalanced expression. " 
							 + "Failed to determine the '(' curresponding to ')' @ "
						 	 + this.inx + " - " + openBr);
				}
				openFlrBr--;
				operatorList.add("}");
				opTkns = new Token<String>("}", TokenType.getEnum("}"));
				tokens.add(new Node(opTkns, NodeType.GROUP));
				break;
			case 't':
				if (this.inx + 3 <= this.max
						&& this.exp.charAt(this.inx + 1) == 'r'
						&& this.exp.charAt(this.inx + 2) == 'u'
						&& this.exp.charAt(this.inx + 3) == 'e') {
					this.inx += 3;
					Token<Boolean> tkTrue = new Token<Boolean>(true,
							TokenType.BOOL);
					operandsList.add(new Node<Token<?>>(tkTrue,
							NodeType.LITERAL_BOOL));
					tokens.add(new Node<Token<?>>(tkTrue, NodeType.LITERAL_BOOL));
				} else
					throw new RuntimeException(
							"Failed to parse the boolena operand as true @ "
									+ this.inx);
				break;
			case 'f':
				if ((this.inx + 4) <= this.max
						&& this.exp.charAt(this.inx + 1) == 'a'
						&& this.exp.charAt(this.inx + 2) == 'l'
						&& this.exp.charAt(this.inx + 3) == 's'
						&& this.exp.charAt(this.inx + 4) == 'e') {
					this.inx += 4;
					Token<Boolean> tkFalse = new Token<Boolean>(false,
							TokenType.BOOL);
					operandsList.add(new Node<Token<?>>(tkFalse,
							NodeType.LITERAL_BOOL));
					tokens.add(new Node<Token<?>>(tkFalse,
							NodeType.LITERAL_BOOL));
				} else
					throw new RuntimeException(
							"Failed to parse the boolena operand as false @ "
									+ this.inx);
				break;
			case '(':
				operatorList.add("(");
				openBr++;
				opTkns = new Token<String>("(", TokenType.getEnum("("));
				tokens.add(new Node(opTkns, NodeType.GROUP));
				// Marks the end of Block.
				break;
			case ')':// If count of open brackets is zero or -ve, its an
						// unbalanced expression.
				if (openBr <= 0) {
					throw new RuntimeException("Unbalanced expression. " 
				              + "Failed to determine '(' curresponding to ')' @ "
							  + this.inx + " - " + openBr);
				}
				openBr--;
				operatorList.add(")");
				opTkns = new Token<String>(")", TokenType.getEnum(")"));
				tokens.add(new Node(opTkns, NodeType.GROUP));
				break;
			case '+':
				if (this.exp.charAt(this.inx + 1) == '+') {
					if (getPrevValidChar(this.inx) == '(') {
						operatorList.add("++_");
						opTkns = new Token<String>("++_",
								TokenType.getEnum("++_"));
						tokens.add(new Node(opTkns, NodeType.OPERATOR_UNARY));
						this.inx++;
					} else if (getNxtValidChar(this.inx + 1) == ')') {
						operatorList.add("_++");
						opTkns = new Token<String>("_++",
								TokenType.getEnum("_++"));
						tokens.add(new Node(opTkns, NodeType.OPERATOR_UNARY));
						this.inx++;
					}
				} else if (getPrevValidChar(this.inx) == '(') {
					operatorList.add("u+");
					opTkns = new Token<String>("u+",
							TokenType.getEnum("u+"));
					tokens.add(new Node(opTkns, NodeType.OPERATOR_UNARY));
				} else {
					operatorList.add("+");
					opTkns = new Token<String>("+", TokenType.getEnum("+"));
					tokens.add(new Node(opTkns, NodeType.OPERATOR));
				}

				break;
			case '*':
			case '/':				
				String oper = String.valueOf(getCurChar());
				opTkns = new Token<String>(oper,TokenType.getEnum(oper));
						tokens.add(new Node(opTkns, NodeType.OPERATOR));
						this.inx++;
				break;
			case '-':
				if (this.exp.charAt(this.inx + 1) == '-') {
					if (getPrevValidChar(this.inx) == '(') {
						operatorList.add("--_");
						opTkns = new Token<String>("u-",
								TokenType.getEnum("--_"));
						tokens.add(new Node(opTkns, NodeType.OPERATOR_UNARY));
						this.inx++;
					} else if (getNxtValidChar(this.inx + 1) == ')') {
						operatorList.add("_--");
						opTkns = new Token<String>("_--",
								TokenType.getEnum("_--"));
						tokens.add(new Node(opTkns, NodeType.OPERATOR_UNARY));
						this.inx++;
					}
				} else if (getPrevValidChar(this.inx) == '(') {
					operatorList.add("u-");
					opTkns = new Token<String>("u-",
							TokenType.getEnum("u-"));
					tokens.add(new Node(opTkns, NodeType.OPERATOR_UNARY));
				} else {
					operatorList.add("-");
					opTkns = new Token<String>("-", TokenType.getEnum("-"));
					tokens.add(new Node(opTkns, NodeType.OPERATOR));
				}

				break;
			case '&':
				if (this.exp.charAt(this.inx + 1) == '&') {
					operatorList.add("!=");
					opTkns = new Token<String>("&&",
							TokenType.getEnum("&&"));
					tokens.add(new Node(opTkns, NodeType.OPERATOR));
					this.inx++;
				} else
					throw new RuntimeException("Expected & but found '"
							+ this.exp.charAt(this.inx + 1) + "' @ "
							+ (this.inx + 1));
				break;
			case '|':
				if (this.exp.charAt(this.inx + 1) == '|') {
					operatorList.add("!=");
					opTkns = new Token<String>("||",
							TokenType.getEnum("||"));
					tokens.add(new Node(opTkns, NodeType.OPERATOR));
					this.inx++;
				} else
					throw new RuntimeException("Expected '|' but found '"
							+ this.exp.charAt(this.inx + 1) + "' @ "
							+ (this.inx + 1));
				break;

			case '!':
				if (getPrevValidChar(this.inx) == '(') {
					operatorList.add("u!");
					opTkns = new Token<String>("u!",
							TokenType.getEnum("u!"));
					tokens.add(new Node(opTkns, NodeType.OPERATOR_UNARY));
					this.inx++;
				} else if (this.exp.charAt(this.inx + 1) == '=') {
					operatorList.add("!=");
					opTkns = new Token<String>("!=",
							TokenType.getEnum("!="));
					tokens.add(new Node(opTkns, NodeType.OPERATOR));
					this.inx++;
				}

				break;
			case '<':
				if (this.exp.charAt(this.inx + 1) == '=') {
					operatorList.add("<=");
					opTkns = new Token<String>("<=",
							TokenType.getEnum("<="));
					this.inx++;
				} else if (this.exp.charAt(this.inx + 1) == '<') {
					operatorList.add("<<");
					opTkns = new Token<String>("<=",
							TokenType.getEnum("<<"));
					this.inx++;
				} else {
					operatorList.add("<");
					opTkns = new Token<String>("<", TokenType.getEnum("<"));
				}
				tokens.add(new Node(opTkns, NodeType.OPERATOR));
				break;
			case '>':
				if (this.exp.charAt(this.inx + 1) == '=') {
					operatorList.add(">=");
					opTkns = new Token<String>("<=",
							TokenType.getEnum(">="));
					this.inx++;
				} else if (this.exp.charAt(this.inx + 1) == '>') {
					if (this.exp.charAt(this.inx + 2) == '>') {
						operatorList.add(">>>");
						opTkns = new Token<String>(">>>",
								TokenType.getEnum(">>>"));
						this.inx++;
					} else {
						operatorList.add(">>");
						opTkns = new Token<String>(">>",
								TokenType.getEnum(">>"));
					}
					this.inx++;

				} else {
					operatorList.add("<");
					opTkns = new Token<String>("<", TokenType.getEnum("<"));
				}
				tokens.add(new Node(opTkns, NodeType.OPERATOR));
				break;
			case '=':
				if (this.exp.charAt(this.inx + 1) == '=') {
					operatorList.add("==");
					opTkns = new Token<String>("==",
							TokenType.getEnum("=="));
					this.inx++;
				} else {
					throw new RuntimeException(
							"Failed to parse the expression after '=' @ "
									+ this.inx);
				}
				tokens.add(new Node(opTkns, NodeType.OPERATOR));
				break;
			case '~':
				if (getPrevValidChar(this.inx) == '(') {
					operatorList.add("u~");
					opTkns = new Token<String>("u~",
							TokenType.getEnum("u~"));
					this.inx++;
				}
				tokens.add(new Node(opTkns, NodeType.OPERATOR));
				break;
			case '$':
				handleVariable();
				this.inx--;
				break;
			case '1':
			case '2':
			case '3':
			case '4':
			case '5':
			case '6':
			case '7':
			case '8':
			case '9':
			case '0':
				handleNumericLiteral();
				this.inx--;
				break;
			case '"':
				handleStrigLiteral();
				this.inx--;
				break;
			case '@':
				handleFunctionCall();
				break;
			default:
				throw new RuntimeException("Unknow char '" + getCurChar()
						+ "' @ " + this.inx);
			}
			this.inx++;
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void handleFunctionCall() {
		StringBuilder func = new StringBuilder();
		Token<String> opTkns = null;
		func.append('@');
		this.inx++;
		while (true) {
			if (getCurChar() == '(')
				break;
			func.append(getCurChar());
			this.inx++;
		}
		operatorList.add(func.toString());
		opTkns = new Token<String>(func.toString(), TokenType.getEnum(func
				.substring(1)));
		tokens.add(new Node(opTkns, NodeType.FUNCTION));

		func.setLength(0); // reuse the buffer.
		this.inx++;
		while (true) {
			if (getCurChar() == ')')
				break;
			func.append(getCurChar());
			this.inx++;
		}
		
        opTkns = new Token<String>(func.toString(), TokenType.SUB_EXP);
    	operandsList.add(new Node<Token<?>>(opTkns, NodeType.EXPRESSION));
    	tokens.add(new Node<Token<?>>(opTkns, NodeType.EXPRESSION));
	}

	private void handleStrigLiteral() {
		StringBuilder str = new StringBuilder();
		this.inx++;
		while (true) {
			if (this.inx >= this.max || getCurChar() == '"')
				break;
			if (getCurChar() == '\\') {
				switch (peekNextChar()) {
				case 't':
					str.append('\t');
					this.inx++;
					break;
				case 'r':
					str.append('\r');
					this.inx++;
					break;
				case 's':
					str.append(' ');
					this.inx++;
					break;
				case 'n':
					str.append('\n');
					this.inx++;
					break;
				case 'b':
					str.append('\b');
					this.inx++;
					break;
				case 'f':
					str.append('\f');
					this.inx++;
					break;
				case '\\':
					str.append('\\');
					this.inx++;
					break;
				case 'u':
					StringBuilder unicode = new StringBuilder(4);
					int Next4 = this.inx + 6;
					for (this.inx = this.inx + 2; this.inx < Next4; this.inx++) {
						unicode.append(getCurChar());
					}
					int value = Integer.parseInt(unicode.toString(), 0x10);
					str.append((char) value);
					break;
				default:
					break;
				}
			} else {
				str.append(getCurChar());
				this.inx++;
			}
		}

		Token<String> opTkns = new Token<String>(str.toString(),
				TokenType.STR);
		operandsList.add(new Node<Token<?>>(opTkns, NodeType.LITERAL_STR));
		tokens.add(new Node<Token<?>>(opTkns, NodeType.EXPRESSION));
		this.inx++;

	}

	private void handleNumericLiteral() {
		StringBuilder num = new StringBuilder();
		num.append(getCurChar());
		this.inx++;
		boolean hasDecimal = false;
		while (this.inx <= this.max) {
			char c = getCurChar();
			if (Character.isDigit(c)) {
			} else if (c == '.') {
				hasDecimal = true;
			} else if (c == 'e' || c == 'E') {
				hasDecimal = true;
			} else
				break;

			num.append(getCurChar());
			this.inx++;
		}

		if (hasDecimal) {
			Double val = new Double(Double.parseDouble(num.toString()));
			Token<Double> tk = new Token<Double>(val, TokenType.DEC);
			operandsList.add(new Node<Token<?>>(tk, NodeType.LITERAL_NUM));
			tokens.add(new Node<Token<?>>(tk, NodeType.LITERAL_NUM));
		} else {
			Long val = new Long(Long.parseLong(num.toString()));
			Token<Long> tk = new Token<Long>(val, TokenType.INT);
			operandsList.add(new Node<Token<?>>(tk, NodeType.LITERAL_NUM));
			tokens.add(new Node<Token<?>>(tk, NodeType.LITERAL_NUM));
		}
	}

	private void handleVariable() {
		if (!(Character.isAlphabetic(peekNextChar())) || '_' == peekNextChar()) {
			throw new RuntimeException(
					"Unbalanced expression. Variable name should start with either a valid alphabet or _ but found '"
							+ peekNextChar() + " @ " + (this.inx + 1));
		}
		StringBuilder var = new StringBuilder();
		var.append('$');
		var.append(peekNextChar());
		this.inx += 2;
		while (true) {
			if (this.inx >= this.max
					|| !(Character.isAlphabetic(getCurChar())
							|| Character.isDigit(getCurChar()) || '_' == getCurChar()))
				break;
			var.append(getCurChar());
			this.inx++;
		}
		Token<String> tk = new Token<String>(var.toString(),
				TokenType.VARIABLE);
		operandsList.add(new Node<Token<?>>(tk, NodeType.VARIABLE));

		tokens.add(new Node<Token<?>>(tk, NodeType.VARIABLE));

	}

	private char getPrevValidChar(int inx) {
		char c = this.exp.charAt(--inx);
		while (true) {
			if (!Character.isWhitespace(c))
				break;
			c = this.exp.charAt(--inx);
		}
		return c;
	}

	private char getNxtValidChar(int inx) {
		char c = this.exp.charAt(++inx);
		while (true) {
			if (!Character.isWhitespace(c))
				break;
			c = this.exp.charAt(++inx);
		}
		return c;
	}

	private char getCurChar() {

		if (this.inx < this.exp.length())
			return this.exp.charAt(this.inx);
		else
			return '\0';
	}

	private char peekNextChar() {
		return this.exp.charAt(this.inx + 1);
	}
}
