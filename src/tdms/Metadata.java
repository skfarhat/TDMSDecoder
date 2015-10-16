package tdms;

import java.io.IOException;
import java.nio.ByteBuffer;

import org.apache.log4j.Logger;

/**
 * 
 * Composition : 
 * 
 * <ul> 
 * <li> Number of Objects </li> 
 * <li> Array Of Objects </li> 
 * </ul> 
 * @author Sami
 *
 */
public class Metadata {

	private static Logger logger = Logger.getLogger(Metadata.class); 

	
	/**
	 * number of objects in this metadata
	 */
	private int nbOfObjects; 
	
	public Metadata(ByteBuffer buffer, ChannelOrderAdder adder) throws IOException {
		
		this.nbOfObjects = buffer.getInt(); 
		logger.debug("nbOfObjects: " + nbOfObjects);
		if (nbOfObjects == 0) { 
			logger.info("Metadata found 0 objects.");
		}
		// CHECK: rename Channel to Object ? 
		for (int i = 0; i < nbOfObjects; i++) { 
			
			/* create objects */ 
			Channel o = new Channel(buffer); 
	
			/* add object */ 
			adder.addObject(o); 
		}
	}
	
}
