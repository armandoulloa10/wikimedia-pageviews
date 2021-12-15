package com.armandoulloa.wikimediapageviews.service;

import com.armandoulloa.wikimediapageviews.model.Pageview;
import com.armandoulloa.wikimediapageviews.util.PageviewsUtils;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *
 * @author Armando
 */
public class PageviewsService {
    public static void main(String[] args) throws Exception {
        String BASE_PATH = "https://dumps.wikimedia.org/other/pageviews/";
        int TOP = 100;
        int LAST_X_HOURS = 5;
        int ZONE_ID = 0;
        Boolean FILE_EXISTS;

        List<Pageview> listViews;
        List<Pageview> totalListViews = new ArrayList<>();

        while (LAST_X_HOURS > 0) {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("uuuuMMdd-HH0000");
            DateTimeFormatter df = DateTimeFormatter.ofPattern("uuuu/uuuu-MM/");
            ZonedDateTime datetime = ZonedDateTime.now().withZoneSameInstant(ZoneId.of("UTC" + "-" + ZONE_ID));

            String PATH_DATE = df.format(datetime);
            String FILE_NAME = "pageviews-" + dtf.format(datetime);
            String FILE_GZ_NAME = FILE_NAME + ".gz";
            String FILE_URL = BASE_PATH + PATH_DATE + FILE_GZ_NAME;

            Path source = Paths.get(FILE_GZ_NAME);
            Path target = Paths.get(FILE_NAME);

            ReadableByteChannel rbc = null;
            //validate if the file is uploaded
            try {
                URL website = new URL(FILE_URL);
                rbc = Channels.newChannel(website.openStream());
                FILE_EXISTS = true;
            } catch (IOException e) {
                System.out.println("File " + FILE_GZ_NAME + " not loaded yet, the next one will be searched.");
                FILE_EXISTS = false;//the current time file is not yet loaded
            }

            if (FILE_EXISTS) {
                datetime = ZonedDateTime.now().withZoneSameInstant(ZoneId.of("UTC" + "-" + ZONE_ID));
                FILE_NAME = "pageviews-" + dtf.format(datetime);

                long init = System.currentTimeMillis();

                FileOutputStream fos = new FileOutputStream(FILE_GZ_NAME);
                fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
                fos.close();
                rbc.close();

                PageviewsUtils.decompressGzipNio(source, target);

                Stream<String> stream = Files.lines(Paths.get(FILE_NAME));
                //1. Filter pages with more than 1 view
                //2. Convert all lines to "View" object 
                //3. Add to the list
                listViews = stream
                        .filter(line -> !line.endsWith(" 1 0"))
                        .map(line -> new Pageview(line.split("\\s+")[0], line.split("\\s+")[1], Integer.parseInt(line.split("\\s+")[2])))
                        .collect(Collectors.toList());
                LAST_X_HOURS--;
                totalListViews.addAll(listViews);

                long finish = System.currentTimeMillis();
                long diffSeconds = (finish - init) / 1000 % 60;
                long diffMinutes = (finish - init) / (60 * 1000) % 60;
                System.out.println("File " + FILE_GZ_NAME + " processed in: " + diffMinutes + " min " + diffSeconds + " sec");
            }
            ZONE_ID++;
        }

        List<Pageview> sortedList = totalListViews.stream()
                .sorted(Comparator.comparingInt(Pageview::getCountViews)
                        .reversed())
                .collect(Collectors.toList());

        String format = "%-10s %-15s %-30s %10s\n";
        System.out.format(format, "TOP", "DOMAIN_CODE", "PAGE_TITLE", "VIEWS_COUNT");
        int i = 1;
        for (Pageview view : sortedList) {
            System.out.format(format, i, view.getDomainCode(), view.getPageTitle(), view.getCountViews());
            if (i++ >= TOP) {
                break;
            }
        }
    }
}
