package berty.plugincoder.interpreter.translators.player;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import berty.plugincoder.interpreter.objects.PluginMethod;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PlayerMethodsTranslator {

    public static void registerMethods(){
        List<String> playerClass= new ArrayList<>();
        playerClass.add(Player.class.getTypeName());
        PluginMethod name=new PluginMethod("name");
        name.getTranslatedMethodClasses().put("getName",new ArrayList<>(playerClass));
        PluginMethod nameP=new PluginMethod("name()");
        nameP.getTranslatedMethodClasses().put("setDisplayName",new ArrayList<>(playerClass));
        PluginMethod message=new PluginMethod("message()");
        message.getTranslatedMethodClasses().put("sendMessage",new ArrayList<>(playerClass));
        PluginMethod health=new PluginMethod("health");
        health.getTranslatedMethodClasses().put("getHealth",playerClass);
        PluginMethod healthP=new PluginMethod("health()");
        healthP.getTranslatedMethodClasses().put("setHealth",playerClass);
        PluginMethod maxhealth=new PluginMethod("maxhealth");
        maxhealth.getTranslatedMethodClasses().put("getMaxHealth",playerClass);
        PluginMethod maxhealthP=new PluginMethod("maxhealth()");
        maxhealthP.getTranslatedMethodClasses().put("setMaxHealth",playerClass);
        PluginMethod xp=new PluginMethod("xp");
        xp.getTranslatedMethodClasses().put("getExp",playerClass);
        PluginMethod xpP=new PluginMethod("xp()");
        xpP.getTranslatedMethodClasses().put("setExp",playerClass);
        PluginMethod level=new PluginMethod("level");
        level.getTranslatedMethodClasses().put("getLevel",playerClass);
        PluginMethod levelP=new PluginMethod("level()");
        levelP.getTranslatedMethodClasses().put("setLevel",playerClass);
        PluginMethod food=new PluginMethod("food");
        food.getTranslatedMethodClasses().put("getFoodLevel",playerClass);
        PluginMethod foodP=new PluginMethod("food()");
        foodP.getTranslatedMethodClasses().put("setFoodLevel",playerClass);
        PluginMethod inventory=new PluginMethod("inventory");
        inventory.getTranslatedMethodClasses().put("getInventory",playerClass);
        PluginMethod uuid=new PluginMethod("uuid");
        uuid.getTranslatedMethodClasses().put("getUniqueId",playerClass);
        PluginMethod gamemode=new PluginMethod("gamemode");
        gamemode.getTranslatedMethodClasses().put("getGameMode",playerClass);
        PluginMethod gamemodeP=new PluginMethod("gamemode()");
        gamemodeP.getTranslatedMethodClasses().put("setGameMode",playerClass);
        PluginMethod location=new PluginMethod("location");
        location.getTranslatedMethodClasses().put("getLocation",Arrays.asList(Player.class.getTypeName(), Entity.class.getTypeName(), LivingEntity.class.getTypeName()));
        PluginMethod damage=new PluginMethod("damage()");
        damage.getTranslatedMethodClasses().put("damage",Arrays.asList(Player.class.getTypeName(), LivingEntity.class.getTypeName()));
        PluginMethod teleport=new PluginMethod("teleport()");
        teleport.getTranslatedMethodClasses().put("teleport",playerClass);
        PluginMethod world=new PluginMethod("world");
        world.getTranslatedMethodClasses().put("getWorld",Arrays.asList(Location.class.getTypeName(),Player.class.getTypeName(), Entity.class.getTypeName(),
                LivingEntity.class.getTypeName()));
        PluginMethod scoreboard=new PluginMethod("scoreboard");
        scoreboard.getTranslatedMethodClasses().put("getScoreboard",new ArrayList<>(playerClass));
        PluginMethod scoreboardP=new PluginMethod("scoreboard()");
        scoreboardP.getTranslatedMethodClasses().put("setScoreboard",new ArrayList<>(playerClass));
        PluginMethod online=new PluginMethod("online");
        online.getTranslatedMethodClasses().put("isOnline",playerClass);
        PluginMethod op=new PluginMethod("op");
        op.getTranslatedMethodClasses().put("isOp",playerClass);
        PluginMethod opP=new PluginMethod("op()");
        opP.getTranslatedMethodClasses().put("setOp",playerClass);
        PluginMethod flying=new PluginMethod("flying");
        flying.getTranslatedMethodClasses().put("isFlying",playerClass);
        PluginMethod open=new PluginMethod("open()");
        open.getTranslatedMethodClasses().put("openInventory",playerClass);
        open.getTranslatedMethodClasses().put("openSign",playerClass);
        open.getTranslatedMethodClasses().put("openBook",playerClass);
    }
}
