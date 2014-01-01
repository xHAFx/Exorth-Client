final class Skills {

	public static final int skillsCount = 22;
	public static final String[] skillNames = { "Attack", "Hitpoints", "Mining", "Strength", "Agility", "Smithing", "Defence", "Herblore",
			"Fishing", "Ranged", "Thieving", "Cooking", "Prayer", "Crafting", "Firemaking", "Magic", "Fletching", "Woodcutting",
			"Runecrafting", "Slayer", "Farming", "Construction" };
}

class MagicSpell {
	private int spellLevel;
	private String spellName;
	private String desc;
	private int[] runes;
	private int[] reqRunes;
	private int reqPrayerLevel = 0;
	
	public MagicSpell(int lvl, String name, String desc, int[] runes, int[] reqRunes, int prayer) {
		this.spellLevel = lvl;
		this.spellName = name;
		this.desc = desc;
		this.runes = runes;
		this.reqRunes = reqRunes;
		this.reqPrayerLevel = prayer;
	}
	
	public int getSpellLevel() {
		return spellLevel;
	}
	
	public String getSpellName() {
		return spellName;
	}
	
	public String getDesc() {
		return desc;
	}
	
	public int[] getRunes() {
		return runes;
	}
	
	public int[] getReqRunes() {
		return reqRunes;
	}
	
	public int getReqPrayerLevel() {
		return reqPrayerLevel;
	}
}
