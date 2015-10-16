package tdms;
import java.io.IOException;
import java.nio.ByteBuffer;

import org.apache.log4j.Logger;

/**
 * 
 * @author Sami Farhat
 *
 */
public class LeadIn {
	
	@SuppressWarnings("unused")
	private static Logger logger = Logger.getLogger(LeadIn.class); 

	/**
	 * beginning string. should be TDMs
	 */
	private String start;
	/** 
	 * Table of content bit mask 
	 */
	private int toC; 
	/**
	 * version of the TDMS file
	 * Labview 2.0: 4713
	 * Labview 1.0: 4712
	 */
	private int version;
	/**
	 * NOTE: should be unsigned
	 * length of the remaining segment
	 * (next_segment_offset)  
	 */
	private long lenRemainSegment;
	/**
	 * NOTE: should be unsigned
	 * overall length of the metadata
	 * (raw_data_offset) 
	 */
	private long lenOfMetadata;

	/**
	 * Reads the byte values from buffer and sets the appropriate fields 
	 * in the created object
	 * @param buffer
	 * @throws IOException
	 */
	public LeadIn(ByteBuffer buffer) throws IOException {
		this.start 				= TDMSManager.readString(buffer, 4); 
		this.toC 				= buffer.getInt();
		this.version 			= buffer.getInt();
		this.lenRemainSegment	= buffer.getLong(); 
		this.lenOfMetadata		= buffer.getLong();
	}

	public String getStart() {
		return start;
	}

	public int getToC() {
		return toC;
	}

	public int getVersion() {
		return version;
	}

	/**
	 * length of the remaining segment
	 * (next_segment_offset)
	 * @return
	 */
	public long getLenRemainSegment() {
		return lenRemainSegment;
	}

	/**
	 * overall length of the metadata
	 * (raw_data_offset)
	 * @return
	 */
	public long getLenOfMetadata() {
		return lenOfMetadata;
	}

	@Override
	public String toString() {
		return 
				String.format("start: %s toC: %d version: %d lenOfSegment: %d lenOfMetaData: %d", 
						start, 
						toC, 
						version, 
						lenRemainSegment, 
						lenOfMetadata); 
	}
	
	public boolean hasMetadata() 		{ return (toC & (1<<1)) != 0; }
	public boolean hasRawdata() 		{ return (toC & (1<<3)) != 0; }
	public boolean hasDAQmxrawData() 	{ return (toC & (1<<7)) != 0; }
	public boolean hasInterleavedData() { return (toC & (1<<5)) != 0; }
	public boolean isBigEndian()		{ return (toC & (1<<6)) != 0; }
	public boolean hasNewObjList()		{ return (toC & (1<<2)) != 0; }
	
}
