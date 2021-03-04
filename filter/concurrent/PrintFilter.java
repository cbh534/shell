package cs131.pa2.filter.concurrent;

/**
 * The filter for printing in the console
 * @author cs131a
 *
 */
public class PrintFilter extends ConcurrentFilter {
	public PrintFilter() {
		super();
	}
	
	public void process() {
		try {
			line = input.take();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        while (!line.equals("Process finished")) {
            processLine(line);
            try {
				line = input.take();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
	}
	
	public String processLine(String line) {
		System.out.println(line);
		return null;
	}
}
