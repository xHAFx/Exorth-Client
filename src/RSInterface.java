import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * TODO: Cache pack when finished
 * 
 * @author AkZu
 * 
 */
public class RSInterface {

	/**
	 * If true, replaces ids of text converted to strings.
	 */
	public static boolean showIds = false;
	
	public void swapInventoryItems(int i, int j) {
		int k = inv[i];
		inv[i] = inv[j];
		inv[j] = k;
		k = invStackSizes[i];
		invStackSizes[i] = invStackSizes[j];
		invStackSizes[j] = k;
	}

	public static void unpack(final CacheArchive streamLoader, final TextDrawingArea[] textDrawingAreas, final CacheArchive streamLoader_1) {
		spriteCache = new MemoryCache(1000); // 50000 originally A MUST <--
		final RSBuffer stream = new RSBuffer(// FileOperations.ReadFile(Signlink.cacheLocation()
				// + "interface_317.dat"));//
				// + "interface_asgarnia_317.dat"));
				streamLoader.getDataForName("data"));
		int i = -1;
		int j = stream.readUShort();
		interfaceCache = new RSInterface[j];

		while (stream.pointer < stream.buffer.length) {
			int k = stream.readUShort();
			if (k == 65535) {
				i = stream.readUShort();
				k = stream.readUShort();
			}
			RSInterface rsi = interfaceCache[k] = new RSInterface();
			rsi.id = (short) k;
			rsi.parentID = (short) i;
			rsi.type = (byte) stream.readUByte();
			rsi.atActionType = (short) stream.readUByte();
			rsi.contentType = (short) stream.readUShort();
			rsi.width = (short) stream.readUShort();
			rsi.height = (short) stream.readUShort();
			rsi.alpha = (byte) stream.readUByte();
			rsi.mouseOverPopupInterface = (short) stream.readUByte();
			if (rsi.mouseOverPopupInterface != 0)
				rsi.mouseOverPopupInterface = (short) ((rsi.mouseOverPopupInterface - 1 << 8) + stream.readUByte());
			else
				rsi.mouseOverPopupInterface = -1;
			int i1 = stream.readUByte();
			if (i1 > 0) {
				rsi.valueCompareType = new int[i1];
				rsi.requiredValues = new int[i1];
				for (int j1 = 0; j1 < i1; j1++) {
					rsi.valueCompareType[j1] = stream.readUByte();
					rsi.requiredValues[j1] = stream.readUShort();
				}
			}
			int k1 = stream.readUByte();
			if (k1 > 0) {
				rsi.valueIndexArray = new int[k1][];
				for (int l1 = 0; l1 < k1; l1++) {
					int i3 = stream.readUShort();
					rsi.valueIndexArray[l1] = new int[i3];
					for (int l4 = 0; l4 < i3; l4++)
						rsi.valueIndexArray[l1][l4] = stream.readUShort();
				}
			}
			if (rsi.type == 0) {
				rsi.scrollMax = (short) stream.readUShort();
				rsi.isMouseoverTriggered = stream.readUByte() == 1;
				int i2 = stream.readUShort();
				rsi.children = new short[i2];
				rsi.childX = new short[i2];
				rsi.childY = new short[i2];
				for (int j3 = 0; j3 < i2; j3++) {
					rsi.children[j3] = (short) stream.readUShort();
					rsi.childX[j3] = (short) stream.readShort();
					rsi.childY[j3] = (short) stream.readShort();
				}
			}
			// dummy (rsi.type == 1)
			if (rsi.type == 2) {
				rsi.inv = new int[rsi.width * rsi.height];
				rsi.invStackSizes = new int[rsi.width * rsi.height];
				rsi.itemsSwappable = stream.readUByte() == 1;
				rsi.isInventoryInterface = stream.readUByte() == 1;
				rsi.usableItemInterface = stream.readUByte() == 1;
				rsi.dragDeletes = stream.readUByte() == 1;
				rsi.invSpritePadX = (short) stream.readUByte();
				rsi.invSpritePadY = (short) stream.readUByte();
				rsi.spritesX = new short[20];
				rsi.spritesY = new short[20];
				rsi.sprites = new DirectImage[20];
				rsi.spriteNames = new String[20];
				rsi.spriteIds = new short[20];
				for (int j2 = 0; j2 < 20; j2++) {
					int k3 = stream.readUByte();
					if (k3 == 1) {
						rsi.spritesX[j2] = (short) stream.readShort();
						rsi.spritesY[j2] = (short) stream.readShort();
						String s1 = stream.readString();
						if (streamLoader_1 != null && s1.length() > 0) {
							int i5 = s1.lastIndexOf(",");
							// rsInterface.sprites[j2] =
							// loadSprite(Integer.parseInt(s1.substring(i5 +
							// 1)),
							// streamLoader_1, s1.substring(0, i5));
							rsi.setSprites(j2, s1.substring(0, i5), Integer.parseInt(s1.substring(i5 + 1)), streamLoader_1);
						}
					}
				}
				rsi.actions = new String[5];
				for (int l3 = 0; l3 < 5; l3++) {
					rsi.actions[l3] = stream.readString();
					switch (rsi.parentID) {
					case 3824:
						rsi.actions[4] = "Buy 50";
						break;
					case 3822:
						rsi.actions[4] = "Sell 50";
						break;
					case 1644:
						rsi.actions[2] = "Operate";
						break;
					}
					if (rsi.actions[l3].length() == 0)
						rsi.actions[l3] = null;
				}
			}
			if (rsi.type == 3)
				rsi.filled = stream.readUByte() == 1;
			if (rsi.type == 4 || rsi.type == 1) {
				rsi.textCentered = stream.readUByte() == 1;
				rsi.fontId = (short) stream.readUByte();
				if (textDrawingAreas != null) {
					rsi.fonts = textDrawingAreas;
					rsi.font = textDrawingAreas[rsi.fontId];
				}
				rsi.textShadow = stream.readUByte() == 1;
			}
			if (rsi.type == 4) {
				rsi.disabledMessage = stream.readString();
				if (showIds)
					rsi.disabledMessage = Integer.toString(rsi.id);
				rsi.disabledMessage = rsi.disabledMessage.replaceAll("RuneScape", "Exorth");
				rsi.disabledMessage = rsi.disabledMessage.replaceAll("Jagex", "Exorth");
				rsi.enabledMessage = stream.readString();
			}
			if (rsi.type == 1 || rsi.type == 3 || rsi.type == 4)
				rsi.disabledTextColor = stream.readInt();
			if (rsi.type == 3 || rsi.type == 4) {
				rsi.enabledTextColor = stream.readInt();
				rsi.disabledTextHoverColor = stream.readInt();
				rsi.enabledTextHoverColor = stream.readInt();
			}
			if (rsi.type == 5) {
				String s = stream.readString();
				/*
				 * if (rsi.id == 1152) { for (int k11 = 0; k11 <= 21; k11++)
				 * rsi.setDisabledSprite("emoteicons", k11, streamLoader_1); for
				 * (int k11 = 22; k11 <= 36; k11++)
				 * rsi.setEnabledSprite("emoteicons", k11, streamLoader_1); for
				 * (int k11 = 0; k11 <= 14; k11++)
				 * rsi.setDisabledSprite("emotesoff", k11, streamLoader_1);
				 * rsi.setDisabledSprite("optionicons", 1, streamLoader_1);
				 * rsi.setEnabledSprite("optionicons", 7, streamLoader_1);
				 * rsi.setEnabledSprite("optionicons2", 0, streamLoader_1);
				 * rsi.setDisabledSprite("optionicons3", 0, streamLoader_1);
				 * rsi.setEnabledSprite("optionicons3", 1, streamLoader_1);
				 * rsi.setDisabledSprite("optionicons", 0, streamLoader_1);
				 * rsi.setDisabledSprite("optionicons", 9, streamLoader_1);
				 * rsi.setDisabledSprite("optionicons", 4, streamLoader_1);
				 * rsi.setDisabledSprite("optionicons", 3, streamLoader_1);
				 * rsi.setDisabledSprite("optionicons", 5, streamLoader_1);
				 * rsi.setDisabledSprite("optionicons", 6, streamLoader_1);
				 * rsi.setDisabledSprite("optionicons", 10, streamLoader_1);
				 * rsi.setEnabledSprite("magicon2", 36, streamLoader_1);
				 * rsi.setDisabledSprite("magicoff2", 36, streamLoader_1);
				 * rsi.setDisabledSprite("magicoff2", 39, streamLoader_1);
				 * rsi.setDisabledSprite("magicon2", 37, streamLoader_1);
				 * rsi.setDisabledSprite("magicon2", 39, streamLoader_1);
				 * rsi.setDisabledSprite("sideicons", 6, streamLoader_1);
				 * rsi.setDisabledSprite("miscgraphics3", 3, streamLoader_1);
				 * rsi.setDisabledSprite("miscgraphics3", 4, streamLoader_1);
				 * rsi.setDisabledSprite("miscgraphics2", 2, streamLoader_1);
				 * rsi.setDisabledSprite("miscgraphics2", 3, streamLoader_1);
				 * rsi.setDisabledSprite("miscgraphics2", 10, streamLoader_1);
				 * rsi.setDisabledSprite("miscgraphics2", 11, streamLoader_1);
				 * rsi.setDisabledSprite("miscgraphics2", 12, streamLoader_1);
				 * rsi.setDisabledSprite("miscgraphics2", 13, streamLoader_1);
				 * rsi.setDisabledSprite("miscgraphics2", 8, streamLoader_1);
				 * rsi.setDisabledSprite("miscgraphics2", 9, streamLoader_1);
				 * rsi.setDisabledSprite("staticons2", 3, streamLoader_1);
				 * rsi.setEnabledSprite("prayeron", 24, streamLoader_1);
				 * rsi.setDisabledSprite("miscgraphics3", 2, streamLoader_1);
				 * rsi.setDisabledSprite("miscgraphics2", 5, streamLoader_1);
				 * rsi.setDisabledSprite("miscgraphics2", 4, streamLoader_1);
				 * for (int k11 = 18; k11 <= 23; k11++) {
				 * rsi.setEnabledSprite("prayeron", k11, streamLoader_1);
				 * rsi.setDisabledSprite("prayeroff", k11, streamLoader_1); }
				 * rsi.setDisabledSprite("attack2", 0, streamLoader_1); for (int
				 * sprite = 0; sprite < 6; sprite++) {
				 * rsi.setDisabledSprite("attack1", sprite, streamLoader_1);
				 * rsi.setEnabledSprite("attack1", sprite, streamLoader_1); } }
				 * 
				 * 
				 * for (int sprite = 0; sprite < 6; sprite++) { if
				 * (streamLoader_1 != null) {
				 * rsInterface.setDisabledSprite("attack1", sprite,
				 * streamLoader_1); rsInterface.setEnabledSprite("attack1",
				 * sprite, streamLoader_1); } } for (int sprite = 0; sprite <
				 * 15; sprite++) { if (streamLoader_1 != null) {
				 * rsInterface.setDisabledSprite("miscgraphics", sprite,
				 * streamLoader_1); rsInterface.setEnabledSprite("miscgraphics",
				 * sprite, streamLoader_1); } } for (int sprite = 0; sprite <
				 * 14; sprite++) { if (streamLoader_1 != null) {
				 * rsInterface.setDisabledSprite("miscgraphics2", sprite,
				 * streamLoader_1);
				 * rsInterface.setEnabledSprite("miscgraphics2", sprite,
				 * streamLoader_1); } } for (int sprite = 0; sprite < 8;
				 * sprite++) { if (streamLoader_1 != null) {
				 * rsInterface.setDisabledSprite("attack2", sprite,
				 * streamLoader_1); rsInterface.setEnabledSprite("attack2",
				 * sprite, streamLoader_1); } } for (int sprite = 0; sprite < 7;
				 * sprite++) { if (streamLoader_1 != null) {
				 * rsInterface.setDisabledSprite("pestcontrol", sprite,
				 * streamLoader_1); rsInterface.setEnabledSprite("pestcontrol",
				 * sprite, streamLoader_1); } } for (int sprite = 0; sprite < 6;
				 * sprite++) { if (streamLoader_1 != null) {
				 * rsInterface.setDisabledSprite("lunar2", sprite,
				 * streamLoader_1); rsInterface.setEnabledSprite("lunar2",
				 * sprite, streamLoader_1); } } for (int sprite = 0; sprite <
				 * 39; sprite++) { if (streamLoader_1 != null) {
				 * rsInterface.setDisabledSprite("lunar0", sprite,
				 * streamLoader_1); rsInterface.setEnabledSprite("lunar0",
				 * sprite, streamLoader_1); } } for (int sprite = 0; sprite <
				 * 39; sprite++) { if (streamLoader_1 != null) {
				 * rsInterface.setDisabledSprite("lunar1", sprite,
				 * streamLoader_1); rsInterface.setEnabledSprite("lunar1",
				 * sprite, streamLoader_1); } } for (int sprite = 0; sprite < 4;
				 * sprite++) { if (streamLoader_1 != null) {
				 * rsInterface.setDisabledSprite("attack3", sprite,
				 * streamLoader_1); rsInterface.setEnabledSprite("attack3",
				 * sprite, streamLoader_1); } }
				 */

				if (streamLoader_1 != null && s.length() > 0) {
					int i4 = s.lastIndexOf(",");
					// rsInterface.disabledSprite =
					// loadSprite(Integer.parseInt(s.substring(i4 + 1)),
					// streamLoader_1, s.substring(0, i4));
					// if (s.substring(0, i4).startsWith("sworddecor"))
					// System.err.println(rsi.parentID);
					rsi.setDisabledSprite(s.substring(0, i4), Integer.parseInt(s.substring(i4 + 1)), streamLoader_1);
				}
				s = stream.readString();
				if (streamLoader_1 != null && s.length() > 0) {
					int j4 = s.lastIndexOf(",");
					// rsInterface.enabledSprite =
					// loadSprite(Integer.parseInt(s.substring(j4 + 1)),
					// streamLoader_1, s.substring(0, j4));
					rsi.setEnabledSprite(s.substring(0, j4), Integer.parseInt(s.substring(j4 + 1)), streamLoader_1);
					// if (rsInterface.enabledSprite != null) {
					// System.out.println("ID: " + rsInterface.id + " " +
					// "CONFIG: " + rsInterface.anIntArray212[1]);
					// System.out.println("ID: " + rsInterface.id + " " +
					// Integer.parseInt(s.substring(j4 + 1)) + " " +
					// s.substring(0, j4));
					// }
				}
			}
			if (rsi.type == 6) {
				int l = stream.readUByte();
				if (l != 0) {
					rsi.mediaType = 1;
					rsi.mediaID = (short) ((l - 1 << 8) + stream.readUByte());
				}
				l = stream.readUByte();
				if (l != 0) {
					rsi.enabledMediaType = 1;
					rsi.enabledMediaID = (short) ((l - 1 << 8) + stream.readUByte());
				}
				l = stream.readUByte();
				if (l != 0)
					rsi.disabledAnimation = (short) ((l - 1 << 8) + stream.readUByte());
				else
					rsi.disabledAnimation = -1;
				l = stream.readUByte();
				if (l != 0)
					rsi.enabledAnimation = (short) ((l - 1 << 8) + stream.readUByte());
				else
					rsi.enabledAnimation = -1;
				rsi.modelZoom = (short) stream.readUShort();
				rsi.modelRotY = (short) stream.readUShort();
				rsi.modelRotX = (short) stream.readUShort();
			}
			if (rsi.type == 7) {
				rsi.inv = new int[rsi.width * rsi.height];
				rsi.invStackSizes = new int[rsi.width * rsi.height];
				rsi.textCentered = stream.readUByte() == 1;
				rsi.fontId = (short) stream.readUByte();
				if (textDrawingAreas != null) {
					rsi.fonts = textDrawingAreas;
					rsi.font = textDrawingAreas[rsi.fontId];
				}
				rsi.textShadow = stream.readUByte() == 1;
				rsi.disabledTextColor = stream.readInt();
				rsi.invSpritePadX = (short) stream.readShort();
				rsi.invSpritePadY = (short) stream.readShort();
				rsi.isInventoryInterface = stream.readUByte() == 1;
				rsi.actions = new String[5];
				for (int k4 = 0; k4 < 5; k4++) {
					rsi.actions[k4] = stream.readString();
					if (rsi.actions[k4].length() == 0)
						rsi.actions[k4] = null;
				}
			}
			if (rsi.atActionType == 2 || rsi.type == 2) {
				rsi.selectedActionName = stream.readString();
				rsi.spellName = stream.readString();
				rsi.spellUsableOn = (short) stream.readUShort();
			}
			if (rsi.type == 8)
				rsi.disabledMessage = stream.readString();
			if (rsi.atActionType == 1 || rsi.atActionType == 4 || rsi.atActionType == 5 || rsi.atActionType == 6) {
				rsi.tooltip = stream.readString();
				if (rsi.tooltip.length() == 0) {
					if (rsi.atActionType == 1)
						rsi.tooltip = "Ok";
					if (rsi.atActionType == 4)
						rsi.tooltip = "Select";
					if (rsi.atActionType == 5)
						rsi.tooltip = "Select";
					if (rsi.atActionType == 6)
						rsi.tooltip = "Continue";
				}
			}
		}
		aClass44 = streamLoader_1;
		/**
		 * A 'cache' of sprites used in interface, so we can use them later on
		 * when dynamicly changing sprites on-the-go. E.g the combat tab
		 * interface with the Autocast option on spells (the spell icons).
		 */
		hardcodedInterfaces(textDrawingAreas);
		magicBook(textDrawingAreas);
	}

