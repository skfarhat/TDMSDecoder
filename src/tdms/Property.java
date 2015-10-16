package tdms;

import java.io.IOException;
import java.nio.ByteBuffer;

import org.apache.log4j.Logger;

/**
 * Composition: 
 * 
 * <ul> 
 * <li> Property Name :	binary[length, name]</li> 
 * <li> Property Value: binary[datatype, length, value]</li>
 * </ul> 
 * @author Sami
 *
 */
public class Property {

	@SuppressWarnings("unused")
	private static Logger logger = Logger.getLogger(Property.class); 
	
	/** name of the property */ 
	private String 			name; 
	/** datatype of the property */ 
	private RawdataDataType datatype;
	
	/** value contained here can be int, long, string.. or other
	 * string is used for convenience of casting when needed
	 * */ 
	private String value; 
	
	public Property(ByteBuffer buffer) throws IOException {
		/* read name */ 
		int nName = buffer.getInt(); 
		this.name = TDMSManager.readString(buffer, nName); 
		/* read property data type */ 
		this.datatype = RawdataDataType.get(buffer.getInt()); 
		
		if (datatype == RawdataDataType.tdsTypeString) {
			/* number of bytes in the string */ 
			int nStr = buffer.getInt();
			
			/* set the field value */ 
			this.value = TDMSManager.readString(buffer, nStr);
			
		} else { 
			/* get the length of the datatype and based on that decide how to read */
			int datatypeLength = datatype.typeLength();
			byte b[] = new byte[datatypeLength]; 
			buffer.get(b, 0, datatypeLength);
			
			/* set the field value in a string */ 
			this.value = new String(b); 
		}
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the datatype
	 */
	public RawdataDataType getDatatype() {
		return datatype;
	}

	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}

	@Override
	public String toString() {
		return String.format("name: %s\ndatatype: %s\nvalue: %s\n", name, datatype.toString(), getValue()); 
	}
	
}
