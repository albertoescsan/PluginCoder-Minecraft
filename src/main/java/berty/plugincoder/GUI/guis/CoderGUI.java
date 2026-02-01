package berty.plugincoder.GUI.guis;

import berty.plugincoder.GUI.InicVars;
import berty.plugincoder.GUI.guis.constructor.ConstructorsGUI;
import berty.plugincoder.GUI.guis.commands.CommandPromptGUI;
import berty.plugincoder.GUI.guis.commands.CommandsGUI;
import berty.plugincoder.GUI.guis.conditions.CheckObjectTypeGUI;
import berty.plugincoder.GUI.guis.conditions.ConditionsGUI;
import berty.plugincoder.GUI.guis.conditions.EqualityGUI;
import berty.plugincoder.GUI.guis.constructor.DictConstructorGUI;
import berty.plugincoder.GUI.guis.constructor.ListConstructorGUI;
import berty.plugincoder.GUI.guis.constructor.ObjectConstructorsGUI;
import berty.plugincoder.GUI.guis.event.EventsGUI;
import berty.plugincoder.GUI.guis.function.*;
import berty.plugincoder.GUI.guis.math.MathGUI;
import berty.plugincoder.GUI.guis.math.NumberGUI;
import berty.plugincoder.GUI.guis.objects.ObjectGUI;
import berty.plugincoder.GUI.guis.objects.ObjectsGUI;
import berty.plugincoder.GUI.guis.parameters.ForParametersGUI;
import berty.plugincoder.GUI.guis.parameters.FunctionParametersGUI;
import berty.plugincoder.GUI.guis.parameters.ParametersGUI;
import berty.plugincoder.GUI.guis.plugins.PluginsGUI;
import berty.plugincoder.GUI.guis.text.TextColorGUI;
import berty.plugincoder.GUI.guis.text.TextGUI;
import berty.plugincoder.GUI.guis.text.TextHexColorGUI;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import berty.plugincoder.main.PluginCoder;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.profile.PlayerTextures;

import java.lang.reflect.Field;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Function;

public class CoderGUI {

	private PluginCoder mainPlugin;
	private Player guiPlayer;
	private PluginsGUI pluginsGUI;
	private Inventory pluginCoderGUI;
	private Inventory languages;
	private Map<String,String> guiTranslations=new HashMap<>();
	private ItemStack homeItem;
	private ItemStack returnItem;
	private ItemStack backItem;
	private ItemStack nextItem;

	private EventsGUI eventsGUI;
	private ObjectsGUI objectsGUI;
	private ObjectGUI objectGUI;

	private FunctionGUI funcionGUI;

	private InstructionsGUI instructionsGUI;

	private VariableGUI variableGUI;


	private ExecutionWriterGUI executionWriterGUI;

	private ReturnGUI returnGUI;

	private SetValueGUI setValueGUI;
	private MathGUI mathGUI;
	private NumberGUI numberGUI;
	private ConditionsGUI conditionsGUI;
	private CheckObjectTypeGUI checkObjectTypeGUI;
	private EqualityGUI equalityGUI;
	private CommandsGUI commandsGUI;
	private CommandPromptGUI commandPromptGUI;
	private TextGUI textGUI;
	private TextColorGUI textColorGUI;
	private TextHexColorGUI textHexColorGUI;
	private ParametersGUI parametersGUI;
	private ForParametersGUI forParametersGUI;
	private FunctionParametersGUI functionParametersGUI;
	private ConstructorsGUI constructorsGUI;
	private ObjectConstructorsGUI objectConstructorsGUI;
	private ListConstructorGUI listConstructorGUI;
	private DictConstructorGUI dictConstructorGUI;
	public Inventory getPluginCoderGUI() {
		return pluginCoderGUI;
	}public Inventory getLanguagesGUI() {
		return languages;
	}

	public InstructionsGUI getInstructionsGUI() {
		return instructionsGUI;
	}

