package fr.station47.cosmovotes;

import fr.station47.cosmovotes.events.VoteUpdate;
import fr.station47.stationAPI.api.config.ConfigObject;
import fr.station47.theme.Theme;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class VoteManager {
    private int objective, voteCounter;
    private ConfigObject config;
    private ConfigObject counterConfig;
    private String playerReward, partyReward, url, broadcastReward, error;
    private HashMap<String,String> votingSites;

    public VoteManager(){
        counterConfig = new ConfigObject();
        counterConfig.put("counter",0);
        Cosmovotes.configs.loadOrDefault("votes", counterConfig);
        voteCounter = counterConfig.getInt("counter");
        config = new ConfigObject();
        config.put("url","http://votes.station47.net:2000/kHdKNawFHlLoN65XsWvR/api/");
        config.put("objective",10);
        config.put("playerReward","cr give {p} VoteCrate {q}");
        config.put("partyReward","boost add money_and_exp 2 3000 Objectif de cosmovotes atteint");
        config.put("jobBoostReason","L'objectif de cosmovote rempli");
        config.put("messages.verifying","Un instant, nous vérifions vos votes");
        config.put("messages.broadcast","{p} a reçu {q} caisse(s) de vote. Faites pareil et votez pour recevoir plein de récompenses!");
        config.put("messages.error","&cUne erreure est survenue! Impossible de vous donner vos cadeaux :(");
        config.put("messages.header","Voici la liste des sites sur lesquels vous pouvez voter");
        config.put("messages.clickToVerify","Cliquer ici pour faire vérifier vos votes");
        config.put("messages.noVotes","Aucun vote non-réclamé détecté");
        config.put("messages.objectiveFormat","{0}/{1}");
        config.put("messages.objective","Les compteur de cosmovotes est à {0}");
        List<String> urls = new ArrayList<>();
        urls.add("Site #1:http://www.serveurs-minecraft.org/vote.php?id=54796");
        urls.add("Site #2:https://www.serveurs-minecraft.com/serveur-minecraft?Classement=Station47");
        urls.add("Site #3:http://www.serveursminecraft.org/serveur.php?id=582");
        urls.add("Site #4:http://www.liste-serveurs-minecraft.org/serveur-minecraft/station47/");
        urls.add("Site #5:http://www.liste-serveur-minecraft.net/serveur-minecraft?Liste-Serveur-Minecraft=Station%2047");
        config.put("listUrl",urls);
        Cosmovotes.configs.loadOrDefault("config",config);
        objective = config.getInt("objective");
        partyReward = config.getString("partyReward");
        playerReward = config.getString("playerReward");
        url =  config.getString("url");
        broadcastReward = config.getString("messages.broadcast");
        error = config.getString("messages.error");
        votingSites = new HashMap<>();
        for (String site : config.getStringList("listUrl")) {
            String[] nameAndUrl = site.split(":");
            votingSites.put(nameAndUrl[0], String.join(":",Arrays.copyOfRange(nameAndUrl,1,nameAndUrl.length)));
        }
        new VoteCommand(this,config);
    }

    void addVoteFrom(Player p, int votes){
        if (votes > 0) {
            addVote(votes);
            rewardPlayer(p, votes);
        } else {
            Theme.sendMessage(p,config.getString("messages.noVotes"));
        }
    }

    private void rewardPlayer(Player p, int votes){
        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(),parse(playerReward,p.getName(),votes));
        Theme.broadcast(parse(broadcastReward,p.getDisplayName(),votes));

    }

    void addVote(int newVote){
        int totalCount = newVote + voteCounter;
        if (totalCount >= objective){
            objectiveReached();
            voteCounter = totalCount%objective;

        } else {
            voteCounter = totalCount;
        }
        voteAdded();
    }

    void verifyPlayer(Player player){
        ApiCallWrapper claim = new ApiCallWrapper(player,url,1,1,error);
        claim.start();
    }

    public int getVoteCount(){
        return voteCounter;
    }

    public int getObjective(){
        return objective;
    }

    public HashMap<String, String> getVotingSites(){
        return votingSites;
    }

    public void setObjective(int objective){
        this.objective = objective;
        if (voteCounter >= objective){
            voteCounter%=objective;
            objectiveReached();
        }
        voteAdded();
    }
    private void voteAdded(){
        Bukkit.getPluginManager().callEvent(new VoteUpdate(voteCounter,objective));
        counterConfig.put("counter",voteCounter);
        Cosmovotes.configs.applyModification("votes",counterConfig);
        Cosmovotes.configs.save("votes");
    }

    private void objectiveReached(){
        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(),partyReward);
    }

    private String parse(String template, String p, int q){
        return template.replace("{p}", ChatColor.stripColor(p)).replace("{q}", String.valueOf(q));
    }
}
