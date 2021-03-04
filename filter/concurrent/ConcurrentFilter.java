package cs131.pa2.filter.concurrent;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import cs131.pa2.filter.Filter;

/**
 * An abstract class that extends the Filter and implements the basic functionality of all filters. Each filter should
 * extend this class and implement functionality that is specific for that filter.
 * @author cs131a
 *
 */
public abstract class ConcurrentFilter extends Filter implements Runnable {
	/**
	 * The input queue for this filter
	 */
	protected LinkedBlockingQueue<String> input;;
	/**
	 * The output queue for this filter
	 */
	protected LinkedBlockingQueue<String> output;;
	String line = "";
	
	@Override
	public void setPrevFilter(Filter prevFilter) {
		prevFilter.setNextFilter(this);
	}
	
	@Override
	public void setNextFilter(Filter nextFilter) {
		if (nextFilter instanceof ConcurrentFilter){
			ConcurrentFilter concurrentNext = (ConcurrentFilter) nextFilter;
			this.next = concurrentNext;
			concurrentNext.prev = this;
			if (this.output == null){
				this.output = new LinkedBlockingQueue<>();
			}
			concurrentNext.input = this.output;
		} else {
			throw new RuntimeException("Should not attempt to link dissimilar filter types.");
		}
	}
	/**
	 * Gets the next filter
	 * @return the next filter
	 */
	public Filter getNext() {
		return next;
	}
	/**
	 * processes the input queue and writes the result to the output queue
	 * @throws InterruptedException 
	 */
	public void process( ){
		while (!isDone()){	
			try {
				line = input.take();
				String processedLine = processLine(line);
				if (processedLine != null){
					output.add(processedLine);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		output.add("Process finished");
	}
	
	public void run() {
		process();
	}
	
	@Override
	public boolean isDone() {
		return input.size() == 0 && line.equals("Process finished");
	}
	
	protected abstract String processLine(String line);
	
}
