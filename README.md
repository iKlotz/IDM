Internet download manager by:
Ido Klotz 
Abotzvi Yadgarov
*********************************************************************************************
Chunk.java - Used to deliver chunks of data from downloading threads to the writing thread.
DownloadableMetada.java - Describes the download status, the metadata involved in the download process during the runtime. 
FileWriter.java - Runnable class that writes downloaded data to the destination file. Invokes metadata update.
HttpRangeGetter.java - Runnable class that downloads a specific range of bytes from a url, then transfers the data to a queue to be written to disk
IdcDm.class - Main class. Initiates the objects and starts the threads.
Range.class - Describes a range of bytes. Used for metadata handling and range downloading.
TokenBucket.class - Describes a synchronized token bucket.
