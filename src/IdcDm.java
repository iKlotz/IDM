import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class IdcDm {
    /**
     * Receives arguments from the command-line, provide some feedback and start the download.
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        int numberOfWorkers = 1;

        if (args.length < 1 || args.length > 3) {
            System.err.printf("usage:\n\tjava IdcDm URL | URL-LIST-FILE [MAX-CONCURRENT-CONNECTIONS]\n");
            System.exit(1);
        } else if (args.length >= 2) {
            numberOfWorkers = Integer.parseInt(args[1]);
        }

        String line = "";
        ArrayList<String> links = new ArrayList<>();

        try {
            File file = new File(args[0]);

            if (file.exists()) {
                BufferedReader bufferedReader = new BufferedReader(new FileReader(file));

                while ((line = bufferedReader.readLine()) != null) {
                    //read the links from the provided file
                    links.add(line);
                }

                bufferedReader.close();
            } else {
                //if only a single link is provided
                links.add(args[0]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


        System.err.println("Downloading:");
        if (numberOfWorkers > 1) {
            System.err.printf("Using %d connections...\n", numberOfWorkers);
        }

        System.err.printf("File size is %d byte: \n", Utility.getFileSize(links.get(0)));
        System.err.printf("Start downloading from: \n", Utility.getFileSize(links.get(0)));
        Utility.printLinks(links);

        Downloader.DownloadURL(links, numberOfWorkers);
    }
}