	public CoderGUI(PluginCoder mainPlugin) {
		this.mainPlugin =mainPlugin;
		pluginsGUI=new PluginsGUI(mainPlugin);
		eventsGUI=new EventsGUI(mainPlugin);
		objectsGUI=new ObjectsGUI(mainPlugin);
		objectGUI=new ObjectGUI(mainPlugin);
		funcionGUI=new FunctionGUI(mainPlugin);
		instructionsGUI=new InstructionsGUI(mainPlugin);
		variableGUI=new VariableGUI(mainPlugin);
		executionWriterGUI=new ExecutionWriterGUI(mainPlugin);
		returnGUI=new ReturnGUI(mainPlugin);
		setValueGUI=new SetValueGUI(mainPlugin);
		mathGUI=new MathGUI(mainPlugin);
		numberGUI=new NumberGUI(mainPlugin);
		conditionsGUI=new ConditionsGUI(mainPlugin);
		checkObjectTypeGUI=new CheckObjectTypeGUI(mainPlugin);
		equalityGUI=new EqualityGUI(mainPlugin);
		commandsGUI=new CommandsGUI(mainPlugin);
		commandPromptGUI=new CommandPromptGUI(mainPlugin);
		textColorGUI=new TextColorGUI(mainPlugin);
		textGUI=new TextGUI(mainPlugin);
		parametersGUI=new ParametersGUI(mainPlugin);
		forParametersGUI=new ForParametersGUI(mainPlugin);
		functionParametersGUI= new FunctionParametersGUI(mainPlugin);
		constructorsGUI=new ConstructorsGUI(mainPlugin);
		objectConstructorsGUI=new ObjectConstructorsGUI(mainPlugin);
		listConstructorGUI=new ListConstructorGUI(mainPlugin);
		dictConstructorGUI=new DictConstructorGUI(mainPlugin);
		if(mainPlugin.getVersionNumber()>=16)textHexColorGUI=new TextHexColorGUI(mainPlugin);
		Bukkit.getScheduler().scheduleSyncDelayedTask(mainPlugin, () -> {
			updateHomeItem();updateReturnItem();updateBackItem();updateNextItem();
		}, 1);
		//si da problemas cambiar el retardo
		Bukkit.getScheduler().scheduleSyncDelayedTask(mainPlugin, () -> createMainInventory(), 2);
		Bukkit.getScheduler().scheduleSyncDelayedTask(mainPlugin, () -> createLanguageInventory(), 2);

	}
	public void createMainInventory() {
		pluginCoderGUI=Bukkit.createInventory(null, 45,"§f§lPLUGINCODER");
		fillWithWhiteBorder(pluginCoderGUI);
		ItemStack plugins=new ItemStack(Material.BOOKSHELF);
		ItemMeta meta=plugins.getItemMeta();
		meta.setDisplayName(ChatColor.GOLD+"Plugins");
		plugins.setItemMeta(meta);
		updatePluginItem(mainPlugin.getSelectedPlugin().getName());
		String commandTitle=getGuiText("commandsTitle");
		ItemStack commands=new ItemStack(mainPlugin.getCodeUtils().getVersionedMaterial("COMMAND_BLOCK"));
		meta=commands.getItemMeta();
		meta.setDisplayName(ChatColor.RED+commandTitle);
		commands.setItemMeta(meta);
		ItemStack objects=new ItemStack(Material.CHEST);
		meta=objects.getItemMeta();
		String objectsTitle=getGuiText("objectsTitle");
		meta.setDisplayName(ChatColor.GOLD+objectsTitle);
		objects.setItemMeta(meta);
		ItemStack listener=new ItemStack(mainPlugin.getCodeUtils().getVersionedMaterial("ENDER_EYE"));
		meta=listener.getItemMeta();
		String eventsTitle=getGuiText("eventsTitle");
		meta.setDisplayName(ChatColor.AQUA+eventsTitle);
		listener.setItemMeta(meta);
		String languageTitle=getGuiText("languageTitle");
		ItemStack languageItem=getPlayerHead("http://textures.minecraft.net/texture/597e4e27a04afa5f06108265a9bfb797630391c7f3d880d244f610bb1ff393d8");
		meta=languageItem.getItemMeta();
		meta.setDisplayName(ChatColor.translateAlternateColorCodes('&',"&3"+languageTitle));
		languageItem.setItemMeta(meta);
		String onEnableTitle=getGuiText("onEnableTitle");
		ItemStack onEnable=new ItemStack(mainPlugin.getCodeUtils().getVersionedMaterial("WRITABLE_BOOK"));
		meta=onEnable.getItemMeta();
		meta.setDisplayName(ChatColor.GREEN+onEnableTitle);
		onEnable.setItemMeta(meta);
		String onDisableTitle=getGuiText("onDisableTitle");
		ItemStack onDisable=new ItemStack(mainPlugin.getCodeUtils().getVersionedMaterial("WRITABLE_BOOK"));
		meta=onDisable.getItemMeta();
		meta.setDisplayName(ChatColor.DARK_RED+onDisableTitle);
		onDisable.setItemMeta(meta);
		ItemStack main=new ItemStack(Material.NETHER_STAR);
		meta=main.getItemMeta();
		//poner nombre de la clase main
		String mainObjectTitle =getGuiText("mainObjectTitle");
		meta.setDisplayName(ChatColor.WHITE+mainObjectTitle);
		main.setItemMeta(meta);
		pluginCoderGUI.setItem(0,plugins);
		pluginCoderGUI.setItem(20, commands);
		pluginCoderGUI.setItem(22, objects);
		pluginCoderGUI.setItem(24, listener);
		pluginCoderGUI.setItem(8, languageItem);
		pluginCoderGUI.setItem(8, languageItem);
		pluginCoderGUI.setItem(38, onEnable);
		pluginCoderGUI.setItem(40, main);
		pluginCoderGUI.setItem(42, onDisable);
	}
	public void createLanguageInventory(){
		String languageTitle=getGuiText("languageTitle").toUpperCase();
		languages=Bukkit.createInventory(null,27,"§f§l"+languageTitle);
		ItemStack spanish=getPlayerHead("http://textures.minecraft.net/texture/c2d730b6dda16b584783b63d082a80049b5fa70228aba4ae884c2c1fc0c3a8bc");
		ItemMeta meta=spanish.getItemMeta();
		meta.setDisplayName(ChatColor.DARK_RED+"Español");
		spanish.setItemMeta(meta);
		ItemStack english=getPlayerHead("http://textures.minecraft.net/texture/8831c73f5468e888c3019e2847e442dfaa88898d50ccf01fd2f914af544d5368");
		meta=english.getItemMeta();
		meta.setDisplayName(ChatColor.DARK_BLUE+"English");
		english.setItemMeta(meta);
		ItemStack french=getPlayerHead("http://textures.minecraft.net/texture/6903349fa45bdd87126d9cd3c6c0abba7dbd6f56fb8d78701873a1e7c8ee33cf");
		meta=french.getItemMeta();
		meta.setDisplayName(ChatColor.BLUE+"Français");
		french.setItemMeta(meta);
		ItemStack portuguese=getPlayerHead("http://textures.minecraft.net/texture/ebd51f4693af174e6fe1979233d23a40bb987398e3891665fafd2ba567b5a53a");
		meta=portuguese.getItemMeta();
		meta.setDisplayName(ChatColor.DARK_GREEN+"Português");
		portuguese.setItemMeta(meta);
		ItemStack german=getPlayerHead("http://textures.minecraft.net/texture/5e7899b4806858697e283f084d9173fe487886453774626b24bd8cfecc77b3f");
		meta=german.getItemMeta();
		meta.setDisplayName(ChatColor.YELLOW+"Deutsch");
		german.setItemMeta(meta);
		ItemStack italian=getPlayerHead("http://textures.minecraft.net/texture/85ce89223fa42fe06ad65d8d44ca412ae899c831309d68924dfe0d142fdbeea4");
		meta=italian.getItemMeta();
		meta.setDisplayName(ChatColor.GREEN+"Italiano");
		italian.setItemMeta(meta);
		ItemStack russian=getPlayerHead("http://textures.minecraft.net/texture/16eafef980d6117dabe8982ac4b4509887e2c4621f6a8fe5c9b735a83d775ad");
		meta=russian.getItemMeta();
		meta.setDisplayName(ChatColor.DARK_BLUE+"Русский");
		russian.setItemMeta(meta);
		fillWithWhiteBorder(languages);
		languages.setItem(10,english);
		languages.setItem(11,spanish);
		languages.setItem(12,portuguese);
		languages.setItem(13,french);
		languages.setItem(14,german);
		languages.setItem(15,italian);
		languages.setItem(16,russian);
	}
	public void updateInventories() {
		updateHomeItem();
		updateReturnItem();
		updateBackItem();
		updateNextItem();
		createMainInventory();
		createLanguageInventory();
		eventsGUI.updateGUI();
		eventsGUI.updateEventItemsGUI();
		objectsGUI.updateGUI();
		objectGUI.updateInventoryLanguage();
		commandsGUI.updateGUI();
		commandPromptGUI.updateInventoryLanguage();
		pluginsGUI.createPluginEditor();
		variableGUI.createInventory();
		instructionsGUI.updateGUI();
		executionWriterGUI.createInventory();
		setValueGUI.updateInventoryLanguage();
		mathGUI.updateInventoryLanguage();
		numberGUI.updateInventoryLanguage();
		conditionsGUI.updateInventoryLanguage();
		checkObjectTypeGUI.updateInventoryLanguage();
		equalityGUI.updateInventoryLanguage();
		textGUI.updateInventoryLanguage();
		textColorGUI.updateInventoryLanguage();
		textHexColorGUI.updateInventoryLanguage();
		parametersGUI.updateInventoryLanguage();
		forParametersGUI.updateInventoryLanguage();
		constructorsGUI.updateInventoryLanguage();
		objectConstructorsGUI.updateGUI();
		listConstructorGUI.updateInventoryLanguage();
		dictConstructorGUI.updateInventoryLanguage();
	}
	private void fillWithWhiteBorder(Inventory inventory){
		ItemStack blanco=new ItemStack(mainPlugin.getVersionNumber()<13?
				Material.getMaterial("STAINED_GLASS_PANE"):Material.WHITE_STAINED_GLASS_PANE);
		ItemMeta meta=blanco.getItemMeta();
		meta.setDisplayName(ChatColor.WHITE+"");
		blanco.setItemMeta(meta);
		for(int i=0;i<9;i++) {
			inventory.setItem(i, blanco);
			inventory.setItem(inventory.getSize()-9+i, blanco);
		}
		for(int i=1;i<inventory.getSize()/9-1;i++) {
			inventory.setItem(9*i, blanco);
			inventory.setItem(9*i+8, blanco);
		}
	}
	public ItemStack getPlayerHead(String url) {
		ItemStack head = mainPlugin.getVersionNumber()<13?new ItemStack(Material.getMaterial("SKULL_ITEM"),1,(short)3):
				new ItemStack(Material.PLAYER_HEAD);
		if(mainPlugin.getVersionNumber()<17){//codigo para versiones inferiores a 1.17
			//TODO funciona para versiones inferiores a 1.13, pero hay que comprobar que funcione para 1.16
			ItemMeta headMeta=head.getItemMeta();
			if (url == null || url.isEmpty())return head;
			GameProfile profile = new GameProfile(UUID.randomUUID(), "PlayerHead");
			byte[] encodedData = Base64.getEncoder().encode(String.format("{textures:{SKIN:{url:\"%s\"}}}", url).getBytes());
			profile.getProperties().put("textures", new Property("textures", new String(encodedData)));
			try {
				Field profileField = headMeta.getClass().getDeclaredField("profile");
				profileField.setAccessible(true);
				profileField.set(headMeta, profile);
			} catch (Exception e) {e.printStackTrace();}
			head.setItemMeta(headMeta);
			return head;
		}
		//codigo para versiones superiores a 1.17
		SkullMeta skullMeta = (SkullMeta) head.getItemMeta();
		PlayerProfile profile =  Bukkit.createPlayerProfile(UUID.randomUUID(), "PlayerHead");
		PlayerTextures textures = profile.getTextures();
		try{
			textures.setSkin(new URL(url));
			profile.setTextures(textures);
		}catch (Exception e){e.printStackTrace();}
		skullMeta.setOwnerProfile(profile);
		head.setItemMeta(skullMeta);
		return head;
	}
	public String getGuiText(String textId){
		return guiTranslations.get(textId);
	}
	public void updateHomeItem() {
		homeItem=getPlayerHead("http://textures.minecraft.net/texture/c5a35b5ca15268685c4660535e5883d21a5ec57c55d397234269acb5dc2954f");
		ItemMeta meta=homeItem.getItemMeta();
		String homeTitle=getGuiText("homeTitle");
		meta.setDisplayName(ChatColor.DARK_RED+homeTitle);
		homeItem.setItemMeta(meta);
	}
	public void updateReturnItem() {
		returnItem=getPlayerHead("http://textures.minecraft.net/texture/468f5037a5e54b71cc0fe814c5896a45adc2556e3c9abe3f7847dc5afec014");
		ItemMeta meta=returnItem.getItemMeta();
		String returnTitle=getGuiText("returnTitle");
		meta.setDisplayName(ChatColor.WHITE+returnTitle);
		returnItem.setItemMeta(meta);
	}
	public void updateBackItem() {
		backItem=getPlayerHead("http://textures.minecraft.net/texture/cdc9e4dcfa4221a1fadc1b5b2b11d8beeb57879af1c42362142bae1edd5");
		ItemMeta meta=backItem.getItemMeta();
		String previousPageTitle=getGuiText("previousPageTitle");
		meta.setDisplayName(ChatColor.WHITE+previousPageTitle);
		backItem.setItemMeta(meta);
	}
	public void updateNextItem() {
		nextItem=getPlayerHead("http://textures.minecraft.net/texture/956a3618459e43b287b22b7e235ec699594546c6fcd6dc84bfca4cf30ab9311");
		ItemMeta meta=nextItem.getItemMeta();
		String nextPageTitle=getGuiText("nextPageTitle");
		meta.setDisplayName(ChatColor.WHITE+nextPageTitle);
		nextItem.setItemMeta(meta);
	}
	public void buttonSound(Player p){
		p.playSound(p.getLocation(), mainPlugin.getVersionNumber()<9?Sound.valueOf("CLICK"):Sound.BLOCK_DISPENSER_DISPENSE, 4, (float) 0);
	}
	public void errorSound(Player p) {
		p.playSound(p.getLocation(), mainPlugin.getVersionNumber()<9?Sound.valueOf("ANVIL_LAND"):Sound.BLOCK_ANVIL_LAND, 4, (float) 0);
	}
	public String putTextColor(String text){
		for(String color: mainPlugin.getColorTranslator().keySet()){
			String colorTranslation=color.equals("RESET")?"§f":mainPlugin.getColorTranslator().get(color);
			if(!colorTranslation.matches("^§[a-f0-9]$"))colorTranslation="§f"+colorTranslation;
			text=text.replaceAll("^"+color+"\\+",colorTranslation+color+"§f+");
			text=text.replaceAll("([+,(=])"+color+"\\+","$1"+ colorTranslation+color+"§f+");
		}
		//hex color
		while(text.matches("^(.*)#[A-Fa-f0-9]{6}(.*)$")){
			String hexCode=text.replaceAll("^(.*)(#[A-Fa-f0-9]{6})(.*)$","$2");
			String parsedHexCode=mainPlugin.getCodeExecuter().parseHexColor(hexCode);
			text=text.replaceAll(hexCode,parsedHexCode+"¤"+hexCode.substring(1)+"§f");
		}
		text=text.replace("¤","#");

		text=getColoredValueText(text,"true","§a");
		text=getColoredValueText(text,"false","§c");
		text=getColoredValueText(text,"null","§4");
		return text;
	}
	private String getColoredValueText(String text,String value,String color){
		text=text.replaceAll("="+value+"$","="+color+value+"§f");
		text=text.replaceAll("^"+value+"$",color+value+"§f");
		while (text.matches("^(.*)([,(])"+value+"([,)])(.*)$")){
			text=text.replaceAll("([,(])"+value+"([,)])","$1"+color+value+"§f$2");
		}
		while (text.matches("^(.*):"+value+"([,)}])(.*)$")){
			text=text.replaceAll("^(.*):"+value+"([,)}])","$1:"+color+value+"§f$2");
		}
		return text;
	}
	public void returnHome(Player p,boolean openInventory) {
		if(openInventory){
			p.openInventory(getPluginCoderGUI());
		}else guiPlayer=null;

		getFunctionGUI().setDeleteInstruction(false);
		getFunctionGUI().getFunctions().clear();
		getFunctionGUI().getGUI().clear();
		getFunctionGUI().getFunctionsIndexes().clear();
		getFunctionGUI().setOriginalFunctionIndex(0);
		InicVars.functionType="";
	}
	public void createInventoryBase(Inventory inventory,boolean paged){
		fillWithWhiteBorder(inventory);
		inventory.setItem(8,homeItem);
		inventory.setItem(0,returnItem);
		if(!paged)return;
		inventory.setItem(inventory.getSize()-9,backItem);
		inventory.setItem(inventory.getSize()-1,nextItem);
	}
	public void updateInventoriesContent(List<Inventory> invs){
		List<ItemStack> items=new ArrayList<>();
		boolean spaceFound=false;
		boolean invBreak=false;
		int itemIndex=10;
		int invIndex=0;
		for(Inventory inv:invs){
			for(int i=10;i<45;i++){
				if(i%9==0||(i+1)%9==0||(i>0&&i<8)||(i>45&&i<53))continue;
				ItemStack item=inv.getItem(i);
				if(item==null||item.getType()==Material.AIR){
					if(spaceFound){invBreak=true;break;}
					spaceFound=true;continue;
				}
				if(spaceFound){
					items.add(item.clone());
					inv.setItem(i,null);
					continue;
				}
				if((itemIndex+2)%9==0){
					if(itemIndex+2==invs.get(invIndex).getSize()-9){
						invIndex++;itemIndex=10;

					}else itemIndex+=3;
				}
				else itemIndex++;
			}
			if(invBreak)break;
		}
		for(ItemStack itemStack:items){//añadir items
			invs.get(invIndex).setItem(itemIndex,itemStack);
			if((itemIndex+2)%9==0){
				if(itemIndex+2==invs.get(invIndex).getSize()-9){
					invIndex++;itemIndex=10;

				}else itemIndex+=3;
			}
			else itemIndex++;
		}
		if(itemIndex==10&&invIndex!=0)invIndex--;
		for(int i=invIndex+1;i<invs.size();i++){//eliminar los inventarios sobrantes
			invs.remove(invIndex+1);
		}
	}

