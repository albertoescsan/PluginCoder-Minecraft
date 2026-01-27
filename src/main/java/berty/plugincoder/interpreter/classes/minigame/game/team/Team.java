package berty.plugincoder.interpreter.classes.minigame.game.team;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Scoreboard;
import berty.plugincoder.main.PluginCoder;

import java.util.List;
import java.util.stream.Collectors;

public class Team {
    private org.bukkit.scoreboard.Team team;
    private int maxPlayers;
    private TeamEquipment equipment;
    private Color color;
    protected Team(String name,int maxPlayers,String color,Scoreboard scoreboard){
        this.maxPlayers=maxPlayers;
        team=scoreboard.registerNewTeam(name);
        if(color.length()>2||!color.startsWith("§")){
            team.setPrefix(ChatColor.WHITE+"");this.color=Color.WHITE;
            equipment=new TeamEquipment(this.color);
            if(JavaPlugin.getPlugin(PluginCoder.class).getVersionNumber()>=13)team.setColor(ChatColor.WHITE);
            return;
        }
        ChatColor chatColor=ChatColor.getByChar(color.charAt(1));
        this.color=chatColorToBukkitColor(chatColor);
        team.setPrefix(color);
        equipment=new TeamEquipment(this.color);
        if(JavaPlugin.getPlugin(PluginCoder.class).getVersionNumber()>=13)team.setColor(chatColor);
    }
    private static Color chatColorToBukkitColor(ChatColor chatColor) {
        switch (chatColor) {
            case BLACK:        return Color.fromRGB(0, 0, 0);
            case DARK_BLUE:    return Color.fromRGB(0, 0, 170);
            case DARK_GREEN:   return Color.fromRGB(0, 170, 0);
            case DARK_AQUA:    return Color.fromRGB(0, 170, 170);
            case DARK_RED:     return Color.fromRGB(170, 0, 0);
            case DARK_PURPLE:  return Color.fromRGB(170, 0, 170);
            case GOLD:         return Color.fromRGB(255, 170, 0);
            case GRAY:         return Color.fromRGB(170, 170, 170);
            case DARK_GRAY:    return Color.fromRGB(85, 85, 85);
            case BLUE:         return Color.fromRGB(85, 85, 255);
            case GREEN:        return Color.fromRGB(85, 255, 85);
            case AQUA:         return Color.fromRGB(85, 255, 255);
            case RED:          return Color.fromRGB(255, 85, 85);
            case LIGHT_PURPLE: return Color.fromRGB(255, 85, 255);
            case YELLOW:       return Color.fromRGB(255, 255, 85);
            default:           return Color.WHITE;
        }
    }
    public boolean addPlayer(Player player){
        if(team.getPlayers().size()>=maxPlayers)return false;
        team.addPlayer(player);
        return true;
    }
    public boolean removePlayer(Player player){
        return team.removePlayer(player);
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public String getName(){return team.getName();}
    public String getPrefix(){return team.getPrefix().substring(2,team.getPrefix().length());}
    public void setPrefix(String prefix){
        String color=team.getPrefix().substring(0,2);
        team.setPrefix(color+prefix);
    }
    protected String getPrefixColor(){return team.getPrefix().substring(0,2);}
    public Color getColor(){return color;}
    public List<Player> getPlayers() {
        return team.getPlayers().stream().map(offlinePlayer -> offlinePlayer.getPlayer()).collect(Collectors.toList());
    }
    public void equip(){
        getPlayers().stream().forEach(player -> equipment.equip(player));
    }

    public TeamEquipment getEquipment() {
        return equipment;
    }

    protected void setEquipment(TeamEquipment equipment) {
        this.equipment = equipment;
    }
}
