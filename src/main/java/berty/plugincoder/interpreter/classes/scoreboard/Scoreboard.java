package berty.plugincoder.interpreter.classes.scoreboard;

import org.bukkit.Bukkit;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import java.util.WeakHashMap;


public class Scoreboard{

    private static WeakHashMap<org.bukkit.scoreboard.Scoreboard,Scoreboard> scoreboardInstances=new WeakHashMap<>();

    private String title;

    private org.bukkit.scoreboard.Scoreboard bukkitScoreboard;

    public Scoreboard(String title) {
        this.title=title;
        this.bukkitScoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective objective = bukkitScoreboard.registerNewObjective("dummy", "dummy");
        objective.setDisplayName(title);
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        scoreboardInstances.put(bukkitScoreboard,this);
    }
    public void addLine(int score,String text) {
        bukkitScoreboard.getObjective("dummy").getScore(text).setScore(score);
    }
    public void clear() {
        bukkitScoreboard.getEntries().stream().forEach(entry-> bukkitScoreboard.resetScores(entry));
    }
    public void hide(){
        bukkitScoreboard.getObjective("dummy").setDisplaySlot(null);
    }
    public void show(){
        bukkitScoreboard.getObjective("dummy").setDisplaySlot(DisplaySlot.SIDEBAR);
    }
    public String getTitle(){
        return title;
    }
    public void setTitle(String title){
        this.title=title;
        bukkitScoreboard.getObjective("dummy").setDisplayName(title);
    }
    @Override
    public String toString(){
        String entries="";
        for(String entry: bukkitScoreboard.getEntries()){
            entries+=entry+",";
        }
        if(bukkitScoreboard.getEntries().size()>0)entries=entries.substring(0,entries.length()-1);
        return "Scoreboard["+entries+"]";
    }

    public org.bukkit.scoreboard.Scoreboard getBukkitScoreboard() {
        return bukkitScoreboard;
    }
    public static Scoreboard getScoreboard(org.bukkit.scoreboard.Scoreboard bukkitScoreboard){
        return scoreboardInstances.get(bukkitScoreboard);
    }

    public void clone(Scoreboard scoreboard) {
        scoreboard.setTitle(title);
        scoreboard.clear();
        for(String entry: bukkitScoreboard.getEntries()){
            int score=bukkitScoreboard.getObjective("dummy").getScore(entry).getScore();
            scoreboard.addLine(score,entry);
        }
    }
}
