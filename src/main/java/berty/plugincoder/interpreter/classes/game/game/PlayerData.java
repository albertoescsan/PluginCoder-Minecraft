package berty.plugincoder.interpreter.classes.game.game;

import org.bukkit.GameMode;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.Scoreboard;

class PlayerData {

    private ItemStack[] contents;
    private ItemStack[] armor;
    private GameMode gamemode;
    private double health;
    private double maxHealth;
    private int food;
    private float exp;
    private int level;
    private Scoreboard scoreboard;

    protected PlayerData(ItemStack[] contents, ItemStack[] armor,GameMode gamemode, double health, double maxHealth,int food, float xp, int level,Scoreboard scoreboard) {
        this.contents = contents;
        this.armor = armor;
        this.gamemode = gamemode;
        this.health = health;
        this.maxHealth = maxHealth;
        this.food = food;
        this.exp = xp;
        this.level = level;
        this.scoreboard=scoreboard;
    }

    public ItemStack[] getContents() {
        return contents;
    }

    public ItemStack[] getArmor() {
        return armor;
    }

    public GameMode getGamemode() {
        return gamemode;
    }
    public double getHealth() {
        return health;
    }

    public double getMaxHealth() {
        return maxHealth;
    }
    public int getFood() {
        return food;
    }
    public float getExp() {
        return exp;
    }

    public int getLevel() {
        return level;
    }

    public Scoreboard getScoreboard() {
        return scoreboard;
    }
}
