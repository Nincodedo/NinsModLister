package com.nincodedo.ninsmodlister;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import com.nincodedo.ninsmodlister.common.NinModContainer;
import com.nincodedo.ninsmodlister.handler.ConfigurationHandler;
import com.nincodedo.ninsmodlister.reference.OverrideType;
import com.nincodedo.ninsmodlister.reference.Reference;
import com.nincodedo.ninsmodlister.reference.Settings;
import com.nincodedo.ninsmodlister.utility.LogHelper;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.common.ForgeVersion;

@Mod(modid = Reference.MOD_ID, name = Reference.MOD_NAME, version = Reference.VERSION, dependencies = Reference.DEPENDENCIES)
public final class NinsModLister {

	@Mod.Instance(Reference.MOD_ID)
	public static NinsModLister instance;

	File mcDir;

	List<String> blackList;
	List<String> priorityList;
	List<String> overrides;

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
		HashMap<String, List<NinModContainer>> customCategories = new HashMap<String, List<NinModContainer>>();
		List<NinModContainer> modIds = new ArrayList<NinModContainer>();
		blackList = Arrays.asList(Settings.configBlackList);
		priorityList = Arrays.asList(Settings.categoryPriority);
		overrides = Arrays.asList(Settings.overrides);

		for (ModContainer fmod : Loader.instance().getModList()) {
			NinModContainer mod = new NinModContainer(fmod);
			if (checkBlackList(mod)) {
				found = false;
				for (String line : Settings.categoryGroups) {
					String category = line.split(":")[0];
					String modId = line.split(":")[1];
					if (mod.getModId().equals(modId) || mod.getName().equals(modId)) {
						modIds = customCategories.getOrDefault(category, new ArrayList<NinModContainer>());
						customCategories.put(category, modIds);
						modIds.add(processOverrides(mod));
						found = true;
					}
				}
				if (!found) {
					modIds = customCategories.getOrDefault(Settings.generalCategoryTitle,
							new ArrayList<NinModContainer>());
					customCategories.put(Settings.generalCategoryTitle, modIds);
					modIds.add(processOverrides(mod));
				}
			}
		}

		Comparator<NinModContainer> compareMods = (m1, m2) -> m1.getName().toLowerCase()
				.compareTo(m2.getName().toLowerCase());

		for (Entry<String, List<NinModContainer>> entry : customCategories.entrySet()) {
			Collections.sort(entry.getValue(), compareMods);
		}

		lines.add("\n");

		for (String priority : priorityList) {
			Optional<List<NinModContainer>> entries = Optional.ofNullable(customCategories.get(priority));

			if (entries.isPresent()) {
				lines.add("\n" + priority + "\n=");
				for (NinModContainer mod : entries.get()) {
					lines.add(createLine(mod));
				}
				lines.add("\n");
			}
		}

		try {
			File file = new File(mcDir, Settings.fileName);
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"));

			if (Settings.showForgeVersion) {
				writer.write("Current Forge Version\n=\n");

				writer.write("- **Forge** v" + ForgeVersion.getVersion());
			}

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

	private NinModContainer processOverrides(NinModContainer mod) {
		String[] overrideList;
		for (String overrideLine : overrides) {
			overrideList = overrideLine.split(":");
			String modId = overrideList[0];
			if (modId.equals(mod.getModId())) {
				String overrideType = overrideList[1];
				String override = overrideList[2];

				switch (overrideType) {
				case OverrideType.NAME:
					mod.setName(override);
					break;
				case OverrideType.VERSION:
					mod.setVersion(override);
					break;
				case OverrideType.AUTHORLIST:
					mod.setAuthorList(override);
					break;
				default:
					break;
				}
			}
		}
		return mod;
	}

	private boolean checkBlackList(NinModContainer mod) {
		try {
			for (String blackListItem : blackList) {
				if (Pattern.matches(blackListItem, mod.getName()) || Pattern.matches(blackListItem, mod.getModId())) {
					return false;
				}
			}
		} catch (PatternSyntaxException e) {
			LogHelper.error(e);
		}
		return true;
	}

	private String createLine(NinModContainer mod) {
		StringBuilder builder = new StringBuilder();
		builder.append("\n");
		builder.append("- **");
		builder.append(mod.getName());
		builder.append("** ");
		if (mod.getVersion().length() > 0 && mod.getVersion().charAt(0) != 'v' && mod.getVersion().charAt(0) != 'r')
			builder.append("v");
		builder.append(mod.getVersion());

		if (mod.getMetadata().getAuthorList() != null && mod.getMetadata().getAuthorList().length() > 0) {
			builder.append(" by ");
			builder.append(mod.getAuthorList());
		}

		return builder.toString();
	}

}