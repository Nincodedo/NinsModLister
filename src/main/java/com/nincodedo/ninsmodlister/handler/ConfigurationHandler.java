package com.nincodedo.ninsmodlister.handler;

import java.io.File;

import net.minecraftforge.common.config.Configuration;

import com.nincodedo.ninsmodlister.NinsModLister;
import com.nincodedo.ninsmodlister.reference.Reference;

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
		NinsModLister.blackList = configuration.getStringList("blackList",
				"general", new String[] { "Forge Mod Loader",
						"Minecraft Coder Pack", "Minecraft Forge" },
				"List of strings that won't show up in the mod listing");
		NinsModLister.categoryGroups = configuration
				.getStringList(
						"categoryGroups",
						"general",
						new String[] {},
						"Use this to create custom categories for modIDs./n"
								+ "For example, if you would like Nin's Mod Lister to show up in a Things category, your config would look like this"
								+ "Things:NinsModLister");

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
