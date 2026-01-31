package berty.plugincoder.interpreter.translators;

import berty.plugincoder.interpreter.classes.scoreboard.Scoreboard;
import berty.plugincoder.interpreter.classes.game.game.Game;
import berty.plugincoder.interpreter.classes.game.game.GameTemplate;
import berty.plugincoder.interpreter.objects.PluginMethod;
import org.bukkit.Location;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import berty.plugincoder.interpreter.translators.classes.game.MinigameMethodsTranslator;
import berty.plugincoder.interpreter.translators.classes.scoreboard.ScoreboardMethodsTranslator;
import berty.plugincoder.interpreter.translators.container.ListMethodsTranslator;
import berty.plugincoder.interpreter.translators.container.MapMethodsTranslator;
import berty.plugincoder.interpreter.translators.event.EventMethodTranslators;
import berty.plugincoder.interpreter.translators.inventory.InventoryMethodsTranslator;
import berty.plugincoder.interpreter.translators.item.ItemMethodsTranslator;
import berty.plugincoder.interpreter.translators.location.LocationMethodsTranslator;
import berty.plugincoder.interpreter.translators.player.PlayerMethodsTranslator;
import berty.plugincoder.interpreter.translators.server.ServerMethodsTranslator;

import java.util.*;

public class Translators {

    public static void registerMethods(){
        PluginMethod text=new PluginMethod("text");
        text.getTranslatedMethodClasses().put("toString",Arrays.asList(""));
        ListMethodsTranslator.registerMethods();
        MapMethodsTranslator.registerMethods();
        EventMethodTranslators.registerMethods();
        PlayerMethodsTranslator.registerMethods();
        ServerMethodsTranslator.registerMethods();
        LocationMethodsTranslator.registerMethods();
        InventoryMethodsTranslator.registerMethods();
        ItemMethodsTranslator.registerMethods();
        MinigameMethodsTranslator.registerMethods();
        ScoreboardMethodsTranslator.registerMethods();
    }
    public static void registerConstructors(Map<String,Class> constructorTranslator){
        constructorTranslator.put("Inventory", Inventory.class);
        constructorTranslator.put("ItemStack", ItemStack.class);
        constructorTranslator.put("Location", Location.class);

        constructorTranslator.put("Scoreboard", Scoreboard.class);
        constructorTranslator.put("Game", Game.class);
        constructorTranslator.put("GameTemplate", GameTemplate.class);
    }
}
