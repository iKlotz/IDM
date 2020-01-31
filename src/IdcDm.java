import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.*;

public class IdcDm {

    /**
     * Receive arguments from the command-line, provide some feedback and start the download.
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        int numberOfWorkers = 1;

        if (args.length < 1 || args.length > 3) {
            System.err.printf("usage:\n\tjava IdcDm URL [MAX-CONCURRENT-CONNECTIONS]\n");
            System.exit(1);
        } else if (args.length >= 2) {
            numberOfWorkers = Integer.parseInt(args[1]);
        }

        String line = "";
        ArrayList<String> links = new ArrayList<>();

        //get the links, utils?
        try {
            File file = new File(args[0]);

            if (file.exists()) {

                BufferedReader bufferedReader = new BufferedReader(new FileReader(file));

                while ((line = bufferedReader.readLine()) != null)
                {
                    links.add(line);
                }
                // close the BufferedReader when we're done
                bufferedReader.close();
            } else {
                links.add(args[0]);
            }
//my debugging
//            for (String link : links) {
//                System.out.println(link);
//            }

        } catch (IOException e) {
            e.printStackTrace();
        }


        System.err.printf("Downloading");
        if (numberOfWorkers > 1) {
            System.err.printf(" using %d connections...\n", numberOfWorkers);
        }


        System.err.printf("Start downloading range ( 0 - %d ) from: ", Utility.getFileSize(links.get(0)));
        Utility.printLinks(links);

        DownloadURL(links, numberOfWorkers);
    }

    /**
     * Initiate metadata file and iterate missing ranges. And pray to good that's the only way to succeed.
     * @param urls            URL to download
     * @param numberOfWorkers number of concurrent connections
     */
    private static void DownloadURL(ArrayList<String> urls, int numberOfWorkers) {
        BlockingQueue<Chunk> outQueue = new LinkedBlockingQueue<>();
        TokenBucket tokenBucket = new TokenBucket();
        DownloadableMetadata metadata = new DownloadableMetadata(urls.get(0));
        FileWriter fileWriter = new FileWriter(metadata, outQueue);
        Thread fileWriterThread = new Thread(fileWriter);
        ExecutorService executor = Executors.newFixedThreadPool(numberOfWorkers);

        try {
            fileWriterThread.start();
            for(int i = 0; i < urls.size(); i++){
                for (Range range : metadata.getRangeList()) {
                    Runnable worker = new HTTPRangeGetter(urls.get(i), range, outQueue, tokenBucket);
                    if (i != urls.size() - 1){
                        i++;
                    } else {
                        i = 0;
                    }
                    executor.execute(worker);
                }
            }

            executor.shutdown();
        } catch (Exception e) {
            System.err.println("Download failed.");
            System.exit(-1);
        }
    }
}
