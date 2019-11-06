package fr.station47.cosmovotes.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class VoteUpdate extends Event{
    private static final HandlerList HANDLERS = new HandlerList();
    private int voteCount, objective;

    public VoteUpdate(int voteCount, int objective){
        this.voteCount = voteCount;
        this.objective = objective;
    }

    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    public int getVoteCount(){
        return voteCount;
    }

    public int getObjective(){
        return objective;
    }
}
