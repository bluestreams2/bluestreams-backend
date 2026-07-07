package com.spmf;

import com.spmf.tv.IPTVChannel;
import com.spmf.tv.IPTVSource;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;

@ApplicationScoped
public class IPTVImportService {

    public void importFromUrl(String providerName, String url) throws Exception {

        try (BufferedReader reader =
                     new BufferedReader(
                             new InputStreamReader(
                                     new URL(url).openStream(),
                                     StandardCharsets.UTF_8))) {

            importPlaylist(providerName, url, reader);
        }
    }

    @Transactional
    public void importFromStream(InputStream inputStream,
                                 String sourceName) throws Exception {

        try (
                BufferedReader reader =
                        new BufferedReader(
                                new InputStreamReader(
                                        inputStream,
                                        StandardCharsets.UTF_8
                                )
                        )
        ) {

            IPTVSource source =
                    IPTVSource.find("name", sourceName)
                            .firstResult();

            if (source == null) {

                source = new IPTVSource();
                source.name = sourceName;
                source.persist();

            }

            parsePlaylist(reader, source);

        }

    }

    private void parsePlaylist(
            BufferedReader reader,
            IPTVSource source
    ) throws Exception {

        String line;

        String name = "";
        String logo = "";
        String group = "";

        while ((line = reader.readLine()) != null) {

            line = line.trim();

            if (line.isBlank()) {
                continue;
            }

            if (line.startsWith("#EXTINF")) {

                name = "";

                logo = "";

                group = "";

                int comma =
                        line.lastIndexOf(',');

                if (comma >= 0) {

                    name =
                            line.substring(comma + 1)
                                    .trim();

                }

                logo =
                        extract(line,
                                "tvg-logo=\"",
                                "\"");

                group =
                        extract(line,
                                "group-title=\"",
                                "\"");

            }
            else if (!line.startsWith("#")) {

                IPTVChannel existing =
                        IPTVChannel.find(
                                "streamUrl",
                                line
                        ).firstResult();

                if (existing == null) {

                    IPTVChannel channel =
                            new IPTVChannel();

                    channel.name = name;

                    channel.logo = logo;

                    channel.groupName = group;

                    channel.streamUrl = line;

                    channel.sourceId = source.id;
                    channel.source = source.name;

                    channel.enabled = true;

                    System.out.println("---------------");
                    System.out.println("name      = " + safeLength(channel.name));
                    System.out.println("logo      = " + safeLength(channel.logo));
                    System.out.println("groupName = " + safeLength(channel.groupName));
                    System.out.println("groupTitle= " + safeLength(channel.groupTitle));
                    System.out.println("source    = " + safeLength(channel.source));
                    System.out.println("streamUrl = " + safeLength(channel.streamUrl));
                    System.out.println("tvgId     = " + safeLength(channel.tvgId));
                    channel.persist();

                } else {

                    existing.name = name;

                    existing.logo = logo;

                    existing.groupName = group;

                    existing.sourceId = source.id;
                    existing.source = source.name;

                    existing.enabled = true;

                }

            }

        }

    }

    private String extract(
            String text,
            String begin,
            String end
    ) {

        int start = text.indexOf(begin);

        if (start < 0) {
            return "";
        }

        start += begin.length();

        int finish =
                text.indexOf(end, start);

        if (finish < 0) {
            return "";
        }

        return text.substring(start, finish);

    }

    @Transactional
    public void importFromText(
            String sourceName,
            String content
    ) throws Exception {

        importFromStream(

                new ByteArrayInputStream(
                        content.getBytes(
                                StandardCharsets.UTF_8
                        )
                ),

                sourceName

        );

    }

    private int safeLength(String s) {
        return s == null ? 0 : s.length();
    }

    @Transactional
    void importPlaylist(
            String providerName,
            String url,
            BufferedReader reader) throws Exception {

        IPTVSource source =
                IPTVSource.find("name", providerName).firstResult();

        if (source == null) {
            source = new IPTVSource();
            source.name = providerName;
            source.url = url;
            source.persist();
        } else {
            source.url = url;
        }

        parsePlaylist(reader, source);
    }

}