	public static void clearCache() {
		interfaceCache = null;
		spriteCache = null;
		aClass44 = null;
	}

	/**
	 * Writes the interface cache (data.dat).
	 */
	public static void save() {
		try {
			RSBuffer buffer = new RSBuffer(new byte[875964]); // check from file
																// size
			buffer.writeShort(interfaceCache.length);

			for (RSInterface rsi : interfaceCache) {
				if (rsi == null) {
					continue;
				}
				if (rsi.parentID != -1) {
					buffer.writeShort(65535);
					buffer.writeShort(rsi.parentID);
					buffer.writeShort(rsi.id);
				} else {
					buffer.writeShort(rsi.id);
				}

				buffer.writeByte(rsi.type);
				buffer.writeByte(rsi.atActionType);
				buffer.writeShort(rsi.contentType);
				buffer.writeShort(rsi.width);
				buffer.writeShort(rsi.height);
				buffer.writeByte(rsi.alpha);
				if (rsi.mouseOverPopupInterface != -1) {
					buffer.writeSpaceSaver(rsi.mouseOverPopupInterface);
				} else {
					buffer.writeByte(0);
				}
				int valueCompareTypeCount = 0;
				if (rsi.valueCompareType != null) {
					valueCompareTypeCount = rsi.valueCompareType.length;
				}
				buffer.writeByte(valueCompareTypeCount);
				if (valueCompareTypeCount > 0) {
					for (int i = 0; i < valueCompareTypeCount; i++) {
						buffer.writeByte(rsi.valueCompareType[i]);
						buffer.writeShort(rsi.requiredValues[i]);
					}
				}
				int valueLength = 0;
				if (rsi.valueIndexArray != null) {
					valueLength = rsi.valueIndexArray.length;
				}
				buffer.writeByte(valueLength);
				if (valueLength > 0) {
					for (int index = 0; index < valueLength; index++) {
						int total = rsi.valueIndexArray[index].length;
						buffer.writeShort(total);
						for (int index2 = 0; index2 < total; index2++) {
							buffer.writeShort(rsi.valueIndexArray[index][index2]);
						}
					}
				}
				if (rsi.type == 0) {
					buffer.writeShort(rsi.scrollMax);
					buffer.writeByte(rsi.isMouseoverTriggered ? 1 : 0);
					buffer.writeShort(rsi.children.length);
					for (int i = 0; i < rsi.children.length; i++) {
						buffer.writeShort(rsi.children[i]);
						buffer.writeShort(rsi.childX[i]);
						buffer.writeShort(rsi.childY[i]);
					}
				} else if (rsi.type == 2) {
					buffer.writeByte(rsi.itemsSwappable ? 1 : 0);
					buffer.writeByte(rsi.isInventoryInterface ? 1 : 0);
					buffer.writeByte(rsi.usableItemInterface ? 1 : 0);
					buffer.writeByte(rsi.dragDeletes ? 1 : 0);
					buffer.writeByte(rsi.invSpritePadX);
					buffer.writeByte(rsi.invSpritePadY);
					for (int index = 0; index < 20; index++) {
						buffer.writeByte(rsi.sprites == null ? 0 : 1);
						if (rsi.sprites != null) {
							buffer.writeShort(rsi.spritesX[index]);
							buffer.writeShort(rsi.spritesY[index]);
							buffer.writeString(rsi.spriteNames[index] + "," + rsi.spriteIds[index]);
						}
					}
					for (int index = 0; index < 5; index++) {
						if (rsi.actions[index] != null) {
							buffer.writeString(rsi.actions[index]);
						} else {
							buffer.writeString("");
						}
					}
				} else if (rsi.type == 3) {
					buffer.writeByte(rsi.filled ? 1 : 0);
				}
				if (rsi.type == 4 || rsi.type == 1) {
					buffer.writeByte(rsi.textCentered ? 1 : 0);
					buffer.writeByte(rsi.fontId);
					buffer.writeByte(rsi.textShadow ? 1 : 0);
				}
				if (rsi.type == 4) {
					buffer.writeString(rsi.disabledMessage);
					if (rsi.enabledMessage != null) {
						buffer.writeString(rsi.enabledMessage);
					} else {
						buffer.writeString("null");
					}
				}
				if (rsi.type == 1 || rsi.type == 3 || rsi.type == 4)
					buffer.writeInt(rsi.disabledTextColor);
				if (rsi.type == 3 || rsi.type == 4) {
					buffer.writeInt(rsi.enabledTextColor);
					buffer.writeInt(rsi.disabledTextHoverColor);
					buffer.writeInt(rsi.enabledTextHoverColor);
				}
				if (rsi.type == 5) {
					if (rsi.disabledSprite != null) {
						if (rsi.disabledSpriteName != null) {
							buffer.writeString(rsi.disabledSpriteName + "," + rsi.disabledSpriteId);
						} else {
							buffer.writeString("");
						}
					} else {
						buffer.writeString("");
					}
					if (rsi.enabledSprite != null) {
						if (rsi.enabledSpriteName != null) {
							buffer.writeString(rsi.enabledSpriteName + "," + rsi.enabledSpriteId);
						} else {
							buffer.writeString("");
						}
					} else {
						buffer.writeString("");
					}
				} else if (rsi.type == 6) {
					if (rsi.mediaType != -1 && rsi.mediaID > 0) {
						buffer.writeSpaceSaver(rsi.mediaID);
					} else {
						buffer.writeByte(0);
					}
					if (rsi.enabledMediaType > 0) {
						buffer.writeSpaceSaver(rsi.enabledMediaID);
					} else {
						buffer.writeByte(0);
					}
					if (rsi.disabledAnimation > 0) {
						buffer.writeSpaceSaver(rsi.disabledAnimation);
					} else {
						buffer.writeByte(0);
					}
					if (rsi.enabledAnimation > 0) {
						buffer.writeSpaceSaver(rsi.enabledAnimation);
					} else {
						buffer.writeByte(0);
					}
					buffer.writeShort(rsi.modelZoom);
					buffer.writeShort(rsi.modelRotY);
					buffer.writeShort(rsi.modelRotX);
				} else if (rsi.type == 7) {
					buffer.writeByte(rsi.textCentered ? 1 : 0);
					buffer.writeByte(rsi.fontId);
					buffer.writeByte(rsi.textShadow ? 1 : 0);
					buffer.writeInt(rsi.disabledTextColor);
					buffer.writeShort(rsi.invSpritePadX);
					buffer.writeShort(rsi.invSpritePadY);
					buffer.writeByte(rsi.isInventoryInterface ? 1 : 0);
					for (int i = 0; i < 5; i++) {
						if (rsi.actions[i] != null) {
							buffer.writeString(rsi.actions[i]);
						} else {
							buffer.writeString("");
						}
					}
				}
				if (rsi.atActionType == 2 || rsi.type == 2) {
					if (rsi.selectedActionName != null)
						buffer.writeString(rsi.selectedActionName);
					if (rsi.spellName != null) {
						buffer.writeString(rsi.spellName);
						buffer.writeShort(rsi.spellUsableOn);
					}
				}
				if (rsi.type == 8) {
					buffer.writeString(rsi.disabledMessage);
				}
				if (rsi.atActionType == 1 || rsi.atActionType == 4 || rsi.atActionType == 5 || rsi.atActionType == 6) {
					buffer.writeString(rsi.tooltip);
				}
			}
			DataOutputStream out = new DataOutputStream(new FileOutputStream(Signlink.cacheLocation() + "interface_317.dat"));
			out.write(buffer.buffer, 0, buffer.pointer);
			out.close();
			System.err.println("Interface cache (data.dat) saved successfully! Length: " + buffer.pointer);
		} catch (IOException e) {
			System.err.println("An error occurred while saving the interface cache.");
			e.printStackTrace();
		}
	}

	public static RSInterface addInterface(int id) {
		RSInterface rsi = interfaceCache[id] = new RSInterface();
		rsi.id = (short) id;
		rsi.parentID = (short) id;
		rsi.width = 512;
		rsi.height = 334;
		return rsi;
	}

	public static void addText(int id, String text, TextDrawingArea tda[], int idx, int color, boolean centered) {
		RSInterface rsi = interfaceCache[id];
		if (centered)
			rsi.textCentered = true;
		rsi.textShadow = true;
		rsi.fontId = idx;
		if (tda != null) {
			rsi.fonts = tda;
			rsi.font = tda[idx];
		}
		rsi.mouseOverPopupInterface = -1;
		rsi.atActionType = 0;
		rsi.contentType = 0;
		rsi.disabledTextHoverColor = 0;
		rsi.isMouseoverTriggered = false;
		rsi.disabledMessage = text;
		rsi.disabledTextColor = color;
		rsi.id = (short) id;
		rsi.type = 4;
	}

	public static void addText(int id, String text, TextDrawingArea tda[], int idx, int color, boolean centered, boolean shadow) {
		RSInterface rsi = addInterface(id);
		if (centered)
			rsi.textCentered = true;
		if (shadow)
			rsi.textShadow = true;
		rsi.fontId = idx;
		if (tda != null) {
			rsi.fonts = tda;
			rsi.font = tda[idx];
		}
		rsi.mouseOverPopupInterface = -1;
		rsi.atActionType = 0;
		rsi.contentType = 0;
		rsi.disabledTextHoverColor = 0;
		rsi.isMouseoverTriggered = false;
		rsi.disabledMessage = text;
		rsi.disabledTextColor = color;
		rsi.id = (short) id;
		rsi.type = 4;
	}

	public static void addTextAction(int id, String text, TextDrawingArea tda[], int idx, int color, boolean centered, boolean shadow,
			String tooltip) {
		RSInterface rsi = interfaceCache[id];
		rsi.textCentered = centered;
		rsi.textShadow = shadow;
		rsi.fontId = idx;
		if (tda != null) {
			rsi.fonts = tda;
			rsi.font = tda[idx];
		}
		rsi.disabledMessage = text;
		rsi.atActionType = 1;
		rsi.mouseOverPopupInterface = -1;
		rsi.isMouseoverTriggered = false;
		rsi.width = (short) tda[idx].getTextWidth(text);
		rsi.height = 14;
		rsi.disabledTextColor = color;
		rsi.disabledTextHoverColor = 0xffffff;
		rsi.id = (short) id;
		rsi.type = 4;
		rsi.tooltip = tooltip;
	}

	public static void addTextPC(int id, String text, TextDrawingArea tda[], int idx, int color, boolean centered) {
		RSInterface rsi = interfaceCache[id];
		if (centered)
			rsi.textCentered = true;
		rsi.textShadow = true;
		rsi.fontId = idx;
		if (tda != null) {
			rsi.fonts = tda;
			rsi.font = tda[idx];
		}
		rsi.isMouseoverTriggered = false;
		rsi.mouseOverPopupInterface = -1;
		rsi.disabledTextHoverColor = 0xffffff;
		rsi.atActionType = 1;
		rsi.width = (short) tda[idx].getTextWidth(text);
		rsi.height = 12;
		rsi.tooltip = "Select";
		rsi.disabledMessage = text;
		rsi.disabledTextColor = color;
		rsi.id = (short) id;
		rsi.type = 4;
	}

	public static void addPCButton(int id, int id2) {
		RSInterface rsi = addInterface(id);
		rsi.id = (short) id;
		rsi.type = 3;
		rsi.filled = false;
		rsi.contentType = 0;
		rsi.atActionType = 1;
		rsi.tooltip = "Confirm";
		rsi.disabledTextColor = 0;
		rsi.width = 133;
		rsi.height = 48;
		rsi.alpha = (byte) 255;
		rsi.isMouseoverTriggered = false;
		rsi.mouseOverPopupInterface = (short) id2;
	}

	public static void addPCHover(int id) {
		RSInterface tab = addInterface(id);
		tab.parentID = (short) id;
		tab.id = (short) id;
		tab.type = 0;
		tab.atActionType = 0;
		tab.width = 133;
		tab.height = 48;
		tab.isMouseoverTriggered = true;
		tab.alpha = 0;
		tab.mouseOverPopupInterface = -1;
		tab.scrollMax = 0;
		addPCHoverFunc(id + 1);
		tab.totalChildren(1);
		tab.child(0, id + 1, 0, 0);
	}

	public static void addPCHoverFunc(int id) {
		RSInterface itf = addInterface(id);
		itf.id = (short) id;
		itf.parentID = (short) id;
		itf.type = 3;
		itf.disabledTextColor = 0xF5F5DC;
		itf.alpha = (byte) 230;
		itf.filled = true;
	}

	public static void addBGHover(int id, int color, int shadow, int width, int height) {
		RSInterface itf = addInterface(id);
		itf.id = (short) id;
		itf.parentID = (short) id;
		itf.type = 3;
		itf.isMouseoverTriggered = true;
		itf.mouseOverPopupInterface = -1;
		itf.width = (short) width;
		itf.height = (short) height;
		itf.disabledTextColor = color;
		itf.alpha = (byte) shadow;
		itf.filled = true;
	}

	public static void addTextHover(int id, String text, TextDrawingArea tda[], int idx, int color, boolean centered) {
		RSInterface rsi = interfaceCache[id];
		if (centered)
			rsi.textCentered = true;
		rsi.textShadow = true;
		rsi.parentID = (short) id;
		rsi.contentType = 0;
		rsi.isMouseoverTriggered = false;
		rsi.mouseOverPopupInterface = -1;
		rsi.atActionType = 1;
		rsi.fontId = idx;
		if (tda != null) {
			rsi.fonts = tda;
			rsi.font = tda[idx];
		}
		rsi.tooltip = "Select";
		rsi.disabledMessage = text;
		rsi.disabledTextHoverColor = 0xff0000;
		rsi.disabledTextColor = color;
		rsi.height = 14;
		rsi.width = 124;
		rsi.id = (short) id;
		rsi.type = 4;
	}

	public static void textColor(int id, int color) {
		RSInterface rsi = interfaceCache[id];
		rsi.disabledTextColor = color;
	}

	public static void textMessage(int id, String text) {
		RSInterface rsi = interfaceCache[id];
		rsi.disabledMessage = text;
	}

	public static void textSpellName(int id, String text) {
		RSInterface rsi = interfaceCache[id];
		rsi.spellName = text;
	}

	public static void textTooltip(int id, String text) {
		RSInterface rsi = interfaceCache[id];
		rsi.tooltip = text;
	}

	public static void rsiParentId(int id, int parent) {
		RSInterface rsi = interfaceCache[id];
		rsi.parentID = (short) parent;
	}

	public static void textSize(int id, TextDrawingArea tda[], int idx) {
		RSInterface rsi = interfaceCache[id];
		rsi.fontId = idx;
		rsi.font = tda[idx];
		if (tda != null) {
			rsi.fonts = tda;
			rsi.font = tda[rsi.fontId];
		}
	}

	public static void textCentered(int id, boolean flag) {
		RSInterface rsi = interfaceCache[id];
		rsi.textCentered = flag;
	}

	public static void textShadowed(int id, boolean flag) {
		RSInterface rsi = interfaceCache[id];
		rsi.textShadow = flag;
	}

	public static void addTooltip8(int id, int width, int height, String text) {
		RSInterface rsi = addInterface(id);
		rsi.id = (short) id;
		rsi.parentID = (short) id;
		rsi.type = 8;
		rsi.isMouseoverTriggered = true;
		rsi.mouseOverPopupInterface = -1;
		rsi.width = (short) width;
		rsi.height = (short) height;
		rsi.disabledMessage = text;
	}

	public static void addCacheSprite(int id, int sprite1, int sprite2, String sprites) {
		RSInterface rsi = interfaceCache[id];
		rsi.setDisabledSprite(sprites, sprite1, aClass44);
		rsi.setEnabledSprite(sprites, sprite2, aClass44);
		rsi.parentID = (short) id;
		rsi.id = (short) id;
		rsi.type = 5;
	}

	public static void createBackground(int id, int alpha, int color) {
		RSInterface rsi = interfaceCache[id];
		rsi.width = 512;
		rsi.height = 334;
		rsi.type = 3;
		rsi.id = (short) id;
		rsi.filled = true;
		rsi.alpha = (byte) alpha;
		rsi.disabledTextColor = color;
	}

	public static void addSprite(int id, int sprite, String name) {
		RSInterface rsi = addInterface(id);
		rsi.type = 5;
		rsi.id = (short) id;
		rsi.setDisabledSprite(name, sprite, aClass44);
	}

	public static void addActionButton(int id, int sprite, int sprite2, int width, int height, String s) {
		RSInterface rsi = interfaceCache[id];
		rsi.setDisabledSprite("attack2", sprite, aClass44);
		rsi.tooltip = s;
		rsi.contentType = 0;
		rsi.atActionType = 1;
		rsi.width = (short) width;
		rsi.mouseOverPopupInterface = -1;
		rsi.parentID = (short) id;
		rsi.id = (short) id;
		rsi.type = 5;
		rsi.height = (short) height;
	}

	public static void addActionButton(int id, int sprite, int sprite2, int width, int height, String s, int hover) {
		RSInterface rsi = interfaceCache[id];
		rsi.setDisabledSprite("attack2", sprite, aClass44);
		if (sprite2 == sprite)
			rsi.setEnabledSprite("attack2", sprite, aClass44);
		else
			rsi.setEnabledSprite("attack2", sprite2, aClass44);
		rsi.tooltip = s;
		rsi.mouseOverPopupInterface = (short) hover;
		rsi.contentType = 0;
		rsi.atActionType = 1;
		rsi.width = (short) width;
		rsi.parentID = (short) id;
		rsi.id = (short) id;
		rsi.type = 5;
		rsi.height = (short) height;
	}

