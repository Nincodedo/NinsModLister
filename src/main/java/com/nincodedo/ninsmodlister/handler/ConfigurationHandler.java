package com.nincodedo.ninsmodlister.handler;

import java.io.File;

import net.minecraftforge.common.config.Configuration;

import com.nincodedo.ninsmodlister.NinsModLister;

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

		if (configuration.hasChanged()) {
			configuration.save();
		}
	}

	@SubscribeEvent
	public void onConfigurationChangedEvent(
			ConfigChangedEvent.OnConfigChangedEvent event) {
		if (event.modID.equalsIgnoreCase(NinsModLister.MOD_ID)) {
			loadConfiguration();
		}
	}

}
