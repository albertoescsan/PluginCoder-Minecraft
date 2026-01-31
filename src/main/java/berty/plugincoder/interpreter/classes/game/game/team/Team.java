package berty.plugincoder.interpreter.classes.game.game.team;

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
        if(color.length()<2||!color.startsWith("§"))color="§f";
        this.color=chatColorToBukkitColor(color);
        team.setPrefix(color);
        equipment=new TeamEquipment(this.color);
        if(JavaPlugin.getPlugin(PluginCoder.class).getVersionNumber()>=13){
            try{
                team.setColor(ChatColor.getByChar(color.charAt(1)));
            }catch (Exception e){team.setColor(ChatColor.WHITE);}
        }
    }
    private static Color chatColorToBukkitColor(String color) {
        if (color == null || color.isEmpty()) return Color.WHITE;
        color = color.toLowerCase();
        if (color.equals("§0")) return Color.BLACK;
        if (color.equals("§1")) return Color.fromRGB(0, 0, 170);         // DARK_BLUE
        if (color.equals("§2")) return Color.fromRGB(0, 170, 0);         // DARK_GREEN
        if (color.equals("§3")) return Color.fromRGB(0, 170, 170);       // DARK_AQUA
        if (color.equals("§4")) return Color.fromRGB(170, 0, 0);         // DARK_RED
        if (color.equals("§5")) return Color.fromRGB(170, 0, 170);       // DARK_PURPLE
        if (color.equals("§6")) return Color.fromRGB(255, 170, 0);       // GOLD
        if (color.equals("§7")) return Color.fromRGB(170, 170, 170);     // GRAY
        if (color.equals("§8")) return Color.fromRGB(85, 85, 85);        // DARK_GRAY
        if (color.equals("§9")) return Color.fromRGB(85, 85, 255);       // BLUE
        if (color.equals("§a")) return Color.fromRGB(85, 255, 85);       // GREEN
        if (color.equals("§b")) return Color.fromRGB(85, 255, 255);      // AQUA
        if (color.equals("§c")) return Color.fromRGB(255, 85, 85);       // RED
        if (color.equals("§d")) return Color.fromRGB(255, 85, 255);      // LIGHT_PURPLE
        if (color.equals("§e")) return Color.fromRGB(255, 255, 85);      // YELLOW
        if (color.equals("§f")) return Color.WHITE;
        if(!color.matches("^§x(§[a-fA-F0-9]){6}$"))return Color.WHITE;
        return Color.fromRGB(Integer.parseInt(color.substring(2).replace("§",""),16));

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