	public static void addToggleButton(int id, int sprite, int sprite2, int setconfig, int width, int height, String s) {
		RSInterface rsi = addInterface(id);
		rsi.setDisabledSprite("attack1", sprite, aClass44);
		rsi.setEnabledSprite("attack1", sprite2, aClass44);
		rsi.requiredValues = new int[1];
		rsi.requiredValues[0] = 1;
		rsi.valueCompareType = new int[1];
		rsi.valueCompareType[0] = 1;
		rsi.valueIndexArray = new int[1][3];
		rsi.valueIndexArray[0][0] = 5;
		rsi.valueIndexArray[0][1] = setconfig;
		rsi.valueIndexArray[0][2] = 0;
		rsi.atActionType = 4;
		rsi.width = (short) width;
		rsi.mouseOverPopupInterface = -1;
		rsi.parentID = (short) id;
		rsi.id = (short) id;
		rsi.type = 5;
		rsi.height = (short) height;
		rsi.tooltip = s;
	}

	public static void pcDeadPortal(int id, int config) {
		RSInterface rsi = addInterface(id);
		rsi.setEnabledSprite("pestcontrol", 6, aClass44);
		rsi.requiredValues = new int[1];
		rsi.requiredValues[0] = 1;
		rsi.valueCompareType = new int[1];
		rsi.valueCompareType[0] = 1;
		rsi.valueIndexArray = new int[1][3];
		rsi.valueIndexArray[0][0] = 5;
		rsi.valueIndexArray[0][1] = config;
		rsi.valueIndexArray[0][2] = 0;
		rsi.mouseOverPopupInterface = -1;
		rsi.parentID = (short) id;
		rsi.id = (short) id;
		rsi.type = 5;
	}

	public static void addToggleText(int id, String text, TextDrawingArea tda[], int idx, int color, boolean centered, int setConfig,
			int width, int popup) {
		RSInterface rsi = interfaceCache[id];
		if (centered)
			rsi.textCentered = true;
		rsi.requiredValues = new int[1];
		rsi.requiredValues[0] = 1;
		rsi.valueCompareType = new int[1];
		rsi.valueCompareType[0] = 1;
		rsi.valueIndexArray = new int[1][3];
		rsi.valueIndexArray[0][0] = 5;
		rsi.valueIndexArray[0][1] = setConfig;
		rsi.valueIndexArray[0][2] = 0;
		rsi.textShadow = true;
		rsi.contentType = 0;
		rsi.isMouseoverTriggered = false;
		rsi.mouseOverPopupInterface = (short) popup;
		rsi.atActionType = 4;
		rsi.fontId = idx;
		if (tda != null) {
			rsi.fonts = tda;
			rsi.font = tda[idx];
		}
		rsi.tooltip = "Toggle";
		rsi.disabledMessage = text;
		rsi.enabledTextColor = color;
		rsi.enabledMessage = text;
		rsi.disabledTextColor = color;
		rsi.height = 14;
		rsi.width = (short) width;
		rsi.id = (short) id;
		rsi.type = 4;
	}

	public static void addToggleButton(int id, int sprite, int sprite2, int setconfig, int width, int height, String s, int hover) {
		RSInterface rsi = addInterface(id);
		rsi.setDisabledSprite("attack1", sprite, aClass44);
		rsi.setEnabledSprite("attack1", sprite2, aClass44);
		rsi.requiredValues = new int[1];
		rsi.requiredValues[0] = 1;
		rsi.valueCompareType = new int[1];
		rsi.valueCompareType[0] = 1;
		rsi.valueIndexArray = new int[1][3];
		rsi.valueIndexArray[0][0] = 5;
		rsi.valueIndexArray[0][1] = setconfig;
		rsi.valueIndexArray[0][2] = 0;
		rsi.atActionType = 4;
		rsi.mouseOverPopupInterface = (short) hover;
		rsi.width = (short) width;
		rsi.parentID = (short) id;
		rsi.id = (short) id;
		rsi.type = 5;
		rsi.height = (short) height;
		rsi.tooltip = s;
	}

	public static void addText(int i, String s, int k, boolean l, boolean m, int a, int j, TextDrawingArea[] TDA) {
		RSInterface rsinterface = addInterface(i);
		rsinterface.parentID = (short) i;
		rsinterface.id = (short) i;
		rsinterface.type = 4;
		rsinterface.atActionType = 0;
		rsinterface.width = 0;
		rsinterface.height = 0;
		rsinterface.contentType = 0;
		rsinterface.alpha = 0;
		rsinterface.mouseOverPopupInterface = (short) a;
		rsinterface.textCentered = l;
		rsinterface.textShadow = m;
		rsinterface.fontId = j;
		if (TDA != null) {
			rsinterface.fonts = TDA;
			rsinterface.font = TDA[j];
		}
		rsinterface.disabledMessage = s;
		rsinterface.disabledTextColor = k;
	}

	private static void setSize(int id, int width, int height) {
		RSInterface rsi = interfaceCache[id];
		rsi.width = (short) width;
		rsi.height = (short) height;
	}

	private static void moveChildren(int mainID, int child, int x, int y) {
		RSInterface rsi = interfaceCache[mainID];
		rsi.childX[child] = (short) x;
		rsi.childY[child] = (short) y;
	}

	/**
	 * Append a child into specific index (between for example)
	 * 
	 * @param frame
	 *            the index where you want place to
	 * @param childId
	 *            child id
	 * @param childX
	 *            child x
	 * @param childY
	 *            child y
	 */
	private void appendChild(int frame, int childId, int childX, int childY) {
		short[] newChildren = new short[this.children.length + 1], newXs = new short[this.childX.length + 1], newYs = new short[this.childY.length + 1];

		for (int k = 0; k < frame; k++) {
			newChildren[k] = this.children[k];
			newXs[k] = this.childX[k];
			newYs[k] = this.childY[k];
		}

		newChildren[frame] = (short) childId;
		newXs[frame] = (short) childX;
		newYs[frame] = (short) childY;

		for (int k = frame + 1; k < this.children.length; k++) {
			newChildren[k] = this.children[k];
			newXs[k] = this.childX[k];
			newYs[k] = this.childY[k];
		}

		this.children = newChildren;
		this.childX = newXs;
		this.childY = newYs;
	}

	private void removeChild(int frame) {
		short[] newChildren = new short[this.children.length - 1], newXs = new short[this.childX.length - 1], newYs = new short[this.childY.length - 1];

		if (frame == 0) {

			for (int k = 0; k < this.children.length - 1; k++) {
				newChildren[k] = this.children[k + 1];
				newXs[k] = this.childX[k + 1];
				newYs[k] = this.childY[k + 1];
			}

		} else {

			for (int k = 0; k < frame; k++) {
				newChildren[k] = this.children[k];
				newXs[k] = this.childX[k];
				newYs[k] = this.childY[k];
			}

			for (int k = frame; k < this.children.length - 1; k++) {
				newChildren[k] = this.children[k + 1];
				newXs[k] = this.childX[k + 1];
				newYs[k] = this.childY[k + 1];
			}

		}

		this.children = newChildren;
		this.childX = newXs;
		this.childY = newYs;
	}

	private void appendChildTop(int frame, int childId, int childX, int childY, RSInterface rsi) {
		short[] newChildren = new short[rsi.children.length + 1], newXs = new short[rsi.childX.length + 1], newYs = new short[rsi.childY.length + 1];
		short[] newChild = { (short) childId }, newX = { (short) childX }, newY = { (short) childY };
		System.arraycopy(newChild, 0, newChildren, frame, newChild.length);
		System.arraycopy(rsi.children, 0, newChildren, 1, rsi.children.length);
		System.arraycopy(newX, 0, newXs, frame, newX.length);
		System.arraycopy(rsi.childX, 0, newXs, 1, rsi.childX.length);
		System.arraycopy(newY, 0, newYs, frame, newY.length);
		System.arraycopy(rsi.childY, 0, newYs, 1, rsi.childY.length);
		rsi.children = newChildren;
		rsi.childX = newXs;
		rsi.childY = newYs;
	}
	
	public static void cornerText(TextDrawingArea[] tda) {
        RSInterface tab = addScreenInterface(16128);
        addText(16129, "Elixare", tda, 1, 0x269CD4, true, true);
        tab.totalChildren(1);
        tab.child(0, 16129, 470, 317);
    }
	
	public static void magicBook(TextDrawingArea[] tda) {
		RSInterface tab = addTabInterface(17000);
		//Spell Usable On: 16 - inv, 4 - use on object, 1 - use on item, 2 - use on npc, 8 - use on player
		/**
		 * First row
		 */
		addMagicButton(17001, 0, 0, 1, "XP Lock", "Lock combat experience", new int[] {557, 554, 564}, new int[] {20, 20, 1}, 0, 5);				
		addMagicButton(17003, 1, 16, 25, "Enchant", "Enchant various gems", new int[] {557, 554, 564}, new int[] {20, 20, 1}, 0, 2);
		addMagicButton(17005, 2, 16, 30, "Super Heat", "Smelt without a furnace", new int[] {554, 561, 557}, new int[] {5, 5, 2}, 0, 2);
		addMagicButton(17007, 3, 16, 50, "High Alch", "Convert items to gold", new int[] {554, 561}, new int[] {10, 2}, 0, 2);
		/**
		 * Second row
		 */
		addMagicButton(17009, 4, 0, 70, "Flaming Arrows", "Enchant your arrows", new int[] {554, 561, 566}, new int[] {5, 2, 2}, 15, 5);
		addMagicButton(17011, 5, 0, 70, "Steroids", "Let's get swol", new int[] {555, 561, 566}, new int[] {5, 2, 2}, 15, 5);
		addMagicButton(17013, 6, 0, 50, "Cure", "Removes all debufs", new int[] {566, 561}, new int[] {5, 10}, 10, 5);
		addMagicButton(17015, 7, 0, 80, "Heal", "Regenerate some health", new int[] {566, 565}, new int[] {15, 15}, 25, 5);
		/**
		 * Third row
		 */
		addMagicButton(17017, 8, 0, 90, "Multi Flaming Arrows", "Enchant your group's arrows", new int[] {554, 561, 566}, new int[] {15, 6, 6}, 30, 5);
		addMagicButton(17019, 9, 0, 90, "Multi Steroids", "Sausage fest", new int[] {555, 561, 566}, new int[] {15, 6, 6}, 30, 5);
		addMagicButton(17021, 10, 0, 70, "Multi Cure", "Cure your group", new int[] {566, 561}, new int[] {10, 20}, 20, 5);
		addMagicButton(17023, 11, 0, 95, "Multi Heal", "Heal your group", new int[] {566, 561}, new int[] {30, 30}, 50, 5);
		/**
		 * Fourth row
		 */
		addMagicButton(17025, 12, 2, 1, "Ember", "Light the ground on fire", new int[] {554, 556}, new int[] {3, 1}, 0, 2);
		addMagicButton(17027, 13, 2, 27, "Shadow Burst", "Unleash the shadows", new int[] {562, 560, 556}, new int[] {4, 2, 1}, 0, 2);
		addMagicButton(17029, 14, 2, 35, "Blood Burst", "Don't get any on your clothes", new int[] {562, 560, 565}, new int[] {4, 2, 2}, 0, 2);
		addMagicButton(17031, 15, 2, 52, "Ice Blitz", "Chill your opponent", new int[] {562, 560, 555}, new int[] {4, 2, 4}, 0, 2);
		/**
		 * Fifth row
		 */
		addMagicButton(17033, 16, 2, 64, "Conflagration", "Set your enemy ablaze", new int[] {554, 556, 560}, new int[] {6, 3, 1}, 0, 2);
		addMagicButton(17035, 17, 2, 78, "Shadow Barrage", "Engulf your enemy in shadows", new int[] {562, 560, 556}, new int[] {8, 4, 2}, 0, 2);
		addMagicButton(17037, 18, 2, 84, "Blood Barrage", "A gruesome sight", new int[] {562, 560, 565}, new int[] {8, 4, 4}, 0, 2);
		addMagicButton(17039, 19, 2, 96, "Ice Barrage", "Warning: May cause shrinkage", new int[] {562, 560, 555}, new int[] {8, 4, 8}, 0, 2);
		/**
		 * Sixth row
		 */
		addMagicButton(17041, 20, 0, 10, "Falador Teleport", "There's no place like home", new int[] {556, 554, 560}, new int[] { 10, 20, 30}, 0, 5);
		addMagicButton(17043, 21, 0, 10, "Fishing Guild Teleport", "Relax and fish", new int[] {556, 554, 560}, new int[] { 10, 20, 30}, 0, 5);
		addMagicButton(17045, 22, 0, 10, "Draynor Teleport", "Home of the lumberjacks", new int[] {556, 554, 560}, new int[] { 10, 20, 30}, 0, 5);
		addMagicButton(17047, 23, 0, 10, "Shilo Teleport", "A rather mysterious place", new int[] {556, 554, 560}, new int[] { 10, 20, 30}, 0, 5);
		/**
		 * Seventh row
		 */
		addMagicButton(17049, 24, 0, 10, "Edgeville Teleport", "Pwn some n00bs", new int[] {556, 554, 560}, new int[] { 10, 20, 30}, 0, 5);
		addMagicButton(17051, 25, 0, 10, "Varrock Teleport", "The city of royalty", new int[] {556, 554, 560}, new int[] { 10, 20, 30}, 0, 5);
		addMagicButton(17053, 26, 0, 10, "Azerith Teleport", "Slay some monsters", new int[] {556, 554, 560}, new int[] { 10, 20, 30}, 0, 5);
		addMagicButton(17055, 27, 0, 10, "Minigame Teleport", "Play a minigame", new int[] {556, 554, 560}, new int[] { 10, 20, 30}, 0, 5);
		
		tab.totalChildren(28);
		tab.child(0, 17001, 20, 15);
		tab.child(1, 17003, 65, 15);
		tab.child(2, 17005, 110, 15);
		tab.child(3, 17007, 155, 15);
		
		tab.child(4, 17009, 20, 45);
		tab.child(5, 17011, 65, 45);
		tab.child(6, 17013, 110, 45);
		tab.child(7, 17015, 155, 45);
		
		tab.child(8, 17017, 20, 75);
		tab.child(9, 17019, 65, 75);
		tab.child(10, 17021, 110, 75);
		tab.child(11, 17023, 155, 75);
		
		tab.child(12, 17025, 20, 105);
		tab.child(13, 17027, 65, 105);
		tab.child(14, 17029, 110, 105);
		tab.child(15, 17031, 155, 105);
		
		tab.child(16, 17033, 20, 135);
		tab.child(17, 17035, 65, 135);
		tab.child(18, 17037, 110, 135);
		tab.child(19, 17039, 155, 135);
		
		tab.child(20, 17041, 20, 165);
		tab.child(21, 17043, 65, 165);
		tab.child(22, 17045, 110, 165);
		tab.child(23, 17047, 155, 165);
		
		tab.child(24, 17049, 20, 195);
		tab.child(25, 17051, 65, 195);
		tab.child(26, 17053, 110, 195);
		tab.child(27, 17055, 155, 195);
	}
	
	public static void hardcodedInterfaces(TextDrawingArea[] TDA) {
		/**
		 * Button X BIG = 89 Button X Small = 2811 Button QUEST X SMALL = 1727
		 * Button QUEST X BIG YELLOW = 5384 Button QUEST X BIG ORANGE = 1827
		 * General blank big interface layout to use ;) 148
		 */

		RSInterface ff1 = interfaceCache[9108];
		ff1.appendChild(27, 1827, 474, 14);

		RSInterface ff2 = interfaceCache[9196];
		ff2.appendChild(49, 1827, 474, 14);

		RSInterface ff3 = interfaceCache[9275];
		ff3.appendChild(82, 1827, 474, 14);

		RSInterface ff4 = interfaceCache[9359];
		ff4.appendChild(93, 1827, 474, 14);

		RSInterface ff5 = interfaceCache[9454];
		ff5.appendChild(51, 1827, 474, 14);

		RSInterface ff6 = interfaceCache[9507];
		ff6.appendChild(123, 1827, 474, 14);

		RSInterface ff7 = interfaceCache[9632];
		ff7.appendChild(86, 1827, 474, 14);

		RSInterface ff8 = interfaceCache[9720];
		ff8.appendChild(117, 1827, 474, 14);

		RSInterface ff9 = interfaceCache[9839];
		ff9.appendChild(84, 1827, 474, 14);

		RSInterface ff10 = interfaceCache[11462];
		ff10.appendChild(39, 1827, 457, 16);

		ff10.removeChild(0);

		/**
		 * TODO: General blank big interface layout to use ;) 148
		 */
		RSInterface ff11 = addInterface(148);
		ff11.totalChildren(89);
		for (int kk = 0; kk < 88; kk++) {
			ff11.children[kk] = interfaceCache[205].children[kk];
			ff11.childX[kk] = interfaceCache[205].childX[kk];
			ff11.childY[kk] = interfaceCache[205].childY[kk];
		}
		ff11.child(88, 89, 472, 27);

		RSInterface ff12 = interfaceCache[4465];
		ff12.child(61, 89, 418, 27);

		RSInterface ff13 = interfaceCache[4909];
		ff13.appendChild(8, 1727, 393, 89);

		RSInterface ff14 = interfaceCache[6960];
		ff14.appendChild(3, 1827, 463, 19);

		textCentered(8144, true);
		textSize(8144, TDA, 3);
		textMessage(8144, "Test Quest #8144");

		RSInterface ff17 = interfaceCache[8134];
		ff17.totalChildren(4);
		ff17.child(0, 8135, 10, 34);
		ff17.child(1, 8143, 50, 85);
		ff17.child(2, 8144, 219, 59);
		ff17.child(3, 1727, 443, 57);

		RSInterface ff16 = addInterface(8135);// 149);
		ff16.type = 6;
		ff16.mediaType = 1;
		ff16.modelRotY = 500;
		ff16.mediaID = 21821;// 20852;
		ff16.modelZoom = 580;

		RSInterface ff20 = interfaceCache[8143];
		ff20.width = 403;
		ff20.height = 215;

		for (int kk = 0; kk < interfaceCache[8143].children.length; kk++) {
			interfaceCache[interfaceCache[8143].children[kk]].width += 62;
			// TODO: add le id to disabled message
			interfaceCache[interfaceCache[8143].children[kk]].disabledMessage = "CHILD_ID:" + interfaceCache[8143].children[kk];
		}

		RSInterface magic = interfaceCache[12424];
		magic.child(5, 1154, 98, 1);
		magic.child(7, 1156, 146, 1);
		magic.child(9, 1158, 26, 25);
		magic.child(39, 1188, 73, 169);
		magic.child(40, 1189, 146, 169);

		RSInterface mta = interfaceCache[15944];
		mta.child(4, 1827, 466, 20);

		RSInterface inv02 = interfaceCache[13824];
		inv02.actions = null;

		System.err.println(posx + " " + posy);
	}

