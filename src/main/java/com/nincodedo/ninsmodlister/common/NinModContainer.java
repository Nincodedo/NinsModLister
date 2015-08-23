package com.nincodedo.ninsmodlister.common;

import cpw.mods.fml.common.InjectedModContainer;
import cpw.mods.fml.common.ModContainer;

public class NinModContainer extends InjectedModContainer {

	private String modId;
	private String name;
	private String authorList;
	private String version;

	public NinModContainer(ModContainer fmod) {
		super(fmod, null);
		this.modId = fmod.getModId();
		this.name = fmod.getName();
		this.authorList = fmod.getMetadata().getAuthorList();
		this.version = fmod.getVersion();
	}

	@Override
	public String getModId() {
		return modId;
	}

	public void setModId(String modId) {
		this.modId = modId;
	}

	@Override
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAuthorList() {
		return authorList;
	}

	public void setAuthorList(String authorList) {
		this.authorList = authorList;
	}

	@Override
	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}
}
