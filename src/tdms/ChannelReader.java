package tdms;

import java.util.Iterator;
import java.util.List;

public interface ChannelReader {
	
	/** returns objects that are channels */ 
	public List<Channel> getChannels();
	
	/** returns the size of the file */ 
	public int filesize(); 
}
