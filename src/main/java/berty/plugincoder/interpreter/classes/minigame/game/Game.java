package berty.plugincoder.interpreter.classes.minigame.game;

import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import berty.plugincoder.interpreter.classes.scoreboard.Scoreboard;
import berty.plugincoder.interpreter.classes.minigame.events.*;
import org.bukkit.entity.Player;
import berty.plugincoder.interpreter.classes.minigame.game.team.Team;
import berty.plugincoder.interpreter.classes.minigame.game.team.Teams;
import berty.plugincoder.main.PluginCoder;
import org.bukkit.FireworkEffect.Type;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

public class Game {

    private PluginCoder plugin=JavaPlugin.getPlugin(PluginCoder.class);
    private String name;
    private boolean activated=false;
    private List<Player> players=new ArrayList<>();
    private Map<String, PlayerData> playerData=new HashMap<>();
    private Teams teams;
    private Location spawn;
    private Scoreboard scoreboard;
    private int minPlayers;
    private int maxPlayers;
    private GameState gameState=GameState.WAITING;
    private Winner winner;

    public Game(String name){
        this.name=name;
        scoreboard=new Scoreboard(name);
        teams=new Teams(scoreboard.getBukkitScoreboard());
    }
    public void activate(){
        if(activated)return;
        activated=true;
        gameState=GameState.WAITING;
        int maxPlayers=teams.getList().stream().mapToInt(team->team.getMaxPlayers()).sum();
        if(maxPlayers>0)this.maxPlayers=maxPlayers;
        spawn=new Location(Bukkit.getWorld("world"),-25,100,-330);//TODO quitar
    }
    public void deactivate(){
        if(!activated)return;
        gameState=GameState.DEACTIVATED;
        activated=false;
        for(Player player:players)leave(player);
    }
    public boolean join(Player player){
        activate();
        if(players.size()==maxPlayers||spawn==null)return false;
        PlayerJoinGameEvent event=new PlayerJoinGameEvent(this,player);
        Bukkit.getServer().getPluginManager().callEvent(event);
        if(event.isCancelled())return false;
        players.add(player);player.teleport(spawn);
        playerData.put(player.getName(),new PlayerData(player.getInventory().getContents(),
                player.getInventory().getArmorContents(),player.getGameMode(),player.getHealth(),player.getMaxHealth(),
                player.getFoodLevel(),player.getExp(),player.getLevel(),player.getScoreboard()));
        player.setScoreboard(scoreboard.getBukkitScoreboard());
        if(!event.getMessage().isEmpty())for(Player gamePlayer:players)gamePlayer.sendMessage(event.getMessage());
        if(players.size()==minPlayers)starting();
        return true;
    }
    public boolean leave(Player player){
        if(!players.contains(player))return false;
        PlayerLeaveGameEvent event=new PlayerLeaveGameEvent(this,player);
        Bukkit.getServer().getPluginManager().callEvent(event);
        if(event.isCancelled())return false;
        players.remove(player);
        Team team=teams.getTeam(player);
        if(team!=null)team.removePlayer(player);
        PlayerData data=playerData.get(player.getName());
        player.getInventory().setContents(data.getContents());
        player.getInventory().setArmorContents(data.getArmor());
        player.setGameMode(data.getGamemode());
        player.setHealth(data.getHealth());
        player.setMaxHealth(data.getMaxHealth());
        player.setFoodLevel(data.getFood());
        player.setExp(data.getExp());
        player.setLevel(data.getLevel());
        player.setScoreboard(data.getScoreboard());
        playerData.remove(player.getName());
        if(!event.getMessage().isEmpty())for(Player gamePlayer:players)gamePlayer.sendMessage(event.getMessage());
        return true;
    }
    public void starting(){
        gameState=GameState.STARTING;
        GameStartingEvent event=new GameStartingEvent(this);
        Bukkit.getServer().getPluginManager().callEvent(event);
        AtomicInteger seconds= new AtomicInteger();
        seconds.set(event.getTime());
        new BukkitRunnable() {
            public void run() {
                if(event.getMessages().containsKey(seconds.get())){
                    for(Player gamePlayer:players)gamePlayer.sendMessage(event.getMessages().get(seconds.get()));
                }
                if(seconds.get()==0){start();cancel();}
                if(minPlayers>players.size()){gameState=GameState.WAITING;cancel();}
                seconds.decrementAndGet();
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }
    public void start(){
        gameState=GameState.PLAYING;
        if(!teams.getList().isEmpty()){
            Random random=new Random();
            int teamIndex = random.nextInt(teams.getList().size());
            List<Player> players=this.players.stream().sorted(Comparator.comparing(
                    player -> random.nextInt(this.players.size())
            )).collect(Collectors.toList());
            for (Player player : players) {
                Team team=null;
                while(team==null||team.getMaxPlayers()>=team.getPlayers().size()){
                    team = teams.getList().get(teamIndex);
                    teamIndex = (teamIndex + 1) % teams.getList().size();
                }
                team.addPlayer(player);
            }
            teams.getList().stream().forEach(team -> team.equip());
        }
        GameStartEvent event=new GameStartEvent(this);
        Bukkit.getServer().getPluginManager().callEvent(event);
        if(!event.getMessage().isEmpty())for(Player gamePlayer:players)gamePlayer.sendMessage(event.getMessage());
    }
    public void finish(){
        gameState=GameState.FINISHING;
        GameFinishEvent event=new GameFinishEvent(this,winner);
        Bukkit.getServer().getPluginManager().callEvent(event);
        if(!event.getMessage().isEmpty())for(Player gamePlayer:players)gamePlayer.sendMessage(event.getMessage());
        AtomicInteger seconds= new AtomicInteger();
        new BukkitRunnable() {
            public void run() {
                if(seconds.get()==0){
                    gameState=GameState.WAITING;winner=null;
                    new ArrayList<>(players).stream().forEach(player -> leave(player));
                    cancel();}
                seconds.decrementAndGet();
                if(!event.isFireworks())return;
                BiFunction<Color,Double,Color> colorVariant=(color,factor)->
                        Color.fromRGB(Math.max(0,Math.min(255,(int)(color.getRed()*factor))),
                                Math.max(0,Math.min(255,(int)(color.getGreen()*factor))),
                                Math.max(0,Math.min(255,(int)(color.getBlue()*factor))));
                Color color=winner.getTeam()!=null?winner.getTeam().getColor():Color.WHITE;
                Color backColor=colorVariant.apply(color,1.3);
                Color fadeColor=colorVariant.apply(color,0.7);
                winner.getPlayers().stream().forEach(player -> {
                    Firework firework= (Firework) player.getWorld().spawnEntity(player.getLocation(), EntityType.FIREWORK);
                    FireworkMeta fireworkMeta= firework.getFireworkMeta();
                    fireworkMeta.addEffect(FireworkEffect.builder().withColor(color,backColor).withFade(fadeColor).with(Type.BALL).build());
                    fireworkMeta.setPower(2);
                    firework.setFireworkMeta(fireworkMeta);
                });
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }
    public String getName() {
        return name;
    }

    public Location getSpawn() {
        return spawn;
    }

    public void setSpawn(Location spawn) {
        if(activated)return;
        this.spawn = spawn;
    }

    public Scoreboard getScoreboard() {
        return scoreboard;
    }

    public Teams getTeams() {
        return teams;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public int getMinPlayers() {
        return minPlayers;
    }

    public void setMinPlayers(int minPlayers) {
        if(activated)return;
        this.minPlayers = minPlayers;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public void setMaxPlayers(int maxPlayers) {
        if(activated)return;
        this.maxPlayers = maxPlayers;
    }

    public GameState getState() {
        return gameState;
    }
    public void setWinner(Player player){
        if(player==null)return;
        winner=new Winner(player);
        finish();
    }
    public void setWinner(Team team){
        if(team==null||team.getPlayers().isEmpty())return;
        winner=new Winner(team);
        finish();
    }
}
