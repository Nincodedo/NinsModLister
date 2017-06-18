package com.nincodedo.ninsmodlister;

import com.nincodedo.ninsmodlister.common.NinModContainer;
import com.nincodedo.ninsmodlister.handler.ConfigurationHandler;
import com.nincodedo.ninsmodlister.reference.OverrideType;
import com.nincodedo.ninsmodlister.reference.Reference;
import com.nincodedo.ninsmodlister.reference.Settings;
import com.nincodedo.ninsmodlister.utility.LogHelper;
import net.minecraftforge.common.ForgeVersion;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

@Mod(modid = Reference.MOD_ID, name = Reference.MOD_NAME, version = Reference.VERSION, dependencies = Reference.DEPENDENCIES)
public final class NinsModLister {

	@Mod.Instance(Reference.MOD_ID)
	private File mcDir;

	private List<String> blackList;
	private List<String> overrides;

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		mcDir = event.getModConfigurationDirectory().getParentFile();
		ConfigurationHandler.init(event.getSuggestedConfigurationFile());
		MinecraftForge.EVENT_BUS.register(new ConfigurationHandler());
	}

	@Mod.EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		List<String> lines = new ArrayList<>();
		boolean found;
		HashMap<String, List<NinModContainer>> customCategories = new HashMap<String, List<NinModContainer>>();
		List<NinModContainer> modIds;
		blackList = Arrays.asList(Settings.configBlackList);
		List<String> priorityList = Arrays.asList(Settings.categoryPriority);
		overrides = Arrays.asList(Settings.overrides);

		for (ModContainer fmod : Loader.instance().getModList()) {
			NinModContainer mod = new NinModContainer(fmod);
			if (checkBlackList(mod)) {
				found = false;
				for (String line : Settings.categoryGroups) {
					String category = line.split(":")[0];
					String modId = line.split(":")[1];
					if (mod.getModId().equals(modId) || mod.getName().equals(modId)) {
						modIds = customCategories.computeIfAbsent(category, k -> new ArrayList<>());
						modIds.add(processOverrides(mod));
						found = true;
					}
				}
				if (!found) {
					modIds = customCategories.computeIfAbsent(Settings.generalCategoryTitle, k -> new ArrayList<>());
					modIds.add(processOverrides(mod));
				}
			}
		}

		Comparator<ModContainer> compareMods = Comparator.comparing(mod1 -> mod1.getName().toLowerCase());

		for (Entry<String, List<NinModContainer>> entry : customCategories.entrySet()) {
			entry.getValue().sort(compareMods);
		}

		lines.add("\n");

		for (String priority : priorityList) {
			List<NinModContainer> entries = customCategories.get(priority);
			if (entries != null) {
				lines.add("\n" + priority + "\n=");
				for (NinModContainer mod : entries) {
					lines.add(createLine(mod));
				}
				lines.add("\n");
			}
		}

		FileOutputStream outputStream = null;
		try {
			File file = new File(mcDir, Settings.fileName);
			outputStream = new FileOutputStream(file);
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));

			if (Settings.showForgeVersion) {
				writer.write("Current Forge Version\n=\n");

				writer.write("- **Forge** v" + ForgeVersion.getVersion());
			}

			for (String s : lines)
				writer.write(s);

			writer.close();

			if (!file.exists())
				file.createNewFile();

			LogHelper.debug("NinsModLister wrote mod data properly.");
		} catch (IOException e) {
			LogHelper.error("NinsModLister failed to write mod data!");
			LogHelper.error(e.getStackTrace());
		}finally {
			IOUtils.closeQuietly(outputStream);
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