package pub.ayada.dataStructures.queues;

public class CQueue1to1Blocked<E>  extends CQueue<E> {
	private static final long serialVersionUID = -6213007202110180843L;
	public CQueue1to1Blocked(String queueID, int CQueue_Length) {
		super(queueID,CQueue_Length);
	}
	public CQueue1to1Blocked(String queueID,int CQueue_Length, boolean testing ) {
		super(queueID,CQueue_Length, testing);
	}
	public CQueue1to1Blocked(String queueID,int CQueue_Length, CQueue<E> clone) {
		super(queueID,CQueue_Length, clone);
	}	
	public CQueue1to1Blocked(String newQueueID,CQueue<E> clone) {
		super(newQueueID,clone);
	}	

	public boolean canAdd() {
	 // log("canAdd()  -" + (this.arr[this.PushTo] == null));
		if (this.arr[this.PushTo] == null && this.PopFrom ==0)
		   return true;
		return false;
	}
	public boolean canGet() {
    // log ("canGet()  -" + !(this.arr[this.PopFrom] == null));
       return (this.LoadEnded.get() && this.arr[this.PopFrom] != null);
	}
	
	public E peekLast() {
		if (this.PushTo ==0)
			return this.arr[this.arr.length-1];
		return this.arr[this.PushTo-1];
	}
	public E peekFirst() {
			return this.arr[0];
	}	
	
}
