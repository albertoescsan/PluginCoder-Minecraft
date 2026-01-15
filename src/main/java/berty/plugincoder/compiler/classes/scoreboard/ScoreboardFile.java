package berty.plugincoder.compiler.classes.scoreboard;

import berty.plugincoder.interpreter.plugin.Plugin;
import berty.plugincoder.main.PluginCoder;
import berty.plugincoder.writer.PluginFileWriter;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class ScoreboardFile {
    public static void createFile(Plugin plugin){
        String filePath=JavaPlugin.getPlugin(PluginCoder.class).getDataFolder().getParentFile().getPath() +
                "/PluginCoder/plugins/"+plugin.getName()+"/"+plugin.getName()+ "/objects/Scoreboard.java";
        File scoreboardFile= new File(filePath);
        if(scoreboardFile.exists())return;
        String content="package objects;\n\nimport org.bukkit.Bukkit;\n" +
                "import org.bukkit.scoreboard.DisplaySlot;\n" +
                "import org.bukkit.scoreboard.Objective;\n" +
                "import java.util.WeakHashMap;\n" +
                "\n" +
                "\n" +
                "public class Scoreboard{\n" +
                "\n" +
                "    private static WeakHashMap<org.bukkit.scoreboard.Scoreboard,Scoreboard> scoreboardInstances=new WeakHashMap<>();\n" +
                "\n" +
                "    private String title;\n" +
                "\n" +
                "    private org.bukkit.scoreboard.Scoreboard bukkitScoreboard;\n" +
                "\n" +
                "    public Scoreboard(String title) {\n" +
                "        this.title=title;\n" +
                "        this.bukkitScoreboard = Bukkit.getScoreboardManager().getNewScoreboard();\n" +
                "        Objective objective = bukkitScoreboard.registerNewObjective(\"dummy\", \"dummy\");\n" +
                "        objective.setDisplayName(title);\n" +
                "        objective.setDisplaySlot(DisplaySlot.SIDEBAR);\n" +
                "        scoreboardInstances.put(bukkitScoreboard,this);\n" +
                "    }\n" +
                "    public void addLine(int score,String text) {\n" +
                "        bukkitScoreboard.getObjective(\"dummy\").getScore(text).setScore(score);\n" +
                "    }\n" +
                "    public void clear() {\n" +
                "        bukkitScoreboard.getEntries().stream().forEach(entry-> bukkitScoreboard.resetScores(entry));\n" +
                "    }\n" +
                "    public void hide(){\n" +
                "        bukkitScoreboard.getObjective(\"dummy\").setDisplaySlot(null);\n" +
                "    }\n" +
                "    public void show(){\n" +
                "        bukkitScoreboard.getObjective(\"dummy\").setDisplaySlot(DisplaySlot.SIDEBAR);\n" +
                "    }\n" +
                "    public String getTitle(){\n" +
                "        return title;\n" +
                "    }\n" +
                "    public void setTitle(String title){\n" +
                "        this.title=title;\n" +
                "        bukkitScoreboard.getObjective(\"dummy\").setDisplayName(title);\n" +
                "    }\n" +
                "    @Override\n" +
                "    public String toString(){\n" +
                "        String entries=\"\";\n" +
                "        for(String entry: bukkitScoreboard.getEntries()){\n" +
                "            entries+=entry+\",\";\n" +
                "        }\n" +
                "        if(bukkitScoreboard.getEntries().size()>0)entries=entries.substring(0,entries.length()-1);\n" +
                "        return \"Scoreboard[\"+entries+\"]\";\n" +
                "    }\n" +
                "\n" +
                "    public org.bukkit.scoreboard.Scoreboard getBukkitScoreboard() {\n" +
                "        return bukkitScoreboard;\n" +
                "    }\n" +
                "    public static Scoreboard getScoreboard(org.bukkit.scoreboard.Scoreboard bukkitScoreboard){\n" +
                "        return scoreboardInstances.get(bukkitScoreboard);\n" +
                "    }\n" +
                "    public void clone(Scoreboard scoreboard) {\n" +
                "        scoreboard.setTitle(title);\n" +
                "        for(String entry: bukkitScoreboard.getEntries()){\n" +
                "            int score=bukkitScoreboard.getObjective(\"dummy\").getScore(entry).getScore();\n" +
                "            scoreboard.addLine(score,entry);\n" +
                "        }\n" +
                "    }"+
                "}";
        PluginFileWriter.writeInFile(scoreboardFile,content);
    }
}