	public static void addAirAmount(int id, TextDrawingArea font[], int runeAmount) {
		RSInterface rsInterface = addInterface(id);
		rsInterface.type = 4;
		rsInterface.valueCompareType = interfaceCache[1222].valueCompareType;
		rsInterface.valueIndexArray = interfaceCache[1222].valueIndexArray;
		rsInterface.textCentered = true;
		rsInterface.height = 14;
		rsInterface.width = 0;
		rsInterface.fontId = 0;
		if (font != null) {
			rsInterface.fonts = font;
			rsInterface.font = font[0];
		}
		rsInterface.textShadow = true;
		rsInterface.enabledMessage = "";
		rsInterface.disabledTextColor = 12582912;
		rsInterface.enabledTextColor = 49152;
		rsInterface.requiredValues = new int[1];
		rsInterface.requiredValues[0] = interfaceCache[1222].requiredValues[0];
		rsInterface.requiredValues[0] = runeAmount - 1;
		rsInterface.disabledMessage = "%1/" + runeAmount + "";
	}

	public static void addWaterAmount(int id, TextDrawingArea font[], int runeAmount) {
		RSInterface rsInterface = addInterface(id);
		rsInterface.type = 4;
		rsInterface.valueCompareType = interfaceCache[1355].valueCompareType;
		rsInterface.valueIndexArray = interfaceCache[1355].valueIndexArray;
		rsInterface.textCentered = true;
		rsInterface.height = 14;
		rsInterface.width = 0;
		rsInterface.fontId = 0;
		if (font != null) {
			rsInterface.fonts = font;
			rsInterface.font = font[0];
		}
		rsInterface.textShadow = true;
		rsInterface.enabledMessage = "";
		rsInterface.disabledTextColor = 12582912;
		rsInterface.enabledTextColor = 49152;
		rsInterface.requiredValues = new int[1];
		rsInterface.requiredValues[0] = interfaceCache[1355].requiredValues[0];
		rsInterface.requiredValues[0] = runeAmount - 1;
		rsInterface.disabledMessage = "%1/" + runeAmount + "";
	}

	public static void addEarthAmount(int id, TextDrawingArea font[], int runeAmount) {
		RSInterface rsInterface = addInterface(id);
		rsInterface.type = 4;
		rsInterface.valueCompareType = interfaceCache[1213].valueCompareType;
		rsInterface.valueIndexArray = interfaceCache[1213].valueIndexArray;
		rsInterface.textCentered = true;
		rsInterface.height = 14;
		rsInterface.width = 0;
		rsInterface.fontId = 0;
		if (font != null) {
			rsInterface.fonts = font;
			rsInterface.font = font[0];
		}
		rsInterface.textShadow = true;
		rsInterface.enabledMessage = "";
		rsInterface.disabledTextColor = 12582912;
		rsInterface.enabledTextColor = 49152;
		rsInterface.requiredValues = new int[1];
		rsInterface.requiredValues[0] = interfaceCache[1213].requiredValues[0];
		rsInterface.requiredValues[0] = runeAmount - 1;
		rsInterface.disabledMessage = "%1/" + runeAmount + "";
	}

	public static void addFireAmount(int id, TextDrawingArea font[], int runeAmount) {
		RSInterface rsInterface = addInterface(id);
		rsInterface.type = 4;
		rsInterface.valueCompareType = interfaceCache[1255].valueCompareType;
		rsInterface.valueIndexArray = interfaceCache[1255].valueIndexArray;
		rsInterface.textCentered = true;
		rsInterface.height = 14;
		rsInterface.width = 0;
		rsInterface.fontId = 0;
		if (font != null) {
			rsInterface.fonts = font;
			rsInterface.font = font[0];
		}
		rsInterface.textShadow = true;
		rsInterface.enabledMessage = "";
		rsInterface.disabledTextColor = 12582912;
		rsInterface.enabledTextColor = 49152;
		rsInterface.requiredValues = new int[1];
		rsInterface.requiredValues[0] = interfaceCache[1255].requiredValues[0];
		rsInterface.requiredValues[0] = runeAmount - 1;
		rsInterface.disabledMessage = "%1/" + runeAmount + "";
	}

	public static void addBodyAmount(int id, TextDrawingArea font[], int runeAmount) {
		RSInterface rsInterface = addInterface(id);
		rsInterface.type = 4;
		rsInterface.valueCompareType = interfaceCache[1214].valueCompareType;
		rsInterface.valueIndexArray = interfaceCache[1214].valueIndexArray;
		rsInterface.textCentered = true;
		rsInterface.height = 14;
		rsInterface.width = 0;
		rsInterface.fontId = 0;
		if (font != null) {
			rsInterface.fonts = font;
			rsInterface.font = font[0];
		}
		rsInterface.textShadow = true;
		rsInterface.enabledMessage = "";
		rsInterface.disabledTextColor = 12582912;
		rsInterface.enabledTextColor = 49152;
		rsInterface.requiredValues = new int[1];
		rsInterface.requiredValues[0] = interfaceCache[1214].requiredValues[0];
		rsInterface.requiredValues[0] = runeAmount - 1;
		rsInterface.disabledMessage = "%1/" + runeAmount + "";
	}

	public static void addNatureAmount(int id, TextDrawingArea font[], int runeAmount) {
		RSInterface rsInterface = addInterface(id);
		rsInterface.type = 4;
		rsInterface.valueCompareType = interfaceCache[1581].valueCompareType;
		rsInterface.valueIndexArray = interfaceCache[1581].valueIndexArray;
		rsInterface.textCentered = true;
		rsInterface.height = 14;
		rsInterface.width = 0;
		rsInterface.fontId = 0;
		if (font != null) {
			rsInterface.fonts = font;
			rsInterface.font = font[0];
		}
		rsInterface.textShadow = true;
		rsInterface.enabledMessage = "";
		rsInterface.disabledTextColor = 12582912;
		rsInterface.enabledTextColor = 49152;
		rsInterface.requiredValues = new int[1];
		rsInterface.requiredValues[0] = interfaceCache[1581].requiredValues[0];
		rsInterface.requiredValues[0] = runeAmount - 1;
		rsInterface.disabledMessage = "%1/" + runeAmount + "";
	}

	public static void addCosmicAmount(int id, TextDrawingArea font[], int runeAmount) {
		RSInterface rsInterface = addInterface(id);
		rsInterface.type = 4;
		rsInterface.valueCompareType = interfaceCache[1230].valueCompareType;
		rsInterface.valueIndexArray = interfaceCache[1230].valueIndexArray;
		rsInterface.textCentered = true;
		rsInterface.height = 14;
		rsInterface.width = 0;
		rsInterface.fontId = 0;
		if (font != null) {
			rsInterface.fonts = font;
			rsInterface.font = font[0];
		}
		rsInterface.textShadow = true;
		rsInterface.enabledMessage = "";
		rsInterface.disabledTextColor = 12582912;
		rsInterface.enabledTextColor = 49152;
		rsInterface.requiredValues = new int[1];
		rsInterface.requiredValues[0] = interfaceCache[1230].requiredValues[0];
		rsInterface.requiredValues[0] = runeAmount - 1;
		rsInterface.disabledMessage = "%1/" + runeAmount + "";
	}

	public static void addBloodAmount(int id, TextDrawingArea font[], int runeAmount) {
		RSInterface rsInterface = addInterface(id);
		rsInterface.type = 4;
		rsInterface.valueCompareType = interfaceCache[1609].valueCompareType;
		rsInterface.valueIndexArray = interfaceCache[1609].valueIndexArray;
		rsInterface.textCentered = true;
		rsInterface.height = 14;
		rsInterface.width = 0;
		rsInterface.fontId = 0;
		if (font != null) {
			rsInterface.fonts = font;
			rsInterface.font = font[0];
		}
		rsInterface.textShadow = true;
		rsInterface.enabledMessage = "";
		rsInterface.disabledTextColor = 12582912;
		rsInterface.enabledTextColor = 49152;
		rsInterface.requiredValues = new int[1];
		rsInterface.requiredValues[0] = interfaceCache[1609].requiredValues[0];
		rsInterface.requiredValues[0] = runeAmount - 1;
		rsInterface.disabledMessage = "%1/" + runeAmount + "";
	}

	public static void addDeathAmount(int id, TextDrawingArea font[], int runeAmount) {
		RSInterface rsInterface = addInterface(id);
		rsInterface.type = 4;
		rsInterface.valueCompareType = interfaceCache[1396].valueCompareType;
		rsInterface.valueIndexArray = interfaceCache[1396].valueIndexArray;
		rsInterface.textCentered = true;
		rsInterface.height = 14;
		rsInterface.width = 0;
		rsInterface.fontId = 0;
		if (font != null) {
			rsInterface.fonts = font;
			rsInterface.font = font[0];
		}
		rsInterface.textShadow = true;
		rsInterface.enabledMessage = "";
		rsInterface.disabledTextColor = 12582912;
		rsInterface.enabledTextColor = 49152;
		rsInterface.requiredValues = new int[1];
		rsInterface.requiredValues[0] = interfaceCache[1396].requiredValues[0];
		rsInterface.requiredValues[0] = runeAmount - 1;
		rsInterface.disabledMessage = "%1/" + runeAmount + "";
	}

	public static void addSoulAmount(int id, TextDrawingArea font[], int runeAmount) {
		RSInterface rsInterface = addInterface(id);
		rsInterface.type = 4;
		rsInterface.valueCompareType = interfaceCache[12442].valueCompareType;
		rsInterface.valueIndexArray = interfaceCache[12442].valueIndexArray;
		rsInterface.textCentered = true;
		rsInterface.height = 14;
		rsInterface.width = 0;
		rsInterface.fontId = 0;
		if (font != null) {
			rsInterface.fonts = font;
			rsInterface.font = font[0];
		}
		rsInterface.textShadow = true;
		rsInterface.enabledMessage = "";
		rsInterface.disabledTextColor = 12582912;
		rsInterface.enabledTextColor = 49152;
		rsInterface.requiredValues = new int[1];
		rsInterface.requiredValues[0] = interfaceCache[12442].requiredValues[0];
		rsInterface.requiredValues[0] = runeAmount - 1;
		rsInterface.disabledMessage = "%1/" + runeAmount + "";
	}

	public static void addChaosAmount(int id, TextDrawingArea font[], int runeAmount) {
		RSInterface rsInterface = addInterface(id);
		rsInterface.type = 4;
		rsInterface.valueCompareType = interfaceCache[1348].valueCompareType;
		rsInterface.valueIndexArray = interfaceCache[1348].valueIndexArray;
		rsInterface.textCentered = true;
		rsInterface.height = 14;
		rsInterface.width = 0;
		rsInterface.fontId = 0;
		if (font != null) {
			rsInterface.fonts = font;
			rsInterface.font = font[0];
		}
		rsInterface.textShadow = true;
		rsInterface.enabledMessage = "";
		rsInterface.disabledTextColor = 12582912;
		rsInterface.enabledTextColor = 49152;
		rsInterface.requiredValues = new int[1];
		rsInterface.requiredValues[0] = interfaceCache[1348].requiredValues[0];
		rsInterface.requiredValues[0] = runeAmount - 1;
		rsInterface.disabledMessage = "%1/" + runeAmount + "";
	}

	public static void addMindAmount(int id, TextDrawingArea font[], int runeAmount) {
		RSInterface rsInterface = addInterface(id);
		rsInterface.type = 4;
		rsInterface.valueCompareType = interfaceCache[1223].valueCompareType;
		rsInterface.valueIndexArray = interfaceCache[1223].valueIndexArray;
		rsInterface.textCentered = true;
		rsInterface.height = 14;
		rsInterface.width = 0;
		rsInterface.fontId = 0;
		if (font != null) {
			rsInterface.fonts = font;
			rsInterface.font = font[0];
		}
		rsInterface.textShadow = true;
		rsInterface.enabledMessage = "";
		rsInterface.disabledTextColor = 12582912;
		rsInterface.enabledTextColor = 49152;
		rsInterface.requiredValues = new int[1];
		rsInterface.requiredValues[0] = interfaceCache[1223].requiredValues[0];
		rsInterface.requiredValues[0] = runeAmount - 1;
		rsInterface.disabledMessage = "%1/" + runeAmount + "";
	}

	public static void addLawAmount(int id, TextDrawingArea font[], int runeAmount) {
		RSInterface rsInterface = addInterface(id);
		rsInterface.type = 4;
		rsInterface.valueCompareType = interfaceCache[1357].valueCompareType;
		rsInterface.valueIndexArray = interfaceCache[1357].valueIndexArray;
		rsInterface.textCentered = true;
		rsInterface.height = 14;
		rsInterface.width = 0;
		rsInterface.fontId = 0;
		if (font != null) {
			rsInterface.fonts = font;
			rsInterface.font = font[0];
		}
		rsInterface.textShadow = true;
		rsInterface.enabledMessage = "";
		rsInterface.disabledTextColor = 12582912;
		rsInterface.enabledTextColor = 49152;
		rsInterface.requiredValues = new int[1];
		rsInterface.requiredValues[0] = interfaceCache[1357].requiredValues[0];
		rsInterface.requiredValues[0] = runeAmount - 1;
		rsInterface.disabledMessage = "%1/" + runeAmount + "";
	}

	/**
	 * #TODO Adding rune req+staves the correct way!
	 * 
	 * RSInterface rsInterface = addInterface(22733); rsInterface.type = 4;
	 * rsInterface.width = 0; rsInterface.height = 12;
	 * 
	 * rsInterface.valueCompareType = new int[1]; rsInterface.requiredValues =
	 * new int[1];
	 * 
	 * rsInterface.valueCompareType[0] = 3; rsInterface.requiredValues[0] = 5;
	 * // 6 earths #TODO
	 * 
	 * rsInterface.valueIndexArray = new int[1][34];
	 * 
	 * rsInterface.valueIndexArray[0][0] = 4; rsInterface.valueIndexArray[0][1]
	 * = 3214; rsInterface.valueIndexArray[0][2] = 557;
	 * rsInterface.valueIndexArray[0][3] = 4; rsInterface.valueIndexArray[0][4]
	 * = 3214; rsInterface.valueIndexArray[0][5] = 4696;
	 * rsInterface.valueIndexArray[0][6] = 4; rsInterface.valueIndexArray[0][7]
	 * = 3214; rsInterface.valueIndexArray[0][8] = 4699;
	 * rsInterface.valueIndexArray[0][9] = 4; rsInterface.valueIndexArray[0][10]
	 * = 3214; rsInterface.valueIndexArray[0][11] = 4698;
	 * rsInterface.valueIndexArray[0][12] = 10;
	 * rsInterface.valueIndexArray[0][13] = 1688;
	 * rsInterface.valueIndexArray[0][14] = 1385;
	 * rsInterface.valueIndexArray[0][15] = 10;
	 * rsInterface.valueIndexArray[0][16] = 1688;
	 * rsInterface.valueIndexArray[0][17] = 1399;
	 * rsInterface.valueIndexArray[0][18] = 10;
	 * rsInterface.valueIndexArray[0][19] = 1688;
	 * rsInterface.valueIndexArray[0][20] = 1407;
	 * rsInterface.valueIndexArray[0][21] = 10;
	 * rsInterface.valueIndexArray[0][22] = 1688;
	 * rsInterface.valueIndexArray[0][23] = 3053;
	 * rsInterface.valueIndexArray[0][24] = 10;
	 * rsInterface.valueIndexArray[0][25] = 1688;
	 * rsInterface.valueIndexArray[0][26] = 3054;
	 * rsInterface.valueIndexArray[0][27] = 10;
	 * rsInterface.valueIndexArray[0][28] = 1688;
	 * rsInterface.valueIndexArray[0][29] = 6562;
	 * rsInterface.valueIndexArray[0][30] = 10;
	 * rsInterface.valueIndexArray[0][31] = 1688;
	 * rsInterface.valueIndexArray[0][32] = 6563;
	 * rsInterface.valueIndexArray[0][33] = 0;
	 * 
	 * rsInterface.textCentered = true; rsInterface.fontId = 0; if (TDA != null)
	 * { rsInterface.fonts = TDA; rsInterface.font = TDA[0]; }
	 * rsInterface.textShadow = true; rsInterface.disabledMessage = "%1/" + 6 +
	 * ""; rsInterface.enabledMessage = ""; rsInterface.disabledTextColor =
	 * 12582912; rsInterface.enabledTextColor = 49152;
	 */

