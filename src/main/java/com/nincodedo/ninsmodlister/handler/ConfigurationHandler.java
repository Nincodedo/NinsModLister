package com.nincodedo.ninsmodlister.handler;

import java.io.File;

import net.minecraftforge.common.config.Configuration;

import com.nincodedo.ninsmodlister.NinsModLister;
import com.nincodedo.ninsmodlister.reference.Reference;
import com.nincodedo.ninsmodlister.reference.Settings;

import cpw.mods.fml.client.event.ConfigChangedEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class ConfigurationHandler {
	public static Configuration configuration;

	public static void init(File configFile) {
		if (configuration == null) {
			configuration = new Configuration(configFile);
			loadConfiguration();
		}
	}

	private static void loadConfiguration() {
		String category = "general";

		Settings.configBlackList = configuration.getStringList("blackList", category, new String[] { "" },
				"List of strings that won't show up in the mod listing");
		Settings.categoryGroups = configuration.getStringList("categoryGroups", category,
				new String[] { "Things:NinsModLister" },
				"Use this to create custom categories for modIDs or names."
						+ "For example, if you would like Nin's Mod Lister to show up in a Things category, your config would look like this "
						+ "Things:NinsModLister");
		Settings.categoryPriority = configuration.getStringList("categoryPriority", category,
				new String[] { "Current Mod Versions" }, "");
		Settings.generalCategoryTitle = configuration.getString("generalCategoryTitle", category,
				"Current Mod Versions", "Category name for any mods that don't fit a defined custom category");
		Settings.overrides = configuration.getStringList("overrides", category, new String[] {""},
			"Use this to override values in a mod's mcmod.info. Syntax is ModID:OverrideField:Value where ModID is the
			ID of the mod you are overriding, OverrideField is the name of the mcmod.info field you are trying to override,
			and Value is the value you want to display instead");
		Settings.fileName = configuration.getString("fileName", category, "Versions.md", "Name of your mod list file");
		Settings.showForgeVersion = configuration.getBoolean("showForgeVersion", category, true,
				"Displays the Forge version at the top of the version list");

		if (configuration.hasChanged()) {
			configuration.save();
		}
	}

	@SubscribeEvent
	public void onConfigurationChangedEvent(ConfigChangedEvent.OnConfigChangedEvent event) {
		if (event.modID.equalsIgnoreCase(Reference.MOD_ID)) {
			loadConfiguration();
		}
	}

}
