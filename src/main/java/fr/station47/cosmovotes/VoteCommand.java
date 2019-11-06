package fr.station47.cosmovotes;

import fr.station47.stationAPI.api.Utils;
import fr.station47.stationAPI.api.commands.MainCommand;
import fr.station47.stationAPI.api.commands.SubCommand;
import fr.station47.stationAPI.api.config.ConfigObject;
import fr.station47.theme.Theme;
import org.apache.commons.lang.StringEscapeUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.utils.PlayerConverter;

import java.util.Map;

public class VoteCommand extends MainCommand {
    private VoteManager vm;
    private ConfigObject config;
    public VoteCommand(VoteManager vm, ConfigObject config) {
        super("vote", Cosmovotes.get());
        this.vm = vm;
        this.config = config;
        addSubcommands(addVotes());
        addSubcommands(setVotes());
        addSubcommands(verifyVotes());
        addSubcommands(getCount());
    }

    private SubCommand setVotes(){
        return new SubCommand("setobjectif","nombreDeVote; Defini le nombre de vote avant d'atteindre l'objectif de cosmovote","cosmovotes.admin") {
            @Override
            public boolean executeCommand(CommandSender sender, String[] args) {
                if(args.length > 0){
                    if (Utils.isInt(args[0])) {
                        vm.setObjective(Integer.parseInt(args[0]));
                        return true;
                    }
                }
                return false;
            }
        };
    }

    private SubCommand addVotes(){
        return new SubCommand("add","nombreDeVote; Ajoute le nombre de vote","cosmovotes.admin") {
            @Override
            public boolean executeCommand(CommandSender sender, String[] args) {
                if(args.length > 0){
                    if (Utils.isInt(args[0])) {
                        vm.addVote(Integer.parseInt(args[0]));
                        return true;
                    }
                }
                return false;
            }
        };
    }

    private SubCommand verifyVotes(){
        return new SubCommand("claim",";Pour recevoir vos cadeaux","none") {
            @Override
            public boolean executeCommand(CommandSender sender, String[] args) {
                if (args.length>0){
                    Player p = Bukkit.getPlayer(args[0]);
                    if (sender.hasPermission("cosmovotes.verify.other") && p != null){
                        vm.verifyPlayer(p);
                    } else {
                        Theme.sendMessage(sender,Theme.permissionDenied);
                    }
                } else if(sender instanceof Player){
                    vm.verifyPlayer((Player)sender);
                }
                return true;
            }
        };
    }

    private SubCommand getCount(){
        return new SubCommand("count","; Affiche le nombre et l'objectif de cosmovote actuel","none") {
            @Override
            public boolean executeCommand(CommandSender sender, String[] args) {
                Theme.sendMessage(sender,Utils.fill(config.getString("messages.objective"),Utils.fill(config.getString("messages.objectiveFormat"),String.valueOf(vm.getVoteCount()),String.valueOf(vm.getObjective()))));
                return true;
            }
        };
    }
    @Override
    protected boolean noArgs(CommandSender sender){
        Theme.sendMessage(sender,Utils.fill(config.getString("messages.objective"),Utils.fill(config.getString("messages.objectiveFormat"),String.valueOf(vm.getVoteCount()),String.valueOf(vm.getObjective()))));
        sender.sendMessage(config.getString("messages.header"));
        BetonQuest.getInstance().getPlayerData(PlayerConverter.getID(sender.getName())).addTag("default.opened_vote");
        for (Map.Entry<String,String> entry:vm.getVotingSites().entrySet()){
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(),"tellraw "+sender.getName()+" {\"text\":\""+entry.getKey()+"\",\"color\":\"blue\",\"clickEvent\":{\"action\":\"open_url\",\"value\":\""+ StringEscapeUtils.escapeJava(entry.getValue())+"\"}}");
        }
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(),"tellraw "+sender.getName()+" {\"text\":\""+config.getString("messages.clickToVerify")+"\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/vote claim\"}}");
        return true;
    }

}