	public static void add2RunesBlackBox(int originalBoxId, int upperText, int lowerText, int firstModel, int secondModel, int amount1,
			int amount2, String spellName) {
		RSInterface original = interfaceCache[originalBoxId];
		int blackBackgroundID = 18588;
		int outerFrameID = 10255;
		int innerFrameID = 16158;
		// Lvl 1: Wind strike color = 0xFFA500
		// A basic Air missile = 0xAF6A1A
		// TODO: magic
		textColor(upperText, 0xFFA500);
		textColor(lowerText, 0xAF6A1A);
		textShadowed(upperText, false);
		textShadowed(lowerText, false);
		textMessage(upperText, spellName);
		original.child(0, upperText, 3, 2);
		original.child(1, lowerText, 3, 18);
		original.child(2, firstModel, 37, 34);
		original.child(3, secondModel, 112, 34);
		original.child(4, amount1, 41, 65);
		original.child(5, amount2, 116, 65);
		// 0x2E2B23 for inner rect
		RSInterface gh2 = addInterface(innerFrameID);
		gh2.type = 3;
		gh2.disabledTextColor = 0x2E2B23;
		// set inner rect size
		setSize(innerFrameID, 179, 79);
		// 0x726451 for outer rect
		RSInterface ghi1 = addInterface(outerFrameID);
		ghi1.type = 3;
		ghi1.disabledTextColor = 0x726451;
		// set outer frame size
		setSize(outerFrameID, 179, 79);
		RSInterface ghi = addInterface(blackBackgroundID);
		ghi.type = 3;
		ghi.alpha = 40;
		ghi.filled = true;
		// set black background size
		setSize(blackBackgroundID, 180, 80);
		// set Original Size
		setSize(originalBoxId, 180, 80);
		original.appendChildTop(0, outerFrameID, 0, 0, original);
		original.appendChildTop(0, innerFrameID, 1, 1, original);
		original.appendChildTop(0, blackBackgroundID, 0, 0, original);
	}

	public static void drawLunarRuneAmount(int id, TextDrawingArea font[], int runeAmount) {
		RSInterface rsInterface = addInterface(id);
		rsInterface.type = 4;
		rsInterface.width = 0;
		rsInterface.height = 14;
		rsInterface.valueCompareType = new int[1];
		rsInterface.requiredValues = new int[1];
		rsInterface.valueCompareType[0] = 3;
		rsInterface.requiredValues[0] = runeAmount - 1;
		rsInterface.valueIndexArray = new int[1][4];
		rsInterface.valueIndexArray[0][0] = 4;
		rsInterface.valueIndexArray[0][1] = 3214;
		rsInterface.valueIndexArray[0][2] = 9075;
		rsInterface.valueIndexArray[0][3] = 0;
		rsInterface.textCentered = true;
		rsInterface.fontId = 0;
		if (font != null) {
			rsInterface.fonts = font;
			rsInterface.font = font[0];
		}
		rsInterface.textShadow = true;
		rsInterface.disabledMessage = "%1/" + runeAmount + "";
		rsInterface.enabledMessage = "";
		rsInterface.disabledTextColor = 12582912;
		rsInterface.enabledTextColor = 49152;
	}

	static int posx = 0;
	static int posy = 0;

	public static void addInv(int id, int h, int w) {
		RSInterface Tab = interfaceCache[id];
		Tab.inv = new int[w * h];
		Tab.invStackSizes = new int[w * h];
		for (int i1 = 0; i1 < w * h; i1++) {
			Tab.invStackSizes[i1] = 1;
			Tab.inv[i1] = 0;
		}
		Tab.spritesY = new short[20];
		Tab.spritesX = new short[20];
		for (int i2 = 0; i2 < 20; i2++) {
			Tab.spritesY[i2] = 0;
			Tab.spritesX[i2] = 0;
		}
		Tab.invSpritePadX = 177;
		Tab.invSpritePadY = 14;
		Tab.width = (short) w;
		Tab.mouseOverPopupInterface = -1;
		Tab.parentID = (short) id;
		Tab.id = (short) id;
		Tab.scrollMax = 0;
		Tab.type = 2;
		Tab.height = (short) h;
	}

	public static void pestControl(TextDrawingArea[] TDA) {
		RSInterface pcRewards = addInterface(31095);
		addText(31096, "Void Knights' Reward Options", TDA, 2, 16748608, false);
		addText(31102, "Confirm:", TDA, 1, 0x969696, false);
		addText(31103, "Defence (100p)", TDA, 1, 0x969696, true);
		addText(31104, "150,000 XP", TDA, 1, 0x969696, true);
		addPCButton(31105, 31106);
		addPCHover(31106);
		int slot = 0;
		pcRewards.totalChildren(11);
		pcRewards.child(0, 31085, 0, 0);
		pcRewards.child(1, 31100, 14, 0);
		pcRewards.child(2, 31096, 155, 20);
		pcRewards.child(3, 5952, 434, 17);
		pcRewards.child(4, 31097, 33, 47);
		pcRewards.child(5, 31101, 180, 268);
		pcRewards.child(6, 31105, 186, 274);
		pcRewards.child(7, 31106, 186, 274);
		pcRewards.child(8, 31102, 229, 274);
		pcRewards.child(9, 31103, 253, 289);
		pcRewards.child(10, 31104, 253, 305);
		int pos = 0;
		int drawX = 25;
		RSInterface confirm = addInterface(31101);
		confirm.totalChildren(12);
		confirm.child(pos, 31002, 0, 0);
		pos++;
		confirm.child(pos, 31002, 52, 0);
		pos++;
		confirm.child(pos, 13040, 0, 0);
		pos++;
		confirm.child(pos, 12905, 120, 0);
		pos++;
		confirm.child(pos, 12907, 0, 30);
		pos++;
		confirm.child(pos, 13057, 120, 30);
		pos++;
		for (pos = 6; pos < 9; pos++, drawX += 36) {
			confirm.child(pos, 13041, drawX, -15);
		}
		drawX = 25;
		for (pos = 9; pos < 12; pos++, drawX += 36) {
			confirm.child(pos, 13058, drawX, 39);
		}
		RSInterface shop = addInterface(31100);
		shop.width = 470;
		shop.height = 263;
		shop.totalChildren(93);
		int positionX = 78;
		int positionY = 10;
		for (slot = 0; slot < 6; slot++, positionX += 68) {
			shop.child(slot, 31002, positionX, 10);
		}
		for (slot = 5; slot < 10; slot++, positionY += 60) {
			shop.child(slot, 31002, 10, positionY);
		}
		positionY = 10;
		for (slot = 10; slot < 15; slot++, positionY += 60) {
			shop.child(slot, 31002, 78, positionY);
		}
		positionY = 10;
		for (slot = 15; slot < 20; slot++, positionY += 60) {
			shop.child(slot, 31002, 142, positionY);
		}
		positionY = 10;
		for (slot = 20; slot < 25; slot++, positionY += 60) {
			shop.child(slot, 31002, 210, positionY);
		}
		positionY = 10;
		for (slot = 25; slot < 30; slot++, positionY += 60) {
			shop.child(slot, 31002, 278, positionY);
		}
		positionY = 10;
		for (slot = 30; slot < 35; slot++, positionY += 60) {
			shop.child(slot, 31002, 346, positionY);
		}
		positionY = 10;
		for (slot = 35; slot < 40; slot++, positionY += 60) {
			shop.child(slot, 31002, 414, positionY);
		}
		shop.child(slot, 13040, 10, 10);
		slot++;
		shop.child(slot, 12905, 445, 10);
		slot++;
		shop.child(slot, 12907, 10, 233);
		slot++;
		shop.child(slot, 13057, 445, 233);
		slot++;
		positionX = 35;
		for (slot = 44; slot < 55; slot++, positionX += 36) {
			shop.child(slot, 13041, positionX, -5);
		}
		shop.child(slot, 13041, 409, -5);
		positionY = 40;
		for (slot = 56; slot < 61; slot++, positionY += 36) {
			shop.child(slot, 12906, -5, positionY);
		}
		shop.child(slot, 12906, -5, 208);
		positionX = 35;
		for (slot = 62; slot < 73; slot++, positionX += 36) {
			shop.child(slot, 13058, positionX, 242);
		}
		shop.child(slot, 13058, 421, 242);
		positionY = 40;
		for (slot = 74; slot < 79; slot++, positionY += 36) {
			shop.child(slot, 31021, 449, positionY);
		}
		shop.child(slot, 31021, 449, 204);
		positionX = 16;
		for (slot = 80; slot < 92; slot++, positionX += 36) {
			shop.child(slot, 13041, positionX, 25);
		}
		shop.child(slot, 13041, 428, 25);
		slot++;
		addInv(31099, 5, 2);
		RSInterface scroll = addInterface(31097);
		scroll.scrollMax = 400;
		scroll.width = 428;
		scroll.height = 209;
		int child2 = 0;
		scroll.totalChildren(68);
		for (int k = 18760; k < 18767; k++) {
			RSInterface pcIcon = interfaceCache[k];
			pcIcon.atActionType = 0;
		}
		for (int k = 18767; k < 18774; k++) {
			RSInterface text = interfaceCache[k];
			text.disabledTextColor = 0xFF981F;
			text.atActionType = 0;
		}
		scroll.child(child2, 18760, 0, 0);
		child2++;
		scroll.child(child2, 18767, 45, 2);
		child2++;
		scroll.child(child2, 18761, 210, 0);
		child2++;
		scroll.child(child2, 18768, 255, 2);
		child2++;
		scroll.child(child2, 18762, 0, 35);
		child2++;
		scroll.child(child2, 18769, 45, 37);
		child2++;
		scroll.child(child2, 18763, 210, 35);
		child2++;
		scroll.child(child2, 18770, 255, 37);
		child2++;
		scroll.child(child2, 18764, 0, 70);
		child2++;
		scroll.child(child2, 18771, 45, 74);
		child2++;
		scroll.child(child2, 18765, 210, 70);
		child2++;
		scroll.child(child2, 18772, 255, 74);
		child2++;
		scroll.child(child2, 18766, 0, 105);
		child2++;
		scroll.child(child2, 18773, 45, 109);
		child2++;
		for (int k = 31129; k < 31139; k++)
			addText(k, "ID:" + k, TDA, 1, 0xFF981F, false);
		int pos1 = 0;
		for (int g = 0; g < 11; g++, child2++, pos1 += 36) {
			scroll.child(child2, 13041, pos1, 130);
		}
		scroll.child(child2, 13041, 389, 130);
		child2++;
		scroll.child(child2, 31099, 0, 158);
		child2++;
		int xPos = 255;
		int points = 1;
		for (int k = 31108; k < 31111; k++, child2++, xPos += 35, points *= 10) {
			if (points == 1)
				addTextPC(k, "(" + points + " Pt)", TDA, 0, 0x855013, false);
			else
				addTextPC(k, "(" + points + " Pts)", TDA, 0, 0x855013, false);
			if (k == 31110)
				xPos += 13;
			scroll.child(child2, k, xPos, 17);
		}
		int xPos1 = 45;
		int points1 = 1;
		for (int k1 = 31111; k1 < 31114; k1++, child2++, xPos1 += 35, points1 *= 10) {
			if (points1 == 1)
				addTextPC(k1, "(" + points1 + " Pt)", TDA, 0, 0x855013, false);
			else
				addTextPC(k1, "(" + points1 + " Pts)", TDA, 0, 0x855013, false);
			if (k1 == 31113)
				xPos1 += 13;
			scroll.child(child2, k1, xPos1, 17);
		}
		int xPos2 = 45;
		int points2 = 1;
		for (int k1 = 31114; k1 < 31117; k1++, child2++, xPos2 += 35, points2 *= 10) {
			if (points2 == 1)
				addTextPC(k1, "(" + points2 + " Pt)", TDA, 0, 0x855013, false);
			else
				addTextPC(k1, "(" + points2 + " Pts)", TDA, 0, 0x855013, false);
			if (k1 == 31116)
				xPos2 += 13;
			scroll.child(child2, k1, xPos2, 52);
		}
		int xPos3 = 255;
		int points3 = 1;
		for (int k1 = 31117; k1 < 31120; k1++, child2++, xPos3 += 35, points3 *= 10) {
			if (points3 == 1)
				addTextPC(k1, "(" + points3 + " Pt)", TDA, 0, 0x855013, false);
			else
				addTextPC(k1, "(" + points3 + " Pts)", TDA, 0, 0x855013, false);
			if (k1 == 31119)
				xPos3 += 13;
			scroll.child(child2, k1, xPos3, 52);
		}
		int xPos4 = 45;
		int points4 = 1;
		for (int k1 = 31120; k1 < 31123; k1++, child2++, xPos4 += 35, points4 *= 10) {
			if (points4 == 1)
				addTextPC(k1, "(" + points4 + " Pt)", TDA, 0, 0x855013, false);
			else
				addTextPC(k1, "(" + points4 + " Pts)", TDA, 0, 0x855013, false);
			if (k1 == 31122)
				xPos4 += 13;
			scroll.child(child2, k1, xPos4, 89);
		}
		int xPos5 = 255;
		int points5 = 1;
		for (int k1 = 31123; k1 < 31126; k1++, child2++, xPos5 += 35, points5 *= 10) {
			if (points5 == 1)
				addTextPC(k1, "(" + points5 + " Pt)", TDA, 0, 0x855013, false);
			else
				addTextPC(k1, "(" + points5 + " Pts)", TDA, 0, 0x855013, false);
			if (k1 == 31125)
				xPos5 += 13;
			scroll.child(child2, k1, xPos5, 89);
		}
		int xPos6 = 45;
		int points6 = 1;
		for (int k1 = 31126; k1 < 31129; k1++, child2++, xPos6 += 35, points6 *= 10) {
			if (points6 == 1)
				addTextPC(k1, "(" + points6 + " Pt)", TDA, 0, 0x855013, false);
			else
				addTextPC(k1, "(" + points6 + " Pts)", TDA, 0, 0x855013, false);
			if (k1 == 31128)
				xPos6 += 13;
			scroll.child(child2, k1, xPos6, 123);
		}
		scroll.child(child2, 31129, 45, 161);
		child2++;
		scroll.child(child2, 31130, 255, 161);
		child2++;
		scroll.child(child2, 31131, 45, 207);
		child2++;
		scroll.child(child2, 31132, 255, 207);
		child2++;
		scroll.child(child2, 31133, 45, 253);
		child2++;
		scroll.child(child2, 31134, 255, 253);
		child2++;
		scroll.child(child2, 31135, 45, 299);
		child2++;
		scroll.child(child2, 31136, 255, 299);
		child2++;
		scroll.child(child2, 31137, 45, 345);
		child2++;
		scroll.child(child2, 31138, 255, 345);
		child2++;
		for (int k = 31139; k < 31149; k++)
			addTextPC(k, "(250 Pts)", TDA, 0, 0x855013, false);
		scroll.child(child2, 31139, 44, 176);
		child2++;
		scroll.child(child2, 31140, 255, 176);
		child2++;
		scroll.child(child2, 31141, 44, 222);
		child2++;
		scroll.child(child2, 31142, 255, 222);
		child2++;
		scroll.child(child2, 31143, 44, 268);
		child2++;
		scroll.child(child2, 31144, 255, 268);
		child2++;
		scroll.child(child2, 31145, 44, 314);
		child2++;
		scroll.child(child2, 31146, 255, 314);
		child2++;
		scroll.child(child2, 31147, 44, 360);
		child2++;
		scroll.child(child2, 31148, 255, 360);
		child2++;
		RSInterface pcLobby = addInterface(31080);
		String[][] values = { { "Next Departure: 5 min 30 seconds", "CCCCCC" }, { "Players Ready: -1", "59D231" },
				{ "(Need 5 to 25 players)", "DED36A" }, { "Pest Points: -1", "99FFFF" } };
		pcLobby.totalChildren(4);
		int yPos = 6;
		for (int k = 31081; k < 31085;) {
			for (int i = 0; i < 4; i++, k++, yPos += 16) {
				int color = Integer.parseInt(values[i][1], 16);
				addText(k, values[i][0], TDA, 1, color, false);
				pcLobby.child(i, k, 6, yPos);
			}
		}
		RSInterface pc = addInterface(31058);
		int config = 941;
		for (int a = 31072; a < 31076; a++, config++) {
			pcDeadPortal(a, config);
		}
		int sprite = 0;
		for (int k = 31059; k < 31065; k++, sprite++) {
			addSprite(k, sprite, "pestcontrol");
		}
		addText(31065, "-200", TDA, 0, 0x5EBB29, false);
		addText(31066, "0", TDA, 0, 0x8B1313, false);
		addText(31067, "100", TDA, 0, 0xFF00FF, true);
		addText(31068, "100", TDA, 0, 0x6666FF, true);
		addText(31069, "100", TDA, 0, 0xFFFF00, true);
		addText(31070, "100", TDA, 0, 0xFF3333, true);
		String[][] data = { { "W", "FF00FF" }, { "E", "6666FF" }, { "SE", "FFFF00" }, { "SW", "FF3333" } };
		for (int D = 31076; D < 31080;) {
			for (int i = 0; i < 4; i++, D++) {
				int color = Integer.parseInt(data[i][1], 16);
				addText(D, data[i][0], TDA, 1, color, false);
			}
		}
		addText(31071, "-2 Min", TDA, 0, 0xCCCCCC, false);
		int child = 0;
		pc.totalChildren(21);
		pc.child(child, 31059, 353, 27);
		child++;
		pc.child(child, 31060, 390, 27);
		child++;
		pc.child(child, 31061, 428, 27);
		child++;
		pc.child(child, 31062, 466, 27);
		child++;
		pc.child(child, 31063, 4, 24);
		child++;
		pc.child(child, 31064, 4, 52);
		child++;
		pc.child(child, 31065, 29, 31);
		child++;
		pc.child(child, 31066, 30, 64);
		child++;
		pc.child(child, 31067, 366, 12);
		child++;
		pc.child(child, 31068, 403, 12);
		child++;
		pc.child(child, 31069, 441, 12);
		child++;
		pc.child(child, 31070, 479, 12);
		child++;
		pc.child(child, 31071, 3, 88);
		child++;
		pc.child(child, 31072, 357, 30);
		child++;
		pc.child(child, 31073, 394, 30);
		child++;
		pc.child(child, 31074, 432, 30);
		child++;
		pc.child(child, 31075, 470, 30);
		child++;
		pc.child(child, 31076, 361, 55);
		child++;
		pc.child(child, 31077, 399, 55);
		child++;
		pc.child(child, 31078, 433, 55);
		child++;
		pc.child(child, 31079, 470, 55);
		child++;
	}

