package tdms;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;


/**
 * May also be referred to as Object in LabView's documentation
 * 
 * Composition:
 * <ul> 
 * <li> Object Path (first parse the length of the path, then the path string) </li>
 * <li> Raw Data Index: info about data block composition (channel, data, # of values in block, and order) </li> 
 * <li> Number of Properties </li> 
 * <li> Array of Properties </li> 
 * </ul>  
 * @author Sami
 *
 */
public class Channel {



	/** Log4j */  
	Logger logger 			= Logger.getLogger(Channel.class); 


	/** this map stores all RawDataInfo objects created
	 * when a new Channel has rawDataIndex = 0x0000 it fetches its RawDataInfo from the map 
	 */
	private static HashMap<String, RawDataInfo> rawDataInfoMap = new HashMap<>(); 
	/**
	 * object path 
	 */
	private String objectPath;

	/**
	 * indicates how the rawdata is stored for this object. 
	 * 0x00000000 	: read the rawdata from previous object
	 * 0xFFFFFFFF	: empty 
	 * other		: new rawdata  
	 */
	private int rawDataIndex;
	/** rawdata associated with the object */
	private RawDataInfo rawDataInfo; 
	/** contains the properties of this object */ 
	private HashMap<String, Property> properties = new HashMap<String, Property>();    
	/**  list containing the data associated with the object  */
	private List<Object> dataList; 

	public Channel(ByteBuffer buffer) throws IOException {
		int lenOfPath = buffer.getInt();

		/* reads the passed number of bytes and returns a string */ 
		this.objectPath = TDMSManager.readString(buffer, lenOfPath);

		/** tells us how the rawdata is stored */ 
		this.rawDataIndex = buffer.getInt();

		/* no new data is passed */ 
		if (rawDataIndex == 0xFFFFFFFF) {
			logger.debug("0xFFFFFFFF");
		} else if (rawDataIndex == 0x00000000) {
			logger.debug("rawDataIndex = 0x00000");
			this.rawDataInfo = rawDataInfoMap.get(objectPath); 
		} else if (rawDataIndex == 0x69130000 || rawDataIndex == 0x69120000){
			logger.warn("rawDataIndex = " + rawDataIndex + " (DAQmx) this format is not supported!"); 
		}
		else { 
			logger.debug("rawDataIndex = " + rawDataIndex); 

			RawDataInfo info = new RawDataInfo(buffer);
			this.rawDataInfo = info;
			rawDataInfoMap.put(objectPath, info); 
		}

		/* number of data values that this object will contain */ 
		final int nbValues = (rawDataInfo == null)? 0 : (int) rawDataInfo.getNbOfValues();   

		/* initialise the dataList */ 
		dataList = new ArrayList<Object>(nbValues); 

		/* properties */ 
		/* number of properties to be read*/ 
		int nProperties = buffer.getInt(); 
		for (int i = 0; i < nProperties; i++) {

			/* creates property by reading the needed bytes from the buffer */ 
			Property property = new Property(buffer); 

			/* add the property to the list */ 
			properties.put(property.getName(), property); 
		}
	}


	public List<Object> getData() {

		/* get datatype of entries */ 
		RawdataDataType datatype = getDatatype();

		if (datatype == RawdataDataType.tdsTypeDoubleFloat) {
			List<Object> retList = new ArrayList<>();
			for (Object obj : dataList) { 
				retList.add((Double) obj); 
			}
			return retList; 
		} else if (datatype == RawdataDataType.tdsTypeI32) { 
			List<Object> retList = new ArrayList<>();
			for (Object obj : dataList) { 
				retList.add((Integer) obj); 
			}
			return retList; 
		} else if (datatype == RawdataDataType.tdsTypeSingleFloat) {
			List<Object> retList = new ArrayList<>();
			for (Object obj : dataList) { 
				retList.add((Float) obj); 
			}
			return retList; 
		}
		else { 
			logger.error("Other datatypes not yet implemented");
			return null; 
		}
	}
	/**
	 * adds data to the Obj list
	 * @param o of any type
	 */
	public void addData(Object o) {
		dataList.add(o); 
	}

	/**
	 * @return the objectPath
	 */
	public String getObjectPath() { return objectPath; }

	/**
	 * @return the rawDataIndex
	 */
	protected int getRawDataIndex() { return rawDataIndex; }

	public int nbOfProperties() { return properties.size(); }
	/**
	 * @return the properties
	 */
	public HashMap<String, Property> getProperties() {
		return properties;
	}

	protected RawDataInfo getRawDataInfo() { return rawDataInfo; }

	public RawdataDataType getDatatype() { return rawDataInfo.getDatatype(); } 

	/**
	 * 
	 * @return
	 */
	protected boolean isChannel() { return rawDataIndex != 0xFFFFFFFF; }

	@Override
	public String toString() {

		StringBuilder str = new StringBuilder();
		str.append("===============================\n"); 

		if (rawDataInfo != null)
			str.append(String.format("RawDataInfo: %s\n", rawDataInfo.getDatatype().toString()));

		str.append(String.format("RawDataIndex: %d\n", rawDataIndex));
		str.append(String.format("ObjectPath: %s\n", objectPath));
		str.append(String.format("rawDataInfo %s\n" , rawDataInfo )); 
		str.append(String.format("nProperties: %d\n", nbOfProperties()));
		str.append(String.format("==============================="));

		return str.toString(); 
	}
}