	public void createCircleArroundSlot(int slot,Inventory inventory,ItemStack item){
		List<Integer> slots=new ArrayList<>();
		slots.add(slot-10);slots.add(slot-9);slots.add(slot-8);slots.add(slot-1);
		slots.add(slot+10);slots.add(slot+9);slots.add(slot+8);slots.add(slot+1);
		for(int i:slots){
			if(!(i>=0&&i<inventory.getSize()))continue;
			inventory.setItem(i,item);
		}
	}
	public ItemStack getFunctionItem(String function,ItemStack baseItem,String functionName){
		ItemStack item=baseItem.clone();
		ItemMeta meta=item.getItemMeta();
		meta.setDisplayName(functionName);
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		item.setItemMeta(meta);
		item=addInstructionsToFunctionItem(function,item);
		return item;
	}
	public ItemStack addInstructionsToFunctionItem(String function,ItemStack item){
		ItemMeta meta=item.getItemMeta();
		meta.setLore(getDisplayLore(function));
		item.setItemMeta(meta);
		return item;
	}
	private List<String> getDisplayLore(String function){
		List<String> instructions=new ArrayList<>();
		for(String instruction: mainPlugin.getCodeExecuter().getGUIInstructionsFromFunction(function)){
			if(instruction.matches("^([^{]+)\\{(.*)}$")&&!instruction.matches("^([A-Za-z0-9_]+)\\s*=(.*)$")){
				String functionTitle=instruction.replaceAll("^([^{]+)\\{(.*)}$","$1{");
				functionTitle="§f"+putTextColor(functionTitle);
				instructions.add(functionTitle);
				for(String subInstruction:getDisplayLore(instruction)){
					instructions.add(subInstruction);
				}
				instructions.add(ChatColor.WHITE+"}");
			}else{
				instruction="§f"+putTextColor(instruction);
				instructions.add(ChatColor.WHITE+instruction);
			}
			if(instructions.size()>=15){
				instructions.add(ChatColor.WHITE+". . .");break;
			}
		}
		return instructions;
	}
	public ItemStack createItemWithDescription(ItemStack item,String description){
		ItemMeta meta=item.getItemMeta();
		List<String> lore=new ArrayList<>();
		String subtext="";int i=0;
		for(Character c:description.toCharArray()){
			if(i==30&&c.equals(' ')){
				lore.add(ChatColor.WHITE+subtext);i=0;subtext="";continue;
			}
			if(i<30)i++;
			subtext+=c;
		}
		lore.add(ChatColor.WHITE+subtext);
		meta.setLore(lore);
		item.setItemMeta(meta);
		return item;
	}
	public void createCenteredItemInventory(Inventory inventory){
		createInventoryBase(inventory,false);
		ItemStack negro= mainPlugin.getVersionNumber()<13?
				new ItemStack(Material.getMaterial("STAINED_GLASS_PANE"),1,(short)15):new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
		ItemMeta meta=negro.getItemMeta();
		meta.setDisplayName(ChatColor.WHITE+"");
		negro.setItemMeta(meta);
		createCircleArroundSlot(22,inventory,negro);
		ItemStack gris= mainPlugin.getVersionNumber()<13?
				new ItemStack(Material.getMaterial("STAINED_GLASS_PANE"),1,(short)8):new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
		meta=negro.getItemMeta();
		meta.setDisplayName(ChatColor.WHITE+"");
		gris.setItemMeta(meta);
		List<Integer> graySlots=new ArrayList<>();
		graySlots.add(10);graySlots.add(11);graySlots.add(15);graySlots.add(16);
		for(int i:graySlots){
			inventory.setItem(i,gris);inventory.setItem(i+9,gris);inventory.setItem(i+18,gris);
		}
	}
	public void createUpperLineInventory(Inventory inventory,boolean paged){
		createInventoryBase(inventory,paged);
		ItemStack blanco=new ItemStack(mainPlugin.getVersionNumber()<13?
				Material.getMaterial("STAINED_GLASS_PANE"):Material.WHITE_STAINED_GLASS_PANE);
		ItemMeta meta=blanco.getItemMeta();
		meta.setDisplayName(ChatColor.WHITE+"");
		blanco.setItemMeta(meta);
		for(int i=1;i<8;i++)inventory.setItem(18+i,blanco);
	}
	//para usar en math gui, condiciones y texto
	public void updateLinedElementsGUI(Inventory gui,List<String> lineContents,int startContentIndex,Function getContentItem){
		for(int i=0;i<7;i++)gui.setItem(10+i,null);
		for(int i=0;i<7;i++){
			if(lineContents.size()==i)break;
			gui.setItem(10+i, (ItemStack) getContentItem.apply(lineContents.get(i+startContentIndex)));
		}
		String operationText="";
		for(String s:lineContents){
			if(s.equals("and")||s.equals("or"))operationText+=" "+s+" ";
			else operationText+=s;
		}
		ItemStack instructionItem=gui.getItem(4);
		ItemMeta meta=instructionItem.getItemMeta();
		meta.setDisplayName("§f"+putTextColor(operationText));
		instructionItem.setItemMeta(meta);
		gui.setItem(4,instructionItem);
	}
	public void updatePluginItem(String name) {
		ItemStack pluginItem=new ItemStack(Material.BOOK);
		ItemMeta meta=pluginItem.getItemMeta();
		meta.setDisplayName(ChatColor.GOLD+name);
		List<String> lore=new ArrayList<>();
		lore.add(ChatColor.YELLOW+getGuiText("clickExport").replace("plugin.jar",ChatColor.GOLD+name+".jar"+ChatColor.YELLOW));
		meta.setLore(lore);
		pluginItem.setItemMeta(meta);
		pluginCoderGUI.setItem(4,pluginItem);
	}
	public Player getGuiPlayer() {
		return guiPlayer;
	}

