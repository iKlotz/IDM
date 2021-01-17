import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.BlockingQueue;

/**
 * A runnable class which downloads files from a given url.
 * It reads CHUNK_SIZE at a time and writs it into a BlockingQueue.
 * It supports downloading a range of data, and limiting the download rate using a token bucket.
 */

public class HTTPRangeGetter implements Runnable {
    static final int CHUNK_SIZE = 65536; //64k
    private static final int CONNECT_TIMEOUT = 10000;
    private static final int READ_TIMEOUT = 10000;
    private final String url;
    private final Range range;
    private final BlockingQueue<Chunk> outQueue;

    HTTPRangeGetter(String url, Range range, BlockingQueue<Chunk> outQueue) {
        this.url = url;
        this.range = range;
        this.outQueue = outQueue;
    }

    private void downloadRange() throws IOException, InterruptedException {
        try {
            URL url = new URL(this.url);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // set connection props
            connection.setConnectTimeout(CONNECT_TIMEOUT);
            connection.setReadTimeout(READ_TIMEOUT);
            connection.setInstanceFollowRedirects(true);
            connection.setRequestProperty("Range", "bytes=" + this.range.getStart() + "-" + this.range.getEnd());

            if (connection.getResponseCode() / 100 == 2) {
                InputStream inputStream = connection.getInputStream();
                long readBytes = 0;
                long bytesToRead = range.getLength();
                while (readBytes < bytesToRead) {
                    byte[] data = new byte[CHUNK_SIZE];
                    long offset = range.getStart() + readBytes;
                    int sizeInBytes = inputStream.read(data); //reads data and returns number of bytes successfully read as an int

                    // check for EOF
                    if (sizeInBytes == -1) {
                        break;
                    }

                    readBytes += sizeInBytes;
                    Chunk outChunk = new Chunk(data, offset, sizeInBytes, this.range);
                    outQueue.put(outChunk);
                }

                inputStream.close();
                connection.disconnect();
            }
        } catch (IOException e) {
            String err = "Couldn't fetch range starting at :" + this.range.getStart() + " and ending at: " + this.range.getEnd() + ". Download failed.";
            throw new IOException(err);
        } catch (InterruptedException e) {
            String err = "Runtime interruption. Download failed.";
            throw new InterruptedException(err);
        }
    }


    @Override
    public void run() {
        try {
            this.downloadRange();
        } catch (IOException | InterruptedException e) {
            System.err.println(e);
            System.exit(-1);
        }
    }
}
