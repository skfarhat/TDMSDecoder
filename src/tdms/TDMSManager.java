package tdms;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Stream;

import org.apache.log4j.Logger;

// Notes
// Java doesnt have primitive unsigned long 
// so for big number this may not work 

// there are readInt() which read signed int values, whereas TDMS uses unsigned so for ints > 2^31 this will not work 

// TODO
// use objectorder instead of the HashMap to get objects in getchannels()

public class TDMSManager implements ChannelOrderAdder, ChannelReader {
	
	private static Logger logger = Logger.getLogger(TDMSManager.class); 
	
	/** TODO: possibly redundant with the presence of linkedhashmap */ 
	private ArrayList<Channel> 				objectOrder = new ArrayList<>();
	
	/** linkedhashmap to retrieve objects based on their paths 
	 * AND to get them in the order they were inserted */
	private LinkedHashMap<String, Channel> 	objects 	= new LinkedHashMap<String, Channel>();
	private ArrayList<Segment> 				segments 	= new ArrayList<>();
	
	private int filesize; 

	/**
	 * Reads TDMS file with the given filename.
	 *  We return the data, which is, object meta data and raw channel data.
	 *  Notice that we do not read the (optionally) accompanying .tdms_index
	 *  since it is supposed to be an exact copy of the .tdms file, without the
	 *  raw data. So it should contain nothing new.
	 * @param filename
	 * @throws IOException 
	 */
	public List<Channel> read(String filename) throws IOException { 

		// TODO: There must be an easier way 
		// Then we read the data from a file, and return that
		FileInputStream stream = new FileInputStream(filename);		
		filesize = stream.available();

		logger.trace("filesize: " + filesize);
		byte[] data = new byte[filesize]; 
		stream.read(data); 

		ByteBuffer buffer = ByteBuffer.wrap(data); 
		buffer.order(ByteOrder.LITTLE_ENDIAN);


		ChannelReader reader 	= this; 
		ChannelOrderAdder adder = this;
		int segmentIndex = 0; 
		while (buffer.hasRemaining()) {
			logger.debug("================================================================="); 
			logger.debug("Segment: " + segmentIndex++); 
			Segment segment = new Segment(buffer, adder, reader); 
			segments.add(segment);
		}
		stream.close();
		
		return getChannels(); 
	}

	@Override
	public void addObject(Channel channel) {
		
		/* add object to ordered list */ 
		if ( !objects.containsKey(channel.getObjectPath()) ) 
			objectOrder.add(channel);
		
		objects.put(channel.getObjectPath(), channel); 
		
		/* check if this is a new object */		
//		if ( !objects.containsKey(channel.getObjectPath()) )  {
//			/* add object to dict */ 
//			objects.put(channel.getObjectPath(), channel); 
//
//			/* add object to ordered list */ 
//			objectOrder.add(channel); 
//		}
	}
	
	// TODO : maybe better to use objectorder rather than objects() 
	@Override
	public List<Channel> getChannels() {

		/* returned */ 
		ArrayList<Channel> ret = new ArrayList<>();

		/* list of all obj values */ 
		Stream<Channel> original = objects.values().stream();

		/* filter the list according to those which have channels */ 
		Stream<Channel> filtered = original.filter(p -> p.isChannel()); 

		/* add the filtered objects to the return array */
		filtered.forEach(o -> ret.add(o));
		
		return ret; 
	}

	@Override 
	public int filesize() { return filesize;  }
	
	public ArrayList<Segment> getSegments() { return segments; }

	/**
	 * reads string from FileInputStream according to length passed
	 * @param stream
	 * @param length
	 * @return
	 * @throws IOException 
	 */
	public static String readString(ByteBuffer buffer, int length) throws IOException { 
		byte[] b1 = new byte[length];
		buffer.get(b1); 
		String x = new String(b1); 

		return x; 
	}

	/**
	 * get list of objects read by TDMSManager
	 * @return
	 */
	public List<Channel> objects() {
		/* return copy */ 
		return new ArrayList<>(objects.values()); 
	}
}