	public void setGuiPlayer(Player guiPlayer) {this.guiPlayer = guiPlayer;}

	public ItemStack getHomeItem() {
		return homeItem;
	}

	public ItemStack getReturnItem() {
		return returnItem;
	}

	public ItemStack getBackItem() {
		return backItem;
	}

	public ItemStack getNextItem() {
		return nextItem;
	}

	public EventsGUI getEventsGUI() {
		return eventsGUI;
	}

	public ObjectsGUI getObjectsGUI() {
		return objectsGUI;
	}

	public FunctionGUI getFunctionGUI() {return funcionGUI;}

	public VariableGUI getVariableGUI() {
		return variableGUI;
	}


	public ExecutionWriterGUI getExecutionWriterGUI() {
		return executionWriterGUI;
	}

	public ReturnGUI getReturnGUI() {
		return returnGUI;
	}

	public Map<String, String> getGuiTranslations() {
		return guiTranslations;
	}

	public PluginsGUI getPluginsGUI() {
		return pluginsGUI;
	}

	public SetValueGUI getSetValueGUI() {
		return setValueGUI;
	}

	public MathGUI getMathGUI() {
		return mathGUI;
	}

	public NumberGUI getNumberGUI() {
		return numberGUI;
	}

	public ConditionsGUI getConditionsGUI() {
		return conditionsGUI;
	}

