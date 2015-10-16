package tdms;

import java.nio.ByteBuffer;

import org.apache.log4j.Logger;

public class RawData {

	private static Logger logger = Logger.getLogger(RawData.class); 

	public RawData(ByteBuffer buffer, LeadIn leadin, ChannelReader reader, Metadata metadata) {
		int filesize = reader.filesize(); 

		long chunkSize = 0; 
		for(Channel o : reader.getChannels()) {
			logger.debug("Channel: " + o.getObjectPath());

			long nbOfValues = o.getRawDataInfo().getNbOfValues();
			int dim 		= o.getRawDataInfo().getDimension();
			int typeSize 	= o.getRawDataInfo().getDatatype().typeLength();

			long channelSize = nbOfValues * dim * typeSize;
			chunkSize += channelSize;

		}

		// TODO: recheck
		if (chunkSize == 0) {
			logger.warn("attempt to divide by zero");
			return; 
		}
		long rawDataOffset = leadin.getLenOfMetadata(); 

		// total filesize
		long size = leadin.getLenRemainSegment(); 
		if (leadin.getLenRemainSegment() == -1) { 
			logger.debug("leadin-getLenRemainSegment()"); 
			size = filesize;
		}
		long totalChunks = size - rawDataOffset; 
		
		// TODO: recheck
		int nChunks; 
		if (totalChunks == 0) 	nChunks = 0; 
		else  					nChunks = (int) (totalChunks / chunkSize);	// floor division

		if (totalChunks % chunkSize != 0) { 
			logger.debug("Data size is not a multiple of the chunk size: " + totalChunks % chunkSize);
		}
		logger.debug("ChunkSize: " 		+ chunkSize);
		logger.debug("Size: " + size 	+ " RawDataOffset: " + rawDataOffset);
		logger.debug("totalChunks: " 	+ totalChunks); 
		logger.debug("nChunks: " 		+ nChunks); 

		//
		//		/* intialise 
		//		 * arrays for each channel
		//		 */
		//		for (Obj o : reader.getChannels()) {
		//			long nbOfValues = o.getRawDataInfo().getNbOfValues();
		//			int typeSize 	= o.getRawDataInfo().getDatatype().typeLength();
		//			int dataSize 	= (int) (nbOfValues * typeSize);
		//
		//			channelData.add(ByteBuffer.allocate(dataSize)); 
		//		}

		if (leadin.hasInterleavedData()) { 

			logger.debug("Interleaved IN DEBUG");
			logger.debug("nChunks: " + nChunks);

			Channel channel = reader.getChannels().iterator().next();
			RawdataDataType datatype = channel.getRawDataInfo().getDatatype();

			interleavedData(buffer, datatype, reader, chunkSize,
					channel.getRawDataInfo().getNbOfValues(), 
					nChunks);

		} else { 
			logger.debug("Not interleaved");
			/* we extract the raw data for each object in the order of appearance of the objects (in metadata) */ 
			for (int i = 0; i < nChunks; i++) {

				for (Channel channel : reader.getChannels()) { 

					RawdataDataType datatype = channel.getRawDataInfo().getDatatype();

					if (datatype == RawdataDataType.tdsTypeDoubleFloat) {
						addDoublesToObject(buffer, channel);
					} 
					else if (datatype == RawdataDataType.tdsTypeI32) {
						addInt32ToObject(buffer, channel);
					}
					else if (datatype == RawdataDataType.tdsTypeSingleFloat) {
						addFloatToObject(buffer, channel);
					}
					else { 
						logger.warn("RawData: Other types not implemented");
					}
				}
			}
		}
	}

	private void interleavedData(ByteBuffer buffer, RawdataDataType datatype,
			ChannelReader reader, long chunkSize, long nbOfElements, long nChunkSize) {

		for (int i = 0; i < nbOfElements; i++) {
			for (int j = 0; j < nChunkSize; j++) { 
				if (datatype == RawdataDataType.tdsTypeSingleFloat) {
					for (Channel channel : reader.getChannels()) {
						float val = buffer.getFloat();
						channel.addData(val);
					}
				}
				else if (datatype == RawdataDataType.tdsTypeDoubleFloat) {
					for (Channel channel : reader.getChannels()) {
						double val = buffer.getDouble();
						channel.addData(val);
					}
				}
				else if (datatype == RawdataDataType.tdsTypeI32) { 
					for (Channel channel : reader.getChannels()) {
						int val = buffer.getInt();
						channel.addData(val);
					}
				}
				else { 
					logger.warn("datatype: " + datatype + " is not supported."); 
				}
			}
		}
	}

	private void addInt32ToObject(ByteBuffer buffer, Channel o) {

		/* 	number of values to read */ 
		final long nbOfValues 	= o.getRawDataInfo().getNbOfValues();			 

		/* read all the values from Bytebuffer and put them in a List */ 
		for (int j = 0; j < nbOfValues ; j++) {
			int val = buffer.getInt();
			o.addData(val);
		}
	}

	private void addFloatToObject(ByteBuffer buffer, Channel o) {

		/* 	number of values to read */ 
		final long nbOfValues 	= o.getRawDataInfo().getNbOfValues();			 

		/* read all the values from Bytebuffer and put them in a List */ 
		for (int j = 0; j < nbOfValues ; j++) {
			float val = buffer.getFloat();
			o.addData(val);
		}
	}

	private void addDoublesToObject(ByteBuffer buffer, Channel o) {
		/* 	number of values to read */ 
		final long nbOfValues 	= o.getRawDataInfo().getNbOfValues(); 

		/* read all the values from Bytebuffer and put them in a List */ 
		for (int j = 0; j < nbOfValues ; j++) {
			double val = buffer.getDouble();
			o.addData(val);
		}
	}

}
