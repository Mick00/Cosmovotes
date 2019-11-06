package fr.station47.cosmovotes;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;

public class ApiCall implements Runnable {

    private Player player;
    private String url;
    private ApiCallWrapper wrapper;
    ApiCall(String url, Player player, ApiCallWrapper wrapper){
        this.url = url;
        this.player = player;
        this.wrapper = wrapper;
    }

    @Override
    public void run() {
        String rawIp = player.getAddress().toString();
        String ip = rawIp.substring(rawIp.indexOf('\\') + 2, rawIp.indexOf(':'));
        String unclaimed = makeApiCall(url,"claims",ip);
        new BukkitRunnable() {
            @Override
            public void run() {
                wrapper.completed(player, unclaimed);
            }
        }.runTask(Cosmovotes.instance);
    }

    private String makeApiCall(String url,String operation,String ip){
        url += "?operation="+operation+"&ip="+ip;
        BufferedReader reader = (BufferedReader) getApiReader(url);
        try {
            return reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Reader getApiReader(String url) {
        URLConnection connection = null;
        try {
            connection = new URL(url).openConnection();
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
            return new BufferedReader(new InputStreamReader(connection.getInputStream(), Charset.forName("UTF-8")));

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
