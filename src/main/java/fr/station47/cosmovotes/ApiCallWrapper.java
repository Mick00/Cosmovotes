package fr.station47.cosmovotes;

import fr.station47.theme.Theme;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class ApiCallWrapper {
    private ApiCall apiCall;
    private Thread thread;
    private int attempt, maxAttempt, waitTime;
    private String failMessage;
    private BukkitTask task;

    public ApiCallWrapper(Player player, String url, int maxAttempt, int waitTime, String failMessage){
        apiCall = new ApiCall(url,player, this);
        attempt = 0;
        this.maxAttempt = maxAttempt;
        this.waitTime = waitTime*20;
        this.failMessage = failMessage;
    }

    public void start(){
        thread = new Thread(apiCall);
        thread.start();
    }

    public void completed(Player player, String result){
        attempt++;
        if (result == null){
            if (attempt > maxAttempt){
                Theme.sendMessage(player,failMessage);
            } else {
                start();
            }
            return;
        }
        int vote = Integer.valueOf(result);

        new BukkitRunnable(){
            @Override
            public void run() {
                Cosmovotes.voteManager.addVoteFrom(player,vote);
            }
        }.runTask(Cosmovotes.instance);
    }

    public void cancel(){
        if (!task.isCancelled()) {
            task.cancel();
        }
    }
}
