package tdms;

import java.nio.ByteBuffer;

import org.apache.log4j.Logger;

/**
 * 
 * @author Sami
 *
 */
public class RawDataInfo {
	
	private static Logger logger = Logger.getLogger(RawDataInfo.class); 

	/** type of rawdata */ 
	private RawdataDataType datatype;
	/** length of raw data */
	private int lenOfRawdata; 
	/** array dimension of the rawdata array. In Labview 2.0, 1 is the only valid value*/
	private int dimension; 
	/** number of values */ 
	private long nbOfValues; 
	/**
	 * size in bytes <br> 
	 * only stored for variable length data types
	 * */ 
	private long sizeInBytes;

	/**
	 * 
	 * @param buffer
	 */
	public RawDataInfo(ByteBuffer buffer) {

		this.datatype 		= RawdataDataType.get(buffer.getInt());
		this.dimension 		= buffer.getInt();
		this.nbOfValues 	= buffer.getLong(); 
//		this.lenOfRawdata	= buffer.getInt(); 
		
		if (datatype == RawdataDataType.tdsTypeString) {
			// NOT SURE OF 
			this.sizeInBytes = buffer.getLong();
		}
	}


	/**
	 * @return the datatype
	 */
	public RawdataDataType getDatatype() {
		return datatype;
	}
	/**
	 * @return the lenOfRawdata
	 */
	public int getLenOfRawdata() {
		return lenOfRawdata;
	}
	/**
	 * @return the dimension
	 */
	public int getDimension() {
		return dimension;
	}
	/**
	 * @return the nbOfValues
	 */
	public long getNbOfValues() {
		return nbOfValues;
	}
	/**
	 * @return the sizeInBytes
	 */
	public long getSizeInBytes() {
		return sizeInBytes;
	}
	@Override
	public String toString() {
		return String.format("{lenRawData: %d, datatype: %s,dim: %d, nbOfValues: %d}",
				lenOfRawdata, 
				datatype.toString(), 
				dimension, 
				nbOfValues); 
	}
}
