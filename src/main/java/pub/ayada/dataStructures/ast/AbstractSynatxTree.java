package pub.ayada.commons.ds.ast;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Stack;
import java.util.TreeMap;

import pub.ayada.commons.ds.ast.node.Node;
import pub.ayada.commons.ds.ast.node.NodeType;
import pub.ayada.commons.ds.ast.tokens.Token;
import pub.ayada.commons.ds.ast.tokens.TokenType;
import pub.ayada.commons.utils.StringUtils;

public class AbstractSynatxTree implements Cloneable, Serializable {

	private static final long serialVersionUID = -1869691687943008980L;

	private int tknInx;
	private int nodeCount;
	private int calcCount;
	private Node<Token<?>> root;
	private boolean debug = false;
	private ArrayList<Node<Token<?>>> expTkns;

	private Stack<Node<Token<?>>> operatorStack = new Stack<>();
	private Stack<Node<Token<?>>> operandStack = new Stack<>();

	private TreeMap<String, Node<Token<?>>> NodenMap = new TreeMap<>();
	private TreeMap<String, Token<?>> varMap = new TreeMap<>();

	public AbstractSynatxTree() {
	}

	public AbstractSynatxTree(String Expr, boolean withDebug) {
		this.debug = withDebug;
		expTkns = new ExpTokanizer(Expr).getExpTokens();
		this.root = BuildAST();
		if (isDebug())
			reportTtreeStructure();
	}

	public AbstractSynatxTree(ArrayList<Node<Token<?>>> ExpTkns) {
		expTkns = ExpTkns;
		this.root = BuildAST();
		if (isDebug())
			reportTtreeStructure();
		// this.firstLeaf = reachtoFirstLeaf(this.root);
		// inOrder();

	}