	public CheckObjectTypeGUI getCheckObjectTypeGUI() {
		return checkObjectTypeGUI;
	}

	public EqualityGUI getEqualityGUI() {
		return equalityGUI;
	}

	public CommandsGUI getCommandsGUI() {
		return commandsGUI;
	}

	public CommandPromptGUI getCommandPromptGUI() {
		return commandPromptGUI;
	}

	public TextGUI getTextGUI() {
		return textGUI;
	}

	public TextColorGUI getTextColorGUI() {
		return textColorGUI;
	}

	public TextHexColorGUI getTextHexColorGUI() {
		return textHexColorGUI;
	}
	public ParametersGUI getParametersGUI() {
		return parametersGUI;
	}

	public ForParametersGUI getForParametersGUI() {
		return forParametersGUI;
	}

	public ObjectGUI getObjectGUI() {
		return objectGUI;
	}

	public FunctionParametersGUI getFunctionParametersGUI() {
		return functionParametersGUI;
	}

	public ConstructorsGUI getConstructorsGUI() {
		return constructorsGUI;
	}
	public ObjectConstructorsGUI getObjectConstructorsGUI() {
		return objectConstructorsGUI;
	}

	public ListConstructorGUI getListConstructorGUI() {
		return listConstructorGUI;
	}

	public DictConstructorGUI getDictConstructorGUI() {
		return dictConstructorGUI;
	}
}
