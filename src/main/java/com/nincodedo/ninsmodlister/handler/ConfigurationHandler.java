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
		Settings.configBlackList = configuration.getStringList("blackList",
				"general", new String[] { "Forge Mod Loader",
						"Minecraft Coder Pack", "Minecraft Forge" },
				"List of strings that won't show up in the mod listing");
		Settings.categoryGroups = configuration
				.getStringList(
						"categoryGroups",
						"general",
						new String[] {},
						"Use this to create custom categories for modIDs or names."
								+ "For example, if you would like Nin's Mod Lister to show up in a Things category, your config would look like this "
								+ "Things:NinsModLister");
		Settings.generalCategoryTitle = configuration
				.getString("generalCategoryTitle", "general",
						"Current Mod Versions", "Category name for any mods that don't fit a defined custom category");
		Settings.fileName = configuration.getString("fileName", "general",
				"Versions.md", "Name of your mod list file");

		if (configuration.hasChanged()) {
			configuration.save();
		}
	}

	@SubscribeEvent
	public void onConfigurationChangedEvent(
			ConfigChangedEvent.OnConfigChangedEvent event) {
		if (event.modID.equalsIgnoreCase(Reference.MOD_ID)) {
			loadConfiguration();
		}
	}

}
