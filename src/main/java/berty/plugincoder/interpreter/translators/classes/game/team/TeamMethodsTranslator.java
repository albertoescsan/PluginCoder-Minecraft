package berty.plugincoder.interpreter.translators.classes.game.team;

import berty.plugincoder.interpreter.classes.game.game.team.Team;
import berty.plugincoder.interpreter.classes.game.game.team.TeamEquipment;
import berty.plugincoder.interpreter.classes.game.game.team.Teams;
import berty.plugincoder.interpreter.objects.PluginMethod;

import java.util.ArrayList;
import java.util.Arrays;

public class TeamMethodsTranslator {

    public static void registerMethods(){
        PluginMethod get=PluginMethod.getMethod("get()");
        get.getTranslatedMethodClasses().put("getTeam",new ArrayList<>(Arrays.asList(Teams.class.getTypeName())));
        PluginMethod create=new PluginMethod("create()");
        create.getTranslatedMethodClasses().put("createTeam",new ArrayList<>(Arrays.asList(Teams.class.getTypeName())));
        PluginMethod list=new PluginMethod("list");
        list.getTranslatedMethodClasses().put("getList",new ArrayList<>(Arrays.asList(Teams.class.getTypeName())));

        PluginMethod equip=new PluginMethod("equip");
        equip.getTranslatedMethodClasses().put("equip",Arrays.asList(Team.class.getTypeName()));

        PluginMethod maxPlayers=PluginMethod.getMethod("maxplayers");
        maxPlayers.getTranslatedMethodClasses().get("getMaxPlayers").add(Team.class.getTypeName());

        PluginMethod equipP=new PluginMethod("equip()");
        equipP.getTranslatedMethodClasses().put("equip",Arrays.asList(TeamEquipment.class.getTypeName()));

        PluginMethod contents=PluginMethod.getMethod("items");
        contents.getTranslatedMethodClasses().get("getContents").add(TeamEquipment.class.getTypeName());

        PluginMethod helmet=PluginMethod.getMethod("helmet");
        helmet.getTranslatedMethodClasses().get("getHelmet").add(TeamEquipment.class.getTypeName());;
        PluginMethod helmetP=PluginMethod.getMethod("helmet()");
        helmetP.getTranslatedMethodClasses().get("setHelmet").add(TeamEquipment.class.getTypeName());;
        PluginMethod chestplate=PluginMethod.getMethod("chestplate");
        chestplate.getTranslatedMethodClasses().get("getChestplate").add(TeamEquipment.class.getTypeName());;
        PluginMethod chestplateP=PluginMethod.getMethod("chestplate()");
        chestplateP.getTranslatedMethodClasses().get("setChestplate").add(TeamEquipment.class.getTypeName());;
        PluginMethod leggins=PluginMethod.getMethod("leggins");
        leggins.getTranslatedMethodClasses().get("getLeggins").add(TeamEquipment.class.getTypeName());;
        PluginMethod legginsP=PluginMethod.getMethod("leggins()");
        legginsP.getTranslatedMethodClasses().get("setLeggins").add(TeamEquipment.class.getTypeName());;
        PluginMethod boots=PluginMethod.getMethod("boots");
        boots.getTranslatedMethodClasses().get("getBoots").add(TeamEquipment.class.getTypeName());;
        PluginMethod bootsP=PluginMethod.getMethod("boots()");
        bootsP.getTranslatedMethodClasses().get("setBoots").add(TeamEquipment.class.getTypeName());;
    }
}
