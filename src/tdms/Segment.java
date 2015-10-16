package tdms;

import java.io.IOException;
import java.nio.ByteBuffer;

import org.apache.log4j.Logger;

// TODO: recheck doc
/**
 * This class represents a Segment,<br>  
 * which - for recap - is generally composed of the following 3: <br>
 * <ul> 
 * <li> Lead in </li> 
 * <li> Metadata </li> 
 * <li> RawData </li> 
 * </ul> 
 * The class will read through the buffer, collect the data according to its type (DoubleFloat, Int32...)
 * and associate the aformentioned data with its corresponding (Channel ? ). 
 *
 */
public class Segment {
	private static Logger logger = Logger.getLogger(Segment.class); 

	/**
	 * leadin
	 */
	private LeadIn leadin; 
	
	/** 
	 * 
	 */
	private Metadata metadata;
	
	/**
	 * rawdata
	 */
	private RawData rawdata; 
	
	/**
	 * 
	 * @param buffer
	 * @param adder instance pointing to TDMSManager class which implements adding to an array
	 * @throws IOException
	 */
	public Segment(ByteBuffer buffer, ChannelOrderAdder adder, ChannelReader reader) throws IOException {

		leadin = new LeadIn(buffer);
		logger.debug("LeadIn: " + leadin);
		logger.debug("*******************"); 
		
		if (leadin.hasMetadata()) { 
			this.metadata = new Metadata(buffer, adder);
			logger.debug("*******************");
		}
		if (leadin.hasRawdata()) { 
			this.rawdata = new RawData(buffer, leadin, reader, metadata);
			logger.debug("*******************");
		}
		
	}
	
	public LeadIn 	getLeadin() 	{ return leadin; 	}
	public Metadata getMetadata() 	{ return metadata; 	}
	public RawData 	getRawdata() 	{ return rawdata; 	}
}
