import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

public class Downloader {
    /**
     * Initiate metadata file and iterate missing ranges. And pray to our lord and savior! (Lucifer, of course...)
     *
     * @param urls            URLs to download
     * @param numberOfWorkers number of concurrent connections
     */
    public static void DownloadURL(ArrayList<String> urls, int numberOfWorkers) {
        BlockingQueue<Chunk> outQueue = new LinkedBlockingQueue<>();
        TokenBucket tokenBucket = new TokenBucket();
        DownloadableMetadata metadata = new DownloadableMetadata(urls.get(0));
        FileWriter fileWriter = new FileWriter(metadata, outQueue);
        Thread fileWriterThread = new Thread(fileWriter);
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfWorkers);

        try {
            fileWriterThread.start();
            int i = 0;
            //each worker gets it's own range of the file
            for (Range range : metadata.getRangeList()) {
                Runnable worker = new HTTPRangeGetter(urls.get(i), range, outQueue);
                i++;
                i = i % urls.size();
                executorService.execute(worker);
            }

            executorService.shutdown();
        } catch (Exception e) {
            System.err.println("Download failed.");
            System.exit(-1);
        }
    }
}
