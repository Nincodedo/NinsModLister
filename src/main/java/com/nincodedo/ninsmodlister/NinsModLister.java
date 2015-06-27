package com.nincodedo.ninsmodlister;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import scala.actors.threadpool.Arrays;
import net.minecraftforge.common.ForgeVersion;

import com.nincodedo.ninsmodlister.handler.ConfigurationHandler;
import com.nincodedo.ninsmodlister.reference.Reference;
import com.nincodedo.ninsmodlister.reference.Settings;
import com.nincodedo.ninsmodlister.utility.LogHelper;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = Reference.MOD_ID, name = Reference.MOD_NAME, version = Reference.VERSION, dependencies = Reference.DEPENDENCIES)
public final class NinsModLister {

	@Mod.Instance(Reference.MOD_ID)
	public static NinsModLister instance;

	File mcDir;

	List blackList;

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		mcDir = event.getModConfigurationDirectory().getParentFile();
		ConfigurationHandler.init(event.getSuggestedConfigurationFile());
		FMLCommonHandler.instance().bus().register(new ConfigurationHandler());
	}

	@Mod.EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		List<String> lines = new ArrayList();
		boolean found = false;
		HashMap<String, List<ModContainer>> customCategories = new HashMap<String, List<ModContainer>>();
		List<ModContainer> modIds = new ArrayList<ModContainer>();
		blackList = Arrays.asList(Settings.configBlackList);

		for (ModContainer mod : Loader.instance().getModList()) {
			found = false;
			for (String line : Settings.categoryGroups) {
				String category = line.split(":")[0];
				String modId = line.split(":")[1];
				if (mod.getModId().equals(modId) || mod.getName().equals(modId)) {
					if (!customCategories.containsKey(category)) {
						modIds = new ArrayList<ModContainer>();
						customCategories.put(category, modIds);
					}
					if (customCategories.containsKey(category)) {
						modIds = customCategories.get(category);
					}
					if (checkBlackList(mod)) {
						modIds.add(mod);
					}
					found = true;
				}
			}
			if (!found) {
				if (!customCategories.containsKey(Settings.generalCategoryTitle)) {
					modIds = new ArrayList<ModContainer>();
					customCategories.put(Settings.generalCategoryTitle, modIds);
				} else {
					modIds = customCategories.get(Settings.generalCategoryTitle);
				}
				if (!blackList.contains(mod.getName())
						&& !blackList.contains(mod.getModId()))
					modIds.add(mod);
			}
		}

		Comparator<ModContainer> compareMods = new Comparator<ModContainer>() {
			@Override
			public int compare(ModContainer mod1, ModContainer mod2) {
				return mod1.getName().toLowerCase()
						.compareTo(mod2.getName().toLowerCase());
			}
		};

		for (Entry<String, List<ModContainer>> entry : customCategories
				.entrySet()) {
			Collections.sort(entry.getValue(), compareMods);
		}

		lines.add("\n");

		for (Entry<String, List<ModContainer>> entry : customCategories
				.entrySet()) {
			lines.add("\n" + entry.getKey() + "\n=");
			for (ModContainer mod : entry.getValue()) {
				lines.add(createLine(mod));
			}
			lines.add("\n");
		}

		try {
			File file = new File(mcDir, "Versions.md");
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(file), "UTF-8"));

			writer.write("Current Forge Version\n=\n");

			writer.write("- **Forge** v" + ForgeVersion.getVersion());

			for (String s : lines)
				writer.write(s);

			writer.close();

			if (!file.exists())
				file.createNewFile();

			LogHelper.info("NinsModLister wrote mod data properly.");
		} catch (IOException e) {
			LogHelper.warn("NinsModLister failed to write mod data!");
			e.printStackTrace();
		}
	}

	private boolean checkBlackList(ModContainer mod) {
		return !blackList.contains(mod.getName())
				&& !blackList.contains(mod.getModId());
	}

	private String createLine(ModContainer container) {
		StringBuilder builder = new StringBuilder();
		builder.append("\n");
		builder.append("- **");
		builder.append(container.getName());
		builder.append("** v");
		builder.append(container.getVersion());

		if (container.getMetadata().getAuthorList() != null
				&& container.getMetadata().getAuthorList().length() > 0) {
			builder.append(" by ");
			builder.append(container.getMetadata().getAuthorList());
		}

		return builder.toString();
	}

}