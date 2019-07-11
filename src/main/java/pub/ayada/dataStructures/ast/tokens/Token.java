package pub.ayada.commons.ds.ast.tokens;

import java.io.Serializable;

public class Token<T extends Comparable<? super T>>  implements Cloneable, Serializable,  Comparable<Token<T>>{

	private static final long serialVersionUID = -7113849598705363948L;	
	private T value;
	private TokenType tokenTyp;
	
	public Token() {}

	public Token(TokenType tokenTyp) {
		this(null, tokenTyp);
	}
	
	public Token(T value, TokenType tokenTyp) {
		this.value = value;
		this.tokenTyp = tokenTyp;
	}

	public T getValue() {
		return value;
	}

	public void setValue(T value) {
		this.value = value;
	}

	public TokenType getTokenTyp() {
		return tokenTyp;
	}

	public void setTokenTyp(TokenType tokenTyp) {
		this.tokenTyp = tokenTyp;
	}

	@Override
	public int compareTo(Token<T> other) {
		return value.compareTo((T)other.getValue());
	}
	@SuppressWarnings("unchecked")
	public boolean LT(Token<?> other) {
		return value.compareTo((T) other.getValue()) < 0 ; 
	}	
	@SuppressWarnings("unchecked")
	public boolean LE(Token<?> other) {
		return value.compareTo((T) other.getValue()) <= 0;
	}
	@SuppressWarnings("unchecked")
	public boolean GT(Token<?> other) {
		return value.compareTo((T) other.getValue()) > 0 ;
	}	
	@SuppressWarnings("unchecked")	
	public boolean GE(Token<?> other) {
		return value.compareTo((T) other.getValue()) >= 0 ; 
	} 
}