	public boolean isDebug() {
		return debug;
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	public void setINT(String key, Number value) {
		Token<?> tkn = new Token<Long>((Long) value, TokenType.INT);
		this.varMap.put(key, tkn);
	}

	public void setDEC(String key, Number value) {
		Token<Double> tkn = new Token<>(value.doubleValue(), TokenType.DEC);
		this.varMap.put(key, tkn);
	}

	public void setSTR(String key, String value) {
		Token<String> tkn = new Token<>(value, TokenType.STR);
		this.varMap.put(key, tkn);
	}

	public void setCHR(String key, Character value) {
		Token<Character> tkn = new Token<>(value, TokenType.CHR);
		this.varMap.put(key, tkn);
	}

	public void setDATE(String key, Date value) {
		Token<Date> tkn = new Token<>(value, TokenType.DATE);
		this.varMap.put(key, tkn);
	}

	public void setBOOL(String key, Boolean value) {
		Token<Boolean> tkn = new Token<>(value, TokenType.BOOL);
		this.varMap.put(key, tkn);
	}

	public TreeMap<String, Node<Token<?>>> getNodenMap() {
		return NodenMap;
	}

	private TreeMap<String, Token<?>> getVarMaps() {
		return this.varMap;
	}

	private TreeMap<String, Node<Token<?>>> getNodeMap() {
		return this.NodenMap;
	}

	private Node<Token<?>> getRootNode() {
		return this.root;
	}

	public ArrayList<String> getVariableList() {
		Set<Entry<String, Token<?>>> it = this.varMap.entrySet();
		ArrayList<String> vars = new ArrayList<>(this.varMap.size());
		for (Entry<String, Token<?>> x : it) {
			vars.add(x.getKey());
		}
		return vars;
	}

	public void printVaribleValues() {
		Set<Entry<String, Token<?>>> it = this.varMap.entrySet();
		StringBuilder sb = new StringBuilder("Variables  :- [  ");
		for (Entry<String, Token<?>> x : it) {
			sb.append("'"
					+ x.getKey()
					+ "':'"
					+ (x.getValue() == null ? "(null)" : x.getValue()
							.getValue()) + "' , ");
		}
		sb.setLength(sb.length() - 2);
		System.out.println(sb.toString() + "  ] \n");
	}

	private Node<Token<?>> BuildAST() {

		this.tknInx = -1;
		int tknCnt = this.expTkns.size();

		while (true) {

			// Break the loop if we reached the end of tokens list.
			if (++this.tknInx >= tknCnt)
				break;
			// Get the next token from the list.
			Node<Token<?>> tkn = this.expTkns.get(this.tknInx);

			// Current token is a GROUP operator :- {, }, ( , )
			if (tkn.getNodeType() == NodeType.GROUP) {
				handleGroup(tkn);
			}
			// Current token is an UNARY operator :- u+, u-
			else if (tkn.getNodeType() == NodeType.OPERATOR_UNARY) {
				throw new RuntimeException(
						"Failed to parse the expression. Unary operation '"
								+ tkn.getValue().toString().substring(1)
								+ "' must be enclosed within '(' ')'");
			}
			// Current token is a binary operator
			else if (tkn.getNodeType() == NodeType.OPERATOR) {
				handleOperator(tkn);
			}
			// Current token is either a literal or a variable
			else if (tkn.getNodeType() == NodeType.LITERAL_STR
					|| tkn.getNodeType() == NodeType.LITERAL_NUM
					|| tkn.getNodeType() == NodeType.LITERAL_BOOL
					|| tkn.getNodeType() == NodeType.LITERAL_DATE
					|| tkn.getNodeType() == NodeType.LITERAL_CHR) {
				this.operandStack.push(tkn); // push the group start operator
			}
			// Current token is a variable
			else if (tkn.getNodeType() == NodeType.VARIABLE) {
				String key = tkn.getValue().getValue().toString();
				if (!this.varMap.containsKey(key))
					this.varMap.put(key, new Token<Character>(null,
							TokenType.UNKN));
				this.operandStack.push(tkn); // push the group start operator
			}
			// Current token is a java function
			else if (tkn.getNodeType() == NodeType.FUNCTION) {
				handleFunction(tkn, this.expTkns.get(++this.tknInx));
			} else if (tkn.getValue().getTokenTyp() == TokenType.STR) {
				this.operandStack.push(tkn);
			}
			// Current token type is unknown...
			else {
				throw new RuntimeException("Unknown token '"
						+ tkn.getValue().toString() + " of type '"
						+ tkn.getNodeType() + ":" + ""
						+ tkn.getValue().getTokenTyp() + "found.");
			}
			// if (isDebug()) printStack();
		}
		while (!operatorStack.empty()) {
			resolveGroup2AST();
			// if (isDebug()) printStack();

		}
		return this.operandStack.pop();
	}

	private void resolveGroup2AST() {
		Node<Token<?>> operNode = null;
		while (true) {
			if (this.operatorStack.empty())
				break;
			operNode = this.operatorStack.pop();

			if (operNode.getValue().getTokenTyp() == TokenType.GROUP_EXP_OPEN)
				break;

			Node<Token<?>> rChld = this.operandStack.pop();
			Node<Token<?>> lChld = this.operandStack.pop();
			if (rChld.getNodeType() == lChld.getNodeType()
					&& (lChld.getNodeType() == NodeType.LITERAL_NUM
							|| lChld.getNodeType() == NodeType.LITERAL_STR || lChld
							.getNodeType() == NodeType.LITERAL_BOOL)) {
				operNode = resolveBinaryOperation(operNode, lChld, rChld);
			} else {
				operNode.setrChild(rChld);
				operNode.setlChild(lChld);
				addToNodeMap("#Exp" + this.nodeCount, operNode);
				this.nodeCount++;
			}
			this.operandStack.push(operNode);
		}
	}

	private void handleOperator(
			Node<pub.ayada.commons.ds.ast.tokens.Token<?>> curOper) {
		while (!this.operatorStack.empty()
				&& !this.operatorStack.peek().getValue().getValue().equals("{")
				&& !hasPresedence(curOper.getValue().getTokenTyp(),
						this.operatorStack.peek().getValue().getTokenTyp())) {
			Node<Token<?>> operator = this.operatorStack.pop();
			operator.setrChild(this.operandStack.pop());
			operator.setlChild(this.operandStack.pop());
			addToNodeMap("#Exp" + this.nodeCount, operator);
			this.nodeCount++;
			this.operandStack.push(operator);
		}
		this.operatorStack.push(curOper);
	}

	private void handleGroup(Node<Token<?>> tkn) {

		if (tkn.getValue().getTokenTyp() == TokenType.GROUP_EXP_OPEN) {
			operatorStack.push(this.expTkns.get(this.tknInx));

		} else if (tkn.getValue().getTokenTyp() == TokenType.GROUP_EXP_CLOSE) {
			resolveGroup2AST();

		} else if (tkn.getValue().getTokenTyp() == TokenType.GROUP_UNRY_OPEN) {
			tkn = this.expTkns.get(++this.tknInx);

			if (tkn.getNodeType() != NodeType.OPERATOR_UNARY)
				throw new RuntimeException(
						"Failed to parse the expression. Expected an unary operator but found '"
								+ tkn.getValue().toString() + "'");

			Node<Token<?>> operand = this.expTkns.get(++this.tknInx);
			if (operand.getNodeType() != NodeType.VARIABLE
					&& operand.getNodeType() != NodeType.LITERAL_NUM) {
				throw new RuntimeException(
						"Expecting either a numeric variable or literal. But found '"
								+ operand.getNodeType());
			} else {
				this.operandStack.push(operand);
			}

			if (this.expTkns.get(this.tknInx + 1).getValue().getTokenTyp() != TokenType.GROUP_UNRY_CLOSE)
				throw new RuntimeException(
						"Failed to parse the expression. Expected ')' but found '"
								+ this.expTkns.get(this.tknInx + 1).getValue()
										.getValue() + "'");

			handleUnaryOperator(tkn, this.operandStack.pop());

			this.tknInx++;
		}
	}

	private void handleUnaryOperator(Node<Token<?>> operator,
			Node<Token<?>> operand) {

		// Operand is a variable.
		if (operand.getNodeType() == NodeType.VARIABLE) {
			// Set the operand as the left child of the operator.
			operator.setlChild(operand);
			// Push the operator to the stack.
			this.operandStack.push(operator);
			addToNodeMap("#Exp" + this.nodeCount, operator);
			this.nodeCount++;
		} else {
			operand = resolveUnaryOperation(operator, operand);
			this.operandStack.push(operand);
		}
	}

	public void addToNodeMap(String key, Node<Token<?>> value) {
		this.NodenMap.put(key, value);
	}

	public void updateNodeMapValue(String key, Node<Token<?>> value)
			throws RuntimeException {
		if (this.NodenMap.containsKey(key))
			addToNodeMap(key, value);
		else
			throw new RuntimeException("Key '" + key
					+ " not found to update the NodeMap");
	}

	private Node<Token<?>> resolveUnaryOperation(Node<Token<?>> operator,
			Node<Token<?>> operand) {
		if (operand.getNodeType() == NodeType.LITERAL_STR
				|| operand.getValue().getTokenTyp() == TokenType.STR)
			throw new RuntimeException(
					"Unary operation on String variables or Literal are not supported.");

		if (operand.getValue().getTokenTyp() == TokenType.DEC) {
			double d = ((Double) operand.getValue().getValue()).doubleValue();
			switch (operator.getValue().getTokenTyp()) {
			case POSITIVE:
				break;
			case NEGATE:
				d = d * -1;
				break;
			case INCR_PRE:
				d++;
				break;
			case DECR_PRE:
				d--;
				break;
			default:
				throw new RuntimeException("Unary operation '"
						+ operator.getValue().getTokenTyp().toString()
						+ "' is not supported.");
			}
			operand = new Node<Token<?>>(new Token<Double>(new Double(d),
					TokenType.DEC), NodeType.LITERAL_NUM);
		} else if (operand.getValue().getTokenTyp() == TokenType.INT) {
			long l = ((Long) operand.getValue().getValue()).longValue();
			switch (operator.getValue().getTokenTyp()) {
			case POSITIVE:
				break;
			case NEGATE:
				l = l * -1;
				break;
			case INCR_PRE:
				l++;
				break;
			case DECR_PRE:
				l--;
				break;
			default:
				throw new RuntimeException("Unary operation '"
						+ operator.getValue().getTokenTyp().toString()
						+ "' is not supported.");
			}
			operand = new Node<Token<?>>(new Token<Long>(new Long(l),
					TokenType.INT), NodeType.LITERAL_NUM);
		}
		return operand;
	}

	private void handleFunction(Node<Token<?>> funcNode, Node<Token<?>> exp) throws ParseException {

		String subExp = exp.getValue().getValue().toString();
		
        switch (funcNode.getValue().getTokenTyp()) {        
        case DATE:
        	Token<Date> dt = null;
        	if (subExp.toUpperCase().equals("SYSDATE")){
        		dt = new Token<Date>(new Date(), TokenType.DATE);
        	}else {
	        	 String[] args = subExp.split(",\"");     	 
	        	 if (args.length != 2 )
	                 throw new RuntimeException("Invalid arguement count for Date funciton ':" + subExp 
	                		  +".\n" +"Should be @date(\"<DateString\">,\"<fmt>\")");
	        	 String fmt = args[1].trim();
	        	 fmt = fmt.substring(1, fmt.length()-1);
	        	 String dtStr = args[0].trim();
	        	 dtStr = dtStr.substring(1, dtStr.length()-1);            
	             dt = new Token<Date>(new SimpleDateFormat(fmt).parse(dtStr), TokenType.DATE);
        	}   
        	 funcNode = new Node<Token<?>>(dt,NodeType.LITERAL_DATE);
             break;    
		case ATAN2:
		case COPYSIGN:
		case POW:
		case HYPOT:
		case IEEEREMAINDER:
		case NEXTAFTER:
		case SCALB:
		case MAX:
		case MIN:
			break;
        default :
    		AbstractSynatxTree ast = new AbstractSynatxTree(subExp, false);
    		this.varMap.putAll(ast.getVarMaps());

    		TreeMap<String, Node<Token<?>>> ndMap = ast.getNodeMap();

    		for (int cnt = ndMap.size() - 1; cnt >= 0; cnt--) {
    			addToNodeMap("#Exp" + this.nodeCount, ndMap.get("#Exp" + (cnt)));
    			this.nodeCount++;
    		}
    		funcNode.setlChild(ast.getRootNode());
    		break;
        }		
		
		
	
		this.operandStack.push(funcNode);
		addToNodeMap("#Exp" + this.nodeCount, funcNode);
		this.nodeCount++;
		
		

		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
	}

	public void inOrder() {
		System.out.println("InOrder traversal result :");
		inOrder(this.root);
	}

	private void inOrder(Node<Token<?>> cur) {
		if (cur == null)
			return;
		if (cur.isLeaf()) {
			System.out.println("*Value:" + cur.getValue().getValue());
			return;
		}
		if (cur.getlChild() != null)
			inOrder(cur.getlChild());
		System.out.println(" Value:" + cur.getValue().getValue());

		if (cur.getlChild() != null)
			inOrder(cur.getrChild());
	}

	private boolean hasPresedence(TokenType tokenTypeEnum,
			TokenType tokenTypeEnum2) {

		if (getOperPriority(tokenTypeEnum) - getOperPriority(tokenTypeEnum2) > 0)
			return true;
		return false;
	}

	private int getOperPriority(TokenType tokenTypeEnum) {
		switch (tokenTypeEnum) {
		case GROUP_EXP_OPEN:
		case GROUP_UNRY_CLOSE:
			return 15;
			// case "_++":
			// case " _--":
		case POSITIVE:
		case NEGATE:
			// case "u! ":
			// case "u~":
			return 14;
		case INCR_PRE:
		case DECR_PRE:
			return 13;
		case MUL:
		case DIV:
		case MOD:
			return 12;
		case ADD:
		case SUB:
			return 11;
			// case LEFT_SHIFT:
			// case RIGHT_SHIFT:
			// case RIGHT_SHIFT_OEXT:
			// return 10;
		case LT:
		case LE:
		case GT:
		case GE:
			// case LT2:
			// case LE2:
			// case GT2:
			// case GE2:
			return 9;
		case EQ:
		case NE:
			// case EQ2:
			// case NE2:
			return 8;
			/*
			 * case BITWISE_AND: return 7; case BITWISE_OR_EXCLUSIVE: return 6;
			 * case BITWISE_OR_INCLUSIVE: return 5;
			 */
		case AND:
			// case AND2:
			return 4;
		case OR:
			// case OR2:
			return 3;
			// case TERNARY:
			// return 2;
			/*
			 * case ASSIGN: case ADDNASSIGN: case SUBNASSIGN: case MULNASSIGN:
			 * case DIVNASSIGN: case MODNASSIGN: return 1;
			 */default:
			throw new RuntimeException(
					"Failed to decide the priority for the operator :"
							+ tokenTypeEnum);
		}
	}

	public void assignParents() {
		assignParents(this.root);
	}

	private void assignParents(Node<Token<?>> curRoot) {
		Node<Token<?>> lChild = curRoot.getlChild();
		Node<Token<?>> rChild = curRoot.getrChild();
		if (lChild != null) {
			lChild.setParent(curRoot);
			if (!lChild.isLeaf())
				assignParents(lChild);
		}
		if (rChild != null) {
			rChild.setParent(curRoot);
			if (!rChild.isLeaf())
				assignParents(rChild);
		}
	}

	public void reportTtreeStructure() {
		assignParents();
		System.out
				.println("\n\n******************************************** Execution order - Tree Structure ********************************************\n\n");

		for (Entry<String, Node<Token<?>>> entry : getNodenMap().entrySet()) {
			System.out
					.println(entry.getKey()
							+ "--> '"
							+ (String) entry.getValue().getValue().getValue()
							+ "'\t (type: "
							+ entry.getValue().getValue().getTokenTyp()
							+ ").\n\t"
							+ (entry.getValue().isLeaf() ? "This is a leaf Node."
									: "LeftChild  : "
											+ (entry.getValue().getlChild()
													.getNodeType() == NodeType.VARIABLE ? (String) entry
													.getValue().getlChild()
													.getValue().getValue()
													+ " (Variable).\n\t"
													: "'"
															+ entry.getValue()
																	.getlChild()
																	.getValue()
																	.getValue()
																	.toString()
															+ "'\t(type:"
															+ entry.getValue()
																	.getlChild()
																	.getValue()
																	.getTokenTyp()
																	.toString()
															+ ").\n\t")
											+ "RightChild : "
											+ (entry.getValue().getrChild() == null ? " No Right Child.\n\t"
													: (entry.getValue()
															.getrChild()
															.getNodeType() == NodeType.VARIABLE ? (String) entry
															.getValue()
															.getrChild()
															.getValue()
															.getValue()
															+ " (Variable).\n\t"
															: "'"
																	+ entry.getValue()
																			.getrChild()
																			.getValue()
																			.getValue()
																			.toString()
																	+ "'\t(type:"
																	+ entry.getValue()
																			.getrChild()
																			.getValue()
																			.getTokenTyp()
																			.toString()
																	+ ").\n\t")))
							+ "Parent     : "
							+ (entry.getValue().getParent() == null ? " <This is the Root node>."
									: "'"
											+ entry.getValue().getParent()
													.getValue().getValue()
													.toString()
											+ "'\t(type:"
											+ entry.getValue().getParent()
													.getValue().getTokenTyp()
													.toString() + ")\n"));
		}
		System.out
				.println("\n\n******************************************** Execution order - Tree Structure ********************************************\n\n");
	}

	public Object resolve() {
		return resolveTree().getValue().getValue();
	}

	public long resolveToINT() {
		long res = (Long) resolveTree().getValue().getValue();
		return res;
	}

	public double resolveToDEC() {
		double res = (Double) resolveTree().getValue().getValue();
		return res;
	}

	public boolean resolveToBOOL() {
		boolean res = (Boolean) resolveTree().getValue().getValue();
		return res;
	}

	public String resolveToSTR() {
		String res = (String) resolveTree().getValue().getValue();
		return res;
	}

	public Character resolveToCHR() {
		Character res = (Character) resolveTree().getValue().getValue();
		return res;
	}

	private Node<Token<?>> resolveTree() {
		return resolveTree(this.root);
	}

	private Node<Token<?>> resolveTree(Node<Token<?>> root) {

		if (root.isLeaf())
			return root;

		Node<Token<?>> lChild = null, rChild = null, resNode = null;

		// If the operation is && and either lChild or the rChild is false. the
		// end result is false.
		// this check is done to save the computation...
		if (root.getValue().getTokenTyp() == TokenType.AND) {// ||
																// root.getValue().getTokenTyp()
																// ==
																// TokenType.AND2
																// ){
			lChild = root.getlChild();
			if (lChild.getNodeType() == NodeType.LITERAL_BOOL
					&& (Boolean) lChild.getValue().getValue() == false) {
				if (isDebug())
					System.out
							.println(String.format("\tTask_%03d : ",
									this.calcCount++)
									+ " Operation : '"
									+ (root.getValue().getTokenTyp() + "'" + StringUtils
											.repeat(" ", 10)).substring(0, 10)
									+ " Operand 1 :  'false'  Operand 2 :  ??????"
									+ "\n\t\t  Result ==> false \n");
				return lChild;
			}
			rChild = root.getrChild();
			if (rChild.getNodeType() == NodeType.LITERAL_BOOL
					&& (Boolean) rChild.getValue().getValue() == false) {
				if (isDebug())
					System.out
							.println(String.format("\tTask_%03d : ",
									this.calcCount++)
									+ " Operation : '"
									+ (root.getValue().getTokenTyp() + "'" + StringUtils
											.repeat(" ", 10)).substring(0, 10)
									+ " Operand 1 :  ??????   Operand 2 : 'false'"
									+ "\n\t\t  Result ==> false \n");
				return rChild;
			}
		}

		if (root.getlChild() != null) {
			lChild = (Node<Token<?>>) resolveTree(root.getlChild());
			root.setlChild(null);
		}
		if (lChild.getNodeType() == NodeType.VARIABLE)
			lChild.setValue(this.varMap.get((String) lChild.getValue()
					.getValue()));
		else if (root.getValue().getTokenTyp() == TokenType.AND) {// ||
																	// root.getValue().getTokenTyp()
																	// ==
																	// TokenType.AND2
																	// ){
			if (lChild.getNodeType() == NodeType.LITERAL_BOOL
					&& (Boolean) lChild.getValue().getValue() == false) {
				if (isDebug())
					System.out
							.println(String.format("\tTask_%03d : ",
									this.calcCount++)
									+ " Operation : '"
									+ (root.getValue().getTokenTyp() + "'" + StringUtils
											.repeat(" ", 10)).substring(0, 10)
									+ " Operand 1 :  'false'  Operand 2 :  ??????"
									+ "\n\t\t  Result ==> false \n");
				return lChild;
			}
		}

		if (root.getrChild() != null) {
			rChild = (Node<Token<?>>) resolveTree(root.getrChild());
			root.setrChild(null);
		}
		if (root.getValue().getTokenTyp() == TokenType.AND) {
			if (rChild.getNodeType() == NodeType.LITERAL_BOOL
					&& (Boolean) rChild.getValue().getValue() == false) {
				if (isDebug())
					System.out
							.println(String.format("\tTask_%03d : ",
									this.calcCount++)
									+ " Operation : '"
									+ (root.getValue().getTokenTyp() + "'" + StringUtils
											.repeat(" ", 10)).substring(0, 10)
									+ " Operand 1 :  ??????   Operand 2 : 'false'"
									+ "\n\t\t  Result ==> false \n");
				return rChild;
			}
		}
		if (isDebug()) {
			System.out.print(String.format("\tTask_%03d : ", this.calcCount++)
					+ " Operation : '"
					+ (root.getValue().getTokenTyp() + "'" + StringUtils
							.repeat(" ", 10)).substring(0, 10));
			System.out.print(" Operand 1 : '"
					+ lChild.getValue().getValue().toString() + "'");
		}

		if (root.getNodeType() == NodeType.OPERATOR_UNARY) {
			if (rChild != null)
				throw new RuntimeException("Unary operator '"
						+ root.getValue().getTokenTyp()
						+ "' can't handle two operands");
			resNode = resolveUnaryOperation(root, lChild);
		} else if (root.getNodeType() == NodeType.FUNCTION) {
			resNode = resolveFunction(root, lChild);
		} else {

			if (rChild == null)
				throw new RuntimeException("Binary operator '"
						+ root.getValue().getTokenTyp()
						+ "' needs the right operand but found null");

			if (rChild.getNodeType() == NodeType.VARIABLE)
				rChild.setValue(this.varMap.get((String) rChild.getValue()
						.getValue()));

			if (isDebug())
				System.out.print(" Operand 2 : '"
						+ rChild.getValue().getValue().toString() + "'");
			resNode = resolveBinaryOperation(root, lChild, rChild);
		}
		if (isDebug())
			System.out.println("\n\t\t  Result ==> "
					+ resNode.getValue().getValue() + "\n");

		return resNode;
	}

	private Node<Token<?>> resolveFunction(Node<Token<?>> funcNode,
			Node<Token<?>> lChild) {
		Token<?> res = lChild.getValue();
		if (res.getValue() == null)
			throw new IllegalArgumentException(funcNode.getValue()
					.getTokenTyp() + " can't handle the null argument. ");
		Node<Token<?>> resNode = null;
		switch (funcNode.getValue().getTokenTyp()) {
		case ABS:
			switch (res.getTokenTyp()) {
			case INT:
				res = new Token<Long>(Math.abs((Long) res.getValue()),
						TokenType.INT);
				break;
			case DEC:
				res = new Token<Double>(new Double(Math.abs(((Double) res
						.getValue()).doubleValue())), TokenType.DEC);
				break;
			default:
				throw new IllegalArgumentException(
						"Math.abs() can't handle the argument type "
								+ res.getTokenTyp().toString() + " for value:"
								+ res.getValue().toString());
			}

			resNode = new Node<Token<?>>(res, NodeType.LITERAL_NUM);
			break;
		case SIN:
			res = new Token<Double>(new Double(Math.sin(((Number) res
					.getValue()).doubleValue())), TokenType.DEC);
			resNode = new Node<Token<?>>(res, NodeType.LITERAL_NUM);
			break;
		case ASIN:
			res = new Token<Double>(new Double(Math.asin(((Number) res
					.getValue()).doubleValue())), TokenType.DEC);
			resNode = new Node<Token<?>>(res, NodeType.LITERAL_NUM);
			break;
		case SINH:
			res = new Token<Double>(new Double(Math.sinh(((Number) res
					.getValue()).doubleValue())), TokenType.DEC);
			resNode = new Node<Token<?>>(res, NodeType.LITERAL_NUM);
			break;
		case COS:
			res = new Token<Double>(new Double(Math.cos(((Number) res
					.getValue()).doubleValue())), TokenType.DEC);
			resNode = new Node<Token<?>>(res, NodeType.LITERAL_NUM);
			break;
		case ACOS:
			res = new Token<Double>(new Double(Math.acos(((Number) res
					.getValue()).doubleValue())), TokenType.DEC);
			resNode = new Node<Token<?>>(res, NodeType.LITERAL_NUM);
			break;
		case COSH:
			res = new Token<Double>(new Double(Math.cosh(((Number) res
					.getValue()).doubleValue())), TokenType.DEC);
			resNode = new Node<Token<?>>(res, NodeType.LITERAL_NUM);
			break;
		case TAN:
			res = new Token<Double>(new Double(Math.tan(((Number) res
					.getValue()).doubleValue())), TokenType.DEC);
			resNode = new Node<Token<?>>(res, NodeType.LITERAL_NUM);
			break;
		case ATAN:
			res = new Token<Double>(new Double(Math.atan(((Number) res
					.getValue()).doubleValue())), TokenType.DEC);
			resNode = new Node<Token<?>>(res, NodeType.LITERAL_NUM);
			break;
		case TANH:
			res = new Token<Double>(new Double(Math.tanh(((Number) res
					.getValue()).doubleValue())), TokenType.DEC);
			resNode = new Node<Token<?>>(res, NodeType.LITERAL_NUM);
			break;
		case CBRT:
			res = new Token<Double>(new Double(Math.cbrt(((Number) res
					.getValue()).doubleValue())), TokenType.DEC);
			resNode = new Node<Token<?>>(res, NodeType.LITERAL_NUM);
			break;
		case CEIL:
			res = new Token<Double>(new Double(Math.ceil(((Number) res
					.getValue()).doubleValue())), TokenType.DEC);
			resNode = new Node<Token<?>>(res, NodeType.LITERAL_NUM);
			break;
		case EXP:
			res = new Token<Double>(new Double(Math.exp(((Number) res
					.getValue()).doubleValue())), TokenType.DEC);
			resNode = new Node<Token<?>>(res, NodeType.LITERAL_NUM);
			break;
		case EXPM1:
			res = new Token<Double>(new Double(Math.expm1(((Number) res
					.getValue()).doubleValue())), TokenType.DEC);
			resNode = new Node<Token<?>>(res, NodeType.LITERAL_NUM);
			break;
		case FLOOR:
			res = new Token<Double>(new Double(Math.floor(((Number) res
					.getValue()).doubleValue())), TokenType.DEC);
			resNode = new Node<Token<?>>(res, NodeType.LITERAL_NUM);
			break;
		case GETEXPONENT:
			res = new Token<Long>(new Long(Math.getExponent(((Number) res
					.getValue()).doubleValue())), TokenType.DEC);
			resNode = new Node<Token<?>>(res, NodeType.LITERAL_NUM);
			break;
		case LOG:
			res = new Token<Double>(new Double(Math.log(((Number) res
					.getValue()).doubleValue())), TokenType.DEC);
			resNode = new Node<Token<?>>(res, NodeType.LITERAL_NUM);
			break;
		case LOG10:
			res = new Token<Double>(new Double(Math.log10(((Number) res
					.getValue()).doubleValue())), TokenType.DEC);
			resNode = new Node<Token<?>>(res, NodeType.LITERAL_NUM);
			break;
		case LEN:
			Number len = ((String) lChild.getValue().getValue()).length();
			Token<Long> tkn = new Token<Long>(len.longValue(), TokenType.INT);
			resNode = new Node<Token<?>>(tkn, NodeType.LITERAL_NUM);
			break;
		case NEXTUP:
			res = new Token<Double>(new Double(Math.nextUp(((Number) res
					.getValue()).doubleValue())), TokenType.DEC);
			resNode = new Node<Token<?>>(res, NodeType.LITERAL_NUM);
			break;
		case RANDOM:
			res = new Token<Double>(new Double(Math.random()), TokenType.DEC);
			resNode = new Node<Token<?>>(res, NodeType.LITERAL_NUM);
			break;
		case RINT:
			res = new Token<Double>(new Double(Math.rint(((Number) res
					.getValue()).doubleValue())), TokenType.DEC);
			resNode = new Node<Token<?>>(res, NodeType.LITERAL_NUM);
			break;
		case ROUND:
			res = new Token<Long>(new Long((long) Math.round(((Number) res
					.getValue()).doubleValue())), TokenType.INT);
			resNode = new Node<Token<?>>(res, NodeType.LITERAL_NUM);
			break;
		case SIGNUM:
			res = new Token<Double>(new Double(Math.signum(((Number) res
					.getValue()).doubleValue())), TokenType.DEC);
			resNode = new Node<Token<?>>(res, NodeType.LITERAL_NUM);
			break;
		case SQRT:
			res = new Token<Double>(new Double(Math.sqrt(((Number) res
					.getValue()).doubleValue())), TokenType.DEC);
			resNode = new Node<Token<?>>(res, NodeType.LITERAL_NUM);
			break;
		case TODEGREES:
			res = new Token<Double>(new Double(Math.toDegrees(((Number) res
					.getValue()).doubleValue())), TokenType.DEC);
			resNode = new Node<Token<?>>(res, NodeType.LITERAL_NUM);
			break;
		case TORADIANS:
			res = new Token<Double>(new Double(Math.toRadians(((Number) res
					.getValue()).doubleValue())), TokenType.DEC);
			resNode = new Node<Token<?>>(res, NodeType.LITERAL_NUM);
			break;
		case ULP:
			res = new Token<Double>(new Double(Math.ulp(((Number) res
					.getValue()).doubleValue())), TokenType.DEC);
			resNode = new Node<Token<?>>(res, NodeType.LITERAL_NUM);
			break;
		case DATE:
		case ATAN2:
		case COPYSIGN:
		case POW:
		case HYPOT:
		case IEEEREMAINDER:
		case NEXTAFTER:
		case SCALB:
		case MAX:
		case MIN:
		default:
			throw new RuntimeException("Unknown function '"
					+ funcNode.getValue().getTokenTyp() + "'");
		}
		return resNode;
	}

	private Node<Token<?>> resolveBinaryOperation(Node<Token<?>> operNode,
			Node<Token<?>> lChild, Node<Token<?>> rChild) {
		Token<?> res = null;
		Node<Token<?>> resNode = null;

		switch (operNode.getValue().getTokenTyp()) {
		case EQ:
			// case EQ2:
			res = new Token<Boolean>(new Boolean(lChild.getValue().getValue()
					.equals(rChild.getValue().getValue())), TokenType.BOOL);
			resNode = new Node<Token<?>>(res, NodeType.LITERAL_BOOL);
			break;
		case NE:
			// case NE2:
			res = new Token<Boolean>(new Boolean(!lChild.getValue().getValue()
					.equals(rChild.getValue().getValue())), TokenType.BOOL);
			resNode = new Node<Token<?>>(res, NodeType.LITERAL_BOOL);
			break;
		case LT:
			// case LT2:
			Boolean lt = lChild.getValue().LT((Token<?>) rChild.getValue());
			res = new Token<Boolean>(lt, TokenType.BOOL);
			resNode = new Node<Token<?>>(res, NodeType.LITERAL_BOOL);
			break;
		case GT:
			// case GT2:
			Boolean gt = lChild.getValue().GT((Token<?>) rChild.getValue());
			res = new Token<Boolean>(gt, TokenType.BOOL);
			resNode = new Node<Token<?>>(res, NodeType.LITERAL_BOOL);
			break;
		case LE:
			// case LE2:
			Boolean le = lChild.getValue().LE((Token<?>) rChild.getValue());
			res = new Token<Boolean>(le, TokenType.BOOL);
			resNode = new Node<Token<?>>(res, NodeType.LITERAL_BOOL);
			break;
		case GE:
			// case LE2:
			Boolean ge = lChild.getValue().GE((Token<?>) rChild.getValue());
			res = new Token<Boolean>(ge, TokenType.BOOL);
			resNode = new Node<Token<?>>(res, NodeType.LITERAL_BOOL);
			break;
		case ADD:
		case SUB:
		case DIV:
		case MUL:
		case MOD:
			res = mathOperate(lChild.getValue(), rChild.getValue(), operNode
					.getValue().getTokenTyp());
			resNode = new Node<Token<?>>(res, NodeType.LITERAL_NUM);
			break;
		case AND:
			if (lChild.getValue().getTokenTyp() == TokenType.BOOL
					&& rChild.getValue().getTokenTyp() == TokenType.BOOL)
				res = new Token<Boolean>(new Boolean((Boolean) lChild
						.getValue().getValue()
						&& (Boolean) rChild.getValue().getValue()),
						TokenType.BOOL);
			else
				throw new RuntimeException("Can't run the logical operation '"
						+ operNode.getValue().getTokenTyp().getTokenLiteral()
						+ "'  against operands (left)"
						+ lChild.getValue().getValue().toString()
						+ " and (right) "
						+ rChild.getValue().getValue().toString());

			resNode = new Node<Token<?>>(res, NodeType.LITERAL_BOOL);
			break;
		default:
			throw new RuntimeException("Unknown Binary operator '"
					+ operNode.getValue().getTokenTyp() + "'");
		}
		return resNode;
	}

	private Token<?> mathOperate(Token<?> lChild, Token<?> rChild,
			TokenType tokenType) throws RuntimeException {
		try {
			if (lChild.getTokenTyp() == TokenType.INT
					&& rChild.getTokenTyp() == TokenType.INT) {
				Long left = (Long) lChild.getValue();
				Long right = (Long) rChild.getValue();
				switch (tokenType) {
				case ADD:
					return new Token<Long>(left + right, TokenType.INT);
				case SUB:
					return new Token<Long>(left - right, TokenType.INT);
				case MUL:
					return new Token<Long>(left * right, TokenType.INT);
				case DIV:
					return new Token<Long>(left / right, TokenType.INT);
				case MOD:
					return new Token<Long>(left % right, TokenType.INT);
				default:
					throw new ParseException("Unknown operator "
							+ tokenType.getTokenLiteral(), 0);
				}
			} else {
				Double left = ((Number) lChild.getValue()).doubleValue();
				Double right = ((Number) rChild.getValue()).doubleValue();
				switch (tokenType) {
				case ADD:
					return new Token<Double>(left + right, TokenType.DEC);
				case SUB:
					return new Token<Double>(left - right, TokenType.DEC);
				case MUL:
					return new Token<Double>(left * right, TokenType.DEC);
				case DIV:
					return new Token<Double>(left / right, TokenType.DEC);
				case MOD:
					return new Token<Double>(left % right, TokenType.DEC);
				default:
					throw new ParseException("Unknown operator "
							+ tokenType.getTokenLiteral(), 0);
				}
			}
		} catch (Exception e) {
			throw new RuntimeException("Error-" + e.getMessage() + "\n"
					+ "During the the operation . "
					+ lChild.getValue().toString() + " " + tokenType.toString()
					+ " " + rChild.getValue().toString());
		}
	}

	public void printStack() { // return operandsList;

		StringBuilder sb = new StringBuilder("[");
		for (Node<Token<?>> node : this.operatorStack) {
			sb.append(node.getValue().getValue() + ":"
					+ node.getValue().getTokenTyp() + " , ");
		}
		System.out.println("Operator Stack : " + sb.append("]").toString());

		sb = new StringBuilder("[");
		for (Node<Token<?>> node : this.operandStack) {
			sb.append(node.getValue().getValue() + ":"
					+ node.getValue().getTokenTyp() + " , ");
		}

		System.out.println("Operands Stack : " + sb.append("]").toString()
				+ "\n\n");

	}
	/*
	 * private Node<Token<?>> reachtoFirstLeaf(Node<Token<?>> cur) { while
	 * (true) { if (cur.getlChild() == null) break; cur = cur.getlChild(); }
	 * return cur; }
	 */
}