	public static void questItf(TextDrawingArea[] TDA) {
		RSInterface quest = interfaceCache[8134];
		quest.child(1, 31005, 483, 5);
		RSInterface quest2 = interfaceCache[297];
		textColor(299, 16748608);
		textShadowed(299, true);
		quest2.child(2, 31086, 483, 5);
		RSInterface quest3 = interfaceCache[12140];
		quest3.child(2, 31005, 483, 5);
		RSInterface quest4 = interfaceCache[15831];
		quest4.child(2, 31005, 483, 5);
		RSInterface quest5 = interfaceCache[837];
		quest5.child(2, 5952, 449, 28);
	}

	public static void tradeScreen(TextDrawingArea[] TDA) {
		/**
		 * layers 116-119 are free
		 */
		addSprite(13057, 3, "steelborder");
		addSprite(13058, 3, "miscgraphics");
		addSprite(12907, 2, "steelborder");
		addSprite(12906, 2, "miscgraphics");
		addSprite(12905, 1, "steelborder");
		addSprite(13041, 0, "steelborder2");
		addSprite(13040, 0, "steelborder");
		// TELEOTHER
		RSInterface accept = interfaceCache[12567];
		accept.disabledTextColor = 32768;
		accept.disabledTextHoverColor = 65280;
		RSInterface decline = interfaceCache[12569];
		decline.disabledTextColor = 8388608;
		decline.disabledTextHoverColor = 16711680;
		RSInterface text = interfaceCache[3443];
		text.appendChild(114, 89, 468, 31);
		addText(2422, "Trading with:\\nID 2422", TDA, 2, 65535, true);
		text.appendChild(115, 2422, 375, 285);
		RSInterface tradeMod = addInterface(6007);
		addText(3557, "ID:3557", TDA, 2, 0xffffff, true);
		setSize(3557, 0, 0);
		setSize(3558, 0, 0);
		tradeMod.width = 214;
		tradeMod.height = 208;
		tradeMod.scrollPosition = 0;
		tradeMod.scrollMax = 395;
		tradeMod.totalChildren(1);
		for (int k = 3533; k < 3536; k++) {
			textShadowed(k, true);
			textColor(k, 16748608);
		}
		for (int k = 3418; k < 3420; k++) {
			textColor(k, 16748608);
			textShadowed(k, true);
		}
		RSInterface txtMod = interfaceCache[3536];
		txtMod.disabledMessage = "@whi@There is @red@NO WAY@whi@ to @red@REVERSE@whi@ a trade if you change your mind!";
		tradeMod.child(0, 3557, 107, 0);
		text.child(112, 6007, 22, 80);
		RSInterface tradeMod2 = addInterface(1567);
		addText(3558, "ID:3558", TDA, 2, 0xffffff, true);
		tradeMod2.width = 214;
		tradeMod2.height = 208;
		tradeMod2.scrollPosition = 0;
		tradeMod2.scrollMax = 395;
		tradeMod2.totalChildren(1);
		tradeMod2.child(0, 3558, 107, 0);
		text.child(113, 1567, 262, 80);
		RSInterface trade = interfaceCache[3323];
		RSInterface trade2 = interfaceCache[3421];
		RSInterface trade3 = interfaceCache[3423];
		RSInterface trade4 = interfaceCache[3547];
		RSInterface trade5 = interfaceCache[3549];
		trade.child(91, 3417, 215, 29);
		textCentered(3417, true);
		trade2.disabledTextColor = 32768;
		trade4.disabledTextColor = 32768;
		trade3.disabledTextColor = 8388608;
		trade5.disabledTextColor = 8388608;
		trade4.disabledTextHoverColor = 65280;
		trade5.disabledTextHoverColor = 16711680;
		trade3.disabledTextHoverColor = 16711680;
		trade2.disabledTextHoverColor = 65280;
		trade.appendChild(109, 1568, 208, 80);
		trade.appendChild(110, 89, 472, 27);
		RSInterface rsi = addInterface(1568);
		rsi.width = 99;
		rsi.height = 56;
		createBackground(13039, 0, 0);
		int child = 0;
		rsi.totalChildren(14);
		rsi.child(child, 13039, 0, 0);
		child++;
		rsi.child(child, 13040, 0, 0);
		child++;
		rsi.child(child, 13041, 25, -15);
		child++;
		rsi.child(child, 13041, 39, -15);
		child++;
		rsi.child(child, 12905, 74, 0);
		child++;
		rsi.child(child, 12905, 74, 0);
		child++;
		rsi.child(child, 12906, -15, 14);
		child++;
		rsi.child(child, 12907, 0, 26);
		child++;
		rsi.child(child, 13058, 25, 35);
		child++;
		rsi.child(child, 13058, 40, 35);
		child++;
		rsi.child(child, 13057, 74, 26);
		child++;
		int Y = 12;
		for (int k = 8482; k < 8485; k++, Y += 10, child++) {
			addText(k, "ID:" + k, TDA, 0, 16748608, true);
			setSize(k, 0, 0);
			rsi.child(child, k, 49, Y);
		}
		addText(8484, "inventory slots", TDA, 0, 16748608, true);
	}

	public static void skillMenu(TextDrawingArea[] TDA) {
		RSInterface submenu = addInterface(31022);
		RSInterface tab = addInterface(31000);
		RSInterface menu = addInterface(31015);
		menu.width = 358;
		createBackground(31001, 70, 0x302313);
		addSprite(31002, 0, "tradebacking");
		addSprite(13041, 0, "steelborder2");
		addSprite(31021, 1, "steelborder2");
		submenu.totalChildren(44);
		submenu.height = 219;
		int child = 0;
		submenu.child(child, 31002, 20, 5);
		child++;
		submenu.child(child, 31002, 65, 5);
		child++;
		submenu.child(child, 31002, 20, 65);
		child++;
		submenu.child(child, 31002, 65, 65);
		child++;
		submenu.child(child, 31002, 20, 125);
		child++;
		submenu.child(child, 31002, 65, 125);
		child++;
		submenu.child(child, 31002, 20, 185);
		child++;
		submenu.child(child, 31002, 65, 185);
		child++;
		submenu.child(child, 12906, 0, 30);
		child++;
		submenu.child(child, 12906, 0, 66);
		child++;
		submenu.child(child, 12906, 0, 66);
		child++;
		submenu.child(child, 12906, 0, 102);
		child++;
		submenu.child(child, 12906, 0, 138);
		child++;
		submenu.child(child, 12906, 0, 174);
		child++;
		submenu.child(child, 13040, 15, 0);
		child++;
		submenu.child(child, 13041, 40, -15);
		child++;
		submenu.child(child, 13041, 76, -15);
		child++;
		submenu.child(child, 13041, 112, -15);
		child++;
		submenu.child(child, 12905, 130, 0);
		child++;
		submenu.child(child, 12906, 0, 179);
		child++;
		submenu.child(child, 12907, 15, 189);
		child++;
		submenu.child(child, 13058, 40, 198);
		child++;
		submenu.child(child, 13058, 76, 198);
		child++;
		submenu.child(child, 13058, 112, 198);
		child++;
		submenu.child(child, 13057, 130, 189);
		child++;
		submenu.child(child, 31021, 134, 164);
		child++;
		submenu.child(child, 31021, 134, 128);
		child++;
		submenu.child(child, 31021, 134, 92);
		child++;
		submenu.child(child, 31021, 134, 56);
		child++;
		submenu.child(child, 31021, 134, 20);
		child++;
		int Y = 10;
		for (int l = 31031; l < 31045; l++) {
			addTextHover(l, "ID:" + l, TDA, 2, 16748608, true);
			submenu.child(child, l, 24, Y);
			child++;
			Y += 14;
		}
		RSInterface bank = interfaceCache[5292];
		bank.childX[90] += 5;
		bank.childY[90] += 2;
		RSInterface oldMenu = interfaceCache[8717];
		oldMenu.width = 326;
		oldMenu.height = 230;
		oldMenu.childX[80] = 50;
		for (int idx = 40; idx < 80; idx++)
			oldMenu.childX[idx] += 25;
		for (int color = 8720; color < 8800; color++) {
			textColor(color, 16748608);
			textShadowed(color, true);
		}
		addText(31024, "Level", TDA, 2, 16748608, false);
		addText(31025, "Advancement", TDA, 2, 16748608, false);
		addText(31026, "ID:31026", TDA, 2, 16748608, false);
		tab.totalChildren(8);
		int frame = 0;
		tab.child(frame, 31001, 0, 0);
		frame++;
		tab.child(frame, 31015, 0, -40);
		frame++;
		tab.child(frame, 31005, 483, 6);
		frame++;
		tab.child(frame, 31022, 350, 50);
		frame++;
		tab.child(frame, 31024, 13, 28);
		frame++;
		tab.child(frame, 31025, 68, 28);
		frame++;
		tab.child(frame, 8717, 8, 55);
		frame++;
		tab.child(frame, 31026, 262, 27);
		frame++;
		menu.totalChildren(64);
		int frame2 = 0;
		menu.child(frame2, 31002, 5, 65);
		frame2++;
		menu.child(frame2, 31002, 93, 65);
		frame2++;
		menu.child(frame2, 31002, 181, 65);
		frame2++;
		menu.child(frame2, 31002, 5, 125);
		frame2++;
		menu.child(frame2, 31002, 93, 125);
		frame2++;
		menu.child(frame2, 31002, 181, 125);
		frame2++;
		menu.child(frame2, 31002, 5, 185);
		frame2++;
		menu.child(frame2, 31002, 93, 185);
		frame2++;
		menu.child(frame2, 31002, 181, 185);
		frame2++;
		menu.child(frame2, 31002, 5, 245);
		frame2++;
		menu.child(frame2, 31002, 93, 245);
		frame2++;
		menu.child(frame2, 31002, 181, 245);
		frame2++;
		menu.child(frame2, 31002, 269, 65);
		frame2++;
		menu.child(frame2, 31002, 269, 125);
		frame2++;
		menu.child(frame2, 31002, 269, 185);
		frame2++;
		menu.child(frame2, 31002, 269, 245);
		frame2++;
		menu.child(frame2, 31002, 5, 305);
		frame2++;
		menu.child(frame2, 31002, 93, 305);
		frame2++;
		menu.child(frame2, 31002, 181, 305);
		frame2++;
		menu.child(frame2, 31002, 269, 305);
		frame2++;
		int X = 10;
		for (frame2 = 20; frame2 < 30; frame2++) {
			menu.child(frame2, 13041, X, 70);
			X += 36;
		}
		menu.child(frame2, 13040, 5, 60);
		frame2++;
		int x = 30;
		for (frame2 = 31; frame2 < 40; frame2++) {
			menu.child(frame2, 13041, x, 45);
			x += 36;
		}
		menu.child(frame2, 12905, 333, 60);
		frame2++;
		int y = 90;
		for (frame2 = 41; frame2 < 47; frame2++) {
			menu.child(frame2, 12906, -10, y);
			y += 36;
		}
		menu.child(frame2, 12907, 5, 304);
		frame2++;
		int x1 = 30;
		for (frame2 = 48; frame2 < 57; frame2++) {
			menu.child(frame2, 13058, x1, 313);
			x1 += 36;
		}
		int y1 = 90;
		for (frame2 = 57; frame2 < 63; frame2++) {
			menu.child(frame2, 31021, 337, y1);
			y1 += 36;
		}
		menu.child(frame2, 13057, 333, 304);
		frame2++;
	}

	public static void createSkillHover(int id) {
		RSInterface hover = addInterface(id);
		hover.type = 8;
		hover.disabledMessage = "";
		hover.width = 64;
		hover.height = 31;
	}

	public void specialBar(int id) {
		addActionButton(id - 12, 0, 0, 150, 26, "Use @gre@Special Attack");
		addTooltip8(4157, 150, 26, "Select to perform\\na special attack");
		RSInterface bar = addInterface(id + 1);
		bar.type = 6;
		bar.mediaID = 18552;
		bar.mediaType = 1;
		bar.modelRotX = 0;
		bar.modelRotY = 500;
		bar.modelZoom = 1153;
		bar.width = 146;
		bar.height = 9;
		RSInterface rsi = interfaceCache[id - 12];
		rsi.width = 150;
		rsi.height = 26;
		rsi = interfaceCache[id];
		rsi.width = 150;
		rsi.height = 326;
		rsi.child(0, id - 12, 0, 0);
		rsi.child(1, id + 1, 2, 9);
		rsi.child(12, id + 12, 16, 8);
		for (int i = 2; i < 12; i++) {
			rsi.childY[i] -= 1;
		}
		rsi.appendChild(13, 4157, 0, 0);
		rsi = interfaceCache[id + 1];
		addSpecialBarModel(id + 2, 18553);
		addSpecialBarModel(id + 3, 18555);
		addSpecialBarModel(id + 4, 18556);
		addSpecialBarModel(id + 5, 18557);
		addSpecialBarModel(id + 6, 18558);
		addSpecialBarModel(id + 7, 18559);
		addSpecialBarModel(id + 8, 18560);
		addSpecialBarModel(id + 9, 18561);
		addSpecialBarModel(id + 10, 18562);
		addSpecialBarModel(id + 11, 18554);
		setSize(id + 2, 145, 12);
		setSize(id + 3, 145 - 30, 12);
		setSize(id + 4, 145 - 30 - 30, 12);
		setSize(id + 5, 145 - 30 - 30 - 28, 12);
		setSize(id + 6, 145 - 30 - 30 - 30 - 26, 12);
		setSize(id + 7, 145 - 30 - 30 - 30 - 30 - 24, 12);
		setSize(id + 8, 145 - 30 - 30 - 30 - 30 - 30 - 25, 12);
		setSize(id + 9, 145 - 30 - 30 - 30 - 30 - 30 - 30 - 23, 12);
		setSize(id + 10, 145 - 30 - 30 - 30 - 30 - 30 - 30 - 30 - 22, 12);
		setSize(id + 11, 145 - 30 - 30 - 30 - 30 - 30 - 30 - 30 - 30 - 19, 12);
	}

	public static void addSpecialBarModel(int id, int modelID) {
		RSInterface mdl = interfaceCache[id];
		mdl.mediaID = (short) modelID;
	}

