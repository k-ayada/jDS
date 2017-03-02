package pub.ayada.dataStructures.record;

public class ColumnMeta {

	private int colNum;
	private char sortOrder;

	public ColumnMeta(int column, char order) {

		this.colNum = column;
		this.sortOrder = order;
	}
	
	public int getColumnNum() {
		return this.colNum;
	}

	public void setColumnNum(int columnNum) {
		this.colNum = columnNum;
	}

	public char getOrder() {
		return this.sortOrder;
	}

	public void setOrder(char order) {
		this.sortOrder = order;
	}
}
