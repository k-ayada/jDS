package pub.ayada.dataStructures.queues;

public class CQueue1to1<E> extends CQueue<E> {
	private static final long serialVersionUID = -3952115187196292811L;
	public CQueue1to1(int CQueue_Length) {
		super(CQueue_Length);
	}
	public CQueue1to1(int CQueue_Length, boolean testing ) {
		super(CQueue_Length, testing);
	}
	public CQueue1to1(int CQueue_Length, CQueue<E> clone) {
		super(CQueue_Length, clone);
	}	
	public CQueue1to1(CQueue<E> clone) {
		super(clone);
	}	
}