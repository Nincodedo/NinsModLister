package com.nincodedo.ninsmodlister;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import net.minecraftforge.common.ForgeVersion;

import com.nincodedo.ninsmodlister.handler.ConfigurationHandler;
import com.nincodedo.ninsmodlister.utility.LogHelper;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = NinsModLister.MOD_ID, name = NinsModLister.MOD_NAME, version = NinsModLister.VERSION, dependencies = NinsModLister.DEPENDENCIES)
public final class NinsModLister {

	public static final String MOD_ID = "NinsModLister";
	public static final String MOD_NAME = "Nin's Mod Lister";
	public static final String VERSION = "1.0";
	public static final String DEPENDENCIES = "after:*";

	@Mod.Instance(MOD_ID)
	public static NinsModLister instance;

	File mcDir;

	public static String[] blackList;

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		mcDir = event.getModConfigurationDirectory().getParentFile();
		ConfigurationHandler.init(event.getSuggestedConfigurationFile());
		FMLCommonHandler.instance().bus().register(new ConfigurationHandler());
	}

	@Mod.EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		List<String> lines = new ArrayList();
		List<ModContainer> mods = new ArrayList<ModContainer>();
		for (ModContainer mod : Loader.instance().getModList())
			mods.add(mod);

		Collections.sort(mods, new Comparator<ModContainer>() {
			@Override
			public int compare(ModContainer mod1, ModContainer mod2) {
				return mod1.getName().toLowerCase()
						.compareTo(mod2.getName().toLowerCase());
			}
		});

		search: for (ModContainer mod : mods) {
			for (String noPrint : blackList) {
				if (mod.getName().contains(noPrint))
					continue search;
			}
			lines.add(createLine(mod));
		}
		try {
			File file = new File(mcDir, "Versions.md");
			BufferedWriter writer = new BufferedWriter(new FileWriter(file));

			writer.write("Current Forge Version\r=\r");

			writer.write("- **Forge** v" + ForgeVersion.getVersion() + "\r\r");

			writer.write("Current Mod Versions\r=");

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

	private String createLine(ModContainer container) {
		StringBuilder builder = new StringBuilder();
		builder.append("\r");
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