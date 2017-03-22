package pub.ayada.dataStructures.queues;

public class CQueue1to1<E> extends CQueue<E> {
	private static final long serialVersionUID = -3952115187196292811L;
	public CQueue1to1(String queueID, int CQueue_Length) {
		super(queueID,CQueue_Length);
	}
	public CQueue1to1(String queueID,int CQueue_Length, boolean testing ) {
		super(queueID,CQueue_Length, testing);
	}
	public CQueue1to1(String queueID,int CQueue_Length, CQueue<E> clone) {
		super(queueID,CQueue_Length, clone);
	}	
	public CQueue1to1(String newQueueID,CQueue<E> clone) {
		super(newQueueID,clone);
	}	
}