	public static void Sidebar0(TextDrawingArea[] tda) {
		RSInterface rsi = addInterface(9509);
		addToggleButton(150, 0, 1, 172, 150, 44, "Auto Retaliate", -1);
		addTooltip8(9634, 150, 44, "When active your player\\nwill automatically\\nfight back if attacked");
		textSize(3983, tda, 0);
		RSInterface but = interfaceCache[4454];
		but.requiredValues[0] = 1;
		RSInterface but1 = interfaceCache[4453];
		but1.requiredValues[0] = 2;
		RSInterface but2 = interfaceCache[4452];
		but2.requiredValues[0] = 0;
		RSInterface but3 = interfaceCache[431];
		but3.requiredValues[0] = 0;
		RSInterface but4 = interfaceCache[432];
		but4.requiredValues[0] = 2;
		RSInterface but5 = interfaceCache[433];
		but5.requiredValues[0] = 1;
		RSInterface but6 = interfaceCache[1755];
		but6.requiredValues[0] = 0;
		RSInterface but7 = interfaceCache[1756];
		but7.requiredValues[0] = 2;
		RSInterface but8 = interfaceCache[1757];
		but8.requiredValues[0] = 1;
		RSInterface but9 = interfaceCache[1770];
		but9.requiredValues[0] = 0;
		RSInterface but10 = interfaceCache[1771];
		but10.requiredValues[0] = 2;
		RSInterface but11 = interfaceCache[1772];
		but11.requiredValues[0] = 1;
		RSInterface but12 = interfaceCache[6135];
		but12.requiredValues[0] = 0;
		RSInterface but13 = interfaceCache[6136];
		but13.requiredValues[0] = 2;
		RSInterface but14 = interfaceCache[6137];
		but14.requiredValues[0] = 1;
		RSInterface but15 = interfaceCache[12296];
		but15.requiredValues[0] = 0;
		RSInterface but16 = interfaceCache[12297];
		but16.requiredValues[0] = 2;
		RSInterface but17 = interfaceCache[12298];
		but17.requiredValues[0] = 1;
		RSInterface text = interfaceCache[3983];
		text.disabledTextColor = 0xff981f;
		text.textCentered = true;
		rsi.totalChildren(3, 3, 3);
		rsi.child(0, 3983, 52, 25);
		rsi.child(1, 9634, 21, 153);
		rsi.child(2, 150, 21, 153);
		addFourSelect(1698, 1701, 7499, "Chop", "Hack", "Smash", "Block", 42, 75, 127, 75, 39, 128, 125, 128, 122, 103, 40, 50, 122, 50,
				40, 103, tda, 4077, 4078, 4079, 4080, "(Accurate)\\n(Slash)\\n(Attack XP)", "(Aggressive)\\n(Slash)\\n(Strength XP)",
				"(Aggressive)\\n(Crush)\\n(Strength XP)", "(Defensive)\\n(Slash)\\n(Defence XP)");
		addFourSelect(2276, 2279, 7574, "Stab", "Lunge", "Slash", "Block", 43, 75, 124, 75, 41, 128, 125, 128, 122, 103, 40, 50, 122, 50,
				40, 103, tda, 4706, 4081, 4083, 4084, "(Accurate)\\n(Stab)\\n(Attack XP)", "(Aggressive)\\n(Stab)\\n(Strength XP)",
				"(Aggressive)\\n(Slash)\\n(Strength XP)", "(Defensive)\\n(Stab)\\n(Defence XP)");
		addFourSelect(2423, 2426, 7599, "Chop", "Slash", "Lunge", "Block", 42, 75, 125, 75, 40, 128, 125, 128, 122, 103, 40, 50, 122, 50,
				40, 103, tda, 4085, 4086, 4087, 4089, "(Accurate)\\n(Slash)\\n(Attack XP)", "(Aggressive)\\n(Slash)\\n(Strength XP)",
				"(Controlled)\\n(Stab)\\n(Shared XP)", "(Defensive)\\n(Slash)\\n(Defence XP)");
		addFourSelect(3796, 3799, 7624, "Pound", "Pummel", "Spike", "Block", 39, 75, 121, 75, 41, 128, 125, 128, 122, 103, 40, 50, 122, 50,
				40, 103, tda, 4090, 4091, 4092, 4093, "(Accurate)\\n(Crush)\\n(Attack XP)", "(Aggressive)\\n(Crush)\\n(Strength XP)",
				"(Controlled)\\n(Stab)\\n(Shared XP)", "(Defensive)\\n(Crush)\\n(Defence XP)");
		addFourSelect(4679, 4682, 7674, "Lunge", "Swipe", "Pound", "Block", 40, 75, 124, 75, 39, 128, 125, 128, 122, 103, 40, 50, 122, 50,
				40, 103, tda, 4095, 4096, 4097, 4098, "(Controlled)\\n(Stab)\\n(Shared XP)", "(Controlled)\\n(Slash)\\n(Shared XP)",
				"(Controlled)\\n(Crush)\\n(Shared XP)", "(Defensive)\\n(Stab)\\n(Defence XP)");
		addFourSelect(4705, 4708, 7699, "Chop", "Slash", "Smash", "Block", 42, 75, 125, 75, 39, 128, 125, 128, 122, 103, 40, 50, 122, 50,
				40, 103, tda, 4099, 4101, 4102, 4103, "(Accurate)\\n(Slash)\\n(Attack XP)", "(Aggressive)\\n(Slash)\\n(Strength XP)",
				"(Aggressive)\\n(Crush)\\n(Strength XP)", "(Defensive)\\n(Slash)\\n(Defence XP)");
		addFourSelectNoSpec(5570, 5573, "Spike", "Impale", "Smash", "Block", 41, 75, 123, 75, 39, 128, 125, 128, 122, 103, 40, 50, 122, 50,
				40, 103, tda, 4104, 4105, 4107, 4108, "(Accurate)\\n(Stab)\\n(Attack XP)", "(Aggressive)\\n(Stab)\\n(Strength XP)",
				"(Aggressive)\\n(Crush)\\n(Strength XP)", "(Defensive)\\n(Stab)\\n(Defence XP)");
		addFourSelect(7762, 7765, 7800, "Chop", "Slash", "Lunge", "Block", 42, 75, 125, 75, 40, 128, 125, 128, 122, 103, 40, 50, 122, 50,
				40, 103, tda, 4109, 4110, 4111, 4113, "(Accurate)\\n(Slash)\\n(Attack XP)", "(Aggressive)\\n(Slash)\\n(Strength XP)",
				"(Controlled)\\n(Stab)\\n(Shared XP)", "(Defensive)\\n(Slash)\\n(Defence XP)");
		addFourSelectNoSpec(776, 779, "Reap", "Chop", "Jab", "Block", 42, 75, 126, 75, 46, 128, 125, 128, 122, 103, 122, 50, 40, 103, 40,
				50, tda, 4114, 4115, 4116, 4117, "(Accurate)\\n(Slash)\\n(Attack XP)", "(Aggressive)\\n(Stab)\\n(Strength XP)",
				"(Aggressive)\\n(Crush)\\n(Strength XP)", "(Defensive)\\n(Slash)\\n(Defence XP)");
		addThreeSelect(425, 428, 7474, "Pound", "Pummel", "Block", 39, 75, 121, 75, 42, 128, 40, 103, 40, 50, 122, 50, tda, 4119, 4120,
				4121, "(Accurate)\\n(Crush)\\n(Attack XP)", "(Aggressive)\\n(Crush)\\n(Strength XP)",
				"(Defensive)\\n(Crush)\\n(Defence XP)");
		addThreeSelect(1749, 1752, 7524, "Accurate", "Rapid", "Longrange", 33, 75, 125, 75, 29, 128, 40, 103, 40, 50, 122, 50, tda, 4122,
				4123, 4125, "(Accurate)\\n(Ranged XP)", "(Rapid)\\n(Ranged XP)", "(Longrange)\\n(Ranged XP)\\n(Defence XP)");
		addThreeSelect(1764, 1767, 7549, "Accurate", "Rapid", "Longrange", 33, 75, 125, 75, 29, 128, 40, 103, 40, 50, 122, 50, tda, 4126,
				4127, 4128, "(Accurate)\\n(Ranged XP)", "(Rapid)\\n(Ranged XP)", "(Longrange)\\n(Ranged XP)\\n(Defence XP)");
		addThreeSelect(4446, 4449, 7649, "Accurate", "Rapid", "Longrange", 33, 75, 125, 75, 29, 128, 40, 103, 40, 50, 122, 50, tda, 4129,
				4131, 4132, "(Accurate)\\n(Ranged XP)", "(Rapid)\\n(Ranged XP)", "(Longrange)\\n(Ranged XP)\\n(Defence XP)");
		// addThreeSelectNoSpec, unarmed
		addThreeSelect(5855, 5857, -1, "Punch", "Kick", "Block", 40, 75, 129, 75, 42, 128, 40, 50, 122, 50, 40, 103, tda, 4133, 4134, 4135,
				"(Accurate)\\n(Crush)\\n(Attack XP)", "(Aggressive)\\n(Crush)\\n(Strength XP)", "(Defensive)\\n(Crush)\\n(Defence XP)");
		addThreeSelect(6103, 6132, 6117, "Bash", "Pound", "Block", 43, 75, 124, 75, 42, 128, 40, 103, 40, 50, 122, 50, tda, 4137, 4138,
				4139, "(Accurate)\\n(Crush)\\n(Attack XP)", "(Aggressive)\\n(Crush)\\n(Strength XP)",
				"(Defensive)\\n(Crush)\\n(Defence XP)");
		addThreeSelect(8460, 8463, 8493, "Jab", "Swipe", "Fend", 46, 75, 124, 75, 43, 128, 40, 103, 40, 50, 122, 50, tda, 4140, 4141, 4143,
				"(Controlled)\\n(Stab)\\n(Shared XP)", "(Aggressive)\\n(Slash)\\n(Strength XP)", "(Defensive)\\n(Stab)\\n(Defence XP)");
		addThreeSelect(12290, 12293, 12323, "Flick", "Lash", "Deflect", 44, 75, 127, 75, 37, 128, 40, 50, 40, 103, 122, 50, tda, 4144,
				4145, 4146, "(Accurate)\\n(Slash)\\n(Attack XP)", "(Controlled)\\n(Slash)\\n(Shared XP)",
				"(Defensive)\\n(Slash)\\n(Defence XP)");
		addFourSelectMage(328, 331, "Bash", "Pound", "Focus", 42, 66, 39, 101, 41, 136, 40, 120, 40, 50, 40, 85, tda, 4147, 4154, 4155,
				4156, "(Accurate)\\n(Crush)\\n(Attack XP)", "(Aggressive)\\n(Crush)\\n(Strength XP)",
				"(Defensive)\\n(Crush)\\n(Defence XP)", "(Spell)\\n(Magic XP)");
	}

	public static void addFourSelect(int id, int id2, int id3, String text1, String text2, String text3, String text4, int str1x,
			int str1y, int str2x, int str2y, int str3x, int str3y, int str4x, int str4y, int img1x, int img1y, int img2x, int img2y,
			int img3x, int img3y, int img4x, int img4y, TextDrawingArea[] tda, int hover, int hover2, int hover3, int hover4,
			String hovert, String hovert2, String hovert3, String hovert4) {
		RSInterface rsi = addInterface(id);
		addText(id2, "-2", tda, 3, 0xff981f, true);
		addText(id2 + 11, text1, tda, 0, 0xff981f, false);
		addText(id2 + 12, text2, tda, 0, 0xff981f, false);
		addText(id2 + 13, text3, tda, 0, 0xff981f, false);
		addText(id2 + 14, text4, tda, 0, 0xff981f, false);
		addTooltip8(hover, 68, 44, hovert);
		addTooltip8(hover2, 68, 44, hovert2);
		addTooltip8(hover3, 68, 44, hovert3);
		addTooltip8(hover4, 68, 44, hovert4);
		rsi.specialBar(id3);
		rsi.width = 190;
		rsi.height = 321;
		int last = 19;
		int frame = 0;
		rsi.totalChildren(last, last, last);
		rsi.child(frame, id2 + 3, 21, 99);
		frame++;
		rsi.child(frame, id2 + 4, 104, 99);
		frame++;
		rsi.child(frame, id2 + 5, 105, 46);
		frame++;
		rsi.child(frame, id2 + 6, 21, 46);
		frame++;
		rsi.child(frame, id2 + 7, img1x, img1y);
		frame++;
		rsi.child(frame, id2 + 8, img2x, img2y);
		frame++;
		rsi.child(frame, id2 + 9, img3x, img3y);
		frame++;
		rsi.child(frame, id2 + 10, img4x, img4y);
		frame++;
		rsi.child(frame, id2 + 11, str1x, str1y);
		frame++;
		rsi.child(frame, id2 + 12, str2x, str2y);
		frame++;
		rsi.child(frame, id2 + 13, str3x, str3y);
		frame++;
		rsi.child(frame, id2 + 14, str4x, str4y);
		frame++;
		rsi.child(frame, id2, 86, 4);
		frame++;
		rsi.child(frame, id3, 21, 205);
		frame++;
		rsi.child(frame, 9509, 0, 0);
		frame++;
		rsi.child(frame, hover, 21, 46);
		frame++;
		rsi.child(frame, hover2, 105, 46);
		frame++;
		rsi.child(frame, hover3, 21, 99);
		frame++;
		rsi.child(frame, hover4, 105, 99);
		frame++;
		for (int i = id2 + 3; i < id2 + 7; i++) {
			rsi = interfaceCache[i];
			rsi.setDisabledSprite("attack1", 2, aClass44);
			rsi.setEnabledSprite("attack1", 3, aClass44);
			rsi.width = 68;
			rsi.height = 44;
		}
	}

	public static void addFourSelectNoSpec(int id, int id2, String text1, String text2, String text3, String text4, int str1x, int str1y,
			int str2x, int str2y, int str3x, int str3y, int str4x, int str4y, int img1x, int img1y, int img2x, int img2y, int img3x,
			int img3y, int img4x, int img4y, TextDrawingArea[] tda, int hover, int hover2, int hover3, int hover4, String hovert,
			String hovert2, String hovert3, String hovert4) {
		RSInterface rsi = addInterface(id);
		addText(id2, "-2", tda, 3, 0xff981f, true);
		addText(id2 + 11, text1, tda, 0, 0xff981f, false);
		addText(id2 + 12, text2, tda, 0, 0xff981f, false);
		addText(id2 + 13, text3, tda, 0, 0xff981f, false);
		addText(id2 + 14, text4, tda, 0, 0xff981f, false);
		addTooltip8(hover, 68, 44, hovert);
		addTooltip8(hover2, 68, 44, hovert2);
		addTooltip8(hover3, 68, 44, hovert3);
		addTooltip8(hover4, 68, 44, hovert4);
		rsi.width = 190;
		rsi.height = 261;
		int last = 18;
		int frame = 0;
		rsi.totalChildren(last, last, last);
		rsi.child(frame, id2 + 3, 21, 99);
		frame++;
		rsi.child(frame, id2 + 4, 104, 99);
		frame++;
		rsi.child(frame, id2 + 5, 105, 46);
		frame++;
		rsi.child(frame, id2 + 6, 21, 46);
		frame++;
		rsi.child(frame, id2 + 7, img1x, img1y);
		frame++;
		rsi.child(frame, id2 + 8, img2x, img2y);
		frame++;
		rsi.child(frame, id2 + 9, img3x, img3y);
		frame++;
		rsi.child(frame, id2 + 10, img4x, img4y);
		frame++;
		rsi.child(frame, id2 + 11, str1x, str1y);
		frame++;
		rsi.child(frame, id2 + 12, str2x, str2y);
		frame++;
		rsi.child(frame, id2 + 13, str3x, str3y);
		frame++;
		rsi.child(frame, id2 + 14, str4x, str4y);
		frame++;
		rsi.child(frame, id2, 86, 4);
		frame++;
		rsi.child(frame, 9509, 0, 0);
		frame++;
		rsi.child(frame, hover, 21, 46);
		frame++;
		rsi.child(frame, hover2, 105, 46);
		frame++;
		rsi.child(frame, hover3, 21, 99);
		frame++;
		rsi.child(frame, hover4, 105, 99);
		frame++;
		for (int i = id2 + 3; i < id2 + 7; i++) {
			rsi = interfaceCache[i];
			rsi.setDisabledSprite("attack1", 2, aClass44);
			rsi.setEnabledSprite("attack1", 3, aClass44);
			rsi.width = 68;
			rsi.height = 44;
		}
	}

	public static void addThreeSelect(int id, int id2, int id3, String text1, String text2, String text3, int str1x, int str1y, int str2x,
			int str2y, int str3x, int str3y, int img1x, int img1y, int img2x, int img2y, int img3x, int img3y, TextDrawingArea[] tda,
			int hover, int hover2, int hover3, String hovert, String hovert2, String hovert3) {
		RSInterface bar = interfaceCache[12335];
		RSInterface bar2 = interfaceCache[7486];
		bar.enabledMessage = "@yel@S P E C I A L  A T T A C K";
		bar2.enabledMessage = "@yel@S P E C I A L  A T T A C K";
		RSInterface rsi = addInterface(id);
		addText(id2, "-2", tda, 3, 0xff981f, true);
		addText(id2 + 9, text1, tda, 0, 0xff981f, false);
		addText(id2 + 10, text2, tda, 0, 0xff981f, false);
		addText(id2 + 11, text3, tda, 0, 0xff981f, false);
		addTooltip8(hover, 68, 44, hovert);
		addTooltip8(hover2, 68, 44, hovert2);
		addTooltip8(hover3, 68, 44, hovert3);
		if (id3 != -1)
			rsi.specialBar(id3);
		rsi.width = 190;
		rsi.height = 321;
		int last = id3 == -1 ? 14 : 15;
		int frame = 0;
		rsi.totalChildren(last, last, last);
		rsi.child(frame, id2 + 3, 21, 99);
		frame++;
		rsi.child(frame, id2 + 4, 105, 46);
		frame++;
		rsi.child(frame, id2 + 5, 21, 46);
		frame++;
		rsi.child(frame, id2 + 6, img1x, img1y);
		frame++;
		rsi.child(frame, id2 + 7, img2x, img2y);
		frame++;
		rsi.child(frame, id2 + 8, img3x, img3y);
		frame++;
		rsi.child(frame, id2 + 9, str1x, str1y);
		frame++;
		rsi.child(frame, id2 + 10, str2x, str2y);
		frame++;
		rsi.child(frame, id2 + 11, str3x, str3y);
		frame++;
		rsi.child(frame, id2, 86, 4);
		frame++;
		if (id3 != -1) {
			rsi.child(frame, id3, 21, 205);
			frame++;
		}
		rsi.child(frame, 9509, 0, 0);
		frame++;
		rsi.child(frame, hover, 21, 46);
		frame++;
		rsi.child(frame, hover2, 105, 46);
		frame++;
		rsi.child(frame, hover3, 21, 99);
		frame++;
		for (int i = id2 + 3; i < id2 + 6; i++) {
			rsi = interfaceCache[i];
			rsi.setDisabledSprite("attack1", 2, aClass44);
			rsi.setEnabledSprite("attack1", 3, aClass44);
			rsi.width = 68;
			rsi.height = 44;
		}
	}

