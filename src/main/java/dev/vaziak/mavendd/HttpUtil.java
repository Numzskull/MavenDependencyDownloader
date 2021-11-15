package dev.vaziak.mavendd;

import lombok.experimental.UtilityClass;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.Channels;

@UtilityClass
public class HttpUtil {
    public boolean existsOnWeb(URL url) throws IOException {
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

        httpURLConnection.setRequestProperty("User-Agent",
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/74.0.3729.169 Safari/537.36");

        return httpURLConnection.getResponseCode() == 200;
    }

    /**
     * @author sim0n
     */
    public void saveToFile(URL url, File file) throws IOException {
        FileOutputStream outputStream = new FileOutputStream(file);

        outputStream.getChannel().transferFrom(Channels.newChannel(url.openStream()), 0L, Long.MAX_VALUE);
    }
}
