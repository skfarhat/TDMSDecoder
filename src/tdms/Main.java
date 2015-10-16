package tdms;

import java.io.IOException;
import org.apache.log4j.BasicConfigurator;

public class Main {

	static {  BasicConfigurator.configure();   }
	
	public static void main(String[] args) throws IOException {
		final String filename = "res/Let E2.tdms";
		TDMSManager manager = new TDMSManager(); 
		manager.read(filename);
		Iterable<Channel> channels2 = manager.getChannels();
		for (Channel c : channels2) {
			System.out.println(c.getObjectPath());
			c.getData().forEach(d -> System.out.printf("%.2f ", d ));
			System.out.println();
		}
	}
}
