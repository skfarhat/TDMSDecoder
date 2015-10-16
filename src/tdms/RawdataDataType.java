package tdms;

/**
 * 
 * @author Sami
 *
 */
public enum RawdataDataType {
	INVALID(-1), 
	tdsTypeVoid(0),
	tdsTypeI8(1),    
	tdsTypeI16(2),
	tdsTypeI32(3),    
	tdsTypeI64(4),
	tdsTypeU8(5),    
	tdsTypeU16(6),    
	tdsTypeU32(7),    
	tdsTypeU64(8),
	tdsTypeSingleFloat(9),    
	tdsTypeDoubleFloat(10),    
	tdsTypeExtendedFloat(11),    
	tdsTypeSingleFloatWithUnit(0x19),
	tdsTypeDoubleFloatWithUnit(13),
	tdsTypeExtendedFloatWithUnit(14),
	tdsTypeString(0x20),   
	tdsTypeBoolean(0x21),   
	tdsTypeTimeStamp(0x44),
	tdsTypeFixedPoint(0x4F),
	tdsTypeComplextSingleFloat(0x08000c),
	tdsTypeComplextDoubleFloat(0x10000d),
	tdsTypeDAQmxRawData(0xFFFFFFFF); 

	private final int type; 
	private RawdataDataType(int type) { this.type = type;  }
	public int getType() { return type; }

	/**
	 * returns the RawdataDatatype based on the index 
	 * @param toC
	 * @return
	 */
	public static RawdataDataType get(int toC) { 
		switch (toC) { 
		case 0: return tdsTypeVoid;
		case 1: return tdsTypeI8;    
		case 2: return tdsTypeI16;
		case 3: return tdsTypeI32;    
		case 4: return tdsTypeI64;
		case 5: return tdsTypeU8;    
		case 6: return tdsTypeU16;    
		case 7: return tdsTypeU32;    
		case 8: return tdsTypeU64;
		case 9: return tdsTypeSingleFloat;    
		case 10: return tdsTypeDoubleFloat;    
		case 11: return tdsTypeExtendedFloat;    
		case 0x19: return tdsTypeSingleFloatWithUnit;
		case 13: return tdsTypeDoubleFloatWithUnit;
		case 14: return tdsTypeExtendedFloatWithUnit;
		case 0x20: return tdsTypeString;   
		case 0x21: return tdsTypeBoolean;   
		case 0x44: return tdsTypeTimeStamp;
		case 0x4F: return tdsTypeFixedPoint;
		case 0x08000c: return tdsTypeComplextSingleFloat;
		case 0x10000d: return tdsTypeComplextDoubleFloat;
		case 0xFFFFFFFF: return tdsTypeDAQmxRawData;
		default: 		 return INVALID; 
		}
	}

	/**
	 * 
	 * @return the length of the datatype <br> 
	 * works only for nonvariable data-types
	 */
	public int typeLength() {
		
		switch (this) {
		
		// 0 bytes
		case tdsTypeVoid: 	return 0;
		
		// 1 byte
		case tdsTypeBoolean:   
		case tdsTypeU8:    
		case tdsTypeI8: 	return 1;    
		
		// 2 bytes
		case tdsTypeU16:
		case tdsTypeI16: 	return 2;
		
		// 4 bytes
		case tdsTypeSingleFloat:     
		case tdsTypeSingleFloatWithUnit:
		case tdsTypeU32:    
		case tdsTypeI32: 	
			return 4;
		
		// 8 bytes
		case tdsTypeU64:
		case tdsTypeDoubleFloat:    
		case tdsTypeDoubleFloatWithUnit:
		case tdsTypeI64: 	return 8;

		// 16 bytes
		case tdsTypeTimeStamp: return 16;

		// A string has no predefined length 
		case tdsTypeString: return -1;   
		
		// Not Supported atm 
		case tdsTypeExtendedFloat:    
		case tdsTypeExtendedFloatWithUnit:
		case tdsTypeFixedPoint:
		case tdsTypeComplextSingleFloat:
		case tdsTypeComplextDoubleFloat:
		case tdsTypeDAQmxRawData:
		default: 		 
			return -1; 
		}
		
	}
}
