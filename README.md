# Nin's Mod Lister [![Build Status](http://play.nincraft.com:8080/buildStatus/icon?job=Nins Mod Lister)](http://play.nincraft.com:8080/job/Nins Mod Lister)
A Minecraft Forge mod that lists the mods in your pack. It is also configurable so you can hide and categorize mods.
#Configurations
## Blacklist
The blacklist is a list of mods or mod IDs that you don't want to show up anywhere in your mod list. The list can be full names or partial matches.
##Category Groups
Category groups are for any mods that you don't want to show up in the general catch all category. To add a mod to a category give the name of the category and the mod name or mod ID separated by a colon. For example if you wanted Nincrafty Things to show up in the Greatest Mods Ever by Nin category, you'd put "Greatest Mods Ever by Nin:Nincrafty Things".
##Overrides
Overrides let you display a mod name, version, or author list other than what's provided by the mcmod.info file. The syntax for this is ModID:OverrideType:OverrideText. ModID is just the mod id of the mod you are overriding. OverrideType is the field you are trying to override. There are three fields available for override, "name", "version" and "authorlist". OverrideText is the text that you will see instead of what the mod provides.
##Category Priority
Here you can define what order you want your categories to show up in. Just list your categories in the order you want them to show up in your mod list.
##File Name
Simple, the name of the output file.
##General Category Title
The name of the catch all category.