	public static void addFourSelectMage(int id, int id2, String text1, String text2, String text3, int str1x, int str1y, int str2x,
			int str2y, int str3x, int str3y, int img1x, int img1y, int img2x, int img2y, int img3x, int img3y, TextDrawingArea[] tda,
			int hover, int hover2, int hover3, int hover4, String hovert, String hovert2, String hovert3, String hovert4) {
		RSInterface rsi = addInterface(id);
		addText(id2, "-2", tda, 3, 0xff981f, true);
		addText(id2 + 9, text1, tda, 0, 0xff981f, false);
		addText(id2 + 10, text2, tda, 0, 0xff981f, false);
		addText(id2 + 11, text3, tda, 0, 0xff981f, false);
		addTooltip8(hover, 68, 32, hovert);
		addTooltip8(hover2, 68, 32, hovert2);
		addTooltip8(hover3, 68, 32, hovert3);
		addTooltip8(hover4, 68, 44, hovert4);
		addTooltip8(9724, 68, 44, "(Spell)\\n(Magic XP)\\n(Defence XP)");
		removeSomething(353);
		RSInterface txt1 = interfaceCache[2004];
		RSInterface txt2 = interfaceCache[6161];
		txt1.disabledTextHoverColor = 0xffffff;
		txt2.disabledTextHoverColor = 0xffffff;
		addText(354, "Spell", tda, 0, 0xff981f, false);
		addCacheSprite(337, 19, 0, "combaticons");
		addCacheSprite(338, 13, 0, "combaticons2");
		addCacheSprite(339, 14, 0, "combaticons2");
		removeSomething(349);
		addCacheSprite(350, 2, 3, "attack1");
		addCacheSprite(1194, 4, 5, "attack1");
		textTooltip(350, "Select");
		textTooltip(1194, "Select");
		RSInterface bt1 = interfaceCache[350];
		bt1.requiredValues = new int[1];
		bt1.requiredValues[0] = 1;
		bt1.valueCompareType = new int[1];
		bt1.valueCompareType[0] = 1;
		bt1.valueIndexArray = new int[1][3];
		bt1.valueIndexArray[0][0] = 5;
		bt1.valueIndexArray[0][1] = 108;
		bt1.valueIndexArray[0][2] = 0;
		bt1.atActionType = 5;
		RSInterface bt2 = interfaceCache[1194];
		bt2.requiredValues = new int[1];
		bt2.requiredValues[0] = 1;
		bt2.valueCompareType = new int[1];
		bt2.valueCompareType[0] = 1;
		bt2.valueIndexArray = new int[1][3];
		bt2.valueIndexArray[0][0] = 5;
		bt2.valueIndexArray[0][1] = 950;
		bt2.valueIndexArray[0][2] = 0;
		bt2.atActionType = 5;
		setSize(350, 68, 44);
		setSize(1194, 68, 44);
		removeSomething(151);
		addSprite(151, 6, "sideicons");
		rsi.width = 190;
		rsi.height = 261;
		int frame = 0;
		rsi.totalChildren(22);
		rsi.child(frame, id2 + 3, 20, 80);
		frame++;
		rsi.child(frame, id2 + 4, 20, 45);
		frame++;
		rsi.child(frame, id2 + 5, 20, 115);
		frame++;
		rsi.child(frame, id2 + 6, img1x, img1y);
		frame++;
		rsi.child(frame, id2 + 7, img2x, img2y);
		frame++;
		rsi.child(frame, id2 + 8, img3x, img3y);
		frame++;
		rsi.child(frame, id2 + 9, str1x, str1y);
		frame++;
		rsi.child(frame, id2 + 10, str2x, str2y);
		frame++;
		rsi.child(frame, id2 + 11, str3x, str3y);
		frame++;
		rsi.child(frame, 350, 104, 99 + 7);
		frame++;
		rsi.child(frame, 151, 140 - 15, 108);
		frame++;
		rsi.child(frame, 354, 125, 134);
		frame++;
		rsi.child(frame, 9509, 0, 0);
		frame++;
		rsi.child(frame, 1194, 105, 46);
		frame++;
		rsi.child(frame, 354, 145 - 20, 74);
		frame++;
		rsi.child(frame, 151, 138, 47);
		frame++;
		rsi.child(frame, id2, 86, 4);
		frame++;
		rsi.child(frame, hover, 26 - 4, 51 - 4);
		frame++;
		rsi.child(frame, hover2, 26 - 4, 86 - 4);
		frame++;
		rsi.child(frame, hover3, 26 - 4, 121 - 4);
		frame++;
		rsi.child(frame, hover4, 104, 106);
		frame++;
		rsi.child(frame, 9724, 105, 46);
		frame++;
		for (int i = id2 + 3; i < id2 + 6; i++) {
			rsi = interfaceCache[i];
			rsi.setDisabledSprite("combatBoxes", 0, aClass44);
			rsi.setEnabledSprite("combatBoxes", 1, aClass44);
			rsi.width = 68;
			rsi.height = 34;
		}
	}

	public void totalChildren(int id, int x, int y) {
		children = new short[id];
		childX = new short[x];
		childY = new short[y];
	}

	public static void setChildren(int total, RSInterface rsinterface) {
		rsinterface.children = new short[total];
		rsinterface.childX = new short[total];
		rsinterface.childY = new short[total];
	}

	public static void setBounds(int ID, int X, int Y, int frame, RSInterface RSinterface) {
		RSinterface.children[frame] = (short) ID;
		RSinterface.childX[frame] = (short) X;
		RSinterface.childY[frame] = (short) Y;
	}

	public static void removeSomething(int id) {
		//lol fail
	}

	public static void addButton(int id, int sid, String spriteName, String tooltip, int w, int h) {
		RSInterface tab = interfaceCache[id];
		tab.id = (short) id;
		tab.parentID = (short) id;
		tab.type = 5;
		tab.atActionType = 1;
		tab.contentType = 0;
		tab.alpha = (byte) 0;
		tab.isMouseoverTriggered = false;
		tab.mouseOverPopupInterface = -1;
		tab.enabledSprite = imageLoader(sid, spriteName);
		tab.disabledSprite = imageLoader(sid, spriteName);
		//tab.setDisabledSprite(spriteName, sid, aClass44);
		//tab.setEnabledSprite(spriteName, sid, aClass44);
		//System.out.println("Name: " + spriteName + " id: " + sid + " " + aClass44);
		tab.width = (short) w;
		tab.height = (short) h;
		tab.tooltip = tooltip;
	}
	
	public static void addMagicButton(int id, int sid, int suo, int lvl, String name, String desc, int[] runes, int[] reqRunes,
			int prayerLvl, int actionType) {
		RSInterface tab = addTabInterface(id);
		tab.id = (short) id;
		tab.parentID = (short) id;
		tab.type = 5;
		tab.spell = new MagicSpell(lvl, name, desc, runes, reqRunes, prayerLvl);
		tab.atActionType = (short)actionType;
		tab.contentType = 0;
		tab.alpha = (byte) 0;
		tab.mouseOverPopupInterface = ((short) (id+1));
		tab.spellUsableOn = (short)suo;
		//tab.enabledSprite = imageLoader(sid, "Magic/MagicOn/SPELL");
		tab.setEnabledSprite("lunar0", sid, aClass44);
		tab.setDisabledSprite("lunar1", sid, aClass44);
		//XP Lock and teleports have no off sprite
		/*if(id != 17001 && id != 17041 && id != 17043 && id != 17045 && id != 17047 && id != 17049 && id != 17051 && id != 17053 && id != 17055)
			tab.disabledSprite = imageLoader(sid, "Magic/SPELL");
		else
			tab.disabledSprite = imageLoader(sid, "Magic/MagicOn/SPELL");*/
		tab.width = (short) 20;
		tab.height = (short) 20;
		tab.isMagicButton = true;
		tab.selectedActionName = "Cast On";
		tab.spellName = name;
		tab.tooltip = "Cast @gre@"+name;
	}
	

	public static void addHoverButton(int i, String imageName, int j, int width, int height, String text, int contentType, int hoverOver,
			int aT) {
		RSInterface tab = addTabInterface(i);
		tab.id = (short) i;
		tab.parentID = (short) i;
		tab.type = 5;
		tab.atActionType = (short) aT;
		tab.contentType = (short) contentType;
		tab.alpha = 0;
		tab.mouseOverPopupInterface = (short) hoverOver;
		tab.setDisabledSprite(imageName, j, aClass44);
		tab.width = (short) width;
		tab.height = (short) height;
		tab.tooltip = text;
	}

	public static void addHoveredButton(int i, String imageName, int j, int w, int h, int IMAGEID) {
		RSInterface tab = addTabInterface(i);
		tab.parentID = (short) i;
		tab.id = (short) i;
		tab.type = 0;
		tab.atActionType = 0;
		tab.width = (short) w;
		tab.height = (short) h;
		tab.isMouseoverTriggered = true;
		tab.alpha = 0;
		tab.mouseOverPopupInterface = -1;
		tab.scrollMax = 0;
		addHoverImage(IMAGEID, j, j, imageName);
		tab.totalChildren(1);
		tab.child(0, IMAGEID, 0, 0);
	}

	public static void addHoverImage(int i, int j, int k, String name) {
		RSInterface tab = addTabInterface(i);
		tab.id = (short) i;
		tab.parentID = (short) i;
		tab.type = 5;
		tab.atActionType = 0;
		tab.contentType = 0;
		tab.width = 512;
		tab.height = 334;
		tab.alpha = 0;
		tab.mouseOverPopupInterface = 52;
		tab.setDisabledSprite(name, j, aClass44);
	}

	public static void addTransparentSprite(int id, int spriteId, String spriteName) {
		RSInterface tab = interfaceCache[id];
		tab.id = (short) id;
		tab.parentID = (short) id;
		tab.type = 5;
		tab.atActionType = 0;
		tab.contentType = 0;
		tab.alpha = (byte) 0;
		tab.mouseOverPopupInterface = -1;
		tab.setDisabledSprite(spriteName, spriteId, aClass44);
		tab.setEnabledSprite(spriteName, spriteId, aClass44);
		tab.width = 512;
		tab.height = 334;
	}

	public static RSInterface addScreenInterface(int id) {
		RSInterface tab = interfaceCache[id];
		tab.id = (short) id;
		tab.parentID = (short) id;
		tab.type = 0;
		tab.atActionType = 0;
		tab.contentType = 0;
		tab.width = 512;
		tab.height = 334;
		tab.alpha = (byte) 0;
		tab.mouseOverPopupInterface = 0;
		return tab;
	}

	public static RSInterface addTabInterface(int id) {
		
		RSInterface tab = interfaceCache[id] = new RSInterface();
		tab.id = (short) id;// 250
		tab.parentID = (short) id;// 236
		tab.type = 0;// 262
		tab.atActionType = 0;// 217
		tab.contentType = 0;
		tab.width = 512;// 220
		tab.height = 334;// 267
		tab.alpha = (byte) 0;
		tab.mouseOverPopupInterface = -1;// Int 230
		return tab;
	}

	public void child(int id, int interID, int x, int y) {
		children[id] = (short) interID;
		childX[id] = (short) x;
		childY[id] = (short) y;
	}

	public void totalChildren(int t) {
		children = new short[t];
		childX = new short[t];
		childY = new short[t];
	}

	/**
	 * TODO: Mediatypes: 1 = Any model 2 = NPC_CHATHEAD_MODEL 3 =
	 * PLAYER_CHATHEAD_MODEL 4 = Item_MODEL 5 = null
	 */
	private Model method206(int i, int j) {
		Model model = (Model) modelCache.insertFromCache((i << 16) + j);
		if (model != null)
			return model;
		if (i == 1)
			model = Model.method462(j);
		if (i == 2)
			model = NpcDefintion.forID(j).getModelHead();
		if (i == 3)
			model = RSClient.myPlayer.method453();
		if (i == 4)
			model = ItemDefinition.forID(j).method202(50);
		if (i == 5)
			model = null;
		if (model != null)
			modelCache.removeFromCache(model, (i << 16) + j);
		return model;
	}

	private static DirectImage loadSprite(int i, CacheArchive streamLoader, String s) {
		long l = (TextClass.method585(s) << 8) + (long) i;
		DirectImage sprite = (DirectImage) spriteCache.insertFromCache(l);
		if (sprite != null)
			return sprite;
		try {
			sprite = new DirectImage(streamLoader, s, i);
			spriteCache.removeFromCache(sprite, l);
		} catch (Exception _ex) {
			// if (i != 0 && s != null)
			//System.out.println("Missing: " + i + " " + s);
			return null;
		}
		return sprite;
	}
	
	protected static DirectImage imageLoader(int i, String s) {
		long l = (TextClass.method585(s) << 8) + (long) i;
		DirectImage sprite = (DirectImage) spriteCache.insertFromCache(l);
		if (sprite != null)
			return sprite;
		try {
			sprite = new DirectImage(s + " " + i);
			spriteCache.removeFromCache(sprite, l);
		} catch (Exception exception) {
			return null;
		}
		return sprite;
	}

	public void setDisabledSprite(String name, int id, CacheArchive archive) {
		try {
			disabledSpriteName = name;
			disabledSpriteId = id;
			disabledSprite = loadSprite(id, archive, name);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setEnabledSprite(String name, int id, CacheArchive archive) {
		enabledSpriteName = name;
		enabledSpriteId = id;
		enabledSprite = loadSprite(id, archive, name);
	}

	public void setSprites(int index, String name, int id, CacheArchive archive) {
		spriteNames[index] = name;
		spriteIds[index] = (short) id;
		sprites[index] = loadSprite(id, archive, name);
	}

	public static void method208(Model model) {
		modelCache.unlinkAll();
		if (model != null)
			modelCache.removeFromCache(model, (5 << 16));
	}

	public Model method209(int j, int k, boolean flag, RSInterface rsi) {
		Model model;
		if (flag)
			model = method206(enabledMediaType, enabledMediaID);
		else
			model = method206(mediaType, mediaID);
		if (model == null)
			return null;
		if (k == -1 && j == -1 && model.triangleColourOrTexture == null)
			return model;
		Model model_1 = new Model(true, Animation.method532(k) & Animation.method532(j), false, model);
		if (k != -1 || j != -1)
			model_1.method469();
		if (k != -1)
			model_1.method470(k);
		if (j != -1)
			model_1.method470(j);
		if (rsi.mediaID >= 18552 && rsi.mediaID <= 18562)
			model_1.light(86, 830, -5, -15, -5, true);
		else if (rsi.id == 298)
			model_1.light(37, 1418, -3, -10, -1, true);
		else if (rsi.parentID == 1151)
			model_1.light(64, 768, -1, -5, -5, true);
		else if (rsi.parentID == 1908 || rsi.parentID == 1689 || rsi.parentID == 1829)
			model_1.light(45, 850, 1, -10, 1, true);
		else if (rsi.id == 9277 || rsi.id == 3650)
			model_1.light(64, 768, -10, -100, -10, true);
		else
			model_1.light(64, 768, -50, -20, -50, true);
		return model_1;
	}

	public RSInterface() {
	}

	public static CacheArchive aClass44;
	public MagicSpell spell;
	public TextDrawingArea[] fonts;
	public DirectImage disabledSprite;
	public short duration;
	public int fontId;
	public DirectImage sprites[];
	public String[] spriteNames;
	public String disabledSpriteName;
	public int disabledSpriteId;
	public String enabledSpriteName;
	public int enabledSpriteId;
	public short[] spriteIds;
	public static RSInterface interfaceCache[];
	public int requiredValues[];
	public short contentType;
	public boolean isMagicButton;
	public short spritesX[];
	public int disabledTextHoverColor;
	public short atActionType;
	public String spellName;
	public int enabledTextColor;
	public short width;
	public String tooltip;
	public String selectedActionName;
	public boolean textCentered;
	public short scrollPosition;
	public String actions[];
	public int valueIndexArray[][];
	public boolean filled;
	public String enabledMessage;
	public short mouseOverPopupInterface;
	public short invSpritePadX;
	public int disabledTextColor;
	public short mediaType;
	public short mediaID;
	public boolean dragDeletes;
	public short parentID;
	public short spellUsableOn;
	private static MemoryCache spriteCache;
	public int enabledTextHoverColor;
	public short children[];
	public short childX[];
	public boolean usableItemInterface;
	public TextDrawingArea font;
	public short invSpritePadY;
	public int valueCompareType[];
	public short animFrame;
	public short spritesY[];
	public String disabledMessage;
	public boolean isInventoryInterface;
	public short id;
	public int invStackSizes[];
	public int inv[];
	public byte alpha;
	private short enabledMediaType;
	private short enabledMediaID;
	public short disabledAnimation;
	public short enabledAnimation;
	public boolean itemsSwappable;
	public DirectImage enabledSprite;
	public short scrollMax;
	public byte type;
	public short xOffset;
	public boolean is_used = false;
	public static final MemoryCache modelCache = new MemoryCache(30);
	public short yOffset;
	public boolean isMouseoverTriggered;
	public short height;
	public boolean textShadow;
	public short modelZoom;
	public short modelRotY;
	public short modelRotX;
	public short childY[];
}
