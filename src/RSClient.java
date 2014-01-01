import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Insets;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;
import java.util.zip.CRC32;

import com.google.gson.Gson;


/**
 * The main class of the application.
 * 
 * @author A Group of curious individuals.
 * 
 */
public class RSClient extends GameShell
{

    private static boolean widget = true;
    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getAnonymousLogger();
    
    private static final int CLIENT_FIXED = 0;
    
    private final int magic_value = 337;
    

    private boolean menuHasAddFriend(int idx)
    {
        if (idx < 0)
        {
            return false;
        }
        int k = menuActionID[idx];
        if (k >= 2000)
        {
            k -= 2000;
        }
        return k == 337;
    }

    public int returnGeneralInterfaceOffsetX()
    {
        return (int) (clientWidth <= 1006 ? (clientWidth / 2) - 256 - (clientWidth * 0.1) : (clientWidth / 2) - 256);
    }

    public short client_zoom = 0;

    /**
     * Resizable / Full-screen Engine stuff
     */
    public static int clientSize = 0;
    public static int clientWidth = 765;
    public static int clientHeight = 503;
    public static int REGULAR_WIDTH = 765, REGULAR_HEIGHT = 503, RESIZABLE_WIDTH = 800, RESIZABLE_HEIGHT = 600;

    public void toggleSize(int size)
    {
        if (size != clientSize)
        {
            // Update interface configs
            anIntArray1045[970] = (size == 0 ? 1 : 0);
            variousSettings[970] = (size == 0 ? 1 : 0);
            anIntArray1045[971] = (size == 1 ? 1 : 0);
            variousSettings[971] = (size == 1 ? 1 : 0);
            anIntArray1045[972] = (size == 2 ? 1 : 0);
            variousSettings[972] = (size == 2 ? 1 : 0);
            needDrawTabArea = true;

            super.clickMode2 = 0;

            if (size == 0)
            {
                clientWidth = REGULAR_WIDTH;
                clientHeight = REGULAR_HEIGHT;
                super.enter_screen_mode((byte) 0);
            }
            else if (size == 1)
            {
                if (super.getWidth() < RESIZABLE_WIDTH)
                {
                    clientWidth = RESIZABLE_WIDTH;
                }
                if (super.getHeight() < RESIZABLE_HEIGHT)
                {
                    clientHeight = RESIZABLE_HEIGHT;
                }
                super.enter_screen_mode((byte) 1);
            }
            else
            {
                super.enter_screen_mode((byte) 2);
            }
            if (size == 0)
            {
                needDrawTabArea = true;
                inputTaken = true;
            }

            // Update some interfaces, mainly walkable interfaces
            RSInterface rs2 = RSInterface.interfaceCache[11878];
            rs2.modelZoom = (short) (size == 0 ? 200 : 180);
            rs2.height = (short) (size == 0 ? 32 : 200);
            rs2.modelRotY = (short) (size == 0 ? 0 : 400);
            RSInterface rs3 = RSInterface.interfaceCache[11879];
            rs3.modelZoom = (short) (size == 0 ? 200 : 180);
            rs3.height = (short) (size == 0 ? 32 : 200);
            rs3.modelRotY = (short) (size == 0 ? 0 : 400);
            RSInterface rs4 = RSInterface.interfaceCache[11146];
            rs4.child(7, 11155, (size == 0 ? 470 : 360), (size == 0 ? 317 : 24));

            clientSize = size;
            updateGame();
        }
    }

    // Refresh the rasterizer bounds and 3d game area.
    public void updateGame()
    {
        System.err.println("Refreshing Game...");

        Rasterizer.method365(clientSize == CLIENT_FIXED ? REGULAR_WIDTH : clientWidth, clientSize == CLIENT_FIXED ? REGULAR_HEIGHT : clientHeight);
        fullScreenTextureArray = Rasterizer.lineOffsets;
        Rasterizer.method365(clientSize == CLIENT_FIXED ? 516 : clientWidth, clientSize == CLIENT_FIXED ? 165 : clientHeight);
        chatAreaTexture = Rasterizer.lineOffsets;
        Rasterizer.method365(clientSize == CLIENT_FIXED ? 249 : clientWidth, clientSize == CLIENT_FIXED ? 335 : clientHeight);
        tabAreaTexture = Rasterizer.lineOffsets;
        Rasterizer.method365(clientSize == CLIENT_FIXED ? 512 : clientWidth, clientSize == CLIENT_FIXED ? 334 : clientHeight);
        mainGameScreenTexture = Rasterizer.lineOffsets;

        int ai[] = new int[9];
        for (int i = 0; i < 9; i++)
        {
            int pitch = 128 + i * 32 + 15;
            int l8 = 600 + pitch * 3;
            int i9 = Rasterizer.sineTable[pitch];
            ai[i] = l8 * i9 >> 16;
        }

        SceneGraph.initViewport(500, 800, clientSize == CLIENT_FIXED ? 512 : clientWidth, clientSize == CLIENT_FIXED ? 334 : clientHeight, ai);

        gameScreenImageProducer = new RSImageProducer(clientSize == CLIENT_FIXED ? 512 : clientWidth, clientSize == CLIENT_FIXED ? 334 : clientHeight, getGameComponent());
        RSRaster.setAllPixelsToZero();
        resetImageProducers2();

        if (!loggedIn)
            resetAllImageProducers();
    }

    /**
     * Chat engine stuff chatTabMode[tab] = on 0/friends 1/off 2/hide 3;
     */
    private byte chatStoneHovered = -1;
    public boolean invHidden = false;
    public boolean chatHidden = false;
    public static String reportAbuseText = "Report Abuse";
    private byte[] chatTabMode =
    { 0, 0, 0, 0, 0, 0 };
    private byte[] chatStoneHoverState =
    { 0, -1, -1, -1, -1, -1 };
    private short[] chatBlueStoneFlash =
    { 0, 0, 0, 0, 0, 0 };

    /**
     * For resizable/full screen modes.
     */
    private void hideChat()
    {
        if (clientSize == CLIENT_FIXED)
            return;

        chatHidden = !chatHidden;

        if (chatHidden)
            for (int i = 0; i < 6; i++)
                if (chatStoneHoverState[i] != 2 && chatStoneHoverState[i] != 3)
                    chatStoneHoverState[i] = -1;
    }

    /**
     * Change chat mode TODO: Add clan chat
     */
    private void changeChatMode(int chatTab, int val)
    {
        if (chatTabMode[chatTab] == val)
            return;
        chatTabMode[chatTab] = (byte) val;
        stream.writeOpcode(95);
        stream.writeByte(chatTabMode[2]);
        stream.writeByte(chatTabMode[3]);
        stream.writeByte(chatTabMode[5]);
    }

    /**
     * Change chat tabs
     */
    private boolean changeActiveChatStoneState(int index)
    {
        if (chatStoneHoverState[index] == 0)
            return false;

        chatStoneHoverState[index] = 0;
        if (chatHidden)
            chatHidden = false;
        for (int i = 0; i < 6; i++)
        {
            if (i != index)
            {
                if ((chatStoneHoverState[i] != 2 && chatStoneHoverState[i] != 3) || index == 0 && i != 3 && (chatStoneHoverState[i] == 2 || chatStoneHoverState[i] == 3))
                    chatStoneHoverState[i] = -1;
            }
        }
        return true;
    }

    public void drawChannelButtons()
    {
        String text[] =
        { "On", "Friends", "Off", "Hide" };
        int textColor[] =
        { 65280, 0xffff00, 0xff0000, 65535 };
        smallText.method389(true, 25, 0xffffff, "All", 158 + (clientSize == CLIENT_FIXED ? 0 : clientHeight - 165));
        smallText.method389(true, 85, 0xffffff, "Game", 158 + (clientSize == CLIENT_FIXED ? 0 : clientHeight - 165));
        smallText.method389(true, 149, 0xffffff, "Public", 152 + (clientSize == CLIENT_FIXED ? 0 : clientHeight - 165));
        smallText.method389(true, 211, 0xffffff, "Private", 152 + (clientSize == CLIENT_FIXED ? 0 : clientHeight - 165));
        smallText.method389(true, 284, 0xffffff, "Clan", 152 + (clientSize == CLIENT_FIXED ? 0 : clientHeight - 165));
        smallText.method389(true, 348, 0xffffff, "Trade", 152 + (clientSize == CLIENT_FIXED ? 0 : clientHeight - 165));
        smallText.method382(textColor[chatTabMode[2]], 164, text[chatTabMode[2]], 162 + (clientSize == CLIENT_FIXED ? 0 : clientHeight - 165), true);
        smallText.method382(textColor[chatTabMode[3]], 230, text[chatTabMode[3]], 162 + (clientSize == CLIENT_FIXED ? 0 : clientHeight - 165), true);
        smallText.method382(textColor[chatTabMode[4]], 296, text[chatTabMode[4]], 162 + (clientSize == CLIENT_FIXED ? 0 : clientHeight - 165), true);
        smallText.method382(textColor[chatTabMode[5]], 362, text[chatTabMode[5]], 162 + (clientSize == CLIENT_FIXED ? 0 : clientHeight - 165), true);
        smallText.method389(true, screenDate ? 429 : 424, screenDate ? DATE_COLOURS[dateColour] : 0xffffff, reportAbuseText, screenDate ? 158 + (clientSize == CLIENT_FIXED ? 0 : clientHeight - 165) : 157 + (clientSize == CLIENT_FIXED ? 0 : clientHeight - 165));
    }

    public RSFont newBoldFont, newRegularFont, newSmallFont;

    // Chat tab engine..
    private void drawChatStones(int index, int x, int y)
    {

        // Report abuse button hover
        if (chatStoneHovered == 6 && index == 6)
        {
            chatStones[4].drawIndexedImage(x, y);
        }
        else if (index != 6)
        {

            switch (chatStoneHoverState[index])
            {
            case -1: // Idle state
                if (chatStoneHovered == index)
                    chatStones[0].drawIndexedImage(x, y);
                break;
            case 0: // Clicked
                if (chatStoneHovered == index)
                    chatStones[3].drawIndexedImage(x, y);
                else
                    chatStones[2].drawIndexedImage(x, y);
                break;
            case 2: // Blue tab blinking
                chatBlueStoneFlash[index]++;

                if (chatStoneHovered == index)
                    chatStones[0].drawIndexedImage(x, y);

                // Steady rate
                if (loopCycle % 61 <= 30 && chatBlueStoneFlash[index] <= 510)
                {
                    if (chatStoneHovered != index)
                        chatStones[1].drawIndexedImage(x, y);
                    inputTaken = true;
                }
                else if (loopCycle % 61 <= 30 && chatBlueStoneFlash[index] >= 510)
                {
                    if (chatStoneHovered != index)
                        chatStones[1].drawIndexedImage(x, y);
                    chatStoneHoverState[index] = 3;
                    chatBlueStoneFlash[index] = 0;
                    inputTaken = true;
                }
                break;
            case 3: // Blue tab still
                if (chatStoneHovered == index)
                    chatStones[0].drawIndexedImage(x, y);
                else
                    chatStones[1].drawIndexedImage(x, y);
                break;
            }
        }
    }

    // Chat tab engine..
    private void chatTabEngine()
    {
        if (clientSize != 0)
            chatArea[2].drawSprite(0, clientHeight - 23);
        drawChatStones(0, 5, 142 + (clientSize == CLIENT_FIXED ? 0 : clientHeight - 165));
        drawChatStones(1, 71, 142 + (clientSize == CLIENT_FIXED ? 0 : clientHeight - 165));
        drawChatStones(2, 137, 142 + (clientSize == CLIENT_FIXED ? 0 : clientHeight - 165));
        drawChatStones(3, 203, 142 + (clientSize == CLIENT_FIXED ? 0 : clientHeight - 165));
        drawChatStones(4, 269, 142 + (clientSize == CLIENT_FIXED ? 0 : clientHeight - 165));
        drawChatStones(5, 335, 142 + (clientSize == CLIENT_FIXED ? 0 : clientHeight - 165));
        drawChatStones(6, 404, 142 + (clientSize == CLIENT_FIXED ? 0 : clientHeight - 165));
        drawChannelButtons();
    }
    
    /**
     * Used to draw the chatbox area.
     */
    private void drawChatArea()
    {
        if (clientSize == CLIENT_FIXED)
            chatImageProducer.initDrawingArea();

        if (clientSize == CLIENT_FIXED)
            chatArea[0].drawSprite(0, 0);

        if (draw_sprites_logon)
            chatTabEngine();

        if (clientSize != 0 && chatHidden)
            return;
        Rasterizer.lineOffsets = chatAreaTexture;

        if (clientSize != CLIENT_FIXED)
            chatBox.drawAdvancedSprite(8, clientHeight - 158);
        
        if (draw_sprites_logon)
        {
            TextDrawingArea textDrawingArea = regularText;
            short inputDiaY = (short) (40 + 19 + 10 + (clientSize == CLIENT_FIXED ? 0 : clientHeight - 165));
            short inputDiaY2 = (short) (60 + 19 + 10 + (clientSize == CLIENT_FIXED ? 0 : clientHeight - 165));

            if (messagePromptRaised)
            {
            	chatFrame_fs.drawSpriteTrans(7, clientHeight - 158, 175);
                chatArea[1].drawSprite(1, clientHeight - 164);
                RSRaster.setDrawingArea(121 + (clientSize == CLIENT_FIXED ? 0 : clientHeight - 165), 8, 512, 8 + (clientSize == CLIENT_FIXED ? 0 : clientHeight - 165));
                chatText.drawText(0, inputTitle, inputDiaY, 239 + 17);
                chatText.drawText(128, promptInput + "*", inputDiaY2, 239 + 17);
                RSRaster.defaultDrawingAreaSize();
            }
            else if (inputDialogState == 1)
            {
            	chatFrame_fs.drawSpriteTrans(7, clientHeight - 158, 175);
                chatArea[1].drawSprite(1, clientHeight - 164);
                RSRaster.setDrawingArea(121 + (clientSize == CLIENT_FIXED ? 0 : clientHeight - 165), 8, 512, 8 + (clientSize == CLIENT_FIXED ? 0 : clientHeight - 165));
                chatText.drawText(0, "Enter amount:", inputDiaY, 239 + 17);
                chatText.drawText(128, amountOrNameInput + "*", inputDiaY2, 239 + 17);
                RSRaster.defaultDrawingAreaSize();
            }
            else if (inputDialogState == 2)
            {
            	chatFrame_fs.drawSpriteTrans(7, clientHeight - 158, 175);
                chatArea[1].drawSprite(1, clientHeight - 164);
                RSRaster.setDrawingArea(121 + (clientSize == CLIENT_FIXED ? 0 : clientHeight - 165), 8, 512, 8 + (clientSize == CLIENT_FIXED ? 0 : clientHeight - 165));
                chatText.drawText(0, "Enter name:", inputDiaY, 239 + 17);
                chatText.drawText(128, amountOrNameInput + "*", inputDiaY2, 239 + 17);
                RSRaster.defaultDrawingAreaSize();
            }
            else if (inputDialogState == 3)
            {
            	chatFrame_fs.drawSpriteTrans(7, clientHeight - 158, 175);
                chatArea[1].drawSprite(1, clientHeight - 164);
                RSRaster.setDrawingArea(121 + (clientSize == CLIENT_FIXED ? 0 : clientHeight - 165), 8, 512, 8 + (clientSize == CLIENT_FIXED ? 0 : clientHeight - 165));
                chatText.drawText(0, inputTitle, inputDiaY, 239 + 17);
                chatText.drawText(128, amountOrNameInput + "*", inputDiaY2, 239 + 17);
                RSRaster.defaultDrawingAreaSize();
            }
            else if (aString844 != null)
            {
            	chatFrame_fs.drawSpriteTrans(7, clientHeight - 158, 175);
                chatArea[1].drawSprite(1, clientHeight - 164);
                RSRaster.setDrawingArea(121 + (clientSize == CLIENT_FIXED ? 0 : clientHeight - 165), 8, 512, 8 + (clientSize == CLIENT_FIXED ? 0 : clientHeight - 165));
                chatText.drawText(0, aString844, inputDiaY, 239 + 17);
                chatText.drawText(128, "Click to continue", inputDiaY2, 239 + 17);
                RSRaster.defaultDrawingAreaSize();
            }
            else if (backDialogID != -1)
            {
                if (clientSize == CLIENT_FIXED)
                {
                    drawInterface(0, 20, RSInterface.interfaceCache[backDialogID], 24 + (clientSize == CLIENT_FIXED ? 0 : clientHeight - 165), true);
                }
                else
                {
                	chatFrame_fs.drawSpriteTrans(7, clientHeight - 158, 175);
                    chatArea[1].drawSprite(1, clientHeight - 164);
                    drawInterface(0, 20, RSInterface.interfaceCache[backDialogID], 24 + (clientSize == CLIENT_FIXED ? 0 : clientHeight - 165), true);
                }
            }
            else if (dialogID != -1)
            {
                if (clientSize == CLIENT_FIXED)
                {
                    drawInterface(0, 20, RSInterface.interfaceCache[dialogID], 24 + (clientSize == CLIENT_FIXED ? 0 : clientHeight - 165), true);
                }
                else
                {
                	chatFrame_fs.drawSpriteTrans(7, clientHeight - 158, 175);
                    chatArea[1].drawSprite(1, clientHeight - 164);
                    drawInterface(0, 20, RSInterface.interfaceCache[dialogID], 24 + (clientSize == CLIENT_FIXED ? 0 : clientHeight - 165), true);
                }
            }
            else
            {
                int amountOfMessages = 0;
                chatTabEngine();
                if (clientSize == CLIENT_FIXED)
                    RSRaster.setDrawingArea(121, 0, 494, 8);
                else
                    RSRaster.setDrawingArea(121 + (clientSize == CLIENT_FIXED ? 0 : clientHeight - 165), 0, 494, 8 + (clientSize == CLIENT_FIXED ? 0 : clientHeight - 165));
                for (int k = 0; k < 100; k++)
                    if (chatMessages[k] != null)
                    {
                        byte playerCrown = 0;
                        int l = chatTypes[k];
                        int i1 = (114 - amountOfMessages * 14) + 3 + chatScrollPos;
                        String s1 = chatNames[k];
                        if (s1 != null && s1.startsWith("@cr1@"))
                        {
                            s1 = s1.substring(5);
                            playerCrown = 1;
                        }
                        if (s1 != null && s1.startsWith("@cr2@"))
                        {
                            s1 = s1.substring(5);
                            playerCrown = 2;
                        }
                        if (s1 != null && s1.startsWith("@cr3@"))
                        {
                            s1 = s1.substring(5);
                            playerCrown = 3;
                        }

                        // Game messages
                        if (l == 0 && (chatStoneHoverState[0] == 0 || chatStoneHoverState[1] == 0))
                        {
                            if (i1 > 0 && i1 < 130)
                                newRegularFont.drawBasicString(chatMessages[k], 11, i1 + (clientSize == CLIENT_FIXED ? 0 : clientHeight - 165), clientSize == CLIENT_FIXED ? 0 : 0xffffff, clientSize == CLIENT_FIXED ? -1 : 0);
                            amountOfMessages++;
                        }

                        // Public messages
                        if ((l == 1 || l == 2) && (chatStoneHoverState[0] == 0 || chatStoneHoverState[2] == 0) && (l == 1 || chatTabMode[2] == 0 || chatTabMode[2] == 1 && isFriendOrSelf(s1)))
                        {
                            if (i1 > 0 && i1 < 130)
                            {
                                int j1 = 11;
                                if (playerCrown != 0)
                                {
                                    modIcons[playerCrown - 1].drawSprite(j1, i1 - 12 + (clientSize == CLIENT_FIXED ? 0 : clientHeight - 165));
                                    j1 += 14;
                                }
                                //Messages already sent
                                if (clientSize == CLIENT_FIXED) {
                                    textDrawingArea.method385(0, s1 + ":", i1 + (clientSize == CLIENT_FIXED ? 0 : clientHeight - 165), j1);

                                } else {
                                    newRegularFont.drawBasicString(s1 + ":", j1, i1 + (clientSize == CLIENT_FIXED ? 0 : clientHeight - 165), clientSize == CLIENT_FIXED ? 0 : 0xffffff, clientSize == CLIENT_FIXED ? -1 : 0);

                                }
                                j1 += textDrawingArea.getTextWidth(s1) + 6;
                                //Typing message
                                if (clientSize == CLIENT_FIXED) {
                                    textDrawingArea.method385(255, chatMessages[k], i1 + (clientSize == CLIENT_FIXED ? 0 : clientHeight - 165), j1);
                                } else {
                                	newRegularFont.drawBasicString(chatMessages[k], j1, i1 + (clientSize == CLIENT_FIXED ? 0 : clientHeight - 165), clientSize == CLIENT_FIXED ? 0 : 0x7FA9FF, clientSize == CLIENT_FIXED ? 0x0000FF : 0);
                                }
                            }
                            amountOfMessages++;
                        }

                        // Split private messages from
                        if ((l == 3 || l == 7) && (chatStoneHoverState[3] == 0) && (l == 7 || chatTabMode[3] == 0 || chatTabMode[3] == 1 && isFriendOrSelf(s1)))
                        {
                            if (i1 > 0 && i1 < 130)
                            {
                                int k1 = 11;
                                textDrawingArea.method385(0, "From", i1 + (clientSize == CLIENT_FIXED ? 0 : clientHeight - 165), k1);
                                k1 += textDrawingArea.getTextWidth("From ");
                                if (playerCrown != 0)
                                {
                                    modIcons[playerCrown - 1].drawSprite(k1, i1 - 12 + (clientSize == CLIENT_FIXED ? 0 : clientHeight - 165));
                                    k1 += 14;
                                }
                                textDrawingArea.method385(0, s1 + ":", i1 + (clientSize == CLIENT_FIXED ? 0 : clientHeight - 165), k1);
                                k1 += textDrawingArea.getTextWidth(s1) + 6;
                                textDrawingArea.method385(0x800000, chatMessages[k], i1 + (clientSize == CLIENT_FIXED ? 0 : clientHeight - 165), k1);
                            }
                            amountOfMessages++;
                        }
                        // Trade messages
                        if (l == 4 && (chatStoneHoverState[0] == 0 || chatStoneHoverState[5] == 0) && (chatTabMode[5] == 0 || chatTabMode[5] == 1 && isFriendOrSelf(s1)))
                        {
                            if (i1 > 0 && i1 < 130) {
                            	if (clientSize == CLIENT_FIXED) {
                            		textDrawingArea.method385(0x800080, s1 + " " + chatMessages[k], i1 + (clientSize == CLIENT_FIXED ? 0 : clientHeight - 165), 11);
                            	} else {
                                    newRegularFont.drawBasicString(s1 + " " + chatMessages[k], 11, i1 + (clientSize == CLIENT_FIXED ? 0 : clientHeight - 165), 0xFF00FF, 0);
                            	}
                            }
                            amountOfMessages++;
                        }
                        // Split private - friend logged in?
                        if (l == 5 && (chatStoneHoverState[3] == 0) && chatTabMode[3] < 2)
                        {
                            if (i1 > 0 && i1 < 130)
                                textDrawingArea.method385(0x800000, chatMessages[k], i1 + (clientSize == CLIENT_FIXED ? 0 : clientHeight - 165), 11);
                            amountOfMessages++;
                        }
                        // Split private messages to
                        if (l == 6 && (chatStoneHoverState[3] == 0) && chatTabMode[3] < 2)
                        {
                            if (i1 > 0 && i1 < 130)
                            {
                                textDrawingArea.method385(0, "To " + s1 + ":", i1 + (clientSize == CLIENT_FIXED ? 0 : clientHeight - 165), 11);
                                textDrawingArea.method385(0x800000, chatMessages[k], i1 + (clientSize == CLIENT_FIXED ? 0 : clientHeight - 165), 17 + textDrawingArea.getTextWidth("To " + s1));
                            }
                            amountOfMessages++;
                        }
                        // Duel messages
                        if (l == 8 && (chatStoneHoverState[0] == 0 || chatStoneHoverState[5] == 0) && (chatTabMode[5] == 0 || chatTabMode[5] == 1 && isFriendOrSelf(s1)))
                        {
                            if (i1 > 0 && i1 < 130)
                                textDrawingArea.method385(0x7e3200, s1 + " " + chatMessages[k], i1 + (clientSize == CLIENT_FIXED ? 0 : clientHeight - 165), 11);
                            amountOfMessages++;
                        }
                    }
                RSRaster.defaultDrawingAreaSize();
                chatScrollMax = amountOfMessages * 14 + 7 + 5;
                if (chatScrollMax < 115) {
                	chatScrollMax = 115;
                }
                if (clientSize == CLIENT_FIXED) {
                    drawScrollbar_chat(114, chatScrollMax - chatScrollPos - 114, 7 + (clientSize == CLIENT_FIXED ? 0 : clientHeight - 165), 496, chatScrollMax);
                } else {
                	drawTransScrollbar(114, chatScrollMax - chatScrollPos - 113, 7 + (clientSize == CLIENT_FIXED ? 0 : clientHeight - 165), 503, chatScrollMax, false, clientSize != 0);
                }
                String s;
                if (myPlayer != null && myPlayer.name != null)
                    s = myPlayer.name;
                else
                    s = TextClass.fixName(capitalize(myUsername));
                if (clientSize == CLIENT_FIXED)
                    RSRaster.setDrawingArea(500, 0, 511, 22);
                else
                    RSRaster.setDrawingArea(500 + (clientSize == CLIENT_FIXED ? 0 : clientHeight - 165), 0, 511, 22 + (clientSize == CLIENT_FIXED ? 0 : clientHeight - 165));
                newRegularFont.drawBasicString(s + ": ", 11, 133 + (clientSize == CLIENT_FIXED ? 0 : clientHeight - 165), clientSize == CLIENT_FIXED ? 0 : 0xffffff, clientSize == CLIENT_FIXED ? -1 : 0);
                newRegularFont.drawBasicString(inputString + "*", 11 + textDrawingArea.getTextWidth(s + ": "), 133 + (clientSize == CLIENT_FIXED ? 0 : clientHeight - 165), clientSize == CLIENT_FIXED ? 0 : 0x7FA9FF, clientSize == CLIENT_FIXED ? -1 : 0);
                if (clientSize == CLIENT_FIXED)
                	textDrawingArea.method385(255, inputString + "*", 133 + (clientSize == CLIENT_FIXED ? 0 : clientHeight - 165), 11 + textDrawingArea.getTextWidth(s + ": "));
                RSRaster.defaultDrawingAreaSize();
                if (clientSize == CLIENT_FIXED) //Brown line thingy
                	RSRaster.method339(121 + (clientSize == CLIENT_FIXED ? 0 : clientHeight - 165), 0x807660, 504, 8);
            }
        }
        if (menuOpen && clientSize == CLIENT_FIXED)
            drawMenu(0, 338);
        if (clientSize == CLIENT_FIXED)
        {
            chatImageProducer.drawGraphics(338, super.graphics, 0);
            gameScreenImageProducer.initDrawingArea();
        }
        Rasterizer.lineOffsets = mainGameScreenTexture;
    }
    
    // TODO This is 100% needed for web applet!
    public void init()
    {
        try
        {
            System.err.println("CALLED");
            nodeID = 10;
            portOff = 0;
            setHighMem();
            new Signlink();
            init_client(clientHeight, clientWidth);
            instance = this;
        }
        catch (Exception exception)
        {
            return;
        }
    }

    public void startRunnable(Runnable runnable, int i)
    {
        if (i > 10)
            i = 10;
        super.startRunnable(runnable, i);
    }

    public Socket openSocket(int port) throws IOException
    {
        return new Socket(InetAddress.getByName(server), port);
    }

    private void updateStats()
    {
        int[] hovers =
        { 4040, 4076, 4112, 4046, 4082, 4118, 4052, 4088, 4124, 4058, 4094, 4130, 4064, 4100, 4136, 4070, 4106, 4142, 4160, 2832, 13917, 4562 };
        for (int j5 = 0; j5 < hovers.length; j5++)
        {
            RSInterface class9_6 = RSInterface.interfaceCache[hovers[j5]];
            class9_6.disabledMessage = setMessage(j5);
        }
    }

    public boolean processMenuClick()
    {
        if (activeInterfaceType != 0)
            return false;
        int j = super.clickMode3;

        if (menuOpen)
        {
            if (j != 1)
            {
                int k = super.mouseX;
                int j1 = super.mouseY;
                if (k < menuOffsetX - 10 || k > menuOffsetX + menuWidth + 10 || j1 < menuOffsetY - 10 || j1 > menuOffsetY + menuHeight + 10)
                {
                    menuOpen = false;
                }
            }
            if (j == 1)
            {
                int l = menuOffsetX;
                int k1 = menuOffsetY;
                int i2 = menuWidth;
                int k2 = super.saveClickX;
                int l2 = super.saveClickY;
                int i3 = -1;
                for (int j3 = 0; j3 < menuActionRow; j3++)
                {
                    int k3 = k1 + (clientSize == CLIENT_FIXED ? 31 : 27) + (menuActionRow - 1 - j3) * 15;
                    if (k2 > l + 4 && k2 < l + i2 + 4 && l2 > k3 - 9 && l2 < k3 + 7)
                        i3 = j3;
                }
                if (i3 != -1)
                    doAction(i3);
                menuOpen = false;
            }
            return true;
        }
        else
        {
            if (j == 1 && menuActionRow > 0)
            {
                int i1 = menuActionID[menuActionRow - 1];
                if (i1 == 632 || i1 == 78 || i1 == 867 || i1 == 431 || i1 == 53 || i1 == 74 || i1 == 454 || i1 == 539 || i1 == 493 || i1 == 847 || i1 == 447 || i1 == 1125)
                {
                    int l1 = menuActionCmd2[menuActionRow - 1];
                    int j2 = menuActionCmd3[menuActionRow - 1];
                    RSInterface class9 = RSInterface.interfaceCache[j2];
                    if (class9.itemsSwappable || class9.dragDeletes)
                    {
                        aBoolean1242 = false;
                        anInt989 = 0;
                        anInt1084 = j2;
                        anInt1085 = l1;
                        activeInterfaceType = 2;
                        anInt1087 = super.saveClickX;
                        anInt1088 = super.saveClickY;
                        if (RSInterface.interfaceCache[j2].parentID == openInterfaceID)
                            activeInterfaceType = 1;
                        if (RSInterface.interfaceCache[j2].parentID == backDialogID)
                            activeInterfaceType = 3;
                        return true;
                    }
                }
            }
            if (j == 1 && (mouse_buttons == 1 || menuHasAddFriend(menuActionRow - 1)) && menuActionRow > 2)
                j = 2;
            if (j == 1 && menuActionRow > 0)
                doAction(menuActionRow - 1);
            if (j == 2 && menuActionRow > 0)
                determineMenuSize();
            return false;
        }
    }

    // TODO: COME HERE FIX MAPS!
    public void method22()
    {
        try
        {
            anInt985 = -1;
            stillGraphicDeque.removeAll();
            aClass19_1013.removeAll();
            Rasterizer.method366();
            unlinkMRUNodes();
            sceneGraph.initToNull();
            if (music_enabled)
            {
                SoundProvider.getInstance().fadeMidi(true);
            }
            System.gc();
            for (int i = 0; i < 4; i++)
            {
                collision_maps[i].reset();
            }
            for (int l = 0; l < 4; l++)
            {
                for (int k1 = 0; k1 < 104; k1++)
                {
                    for (int j2 = 0; j2 < 104; j2++)
                    {
                        byteGroundArray[l][k1][j2] = 0;
                    }
                }
            }
            Region objectManager = new Region(byteGroundArray, intGroundArray);
            int k2 = aByteArrayArray1183.length;
            stream.writeOpcode(0);
            if (!aBoolean1159)
            {
                for (int i3 = 0; i3 < k2; i3++)
                {
                    int i4 = (anIntArray1234[i3] >> 8) * 64 - baseX;
                    int k5 = (anIntArray1234[i3] & 0xff) * 64 - baseY;
                    byte abyte0[] = aByteArrayArray1183[i3];
                    logger.info("Floormap: " + anIntArray1235[i3]);
                    // FileOperations.WriteFile(signlink.cacheLocation() +
                    // "./dump/maps/" + anIntArray1235[i3]
                    // + ".dat", abyte0);
                    /*
                     * if (FileOperations.FileExists(signlink.cacheLocation() +
                     * "./maps/" + anIntArray1235[i3] + ".dat")) abyte0 =
                     * FileOperations.ReadFile(signlink.cacheLocation() +
                     * "./maps/" + anIntArray1235[i3] + ".dat");
                     */
                    if (abyte0 != null)
                        objectManager.loadTerrainBlock(abyte0, k5, i4, (anInt1069 - 6) * 8, (anInt1070 - 6) * 8, collision_maps);
                }
                for (int j4 = 0; j4 < k2; j4++)
                {
                    logger.info("Pos: " + anIntArray1234[j4]);
                }
                stream.writeOpcode(0);
                for (int i6 = 0; i6 < k2; i6++)
                {
                    byte abyte1[] = aByteArrayArray1247[i6];
                    // FileOperations.WriteFile(signlink.cacheLocation() +
                    // "./dump/maps/" + anIntArray1236[i6]
                    // + ".dat", abyte1);
                    /*
                     * if (FileOperations.FileExists(signlink.cacheLocation() +
                     * "./maps/" + anIntArray1236[i6] + ".dat")) abyte1 =
                     * FileOperations.ReadFile(signlink.cacheLocation() +
                     * "./maps/" + anIntArray1236[i6] + ".dat");
                     */
                    logger.info("Objectmap: " + anIntArray1236[i6]);
                    if (abyte1 != null)
                    {
                        int l8 = (anIntArray1234[i6] >> 8) * 64 - baseX;
                        int k9 = (anIntArray1234[i6] & 0xff) * 64 - baseY;
                        objectManager.method190(l8, collision_maps, k9, sceneGraph, abyte1);
                    }
                }
            }
            if (aBoolean1159)
            {
                for (int j3 = 0; j3 < 4; j3++)
                {
                    for (int k4 = 0; k4 < 13; k4++)
                    {
                        for (int j6 = 0; j6 < 13; j6++)
                        {
                            int l7 = anIntArrayArrayArray1129[j3][k4][j6];
                            if (l7 != -1)
                            {
                                int i9 = l7 >> 24 & 3;
                                int l9 = l7 >> 1 & 3;
                                int j10 = l7 >> 14 & 0x3ff;
                                int l10 = l7 >> 3 & 0x7ff;
                                int j11 = (j10 / 8 << 8) + l10 / 8;
                                for (int l11 = 0; l11 < anIntArray1234.length; l11++)
                                {
                                    if (anIntArray1234[l11] != j11 || aByteArrayArray1183[l11] == null)
                                        continue;
                                    objectManager.loadTerrainSubBlock(i9, l9, collision_maps, k4 * 8, (j10 & 7) * 8, aByteArrayArray1183[l11], (l10 & 7) * 8, j3, j6 * 8);
                                    break;
                                }
                            }
                        }
                    }
                }
                for (int l4 = 0; l4 < 13; l4++)
                {
                    for (int k6 = 0; k6 < 13; k6++)
                    {
                        int i8 = anIntArrayArrayArray1129[0][l4][k6];
                        if (i8 == -1)
                            objectManager.clearRegion(k6 * 8, 8, 8, l4 * 8);
                    }
                }
                stream.writeOpcode(0);
                for (int l6 = 0; l6 < 4; l6++)
                {
                    for (int j8 = 0; j8 < 13; j8++)
                    {
                        for (int j9 = 0; j9 < 13; j9++)
                        {
                            int i10 = anIntArrayArrayArray1129[l6][j8][j9];
                            if (i10 != -1)
                            {
                                int k10 = i10 >> 24 & 3;
                                int i11 = i10 >> 1 & 3;
                                int k11 = i10 >> 14 & 0x3ff;
                                int i12 = i10 >> 3 & 0x7ff;
                                int j12 = (k11 / 8 << 8) + i12 / 8;
                                for (int k12 = 0; k12 < anIntArray1234.length; k12++)
                                {
                                    if (anIntArray1234[k12] != j12 || aByteArrayArray1247[k12] == null)
                                        continue;
                                    objectManager.loadObjectBlock(collision_maps, sceneGraph, k10, j8 * 8, (i12 & 7) * 8, l6, aByteArrayArray1247[k12], (k11 & 7) * 8, i11, j9 * 8);
                                    break;
                                }
                            }
                        }
                    }
                }
            }
            stream.writeOpcode(0);
            objectManager.createRegionScene(collision_maps, sceneGraph);
            gameScreenImageProducer.initDrawingArea();
            stream.writeOpcode(0);
            int k3 = Region.lowestPlane;
            if (k3 > floor_level)
                k3 = floor_level;
            if (k3 < floor_level - 1)
                k3 = floor_level - 1;
            sceneGraph.setHeightLevel(0);
            for (int i5 = 0; i5 < 104; i5++)
            {
                for (int i7 = 0; i7 < 104; i7++)
                    spawnGroundItem(i5, i7);
            }
            method63();
        }
        catch (Exception exception)
        {
        }
        ObjectDefinition.mruNodes1.unlinkAll();
        /*
         * if (super.gameFrame != null) { stream.writeOpcode(210);
         * stream.writeInt(0x3f008edd); }
         */
        System.gc();
        Rasterizer.method367();
        resourceProvider.method566();
        int k = (anInt1069 - 6) / 8 - 1;
        int j1 = (anInt1069 + 6) / 8 + 1;
        int i2 = (anInt1070 - 6) / 8 - 1;
        int l2 = (anInt1070 + 6) / 8 + 1;
        if (aBoolean1141)
        {
            k = 49;
            j1 = 50;
            i2 = 49;
            l2 = 50;
        }
        for (int l3 = k; l3 <= j1; l3++)
        {
            for (int j5 = i2; j5 <= l2; j5++)
            {
                if (l3 == k || l3 == j1 || j5 == i2 || j5 == l2)
                {
                    int j7 = resourceProvider.method562(0, j5, l3);
                    if (j7 != -1)
                        resourceProvider.method560(j7, 3);
                    int k8 = resourceProvider.method562(1, j5, l3);
                    if (k8 != -1)
                        resourceProvider.method560(k8, 3);
                }
            }
        }
    }

    public void unlinkMRUNodes()
    {
        ObjectDefinition.mruNodes1.unlinkAll();
        ObjectDefinition.mruNodes2.unlinkAll();
        NpcDefintion.memCache.unlinkAll();
        ItemDefinition.mruNodes2.unlinkAll();
        ItemDefinition.mruNodes1.unlinkAll();
        Player.mruNodes.unlinkAll();
        Graphic.aMRUNodes_415.unlinkAll();
    }

    public void renderMapScene(int i)
    {
        int ai[] = miniMap.myPixels;
        int j = ai.length;
        for (int k = 0; k < j; k++)
            ai[k] = 0;
        for (int l = 1; l < 103; l++)
        {
            int i1 = 24628 + (103 - l) * 512 * 4;
            for (int k1 = 1; k1 < 103; k1++)
            {
                if ((byteGroundArray[i][k1][l] & 0x18) == 0)
                    sceneGraph.drawMinimapTile(ai, i1, i, k1, l);
                if (i < 3 && (byteGroundArray[i + 1][k1][l] & 8) != 0)
                    sceneGraph.drawMinimapTile(ai, i1, i + 1, k1, l);
                i1 += 4;
            }
        }
        int j1 = 0xffffff;
        int l1 = 0xee0000;
        miniMap.method343();
        for (int i2 = 1; i2 < 103; i2++)
        {
            for (int j2 = 1; j2 < 103; j2++)
            {
                if ((byteGroundArray[i][j2][i2] & 0x18) == 0)
                    drawMapOutline(i2, j1, j2, l1, i);
                if (i < 3 && (byteGroundArray[i + 1][j2][i2] & 8) != 0)
                    drawMapOutline(i2, j1, j2, l1, i + 1);
            }
        }
        gameScreenImageProducer.initDrawingArea();
        anInt1071 = 0;
        for (int k2 = 0; k2 < 104; k2++)
        {
            for (int l2 = 0; l2 < 104; l2++)
            {
                int i3 = sceneGraph.getGroundDecortionUID(floor_level, k2, l2);
                if (i3 != 0)
                {
                    i3 = i3 >> 14 & 0x7fff;
                    int j3 = ObjectDefinition.forID(i3).mapIcon;
                    if (j3 >= 0)
                    {
                        int k3 = k2;
                        int l3 = l2;
                        if (j3 != 22 && j3 != 29 && j3 != 34 && j3 != 36 && j3 != 46 && j3 != 47 && j3 != 48)
                        {
                        }
                        aClass30_Sub2_Sub1_Sub1Array1140[anInt1071] = mapFunctions[j3];
                        anIntArray1072[anInt1071] = k3;
                        anIntArray1073[anInt1071] = l3;
                        anInt1071++;
                    }
                }
            }
        }
        // TODO: Use this to dump image's.
        /*
         * File directory = new File(signlink.cacheLocation() +
         * "MapImageDumps/"); if (!directory.exists()) { directory.mkdir(); }
         * BufferedImage bufferedimage = new BufferedImage(miniMap.myWidth,
         * miniMap.myHeight, 1); bufferedimage.setRGB(0, 0, miniMap.myWidth,
         * miniMap.myHeight, miniMap.myPixels, 0, miniMap.myWidth); Graphics2D
         * graphics2d = bufferedimage.createGraphics(); graphics2d.dispose();
         * try { File file1 = new File(signlink.cacheLocation() +
         * "MapImageDumps/" + (directory.listFiles().length + 1) + ".png");
         * ImageIO.write(bufferedimage, "png", file1); } catch (Exception e) {
         * e.printStackTrace(); }
         */
    }

    public void createMenu1Option(int id, String text)
    {
        menuActionName[1] = text;
        menuActionID[1] = id;
        menuActionRow++;
    }

    public void createMenu4Options(int i, int i1, int i2, int i3, String s, String s1, String s2, String s3)
    {
        menuActionName[4] = s;
        menuActionID[4] = i;
        menuActionRow++;
        menuActionName[3] = s1;
        menuActionID[3] = i1;
        menuActionRow++;
        menuActionName[2] = s2;
        menuActionID[2] = i2;
        menuActionRow++;
        menuActionName[1] = s3;
        menuActionID[1] = i3;
        menuActionRow++;
    }

    public void createMenu5Options(int i, int i1, int i2, int i3, int i4, String s, String s1, String s2, String s3, String s4)
    {
        menuActionName[5] = s;
        menuActionID[5] = i;
        menuActionRow++;
        menuActionName[4] = s1;
        menuActionID[4] = i1;
        menuActionRow++;
        menuActionName[3] = s2;
        menuActionID[3] = i2;
        menuActionRow++;
        menuActionName[2] = s3;
        menuActionID[2] = i3;
        menuActionRow++;
        menuActionName[1] = s4;
        menuActionID[1] = i4;
        menuActionRow++;
    }

    public void spawnGroundItem(int i, int j)
    {
        Deque class19 = groundArray[floor_level][i][j];
        if (class19 == null)
        {
            sceneGraph.removeGroundItemTile(floor_level, i, j);
            return;
        }
        int k = 0xfa0a1f01;
        Object obj = null;
        for (Item item = (Item) class19.reverseGetFirst(); item != null; item = (Item) class19.reverseGetNext())
        {
            ItemDefinition itemDef = ItemDefinition.forID(item.ID);
            int l = itemDef.value;
            if (itemDef.stackable)
                l *= item.item_count + 1;
            if (l > k)
            {
                k = l;
                obj = item;
            }
        }
        class19.insertTail(((Node) (obj)));
        Object obj1 = null;
        Object obj2 = null;
        for (Item class30_sub2_sub4_sub2_1 = (Item) class19.reverseGetFirst(); class30_sub2_sub4_sub2_1 != null; class30_sub2_sub4_sub2_1 = (Item) class19.reverseGetNext())
        {
            if (class30_sub2_sub4_sub2_1.ID != ((Item) (obj)).ID && obj1 == null)
                obj1 = class30_sub2_sub4_sub2_1;
            if (class30_sub2_sub4_sub2_1.ID != ((Item) (obj)).ID && class30_sub2_sub4_sub2_1.ID != ((Item) (obj1)).ID && obj2 == null)
                obj2 = class30_sub2_sub4_sub2_1;
        }
        int i1 = i + (j << 7) + 0x60000000;
        sceneGraph.addGroundItemTile(i, i1, ((Renderable) (obj1)), method42(floor_level, j * 128 + 64, i * 128 + 64), ((Renderable) (obj2)), ((Renderable) (obj)), floor_level, j);
    }

    public void method26(boolean flag)
    {
        for (int j = 0; j < npcCount; j++)
        {
            NPC npc = npcArray[npcIndices[j]];
            int k = 0x20000000 + (npcIndices[j] << 14);
            if (npc == null || !npc.isVisible() || npc.desc.aBoolean93 != flag)
                continue;
            int l = npc.x >> 7;
            int i1 = npc.y >> 7;
            if (l < 0 || l >= 104 || i1 < 0 || i1 >= 104)
                continue;
            if (npc.anInt1540 == 1 && (npc.x & 0x7f) == 64 && (npc.y & 0x7f) == 64)
            {
                if (anIntArrayArray929[l][i1] == anInt1265)
                    continue;
                anIntArrayArray929[l][i1] = anInt1265;
            }
            if (!npc.desc.aBoolean84)
                k += 0x80000000;
            sceneGraph.addRenderableA(floor_level, npc.anInt1552, method42(floor_level, npc.y, npc.x), k, npc.y, (npc.anInt1540 - 1) * 64 + 60, npc.x, npc, npc.aBoolean1541);
        }
    }

    private static boolean roofRemove = false;
    public static boolean themeMusic = false;
    private static boolean enterOnLogin = true;
    public static boolean lowMemory = false;
    private static final byte user_settings_version = 1;

    public static void writeSettings() throws IOException
    {
        DataOutputStream out = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(Signlink.cacheLocation() + "user_settings.dat")));
        out.writeByte(user_settings_version);
        out.writeByte(roofRemove ? 1 : 0);
        out.writeByte(themeMusic ? 1 : 0);
        out.writeByte(pictureCount);
        out.writeUTF(pictureFileName);
        out.writeByte(DATE_LOCAL);
        out.writeByte(useDate ? 1 : 0);
        out.writeByte(pictureFormatQuality);
        out.writeByte(dateColour);
        out.writeByte(pictureRegionID);
        out.writeByte(enterOnLogin ? 1 : 0);
        out.writeByte(lowMemory ? 1 : 0);
        if (!pictureFileName.equals(getDate(DATE_LOCAL) + "_") && useDate)
        {
            pictureFileName = getDate(DATE_LOCAL) + "_";
            pictureCount = 0;
        }
        out.close();
        logger.info("Settings successfully saved.");
    }

    public static void readSettings() throws IOException
    {
        DataInputStream in = new DataInputStream(new BufferedInputStream(new FileInputStream(Signlink.cacheLocation() + "user_settings.dat")));
        if (in.readByte() != user_settings_version)
        { // Version check..
            new File(Signlink.cacheLocation() + "user_settings.dat").delete();
            return;
        }
        roofRemove = in.readByte() == 1 ? true : false;
        themeMusic = in.readByte() == 1 ? true : false;
        pictureCount = in.readByte();
        pictureFileName = in.readUTF();
        DATE_LOCAL = in.readByte();
        useDate = in.readByte() == 1 ? true : false;
        pictureFormatQuality = in.readByte();
        dateColour = in.readByte();
        pictureRegionID = in.readByte();
        enterOnLogin = in.readByte() == 1 ? true : false;
        lowMemory = in.readByte() == 1 ? true : false;
        if (!pictureFileName.equals(getDate(DATE_LOCAL) + "_") && useDate)
        {
            pictureFileName = getDate(DATE_LOCAL) + "_";
            pictureCount = 0;
        }
        in.close();
        logger.info("Settings successfully loaded.");
    }

    /* 
     * Draws hover text
     * 
     * Called repeatedly with mouse position and current interface position,
     * method returns out if mouse pos is not within bounds.
     * 
     *  -Monster
     */
    public void buildInterfaceMenu(int rsiX, RSInterface rsInterface, int mouseX, int rsiY, int mouseY, int scrollPosition)
    {
        if (rsInterface.type != 0 || rsInterface.children == null || rsInterface.isMouseoverTriggered)
            return;
        if (mouseX < rsiX || mouseY < rsiY || mouseX > rsiX + rsInterface.width || mouseY > rsiY + rsInterface.height)
            return;
        int rsiChildren = rsInterface.children.length;
        for (int l1 = 0; l1 < rsiChildren; l1++)
        {
            int rsiChild_x = rsInterface.childX[l1] + rsiX;
            int rsiChild_y = (rsInterface.childY[l1] + rsiY) - scrollPosition;
            RSInterface rsiChild = RSInterface.interfaceCache[rsInterface.children[l1]];
            rsiChild_x += rsiChild.xOffset;
            rsiChild_y += rsiChild.yOffset;
            if ((rsiChild.mouseOverPopupInterface >= 0 || rsiChild.disabledTextHoverColor != 0) && mouseX >= rsiChild_x && mouseY >= rsiChild_y && mouseX < rsiChild_x + rsiChild.width && mouseY < rsiChild_y + rsiChild.height)
                if (rsiChild.mouseOverPopupInterface >= 0)
                    anInt886 = rsiChild.mouseOverPopupInterface;
                else
                    anInt886 = rsiChild.id;
            if (rsiChild.type == 8 && mouseX >= rsiChild_x && mouseY >= rsiChild_y && mouseX < rsiChild_x + rsiChild.width && mouseY < rsiChild_y + rsiChild.height)
            {
                anInt1315 = rsiChild.id;
            }
            if (rsiChild.type == 0)
            {
                buildInterfaceMenu(rsiChild_x, rsiChild, mouseX, rsiChild_y, mouseY, rsiChild.scrollPosition);
                if (rsiChild.scrollMax > rsiChild.height)
                    moveScroller(rsiChild_x + rsiChild.width, rsiChild.height, mouseX, mouseY, rsiChild, rsiChild_y, true, rsiChild.scrollMax);
            }
            else
            {
                if (rsiChild.atActionType == 1 && mouseX >= rsiChild_x && mouseY >= rsiChild_y && mouseX < rsiChild_x + rsiChild.width && mouseY < rsiChild_y + rsiChild.height)
                {
                    boolean flag = false;
                    if (rsiChild.contentType != 0)
                        flag = buildFriendsListMenu(rsiChild);
                    if (!flag && spellSelected == 0)
                    {
                        menuActionName[menuActionRow] = rsiChild.tooltip;
                        menuActionID[menuActionRow] = 315;
                        menuActionCmd3[menuActionRow] = rsiChild.id;
                        menuActionRow++;
                    }
                }
                if (rsiChild.atActionType == 2 && spellSelected == 0 && mouseX >= rsiChild_x && mouseY >= rsiChild_y && mouseX < rsiChild_x + rsiChild.width && mouseY < rsiChild_y + rsiChild.height)
                {
                    String s = rsiChild.selectedActionName;
                    if (s.indexOf(" ") != -1)
                        s = s.substring(0, s.indexOf(" "));
                    menuActionName[menuActionRow] = s + " @gre@" + rsiChild.spellName;
                    menuActionID[menuActionRow] = 626;
                    menuActionCmd3[menuActionRow] = rsiChild.id;
                    menuActionRow++;
                }
                if (rsiChild.atActionType == 3 && mouseX >= rsiChild_x && mouseY >= rsiChild_y && mouseX < rsiChild_x + rsiChild.width && mouseY < rsiChild_y + rsiChild.height)
                {
                    menuActionName[menuActionRow] = "Close";
                    menuActionID[menuActionRow] = 200;
                    menuActionCmd3[menuActionRow] = rsiChild.id;
                    menuActionRow++;
                }
                if (rsiChild.atActionType == 4 && mouseX >= rsiChild_x && mouseY >= rsiChild_y && mouseX < rsiChild_x + rsiChild.width && mouseY < rsiChild_y + rsiChild.height)
                {
                    menuActionName[menuActionRow] = rsiChild.tooltip;
                    menuActionID[menuActionRow] = 169;
                    menuActionCmd3[menuActionRow] = rsiChild.id;
                    menuActionRow++;
                }
                if (rsiChild.atActionType == 5 && spellSelected == 0 && mouseX >= rsiChild_x && mouseY >= rsiChild_y && mouseX < rsiChild_x + rsiChild.width && mouseY < rsiChild_y + rsiChild.height)
                {
                    menuActionName[menuActionRow] = rsiChild.tooltip;
                    menuActionID[menuActionRow] = 646;
                    menuActionCmd3[menuActionRow] = rsiChild.id;
                    menuActionRow++;
                }
                if (rsiChild.atActionType == 6 && !aBoolean1149 && mouseX >= rsiChild_x && mouseY >= rsiChild_y && mouseX < rsiChild_x + rsiChild.width && mouseY < rsiChild_y + rsiChild.height)
                {
                    menuActionName[menuActionRow] = rsiChild.tooltip;
                    menuActionID[menuActionRow] = 679;
                    menuActionCmd3[menuActionRow] = rsiChild.id;
                    menuActionRow++;
                }
                if (rsiChild.type == 2)
                {
                    int k2 = 0;
                    for (int l2 = 0; l2 < rsiChild.height; l2++)
                    {
                        for (int i3 = 0; i3 < rsiChild.width; i3++)
                        {
                            int j3 = rsiChild_x + i3 * (32 + rsiChild.invSpritePadX);
                            int k3 = rsiChild_y + l2 * (32 + rsiChild.invSpritePadY);
                            if (k2 < 20)
                            {
                                j3 += rsiChild.spritesX[k2];
                                k3 += rsiChild.spritesY[k2];
                            }
                            if (mouseX >= j3 && mouseY >= k3 && mouseX < j3 + 32 && mouseY < k3 + 32)
                            {
                                mouseInvInterfaceIndex = k2;
                                lastActiveInvInterface = rsiChild.id;
                                if (rsiChild.inv[k2] > 0)
                                {
                                    ItemDefinition itemDef = ItemDefinition.forID(rsiChild.inv[k2] - 1);
                                    if (itemSelected == 1 && rsiChild.isInventoryInterface)
                                    {
                                        if (rsiChild.id != anInt1284 || k2 != anInt1283)
                                        {
                                            menuActionName[menuActionRow] = "Use @lre@" + selectedItemName + " @whi@-> @lre@" + itemDef.name;
                                            menuActionID[menuActionRow] = 870;
                                            menuActionCmd1[menuActionRow] = itemDef.id;
                                            menuActionCmd2[menuActionRow] = k2;
                                            menuActionCmd3[menuActionRow] = rsiChild.id;
                                            menuActionRow++;
                                        }
                                    }
                                    else if (spellSelected == 1 && rsiChild.isInventoryInterface)
                                    {
                                        if ((spellUsableOn & 0x10) == 16)
                                        {
                                            menuActionName[menuActionRow] = spellTooltip + " @lre@" + itemDef.name;
                                            menuActionID[menuActionRow] = 543;
                                            menuActionCmd1[menuActionRow] = itemDef.id;
                                            menuActionCmd2[menuActionRow] = k2;
                                            menuActionCmd3[menuActionRow] = rsiChild.id;
                                            menuActionRow++;
                                        }
                                    }
                                    else
                                    {
                                        if (rsiChild.isInventoryInterface && openInterfaceID != 4074)
                                        {
                                            for (int l3 = 4; l3 >= 3; l3--)
                                                if (itemDef.actions != null && itemDef.actions[l3] != null)
                                                {
                                                    menuActionName[menuActionRow] = itemDef.actions[l3] + " @lre@" + itemDef.name;
                                                    if (l3 == 3)
                                                        menuActionID[menuActionRow] = 493;
                                                    if (l3 == 4)
                                                        menuActionID[menuActionRow] = 847;
                                                    menuActionCmd1[menuActionRow] = itemDef.id;
                                                    menuActionCmd2[menuActionRow] = k2;
                                                    menuActionCmd3[menuActionRow] = rsiChild.id;
                                                    menuActionRow++;
                                                }
                                                else if (l3 == 4 && openInterfaceID != 4074 && tabID != 4)
                                                {
                                                    menuActionName[menuActionRow] = "Drop @lre@" + itemDef.name;
                                                    menuActionID[menuActionRow] = 847;
                                                    menuActionCmd1[menuActionRow] = itemDef.id;
                                                    menuActionCmd2[menuActionRow] = k2;
                                                    menuActionCmd3[menuActionRow] = rsiChild.id;
                                                    menuActionRow++;
                                                }
                                        }
                                        if (rsiChild.usableItemInterface && openInterfaceID != 4074)
                                        {
                                            menuActionName[menuActionRow] = "Use @lre@" + itemDef.name;
                                            menuActionID[menuActionRow] = 447;
                                            menuActionCmd1[menuActionRow] = itemDef.id;
                                            menuActionCmd2[menuActionRow] = k2;
                                            menuActionCmd3[menuActionRow] = rsiChild.id;
                                            menuActionRow++;
                                        }
                                        if (rsiChild.isInventoryInterface && itemDef.actions != null && openInterfaceID == 4074)
                                        {
                                            if (itemDef.actions[1] != null && (itemDef.actions[1].equals("Wield") || itemDef.actions[1].equals("Wear")))
                                            {
                                                menuActionName[menuActionRow] = itemDef.actions[1] + " @lre@" + itemDef.name;
                                                menuActionID[menuActionRow] = 454;
                                                menuActionCmd1[menuActionRow] = itemDef.id;
                                                menuActionCmd2[menuActionRow] = k2;
                                                menuActionCmd3[menuActionRow] = rsiChild.id;
                                                menuActionRow++;
                                            }
                                        }
                                        if (rsiChild.isInventoryInterface && itemDef.actions != null && openInterfaceID != 4074)
                                        {
                                            for (int i4 = 2; i4 >= 0; i4--)
                                                if (itemDef.actions[i4] != null)
                                                {
                                                    menuActionName[menuActionRow] = itemDef.actions[i4] + " @lre@" + itemDef.name;
                                                    if (i4 == 0)
                                                        menuActionID[menuActionRow] = 74;
                                                    if (i4 == 1)
                                                        menuActionID[menuActionRow] = 454;
                                                    if (i4 == 2)
                                                        menuActionID[menuActionRow] = 539;
                                                    menuActionCmd1[menuActionRow] = itemDef.id;
                                                    menuActionCmd2[menuActionRow] = k2;
                                                    menuActionCmd3[menuActionRow] = rsiChild.id;
                                                    menuActionRow++;
                                                }
                                        }
                                        if (rsiChild.actions != null)
                                        {
                                            for (int j4 = 4; j4 >= 0; j4--)
                                                if (rsiChild.actions[j4] != null)
                                                {
                                                    menuActionName[menuActionRow] = rsiChild.actions[j4] + " @lre@" + itemDef.name;
                                                    if (j4 == 0)
                                                        menuActionID[menuActionRow] = 632;
                                                    if (j4 == 1)
                                                        menuActionID[menuActionRow] = 78;
                                                    if (j4 == 2)
                                                        menuActionID[menuActionRow] = 867;
                                                    if (j4 == 3)
                                                        menuActionID[menuActionRow] = 431;
                                                    if (j4 == 4)
                                                        menuActionID[menuActionRow] = 53;
                                                    menuActionCmd1[menuActionRow] = itemDef.id;
                                                    menuActionCmd2[menuActionRow] = k2;
                                                    menuActionCmd3[menuActionRow] = rsiChild.id;
                                                    menuActionRow++;
                                                }
                                            menuActionName[menuActionRow] = "Examine @lre@" + itemDef.name;
                                            menuActionID[menuActionRow] = 1125;
                                            menuActionCmd1[menuActionRow] = itemDef.id;
                                            menuActionCmd2[menuActionRow] = k2;
                                            menuActionCmd3[menuActionRow] = rsiChild.id;
                                            menuActionRow++;
                                        }
                                    }
                                }
                            }
                            k2++;
                        }
                    }
                }
            }
        }
    }

    private void drawTransScrollbar(int barHeight, int scrollPos, int yPos, int xPos, int contentHeight, boolean newScroller, boolean isTransparent) {
		int backingAmount = (barHeight - 32) / (isTransparent ? 2 : 5);
		int scrollPartHeight = ((barHeight - 32) * barHeight) / contentHeight;
		int scrollerID;
		if (newScroller) {
			scrollerID = 4;
		} else if (isTransparent) {
			scrollerID = 8;
		} else {
			scrollerID = 0;
		}
		if (scrollPartHeight < 10)
			scrollPartHeight = 10;
		int scrollPartAmount = (scrollPartHeight / (isTransparent ? 1 : 5)) - 2;
		int scrollPartPos = ((barHeight - 32 - scrollPartHeight) * scrollPos)
				/ (contentHeight - barHeight) + 16 + yPos;
		//Bar fill
		for (int i = 0, yyPos = yPos + 16; i <= (isTransparent ? backingAmount / 2 + 6
				: backingAmount); i++, yyPos += (isTransparent ? 3 : 5)) {
			if (isTransparent) {
				scrollFill.drawAdvancedSprite(xPos - 6, yyPos);
			} else {
				scrollBar[3].drawIndexedImage(xPos, yyPos);
			}
		}
		//Top of bar 
		if (isTransparent) {
			scrollTop.drawAdvancedSprite(xPos - 6, scrollPartPos);
		} else
			scrollBar[3].drawIndexedImage(xPos, scrollPartPos);
		scrollPartPos += 5;
		// Middle of bar
		for (int i = 0; i <= (isTransparent ? scrollPartAmount - 9
				: scrollPartAmount); i++) {
			if (isTransparent) {
				scrollMiddle.drawAdvancedSprite(xPos - 6, scrollPartPos);
			} else
				scrollBar[3].drawIndexedImage(xPos, scrollPartPos);
			scrollPartPos += isTransparent ? 1 : 5;
		}
		scrollPartPos = ((barHeight - 32 - scrollPartHeight) * scrollPos)
				/ (contentHeight - barHeight) + 16 + yPos
				+ (scrollPartHeight - 5);
		//Bottom of bar 
		if (isTransparent) {
			scrollBottom.drawAdvancedSprite(xPos - 6, scrollPartPos);
		} else
			 scrollBar[3].drawIndexedImage(xPos, scrollPartPos);
		//Arrows 
		if (newScroller) {
			 scrollBar[0].drawIndexedImage(xPos, yPos);
			 scrollBar[1].drawIndexedImage(xPos, (yPos + barHeight) - 16);
		} else if (isTransparent) {
			scrollUp.drawAdvancedSprite(xPos - 6, yPos);
			scrollDown.drawAdvancedSprite(xPos - 6, (yPos + barHeight) - 16);
		} else {
			scrollBar[0].drawIndexedImage(xPos, yPos);
			scrollBar[1].drawIndexedImage(xPos, (yPos + barHeight) - 16);
		}
	}
    
    private void drawRegularScrollbar(int j, int k, int l, int i1, int j1)
    {
        scrollBar[0].drawIndexedImage(i1, l);
        scrollBar[1].drawIndexedImage(i1, (l + j) - 16);
        RSRaster.drawPixels(j - 32, l + 16, i1, anInt1002, 16);
        int k1 = ((j - 32) * j) / j1;
        if (k1 < 8)
            k1 = 8;
        int l1 = ((j - 32 - k1) * k) / (j1 - j);
        RSRaster.drawPixels(k1, l + 16 + l1, i1, barFillColor, 16);
        RSRaster.method341(l + 16 + l1, anInt902, k1, i1);
        RSRaster.method341(l + 16 + l1, anInt902, k1, i1 + 1);
        RSRaster.method339(l + 16 + l1, anInt902, 16, i1);
        RSRaster.method339(l + 17 + l1, anInt902, 16, i1);
        RSRaster.method341(l + 16 + l1, anInt927, k1, i1 + 15);
        RSRaster.method341(l + 17 + l1, anInt927, k1 - 1, i1 + 14);
        RSRaster.method339(l + 15 + l1 + k1, anInt927, 16, i1);
        RSRaster.method339(l + 14 + l1 + k1, anInt927, 15, i1 + 1);
    }

    public void drawScrollbar_chat(int j, int k, int l, int i1, int j1)
    {
        scrollBar[2].drawIndexedImage(i1, l);
        scrollBar[3].drawIndexedImage(i1, (l + j) - 16);
        RSRaster.drawPixels(j - 32, l + 16, i1, 0x000001, 16);
        RSRaster.drawPixels(j - 32, l + 16, i1, 0x3d3426, 15);
        RSRaster.drawPixels(j - 32, l + 16, i1, 0x342d21, 13);
        RSRaster.drawPixels(j - 32, l + 16, i1, 0x2e281d, 11);
        RSRaster.drawPixels(j - 32, l + 16, i1, 0x29241b, 10);
        RSRaster.drawPixels(j - 32, l + 16, i1, 0x252019, 9);
        RSRaster.drawPixels(j - 32, l + 16, i1, 0x000001, 1);
        int k1 = ((j - 32) * j) / j1;
        if (k1 < 8)
            k1 = 8;
        int l1 = ((j - 32 - k1) * k) / (j1 - j);
        RSRaster.drawPixels(k1, l + 16 + l1, i1, barFillColor, 16);
        RSRaster.method341(l + 16 + l1, 0x000001, k1, i1);
        RSRaster.method341(l + 16 + l1, 0x817051, k1, i1 + 1);
        RSRaster.method341(l + 16 + l1, 0x73654a, k1, i1 + 2);
        RSRaster.method341(l + 16 + l1, 0x6a5c43, k1, i1 + 3);
        RSRaster.method341(l + 16 + l1, 0x6a5c43, k1, i1 + 4);
        RSRaster.method341(l + 16 + l1, 0x655841, k1, i1 + 5);
        RSRaster.method341(l + 16 + l1, 0x655841, k1, i1 + 6);
        RSRaster.method341(l + 16 + l1, 0x61553e, k1, i1 + 7);
        RSRaster.method341(l + 16 + l1, 0x61553e, k1, i1 + 8);
        RSRaster.method341(l + 16 + l1, 0x5d513c, k1, i1 + 9);
        RSRaster.method341(l + 16 + l1, 0x5d513c, k1, i1 + 10);
        RSRaster.method341(l + 16 + l1, 0x594e3a, k1, i1 + 11);
        RSRaster.method341(l + 16 + l1, 0x594e3a, k1, i1 + 12);
        RSRaster.method341(l + 16 + l1, 0x514635, k1, i1 + 13);
        RSRaster.method341(l + 16 + l1, 0x4b4131, k1, i1 + 14);
        RSRaster.method339(l + 16 + l1, 0x000001, 15, i1);
        RSRaster.method339(l + 17 + l1, 0x000001, 15, i1);
        RSRaster.method339(l + 17 + l1, 0x655841, 14, i1);
        RSRaster.method339(l + 17 + l1, 0x6a5c43, 13, i1);
        RSRaster.method339(l + 17 + l1, 0x6d5f48, 11, i1);
        RSRaster.method339(l + 17 + l1, 0x73654a, 10, i1);
        RSRaster.method339(l + 17 + l1, 0x76684b, 7, i1);
        RSRaster.method339(l + 17 + l1, 0x7b6a4d, 5, i1);
        RSRaster.method339(l + 17 + l1, 0x7e6e50, 4, i1);
        RSRaster.method339(l + 17 + l1, 0x817051, 3, i1);
        RSRaster.method339(l + 17 + l1, 0x000001, 2, i1);
        RSRaster.method339(l + 18 + l1, 0x000001, 16, i1);
        RSRaster.method339(l + 18 + l1, 0x564b38, 15, i1);
        RSRaster.method339(l + 18 + l1, 0x5d513c, 14, i1);
        RSRaster.method339(l + 18 + l1, 0x625640, 11, i1);
        RSRaster.method339(l + 18 + l1, 0x655841, 10, i1);
        RSRaster.method339(l + 18 + l1, 0x6a5c43, 7, i1);
        RSRaster.method339(l + 18 + l1, 0x6e6046, 5, i1);
        RSRaster.method339(l + 18 + l1, 0x716247, 4, i1);
        RSRaster.method339(l + 18 + l1, 0x7b6a4d, 3, i1);
        RSRaster.method339(l + 18 + l1, 0x817051, 2, i1);
        RSRaster.method339(l + 18 + l1, 0x000001, 1, i1);
        RSRaster.method339(l + 19 + l1, 0x000001, 16, i1);
        RSRaster.method339(l + 19 + l1, 0x514635, 15, i1);
        RSRaster.method339(l + 19 + l1, 0x564b38, 14, i1);
        RSRaster.method339(l + 19 + l1, 0x5d513c, 11, i1);
        RSRaster.method339(l + 19 + l1, 0x61553e, 9, i1);
        RSRaster.method339(l + 19 + l1, 0x655841, 7, i1);
        RSRaster.method339(l + 19 + l1, 0x6a5c43, 5, i1);
        RSRaster.method339(l + 19 + l1, 0x6e6046, 4, i1);
        RSRaster.method339(l + 19 + l1, 0x73654a, 3, i1);
        RSRaster.method339(l + 19 + l1, 0x817051, 2, i1);
        RSRaster.method339(l + 19 + l1, 0x000001, 1, i1);
        RSRaster.method339(l + 20 + l1, 0x000001, 16, i1);
        RSRaster.method339(l + 20 + l1, 0x4b4131, 15, i1);
        RSRaster.method339(l + 20 + l1, 0x544936, 14, i1);
        RSRaster.method339(l + 20 + l1, 0x594e3a, 13, i1);
        RSRaster.method339(l + 20 + l1, 0x5d513c, 10, i1);
        RSRaster.method339(l + 20 + l1, 0x61553e, 8, i1);
        RSRaster.method339(l + 20 + l1, 0x655841, 6, i1);
        RSRaster.method339(l + 20 + l1, 0x6a5c43, 4, i1);
        RSRaster.method339(l + 20 + l1, 0x73654a, 3, i1);
        RSRaster.method339(l + 20 + l1, 0x817051, 2, i1);
        RSRaster.method339(l + 20 + l1, 0x000001, 1, i1);
        RSRaster.method341(l + 16 + l1, 0x000001, k1, i1 + 15);
        RSRaster.method339(l + 15 + l1 + k1, 0x000001, 16, i1);
        RSRaster.method339(l + 14 + l1 + k1, 0x000001, 15, i1);
        RSRaster.method339(l + 14 + l1 + k1, 0x3f372a, 14, i1);
        RSRaster.method339(l + 14 + l1 + k1, 0x443c2d, 10, i1);
        RSRaster.method339(l + 14 + l1 + k1, 0x483e2f, 9, i1);
        RSRaster.method339(l + 14 + l1 + k1, 0x4a402f, 7, i1);
        RSRaster.method339(l + 14 + l1 + k1, 0x4b4131, 4, i1);
        RSRaster.method339(l + 14 + l1 + k1, 0x564b38, 3, i1);
        RSRaster.method339(l + 14 + l1 + k1, 0x000001, 2, i1);
        RSRaster.method339(l + 13 + l1 + k1, 0x000001, 16, i1);
        RSRaster.method339(l + 13 + l1 + k1, 0x443c2d, 15, i1);
        RSRaster.method339(l + 13 + l1 + k1, 0x4b4131, 11, i1);
        RSRaster.method339(l + 13 + l1 + k1, 0x514635, 9, i1);
        RSRaster.method339(l + 13 + l1 + k1, 0x544936, 7, i1);
        RSRaster.method339(l + 13 + l1 + k1, 0x564b38, 6, i1);
        RSRaster.method339(l + 13 + l1 + k1, 0x594e3a, 4, i1);
        RSRaster.method339(l + 13 + l1 + k1, 0x625640, 3, i1);
        RSRaster.method339(l + 13 + l1 + k1, 0x6a5c43, 2, i1);
        RSRaster.method339(l + 13 + l1 + k1, 0x000001, 1, i1);
        RSRaster.method339(l + 12 + l1 + k1, 0x000001, 16, i1);
        RSRaster.method339(l + 12 + l1 + k1, 0x443c2d, 15, i1);
        RSRaster.method339(l + 12 + l1 + k1, 0x4b4131, 14, i1);
        RSRaster.method339(l + 12 + l1 + k1, 0x544936, 12, i1);
        RSRaster.method339(l + 12 + l1 + k1, 0x564b38, 11, i1);
        RSRaster.method339(l + 12 + l1 + k1, 0x594e3a, 10, i1);
        RSRaster.method339(l + 12 + l1 + k1, 0x5d513c, 7, i1);
        RSRaster.method339(l + 12 + l1 + k1, 0x61553e, 4, i1);
        RSRaster.method339(l + 12 + l1 + k1, 0x6e6046, 3, i1);
        RSRaster.method339(l + 12 + l1 + k1, 0x7b6a4d, 2, i1);
        RSRaster.method339(l + 12 + l1 + k1, 0x000001, 1, i1);
        RSRaster.method339(l + 11 + l1 + k1, 0x000001, 16, i1);
        RSRaster.method339(l + 11 + l1 + k1, 0x4b4131, 15, i1);
        RSRaster.method339(l + 11 + l1 + k1, 0x514635, 14, i1);
        RSRaster.method339(l + 11 + l1 + k1, 0x564b38, 13, i1);
        RSRaster.method339(l + 11 + l1 + k1, 0x594e3a, 11, i1);
        RSRaster.method339(l + 11 + l1 + k1, 0x5d513c, 9, i1);
        RSRaster.method339(l + 11 + l1 + k1, 0x61553e, 7, i1);
        RSRaster.method339(l + 11 + l1 + k1, 0x655841, 5, i1);
        RSRaster.method339(l + 11 + l1 + k1, 0x6a5c43, 4, i1);
        RSRaster.method339(l + 11 + l1 + k1, 0x73654a, 3, i1);
        RSRaster.method339(l + 11 + l1 + k1, 0x7b6a4d, 2, i1);
        RSRaster.method339(l + 11 + l1 + k1, 0x000001, 1, i1);
    }

    public void updateNPCs(RSBuffer stream, int i)
    {
        anInt839 = 0;
        anInt893 = 0;
        method139(stream);
        method46(i, stream);
        method86(stream);
        for (int k = 0; k < anInt839; k++)
        {
            int l = anIntArray840[k];
            if (npcArray[l].anInt1537 != loopCycle)
            {
                npcArray[l].desc = null;
                npcArray[l] = null;
            }
        }
        if (stream.pointer != i)
        {
            System.err.println(myUsername + " size mismatch in getnpcpos - pos:" + stream.pointer + " psize:" + i);
            throw new RuntimeException("eek");
        }
        for (int i1 = 0; i1 < npcCount; i1++)
            if (npcArray[npcIndices[i1]] == null)
            {
                System.err.println(myUsername + " null entry in npc list - pos:" + i1 + " size:" + npcCount);
                throw new RuntimeException("eek");
            }
    }

    public void processChatTabs()
    {

        // Blue tab flashing
        for (int i = 0; i < 6; i++)
        {
            if (chatStoneHoverState[i] == 2)
            {
                inputTaken = true;
            }
        }

        if (mouseIsWithin(5, 480 + (clientSize == CLIENT_FIXED ? 0 : clientHeight - 504), 55, 22) && !menuOpen)
        {
            chatStoneHovered = 0;
            inputTaken = true;
        }
        else if (mouseIsWithin(71, 480 + (clientSize == CLIENT_FIXED ? 0 : clientHeight - 504), 55, 22) && !menuOpen)
        {
            chatStoneHovered = 1;
            inputTaken = true;
        }
        else if (mouseIsWithin(137, 480 + (clientSize == CLIENT_FIXED ? 0 : clientHeight - 504), 55, 22) && !menuOpen)
        {
            chatStoneHovered = 2;
            inputTaken = true;
        }
        else if (mouseIsWithin(203, 480 + (clientSize == CLIENT_FIXED ? 0 : clientHeight - 504), 55, 22) && !menuOpen)
        {
            chatStoneHovered = 3;
            inputTaken = true;
        }
        else if (mouseIsWithin(269, 480 + (clientSize == CLIENT_FIXED ? 0 : clientHeight - 504), 55, 22) && !menuOpen)
        {
            chatStoneHovered = 4;
            inputTaken = true;
        }
        else if (mouseIsWithin(335, 480 + (clientSize == CLIENT_FIXED ? 0 : clientHeight - 504), 55, 22) && !menuOpen)
        {
            chatStoneHovered = 5;
            inputTaken = true;
        }
        else if (mouseIsWithin(404, 480 + (clientSize == CLIENT_FIXED ? 0 : clientHeight - 504), 110, 22) && !menuOpen)
        {
            chatStoneHovered = 6;
            inputTaken = true;
        }
        else if (chatStoneHovered != -1)
        {
            inputTaken = true;
            chatStoneHovered = -1;
        }
    }

    private final static String[] magicSpellNames =
    { "Wind strike", "", "Water strike", "", "Earth strike", "", "Fire strike", "", "Wind bolt", "", "", "Water bolt", "", "", "Earth bolt", "", "", "Fire bolt", "", "", "Wind blast", "", "", "Water blast", "", "Earth blast", "", "", "", "Fire blast", "", "Wind wave", "", "Water wave", "", "", "Earth wave", "Fire wave", "", "", "", "", "", "Ice rush", "Ice burst", "Ice blitz", "Ice barrage", "Smoke rush", "Smoke burst", "Smoke blitz", "Smoke barrage", "Blood rush", "Blood burst", "Blood blitz", "Blood barrage", "Shadow rush", "Shadow burst", "Shadow blitz", "Shadow barrage" };

    /**
     * TODO: AKZU CHECK THIS
     * 
     * @param config
     * @param value
     */
    public void updateMagicSettings(int config, int value)
    {
        if (config == 960)
        {
            RSInterface magic = RSInterface.interfaceCache[151];

            RSInterface hover = RSInterface.interfaceCache[9724];
            RSInterface hover2 = RSInterface.interfaceCache[4156];

            hover2.disabledMessage = (magicSpellNames[value].isEmpty() ? "(Attack with\\na spell)" : magicSpellNames[value]) + "\\n(Magic XP)";
            hover.disabledMessage = (magicSpellNames[value].isEmpty() ? "(Attack with\\na spell)" : magicSpellNames[value]) + "\\n(Magic XP)\\n(Defence XP)";

            if (value == 1)
            {
                magic.setDisabledSprite("sideicons", 6, RSInterface.aClass44);
                magic.xOffset = 0;
                magic.yOffset = 0;
            }
            else
            {
                magic.setDisabledSprite(value > 37 ? "magicon2" : "magicon", value > 37 ? (value - 37) : value, RSInterface.aClass44);
                magic.xOffset = 5;
                magic.yOffset = 5;
            }
        }
    }

    /**
     * come here to adjust music akzu TODO
     */
    public void method33(int i)
    {
        int action = Varp.cache[i].anInt709;
        if (action == 0)
            return;
        int config = variousSettings[i];
        if (action == 1)
        {
            if (config == 1)
                Rasterizer.method372(0.9D);
            if (config == 2)
                Rasterizer.method372(0.75D);
            if (config == 3)
                Rasterizer.method372(0.65D);
            if (config == 4)
                Rasterizer.method372(0.54D);
            ItemDefinition.mruNodes1.unlinkAll();
            welcomeScreenRaised = true;
        }
        if (action == 4)
        {
            SoundPlayer.setVolume(config);
            if (config == 0)
            {
                aBoolean848 = true;
            }
            if (config == 1)
            {
                aBoolean848 = true;
            }
            if (config == 2)
            {
                aBoolean848 = true;
            }
            if (config == 3)
            {
                aBoolean848 = true;
            }
            if (config == 4)
            {
                aBoolean848 = false;
            }
        }
        if (action == 5)
            mouse_buttons = config;
        if (action == 6)
            chat_colors_config = config;
        if (action == 8)
        {
            splitPrivateChat = config;
            inputTaken = true;
        }
        if (action == 9)
            anInt913 = config;
    }

    public void updateEntities()
    {
        try
        {
            int anInt974 = 0;
            for (int j = -1; j < playerCount + npcCount; j++)
            {
                Object obj;
                if (j == -1)
                    obj = myPlayer;
                else if (j < playerCount)
                    obj = playerArray[playerIndices[j]];
                else
                    obj = npcArray[npcIndices[j - playerCount]];
                if (obj == null || !((Mobile) (obj)).isVisible())
                    continue;
                if (obj instanceof NPC)
                {
                    NpcDefintion entityDef = ((NPC) obj).desc;
                    if (entityDef.childrenIDs != null)
                        entityDef = entityDef.method161();
                    if (entityDef == null)
                        continue;
                }
                // TODO: Fix headicon's etc.. akzuhere
                if (j < playerCount)
                {
                    int l = 30;
                    Player player = (Player) obj;
                    if (player.headIcon >= 0)
                    {
                        npcScreenPos(((Mobile) (obj)), ((Mobile) (obj)).height + 15);
                        if (spriteDrawX > -1)
                        {
                            if (player.skullIcon < 2)
                            {
                                skullIcons[player.skullIcon].drawIndexedImage(spriteDrawX - 12, spriteDrawY - l);
                                l += 25;
                            }
                            if (player.headIcon < 7)
                            {
                                headIcons[player.headIcon].drawIndexedImage(spriteDrawX - 12, spriteDrawY - l);
                                l += 26;
                            }
                        }
                    }
                    if (j >= 0 && hintType == 10 && hintArrowPlayerID == playerIndices[j])
                    {
                        npcScreenPos(((Mobile) (obj)), ((Mobile) (obj)).height + 15);
                        if (spriteDrawX > -1)
                        {
                            headIconsHint[1].drawIndexedImage(spriteDrawX - 11, spriteDrawY - l);
                        }
                    }
                }
                else
                {
                    NpcDefintion entityDef_1 = ((NPC) obj).desc;
                    if (entityDef_1.headIcon >= 0 && entityDef_1.headIcon < headIcons.length)
                    {
                        npcScreenPos(((Mobile) (obj)), ((Mobile) (obj)).height + 15);
                        if (spriteDrawX > -1)
                            headIcons[entityDef_1.headIcon].drawIndexedImage(spriteDrawX - 12, spriteDrawY - 30);
                    }
                    if (hintType == 1 && hintArrowNPCID == npcIndices[j - playerCount] && loopCycle % 20 < 10)
                    {
                        npcScreenPos(((Mobile) (obj)), ((Mobile) (obj)).height + 15);
                        if (spriteDrawX > -1)
                            headIconsHint[0].drawIndexedImage(spriteDrawX - 11, spriteDrawY - 28);
                    }
                }
                if (((Mobile) (obj)).textSpoken != null && (j >= playerCount || chatTabMode[2] == 0 || chatTabMode[2] == 3 || chatTabMode[2] == 1 && isFriendOrSelf(((Player) obj).name)))
                {
                    npcScreenPos(((Mobile) (obj)), ((Mobile) (obj)).height);
                    if (spriteDrawX > -1 && anInt974 < anInt975)
                    {
                        anIntArray979[anInt974] = chatText.method384(((Mobile) (obj)).textSpoken) / 2;
                        anIntArray978[anInt974] = chatText.anInt1497;
                        anIntArray976[anInt974] = spriteDrawX;
                        anIntArray977[anInt974] = spriteDrawY;
                        chat_color[anInt974] = ((Mobile) (obj)).anInt1513;
                        chat_effects[anInt974] = ((Mobile) (obj)).anInt1531;
                        anIntArray982[anInt974] = ((Mobile) (obj)).textCycle;
                        aStringArray983[anInt974++] = ((Mobile) (obj)).textSpoken;
                        if (chat_colors_config == 0 && ((Mobile) (obj)).anInt1531 >= 1 && ((Mobile) (obj)).anInt1531 <= 3)
                        {
                            anIntArray978[anInt974] += 10;
                            anIntArray977[anInt974] += 5;
                        }
                        if (chat_colors_config == 0 && ((Mobile) (obj)).anInt1531 == 4)
                            anIntArray979[anInt974] = 60;
                        if (chat_colors_config == 0 && ((Mobile) (obj)).anInt1531 == 5)
                            anIntArray978[anInt974] += 5;
                    }
                }
                if (((Mobile) (obj)).loopCycleStatus > loopCycle)
                {
                    try
                    {
                        npcScreenPos(((Mobile) (obj)), ((Mobile) (obj)).height + 15);
                        if (spriteDrawX > -1)
                        {
                            int i1 = (((Mobile) (obj)).currentHealth * 30) / ((Mobile) (obj)).maxHealth;
                            if (i1 > 30)
                                i1 = 30;
                            RSRaster.drawPixels(5, spriteDrawY - 3, spriteDrawX - 15, 65280, i1);
                            RSRaster.drawPixels(5, spriteDrawY - 3, (spriteDrawX - 15) + i1, 0xff0000, 30 - i1);
                        }
                    }
                    catch (Exception e)
                    {
                    }
                }
                for (int j1 = 0; j1 < 4; j1++)
                    if (((Mobile) (obj)).hitsLoopCycle[j1] > loopCycle)
                    {
                        npcScreenPos(((Mobile) (obj)), ((Mobile) (obj)).height / 2);
                        if (spriteDrawX > -1)
                        {
                            if (j1 == 1)
                                spriteDrawY -= 20;
                            if (j1 == 2)
                            {
                                spriteDrawX -= 15;
                                spriteDrawY -= 10;
                            }
                            if (j1 == 3)
                            {
                                spriteDrawX += 15;
                                spriteDrawY -= 10;
                            }
                            hitMarks[((Mobile) (obj)).hitMarkTypes[j1]].drawIndexedImage(spriteDrawX - 12, spriteDrawY - 12);
                            smallText.drawText(0, String.valueOf(((Mobile) (obj)).hitArray[j1]), spriteDrawY + 4, spriteDrawX);
                            smallText.drawText(0xffffff, String.valueOf(((Mobile) (obj)).hitArray[j1]), spriteDrawY + 3, spriteDrawX - 1);
                        }
                    }
            }
            for (int k = 0; k < anInt974; k++)
            {
                int k1 = anIntArray976[k];
                int l1 = anIntArray977[k];
                int j2 = anIntArray979[k];
                int k2 = anIntArray978[k];
                boolean flag = true;
                while (flag)
                {
                    flag = false;
                    for (int l2 = 0; l2 < k; l2++)
                        if (l1 + 2 > anIntArray977[l2] - anIntArray978[l2] && l1 - k2 < anIntArray977[l2] + 2 && k1 - j2 < anIntArray976[l2] + anIntArray979[l2] && k1 + j2 > anIntArray976[l2] - anIntArray979[l2] && anIntArray977[l2] - anIntArray978[l2] < l1)
                        {
                            l1 = anIntArray977[l2] - anIntArray978[l2];
                            flag = true;
                        }
                }
                spriteDrawX = anIntArray976[k];
                spriteDrawY = anIntArray977[k] = l1;
                String s = aStringArray983[k];
                if (chat_colors_config == 0)
                {
                    int color = 0xffff00;
                    if (chat_color[k] < 6)
                        color = anIntArray965[chat_color[k]];
                    if (chat_color[k] == 6)
                        color = anInt1265 % 20 >= 10 ? 0xffff00 : 0xff0000;
                    if (chat_color[k] == 7)
                        color = anInt1265 % 20 >= 10 ? 65535 : 255;
                    if (chat_color[k] == 8)
                        color = anInt1265 % 20 >= 10 ? 0x80ff80 : 45056;
                    if (chat_color[k] == 9)
                    {
                        int j3 = 150 - anIntArray982[k];
                        if (j3 < 50)
                            color = 0xff0000 + 1280 * j3;
                        else if (j3 < 100)
                            color = 0xffff00 - 0x50000 * (j3 - 50);
                        else if (j3 < 150)
                            color = 65280 + 5 * (j3 - 100);
                    }
                    if (chat_color[k] == 10)
                    {
                        int k3 = 150 - anIntArray982[k];
                        if (k3 < 50)
                            color = 0xff0000 + 5 * k3;
                        else if (k3 < 100)
                            color = 0xff00ff - 0x50000 * (k3 - 50);
                        else if (k3 < 150)
                            color = (255 + 0x50000 * (k3 - 100)) - 5 * (k3 - 100);
                    }
                    if (chat_color[k] == 11)
                    {
                        int l3 = 150 - anIntArray982[k];
                        if (l3 < 50)
                            color = 0xffffff - 0x50005 * l3;
                        else if (l3 < 100)
                            color = 65280 + 0x50005 * (l3 - 50);
                        else if (l3 < 150)
                            color = 0xffffff - 0x50000 * (l3 - 100);
                    }
                    if (chat_effects[k] == 0)
                    {
                        chatText.drawText(0, s, spriteDrawY + 1, spriteDrawX + 1);
                        chatText.drawText(color, s, spriteDrawY, spriteDrawX);
                    }
                    if (chat_effects[k] == 1)
                    {
                        chatText.method386(0, s, spriteDrawX + 1, anInt1265, spriteDrawY + 1);
                        chatText.method386(color, s, spriteDrawX, anInt1265, spriteDrawY);
                    }
                    if (chat_effects[k] == 2)
                    {
                        chatText.method387(spriteDrawX + 1, s, anInt1265, spriteDrawY + 1, 0);
                        chatText.method387(spriteDrawX, s, anInt1265, spriteDrawY, color);
                    }
                    if (chat_effects[k] == 3)
                    {
                        chatText.method388(150 - anIntArray982[k], s, anInt1265, spriteDrawY + 1, spriteDrawX + 1, 0);
                        chatText.method388(150 - anIntArray982[k], s, anInt1265, spriteDrawY, spriteDrawX, color);
                    }
                    if (chat_effects[k] == 4)
                    {
                        int i4 = chatText.method384(s);
                        int k4 = ((150 - anIntArray982[k]) * (i4 + 100)) / 150;
                        RSRaster.setDrawingArea(334, spriteDrawX - 50, spriteDrawX + 50, 0);
                        chatText.method385(0, s, spriteDrawY + 1, (spriteDrawX + 51) - k4);
                        chatText.method385(color, s, spriteDrawY, (spriteDrawX + 50) - k4);
                        RSRaster.defaultDrawingAreaSize();
                    }
                    if (chat_effects[k] == 5)
                    {
                        int j4 = 150 - anIntArray982[k];
                        int l4 = 0;
                        if (j4 < 25)
                            l4 = j4 - 25;
                        else if (j4 > 125)
                            l4 = j4 - 125;
                        RSRaster.setDrawingArea(spriteDrawY + 5, 0, 512, spriteDrawY - chatText.anInt1497 - 1);
                        chatText.drawText(0, s, spriteDrawY + 1 + l4, spriteDrawX + 1);
                        chatText.drawText(color, s, spriteDrawY + l4, spriteDrawX);
                        RSRaster.defaultDrawingAreaSize();
                    }
                }
                else
                {
                    chatText.drawText(0, s, spriteDrawY + 1, spriteDrawX + 1);
                    chatText.drawText(0xffff00, s, spriteDrawY, spriteDrawX);
                }
            }
        }
        catch (Exception e)
        {
        }
    }

    public void delFriend(long l)
    {
        try
        {
            if (l == 0L)
                return;
            for (int i = 0; i < friendsCount; i++)
            {
                if (friendsListAsLongs[i] != l)
                    continue;
                friendsCount--;
                needDrawTabArea = true;
                for (int j = i; j < friendsCount; j++)
                {
                    friendsList[j] = friendsList[j + 1];
                    friendsNodeIDs[j] = friendsNodeIDs[j + 1];
                    friendsListAsLongs[j] = friendsListAsLongs[j + 1];
                }
                stream.writeOpcode(215);
                stream.writeLong(l);
                break;
            }
        }
        catch (RuntimeException runtimeexception)
        {
            System.err.println("18622, " + false + ", " + l + ", " + runtimeexception.toString());
            throw new RuntimeException();
        }
    }

    public static String capitalize(String s)
    {
        for (int i = 0; i < s.length(); i++)
        {
            if (i == 0)
            {
                s = String.format("%s%s", Character.toUpperCase(s.charAt(0)), s.substring(1));
            }
            if (!Character.isLetterOrDigit(s.charAt(i)))
            {
                if (i + 1 < s.length())
                {
                    s = String.format("%s%s%s", s.subSequence(0, i + 1), Character.toUpperCase(s.charAt(i + 1)), s.substring(i + 2));
                }
            }
        }
        return s;
    }

    private void drawSIcon(int id, int x, int y)
    {
        if (tabInterfaceIDs[id] != -1 && (anInt1054 != id || loopCycle % 20 < 10))
        {
            sideIcons[id].drawIndexedImage(x, y);
        }
    }

    public void drawSideIcons()
    {
        int x = 0;
        int y = 0;
        if (clientSize == CLIENT_FIXED)
        {
            drawSIcon(0, 13, 4);
            drawSIcon(1, 46, 4);
            drawSIcon(2, 79, 3);
            drawSIcon(3, 113, 4);
            drawSIcon(4, 143, 2);
            drawSIcon(5, 177, 1);
            drawSIcon(6, 211, 4);
            drawSIcon(7, 14, 300);
            drawSIcon(8, 49, 306);
            drawSIcon(9, 82, 306);
            drawSIcon(10, 116, 300);
            drawSIcon(11, 148, 304);
            drawSIcon(12, 184, 302);
            drawSIcon(13, 216, 303);
        }
        else if (clientWidth >= 1006)
        {
            x = clientWidth - 473;
            y = clientHeight - 34;
            drawSIcon(0, x, y);
            x += 32;
            y += 2;
            drawSIcon(1, x, y);
            x += 35;
            y -= 1;
            drawSIcon(2, x, y);
            x += 35;
            y += 2;
            drawSIcon(3, x, y);
            x += 32;
            y -= 4;
            drawSIcon(4, x, y);
            x += 34;
            y -= 1;
            drawSIcon(5, x, y);
            x += 34;
            y += 3;
            drawSIcon(6, x, y);
            x += 38;
            y -= 2;
            drawSIcon(7, x, y);
            x += 34;
            y += 5;
            drawSIcon(8, x, y);
            x += 34;
            drawSIcon(9, x, y);
            x += 35;
            y -= 5;
            drawSIcon(10, x, y);
            x += 33;
            y += 5;
            drawSIcon(11, x, y);
            x += 36;
            y -= 2;
            drawSIcon(12, x, y);
            x += 33;
            y += 2;
            drawSIcon(13, x, y);
        }
        else
        {
            x = clientWidth - 237;
            y = clientHeight - 71;
            drawSIcon(0, x, y);
            x += 34;
            drawSIcon(1, x, y);
            x += 33;
            drawSIcon(2, x, y);
            x += 34;
            y += 1;
            drawSIcon(3, x, y);
            x += 30;
            y -= 3;
            drawSIcon(4, x, y);
            x += 34;
            y -= 1;
            drawSIcon(5, x, y);
            x += 35;
            y += 3;
            drawSIcon(6, x, y);
            x -= 198;
            y += 33;
            drawSIcon(7, x, y);
            x += 35;
            y += 6;
            drawSIcon(8, x, y);
            x += 33;
            drawSIcon(9, x, y);
            x += 34;
            y -= 6;
            drawSIcon(10, x, y);
            x += 32;
            y += 6;
            drawSIcon(11, x, y);
            x += 35;
            y -= 2;
            drawSIcon(12, x, y);
            x += 33;
            y += 1;
            drawSIcon(13, x, y);
        }
    }

    private void drawRedStIcon(int id, int sid, int x, int y)
    {
        if (tabInterfaceIDs[id] != -1)
        {
            redStones[sid].drawIndexedImage(x, y);
        }
    }

    public void drawRedStones()
    {
        int x = 0;
        int y = 0;
        if (clientSize == CLIENT_FIXED)
        {
            switch (tabID)
            {
            case 0:
                drawRedStIcon(tabID, 0, 6, 0);
                break;
            case 1:
                drawRedStIcon(tabID, 1, 44, 0);
                break;
            case 2:
                drawRedStIcon(tabID, 1, 77, 0);
                break;
            case 3:
                drawRedStIcon(tabID, 1, 110, 0);
                break;
            case 4:
                drawRedStIcon(tabID, 1, 143, 0);
                break;
            case 5:
                drawRedStIcon(tabID, 1, 176, 0);
                break;
            case 6:
                drawRedStIcon(tabID, 2, 203, 0);
                break;
            case 7:
                drawRedStIcon(tabID, 3, 6, 297);
                break;
            case 8:
                drawRedStIcon(tabID, 1, 44, 298);
                break;
            case 9:
                drawRedStIcon(tabID, 1, 77, 298);
                break;
            case 10:
                drawRedStIcon(tabID, 1, 110, 298);
                break;
            case 11:
                drawRedStIcon(tabID, 1, 143, 298);
                break;
            case 12:
                drawRedStIcon(tabID, 1, 176, 298);
                break;
            case 13:
                drawRedStIcon(tabID, 4, 203, 297);
                break;
            }
        }
        else if (clientWidth >= 1006 && !invHidden)
        {
            x = clientWidth - 476;
            y = clientHeight - 37;
            switch (tabID)
            {
            case 0:
                drawRedStIcon(tabID, 1, x, y);
                break;
            case 1:
                x += 34;
                drawRedStIcon(tabID, 1, x, y);
                break;
            case 2:
                x += 34 * 2;
                drawRedStIcon(tabID, 1, x, y);
                break;
            case 3:
                x += 34 * 3;
                drawRedStIcon(tabID, 1, x, y);
                break;
            case 4:
                x += 34 * 4;
                drawRedStIcon(tabID, 1, x, y);
                break;
            case 5:
                x += 34 * 5;
                drawRedStIcon(tabID, 1, x, y);
                break;
            case 6:
                x += 34 * 6;
                drawRedStIcon(tabID, 1, x, y);
                break;
            case 7:
                x += 34 * 7;
                drawRedStIcon(tabID, 1, x, y);
                break;
            case 8:
                x += 34 * 8;
                drawRedStIcon(tabID, 1, x, y);
                break;
            case 9:
                x += 34 * 9;
                drawRedStIcon(tabID, 1, x, y);
                break;
            case 10:
                x += 34 * 10;
                drawRedStIcon(tabID, 1, x, y);
                break;
            case 11:
                x += 34 * 11;
                drawRedStIcon(tabID, 1, x, y);
                break;
            case 12:
                x += 34 * 12;
                drawRedStIcon(tabID, 1, x, y);
                break;
            case 13:
                x += 34 * 13;
                drawRedStIcon(tabID, 1, x, y);
                break;
            }
        }
        else if (!invHidden)
        {
            x = clientWidth - 243;
            y = clientHeight - 75;
            switch (tabID)
            {
            case 0:
                drawRedStIcon(tabID, 0, x, y);
                break;
            case 1:
                x += 38;
                drawRedStIcon(tabID, 1, x, y);
                break;
            case 2:
                x += 71;
                drawRedStIcon(tabID, 1, x, y);
                break;
            case 3:
                x += 104;
                drawRedStIcon(tabID, 1, x, y);
                break;
            case 4:
                x += 137;
                drawRedStIcon(tabID, 1, x, y);
                break;
            case 5:
                x += 170;
                drawRedStIcon(tabID, 1, x, y);
                break;
            case 6:
                x += 197;
                drawRedStIcon(tabID, 2, x, y);
                break;
            case 7:
                y += 35;
                drawRedStIcon(tabID, 3, x, y);
                break;
            case 8:
                x += 38;
                y += 36;
                drawRedStIcon(tabID, 1, x, y);
                break;
            case 9:
                x += 71;
                y += 36;
                drawRedStIcon(tabID, 1, x, y);
                break;
            case 10:
                x += 104;
                y += 36;
                drawRedStIcon(tabID, 1, x, y);
                break;
            case 11:
                x += 137;
                y += 36;
                drawRedStIcon(tabID, 1, x, y);
                break;
            case 12:
                x += 170;
                y += 36;
                drawRedStIcon(tabID, 1, x, y);
                break;
            case 13:
                x += 197;
                y += 35;
                drawRedStIcon(tabID, 4, x, y);
                break;
            }
        }
    }

    public void drawTabArea()
    {
        if (clientSize == CLIENT_FIXED)
            inventoryImageProducer.initDrawingArea();
        Rasterizer.lineOffsets = tabAreaTexture;
        if (clientSize == CLIENT_FIXED)
            tabArea[0].drawSprite(0, 0);
        else
        {
            if (clientWidth >= 1006)
            {
                int x = clientWidth - 34;
                int y = clientHeight - 37;
                for (int kk = 0; kk < 14; kk++)
                {
                    tabArea[3].drawSprite(x, y);
                    x -= 34;
                }
                if (clientSize != 0 && invHidden)
                {
                    drawRedStones();
                    drawSideIcons();
                    return;
                }
                tabArea_fs.drawSpriteTrans(clientWidth - 213 + 13, clientHeight - 344 + 37, 185);
                tabArea[1].drawSprite(clientWidth - 220 + 14, clientHeight - 350 + 37);
            }
            else
            {
                tabArea[2].drawSprite(clientWidth - 243, clientHeight - 75);
                if (clientSize != 0 && invHidden)
                {
                    drawRedStones();
                    drawSideIcons();
                    return;
                }
                tabArea_fs.drawSpriteTrans(clientWidth - 213, clientHeight - 344 - 1, 185);
                tabArea[1].drawSprite(clientWidth - 220 + 1, clientHeight - 350 - 1);
            }
        }
        if ((clientSize == CLIENT_FIXED || !invHidden) && invOverlayInterfaceID == -1 && draw_sprites_logon)
        {
            drawRedStones();
            drawSideIcons();
        }
        if (invOverlayInterfaceID != -1 && draw_sprites_logon) {
            drawInterface(0, (clientSize == CLIENT_FIXED ? 37 - 6 : (clientWidth >= 1006 ? clientWidth - 249 + 50 : clientWidth - 212)), RSInterface.interfaceCache[invOverlayInterfaceID], (clientSize == CLIENT_FIXED ? 37 : (clientWidth >= 1006 ? clientHeight - 307 : clientHeight - 345)), false);
        } else if (tabInterfaceIDs[tabID] != -1 && draw_sprites_logon)
        {
            drawInterface(0, (clientSize == CLIENT_FIXED ? 37 - 6 : (clientWidth >= 1006 ? clientWidth - 249 + 50 : clientWidth - 212)), RSInterface.interfaceCache[tabInterfaceIDs[tabID]], (clientSize == CLIENT_FIXED ? 37 : (clientWidth >= 1006 ? clientHeight - 307 : clientHeight - 345)), false);
        }
        if (menuOpen && clientSize == CLIENT_FIXED)
            drawMenu(516, 168);
        if (clientSize == CLIENT_FIXED)
        {
            inventoryImageProducer.drawGraphics(168, super.graphics, 516);
            gameScreenImageProducer.initDrawingArea();
        }

        Rasterizer.lineOffsets = mainGameScreenTexture;
    }

    /**
     * Animates a texture when in high definition.
     * 
     * @param textureId The texture being animated.
     */
    public void animateTexture(int textureId)
    {
        if (!lowMemory)
        {
            if (Rasterizer.textureLastUsed[17] >= textureId)
            {
                IndexedImage background = Rasterizer.textureImages[17];
                int backgroundArea = background.myWidth * background.myHeight - 1;
                int offset = background.myWidth * anInt945 * 2;
                byte originalPixels[] = background.imgPixels;
                byte shiftedPixels[] = animatedPixels;
                for (int index = 0; index <= backgroundArea; index++)
                    shiftedPixels[index] = originalPixels[index - offset & backgroundArea];
                background.imgPixels = shiftedPixels;
                animatedPixels = originalPixels;
                Rasterizer.resetTexture(17);
            }
            if (Rasterizer.textureLastUsed[24] >= textureId)
            {
                IndexedImage indexedImage = Rasterizer.textureImages[24];
                int backgroundArea = indexedImage.myWidth * indexedImage.myHeight - 1;
                int offset = indexedImage.myWidth * anInt945 * 2;
                byte originalPixels[] = indexedImage.imgPixels;
                byte shifted[] = animatedPixels;
                for (int j2 = 0; j2 <= backgroundArea; j2++)
                    shifted[j2] = originalPixels[j2 - offset & backgroundArea];
                indexedImage.imgPixels = shifted;
                animatedPixels = originalPixels;
                Rasterizer.resetTexture(24);
            }
            if (Rasterizer.textureLastUsed[34] >= textureId)
            {
                IndexedImage image = Rasterizer.textureImages[34];
                int imageArea = image.myWidth * image.myHeight - 1;
                int offset = image.myWidth * anInt945 * 2;
                byte originalPixels[] = image.imgPixels;
                byte shiftedPixels[] = animatedPixels;
                for (int k2 = 0; k2 <= imageArea; k2++)
                    shiftedPixels[k2] = originalPixels[k2 - offset & imageArea];
                image.imgPixels = shiftedPixels;
                animatedPixels = originalPixels;
                Rasterizer.resetTexture(34);
            }
            if (Rasterizer.textureLastUsed[40] >= textureId)
            {
                IndexedImage background_2 = Rasterizer.textureImages[40];
                int i1 = background_2.myWidth * background_2.myHeight - 1;
                int l1 = background_2.myWidth * anInt945 * 2;
                byte originalPixels[] = background_2.imgPixels;
                byte shiftedPixels[] = animatedPixels;
                for (int k2 = 0; k2 <= i1; k2++)
                    shiftedPixels[k2] = originalPixels[k2 - l1 & i1];
                background_2.imgPixels = shiftedPixels;
                animatedPixels = originalPixels;
                Rasterizer.resetTexture(40);
            }
        }
    }

    public void resetMobSpokenText()
    {
        for (int i = -1; i < playerCount; i++)
        {
            int j;
            if (i == -1)
                j = myPlayerIndex;
            else
                j = playerIndices[i];
            Player player = playerArray[j];
            if (player != null && player.textCycle > 0)
            {
                player.textCycle--;
                if (player.textCycle == 0)
                    player.textSpoken = null;
            }
        }
        for (int k = 0; k < npcCount; k++)
        {
            int l = npcIndices[k];
            NPC npc = npcArray[l];
            if (npc != null && npc.textCycle > 0)
            {
                npc.textCycle--;
                if (npc.textCycle == 0)
                    npc.textSpoken = null;
            }
        }
    }

    public void calcCameraPos()
    {
        int i = anInt1098 * 128 + 64;
        int j = anInt1099 * 128 + 64;
        int k = method42(floor_level, j, i) - anInt1100;
        if (xCameraPos < i)
        {
            xCameraPos += anInt1101 + ((i - xCameraPos) * anInt1102) / 1000;
            if (xCameraPos > i)
                xCameraPos = i;
        }
        if (xCameraPos > i)
        {
            xCameraPos -= anInt1101 + ((xCameraPos - i) * anInt1102) / 1000;
            if (xCameraPos < i)
                xCameraPos = i;
        }
        if (zCameraPos < k)
        {
            zCameraPos += anInt1101 + ((k - zCameraPos) * anInt1102) / 1000;
            if (zCameraPos > k)
                zCameraPos = k;
        }
        if (zCameraPos > k)
        {
            zCameraPos -= anInt1101 + ((zCameraPos - k) * anInt1102) / 1000;
            if (zCameraPos < k)
                zCameraPos = k;
        }
        if (yCameraPos < j)
        {
            yCameraPos += anInt1101 + ((j - yCameraPos) * anInt1102) / 1000;
            if (yCameraPos > j)
                yCameraPos = j;
        }
        if (yCameraPos > j)
        {
            yCameraPos -= anInt1101 + ((yCameraPos - j) * anInt1102) / 1000;
            if (yCameraPos < j)
                yCameraPos = j;
        }
        i = anInt995 * 128 + 64;
        j = anInt996 * 128 + 64;
        k = method42(floor_level, j, i) - anInt997;
        int l = i - xCameraPos;
        int i1 = k - zCameraPos;
        int j1 = j - yCameraPos;
        int k1 = (int) Math.sqrt(l * l + j1 * j1);
        int l1 = (int) (Math.atan2(i1, k1) * 325.94900000000001D) & 0x7ff;
        int i2 = (int) (Math.atan2(l, j1) * -325.94900000000001D) & 0x7ff;
        if (l1 < 128)
            l1 = 128;
        if (l1 > 383)
            l1 = 383;
        if (yCameraCurve < l1)
        {
            yCameraCurve += anInt998 + ((l1 - yCameraCurve) * anInt999) / 1000;
            if (yCameraCurve > l1)
                yCameraCurve = l1;
        }
        if (yCameraCurve > l1)
        {
            yCameraCurve -= anInt998 + ((yCameraCurve - l1) * anInt999) / 1000;
            if (yCameraCurve < l1)
                yCameraCurve = l1;
        }
        int j2 = i2 - xCameraCurve;
        if (j2 > 1024)
            j2 -= 2048;
        if (j2 < -1024)
            j2 += 2048;
        if (j2 > 0)
        {
            xCameraCurve += anInt998 + (j2 * anInt999) / 1000;
            xCameraCurve &= 0x7ff;
        }
        if (j2 < 0)
        {
            xCameraCurve -= anInt998 + (-j2 * anInt999) / 1000;
            xCameraCurve &= 0x7ff;
        }
        int k2 = i2 - xCameraCurve;
        if (k2 > 1024)
            k2 -= 2048;
        if (k2 < -1024)
            k2 += 2048;
        if (k2 < 0 && j2 > 0 || k2 > 0 && j2 < 0)
            xCameraCurve = i2;
    }

    private static int brightnessAmount = 100;

    private void drawMenu(int xOffSet, int yOffSet)
    {
        int xPos = menuOffsetX - (xOffSet - (clientSize == CLIENT_FIXED ? 4 : 0));
        int yPos = (-yOffSet + (clientSize == CLIENT_FIXED ? 4 : 0)) + menuOffsetY;
        int menuW = menuWidth;
        int menuH = menuHeight + 1;
        int color = 0x5d5447;
        if (clientSize == CLIENT_FIXED && menuOffsetX <= 513 && yPos + menuH >= 338)
            inputTaken = true;
        if (clientSize == CLIENT_FIXED && xPos + menuW >= 513 && yPos + menuH >= 165)
            needDrawTabArea = true;
        /*
         * if (transparentMenu) { DrawingArea.transparentBox(menuH, yPos, xPos,
         * color, menuW, 0, transparentMenuAmount); } else {
         */
        RSRaster.drawPixels(menuH, yPos, xPos, color, menuW);
        // }
        RSRaster.drawPixels(16, yPos + 1, xPos + 1, 0, menuW - 2);
        RSRaster.fillPixels(xPos + 1, menuW - 2, menuH - 19, 0, yPos + 18);
        chatText.method385(color, "Choose Option", yPos + 14, xPos + 3);
        int mouseX = super.mouseX - (xOffSet);
        int mouseY = (-yOffSet) + super.mouseY;
        for (int l1 = 0; l1 < menuActionRow; l1++)
        {
            int textY = yPos + 31 + (menuActionRow - 1 - l1) * 15;
            int disColor = 0xffffff;
            if (mouseX > xPos && mouseX < xPos + menuW && mouseY > textY - 13 && mouseY < textY + 3)
                disColor = 0xffff00;
            chatText.method389(true, xPos + 3, disColor, menuActionName[l1], textY);
        }
    }

    public void addFriend(long l)
    {
        try
        {
            if (l == 0L)
                return;
            if (friendsCount >= 100 && anInt1046 != 1)
            {
                pushMessage("Your friendlist is full. Max of 100 for free users, and 200 for members", 0, "");
                return;
            }
            if (friendsCount >= 200)
            {
                pushMessage("Your friendlist is full. Max of 100 for free users, and 200 for members", 0, "");
                return;
            }
            String s = TextClass.fixName(TextClass.nameForLong(l));
            for (int i = 0; i < friendsCount; i++)
                if (friendsListAsLongs[i] == l)
                {
                    pushMessage(capitalize(s) + " is already on your friend list", 0, "");
                    return;
                }
            for (int j = 0; j < ignoreCount; j++)
                if (ignoreListAsLongs[j] == l)
                {
                    pushMessage("Please remove " + capitalize(s) + " from your ignore list first", 0, "");
                    return;
                }
            if (s.equals(myPlayer.name))
            {
                return;
            }
            else
            {
                friendsList[friendsCount] = s;
                friendsListAsLongs[friendsCount] = l;
                friendsNodeIDs[friendsCount] = 0;
                friendsCount++;
                needDrawTabArea = true;
                stream.writeOpcode(188);
                stream.writeLong(l);
                return;
            }
        }
        catch (RuntimeException runtimeexception)
        {
            System.err.println("15283, " + (byte) 68 + ", " + l + ", " + runtimeexception.toString());
        }
        throw new RuntimeException();
    }

    private int method42(int floorLevel, int j, int k)
    { // this is a
      // getSomething() //
      // cant figure out
      // the other two
      // names of the
      // args,
        int l = k >> 7;
        int i1 = j >> 7;
        if (l < 0 || i1 < 0 || l > 103 || i1 > 103)
            return 0;
        int floor = floorLevel;
        if (floor < 3 && (byteGroundArray[1][l][i1] & 2) == 2)
            floor++;
        int k1 = k & 0x7f;
        int l1 = j & 0x7f;
        int i2 = intGroundArray[floor][l][i1] * (128 - k1) + intGroundArray[floor][l + 1][i1] * k1 >> 7;
        int j2 = intGroundArray[floor][l][i1 + 1] * (128 - k1) + intGroundArray[floor][l + 1][i1 + 1] * k1 >> 7;
        return i2 * (128 - l1) + j2 * l1 >> 7;
    }

    private static String intToKOrMil(int j)
    {
        if (j == -1)
            return "@inf@";
        if (j < 0x186a0)
            return String.valueOf(j);
        if (j < 0x989680)
            return j / 1000 + "K";
        else
            return j / 0xf4240 + "M";
    }

    public void resetLogout()
    {
        try
        {
            if (socketStream != null)
                socketStream.close();
        }
        catch (Exception _ex)
        {
            _ex.printStackTrace();
        }
        if (themeMusic)
            SoundProvider.getInstance().stopMidi();
        socketStream = null;
        loggedIn = false;
        loginScreenState = 0;
        inputString = "";
        startThemeMusic();
        // TODO: Remember to fill these.
        //myUsername =; // akzuuu
        //myPassword = "";
        status = "Idle...";
        clientSize = 0;
        unlinkMRUNodes();
        sceneGraph.initToNull();
        for (int i = 0; i < 4; i++)
            collision_maps[i].reset();
        System.gc();
        currentSong = -1;
        nextSong = -1;
        previousSong = 0;
    }

    public void method45()
    {
        char_edit_screen_update = true;
        for (int j = 0; j < 7; j++)
        {
            body_part_list[j] = -1;
            for (int k = 0; k < IdentityKit.length; k++)
            {
                if (IdentityKit.cache[k].notSelectable || IdentityKit.cache[k].bodyPartId != j + (gender ? 0 : 7))
                    continue;
                body_part_list[j] = k;
                break;
            }
        }
    }

    public void method46(int i, RSBuffer stream)
    {
        while (stream.bitPosition + 21 < i * 8)
        {
            // TODO: play with these
            int k = stream.readBits(14);
            if (k == 16383)
                break;
            if (npcArray[k] == null)
                npcArray[k] = new NPC();
            NPC npc = npcArray[k];
            npcIndices[npcCount++] = k;
            npc.anInt1537 = loopCycle;
            int l = stream.readBits(5);
            if (l > 15)
                l -= 32;
            int i1 = stream.readBits(5);
            if (i1 > 15)
                i1 -= 32;
            int j1 = stream.readBits(1);
            npc.desc = NpcDefintion.forID(stream.readBits(14));
            int k1 = stream.readBits(1);
            if (k1 == 1)
                anIntArray894[anInt893++] = k;
            npc.anInt1540 = npc.desc.size;
            npc.anInt1504 = npc.desc.getDegreesToTurn;
            npc.anInt1554 = npc.desc.walkAnimation;
            npc.anInt1555 = npc.desc.turn180Animation;
            npc.anInt1556 = npc.desc.turn90LeftAnimation;
            npc.anInt1557 = npc.desc.turn90RightAnimation;
            npc.anInt1511 = npc.desc.standAnimation;
            npc.setPos(myPlayer.smallX[0] + i1, myPlayer.smallY[0] + l, j1 == 1);
        }
        stream.finishBitAccess();
    }

    public void processGameLoop()
    {
        if (rsAlreadyLoaded || loadingError || genericLoadingError)
            return;
        loopCycle++;

        // Resizable desktop client stuff
        if (widget)
        {
            if (clientSize == 1 && super.gameFrame.getWidth() - super.gameFrame.getInsets().left - super.gameFrame.getInsets().right < RESIZABLE_WIDTH)
            {
                super.gameFrame.setSize(new Dimension(RESIZABLE_WIDTH + super.gameFrame.getInsets().left + super.gameFrame.getInsets().right, clientHeight + super.gameFrame.getInsets().top + super.gameFrame.getInsets().bottom));
                clientWidth = RESIZABLE_WIDTH;
                updateGame();
            }
            if (clientSize == 1 && super.gameFrame.getHeight() - super.gameFrame.getInsets().top - super.gameFrame.getInsets().bottom < RESIZABLE_HEIGHT)
            {
                super.gameFrame.setSize(new Dimension(clientWidth + super.gameFrame.getInsets().left + super.gameFrame.getInsets().right, RESIZABLE_HEIGHT + super.gameFrame.getInsets().top + super.gameFrame.getInsets().bottom));
                clientHeight = RESIZABLE_HEIGHT;
                updateGame();
            }
            if (clientSize == 1 && clientWidth != super.gameFrame.getWidth() - super.gameFrame.getInsets().left - super.gameFrame.getInsets().right)
            {
                clientWidth = super.gameFrame.getWidth() - super.gameFrame.getInsets().left - super.gameFrame.getInsets().right;
                updateGame();
            }
            if (clientSize == 1 && clientHeight != super.gameFrame.getHeight() - super.gameFrame.getInsets().top - super.gameFrame.getInsets().bottom)
            {
                clientHeight = super.gameFrame.getHeight() - super.gameFrame.getInsets().top - super.gameFrame.getInsets().bottom;
                updateGame();
            }
        }
        else
        {
            if (clientSize == 1 && super.getWidth() < RESIZABLE_WIDTH)
            {
                super.setSize(new Dimension(RESIZABLE_WIDTH, clientHeight));
                clientWidth = RESIZABLE_WIDTH;
                updateGame();
            }
            if (clientSize == 1 && super.getHeight() < RESIZABLE_HEIGHT)
            {
                super.setSize(new Dimension(clientWidth, RESIZABLE_HEIGHT));
                clientHeight = RESIZABLE_HEIGHT;
                updateGame();
            }
            if (clientSize == 1 && clientWidth != super.getWidth())
            {
                clientWidth = super.getWidth();
                updateGame();
            }
            if (clientSize == 1 && clientHeight != super.getHeight())
            {
                clientHeight = super.getHeight();
                updateGame();
            }
        }

        if (!loggedIn)
            processLoginScreenInput();
        else
            mainGameProcessor();
        processOnDemandQueue();
    }

    public void method47(boolean flag)
    {
        if (myPlayer.x >> 7 == destX && myPlayer.y >> 7 == destY)
            destX = 0;
        int j = playerCount;
        if (flag)
            j = 1;
        for (int l = 0; l < j; l++)
        {
            Player player;
            int i1;
            if (flag)
            {
                player = myPlayer;
                i1 = myPlayerIndex << 14;
            }
            else
            {
                player = playerArray[playerIndices[l]];
                i1 = playerIndices[l] << 14;
            }
            if (player == null || !player.isVisible())
                continue;
            // TODO: CHECK THIS MAY FUCK UP STAND ANIMS
            // player.aBoolean1699 = false;//
            player.aBoolean1699 = (lowMemory && playerCount > 50 || playerCount > 200) && !flag && player.anInt1517 == player.anInt1511;
            int j1 = player.x >> 7;
            int k1 = player.y >> 7;
            if (j1 < 0 || j1 >= 104 || k1 < 0 || k1 >= 104)
                continue;
            if (player.aModel_1714 != null && loopCycle >= player.anInt1707 && loopCycle < player.anInt1708)
            {
                player.aBoolean1699 = false;
                player.anInt1709 = method42(floor_level, player.y, player.x);
                sceneGraph.addEntity(floor_level, player.y, player, player.anInt1552, player.anInt1722, player.x, player.anInt1709, player.anInt1719, player.anInt1721, i1, player.anInt1720);
                continue;
            }
            if ((player.x & 0x7f) == 64 && (player.y & 0x7f) == 64)
            {
                if (anIntArrayArray929[j1][k1] == anInt1265)
                    continue;
                anIntArrayArray929[j1][k1] = anInt1265;
            }
            player.anInt1709 = method42(floor_level, player.y, player.x);
            sceneGraph.addRenderableA(floor_level, player.anInt1552, player.anInt1709, i1, player.y, 60, player.x, player, player.aBoolean1541);
        }
    }

    private boolean promptUserForInput(RSInterface class9)
    {
        int contentType = class9.contentType;
        if (anInt900 == 2)
        {
            if (contentType == 201)
            {
                if (chatHidden)
                    changeActiveChatStoneState(0);
                inputTaken = true;
                inputDialogState = 0;
                messagePromptRaised = true;
                promptInput = "";
                friendsListAction = 1;
                inputTitle = "Enter name of friend to add to list";
            }
            if (contentType == 202)
            {
                if (chatHidden)
                    changeActiveChatStoneState(0);
                inputTaken = true;
                inputDialogState = 0;
                messagePromptRaised = true;
                promptInput = "";
                friendsListAction = 2;
                inputTitle = "Enter name of friend to delete from list";
            }
        }
        if (contentType == 205)
        {
            anInt1011 = 250;
            return true;
        }
        if (contentType == 501)
        {
            if (chatHidden)
                changeActiveChatStoneState(0);
            inputTaken = true;
            inputDialogState = 0;
            messagePromptRaised = true;
            promptInput = "";
            friendsListAction = 4;
            inputTitle = "Enter name of player to add to list";
        }
        if (contentType == 502)
        {
            if (chatHidden)
                changeActiveChatStoneState(0);
            inputTaken = true;
            inputDialogState = 0;
            messagePromptRaised = true;
            promptInput = "";
            friendsListAction = 5;
            inputTitle = "Enter name of player to delete from list";
        }
        if (contentType >= 300 && contentType <= 313)
        {
            int k = (contentType - 300) / 2;
            int j1 = contentType & 1;
            int i2 = body_part_list[k];
            if (i2 != -1)
            {
                do
                {
                    if (j1 == 0 && --i2 < 0)
                        i2 = IdentityKit.length - 1;
                    if (j1 == 1 && ++i2 >= IdentityKit.length)
                        i2 = 0;
                }
                while (IdentityKit.cache[i2].notSelectable || IdentityKit.cache[i2].bodyPartId != k + (gender ? 0 : 7));
                body_part_list[k] = i2;
                char_edit_screen_update = true;
            }
        }
        if (contentType >= 314 && contentType <= 323)
        {
            int l = (contentType - 314) / 2;
            int k1 = contentType & 1;
            int j2 = player_outfit_colors[l];
            if (k1 == 0 && --j2 < 0)
                j2 = player_outfit_color_array[l].length - 1;
            if (k1 == 1 && ++j2 >= player_outfit_color_array[l].length)
                j2 = 0;
            player_outfit_colors[l] = j2;
            char_edit_screen_update = true;
        }
        if (contentType == 324 && !gender)
        {
            gender = true;
            method45();
        }
        if (contentType == 325 && gender)
        {
            gender = false;
            method45();
        }
        if (contentType == 326)
        {
            stream.writeOpcode(101);
            stream.writeByte(gender ? 0 : 1);
            for (int i1 = 0; i1 < 7; i1++)
                stream.writeByte(body_part_list[i1]);
            for (int l1 = 0; l1 < 5; l1++)
                stream.writeByte(player_outfit_colors[l1]);
            return true;
        }
        if (contentType == 620)
            canMute = !canMute;
        if (contentType >= 601 && contentType <= 613)
        {
            clearTopInterfaces();
            if (reportAbuseInput.length() > 0)
            {
                stream.writeOpcode(218);
                stream.writeLong(TextClass.longForName(reportAbuseInput));
                stream.writeByte(contentType - 601);
                stream.writeByte(canMute ? 1 : 0);
            }
        }
        return false;
    }

    public void method49(RSBuffer stream)
    {
        for (int j = 0; j < anInt893; j++)
        {
            int k = anIntArray894[j];
            Player player = playerArray[k];
            int l = stream.readUByte();
            if ((l & 0x40) != 0)
                l += stream.readUByte() << 8;
            method107(l, k, stream, player);
        }
    }

    public void drawMapOutline(int i, int k, int l, int i1, int j1)
    {
        int k1 = sceneGraph.getWallObjectUID(j1, l, i);
        if ((k1 ^ 0xffffffffffffffffL) != -1L)
        {
            int l1 = sceneGraph.getTileArrayIdForPosition(j1, l, i, k1);
            int k2 = l1 >> 6 & 3;
            int i3 = l1 & 0x1f;
            int k3 = k;
            if (k1 > 0)
                k3 = i1;
            int ai[] = miniMap.myPixels;
            int k4 = 24624 + l * 4 + (103 - i) * 512 * 4;
            int i5 = k1 >> 14 & 0x7fff;
            ObjectDefinition objDefinition_2 = ObjectDefinition.forID(i5);
            if ((objDefinition_2.mapScene ^ 0xffffffff) == 0)
            {
                if (i3 == 0 || i3 == 2)
                {
                    if (k2 == 0)
                    {
                        ai[k4] = k3;
                        ai[k4 + 512] = k3;
                        ai[1024 + k4] = k3;
                        ai[1536 + k4] = k3;
                    }
                    else if ((k2 ^ 0xffffffff) == -2)
                    {
                        ai[k4] = k3;
                        ai[k4 + 1] = k3;
                        ai[k4 + 2] = k3;
                        ai[3 + k4] = k3;
                    }
                    else if (k2 == 2)
                    {
                        ai[k4 - -3] = k3;
                        ai[3 + (k4 + 512)] = k3;
                        ai[3 + (k4 + 1024)] = k3;
                        ai[1536 + (k4 - -3)] = k3;
                    }
                    else if (k2 == 3)
                    {
                        ai[k4 + 1536] = k3;
                        ai[k4 + 1536 + 1] = k3;
                        ai[2 + k4 + 1536] = k3;
                        ai[k4 + 1539] = k3;
                    }
                }
                if (i3 == 3)
                    if (k2 == 0)
                        ai[k4] = k3;
                    else if (k2 == 1)
                        ai[k4 + 3] = k3;
                    else if (k2 == 2)
                        ai[k4 + 3 + 1536] = k3;
                    else if (k2 == 3)
                        ai[k4 + 1536] = k3;
                if (i3 == 2)
                    if (k2 == 3)
                    {
                        ai[k4] = k3;
                        ai[k4 + 512] = k3;
                        ai[k4 + 1024] = k3;
                        ai[k4 + 1536] = k3;
                    }
                    else if (k2 == 0)
                    {
                        ai[k4] = k3;
                        ai[k4 + 1] = k3;
                        ai[k4 + 2] = k3;
                        ai[k4 + 3] = k3;
                    }
                    else if (k2 == 1)
                    {
                        ai[k4 + 3] = k3;
                        ai[k4 + 3 + 512] = k3;
                        ai[k4 + 3 + 1024] = k3;
                        ai[k4 + 3 + 1536] = k3;
                    }
                    else if (k2 == 2)
                    {
                        ai[k4 + 1536] = k3;
                        ai[k4 + 1536 + 1] = k3;
                        ai[k4 + 1536 + 2] = k3;
                        ai[k4 + 1536 + 3] = k3;
                    }
            }
        }
        k1 = sceneGraph.getInteractiveObjectUID(j1, l, i);
        if (k1 != 0)
        {
            int i2 = sceneGraph.getTileArrayIdForPosition(j1, l, i, k1);
            int l2 = i2 >> 6 & 3;
            int j3 = i2 & 0x1f;
            int l3 = k1 >> 14 & 0x7fff;
            ObjectDefinition objDefinition_1 = ObjectDefinition.forID(l3);
            if (objDefinition_1.mapScene != -1)
            {
                IndexedImage background_1 = mapScenes[objDefinition_1.mapScene];
                if (background_1 != null)
                {
                    int j5 = (objDefinition_1.width * 4 - background_1.myWidth) / 2;
                    int k5 = (objDefinition_1.height * 4 - background_1.myHeight) / 2;
                    background_1.drawIndexedImage(48 + l * 4 + j5, 48 + (104 - i - objDefinition_1.height) * 4 + k5);
                }
            }
            else if (j3 == 9)
            {
                int l4 = 0xeeeeee;
                if (k1 > 0)
                    l4 = 0xee0000;
                int ai1[] = miniMap.myPixels;
                int l5 = 24624 + l * 4 + (103 - i) * 512 * 4;
                if (l2 == 0 || l2 == 2)
                {
                    ai1[l5 + 1536] = l4;
                    ai1[l5 + 1024 + 1] = l4;
                    ai1[l5 + 512 + 2] = l4;
                    ai1[l5 + 3] = l4;
                }
                else
                {
                    ai1[l5] = l4;
                    ai1[l5 + 512 + 1] = l4;
                    ai1[l5 + 1024 + 2] = l4;
                    ai1[l5 + 1536 + 3] = l4;
                }
            }
        }
        k1 = sceneGraph.getGroundDecortionUID(j1, l, i);
        if (k1 != 0)
        {
            int j2 = k1 >> 14 & 0x7fff;
            ObjectDefinition objDefinition = ObjectDefinition.forID(j2);
            if (objDefinition.mapScene != -1)
            {
                IndexedImage background = mapScenes[objDefinition.mapScene];
                if (background != null)
                {
                    int i4 = (objDefinition.width * 4 - background.myWidth) / 2;
                    int j4 = (objDefinition.height * 4 - background.myHeight) / 2;
                    background.drawIndexedImage(48 + l * 4 + i4, 48 + (104 - i - objDefinition.height) * 4 + j4);
                }
            }
        }
    }

    private static void setHighMem()
    {
        SceneGraph.lowMem = false;
        Rasterizer.lowMem = false;
        Region.lowMem = false;
        ObjectDefinition.lowMem = false;
    }

    public static void setLowMem()
    {
        SceneGraph.lowMem = true;
        Rasterizer.lowMem = true;
        Region.lowMem = true;
        ObjectDefinition.lowMem = true;
    }

    private static boolean music_enabled;

    /* public static void main(String[] args)
    {
        try
        {
            System.err.println("#main method");
            nodeID = 10;
            portOff = 0;
            isMembers = true;
            if (args[0].equals("lowmem"))
                lowMemory = true;
            else if (args[0].equals("highmem"))
            {
                lowMemory = false;
            }
            else
            {
                System.err.println("Usage: [lowmem/highmem], [sounds_off/sounds_on]");
                return;
            }
            if (args[1].equals("sounds_off"))
                music_enabled = false;
            else if (args[1].equals("sounds_on"))
            {
                music_enabled = !true;
                // TODO Fixing up music later on
            }
            else
            {
                System.err.println("Usage: [lowmem/highmem], [sounds_off/sounds_on]");
                return;
            }
            new Signlink();
            RSClient gameClient = new RSClient();
            gameClient.init_client(503, 765);
            instance = gameClient;
        }
        catch (Exception e)
        {
        }
    }*/
    
    public static void main(String[] args)
    {
        try
        {
            System.err.println("#main method");
            nodeID = 10;
            portOff = 0;
            isMembers = true;
            lowMemory = false;
            music_enabled = false;
            new Signlink();
            RSClient gameClient = new RSClient();
            gameClient.init_client(503, 765);
            instance = gameClient;
        }
        catch (Exception e)
        {
        }
    }

    public static RSClient instance;

    // TODO: COME HERE! AKZU!!!!!!!!
    public void loadingStages()
    {
        if (loadingStage == 1)
        {
            int j = method54();
            if (j != 0 && System.currentTimeMillis() - aLong824 > 0x57e40L)
            {
                System.err.println(myUsername + " glcfb " + aLong1215 + "," + j + "," + decompressors[0] + "," + resourceProvider.getNodeCount() + "," + floor_level + "," + anInt1069 + "," + anInt1070);
                aLong824 = System.currentTimeMillis();
            }
        }
        if (loadingStage == 2 && floor_level != anInt985)
        {
            anInt985 = floor_level;
            renderMapScene(floor_level);
        }
    }

    private int method54()
    {
        for (int i = 0; i < aByteArrayArray1183.length; i++)
        {
            if (aByteArrayArray1183[i] == null && anIntArray1235[i] != -1)
                return -1;
            if (aByteArrayArray1247[i] == null && anIntArray1236[i] != -1)
                return -2;
        }
        boolean flag = true;
        for (int j = 0; j < aByteArrayArray1183.length; j++)
        {
            byte abyte0[] = aByteArrayArray1247[j];
            if (abyte0 != null)
            {
                int k = (anIntArray1234[j] >> 8) * 64 - baseX;
                int l = (anIntArray1234[j] & 0xff) * 64 - baseY;
                if (aBoolean1159)
                {
                    k = 10;
                    l = 10;
                }
                flag &= Region.isObjectBlockedCached(k, abyte0, l);
            }
        }
        if (!flag)
            return -3;
        if (aBoolean1080)
        {
            return -4;
        }
        else
        {
            loadingStage = 2;
            Region.anInt131 = floor_level;
            method22();
            stream.writeOpcode(121);
            return 0;
        }
    }

    public void method55()
    {
        for (Projectile class30_sub2_sub4_sub4 = (Projectile) aClass19_1013.reverseGetFirst(); class30_sub2_sub4_sub4 != null; class30_sub2_sub4_sub4 = (Projectile) aClass19_1013.reverseGetNext())
            if (class30_sub2_sub4_sub4.anInt1597 != floor_level || loopCycle > class30_sub2_sub4_sub4.speedtime)
                class30_sub2_sub4_sub4.unlink();
            else if (loopCycle >= class30_sub2_sub4_sub4.delayTime)
            {
                if (class30_sub2_sub4_sub4.anInt1590 > 0)
                {
                    NPC npc = npcArray[class30_sub2_sub4_sub4.anInt1590 - 1];
                    if (npc != null && npc.x >= 0 && npc.x < 13312 && npc.y >= 0 && npc.y < 13312)
                        class30_sub2_sub4_sub4.method455(loopCycle, npc.y, method42(class30_sub2_sub4_sub4.anInt1597, npc.y, npc.x) - class30_sub2_sub4_sub4.anInt1583, npc.x);
                }
                if (class30_sub2_sub4_sub4.anInt1590 < 0)
                {
                    int j = -class30_sub2_sub4_sub4.anInt1590 - 1;
                    Player player;
                    if (j == playerID)
                        player = myPlayer;
                    else
                        player = playerArray[j];
                    if (player != null && player.x >= 0 && player.x < 13312 && player.y >= 0 && player.y < 13312)
                        class30_sub2_sub4_sub4.method455(loopCycle, player.y, method42(class30_sub2_sub4_sub4.anInt1597, player.y, player.x) - class30_sub2_sub4_sub4.anInt1583, player.x);
                }
                class30_sub2_sub4_sub4.method456(anInt945);
                sceneGraph.addRenderableA(floor_level, class30_sub2_sub4_sub4.anInt1595, (int) class30_sub2_sub4_sub4.aDouble1587, -1, (int) class30_sub2_sub4_sub4.aDouble1586, 60, (int) class30_sub2_sub4_sub4.aDouble1585, class30_sub2_sub4_sub4, false);
            }
    }

    public void processOnDemandQueue()
    {
        do
        {
            Resource onDemandData;
            // do {
            onDemandData = resourceProvider.getNextNode();
            if (onDemandData == null)
                return;
            if (onDemandData.dataType == 0 && onDemandData.buffer != null)
            {
                Model.method460(onDemandData.buffer, onDemandData.ID);
                needDrawTabArea = true;
                if (backDialogID != -1)
                    inputTaken = true;
            }
            if (onDemandData.dataType == 1 && onDemandData.buffer != null)
            {
                Animation.load(onDemandData.buffer, onDemandData.ID);
            }
            if (onDemandData.dataType == 2 && music_enabled && onDemandData.ID == nextSong && onDemandData.buffer != null)
                SoundProvider.getInstance().playMIDI(onDemandData.buffer);
            if (onDemandData.dataType == 3 && loadingStage == 1)
            {
                // remove loadingStage == 1
                // FileOperations.WriteFile(signlink.cacheLocation() +
                // "/dump/maps/" + onDemandData.ID + ".dat",
                // onDemandData.buffer);
                for (int i = 0; i < aByteArrayArray1183.length; i++)
                {
                    if (anIntArray1235[i] == onDemandData.ID)
                    {
                        aByteArrayArray1183[i] = onDemandData.buffer;
                        if (onDemandData.buffer == null)
                        {
                            anIntArray1235[i] = -1;
                        }
                        break;
                    }
                    if (anIntArray1236[i] != onDemandData.ID)
                        continue;
                    aByteArrayArray1247[i] = onDemandData.buffer;
                    if (onDemandData.buffer == null)
                    {
                        anIntArray1236[i] = -1;
                    }
                    break;
                }
            }
            // TODO: Dummy?? Akzu
            // } while (true);//onDemandData.dataType != 93 ||
            // !onDemandFetcher.method564(onDemandData.ID));
            // ObjectManager.method173(new Stream(onDemandData.buffer),
            // onDemandFetcher);
        }
        while (true);
    }

    public void method60(int i)
    {
        RSInterface class9 = RSInterface.interfaceCache[i];
        for (int j = 0; j < class9.children.length; j++)
        {
            if (class9.children[j] == -1)
                break;
            RSInterface class9_1 = RSInterface.interfaceCache[class9.children[j]];
            if (class9_1.type == 1)
                method60(class9_1.id);
            class9_1.animFrame = 0;
            class9_1.duration = 0;
        }
    }

    // TODO: AKZU DRAW HEAD ICONS
    private void drawHeadIcon()
    {
        if (hintType != 2)
        {
            return;
        }
        calcEntityScreenPos((anInt934 - baseX << 7) + anInt937, anInt936 * 2, (anInt935 - baseY << 7) + anInt938);
        if (spriteDrawX > -1 && loopCycle % 20 < 10)
        {
            headIconsHint[0].drawIndexedImage(spriteDrawX - 11, spriteDrawY - 28);
        }
    }

    private final boolean inDuelArenaFightArea(int x, int y)
    {
        return ((x >= 3363 && x <= 3389 && y >= 3244 && y <= 3258) || (x >= 3363 && x <= 3389 && y >= 3225 && y <= 3239) || (x >= 3363 && x <= 3389 && y >= 3206 && y <= 3220) || (x >= 3332 && x <= 3358 && y >= 3206 && y <= 3220) || (x >= 3332 && x <= 3358 && y >= 3225 && y <= 3239) || (x >= 3332 && x <= 3358 && y >= 3244 && y <= 3258));
    }

    private final boolean inDuelArenaOutskirt(int x, int y)
    {
        return (x >= 3328 && x <= 3395 && y >= 3200 && y <= 3287);
    }

    private final boolean inWilderness(int x, int y)
    {
        return (x > 2941 && x < 3392 && y >= 3525 && y < 3970) || (x > 2941 && x < 3392 && y > 9918 && y < 10366);
    }

    private final boolean inBarrows(int x, int y, int z)
    {
        return (x > 3539 && x < 3582 && y >= 9675 && y < 9722);
    }

    private final boolean inFightPits(int x, int y)
    {
        return (x > 2373 && x < 2424 && y >= 5126 && y < 5168);
    }

    private final boolean inAlchemyMinigame(int x, int y, int z)
    {
        return (x >= 3350 && x <= 3381 && y >= 9616 && y <= 9655 && z == 2);
    }

    private final boolean inGraveyardMinigame(int x, int y, int z)
    {
        return (x >= 3337 && x <= 3386 && y >= 9616 && y <= 9664 && z == 1);
    }

    private final boolean inTelekineticMinigame(int x, int y, int z)
    {
        return (x >= 3333 && x <= 3391 && y >= 9669 && y <= 9726 && z != 3);
    }

    private final boolean inEnchantmentMinigame(int x, int y, int z)
    {
        return (x >= 3340 && x <= 3387 && y >= 9616 && y <= 9662 && z == 0);
    }

    private static boolean inMultiArea(int x, int y)
    {
        return x >= 3029 && x <= 3374 && y >= 3759 && y <= 3903 || (x >= 2250 && x <= 2280 && y >= 4670 && y <= 4720) || (x >= 3198 && x <= 3380 && y >= 3904 && y <= 3970) || (x >= 3191 && x <= 3326 && y >= 3510 && y <= 3759) || (x >= 2987 && x <= 3006 && y >= 3912 && y <= 3937) || (x >= 2245 && x <= 2295 && y >= 4675 && y <= 4720) || (x >= 2450 && x <= 3520 && y >= 9450 && y <= 9550) || (x >= 3006 && x <= 3071 && y >= 3602 && y <= 3710) || (x >= 3134 && x <= 3192 && y >= 3519 && y <= 3646) || (x >= 2375 && x <= 2420 && y >= 5126 && y <= 5168);
    }

    public static int getWildernessLevel(int x, int y)
    {
        return (((y > 6400 ? y - 6400 : y) - 3524) / 8) + 1;
    }

    public void mainGameProcessor()
    {
        if (systemUpdateTime > 1)
            systemUpdateTime--;
        if (anInt1011 > 0)
            anInt1011--;
        for (int j = 0; j < 5; j++)
            if (!parsePacket())
                break;
        if (!loggedIn)
            return;

        /**
         * Wilderness levels & Multi areas
         */
        if (!walkableInterfaceMode)
        {
            if (inWilderness(baseX + (myPlayer.x - 6 >> 7), baseY + (myPlayer.y - 6 >> 7)))
            {
                sendString("Level: " + getWildernessLevel(baseX + (myPlayer.x - 6 >> 7), baseY + (myPlayer.y - 6 >> 7)), 199);
                atPlayerActions[0] = "Attack";
                atPlayerArray[0] = true;
                atPlayerActions[1] = null;
                atPlayerArray[1] = false;
                walkableInterface = 197;
            }
            else if (inFightPits(baseX + (myPlayer.x - 6 >> 7), baseY + (myPlayer.y - 6 >> 7)))
            {
                atPlayerActions[0] = "Attack";
                atPlayerArray[0] = true;
                atPlayerActions[1] = null;
                atPlayerArray[1] = false;
                walkableInterface = 2804;
            }
            else if (inAlchemyMinigame(baseX + (myPlayer.x - 6 >> 7), baseY + (myPlayer.y - 6 >> 7), floor_level))
            {
                walkableInterface = 15892;
            }
            else if (inGraveyardMinigame(baseX + (myPlayer.x - 6 >> 7), baseY + (myPlayer.y - 6 >> 7), floor_level))
            {
                walkableInterface = 15931;
            }
            else if (inTelekineticMinigame(baseX + (myPlayer.x - 6 >> 7), baseY + (myPlayer.y - 6 >> 7), floor_level))
            {
                walkableInterface = 15962;
            }
            else if (inEnchantmentMinigame(baseX + (myPlayer.x - 6 >> 7), baseY + (myPlayer.y - 6 >> 7), floor_level))
            {
                walkableInterface = 15917;
            }
            else if (inBarrows(baseX + (myPlayer.x - 6 >> 7), baseY + (myPlayer.y - 6 >> 7), floor_level))
            {
                walkableInterface = 4535;
            }
            else if (inDuelArenaOutskirt(baseX + (myPlayer.x - 6 >> 7), baseY + (myPlayer.y - 6 >> 7)))
            {
                if (inDuelArenaFightArea(baseX + (myPlayer.x - 6 >> 7), baseY + (myPlayer.y - 6 >> 7)))
                {
                    atPlayerActions[0] = "Fight";
                    atPlayerArray[0] = false;
                    atPlayerActions[1] = null;
                    atPlayerArray[1] = false;
                }
                else
                {
                    atPlayerActions[0] = null;
                    atPlayerArray[0] = false;
                    atPlayerActions[1] = "Challenge";
                    atPlayerArray[1] = true;
                }
                walkableInterface = 201;
            }
            else
            {
                atPlayerActions[0] = null;
                atPlayerArray[0] = false;
                atPlayerActions[1] = null;
                atPlayerArray[1] = false;
                walkableInterface = -1;
            }
        }
        else
        {
        }

        drawMultiIcon = (inMultiArea(baseX + (myPlayer.x - 6 >> 7), baseY + (myPlayer.y - 6 >> 7)) ? 1 : 0);

        if (anInt1016 > 0)
            anInt1016--;

        if (super.keyArray[1] == 1 || super.keyArray[2] == 1 || super.keyArray[3] == 1 || super.keyArray[4] == 1)
            aBoolean1017 = true;

        // Anti cheat shit
        /*
         * if (aBoolean1017 && anInt1016 <= 0) { anInt1016 = 20; aBoolean1017 =
         * false; stream.writeOpcode(86); stream.writeShort(anInt1184);
         * stream.method432(viewRotation); }
         */
        if (super.awtFocus && !aBoolean954)
        {
            aBoolean954 = true;
            inputTaken = true;
            needDrawTabArea = true;
            stream.writeOpcode(3);
            stream.writeByte(1);
        }
        if (!super.awtFocus && aBoolean954)
        {
            aBoolean954 = false;
            inputTaken = true;
            needDrawTabArea = true;
            stream.writeOpcode(3);
            stream.writeByte(0);
        }
        processChatTabs();
        loadingStages();
        updateSpawnedObjects();
        handleMusicEvents();
        timeoutCounter++;
        if (timeoutCounter > 750)
            dropClient();
        updatePlayerInstances();
        forceNpcUpdateBlock();
        resetMobSpokenText();
        anInt945++;
        if (crossType != 0)
        {
            crossIndex += 20;
            if (crossIndex >= 400)
                crossType = 0;
        }
        if (atInventoryInterfaceType != 0)
        {
            atInventoryLoopCycle++;
            if (atInventoryLoopCycle >= 15)
            {
                if (atInventoryInterfaceType == 2)
                    needDrawTabArea = true;
                if (atInventoryInterfaceType == 3)
                    inputTaken = true;
                atInventoryInterfaceType = 0;
            }
        }
        if (activeInterfaceType != 0)
        {
            anInt989++;
            if (super.mouseX > anInt1087 + 5 || super.mouseX < anInt1087 - 5 || super.mouseY > anInt1088 + 5 || super.mouseY < anInt1088 - 5)
                aBoolean1242 = true;
            if (super.clickMode2 == 0)
            {
                if (activeInterfaceType == 2)
                    needDrawTabArea = true;
                if (activeInterfaceType == 3)
                    inputTaken = true;
                activeInterfaceType = 0;
                if (aBoolean1242 && anInt989 >= 10)
                {
                    lastActiveInvInterface = -1;
                    processRightClick();
                    if (lastActiveInvInterface == anInt1084 && mouseInvInterfaceIndex != anInt1085)
                    {
                        RSInterface class9 = RSInterface.interfaceCache[anInt1084];
                        int j1 = 0;
                        if (anInt913 == 1 && class9.contentType == 206)
                            j1 = 1;
                        if (class9.inv[mouseInvInterfaceIndex] <= 0)
                            j1 = 0;
                        if (class9.dragDeletes)
                        {
                            int l2 = anInt1085;
                            int l3 = mouseInvInterfaceIndex;
                            class9.inv[l3] = class9.inv[l2];
                            class9.invStackSizes[l3] = class9.invStackSizes[l2];
                            class9.inv[l2] = -1;
                            class9.invStackSizes[l2] = 0;
                        }
                        else if (j1 == 1)
                        {
                            int i3 = anInt1085;
                            for (int i4 = mouseInvInterfaceIndex; i3 != i4;)
                                if (i3 > i4)
                                {
                                    class9.swapInventoryItems(i3, i3 - 1);
                                    i3--;
                                }
                                else if (i3 < i4)
                                {
                                    class9.swapInventoryItems(i3, i3 + 1);
                                    i3++;
                                }
                        }
                        else
                        {
                            class9.swapInventoryItems(anInt1085, mouseInvInterfaceIndex);
                        }
                        stream.writeOpcode(214);
                        stream.method433(anInt1084);
                        stream.readInverseByte(j1);
                        stream.method433(anInt1085);
                        stream.method431(mouseInvInterfaceIndex);
                    }
                }
                else if ((mouse_buttons == 1 || menuHasAddFriend(menuActionRow - 1)) && menuActionRow > 2)
                    determineMenuSize();
                else if (menuActionRow > 0)
                    doAction(menuActionRow - 1);
                atInventoryLoopCycle = 10;
                super.clickMode3 = 0;
            }
        }
        if (SceneGraph.clickedTileX != -1)
        {
            int k = SceneGraph.clickedTileX;
            int k1 = SceneGraph.clickedTileY;
            boolean flag = doWalkTo(0, 0, 0, 0, myPlayer.smallY[0], 0, 0, k1, myPlayer.smallX[0], true, k);
            SceneGraph.clickedTileX = -1;
            if (flag)
            {
                crossX = super.saveClickX;
                crossY = super.saveClickY;
                crossType = 1;
                crossIndex = 0;
            }
        }
        if (super.clickMode3 == 1 && aString844 != null)
        {
            aString844 = null;
            inputTaken = true;
            super.clickMode3 = 0;
        }
        if (!processMenuClick())
        {
            processMinimapClick();
        }
        if (screenDate)
        {
            screenDateTimer--;
        }
        if (screenDate && screenDateTimer < 1)
        {
            screenDate = false;
            screenDateTimer = 2;
            screenShot();
            reportAbuseText = "Report Abuse";
            inputTaken = true;
        }
        if (super.clickMode2 == 1)
            anInt1213++;
        if (((anInt1500 != 0 || anInt1044 != 0 || anInt1129 != 0) || (fullscreenInterfaceID == -1 && (anInt1039 != 0 || anInt1048 != 0 || anInt1026 != 0))) && !menuOpen)
        {
            if (anInt1501 < 50 && !menuOpen)
            {
                anInt1501++;
                if (anInt1501 == 50)
                {
                    if (anInt1500 != 0)
                    {
                        inputTaken = true;
                    }
                    if ((anInt1044 != 0) || (anInt1048 != 0))
                    {
                        needDrawTabArea = true;
                    }
                }
            }
        }
        else if (anInt1501 > 0)
        {
            anInt1501--;
        }
        if (loadingStage == 2)
            method108();
        if (loadingStage == 2 && aBoolean1160)
            calcCameraPos();
        for (int i1 = 0; i1 < 5; i1++)
            anIntArray1030[i1]++;
        handleInputOutput();
        super.idleTime++;
        if (super.idleTime > 4500)
        {
            anInt1011 = 250;
            super.idleTime -= 500;
            stream.writeOpcode(202);
        }
        anInt1010++;
        if (anInt1010 > 50)
            stream.writeOpcode(0);
        try
        {
            if (socketStream != null && stream.pointer > 0)
            {
                socketStream.queueBytes(stream.pointer, stream.buffer);
                stream.pointer = 0;
                anInt1010 = 0;
            }
        }
        catch (IOException _ex)
        {
            dropClient();
        }
        catch (Exception exception)
        {
            resetLogout();
        }
    }

    public void method63()
    {
        SpawnObjectNode class30_sub1 = (SpawnObjectNode) aClass19_1179.reverseGetFirst();
        for (; class30_sub1 != null; class30_sub1 = (SpawnObjectNode) aClass19_1179.reverseGetNext())
            if (class30_sub1.anInt1294 == -1)
            {
                class30_sub1.anInt1302 = 0;
                method89(class30_sub1);
            }
            else
            {
                class30_sub1.unlink();
            }
    }

    public void resetImageProducers()
    {
        if (loginScreen != null)
            return;
        System.err.println("resetImageProducer - called");
        super.fullGameScreen = null;
        chatImageProducer = null;
        mapAreaImageProducer = null;
        inventoryImageProducer = null;
        gameScreenImageProducer = null;
        RSRaster.setAllPixelsToZero();
        loginScreen = new RSImageProducer((clientSize == CLIENT_FIXED ? 765 : clientWidth), (clientSize == CLIENT_FIXED ? 503 : clientHeight), getGameComponent());
        RSRaster.setAllPixelsToZero();
        if (titleStreamLoader != null)
        {
            System.gc();
        }
        welcomeScreenRaised = true;
    }

    void drawLoadingText(int i, String s)
    {
        anInt1079 = i;
        aString1049 = s;
        resetImageProducers();
        if (titleStreamLoader == null)
        {
            super.drawLoadingText(i, s);
            return;
        }
        loginScreen.initDrawingArea();
        char c = '\u0168';
        char c1 = '\310';
        byte byte1 = 20;
        int offX = 202;
        int offY = 171;
        chatText.drawText(0xffffff, "Loading - please wait...", c1 / 2 - 26 - byte1 + offY, c / 2 + offX);
        int j = c1 / 2 - 18 - byte1;
        //RSRaster.drawAlphaGradient(222, 215, 300, 100, 0x808080, 0x000000, 70);
        RSRaster.fillPixels(c / 2 - 152 + offX, 304, 34, 0x8c1111, j + offY);
        RSRaster.fillPixels(c / 2 - 151 + offX, 302, 32, 0, j + 1 + offY);

        int color = 0x8c1111;
        int fade = 0;

        //RSRaster.drawGradient(c / 2 - 150 + offX, j + 2 + offY, 30 - fade, i * 3, 0xE60000, 0x800000);

        /*
         * Applies a gradient effect to the loading bar .. <3 Sneaky
         */
        while (fade <= 30)
        {
            //RSRaster.drawPixels(30 - fade, j + 2 + offY, c / 2 - 150 + offX, color, i * 3);
            RSRaster.drawAlphaGradient(c / 2 - 150 + offX, j + 2 + offY, i * 3, 30, 0xE60000, 0x800000, 40);
            color += 65536 * 3;
            fade += 1;
        }
        // RSRaster.drawPixels(30, j + 2 + offY, c / 2 - 150 + offX, 0x8c1111, i
        // * 3);

        //RSRaster.drawPixels(30, j + 2 + offY, (c / 2 - 150 + offX) + i * 3, 0, 300 - i * 3);
        chatText.drawText(0xffffff, s, (c1 / 2 + 5) - byte1 + offY, c / 2 + offX);
        loginScreen.drawGraphics(0, super.graphics, 0);
        loginBkg.drawSprite(0, 0);
        welcomeScreenRaised = !welcomeScreenRaised;
    }

    public void moveScroller(int i, int j, int k, int l, RSInterface class9, int i1, boolean flag, int j1)
    {
        int anInt992;
        if (aBoolean972)
            anInt992 = 32;
        else
            anInt992 = 0;
        aBoolean972 = false;
        if (k >= i && k < i + 16 && l >= i1 && l < i1 + 16)
        {
            class9.scrollPosition -= anInt1213 * 4;
            if (flag)
            {
                needDrawTabArea = true;
            }
        }
        else if (k >= i && k < i + 16 && l >= (i1 + j) - 16 && l < i1 + j)
        {
            class9.scrollPosition += anInt1213 * 4;
            if (flag)
            {
                needDrawTabArea = true;
            }
            // free scrolling
        }
        else if (k >= i - anInt992 && k < i + 16 + anInt992 && l >= i1 + 16 && l < (i1 + j) - 16 && anInt1213 > 0)
        {
            int l1 = ((j - 32) * j) / j1;
            if (l1 < 8)
                l1 = 8;
            int i2 = l - i1 - 16 - l1 / 2;
            int j2 = j - 32 - l1;
            class9.scrollPosition = (short) (((j1 - j) * i2) / j2);
            if (flag)
                needDrawTabArea = true;
            aBoolean972 = true;
        }
    }

    private boolean method66(int i, int j, int k)
    {
        int i1 = i >> 14 & 0x7fff;
        int j1 = sceneGraph.getTileArrayIdForPosition(floor_level, k, j, i);
        if (j1 == -1)
            return false;
        int k1 = j1 & 0x1f;
        int l1 = j1 >> 6 & 3;
        if (k1 == 10 || k1 == 11 || k1 == 22)
        {
            ObjectDefinition objDefinition = ObjectDefinition.forID(i1);
            int i2;
            int j2;
            if (l1 == 0 || l1 == 2)
            {
                i2 = objDefinition.width;
                j2 = objDefinition.height;
            }
            else
            {
                i2 = objDefinition.height;
                j2 = objDefinition.width;
            }
            int k2 = objDefinition.anInt768;
            if (l1 != 0)
                k2 = (k2 << l1 & 0xf) + (k2 >> 4 - l1);
            doWalkTo(2, 0, j2, 0, myPlayer.smallY[0], i2, k2, j, myPlayer.smallX[0], false, k);
        }
        else
        {
            doWalkTo(2, l1, 0, k1 + 1, myPlayer.smallY[0], 0, 0, j, myPlayer.smallX[0], false, k);
        }
        crossX = super.saveClickX;
        crossY = super.saveClickY;
        crossType = 2;
        crossIndex = 0;
        return true;
    }

    private CacheArchive streamLoaderForName(int i, String s, String s1, int j, int k)
    {
        byte abyte0[] = null;
        int l = 5;
        try
        {
            if (decompressors[0] != null)
                abyte0 = decompressors[0].decompress(i);
        }
        catch (Exception _ex)
        {
        }
        // TODO: Disable if you wanna update-server disabled
        if (abyte0 != null)
        {
            /*
             * aCRC32_930.reset(); aCRC32_930.update(abyte0); int i1 = (int)
             * aCRC32_930.getValue(); if (i1 != j) abyte0 = null;
             */
        }
        if (abyte0 != null)
        {
            CacheArchive streamLoader = new CacheArchive(abyte0);
            return streamLoader;
        }
        int j1 = 0;
        while (abyte0 == null)
        {
            String s2 = "Unknown error";
            drawLoadingText(k, "Requesting " + s);
            try
            {
                int k1 = 0;
                DataInputStream datainputstream = openJagGrabInputStream(s1 + j);
                byte abyte1[] = new byte[6];
                datainputstream.readFully(abyte1, 0, 6);
                RSBuffer stream = new RSBuffer(abyte1);
                stream.pointer = 3;
                int i2 = stream.read24Int() + 6;
                int j2 = 6;
                abyte0 = new byte[i2];
                System.arraycopy(abyte1, 0, abyte0, 0, 6);
                while (j2 < i2)
                {
                    int l2 = i2 - j2;
                    if (l2 > 1000)
                        l2 = 1000;
                    int j3 = datainputstream.read(abyte0, j2, l2);
                    if (j3 < 0)
                    {
                        s2 = "Length error: " + j2 + "/" + i2;
                        throw new IOException("EOF");
                    }
                    j2 += j3;
                    int k3 = (j2 * 100) / i2;
                    if (k3 != k1)
                        drawLoadingText(k, "Loading " + s + " - " + k3 + "%");
                    k1 = k3;
                }
                datainputstream.close();
                try
                {
                    if (decompressors[0] != null)
                        decompressors[0].method234(abyte0.length, abyte0, i);
                }
                catch (Exception _ex)
                {
                    decompressors[0] = null;
                }
                // TODO: Disable if you wanna update-server disabled
                if (abyte0 != null)
                {
                    /*
                     * aCRC32_930.reset(); aCRC32_930.update(abyte0); int i3 =
                     * (int) aCRC32_930.getValue(); if (i3 != j) { abyte0 =
                     * null; j1++; s2 = "Checksum error: " + i3; }
                     */
                }
            }
            catch (IOException ioexception)
            {
                if (s2.equals("Unknown error"))
                    s2 = "Connection error";
                abyte0 = null;
            }
            catch (NullPointerException _ex)
            {
                s2 = "Null error";
                abyte0 = null;
            }
            catch (ArrayIndexOutOfBoundsException _ex)
            {
                s2 = "Bounds error";
                abyte0 = null;
            }
            catch (Exception _ex)
            {
                s2 = "Unexpected error";
                abyte0 = null;
            }
            if (abyte0 == null)
            {
                for (int l1 = l; l1 > 0; l1--)
                {
                    if (j1 >= 3)
                    {
                        drawLoadingText(k, "Game updated - please reload page");
                        l1 = 10;
                    }
                    else
                    {
                        drawLoadingText(k, s2 + " - Retrying in " + l1);
                    }
                    try
                    {
                        Thread.sleep(1000L);
                    }
                    catch (Exception _ex)
                    {
                    }
                }
                l *= 2;
                if (l > 60)
                    l = 60;
            }
        }
        CacheArchive streamLoader_1 = new CacheArchive(abyte0);
        return streamLoader_1;
    }

    public void dropClient()
    {
        if (anInt1011 > 0)
        {
            resetLogout();
            return;
        }
        if (fullscreenInterfaceID == -1 && super.fullGameScreen != null)
        {
            if (gameScreenImageProducer != null)
                gameScreenImageProducer.initDrawingArea();
        }
        RSRaster.fillPixels(2, 229, 39, 0xffffff, 2);
        RSRaster.drawPixels(37, 3, 3, 0, 227);
        regularText.drawText(0, "Connection Lost", 19, 120);
        regularText.drawText(0xffffff, "Connection Lost", 18, 119);
        regularText.drawText(0, "Please Wait - Reconnecting", 34, 117);
        regularText.drawText(0xffffff, "Please Wait - Reconnecting", 34, 116);
        if (fullscreenInterfaceID == -1 && super.fullGameScreen != null && gameScreenImageProducer != null)
        {
            gameScreenImageProducer.drawGraphics((clientSize == CLIENT_FIXED ? 4 : 0), super.graphics, (clientSize == CLIENT_FIXED ? 4 : 0));
        }
        hideMinimap = 0;
        destX = 0;
        RSSocket rsSocket = socketStream;
        loggedIn = false;
        loginFailures = 0;
        login(myUsername, myPassword, true);
        if (!loggedIn)
            resetLogout();
        try
        {
            rsSocket.close();
        }
        catch (Exception _ex)
        {
        }
    }

    public void responsePrivateMessage()
    {
        String name = null;
        for (int j = 0; j < 100; j++)
            if (chatMessages[j] != null)
            {
                if (chatTypes[j] == 3 || chatTypes[j] == 7)
                {
                    name = chatNames[j];
                    break;
                }
            }
        if (name != null && name.startsWith("@cr"))
        {
            name = name.substring(5);
        }
        if (name == null)
            pushMessage("You have not received any private messages.", 0, "");
        try
        {
            if (name != null)
            {
                long namel = TextClass.longForName(name.trim());
                int node = -1;
                for (int count = 0; count < friendsCount; count++)
                {
                    if (friendsListAsLongs[count] != namel)
                        continue;
                    node = count;
                    break;
                }
                if (node != -1 && friendsNodeIDs[node] > 0)
                {
                    if (chatHidden)
                        changeActiveChatStoneState(0);
                    inputTaken = true;
                    inputDialogState = 0;
                    messagePromptRaised = true;
                    promptInput = "";
                    friendsListAction = 3;
                    aLong953 = friendsListAsLongs[node];
                    inputTitle = "Enter message to send to " + capitalize(friendsList[node]);
                }
                else if (node != -1)
                {
                    pushMessage("That player is currently offline.", 0, "");
                }
                else
                {
                    pushMessage("That player is not in your friends list.", 0, "");
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void doAction(int i)
    {
        if (i < 0)
            return;

        if (inputDialogState != 0)
        {
            inputDialogState = 0;
            inputTaken = true;
        }

        int j = menuActionCmd2[i];
        int k = menuActionCmd3[i];
        int l = menuActionID[i];
        int i1 = menuActionCmd1[i];
        if (l >= 2000)
            l -= 2000;

        if (l == 1100)
            setTab(0);
        if (l == 1101)
            setTab(1);
        if (l == 1102)
            setTab(2);
        if (l == 1103)
            setTab(3);
        if (l == 1104)
            setTab(4);
        if (l == 1105)
            setTab(5);
        if (l == 1106)
            setTab(6);
        if (l == 1108)
            setTab(8);
        if (l == 1109)
            setTab(9);
        if (l == 1110)
            setTab(10);
        if (l == 1111)
            setTab(11);
        if (l == 1112)
            setTab(12);
        if (l == 1113)
            setTab(13);
        if (l == 1114)
            setTab(7);
        if (l == 1115)
        {
            if (!changeActiveChatStoneState(2))
                hideChat();
        }
        if (l == 1120)
        {
            if (!changeActiveChatStoneState(0))
                hideChat();
        }
        if (l == 1121)
        {
            if (!changeActiveChatStoneState(1))
                hideChat();
        }
        if (l == 1140)
        {
            if (!changeActiveChatStoneState(3))
                hideChat();
        }
        if (l == 1131)
        {
            if (!changeActiveChatStoneState(4))
                hideChat();
        }
        if (l == 1144)
        {
            if (!changeActiveChatStoneState(5))
                hideChat();
        }
        if (l == 1116)
            changeChatMode(2, 0);
        if (l == 1117)
            changeChatMode(2, 1);
        if (l == 1118)
            changeChatMode(2, 2);
        if (l == 1119)
            changeChatMode(2, 3);
        if (l == 1141)
            changeChatMode(3, 0);
        if (l == 1142)
            changeChatMode(3, 1);
        if (l == 1143)
            changeChatMode(3, 2);
        if (l == 1145)
            changeChatMode(5, 0);
        if (l == 1146)
            changeChatMode(5, 1);
        if (l == 1147)
            changeChatMode(5, 2);
        if (l == 1130)
        {
            if (openInterfaceID != 5875)
            {
                if (openInterfaceID == -1)
                {
                    clearTopInterfaces();
                    reportAbuseInput = "";
                    canMute = false;
                    for (int i2 = 0; i2 < RSInterface.interfaceCache.length; i2++)
                    {
                        if (RSInterface.interfaceCache[i2] == null || RSInterface.interfaceCache[i2].contentType != 600)
                            continue;
                        reportAbuseInterfaceID = openInterfaceID = RSInterface.interfaceCache[i2].parentID;
                        break;
                    }
                }
                else
                {
                    pushMessage("Please close the interface you have open before using Report Abuse.", 0, "");
                }
            }
        }

        if (l == 582)
        {
            NPC npc = npcArray[i1];
            if (npc != null)
            {
                doWalkTo(2, 0, 1, 0, myPlayer.smallY[0], 1, 0, npc.smallY[0], myPlayer.smallX[0], false, npc.smallX[0]);
                crossX = super.saveClickX;
                crossY = super.saveClickY;
                crossType = 2;
                crossIndex = 0;
                stream.writeOpcode(57);
                stream.method432(anInt1285);
                stream.method432(i1);
                stream.method431(anInt1283);
                stream.method432(anInt1284);
            }
        }
        if (l == 234)
        {
            boolean flag1 = doWalkTo(2, 0, 0, 0, myPlayer.smallY[0], 0, 0, k, myPlayer.smallX[0], false, j);
            if (!flag1)
                flag1 = doWalkTo(2, 0, 1, 0, myPlayer.smallY[0], 1, 0, k, myPlayer.smallX[0], false, j);
            crossX = super.saveClickX;
            crossY = super.saveClickY;
            crossType = 2;
            crossIndex = 0;
            stream.writeOpcode(236);
            stream.method431(k + baseY);
            stream.writeShort(i1);
            stream.method431(j + baseX);
        }
        if (l == 62 && method66(i1, k, j))
        {
            stream.writeOpcode(192);
            stream.writeShort(anInt1284);
            stream.method431(i1 >> 14 & 0x7fff);
            stream.method433(k + baseY);
            stream.method431(anInt1283);
            stream.method433(j + baseX);
            stream.writeShort(anInt1285);
        }
        if (l == 511)
        {
            boolean flag2 = doWalkTo(2, 0, 0, 0, myPlayer.smallY[0], 0, 0, k, myPlayer.smallX[0], false, j);
            if (!flag2)
                flag2 = doWalkTo(2, 0, 1, 0, myPlayer.smallY[0], 1, 0, k, myPlayer.smallX[0], false, j);
            crossX = super.saveClickX;
            crossY = super.saveClickY;
            crossType = 2;
            crossIndex = 0;
            stream.writeOpcode(25);
            stream.method431(anInt1284);
            stream.method432(anInt1285);
            stream.writeShort(i1);
            stream.method432(k + baseY);
            stream.method433(anInt1283);
            stream.writeShort(j + baseX);
        }
        if (l == 74)
        {
            stream.writeOpcode(122);
            stream.method433(k);
            stream.method432(j);
            stream.method431(i1);
            atInventoryLoopCycle = 0;
            atInventoryInterface = k;
            atInventoryIndex = j;
            atInventoryInterfaceType = 2;
            if (RSInterface.interfaceCache[k].parentID == openInterfaceID)
                atInventoryInterfaceType = 1;
            if (RSInterface.interfaceCache[k].parentID == backDialogID)
                atInventoryInterfaceType = 3;
        }
        if (l == 315)
        {
            RSInterface class9 = RSInterface.interfaceCache[k];
            boolean flag8 = true;
            if (class9.contentType > 0)
                flag8 = promptUserForInput(class9);
            if (flag8)
            {
                // Resolution text childs...
                if (k >= 22855 && k <= 22884)
                {

                    if (super.screenManager.desired_mode == null)
                        return;

                    RSInterface klol = RSInterface.interfaceCache[22838];

                    super.screenManager.current_screen_pos = (byte) (k - 22855);
                    super.screenManager.desired_mode = super.screenManager.avaible_modes[super.screenManager.current_screen_pos];

                    klol.disabledMessage = "[" + super.screenManager.avaible_modes[super.screenManager.current_screen_pos].getWidth() + "x" + super.screenManager.avaible_modes[super.screenManager.current_screen_pos].getHeight() + "x" + super.screenManager.avaible_modes[super.screenManager.current_screen_pos].getBitDepth() + "]";
                    needDrawTabArea = true;

                    // Redraw the fullscreen as DisplayMode changed...
                    if (clientSize == 2)
                    {
                        super.clickMode2 = 0;
                        if (super.screenManager.desired_mode != null && super.screenManager.device.isDisplayChangeSupported())
                        {
                            super.screenManager.device.setDisplayMode(super.screenManager.desired_mode);
                        }
                        if (widget)
                        {
                            Insets insets = super.screenManager.window.getInsets();
                            super.setWindowInsets(insets);
                        }
                        RSClient.clientWidth = super.screenManager.desired_mode.getWidth();
                        RSClient.clientHeight = super.screenManager.desired_mode.getHeight();
                        updateGame();
                    }
                    return;
                }
                switch (k)
                {
                case 1672:
                    sendFrame248(4074, 3213);
                    break;
                case 1198:
                    updateScreenShotStrings();
                    sendChatInterface(1244);
                    break;
                case 22840:
                case 22841:
                case 22842:
                    toggleSize(k - 22840);
                    break;
                case 12944:
                    inputTaken = true;
                    inputDialogState = 3;
                    messagePromptRaised = false;
                    amountOrNameInput = "";
                    customMenuAddAction = 3;
                    inputTitle = "Set brightness % (Current: " + brightnessAmount + "%)";
                    break;
                case 1294:
                    inputTaken = true;
                    inputDialogState = 3;
                    messagePromptRaised = false;
                    amountOrNameInput = "";
                    customMenuAddAction = 2;
                    inputTitle = "Enter a custom filename prefix";
                    break;
                case 1370:
                    DATE_LOCAL = (DATE_LOCAL + 1) % DATE_FORMAT.length;
                    try
                    {
                        writeSettings();
                    }
                    catch (IOException e)
                    {
                    }
                    updateScreenShotStrings();
                    break;
                case 1393:
                    pictureRegionID = (pictureRegionID + (clientSize != 0 && pictureRegionID == 1 ? 2 : 1)) % pictureRegion.length;
                    try
                    {
                        writeSettings();
                    }
                    catch (IOException e)
                    {
                    }
                    updateScreenShotStrings();
                    break;
                case 1253:
                    pictureFormatQuality = (pictureFormatQuality + 1) % pictureFormat.length;
                    try
                    {
                        writeSettings();
                    }
                    catch (IOException e)
                    {
                    }
                    updateScreenShotStrings();
                    break;
                case 1293:
                    browseFolders(new File(Signlink.cacheLocation() + "pictures" + System.getProperty("file.separator")));
                    break;
                case 1363:
                    dateColour = (dateColour + 1) % DATE_COLOURS.length;
                    try
                    {
                        writeSettings();
                    }
                    catch (IOException e)
                    {
                    }
                    updateScreenShotStrings();
                    break;
                case 1254:
                    pictureCount = 0;
                    try
                    {
                        writeSettings();
                    }
                    catch (IOException e)
                    {
                    }
                    updateScreenShotStrings();
                    break;
                case 21361:
                    sendFrame248(-1, -1);
                    break;
                case 21383:
                    sendFrame248(-1, -1);
                    break;
                default:
                    stream.writeOpcode(185);
                    stream.writeShort(k);
                    break;
                }
            }
        }
        if (l == 561)
        {
            Player player = playerArray[i1];
            if (player != null)
            {
                doWalkTo(2, 0, 1, 0, myPlayer.smallY[0], 1, 0, player.smallY[0], myPlayer.smallX[0], false, player.smallX[0]);
                crossX = super.saveClickX;
                crossY = super.saveClickY;
                crossType = 2;
                crossIndex = 0;
                stream.writeOpcode(128);
                stream.writeShort(i1);
            }
        }
        if (l == 20)
        {
            NPC class30_sub2_sub4_sub1_sub1_1 = npcArray[i1];
            if (class30_sub2_sub4_sub1_sub1_1 != null)
            {
                doWalkTo(2, 0, 1, 0, myPlayer.smallY[0], 1, 0, class30_sub2_sub4_sub1_sub1_1.smallY[0], myPlayer.smallX[0], false, class30_sub2_sub4_sub1_sub1_1.smallX[0]);
                crossX = super.saveClickX;
                crossY = super.saveClickY;
                crossType = 2;
                crossIndex = 0;
                stream.writeOpcode(155);
                stream.method431(i1);
            }
        }
        if (l == 779)
        {
            Player class30_sub2_sub4_sub1_sub2_1 = playerArray[i1];
            if (class30_sub2_sub4_sub1_sub2_1 != null)
            {
                doWalkTo(2, 0, 1, 0, myPlayer.smallY[0], 1, 0, class30_sub2_sub4_sub1_sub2_1.smallY[0], myPlayer.smallX[0], false, class30_sub2_sub4_sub1_sub2_1.smallX[0]);
                crossX = super.saveClickX;
                crossY = super.saveClickY;
                crossType = 2;
                crossIndex = 0;
                stream.writeOpcode(153);
                stream.method431(i1);
            }
        }
        if (l == 516)
            if (!menuOpen)
                sceneGraph.request2DTrace(super.saveClickY - (clientSize == CLIENT_FIXED ? 4 : 0), super.saveClickX - (clientSize == CLIENT_FIXED ? 4 : 0));
            else
                sceneGraph.request2DTrace(k - (clientSize == CLIENT_FIXED ? 4 : 0), j - (clientSize == CLIENT_FIXED ? 4 : 0));
        if (l == 1062)
        {
            method66(i1, k, j);
            stream.writeOpcode(228);
            stream.method432(i1 >> 14 & 0x7fff);
            stream.method432(k + baseY);
            stream.writeShort(j + baseX);
        }
        if (l == 679 && !aBoolean1149)
        {
            stream.writeOpcode(40);
            stream.writeShort(k);
            aBoolean1149 = true;
        }
        if (l == 431)
        {
            stream.writeOpcode(129);
            stream.method432(j);
            stream.writeShort(k);
            stream.method432(i1);
            atInventoryLoopCycle = 0;
            atInventoryInterface = k;
            atInventoryIndex = j;
            atInventoryInterfaceType = 2;
            if (RSInterface.interfaceCache[k].parentID == openInterfaceID)
                atInventoryInterfaceType = 1;
            if (RSInterface.interfaceCache[k].parentID == backDialogID)
                atInventoryInterfaceType = 3;
        }
        if (l == 337 || l == 42 || l == 792 || l == 322)
        {
            String s = menuActionName[i];
            int k1 = s.indexOf("@whi@");
            if (k1 != -1)
            {
                long l3 = TextClass.longForName(s.substring(k1 + 5).trim());
                if (l == 337)
                    addFriend(l3);
                if (l == 42)
                    addIgnore(l3);
                if (l == 792)
                    delFriend(l3);
                if (l == 322)
                    delIgnore(l3);
            }
        }
        if (l == 53)
        {
            stream.writeOpcode(135);
            stream.method431(j);
            stream.method432(k);
            stream.method431(i1);
            atInventoryLoopCycle = 0;
            atInventoryInterface = k;
            atInventoryIndex = j;
            atInventoryInterfaceType = 2;
            if (RSInterface.interfaceCache[k].parentID == openInterfaceID)
                atInventoryInterfaceType = 1;
            if (RSInterface.interfaceCache[k].parentID == backDialogID)
                atInventoryInterfaceType = 3;
        }
        if (l == 539)
        {
            stream.writeOpcode(16);
            stream.method432(i1);
            stream.method433(j);
            stream.method433(k);
            atInventoryLoopCycle = 0;
            atInventoryInterface = k;
            atInventoryIndex = j;
            atInventoryInterfaceType = 2;
            if (RSInterface.interfaceCache[k].parentID == openInterfaceID)
                atInventoryInterfaceType = 1;
            if (RSInterface.interfaceCache[k].parentID == backDialogID)
                atInventoryInterfaceType = 3;
        }
        if (l == 484 || l == 6)
        {
            String s1 = menuActionName[i];
            int l1 = s1.indexOf("@whi@");
            if (l1 != -1)
            {
                s1 = s1.substring(l1 + 5).trim();
                String s7 = TextClass.fixName(TextClass.nameForLong(TextClass.longForName(s1)));
                boolean flag9 = false;
                for (int j3 = 0; j3 < playerCount; j3++)
                {
                    Player class30_sub2_sub4_sub1_sub2_7 = playerArray[playerIndices[j3]];
                    if (class30_sub2_sub4_sub1_sub2_7 == null || class30_sub2_sub4_sub1_sub2_7.name == null || !class30_sub2_sub4_sub1_sub2_7.name.equalsIgnoreCase(s7))
                        continue;
                    doWalkTo(2, 0, 1, 0, myPlayer.smallY[0], 1, 0, class30_sub2_sub4_sub1_sub2_7.smallY[0], myPlayer.smallX[0], false, class30_sub2_sub4_sub1_sub2_7.smallX[0]);
                    if (l == 484)
                    {
                        stream.writeOpcode(139);
                        stream.method431(playerIndices[j3]);
                    }
                    if (l == 6)
                    {
                        stream.writeOpcode(153);
                        stream.method431(playerIndices[j3]);
                    }
                    flag9 = true;
                    break;
                }
                if (!flag9)
                    pushMessage("Unable to find " + capitalize(s7), 0, "");
            }
        }
        if (l == 870)
        {
            stream.writeOpcode(53);
            stream.writeShort(j);
            stream.method432(anInt1283);
            stream.method433(i1);
            stream.writeShort(anInt1284);
            stream.method431(anInt1285);
            stream.writeShort(k);
            atInventoryLoopCycle = 0;
            atInventoryInterface = k;
            atInventoryIndex = j;
            atInventoryInterfaceType = 2;
            if (RSInterface.interfaceCache[k].parentID == openInterfaceID)
                atInventoryInterfaceType = 1;
            if (RSInterface.interfaceCache[k].parentID == backDialogID)
                atInventoryInterfaceType = 3;
        }
        if (l == 847)
        {
            stream.writeOpcode(87);
            stream.method432(i1);
            stream.writeShort(k);
            stream.method432(j);
            atInventoryLoopCycle = 0;
            atInventoryInterface = k;
            atInventoryIndex = j;
            atInventoryInterfaceType = 2;
            if (RSInterface.interfaceCache[k].parentID == openInterfaceID)
                atInventoryInterfaceType = 1;
            if (RSInterface.interfaceCache[k].parentID == backDialogID)
                atInventoryInterfaceType = 3;
        }
        if (l == 626)
        {
            RSInterface class9_1 = RSInterface.interfaceCache[k];
            spellSelected = 1;
            spellID = class9_1.id;
            anInt1137 = k;
            spellUsableOn = class9_1.spellUsableOn;
            itemSelected = 0;
            needDrawTabArea = true;
            String s4 = class9_1.selectedActionName;
            if (s4.indexOf(" ") != -1)
                s4 = s4.substring(0, s4.indexOf(" "));
            String s8 = class9_1.selectedActionName;
            if (s8.indexOf(" ") != -1)
                s8 = s8.substring(s8.indexOf(" ") + 1);
            if (s8.equals("on") || s8.equals("On"))
                s8 = "@whi@->";
            spellTooltip = s4 + " " + "@gre@" + class9_1.spellName + " " + s8;
            if (spellUsableOn == 16)
            {
                needDrawTabArea = true;
                tabID = 3;
            }
            return;
        }
        if (l == 78)
        {
            stream.writeOpcode(117);
            stream.method433(k);
            stream.method433(i1);
            stream.method431(j);
            atInventoryLoopCycle = 0;
            atInventoryInterface = k;
            atInventoryIndex = j;
            atInventoryInterfaceType = 2;
            if (RSInterface.interfaceCache[k].parentID == openInterfaceID)
                atInventoryInterfaceType = 1;
            if (RSInterface.interfaceCache[k].parentID == backDialogID)
                atInventoryInterfaceType = 3;
        }
        if (l == 27)
        {
            Player class30_sub2_sub4_sub1_sub2_2 = playerArray[i1];
            if (class30_sub2_sub4_sub1_sub2_2 != null)
            {
                doWalkTo(2, 0, 1, 0, myPlayer.smallY[0], 1, 0, class30_sub2_sub4_sub1_sub2_2.smallY[0], myPlayer.smallX[0], false, class30_sub2_sub4_sub1_sub2_2.smallX[0]);
                crossX = super.saveClickX;
                crossY = super.saveClickY;
                crossType = 2;
                crossIndex = 0;
                stream.writeOpcode(73);
                stream.method431(i1);
            }
        }
        if (l == 213)
        {
            boolean flag3 = doWalkTo(2, 0, 0, 0, myPlayer.smallY[0], 0, 0, k, myPlayer.smallX[0], false, j);
            if (!flag3)
                flag3 = doWalkTo(2, 0, 1, 0, myPlayer.smallY[0], 1, 0, k, myPlayer.smallX[0], false, j);
            crossX = super.saveClickX;
            crossY = super.saveClickY;
            crossType = 2;
            crossIndex = 0;
            stream.writeOpcode(79);
            stream.method431(k + baseY);
            stream.writeShort(i1);
            stream.method432(j + baseX);
        }
        if (l == 632)
        {
            stream.writeOpcode(145);
            stream.method432(k);
            stream.method432(j);
            stream.method432(i1);
            atInventoryLoopCycle = 0;
            atInventoryInterface = k;
            atInventoryIndex = j;
            atInventoryInterfaceType = 2;
            if (RSInterface.interfaceCache[k].parentID == openInterfaceID)
                atInventoryInterfaceType = 1;
            if (RSInterface.interfaceCache[k].parentID == backDialogID)
                atInventoryInterfaceType = 3;
        }
        if (l == 493)
        {
            stream.writeOpcode(75);
            stream.method433(k);
            stream.method431(j);
            stream.method432(i1);
            atInventoryLoopCycle = 0;
            atInventoryInterface = k;
            atInventoryIndex = j;
            atInventoryInterfaceType = 2;
            if (RSInterface.interfaceCache[k].parentID == openInterfaceID)
                atInventoryInterfaceType = 1;
            if (RSInterface.interfaceCache[k].parentID == backDialogID)
                atInventoryInterfaceType = 3;
        }
        if (l == 652)
        {
            boolean flag4 = doWalkTo(2, 0, 0, 0, myPlayer.smallY[0], 0, 0, k, myPlayer.smallX[0], false, j);
            if (!flag4)
                flag4 = doWalkTo(2, 0, 1, 0, myPlayer.smallY[0], 1, 0, k, myPlayer.smallX[0], false, j);
            crossX = super.saveClickX;
            crossY = super.saveClickY;
            crossType = 2;
            crossIndex = 0;
            stream.writeOpcode(156);
            stream.method432(j + baseX);
            stream.method431(k + baseY);
            stream.method433(i1);
        }
        if (l == 94)
        {
            boolean flag5 = doWalkTo(2, 0, 0, 0, myPlayer.smallY[0], 0, 0, k, myPlayer.smallX[0], false, j);
            if (!flag5)
                flag5 = doWalkTo(2, 0, 1, 0, myPlayer.smallY[0], 1, 0, k, myPlayer.smallX[0], false, j);
            crossX = super.saveClickX;
            crossY = super.saveClickY;
            crossType = 2;
            crossIndex = 0;
            stream.writeOpcode(181);
            stream.method431(k + baseY);
            stream.writeShort(i1);
            stream.method431(j + baseX);
            stream.method432(anInt1137);
        }
        if (l == 646)
        {
            stream.writeOpcode(185);
            stream.writeShort(k);
            RSInterface class9_2 = RSInterface.interfaceCache[k];
            if (class9_2.valueIndexArray != null && class9_2.valueIndexArray[0][0] == 5)
            {
                int i2 = class9_2.valueIndexArray[0][1];
                if (variousSettings[i2] != class9_2.requiredValues[0])
                {
                    variousSettings[i2] = class9_2.requiredValues[0];
                    method33(i2);
                    needDrawTabArea = true;
                }
            }
        }
        if (l == 225)
        {
            NPC class30_sub2_sub4_sub1_sub1_2 = npcArray[i1];
            if (class30_sub2_sub4_sub1_sub1_2 != null)
            {
                doWalkTo(2, 0, 1, 0, myPlayer.smallY[0], 1, 0, class30_sub2_sub4_sub1_sub1_2.smallY[0], myPlayer.smallX[0], false, class30_sub2_sub4_sub1_sub1_2.smallX[0]);
                crossX = super.saveClickX;
                crossY = super.saveClickY;
                crossType = 2;
                crossIndex = 0;
                stream.writeOpcode(17);
                stream.method433(i1);
            }
        }
        if (l == 965)
        {
            NPC class30_sub2_sub4_sub1_sub1_3 = npcArray[i1];
            if (class30_sub2_sub4_sub1_sub1_3 != null)
            {
                doWalkTo(2, 0, 1, 0, myPlayer.smallY[0], 1, 0, class30_sub2_sub4_sub1_sub1_3.smallY[0], myPlayer.smallX[0], false, class30_sub2_sub4_sub1_sub1_3.smallX[0]);
                crossX = super.saveClickX;
                crossY = super.saveClickY;
                crossType = 2;
                crossIndex = 0;
                stream.writeOpcode(21);
                stream.writeShort(i1);
            }
        }
        if (l == 413)
        {
            NPC class30_sub2_sub4_sub1_sub1_4 = npcArray[i1];
            if (class30_sub2_sub4_sub1_sub1_4 != null)
            {
                doWalkTo(2, 0, 1, 0, myPlayer.smallY[0], 1, 0, class30_sub2_sub4_sub1_sub1_4.smallY[0], myPlayer.smallX[0], false, class30_sub2_sub4_sub1_sub1_4.smallX[0]);
                crossX = super.saveClickX;
                crossY = super.saveClickY;
                crossType = 2;
                crossIndex = 0;
                stream.writeOpcode(131);
                stream.method433(i1);
                stream.method432(anInt1137);
            }
        }
        if (l == 200)
        {
            super.clickMode3 = 0;
            clearTopInterfaces();
        }
        if (l == 1025)
        {
            NPC class30_sub2_sub4_sub1_sub1_5 = npcArray[i1];
            if (class30_sub2_sub4_sub1_sub1_5 != null)
            {
                NpcDefintion entityDef = class30_sub2_sub4_sub1_sub1_5.desc;
                if (entityDef.childrenIDs != null)
                    entityDef = entityDef.method161();
                if (entityDef != null)
                {
                    String description;
                    if (entityDef.description != null)
                        description = new String(entityDef.description).replaceAll("RuneScape", "Exorth");
                    else
                        description = entityDef.name;
                    pushMessage(description, 0, "");
                }
            }
        }
        if (l == 900)
        {
            method66(i1, k, j);
            stream.writeOpcode(252);
            stream.method433(i1 >> 14 & 0x7fff);
            stream.method431(k + baseY);
            stream.method432(j + baseX);
        }
        if (l == 412)
        {
            NPC class30_sub2_sub4_sub1_sub1_6 = npcArray[i1];
            if (class30_sub2_sub4_sub1_sub1_6 != null)
            {
                doWalkTo(2, 0, 1, 0, myPlayer.smallY[0], 1, 0, class30_sub2_sub4_sub1_sub1_6.smallY[0], myPlayer.smallX[0], false, class30_sub2_sub4_sub1_sub1_6.smallX[0]);
                crossX = super.saveClickX;
                crossY = super.saveClickY;
                crossType = 2;
                crossIndex = 0;
                stream.writeOpcode(72);
                stream.method432(i1);
            }
        }
        if (l == 365)
        {
            Player class30_sub2_sub4_sub1_sub2_3 = playerArray[i1];
            if (class30_sub2_sub4_sub1_sub2_3 != null)
            {
                doWalkTo(2, 0, 1, 0, myPlayer.smallY[0], 1, 0, class30_sub2_sub4_sub1_sub2_3.smallY[0], myPlayer.smallX[0], false, class30_sub2_sub4_sub1_sub2_3.smallX[0]);
                crossX = super.saveClickX;
                crossY = super.saveClickY;
                crossType = 2;
                crossIndex = 0;
                stream.writeOpcode(249);
                stream.method432(i1);
                stream.method431(anInt1137);
            }
        }
        if (l == 729)
        {
            Player class30_sub2_sub4_sub1_sub2_4 = playerArray[i1];
            if (class30_sub2_sub4_sub1_sub2_4 != null)
            {
                doWalkTo(2, 0, 1, 0, myPlayer.smallY[0], 1, 0, class30_sub2_sub4_sub1_sub2_4.smallY[0], myPlayer.smallX[0], false, class30_sub2_sub4_sub1_sub2_4.smallX[0]);
                crossX = super.saveClickX;
                crossY = super.saveClickY;
                crossType = 2;
                crossIndex = 0;
                stream.writeOpcode(39);
                stream.method431(i1);
            }
        }
        if (l == 577)
        {
            Player class30_sub2_sub4_sub1_sub2_5 = playerArray[i1];
            if (class30_sub2_sub4_sub1_sub2_5 != null)
            {
                doWalkTo(2, 0, 1, 0, myPlayer.smallY[0], 1, 0, class30_sub2_sub4_sub1_sub2_5.smallY[0], myPlayer.smallX[0], false, class30_sub2_sub4_sub1_sub2_5.smallX[0]);
                crossX = super.saveClickX;
                crossY = super.saveClickY;
                crossType = 2;
                crossIndex = 0;
                stream.writeOpcode(139);
                stream.method431(i1);
            }
        }
        if (l == 956 && method66(i1, k, j))
        {
            stream.writeOpcode(35);
            stream.method431(j + baseX);
            stream.method432(anInt1137);
            stream.method432(k + baseY);
            stream.method431(i1 >> 14 & 0x7fff);
        }
        if (l == 567)
        {
            boolean flag6 = doWalkTo(2, 0, 0, 0, myPlayer.smallY[0], 0, 0, k, myPlayer.smallX[0], false, j);
            if (!flag6)
                flag6 = doWalkTo(2, 0, 1, 0, myPlayer.smallY[0], 1, 0, k, myPlayer.smallX[0], false, j);
            crossX = super.saveClickX;
            crossY = super.saveClickY;
            crossType = 2;
            crossIndex = 0;
            stream.writeOpcode(23);
            stream.method431(k + baseY);
            stream.method431(i1);
            stream.method431(j + baseX);
        }
        if (l == 867)
        {
            stream.writeOpcode(43);
            stream.method431(k);
            stream.method432(i1);
            stream.method432(j);
            atInventoryLoopCycle = 0;
            atInventoryInterface = k;
            atInventoryIndex = j;
            atInventoryInterfaceType = 2;
            if (RSInterface.interfaceCache[k].parentID == openInterfaceID)
                atInventoryInterfaceType = 1;
            if (RSInterface.interfaceCache[k].parentID == backDialogID)
                atInventoryInterfaceType = 3;
        }
        if (l == 543)
        {
            stream.writeOpcode(237);
            stream.writeShort(j);
            stream.method432(i1);
            stream.writeShort(k);
            stream.method432(anInt1137);
            atInventoryLoopCycle = 0;
            atInventoryInterface = k;
            atInventoryIndex = j;
            atInventoryInterfaceType = 2;
            setTab(6);
            if (RSInterface.interfaceCache[k].parentID == openInterfaceID)
                atInventoryInterfaceType = 1;
            if (RSInterface.interfaceCache[k].parentID == backDialogID)
                atInventoryInterfaceType = 3;
        }
        if (l == 606)
        {
            String s2 = menuActionName[i];
            int j2 = s2.indexOf("@whi@");
            if (j2 != -1)
                if (openInterfaceID == -1)
                {
                    clearTopInterfaces();
                    reportAbuseInput = s2.substring(j2 + 5).trim();
                    canMute = false;
                    for (int i3 = 0; i3 < RSInterface.interfaceCache.length; i3++)
                    {
                        if (RSInterface.interfaceCache[i3] == null || RSInterface.interfaceCache[i3].contentType != 600)
                            continue;
                        reportAbuseInterfaceID = openInterfaceID = RSInterface.interfaceCache[i3].parentID;
                        break;
                    }
                }
                else
                {
                    pushMessage("Please close the interface you have open before using 'report abuse'", 0, "");
                }
        }
        if (l == 491)
        {
            Player class30_sub2_sub4_sub1_sub2_6 = playerArray[i1];
            if (class30_sub2_sub4_sub1_sub2_6 != null)
            {
                doWalkTo(2, 0, 1, 0, myPlayer.smallY[0], 1, 0, class30_sub2_sub4_sub1_sub2_6.smallY[0], myPlayer.smallX[0], false, class30_sub2_sub4_sub1_sub2_6.smallX[0]);
                crossX = super.saveClickX;
                crossY = super.saveClickY;
                crossType = 2;
                crossIndex = 0;
                stream.writeOpcode(14);
                stream.method432(anInt1284);
                stream.writeShort(i1);
                stream.writeShort(anInt1285);
                stream.method431(anInt1283);
            }
        }
        if (l == 639)
        {
            String s3 = menuActionName[i];
            int k2 = s3.indexOf("@whi@");
            if (k2 != -1)
            {
                long l4 = TextClass.longForName(s3.substring(k2 + 5).trim());
                if (chatHidden)
                    changeActiveChatStoneState(0);
                inputTaken = true;
                inputDialogState = 0;
                messagePromptRaised = true;
                promptInput = "";
                friendsListAction = 3;
                aLong953 = l4;
                inputTitle = "Enter message to send to " + capitalize(s3.substring(k2 + 5).trim());
            }
        }
        if (l == 454)
        {
            stream.writeOpcode(41);
            stream.writeShort(i1);
            stream.method432(j);
            stream.method432(k);
            atInventoryLoopCycle = 0;
            atInventoryInterface = k;
            atInventoryIndex = j;
            atInventoryInterfaceType = 2;
            if (RSInterface.interfaceCache[k].parentID == openInterfaceID)
                atInventoryInterfaceType = 1;
            if (RSInterface.interfaceCache[k].parentID == backDialogID)
                atInventoryInterfaceType = 3;
        }
        if (l == 478)
        {
            NPC class30_sub2_sub4_sub1_sub1_7 = npcArray[i1];
            if (class30_sub2_sub4_sub1_sub1_7 != null)
            {
                doWalkTo(2, 0, 1, 0, myPlayer.smallY[0], 1, 0, class30_sub2_sub4_sub1_sub1_7.smallY[0], myPlayer.smallX[0], false, class30_sub2_sub4_sub1_sub1_7.smallX[0]);
                crossX = super.saveClickX;
                crossY = super.saveClickY;
                crossType = 2;
                crossIndex = 0;
                stream.writeOpcode(18);
                stream.method431(i1);
            }
        }
        if (l == 113)
        {
            method66(i1, k, j);
            stream.writeOpcode(70);
            stream.method431(j + baseX);
            stream.writeShort(k + baseY);
            stream.method433(i1 >> 14 & 0x7fff);
        }
        if (l == 872)
        {
            method66(i1, k, j);
            stream.writeOpcode(234);
            stream.method433(j + baseX);
            stream.method432(i1 >> 14 & 0x7fff);
            stream.method433(k + baseY);
        }
        if (l == 502)
        {
            method66(i1, k, j);
            stream.writeOpcode(132);
            stream.method433(j + baseX);
            stream.writeShort(i1 >> 14 & 0x7fff);
            stream.method432(k + baseY);
        }
        if (l == 1125)
        {
            atInventoryLoopCycle = 0;
            atInventoryInterface = k;
            atInventoryIndex = j;
            atInventoryInterfaceType = 2;
            ItemDefinition itemDef = ItemDefinition.forID(i1);
            RSInterface class9_4 = RSInterface.interfaceCache[k];
            String s5;
            Locale locale = Locale.US;
            String s6 = NumberFormat.getNumberInstance(locale).format(class9_4.invStackSizes[j]);
            if (class9_4 != null && class9_4.invStackSizes[j] >= 100000)
            {
                s5 = s6 + " x " + itemDef.name;
            }
            else if (itemDef.description != null)
            {
                s5 = new String(itemDef.description);
            }
            else
            {
                s5 = itemDef.name;
            }
            pushMessage(s5, 0, "");
        }
        if (l == 169)
        {
            super.clickMode3 = 0;
            switch (k)
            {
            case 12946:
                // TODO Fixing up music later on
                if (1 + 1 == 2)
                    break;
                boolean music = music_enabled;
                music_enabled = !music_enabled;
                if (music_enabled != music)
                {
                    if (music_enabled)
                    {
                        nextSong = currentSong;
                        resourceProvider.method558(2, nextSong);
                    }
                    else
                    {
                        SoundProvider.getInstance().stopMidi();
                    }
                    previousSong = 0;
                }
                anIntArray1045[168] = music_enabled ? 1 : 0;
                variousSettings[168] = music_enabled ? 1 : 0;
                method33(168);
                break;
            default:
                stream.writeOpcode(185);
                stream.writeShort(k);
                break;
            }
            RSInterface class9_3 = RSInterface.interfaceCache[k];
            if (class9_3.valueIndexArray != null && class9_3.valueIndexArray[0][0] == 5)
            {
                int l2 = class9_3.valueIndexArray[0][1];
                variousSettings[l2] = 1 - variousSettings[l2];
                method33(l2);
                needDrawTabArea = true;
            }
        }
        if (l == 447)
        {
            itemSelected = 1;
            anInt1283 = j;
            anInt1284 = k;
            anInt1285 = i1;
            selectedItemName = ItemDefinition.forID(i1).name;
            spellSelected = 0;
            needDrawTabArea = true;
            return;
        }
        if (l == 1226)
        {
            int j1 = i1 >> 14 & 0x7fff;
            ObjectDefinition objDefinition = ObjectDefinition.forID(j1);
            String s10;
            if (objDefinition.description != null)
                s10 = new String(objDefinition.description).replaceAll("RuneScape", "Exorth");
            else
                s10 = objDefinition.name;
            pushMessage(s10, 0, "");
        }
        if (l == 244)
        {
            boolean flag7 = doWalkTo(2, 0, 0, 0, myPlayer.smallY[0], 0, 0, k, myPlayer.smallX[0], false, j);
            if (!flag7)
                flag7 = doWalkTo(2, 0, 1, 0, myPlayer.smallY[0], 1, 0, k, myPlayer.smallX[0], false, j);
            crossX = super.saveClickX;
            crossY = super.saveClickY;
            crossType = 2;
            crossIndex = 0;
            stream.writeOpcode(253);
            stream.method431(j + baseX);
            stream.method433(k + baseY);
            stream.method432(i1);
        }
        if (l == 1448)
        {
            ItemDefinition itemDef_1 = ItemDefinition.forID(i1);
            String s6;
            if (itemDef_1.description != null)
                s6 = new String(itemDef_1.description);
            else
                s6 = itemDef_1.name;
            pushMessage(s6, 0, "");
        }
        itemSelected = 0;
        spellSelected = 0;
        needDrawTabArea = true;
    }

    public void method70()
    {
        anInt1251 = 0;
        int j = (myPlayer.x >> 7) + baseX;
        int k = (myPlayer.y >> 7) + baseY;
        if (j >= 3053 && j <= 3156 && k >= 3056 && k <= 3136)
            anInt1251 = 1;
        if (j >= 3072 && j <= 3118 && k >= 9492 && k <= 9535)
            anInt1251 = 1;
        if (anInt1251 == 1 && j >= 3139 && j <= 3199 && k >= 3008 && k <= 3062)
            anInt1251 = 0;
    }

    public void build3dScreenMenu()
    {
        menuActionName[menuActionRow] = "Walk here";
        menuActionID[menuActionRow] = 516;
        menuActionCmd2[menuActionRow] = super.mouseX;
        menuActionCmd3[menuActionRow] = super.mouseY;
        menuActionRow++;
        int j = -1;
        for (int k = 0; k < Model.resourceCount; k++)
        {
            int l = Model.resourceIDTAG[k];
            int i1 = l & 0x7f;
            int j1 = l >> 7 & 0x7f;
            int k1 = l >> 29 & 3;
            int l1 = l >> 14 & 0x7fff;
            if (l == j)
                continue;
            j = l;
            if (k1 == 2 && sceneGraph.getTileArrayIdForPosition(floor_level, i1, j1, l) >= 0)
            {
                ObjectDefinition objDefinition = ObjectDefinition.forID(l1);
                if (objDefinition.childrenIDs != null)
                    objDefinition = objDefinition.method580();
                if (objDefinition == null)
                    continue;
                if (itemSelected == 1 && objDefinition.name != null && !objDefinition.name.equalsIgnoreCase("null"))
                {
                    for (int i11 = 0; i11 < menuActionRow; i11++)
                        if (menuActionID[i11] == 516)
                        {
                            menuActionName[i11] = "Use @lre@" + selectedItemName + " @whi@-> @cya@" + objDefinition.name;
                            menuActionID[i11] = 62;
                            menuActionCmd1[i11] = l;
                            menuActionCmd2[i11] = i1;
                            menuActionCmd3[i11] = j1;
                            menuActionRow = i11;
                        }
                    menuActionName[menuActionRow] = "Use @lre@" + selectedItemName + " @whi@-> @cya@" + objDefinition.name;
                    menuActionID[menuActionRow] = 62;
                    menuActionCmd1[menuActionRow] = l;
                    menuActionCmd2[menuActionRow] = i1;
                    menuActionCmd3[menuActionRow] = j1;
                    menuActionRow++;
                }
                else if (spellSelected == 1)
                {
                    if ((spellUsableOn & 4) == 4)
                    {
                        for (int i11 = 0; i11 < menuActionRow; i11++)
                            if (menuActionID[i11] == 516)
                            {
                                menuActionName[i11] = spellTooltip + " @cya@" + objDefinition.name;
                                menuActionID[i11] = 956;
                                menuActionCmd1[i11] = l;
                                menuActionCmd2[i11] = i1;
                                menuActionCmd3[i11] = j1;
                                menuActionRow = i11;
                            }
                        menuActionName[menuActionRow] = spellTooltip + " @cya@" + objDefinition.name;
                        menuActionID[menuActionRow] = 956;
                        menuActionCmd1[menuActionRow] = l;
                        menuActionCmd2[menuActionRow] = i1;
                        menuActionCmd3[menuActionRow] = j1;
                        menuActionRow++;
                    }
                }
                else
                {
                    if (objDefinition.actions != null)
                    {
                        for (int i2 = 4; i2 >= 0; i2--)
                            if (objDefinition.actions[i2] != null)
                            {
                                menuActionName[menuActionRow] = objDefinition.actions[i2] + " @cya@" + objDefinition.name;
                                if (i2 == 0)
                                    menuActionID[menuActionRow] = 502;
                                if (i2 == 1)
                                    menuActionID[menuActionRow] = 900;
                                if (i2 == 2)
                                    menuActionID[menuActionRow] = 113;
                                if (i2 == 3)
                                    menuActionID[menuActionRow] = 872;
                                if (i2 == 4)
                                    menuActionID[menuActionRow] = 1062;
                                menuActionCmd1[menuActionRow] = l;
                                menuActionCmd2[menuActionRow] = i1;
                                menuActionCmd3[menuActionRow] = j1;
                                menuActionRow++;
                            }
                    }
                    if (objDefinition.name != null && !objDefinition.name.equalsIgnoreCase("null"))
                    {
                        menuActionName[menuActionRow] = "Examine @cya@" + objDefinition.name;
                        menuActionID[menuActionRow] = 1226;
                        menuActionCmd1[menuActionRow] = objDefinition.type << 14;
                        menuActionCmd2[menuActionRow] = i1;
                        menuActionCmd3[menuActionRow] = j1;
                        menuActionRow++;
                    }
                }
            }
            if (k1 == 1)
            {
                NPC npc = npcArray[l1];
                if (npc.desc.size == 1 && (npc.x & 0x7f) == 64 && (npc.y & 0x7f) == 64)
                {
                    for (int j2 = 0; j2 < npcCount; j2++)
                    {
                        NPC npc2 = npcArray[npcIndices[j2]];
                        if (npc2 != null && npc2 != npc && npc2.desc.size == 1 && npc2.x == npc.x && npc2.y == npc.y)
                            buildAtNPCMenu(npc2.desc, npcIndices[j2], j1, i1);
                    }
                    for (int l2 = 0; l2 < playerCount; l2++)
                    {
                        Player player = playerArray[playerIndices[l2]];
                        if (player != null && player.x == npc.x && player.y == npc.y)
                            buildAtPlayerMenu(i1, playerIndices[l2], player, j1);
                    }
                }
                buildAtNPCMenu(npc.desc, l1, j1, i1);
            }
            if (k1 == 0)
            {
                Player player = playerArray[l1];
                if ((player.x & 0x7f) == 64 && (player.y & 0x7f) == 64)
                {
                    for (int k2 = 0; k2 < npcCount; k2++)
                    {
                        NPC class30_sub2_sub4_sub1_sub1_2 = npcArray[npcIndices[k2]];
                        if (class30_sub2_sub4_sub1_sub1_2 != null && class30_sub2_sub4_sub1_sub1_2.desc.size == 1 && class30_sub2_sub4_sub1_sub1_2.x == player.x && class30_sub2_sub4_sub1_sub1_2.y == player.y)
                            buildAtNPCMenu(class30_sub2_sub4_sub1_sub1_2.desc, npcIndices[k2], j1, i1);
                    }
                    for (int i3 = 0; i3 < playerCount; i3++)
                    {
                        Player class30_sub2_sub4_sub1_sub2_2 = playerArray[playerIndices[i3]];
                        if (class30_sub2_sub4_sub1_sub2_2 != null && class30_sub2_sub4_sub1_sub2_2 != player && class30_sub2_sub4_sub1_sub2_2.x == player.x && class30_sub2_sub4_sub1_sub2_2.y == player.y)
                            buildAtPlayerMenu(i1, playerIndices[i3], class30_sub2_sub4_sub1_sub2_2, j1);
                    }
                }
                buildAtPlayerMenu(i1, l1, player, j1);
            }
            if (k1 == 3)
            {
                Deque class19 = groundArray[floor_level][i1][j1];
                if (class19 != null)
                {
                    for (Item item = (Item) class19.getFirst(); item != null; item = (Item) class19.getNext())
                    {
                        ItemDefinition itemDef = ItemDefinition.forID(item.ID);
                        if (itemSelected == 1)
                        {
                            for (int i11 = 0; i11 < menuActionRow; i11++)
                                if (menuActionID[i11] == 516)
                                {
                                    menuActionName[i11] = "Use @lre@" + selectedItemName + " @whi@-> @lre@" + itemDef.name;
                                    menuActionID[i11] = 511;
                                    menuActionCmd1[i11] = item.ID;
                                    menuActionCmd2[i11] = i1;
                                    menuActionRow = i11;
                                }
                            menuActionName[menuActionRow] = "Use @lre@" + selectedItemName + " @whi@-> @lre@" + itemDef.name;
                            menuActionID[menuActionRow] = 511;
                            menuActionCmd1[menuActionRow] = item.ID;
                            menuActionCmd2[menuActionRow] = i1;
                            menuActionRow++;
                        }
                        else if (spellSelected == 1)
                        {
                            if ((spellUsableOn & 1) == 1)
                            {
                                for (int i11 = 0; i11 < menuActionRow; i11++)
                                    if (menuActionID[i11] == 516)
                                    {
                                        menuActionName[i11] = spellTooltip + " @lre@" + itemDef.name;
                                        menuActionID[i11] = 94;
                                        menuActionCmd1[i11] = item.ID;
                                        menuActionCmd2[i11] = i1;
                                        menuActionCmd3[i11] = j1;
                                        menuActionRow = i11;
                                    }
                                menuActionName[menuActionRow] = spellTooltip + " @lre@" + itemDef.name;
                                menuActionID[menuActionRow] = 94;
                                menuActionCmd1[menuActionRow] = item.ID;
                                menuActionCmd2[menuActionRow] = i1;
                                menuActionCmd3[menuActionRow] = j1;
                                menuActionRow++;
                            }
                        }
                        else
                        {
                            for (int j3 = 4; j3 >= 0; j3--)
                                if (itemDef.groundActions != null && itemDef.groundActions[j3] != null)
                                {
                                    menuActionName[menuActionRow] = itemDef.groundActions[j3] + " @lre@" + itemDef.name;
                                    if (j3 == 0)
                                        menuActionID[menuActionRow] = 652;
                                    if (j3 == 1)
                                        menuActionID[menuActionRow] = 567;
                                    if (j3 == 2)
                                        menuActionID[menuActionRow] = 234;
                                    if (j3 == 3)
                                        menuActionID[menuActionRow] = 244;
                                    if (j3 == 4)
                                        menuActionID[menuActionRow] = 213;
                                    menuActionCmd1[menuActionRow] = item.ID;
                                    menuActionCmd2[menuActionRow] = i1;
                                    menuActionCmd3[menuActionRow] = j1;
                                    menuActionRow++;
                                }
                                else if (j3 == 2)
                                {
                                    menuActionName[menuActionRow] = "Take @lre@" + itemDef.name;
                                    menuActionID[menuActionRow] = 234;
                                    menuActionCmd1[menuActionRow] = item.ID;
                                    menuActionCmd2[menuActionRow] = i1;
                                    menuActionCmd3[menuActionRow] = j1;
                                    menuActionRow++;
                                }
                            menuActionName[menuActionRow] = "Examine @lre@" + itemDef.name;
                            menuActionID[menuActionRow] = 1448;
                            menuActionCmd1[menuActionRow] = item.ID;
                            menuActionCmd2[menuActionRow] = i1;
                            menuActionCmd3[menuActionRow] = j1;
                            menuActionRow++;
                        }
                    }
                }
            }
        }
    }

    public Component getGameComponent()
    {
        if (super.gameFrame != null)
            return super.gameFrame;
        else
            return this;
    }

    private void handleBrightness(int amount)
    {
        if (amount >= 100)
        {
            brightnessAmount = 100;
        }
        else if (amount <= 0)
        {
            brightnessAmount = 0;
        }
        else
        {
            brightnessAmount = amount;
        }
        Rasterizer.method372(1.49D + ((brightnessAmount / -1) * 0.01));
        ItemDefinition.mruNodes1.unlinkAll();
        ItemDefinition.mruNodes2.unlinkAll();
        ItemDefinition.mruNodes3.unlinkAll();
        RSInterface.modelCache.unlinkAll();
        welcomeScreenRaised = true;
    }

    public void handleInputOutput()
    {
        do
        {
            int pressedKey = readChar(-796);
            if (pressedKey == -1)
            {
                break;
            }
            if (openInterfaceID != -1 && openInterfaceID == reportAbuseInterfaceID)
            {
                if (pressedKey == 8 && reportAbuseInput.length() > 0)
                {
                    reportAbuseInput = reportAbuseInput.substring(0, reportAbuseInput.length() - 1);
                }
                if ((pressedKey >= 97 && pressedKey <= 122 || pressedKey >= 65 && pressedKey <= 90 || pressedKey >= 48 && pressedKey <= 57 || pressedKey == 32) && reportAbuseInput.length() < 12)
                {
                    reportAbuseInput += (char) pressedKey;
                }
            }
            else if (messagePromptRaised && !chatHidden)
            {
                if (pressedKey >= 32 && pressedKey <= 122 && promptInput.length() < 80)
                {
                    promptInput += (char) pressedKey;
                    inputTaken = true;
                }
                if (pressedKey == 8 && !chatHidden && promptInput.length() > 0)
                {
                    promptInput = promptInput.substring(0, promptInput.length() - 1);
                    inputTaken = true;
                }
                if (pressedKey == 13 || pressedKey == 10 && !chatHidden)
                {
                    messagePromptRaised = false;
                    inputTaken = true;
                    if (friendsListAction == 1)
                    {
                        long l = TextClass.longForName(promptInput);
                        addFriend(l);
                    }
                    if (friendsListAction == 2 && friendsCount > 0)
                    {
                        long l1 = TextClass.longForName(promptInput);
                        delFriend(l1);
                    }
                    if (friendsListAction == 3 && promptInput.length() > 0)
                    {
                        stream.writeOpcode(126);
                        stream.writeByte(0);
                        int k = stream.pointer;
                        stream.writeLong(aLong953);
                        TextInput.method526(promptInput, stream);
                        stream.writeBytes(stream.pointer - k);
                        promptInput = TextInput.processText(promptInput);
                        pushMessage(promptInput, 6, TextClass.fixName(TextClass.nameForLong(aLong953)));
                        if (chatTabMode[3] == 2)
                        {
                            chatTabMode[3] = 1;
                            stream.writeOpcode(95);
                            stream.writeByte(chatTabMode[2]);
                            stream.writeByte(chatTabMode[3]);
                            stream.writeByte(chatTabMode[5]);
                        }
                    }
                    if (friendsListAction == 4 && ignoreCount < 100)
                    {
                        long l2 = TextClass.longForName(promptInput);
                        addIgnore(l2);
                    }
                    if (friendsListAction == 5 && ignoreCount > 0)
                    {
                        long l3 = TextClass.longForName(promptInput);
                        delIgnore(l3);
                    }
                }
            }
            else if (inputDialogState == 3 && !chatHidden)
            {
                if (customMenuAddAction == 2)
                {
                    if ((pressedKey >= 97 && pressedKey <= 122 || pressedKey == 95 || pressedKey == 246 || pressedKey == 228 || pressedKey == 45 || pressedKey == 39 || pressedKey == 46 || pressedKey == 214 || pressedKey == 196 || pressedKey >= 65 && pressedKey <= 90 || pressedKey >= 48 && pressedKey <= 57 || pressedKey == 32) && amountOrNameInput.length() < 12)
                    {
                        amountOrNameInput += (char) pressedKey;
                        inputTaken = true;
                    }
                    if (pressedKey == 13 || pressedKey == 10)
                    {
                        useDate = true;
                        if (amountOrNameInput.length() > 0)
                        {
                            pictureFileName = amountOrNameInput;
                            useDate = false;
                            pictureCount = 0;
                            try
                            {
                                writeSettings();
                            }
                            catch (IOException e)
                            {
                                /* Empty */
                            }
                            updateScreenShotStrings();
                        }
                        inputDialogState = 0;
                        inputTaken = true;
                        try
                        {
                            writeSettings();
                        }
                        catch (IOException e)
                        {
                            /* Empty */
                        }
                        updateScreenShotStrings();
                    }
                }
                else if (pressedKey >= 48 && pressedKey <= 57 && amountOrNameInput.length() < 3)
                {
                    amountOrNameInput += (char) pressedKey;
                    inputTaken = true;
                }
                if (pressedKey == 8 && amountOrNameInput.length() > 0)
                {
                    amountOrNameInput = amountOrNameInput.substring(0, amountOrNameInput.length() - 1);
                    inputTaken = true;
                }
                if (pressedKey == 13 || pressedKey == 10)
                {
                    if (amountOrNameInput.length() > 0)
                    {
                        if (customMenuAddAction == 3)
                        {
                            int amount = 0;
                            amount = Integer.parseInt(amountOrNameInput);
                            handleBrightness(amount);
                        }
                    }
                    inputDialogState = 0;
                    inputTaken = true;
                }
            }
            else if (inputDialogState == 1 && !chatHidden)
            {
                if (pressedKey >= 48 && pressedKey <= 57 && amountOrNameInput.length() < 10)
                {
                    amountOrNameInput += (char) pressedKey;
                    inputTaken = true;
                }
                if ((!amountOrNameInput.toLowerCase().contains("k") && !amountOrNameInput.toLowerCase().contains("m") && !amountOrNameInput.toLowerCase().contains("b")) && (pressedKey == 107 || pressedKey == 109 || pressedKey == 75 || pressedKey == 77 || pressedKey == 66 || pressedKey == 98))
                {
                    amountOrNameInput += (char) pressedKey;
                    inputTaken = true;
                }
                if (pressedKey == 8 && amountOrNameInput.length() > 0)
                {
                    amountOrNameInput = amountOrNameInput.substring(0, amountOrNameInput.length() - 1);
                    inputTaken = true;
                }
                if (pressedKey == 13 || pressedKey == 10)
                {
                    if (amountOrNameInput.length() > 0)
                    {
                        if (amountOrNameInput.toLowerCase().contains("k"))
                        {
                            amountOrNameInput = amountOrNameInput.toLowerCase().replace("k", "000");
                        }
                        else if (amountOrNameInput.toLowerCase().contains("m"))
                        {
                            amountOrNameInput = amountOrNameInput.toLowerCase().replaceAll("m", "000000");
                        }
                        else if (amountOrNameInput.toLowerCase().contains("b"))
                        {
                            amountOrNameInput = amountOrNameInput.toLowerCase().replaceAll("b", "000000000");
                        }
                        int amount = 0;
                        try
                        {
                            amount = Integer.parseInt(amountOrNameInput);
                        }
                        catch (Exception e)
                        {
                            amount = Integer.MAX_VALUE;
                        }
                        stream.writeOpcode(208);
                        stream.writeInt(amount);
                    }
                    inputDialogState = 0;
                    inputTaken = true;
                }
            }
            else if (inputDialogState == 2 && !chatHidden)
            {
                if (pressedKey >= 32 && pressedKey <= 122 && amountOrNameInput.length() < 12)
                {
                    amountOrNameInput += (char) pressedKey;
                    inputTaken = true;
                }
                if (pressedKey == 8 && amountOrNameInput.length() > 0)
                {
                    amountOrNameInput = amountOrNameInput.substring(0, amountOrNameInput.length() - 1);
                    inputTaken = true;
                }
                if (pressedKey == 13 || pressedKey == 10)
                {
                    if (amountOrNameInput.length() > 0)
                    {
                        stream.writeOpcode(60);
                        stream.writeLong(TextClass.longForName(amountOrNameInput));
                    }
                    inputDialogState = 0;
                    inputTaken = true;
                }
            }
            else if (backDialogID == -1 && !chatHidden)
            {
                if (pressedKey >= 32 && pressedKey <= 122 || pressedKey == 246 || pressedKey == 214 || pressedKey == 196 || pressedKey == 197 || pressedKey == 167 || pressedKey == 229 || pressedKey == 189 || pressedKey == 164 || pressedKey == 180 || pressedKey == 168 || pressedKey == 126 || pressedKey == 125 || pressedKey == 123 || pressedKey == 163 || pressedKey == 228 && inputString.length() < 80)
                {
                    inputString += (char) pressedKey;
                    inputTaken = true;
                }
                if (pressedKey == 8 && inputString.length() > 0)
                {
                    inputString = inputString.substring(0, inputString.length() - 1);
                    inputTaken = true;
                }
                if ((pressedKey == 13 || pressedKey == 10) && inputString.length() > 0)
                {
                    if (inputString.equals(";;data"))
                    {
                        fpsOn = !fpsOn;
                        inputString = "";
                        inputTaken = true;
                        return;
                    }
                    if (myPrivilege >= 3)
                    { // TODO: DEFAULT IS == 2
                        /*
                         * if (inputString.startsWith("o")) { loadingStage = 1;
                         * } if (inputString.startsWith("p")) {
                         * anIntArray1045[941] = 1; variousSettings[941] = 1;
                         * method33(941); anIntArray1045[942] = 1;
                         * variousSettings[942] = 1; method33(942);
                         * anIntArray1045[943] = 1; variousSettings[943] = 1;
                         * method33(943); anIntArray1045[944] = 1;
                         * variousSettings[944] = 1; method33(944); }
                         */
                        if (inputString.startsWith(";;noob"))
                        {
                            try
                            {
                                String[] args = inputString.split(" ");
                                // int id1 = Integer.parseInt(args[1]);
                                // System.err.println(id1 + " " +
                                // RSInterface.interfaceCache[id1].valueIndexArray[0][1]);
                                /*
                                 * if (id1 == 1) { aBoolean1160 = true;
                                 * anInt1098 = 45; anInt1099 = 47; anInt1100 =
                                 * 250; anInt1101 = 5; anInt1102 = 5; if
                                 * (anInt1102 >= 100) { xCameraPos = anInt1098 *
                                 * 128 + 64; yCameraPos = anInt1099 * 128 + 64;
                                 * zCameraPos = method42(floor_level,
                                 * yCameraPos, xCameraPos) - anInt1100; } }
                                 * 
                                 * if (id1 == 2) { aBoolean1160 = true; anInt995
                                 * = 3232 / 64; anInt996 = 3434 / 64; anInt997 =
                                 * 250; anInt998 = 5; anInt999 = 5; if (anInt999
                                 * >= 100) { int k7 = anInt995 * 128 + 64; int
                                 * k14 = anInt996 * 128 + 64; int i20 =
                                 * method42(floor_level, k14, k7) - anInt997;
                                 * int l22 = k7 - xCameraPos; int k25 = i20 -
                                 * zCameraPos; int j28 = k14 - yCameraPos; int
                                 * i30 = (int) Math.sqrt(l22 * l22 + j28 * j28);
                                 * yCameraCurve = (int) (Math.atan2(k25, i30) *
                                 * 325.94900000000001D) & 0x7ff; xCameraCurve =
                                 * (int) (Math.atan2(l22, j28) *
                                 * -325.94900000000001D) & 0x7ff; if
                                 * (yCameraCurve < 128) yCameraCurve = 128; if
                                 * (yCameraCurve > (clientSize == CLIENT_FIXED ? 383 :
                                 * 283)) yCameraCurve = (clientSize == CLIENT_FIXED ? 383 :
                                 * 283); } }
                                 * 
                                 * // reset if (id1 == 0) { aBoolean1160 =
                                 * false; for (int l = 0; l < 5; l++)
                                 * aBooleanArray876[l] = false; }
                                 */
                                // = false;
                                // String[] args = inputString.split(" ");
                                // int id1 = Integer.parseInt(args[1]);
                                // sendMusic(id1);
                                // toggleSize(clientSize == CLIENT_FIXED ? 1 : 0);
                                /*
                                 * String[] args = inputString.split(" "); int
                                 * id1 = Integer.parseInt(args[1]); if (id1 >=
                                 * 18000) id1 = -1; RSInterface class9_4 =
                                 * RSInterface.interfaceCache[3307];
                                 * class9_4.enabledAnimation = 632;
                                 * //moveComponent(- (clientWidth / 2) + 256, -
                                 * (clientHeight / 2) + 167, id1);
                                 * openInterfaceID = id1;
                                 * pushMessage("<trans=150><img=2></trans>hey</img>"
                                 * , 0, "<img=2>hey</img>");
                                 */
                                inputString = "";
                                inputTaken = true;
                                return;
                            }
                            catch (Exception e)
                            {
                                e.printStackTrace();
                            }
                        }
                        if (inputString.startsWith(";;load"))
                        {
                            try
                            {
                                smallText = new TextDrawingArea(false, "p11_full", titleStreamLoader);
                                regularText = new TextDrawingArea(false, "p12_full", titleStreamLoader);
                                chatText = new TextDrawingArea(false, "b12_full", titleStreamLoader);
                                TextDrawingArea fancyText = new TextDrawingArea(true, "q8_full", titleStreamLoader);
                                TextDrawingArea textDrawingArea[] =
                                { smallText, regularText, chatText, fancyText };
                                CacheArchive streamLoader_1 = streamLoaderForName(3, "interface", "interface", expectedCRCs[3], 35);
                                CacheArchive streamLoader_2 = streamLoaderForName(4, "2d graphics", "media", expectedCRCs[4], 40);
                                RSInterface.unpack(streamLoader_1, textDrawingArea, streamLoader_2);
                                inputString = "";
                                inputTaken = true;
                                return;
                            }
                            catch (Exception e)
                            {
                            }
                        }
                        if (inputString.startsWith(";;s"))
                        {
                            try
                            {
                                String[] args = inputString.split(" ");
                                int id = Integer.parseInt(args[1]);
                                RSInterface.posy -= id;
                                CacheArchive streamLoader_1 = streamLoaderForName(3, "interface", "interface", expectedCRCs[3], 35);
                                CacheArchive streamLoader_2 = streamLoaderForName(4, "2d graphics", "media", expectedCRCs[4], 40);
                                TextDrawingArea fancyText = new TextDrawingArea(true, "q8_full", titleStreamLoader);
                                TextDrawingArea textDrawingArea[] =
                                { smallText, regularText, chatText, fancyText };
                                RSInterface.unpack(streamLoader_1, textDrawingArea, streamLoader_2);
                                inputString = "";
                                inputTaken = true;
                                return;
                            }
                            catch (Exception e)
                            {
                            }
                        }
                        if (inputString.startsWith(";;a"))
                        {
                            try
                            {
                                String[] args = inputString.split(" ");
                                int id = Integer.parseInt(args[1]);
                                RSInterface.posx -= id;
                                CacheArchive streamLoader_1 = streamLoaderForName(3, "interface", "interface", expectedCRCs[3], 35);
                                CacheArchive streamLoader_2 = streamLoaderForName(4, "2d graphics", "media", expectedCRCs[4], 40);
                                TextDrawingArea fancyText = new TextDrawingArea(true, "q8_full", titleStreamLoader);
                                TextDrawingArea textDrawingArea[] =
                                { smallText, regularText, chatText, fancyText };
                                RSInterface.unpack(streamLoader_1, textDrawingArea, streamLoader_2);
                                inputString = "";
                                inputTaken = true;
                                return;
                            }
                            catch (Exception e)
                            {
                            }
                        }
                        if (inputString.equals(";;gc"))
                        {
                            System.gc();
                            System.runFinalization();
                            inputString = "";
                            inputTaken = true;
                            return;
                        }
                        if (inputString.equals(";;1"))
                        {
                            try
                            {
                                inputString = "";
                                inputTaken = true;
                                return;
                                // openInterfaceID = 6412;
                                // int regId = 6985;
                                // System.err.println("X: " + ((regId >> 8) *
                                // 64) + " Y: " + ((regId & 0xff) * 64));
                                // ObjectDef.writeObjects();
                                // updateScreenShotStrings();
                                // sendChatInterface(1244);
                                // SoundProvider.getSingleton().fadeMidi(true);
                                // anIntArray1045[166] = 5;
                                // variousSettings[166] = 5;
                                // method33(166);
                                // for (int k = 0; k <
                                // RSInterface.interfaceCache[12468].children.length;
                                // k++)
                                // if
                                // (RSInterface.interfaceCache[12468].children[k]
                                // == 12557)
                                // System.err.println(k+" "+RSInterface.interfaceCache[12468].childX[k]+" "+RSInterface.interfaceCache[12468].childY[k]);
                                // System.err.println(RSInterface.interfaceCache[13293].parentID);
                                // System.err.println(RSInterface.interfaceCache[1829].children[k]);
                                // RSInterface.interfaceCache[1567].scrollMax =
                                // 222;
                                // System.err.println(RSInterface.interfaceCache[RSInterface.interfaceCache[1829].children[k]].parentID);
                            }
                            catch (Exception e)
                            {
                                e.printStackTrace();
                            }
                        }
                        if (inputString.equals(";;2"))
                        {
                            try
                            {
                                // method22();
                                inputString = "";
                                inputTaken = true;
                                return;
                            }
                            catch (Exception e)
                            {
                            }
                        }
                        if (inputString.startsWith(";;d"))
                        {
                            try
                            {
                                String[] args = inputString.split(" ");
                                int id = Integer.parseInt(args[1]);
                                RSInterface.posx += id;
                                CacheArchive streamLoader_1 = streamLoaderForName(3, "interface", "interface", expectedCRCs[3], 35);
                                CacheArchive streamLoader_2 = streamLoaderForName(4, "2d graphics", "media", expectedCRCs[4], 40);
                                TextDrawingArea fancyText = new TextDrawingArea(true, "q8_full", titleStreamLoader);
                                TextDrawingArea textDrawingArea[] =
                                { smallText, regularText, chatText, fancyText };
                                RSInterface.unpack(streamLoader_1, textDrawingArea, streamLoader_2);
                                inputString = "";
                                inputTaken = true;
                                return;
                            }
                            catch (Exception e)
                            {
                            }
                        }
                        if (inputString.startsWith(";;w"))
                        {
                            try
                            {
                                // System.err.println("X: "+((9018 >> 8) *
                                // 64)+" Y: "+((9018 & 0xff) * 64));

                                /*
                                 * BufferedReader bufferedReader = null; try {
                                 * bufferedReader = new BufferedReader(new
                                 * FileReader(signlink.cacheLocation() +
                                 * "./map_index.txt")); } catch
                                 * (FileNotFoundException e1) {
                                 * e1.printStackTrace(); } String line = null;
                                 * int[] reg = new int[1500]; int[] flo = new
                                 * int[1500]; int[] obj = new int[1500]; byte[]
                                 * mem = new byte[1500]; int index = 0; try {
                                 * while ((line = bufferedReader.readLine()) !=
                                 * null) { reg[index] =
                                 * Integer.parseInt(line.substring
                                 * (line.indexOf("[REG]") + 5,
                                 * line.indexOf("[FLO]"))); flo[index] =
                                 * Integer.
                                 * parseInt(line.substring(line.indexOf("[FLO]")
                                 * + 5, line.indexOf("[OBJ]"))); obj[index] =
                                 * Integer
                                 * .parseInt(line.substring(line.indexOf("[OBJ]"
                                 * ) + 5, line.indexOf("[MEM]"))); mem[index] =
                                 * (byte)
                                 * Integer.parseInt(line.substring(line.indexOf
                                 * ("[MEM]") + 5)); index++; } DataOutputStream
                                 * out = new DataOutputStream(new
                                 * BufferedOutputStream(new FileOutputStream(
                                 * signlink.cacheLocation() +
                                 * "map_index.dat"))); for (int kk = 0; kk <
                                 * index; kk++) { out.writeShort(reg[kk]);
                                 * out.writeShort(flo[kk]);
                                 * out.writeShort(obj[kk]);
                                 * out.writeByte(mem[kk]); } out.close(); }
                                 * catch (Exception e) { e.printStackTrace(); }
                                 */

                                String[] args = inputString.split(" ");
                                int id = Integer.parseInt(args[1]);
                                RSInterface.posy += id;
                                CacheArchive streamLoader_1 = streamLoaderForName(3, "interface", "interface", expectedCRCs[3], 35);
                                CacheArchive streamLoader_2 = streamLoaderForName(4, "2d graphics", "media", expectedCRCs[4], 40);
                                TextDrawingArea fancyText = new TextDrawingArea(true, "q8_full", titleStreamLoader);
                                TextDrawingArea textDrawingArea[] =
                                { smallText, regularText, chatText, fancyText };
                                RSInterface.unpack(streamLoader_1, textDrawingArea, streamLoader_2);
                                inputString = "";
                                inputTaken = true;
                                return;
                            }
                            catch (Exception e)
                            {
                            }
                        }
                        if (inputString.startsWith(";;reset"))
                        {
                            RSInterface.posx = 0;
                            RSInterface.posy = 0;
                            inputString = "";
                            inputTaken = true;
                            return;
                        }
                        if (inputString.equals("::noclip"))
                        {
                            for (int k1 = 0; k1 < 4; k1++)
                            {
                                for (int i2 = 1; i2 < 103; i2++)
                                {
                                    for (int k2 = 1; k2 < 103; k2++)
                                        collision_maps[k1].clips[i2][k2] = 0;
                                }
                            }
                        }
                    }
                    if (inputString.startsWith("::"))
                    {
                        stream.writeOpcode(103);
                        stream.writeByte(inputString.length() - 1);
                        stream.writeString(inputString.substring(2));
                    }
                    else
                    {
                        String s = inputString.toLowerCase();
                        int j2 = 0;
                        if (s.startsWith("yellow:"))
                        {
                            j2 = 0;
                            inputString = inputString.substring(7);
                        }
                        else if (s.startsWith("red:"))
                        {
                            j2 = 1;
                            inputString = inputString.substring(4);
                        }
                        else if (s.startsWith("green:"))
                        {
                            j2 = 2;
                            inputString = inputString.substring(6);
                        }
                        else if (s.startsWith("cyan:"))
                        {
                            j2 = 3;
                            inputString = inputString.substring(5);
                        }
                        else if (s.startsWith("purple:"))
                        {
                            j2 = 4;
                            inputString = inputString.substring(7);
                        }
                        else if (s.startsWith("white:"))
                        {
                            j2 = 5;
                            inputString = inputString.substring(6);
                        }
                        else if (s.startsWith("flash1:"))
                        {
                            j2 = 6;
                            inputString = inputString.substring(7);
                        }
                        else if (s.startsWith("flash2:"))
                        {
                            j2 = 7;
                            inputString = inputString.substring(7);
                        }
                        else if (s.startsWith("flash3:"))
                        {
                            j2 = 8;
                            inputString = inputString.substring(7);
                        }
                        else if (s.startsWith("glow1:"))
                        {
                            j2 = 9;
                            inputString = inputString.substring(6);
                        }
                        else if (s.startsWith("glow2:"))
                        {
                            j2 = 10;
                            inputString = inputString.substring(6);
                        }
                        else if (s.startsWith("glow3:"))
                        {
                            j2 = 11;
                            inputString = inputString.substring(6);
                        }
                        s = inputString.toLowerCase();
                        int i3 = 0;
                        if (s.startsWith("wave:"))
                        {
                            i3 = 1;
                            inputString = inputString.substring(5);
                        }
                        else if (s.startsWith("wave2:"))
                        {
                            i3 = 2;
                            inputString = inputString.substring(6);
                        }
                        else if (s.startsWith("shake:"))
                        {
                            i3 = 3;
                            inputString = inputString.substring(6);
                        }
                        else if (s.startsWith("scroll:"))
                        {
                            i3 = 4;
                            inputString = inputString.substring(7);
                        }
                        else if (s.startsWith("slide:"))
                        {
                            i3 = 5;
                            inputString = inputString.substring(6);
                        }
                        stream.writeOpcode(4);
                        stream.writeByte(0);
                        int j3 = stream.pointer;
                        stream.method425(i3);
                        stream.method425(j2);
                        aStream_834.pointer = 0;
                        TextInput.method526(inputString, aStream_834);
                        stream.method441(0, aStream_834.buffer, aStream_834.pointer);
                        stream.writeBytes(stream.pointer - j3);
                        inputString = TextInput.processText(inputString);
                        myPlayer.textSpoken = inputString;
                        myPlayer.anInt1513 = j2;
                        myPlayer.anInt1531 = i3;
                        myPlayer.textCycle = 150;
                        if (myPrivilege == 2)
                            pushMessage(myPlayer.textSpoken, 2, "@cr3@" + myPlayer.name);
                        else if (myPrivilege == 3)
                            pushMessage(myPlayer.textSpoken, 2, "@cr2@" + myPlayer.name);
                        else if (myPrivilege == 1)
                            pushMessage(myPlayer.textSpoken, 2, "@cr1@" + myPlayer.name);
                        else
                            pushMessage(myPlayer.textSpoken, 2, myPlayer.name);
                        if (chatTabMode[2] == 2)
                        {
                            chatTabMode[2] = 3;
                            stream.writeOpcode(95);
                            stream.writeByte(chatTabMode[2]);
                            stream.writeByte(chatTabMode[3]);
                            stream.writeByte(chatTabMode[5]);
                        }
                    }
                    inputString = "";
                    inputTaken = true;
                }
            }
        }
        while (true);
    }

    // TODO: HERE WE ARE SO FAR @ 17.12.2011 @ 18.36 GMT+2
    public void buildChatAreaMenu(int j)
    {
        int l = 0;
        for (int i1 = 0; i1 < 100; i1++)
        {
            if (chatMessages[i1] == null)
                continue;
            int j1 = chatTypes[i1];
            int k1 = (114 - l * 14) + chatScrollPos + 3;
            if (k1 < -20)
                break;
            String s = chatNames[i1];
            if (s != null && s.startsWith("@cr"))
                s = s.substring(5);
            if (j1 == 0 && (chatStoneHoverState[0] == 0))
                l++;
            if ((j1 == 1 || j1 == 2) && (chatStoneHoverState[0] == 0 || chatStoneHoverState[2] == 0) && (j1 == 1 || chatTabMode[2] == 0 || chatTabMode[2] == 1 && isFriendOrSelf(s)))
            {
                if (j > k1 - 14 && j <= k1 && !s.equals(myPlayer.name))
                {
                    if (myPrivilege >= 1)
                    {
                        menuActionName[menuActionRow] = "Report abuse @whi@" + capitalize(s);
                        menuActionID[menuActionRow] = 606;
                        menuActionRow++;
                    }
                    menuActionName[menuActionRow] = "Add ignore @whi@" + capitalize(s);
                    menuActionID[menuActionRow] = 42;
                    menuActionRow++;
                    menuActionName[menuActionRow] = "Add friend @whi@" + capitalize(s);
                    menuActionID[menuActionRow] = 337;
                    menuActionRow++;
                }
                l++;
            }
            if ((j1 == 3 || j1 == 7) && (chatStoneHoverState[0] == 0 || chatStoneHoverState[3] == 0) && splitPrivateChat == 0 && (j1 == 7 || chatTabMode[3] == 0 || chatTabMode[3] == 1 && isFriendOrSelf(s)))
            {
                if (j > k1 - 14 && j <= k1)
                {
                    if (myPrivilege >= 1)
                    {
                        menuActionName[menuActionRow] = "Report abuse @whi@" + capitalize(s);
                        menuActionID[menuActionRow] = 606;
                        menuActionRow++;
                    }
                    menuActionName[menuActionRow] = "Add ignore @whi@" + capitalize(s);
                    menuActionID[menuActionRow] = 42;
                    menuActionRow++;
                    menuActionName[menuActionRow] = "Add friend @whi@" + capitalize(s);
                    menuActionID[menuActionRow] = 337;
                    menuActionRow++;
                    menuActionName[menuActionRow] = "Reply to @whi@" + capitalize(s);
                    menuActionID[menuActionRow] = 639;
                    menuActionRow++;
                }
                l++;
            }
            if (j1 == 4 && (chatStoneHoverState[0] == 0 || chatStoneHoverState[5] == 0) && (chatTabMode[5] == 0 || chatTabMode[5] == 1 && isFriendOrSelf(s)))
            {
                if (j > k1 - 14 && j <= k1)
                {
                    menuActionName[menuActionRow] = "Accept trade @whi@" + capitalize(s);
                    menuActionID[menuActionRow] = 484;
                    menuActionRow++;
                }
                l++;
            }
            if ((j1 == 5 || j1 == 6) && (chatStoneHoverState[0] == 0 || chatStoneHoverState[3] == 0) && splitPrivateChat == 0 && chatTabMode[3] < 2)
                l++;
            if (j1 == 8 && (chatStoneHoverState[0] == 0 || chatStoneHoverState[5] == 0) && (chatTabMode[5] == 0 || chatTabMode[5] == 1 && isFriendOrSelf(s)))
            {
                if (j > k1 - 14 && j <= k1)
                {
                    menuActionName[menuActionRow] = "Accept challenge @whi@" + capitalize(s);
                    menuActionID[menuActionRow] = 6;
                    menuActionRow++;
                }
                l++;
            }
        }
    }

    int[] levelIDs =
    { 0, 3, 14, 2, 16, 13, 1, 15, 10, 4, 17, 7, 5, 12, 11, 6, 9, 8, 20, 18, 19, 21 };

    public int getXPForLevel(int level)
    {
        int points = 0;
        int output = 0;
        for (int lvl = 1; lvl <= level; lvl++)
        {
            points += Math.floor(lvl + 300.0 * Math.pow(2.0, lvl / 7.0));
            if (lvl >= level)
            {
                return output;
            }
            output = (int) Math.floor(points / 4);
        }
        return 0;
    }

    public String setMessage(int level)
    {
        String[] messages = new String[3];
        String message = "";
        Locale locale = Locale.US;
        String s = NumberFormat.getNumberInstance(locale).format(currentExp[levelIDs[level]]);
        String s1 = NumberFormat.getNumberInstance(locale).format((getXPForLevel(maxStats[levelIDs[level]] + 1)));
        int maxLevel = 99;
        if (maxStats[level] > maxLevel)
        {
            maxStats[level] = 99;
        }
        messages[0] = Skills.skillNames[level] + ": " + currentStats[levelIDs[level]] + "/" + maxStats[levelIDs[level]] + "\\n";
        messages[1] = "Current XP: " + s + "\\n";
        messages[2] = "Next level at: " + s1 + "\\n";
        if (maxStats[levelIDs[level]] >= 99)
        {
            message = messages[0] + messages[1];
        }
        else
        {
            message = messages[0] + messages[1] + messages[2];
        }
        return message;
    }

    public void drawFriendsListOrWelcomeScreen(RSInterface class9)
    {
        int j = class9.contentType;
        if (j >= 1 && j <= 100 || j >= 701 && j <= 800)
        {
            if (j == 1 && anInt900 == 0)
            {
                class9.disabledMessage = "Loading friend list";
                class9.atActionType = 0;
                return;
            }
            if (j == 1 && anInt900 == 1)
            {
                class9.disabledMessage = "Connecting to friendserver";
                class9.atActionType = 0;
                return;
            }
            if (j == 2 && anInt900 != 2)
            {
                class9.disabledMessage = "Please wait...";
                class9.atActionType = 0;
                return;
            }
            int k = friendsCount;
            if (anInt900 != 2)
                k = 0;
            if (j > 700)
                j -= 601;
            else
                j--;
            if (j >= k)
            {
                class9.disabledMessage = "";
                class9.atActionType = 0;
                return;
            }
            else
            {
                class9.disabledMessage = friendsList[j];
                class9.atActionType = 1;
                return;
            }
        }
        if (j >= 101 && j <= 200 || j >= 801 && j <= 900)
        {
            int l = friendsCount;
            if (anInt900 != 2)
                l = 0;
            if (j > 800)
                j -= 701;
            else
                j -= 101;
            if (j >= l)
            {
                class9.disabledMessage = "";
                class9.atActionType = 0;
                return;
            }
            if (friendsNodeIDs[j] == 0)
                class9.disabledMessage = "@red@Offline";
            else if (friendsNodeIDs[j] == nodeID)
                class9.disabledMessage = "@gre@Online";
            else
                class9.disabledMessage = "@red@Offline";
            class9.atActionType = 1;
            return;
        }
        if (j == 203)
        {
            int i1 = friendsCount;
            if (anInt900 != 2)
                i1 = 0;
            class9.scrollMax = (short) (i1 * 15 + 20);
            if (class9.scrollMax <= class9.height)
                class9.scrollMax = (short) (class9.height + 1);
            return;
        }
        if (j >= 401 && j <= 500)
        {
            if ((j -= 401) == 0 && anInt900 == 0)
            {
                class9.disabledMessage = "Loading ignore list";
                class9.atActionType = 0;
                return;
            }
            if (j == 1 && anInt900 == 0)
            {
                class9.disabledMessage = "Please wait...";
                class9.atActionType = 0;
                return;
            }
            int j1 = ignoreCount;
            if (anInt900 == 0)
                j1 = 0;
            if (j >= j1)
            {
                class9.disabledMessage = "";
                class9.atActionType = 0;
                return;
            }
            else
            {
                class9.disabledMessage = TextClass.fixName(TextClass.nameForLong(ignoreListAsLongs[j]));
                class9.atActionType = 1;
                return;
            }
        }
        if (j == 503)
        {
            class9.scrollMax = (short) (ignoreCount * 15 + 20);
            if (class9.scrollMax <= class9.height)
                class9.scrollMax = (short) (class9.height + 1);
            return;
        }
        if (j == 327)
        {
            class9.modelRotY = 150;
            class9.modelRotX = (short) ((int) (Math.sin((double) loopCycle / 40D) * 256D) & 0x7ff);
            if (char_edit_screen_update)
            {
                for (int k1 = 0; k1 < 7; k1++)
                {
                    int l1 = body_part_list[k1];
                    if (l1 >= 0 && !IdentityKit.cache[l1].method537())
                        return;
                }
                char_edit_screen_update = false;
                Model aclass30_sub2_sub4_sub6s[] = new Model[7];
                int i2 = 0;
                for (int j2 = 0; j2 < 7; j2++)
                {
                    int k2 = body_part_list[j2];
                    if (k2 >= 0)
                        aclass30_sub2_sub4_sub6s[i2++] = IdentityKit.cache[k2].method538();
                }
                Model model = new Model(i2, aclass30_sub2_sub4_sub6s);
                for (int l2 = 0; l2 < 5; l2++)
                    if (player_outfit_colors[l2] != 0)
                    {
                        model.method476(player_outfit_color_array[l2][0], player_outfit_color_array[l2][player_outfit_colors[l2]]);
                        if (l2 == 1)
                            model.method476(anIntArray1204[0], anIntArray1204[player_outfit_colors[l2]]);
                    }
                model.method469();
                model.method470(Sequence.anims[myPlayer.anInt1511].anIntArray353[0]);
                model.light(64, 850, -30, -50, -30, true);
                class9.mediaType = 5;
                class9.mediaID = 0;
                RSInterface.method208(model);
            }
            return;
        }
        if (j == 328)
        {
            RSInterface rsInterface = class9;
            int verticleTilt = 150;
            int animationSpeed = (int) (Math.sin((double) loopCycle / 40D) * 256D) & 0x7ff;
            rsInterface.modelRotY = (short) verticleTilt;
            rsInterface.modelRotX = (short) animationSpeed;
            Model characterDisplay = myPlayer.method452(true);
            rsInterface.mediaType = 5;
            rsInterface.mediaID = 0;
            RSInterface.method208(characterDisplay);
            return;
        }
        if (j == 324)
        {
            if (aClass30_Sub2_Sub1_Sub1_931 == null)
            {
                aClass30_Sub2_Sub1_Sub1_931 = class9.disabledSprite;
                aClass30_Sub2_Sub1_Sub1_932 = class9.enabledSprite;
            }
            if (gender)
            {
                class9.disabledSprite = aClass30_Sub2_Sub1_Sub1_932;
                return;
            }
            else
            {
                class9.disabledSprite = aClass30_Sub2_Sub1_Sub1_931;
                return;
            }
        }
        if (j == 325)
        {
            if (aClass30_Sub2_Sub1_Sub1_931 == null)
            {
                aClass30_Sub2_Sub1_Sub1_931 = class9.disabledSprite;
                aClass30_Sub2_Sub1_Sub1_932 = class9.enabledSprite;
            }
            if (gender)
            {
                class9.disabledSprite = aClass30_Sub2_Sub1_Sub1_931;
                return;
            }
            else
            {
                class9.disabledSprite = aClass30_Sub2_Sub1_Sub1_932;
                return;
            }
        }
        if (j == 600)
        {
            class9.disabledMessage = reportAbuseInput;
            if (loopCycle % 20 < 10)
            {
                class9.disabledMessage += "|";
                return;
            }
            else
            {
                class9.disabledMessage += " ";
                return;
            }
        }
        if (j == 620)
            if (myPrivilege >= 1)
            {
                if (canMute)
                {
                    class9.disabledTextColor = 0xff0000;
                    class9.disabledMessage = "Moderator option: Mute player for 48 hours: <ON>";
                }
                else
                {
                    class9.disabledTextColor = 0xffffff;
                    class9.disabledMessage = "Moderator option: Mute player for 48 hours: <OFF>";
                }
            }
            else
            {
                class9.disabledMessage = "";
            }
    }

    public void drawSplitPrivateChat()
    {
        if (splitPrivateChat == 0)
            return;
        TextDrawingArea textDrawingArea = regularText;
        int i = 0;
        if (systemUpdateTime != 0)
            i = 1;
        for (int j = 0; j < 100; j++)
            if (chatMessages[j] != null)
            {
                int k = chatTypes[j];
                String s = chatNames[j];
                byte byte1 = 0;
                if (s != null && s.startsWith("@cr1@"))
                {
                    s = s.substring(5);
                    byte1 = 1;
                }
                if (s != null && s.startsWith("@cr2@"))
                {
                    s = s.substring(5);
                    byte1 = 2;
                }
                if (s != null && s.startsWith("@cr3@"))
                {
                    s = s.substring(5);
                    byte1 = 3;
                }
                if ((k == 3 || k == 7) && (k == 7 || chatTabMode[3] == 0 || chatTabMode[3] == 1 && isFriendOrSelf(s)))
                {
                    int l = (clientSize == CLIENT_FIXED ? 329 : (chatHidden ? clientHeight - 30 : clientHeight - 170)) - i * 13;
                    int k1 = 4;
                    textDrawingArea.method385(0, "From", l, k1);
                    textDrawingArea.method385(65535, "From", l - 1, k1);
                    k1 += textDrawingArea.getTextWidth("From ");
                    if (byte1 != 0)
                    {
                        modIcons[byte1 - 1].drawSprite(k1 - 1, l - 12);
                        k1 += 13;
                    }
                    textDrawingArea.method385(0, s + ": " + chatMessages[j], l, k1);
                    textDrawingArea.method385(65535, s + ": " + chatMessages[j], l - 1, k1);
                    if (++i >= 5)
                        return;
                }
                if (k == 5 && chatTabMode[3] < 2)
                {
                    int i1 = (clientSize == CLIENT_FIXED ? 329 : (chatHidden ? clientHeight - 30 : clientHeight - 170)) - i * 13;
                    textDrawingArea.method385(0, chatMessages[j], i1, 4);
                    textDrawingArea.method385(65535, chatMessages[j], i1 - 1, 4);
                    if (++i >= 5)
                        return;
                }
                if (k == 6 && chatTabMode[3] < 2)
                {
                    int j1 = (clientSize == CLIENT_FIXED ? 329 : (chatHidden ? clientHeight - 30 : clientHeight - 170)) - i * 13;
                    textDrawingArea.method385(0, "To " + s + ": " + chatMessages[j], j1, 4);
                    textDrawingArea.method385(65535, "To " + s + ": " + chatMessages[j], j1 - 1, 4);
                    if (++i >= 5)
                        return;
                }
            }
    }

    public void pushMessage(String s, int i, String s1)
    {
        if (i == 0 && dialogID != -1)
        {
            aString844 = s;
            super.clickMode3 = 0;
        }
        if (backDialogID == -1)
            inputTaken = true;
        for (int j = 99; j > 0; j--)
        {
            chatTypes[j] = chatTypes[j - 1];
            chatNames[j] = chatNames[j - 1];
            chatMessages[j] = chatMessages[j - 1];
            chatRights[j] = chatRights[j - 1];
        }
        chatTypes[0] = (byte) i;
        chatNames[0] = s1;
        chatMessages[0] = s;
        chatRights[0] = rights;

        switch (i)
        {
        case 0: // Game
            if (chatStoneHoverState[0] != 0 && chatStoneHoverState[1] != 0)
            {
                chatStoneHoverState[1] = 2;
                inputTaken = true;
            }
            break;
        case 1: // Public
            if (chatStoneHoverState[0] != 0 && chatStoneHoverState[2] != 0)
            {
                chatStoneHoverState[2] = 2;
                inputTaken = true;
            }
            break;
        case 3: // Private
        case 7:
            if (chatStoneHoverState[3] != 0 && splitPrivateChat == 0)
            {
                chatStoneHoverState[3] = 2;
                inputTaken = true;
            }
            break;
        case 4: // Trade, duel
        case 8:
            if (chatStoneHoverState[0] != 0 && chatStoneHoverState[5] != 0)
            {
                chatStoneHoverState[5] = 2;
                inputTaken = true;
            }
            break;
        }
    }

    public void processInvChatTabs()
    {
        if (mouseIsWithin(5, 480 + (clientSize == CLIENT_FIXED ? 0 : clientHeight - 504), 55, 22))
        {
            createMenu1Option(1120, "View All");
        }
        else if (mouseIsWithin(71, 480 + (clientSize == CLIENT_FIXED ? 0 : clientHeight - 504), 55, 22))
        {
            createMenu1Option(1121, "View Game");
        }
        else if (mouseIsWithin(137, 480 + (clientSize == CLIENT_FIXED ? 0 : clientHeight - 504), 55, 22))
        {
            createMenu5Options(1115, 1116, 1117, 1118, 1119, "View Public", "On Public", "Friends Public", "Off Public", "Hide Public");
        }
        else if (mouseIsWithin(203, 480 + (clientSize == CLIENT_FIXED ? 0 : clientHeight - 504), 55, 22))
        {
            createMenu4Options(1140, 1141, 1142, 1143, "View Private", "On Private", "Friends Private", "Off Private");
        }
        else if (mouseIsWithin(269, 480 + (clientSize == CLIENT_FIXED ? 0 : clientHeight - 504), 55, 22))
        {
            createMenu1Option(1131, "View Clan");
        }
        else if (mouseIsWithin(335, 480 + (clientSize == CLIENT_FIXED ? 0 : clientHeight - 504), 55, 22))
        {
            createMenu4Options(1144, 1145, 1146, 1147, "View Trade", "On Trade", "Friends Trade", "Off Trade");
        }
        else if (mouseIsWithin(404, 480 + (clientSize == CLIENT_FIXED ? 0 : clientHeight - 504), 110, 22))
        {
            createMenu1Option(1130, "Report Abuse");
        }

        else if (mouseIsWithin((clientSize == CLIENT_FIXED ? 522 : (clientWidth >= 1006 ? clientWidth - 476 : clientWidth - 243)), (clientSize == CLIENT_FIXED ? 168 : (clientWidth >= 1006 ? clientHeight - 37 : clientHeight - 75)), (clientSize == CLIENT_FIXED ? 37 : (clientWidth >= 1006 ? 32 : 37)), 35) && (tabInterfaceIDs[0] != -1) && (invOverlayInterfaceID == -1))
        {
            createMenu1Option(1100, "Combat Styles");
        }
        else if (mouseIsWithin((clientSize == CLIENT_FIXED ? 560 : (clientWidth >= 1006 ? clientWidth - 442 : clientWidth - 205)), (clientSize == CLIENT_FIXED ? 168 : (clientWidth >= 1006 ? clientHeight - 37 : clientHeight - 75)), (clientSize == CLIENT_FIXED ? 32 : (clientWidth >= 1006 ? 32 : 32)), 35) && (tabInterfaceIDs[1] != -1) && (invOverlayInterfaceID == -1))
        {
            createMenu1Option(1101, "Stats");
        }
        else if (mouseIsWithin((clientSize == CLIENT_FIXED ? 593 : (clientWidth >= 1006 ? clientWidth - 408 : clientWidth - 172)), (clientSize == CLIENT_FIXED ? 168 : (clientWidth >= 1006 ? clientHeight - 37 : clientHeight - 75)), (clientSize == CLIENT_FIXED ? 32 : (clientWidth >= 1006 ? 32 : 32)), 35) && (tabInterfaceIDs[2] != -1) && (invOverlayInterfaceID == -1))
        {
            createMenu1Option(1102, "Quests");
        }
        else if (mouseIsWithin((clientSize == CLIENT_FIXED ? 626 : (clientWidth >= 1006 ? clientWidth - 374 : clientWidth - 139)), (clientSize == CLIENT_FIXED ? 168 : (clientWidth >= 1006 ? clientHeight - 37 : clientHeight - 75)), (clientSize == CLIENT_FIXED ? 32 : (clientWidth >= 1006 ? 32 : 32)), 35) && (tabInterfaceIDs[3] != -1) && (invOverlayInterfaceID == -1))
        {
            createMenu1Option(1103, "Inventory");
        }
        else if (mouseIsWithin((clientSize == CLIENT_FIXED ? 659 : (clientWidth >= 1006 ? clientWidth - 340 : clientWidth - 106)), (clientSize == CLIENT_FIXED ? 168 : (clientWidth >= 1006 ? clientHeight - 37 : clientHeight - 75)), (clientSize == CLIENT_FIXED ? 32 : (clientWidth >= 1006 ? 32 : 32)), 35) && (tabInterfaceIDs[4] != -1) && (invOverlayInterfaceID == -1))
        {
            createMenu1Option(1104, "Equipment");
        }
        else if (mouseIsWithin((clientSize == CLIENT_FIXED ? 692 : (clientWidth >= 1006 ? clientWidth - 306 : clientWidth - 73)), (clientSize == CLIENT_FIXED ? 168 : (clientWidth >= 1006 ? clientHeight - 37 : clientHeight - 75)), (clientSize == CLIENT_FIXED ? 32 : (clientWidth >= 1006 ? 32 : 32)), 35) && (tabInterfaceIDs[5] != -1) && (invOverlayInterfaceID == -1))
        {
            createMenu1Option(1105, "Prayer");
        }
        else if (mouseIsWithin((clientSize == CLIENT_FIXED ? 725 : (clientWidth >= 1006 ? clientWidth - 272 : clientWidth - 40)), (clientSize == CLIENT_FIXED ? 168 : (clientWidth >= 1006 ? clientHeight - 37 : clientHeight - 75)), (clientSize == CLIENT_FIXED ? 37 : (clientWidth >= 1006 ? 32 : 37)), 35) && (tabInterfaceIDs[6] != -1) && (invOverlayInterfaceID == -1))
        {
            createMenu1Option(1106, "Spellbook");
        }
        else if (mouseIsWithin((clientSize == CLIENT_FIXED ? 522 : (clientWidth >= 1006 ? clientWidth - 238 : clientWidth - 243)), (clientSize == CLIENT_FIXED ? 466 : (clientWidth >= 1006 ? clientHeight - 37 : clientHeight - 39)), (clientSize == CLIENT_FIXED ? 37 : (clientWidth >= 1006 ? 32 : 37)), (clientWidth >= 1006 ? 35 : 36)) && (tabInterfaceIDs[7] != -1) && (invOverlayInterfaceID == -1))
        {
            createMenu1Option(1114, "Clan Chat");
        }
        else if (mouseIsWithin((clientSize == CLIENT_FIXED ? 560 : (clientWidth >= 1006 ? clientWidth - 204 : clientWidth - 205)), (clientSize == CLIENT_FIXED ? 466 : (clientWidth >= 1006 ? clientHeight - 37 : clientHeight - 39)), (clientSize == CLIENT_FIXED ? 32 : (clientWidth >= 1006 ? 32 : 32)), (clientWidth >= 1006 ? 35 : 36)) && (tabInterfaceIDs[8] != -1) && (invOverlayInterfaceID == -1))
        {
            createMenu1Option(1108, "Friends List");
        }
        else if (mouseIsWithin((clientSize == CLIENT_FIXED ? 593 : (clientWidth >= 1006 ? clientWidth - 170 : clientWidth - 172)), (clientSize == CLIENT_FIXED ? 466 : (clientWidth >= 1006 ? clientHeight - 37 : clientHeight - 39)), (clientSize == CLIENT_FIXED ? 32 : (clientWidth >= 1006 ? 32 : 32)), (clientWidth >= 1006 ? 35 : 36)) && (tabInterfaceIDs[9] != -1) && (invOverlayInterfaceID == -1))
        {
            createMenu1Option(1109, "Ignore List");
        }
        else if (mouseIsWithin((clientSize == CLIENT_FIXED ? 626 : (clientWidth >= 1006 ? clientWidth - 136 : clientWidth - 139)), (clientSize == CLIENT_FIXED ? 466 : (clientWidth >= 1006 ? clientHeight - 37 : clientHeight - 39)), (clientSize == CLIENT_FIXED ? 32 : (clientWidth >= 1006 ? 32 : 32)), (clientWidth >= 1006 ? 35 : 36)) && (tabInterfaceIDs[10] != -1) && (invOverlayInterfaceID == -1))
        {
            createMenu1Option(1110, "Logout");
        }
        else if (mouseIsWithin((clientSize == CLIENT_FIXED ? 659 : (clientWidth >= 1006 ? clientWidth - 102 : clientWidth - 106)), (clientSize == CLIENT_FIXED ? 466 : (clientWidth >= 1006 ? clientHeight - 37 : clientHeight - 39)), (clientSize == CLIENT_FIXED ? 32 : (clientWidth >= 1006 ? 32 : 32)), (clientWidth >= 1006 ? 35 : 36)) && (tabInterfaceIDs[11] != -1) && (invOverlayInterfaceID == -1))
        {
            createMenu1Option(1111, "Settings");
        }
        else if (mouseIsWithin((clientSize == CLIENT_FIXED ? 692 : (clientWidth >= 1006 ? clientWidth - 68 : clientWidth - 73)), (clientSize == CLIENT_FIXED ? 466 : (clientWidth >= 1006 ? clientHeight - 37 : clientHeight - 39)), (clientSize == CLIENT_FIXED ? 32 : (clientWidth >= 1006 ? 32 : 32)), (clientWidth >= 1006 ? 35 : 36)) && (tabInterfaceIDs[12] != -1) && (invOverlayInterfaceID == -1))
        {
            createMenu1Option(1112, "Emotes");
        }
        else if (mouseIsWithin((clientSize == CLIENT_FIXED ? 725 : (clientWidth >= 1006 ? clientWidth - 34 : clientWidth - 40)), (clientSize == CLIENT_FIXED ? 466 : (clientWidth >= 1006 ? clientHeight - 37 : clientHeight - 39)), (clientSize == CLIENT_FIXED ? 37 : (clientWidth >= 1006 ? 32 : 37)), (clientWidth >= 1006 ? 35 : 36)) && (tabInterfaceIDs[13] != -1) && (invOverlayInterfaceID == -1))
        {
            createMenu1Option(1113, "Music");
        }
    }

    public void resetImageProducers2()
    {
        if ((chatImageProducer != null && clientSize == CLIENT_FIXED) || (clientSize != 0 && fullscreenInterfaceID == -1 && gameScreenImageProducer != null))
            return;
        System.err.println("resetImageProducer2 - called");
        super.fullGameScreen = null;
        loginScreen = null;
        if (clientSize == CLIENT_FIXED)
            chatImageProducer = new RSImageProducer(516, 165, getGameComponent());
        if (clientSize == CLIENT_FIXED)
            mapAreaImageProducer = new RSImageProducer(249, 168, getGameComponent());
        RSRaster.setAllPixelsToZero();
        mapArea[(clientSize == CLIENT_FIXED ? 0 : 2)].drawSprite(0, 0);
        if (clientSize == CLIENT_FIXED)
            inventoryImageProducer = new RSImageProducer(249, 335, getGameComponent());
        gameScreenImageProducer = new RSImageProducer(clientSize == CLIENT_FIXED ? 512 : clientWidth, clientSize == CLIENT_FIXED ? 334 : clientHeight, getGameComponent());
        RSRaster.setAllPixelsToZero();
        welcomeScreenRaised = true;
    }

    public void markRedFlag(IndexedImage sprite, int y, int x)
    {
        int l = x * x + y * y;
        if (l > 4225 && l < 0x15f90)
        {
            int i1 = viewRotation + minimapRotation & 0x7ff;
            int j1 = Model.SINE[i1];
            int k1 = Model.COSINE[i1];
            j1 = (j1 * 256) / (minimapZoom + 256);
            k1 = (k1 * 256) / (minimapZoom + 256);
            int l1 = y * j1 + x * k1 >> 16;
            int i2 = y * k1 - x * j1 >> 16;
            double d = Math.atan2(l1, i2);
            int j2 = (int) (Math.sin(d) * 63D);
            int k2 = (int) (Math.cos(d) * 57D);
            mapEdge.method353((clientSize == CLIENT_FIXED ? 86 : 89) - k2 - 20, d, ((clientSize == CLIENT_FIXED ? 131 : clientWidth + 131 - 238) + j2) - 10 - 5);
        }
        else
        {
            markMinimap(sprite, x, y);
        }
    }

    public void setTab(int i)
    {
        if ((tabID == i || invHidden) && clientSize != 0)
            invHidden = !invHidden;

        tabID = i;
        spellSelected = 0;
        itemSelected = 0;
        needDrawTabArea = true;
    }

    public void processRightClick()
    {
        if (activeInterfaceType != 0)
        {
            return;
        }
        menuActionName[0] = "Cancel";
        menuActionID[0] = 1107;
        menuActionRow = 1;
        if (clientSize == CLIENT_FIXED)
        {
            if (fullscreenInterfaceID != -1)
            {
                anInt886 = 0;
                anInt1315 = 0;
                buildInterfaceMenu(8, RSInterface.interfaceCache[fullscreenInterfaceID], super.mouseX, 8, super.mouseY, 0);
                if (anInt886 != anInt1026)
                {
                    anInt1026 = anInt886;
                }
                if (anInt1315 != anInt1129)
                {
                    anInt1129 = anInt1315;
                }
                return;
            }
        }
        if (clientSize != CLIENT_FIXED)
        {
            if (fullscreenInterfaceID != -1)
            {
                anInt886 = 0;
                anInt1315 = 0;
                buildInterfaceMenu((clientWidth / 2) - 765 / 2, RSInterface.interfaceCache[fullscreenInterfaceID], super.mouseX, (clientHeight / 2) - 503 / 2, super.mouseY, 0);
                if (anInt886 != anInt1026)
                {
                    anInt1026 = anInt886;
                }
                if (anInt1315 != anInt1129)
                {
                    anInt1129 = anInt1315;
                }
                return;
            }
        }
        buildSplitPrivateChatMenu();
        anInt886 = 0;
        anInt1315 = 0;
        if (clientSize == CLIENT_FIXED)
        {
            if (super.mouseX > 4 && super.mouseY > 4 && super.mouseX < 516 && super.mouseY < 338)
            {
                if (openInterfaceID != -1)
                {
                    buildInterfaceMenu(4, RSInterface.interfaceCache[openInterfaceID], super.mouseX, 4, super.mouseY, 0);
                }
                else
                {
                    build3dScreenMenu();
                }
            }
        }
        else
        {
            if (super.mouseX >= 0 && super.mouseY >= 0 && super.mouseX < clientWidth && super.mouseY < clientHeight)
            {
                if (openInterfaceID != -1)
                {
                    buildInterfaceMenu(returnGeneralInterfaceOffsetX(), RSInterface.interfaceCache[openInterfaceID], super.mouseX, (clientHeight / 2) - 256, super.mouseY, 0);
                }
                else
                {
                    // Disable chat, inventory, map areas
                    // When if is true then it will draw 3d menu (walk here etc)
                    // RSRaster.drawPixels(151, 12,
                    // clientWidth - 184, 0xff0000, 146);
                    if (!((mouseIsWithin(clientWidth - 219, clientHeight - 351, 204, 274) && clientWidth <= 1005 && !invHidden) || (mouseIsWithin(clientWidth - 206, clientHeight - 313, 206, 275) && clientWidth >= 1006 && !invHidden) || (mouseIsWithin(clientWidth - 220, 0, 220, 171)) || (mouseIsWithin(clientWidth - 476, clientHeight - 38, 476, 38) && clientWidth >= 1006) || (mouseIsWithin(clientWidth - 244, clientHeight - 76, 244, 76) && clientWidth <= 1005) || (mouseIsWithin(0, clientHeight - 165, 519, 165) && !chatHidden) || (mouseIsWithin(0, clientHeight - 25, 516, 25) && chatHidden)))
                        build3dScreenMenu();
                }
            }
        }
        if (anInt886 != anInt1026)
        {
            anInt1026 = anInt886;
        }
        if (anInt1315 != anInt1129)
        {
            anInt1129 = anInt1315;
        }
        anInt886 = 0;
        anInt1315 = 0;
        if (clientSize == CLIENT_FIXED)
        {
            if (super.mouseX > 554 - 6 && super.mouseY > 204 && super.mouseX <= 736 && super.mouseY < 466)
            {
                if (invOverlayInterfaceID != -1)
                {
                    buildInterfaceMenu(553 - 6, RSInterface.interfaceCache[invOverlayInterfaceID], super.mouseX, 205, super.mouseY, 0);
                }
                else if (tabInterfaceIDs[tabID] != -1)
                {
                    buildInterfaceMenu(553 - 6, RSInterface.interfaceCache[tabInterfaceIDs[tabID]], super.mouseX, 205, super.mouseY, 0);
                }
            }
        }
        else if (!invHidden)
        {
            if (super.mouseX >= (clientWidth >= 1006 ? clientWidth - 249 + 50 : clientWidth - 212) && super.mouseY >= (clientWidth >= 1006 ? clientHeight - 307 : clientHeight - 345) && super.mouseX <= (clientWidth >= 1006 ? clientWidth - 9 : clientWidth - 22) && super.mouseY <= (clientWidth >= 1006 ? clientHeight - 45 : clientHeight - 83))
            {
                if (invOverlayInterfaceID != -1)
                {
                    buildInterfaceMenu((clientWidth >= 1006 ? clientWidth - 249 + 50 : clientWidth - 212), RSInterface.interfaceCache[invOverlayInterfaceID], super.mouseX, (clientWidth >= 1006 ? clientHeight - 307 : clientHeight - 345), super.mouseY, 0);
                }
                else if (tabInterfaceIDs[tabID] != -1)
                {
                    buildInterfaceMenu((clientWidth >= 1006 ? clientWidth - 249 + 50 : clientWidth - 212), RSInterface.interfaceCache[tabInterfaceIDs[tabID]], super.mouseX, (clientWidth >= 1006 ? clientHeight - 307 : clientHeight - 345), super.mouseY, 0);
                }
            }
        }
        if (anInt886 != anInt1048)
        {
        	//System.out.println(anInt886 + " - " + anInt1048);
            needDrawTabArea = true;
            anInt1048 = anInt886;
        }
        if (anInt1315 != anInt1044)
        {
            needDrawTabArea = true;
            anInt1044 = anInt1315;
        }
        anInt886 = 0;
        anInt1315 = 0;

        if (clientSize == CLIENT_FIXED)
        {
            if (super.mouseX > 7 && super.mouseY > 345 && super.mouseX < 496 && super.mouseY < 474)
            {
                if (backDialogID != -1 && (inputDialogState == 0 && !messagePromptRaised))
                {
                    buildInterfaceMenu(20, RSInterface.interfaceCache[backDialogID], super.mouseX, 362, super.mouseY, 0);
                }
                else if (super.mouseY < 474 && super.mouseX < 496 && (inputDialogState == 0 && !messagePromptRaised))
                {
                    buildChatAreaMenu(super.mouseY - 339);
                }
            }
        }
        else
        {
            if (super.mouseX >= 7 && super.mouseY >= clientHeight - 158 && super.mouseX <= 494 && super.mouseY <= clientHeight - 45 && !chatHidden)
            {
                if (backDialogID != -1 && (inputDialogState == 0 && !messagePromptRaised))
                {
                    buildInterfaceMenu(20, RSInterface.interfaceCache[backDialogID], super.mouseX, 0, super.mouseY - clientHeight + 141, 0);
                }
                else if (inputDialogState == 0 && !messagePromptRaised)
                {
                    buildChatAreaMenu(super.mouseY - clientHeight + 163);
                }
            }
        }
        if (anInt886 != anInt1039)
        {
            inputTaken = true;
            anInt1039 = anInt886;
        }
        processInvChatTabs();
        if (anInt1315 != anInt1500)
        {
            inputTaken = true;
            anInt1500 = anInt1315;
        }
        boolean flag = false;
        while (!flag)
        {
            flag = true;
            for (int j = 0; j < menuActionRow - 1; j++)
            {
                if (menuActionID[j] < 1000 && menuActionID[j + 1] > 1000)
                {
                    String s = menuActionName[j];
                    menuActionName[j] = menuActionName[j + 1];
                    menuActionName[j + 1] = s;
                    int k = menuActionID[j];
                    menuActionID[j] = menuActionID[j + 1];
                    menuActionID[j + 1] = k;
                    k = menuActionCmd2[j];
                    menuActionCmd2[j] = menuActionCmd2[j + 1];
                    menuActionCmd2[j + 1] = k;
                    k = menuActionCmd3[j];
                    menuActionCmd3[j] = menuActionCmd3[j + 1];
                    menuActionCmd3[j + 1] = k;
                    k = menuActionCmd1[j];
                    menuActionCmd1[j] = menuActionCmd1[j + 1];
                    menuActionCmd1[j + 1] = k;
                    flag = false;
                }
            }
        }
    }

    public void setNorth()
    {
        cameraOffsetX = 0;
        cameraOffsetY = 0;
        viewRotationOffset = 0;
        viewRotation = 0;
        minimapRotation = 0;
        minimapZoom = 0;
    }

    // TODO: Used to scroll every scroller to start for fresh game
    private void setScrollPosition()
    {
        for (int k = 0; k < RSInterface.interfaceCache.length; k++)
        {
            RSInterface class9_3 = RSInterface.interfaceCache[k];
            if (class9_3 != null && class9_3.type == 0)
            {
                class9_3.scrollPosition = 0;
            }
        }
    }

    public void login(String s, String s1, boolean reconnect)
    {
        try
        {
            if (!reconnect)
            {
                loginMessage1 = "";
                status = "Connecting to server...";
                myUsername = capitalize(myUsername);
                drawLoginScreen(true);
            }
            socketStream = new RSSocket(this, openSocket(43594 + portOff));
            long l = TextClass.longForName(s);
            int i = (int) (l >> 16 & 31L);
            stream.pointer = 0;
            stream.writeByte(14);
            stream.writeByte(i);
            socketStream.queueBytes(2, stream.buffer);
            for (int j = 0; j < 8; j++)
                socketStream.read();
            int k = socketStream.read();
            int i1 = k;
            if (k == 0)
            {
                socketStream.flushInputStream(inStream.buffer, 8);
                inStream.pointer = 0;
                aLong1215 = inStream.readLong();
                int ai[] = new int[4];
                ai[0] = (int) (Math.random() * 99999999D);
                ai[1] = (int) (Math.random() * 99999999D);
                ai[2] = (int) (aLong1215 >> 32);
                ai[3] = (int) aLong1215;
                stream.pointer = 0;
                stream.writeByte(10);
                stream.writeInt(ai[0]);
                stream.writeInt(ai[1]);
                stream.writeInt(ai[2]);
                stream.writeInt(ai[3]);
                stream.writeInt(magic_value);// Signlink.getUID());
                stream.writeString(s);
                stream.writeString(s1);
                stream.applyRSA();
                aStream_847.pointer = 0;
                if (reconnect)
                    aStream_847.writeByte(18);
                else
                    aStream_847.writeByte(16);
                aStream_847.writeByte(stream.pointer + 36 + 1 + 1 + 2);
                aStream_847.writeByte(255);
                aStream_847.writeShort(317);
                aStream_847.writeByte(lowMemory ? 1 : 0);
                for (int l1 = 0; l1 < 9; l1++)
                    aStream_847.writeInt(expectedCRCs[l1]);
                aStream_847.writeBytes(stream.buffer, stream.pointer, 0);
                stream.encryption = new ISAACGenerator(ai);
                for (int j2 = 0; j2 < 4; j2++)
                    ai[j2] += 50;
                encryption = new ISAACGenerator(ai);
                socketStream.queueBytes(aStream_847.pointer, aStream_847.buffer);
                k = socketStream.read();
            }
            if (k == 1)
            {
                try
                {
                    Thread.sleep(2000L);
                }
                catch (Exception _ex)
                {
                }
                login(s, s1, reconnect);
                return;
            }
            if (k == 2)
            {
                myPrivilege = socketStream.read();
                if (music_enabled)
                {
                    SoundProvider.getInstance().fadeMidi(true);
                }
                super.awtFocus = true;
                aBoolean954 = true;
                loggedIn = true;
                draw_sprites_logon = true;
                invHidden = false;
                chatHidden = false;
                setScrollPosition();
                changeActiveChatStoneState(0);
                atLoginMenu = false;
                stream.pointer = 0;
                inStream.pointer = 0;
                opCode = -1;
                anInt841 = -1;
                anInt842 = -1;
                anInt843 = -1;
                pktSize = 0;
                timeoutCounter = 0;
                systemUpdateTime = 0;
                anInt1011 = 0;
                hintType = 0;
                menuActionRow = 0;
                menuOpen = false;
                super.idleTime = 0;
                for (int j1 = 0; j1 < 100; j1++)
                    chatMessages[j1] = null;
                itemSelected = 0;
                spellSelected = 0;
                loadingStage = 0;
                currentSound = 0;
                setNorth();
                hideMinimap = 0;
                anInt985 = -1;
                destX = 0;
                destY = 0;
                playerCount = 0;
                npcCount = 0;
                for (int i2 = 0; i2 < maxPlayers; i2++)
                {
                    playerArray[i2] = null;
                    aStreamArray895s[i2] = null;
                }
                for (int k2 = 0; k2 < 16384; k2++)
                    npcArray[k2] = null;
                myPlayer = playerArray[myPlayerIndex] = new Player();
                aClass19_1013.removeAll();
                stillGraphicDeque.removeAll();
                for (int l2 = 0; l2 < 4; l2++)
                {
                    for (int i3 = 0; i3 < 104; i3++)
                    {
                        for (int k3 = 0; k3 < 104; k3++)
                            groundArray[l2][i3][k3] = null;
                    }
                }
                aClass19_1179 = new Deque();
                fullscreenInterfaceID = -1;
                anInt900 = 0;
                friendsCount = 0;
                dialogID = -1;
                backDialogID = -1;
                openInterfaceID = -1;
                invOverlayInterfaceID = -1;
                walkableInterface = -1;
                aBoolean1149 = false;
                tabID = 3;
                inputDialogState = 0;
                menuOpen = false;
                messagePromptRaised = false;
                aString844 = null;
                drawMultiIcon = 0;
                anInt1054 = -1;
                gender = true;
                method45();
                for (int j3 = 0; j3 < 5; j3++)
                    player_outfit_colors[j3] = 0;
                for (int l3 = 0; l3 < 5; l3++)
                {
                    atPlayerActions[l3] = null;
                    atPlayerArray[l3] = false;
                }
                resetImageProducers2();
                return;
            }
            if (k == 3)
            {
                loginMessage1 = "";
                status = "Invalid username or password.";
                return;
            }
            if (k == 4)
            {
                status = "Your account has been disabled.";
                loginMessage2 = "Please check your message-center for details.";
                return;
            }
            if (k == 5)
            {
                status = "Your account is already logged in.";
                loginMessage2 = "Try again in 60 secs...";
                return;
            }
            if (k == 6)
            {
                status = "Exorth has been updated!";
                loginMessage2 = "Please reload this page.";
                return;
            }
            if (k == 7)
            {
                status = "This world is full.";
                loginMessage2 = "Please use a different world.";
                return;
            }
            if (k == 8)
            {
                status = "Unable to connect.";
                loginMessage2 = "Login server offline.";
                return;
            }
            if (k == 9)
            {
                status = "Login limit exceeded.";
                loginMessage2 = "Too many connections from your address.";
                return;
            }
            if (k == 10)
            {
                status = "Unable to connect.";
                loginMessage2 = "Bad session id.";
                return;
            }
            if (k == 11)
            {
                status = "Login server rejected session.";
                loginMessage2 = "Please try again.";
                return;
            }
            if (k == 12)
            {
                loginMessage1 = "You need a members account to login to this world.";
                loginMessage2 = "Please subscribe, or use a different world.";
                return;
            }
            if (k == 13)
            {
                status = "Could not complete login.";
                loginMessage2 = "Please try using a different world.";
                return;
            }
            if (k == 14)
            {
                status = "The server is being updated.";
                loginMessage2 = "Please wait 1 minute and try again.";
                return;
            }
            if (k == 15)
            {
                loggedIn = true;
                stream.pointer = 0;
                inStream.pointer = 0;
                opCode = -1;
                anInt841 = -1;
                anInt842 = -1;
                anInt843 = -1;
                pktSize = 0;
                timeoutCounter = 0;
                systemUpdateTime = 0;
                menuActionRow = 0;
                menuOpen = false;
                aLong824 = System.currentTimeMillis();
                return;
            }
            if (k == 16)
            {
                status = "Login attempts exceeded.";
                loginMessage2 = "Please wait 5 minutes and try again.";
                return;
            }
            if (k == 17)
            {
                loginMessage1 = "You are standing in a members-only area.";
                loginMessage2 = "To play on this world move to a free area first";
                return;
            }
            if (k == 20)
            {
                loginMessage1 = "Invalid loginserver requested";
                loginMessage2 = "Please try using a different world.";
                return;
            }
            if (k == 21)
            {
                for (int k1 = socketStream.read(); k1 >= 0; k1--)
                {
                    loginMessage1 = "You have only just left another world";
                    loginMessage2 = "Your profile will be transferred in: " + k1 + " seconds";
                    drawLoginScreen(true);
                    try
                    {
                        Thread.sleep(1000L);
                    }
                    catch (Exception _ex)
                    {
                    }
                }
                login(s, s1, reconnect);
                return;
            }
            if (k == -1)
            {
                if (i1 == 0)
                {
                    if (loginFailures < 2)
                    {
                        try
                        {
                            Thread.sleep(2000L);
                        }
                        catch (Exception _ex)
                        {
                        }
                        loginFailures++;
                        login(s, s1, reconnect);
                        return;
                    }
                    else
                    {
                        status = "No response from loginserver";
                        loginMessage2 = "Please wait 1 minute and try again.";
                        return;
                    }
                }
                else
                {
                    status = "No response from server";
                    loginMessage2 = "Please try using a different world.";
                    return;
                }
            }
            else
            {
                status = "Unexpected server response";
                loginMessage2 = "Please try using a different world.";
                return;
            }
        }
        catch (IOException _ex)
        {
            loginMessage1 = "";
        }
        status = "Error connecting to server.";
    }

    public void resetWalk()
    {
        for (int l2 = 0; l2 < 104; l2++)
        {
            for (int i3 = 0; i3 < 104; i3++)
            {
                walk_prev[l2][i3] = 0;
                walk_dist[l2][i3] = 0x5f5e0ff;
            }
        }
    }

    private boolean doWalkTo(int i, int j, int k, int i1, int j1, int k1, int l1, int y, int j2, boolean flag, int x)
    {
        try
        {
            resetWalk();
            int j3 = j2;
            int k3 = j1;
            walk_prev[j2][j1] = 99;
            walk_dist[j2][j1] = 0;
            int l3 = 0;
            int i4 = 0;
            bigX[l3] = j2;
            bigY[l3++] = j1;
            boolean flag1 = false;
            int j4 = bigX.length;
            int ai[][] = collision_maps[floor_level].clips;
            while (i4 != l3)
            {
                j3 = bigX[i4];
                k3 = bigY[i4];
                i4 = (i4 + 1) % j4;
                if (j3 == x && k3 == y)
                {
                    flag1 = true;
                    break;
                }
                if (i1 != 0)
                {
                    if ((i1 < 5 || i1 == 10) && collision_maps[floor_level].blockedTile(x, j3, k3, j, i1 - 1, y))
                    {
                        flag1 = true;
                        break;
                    }
                    if (i1 < 10 && collision_maps[floor_level].method220(x, y, k3, i1 - 1, j, j3))
                    {
                        flag1 = true;
                        break;
                    }
                }
                if (k1 != 0 && k != 0 && collision_maps[floor_level].method221(y, x, j3, k, l1, k1, k3))
                {
                    flag1 = true;
                    break;
                }
                int l4 = walk_dist[j3][k3] + 1;
                if (j3 > 0 && walk_prev[j3 - 1][k3] == 0 && (ai[j3 - 1][k3] & 0x1280108) == 0)
                {
                    bigX[l3] = j3 - 1;
                    bigY[l3] = k3;
                    l3 = (l3 + 1) % j4;
                    walk_prev[j3 - 1][k3] = 2;
                    walk_dist[j3 - 1][k3] = l4;
                }
                if (j3 < 103 && walk_prev[j3 + 1][k3] == 0 && (ai[j3 + 1][k3] & 0x1280180) == 0)
                {
                    bigX[l3] = j3 + 1;
                    bigY[l3] = k3;
                    l3 = (l3 + 1) % j4;
                    walk_prev[j3 + 1][k3] = 8;
                    walk_dist[j3 + 1][k3] = l4;
                }
                if (k3 > 0 && walk_prev[j3][k3 - 1] == 0 && (ai[j3][k3 - 1] & 0x1280102) == 0)
                {
                    bigX[l3] = j3;
                    bigY[l3] = k3 - 1;
                    l3 = (l3 + 1) % j4;
                    walk_prev[j3][k3 - 1] = 1;
                    walk_dist[j3][k3 - 1] = l4;
                }
                if (k3 < 103 && walk_prev[j3][k3 + 1] == 0 && (ai[j3][k3 + 1] & 0x1280120) == 0)
                {
                    bigX[l3] = j3;
                    bigY[l3] = k3 + 1;
                    l3 = (l3 + 1) % j4;
                    walk_prev[j3][k3 + 1] = 4;
                    walk_dist[j3][k3 + 1] = l4;
                }
                if (j3 > 0 && k3 > 0 && walk_prev[j3 - 1][k3 - 1] == 0 && (ai[j3 - 1][k3 - 1] & 0x128010e) == 0 && (ai[j3 - 1][k3] & 0x1280108) == 0 && (ai[j3][k3 - 1] & 0x1280102) == 0)
                {
                    bigX[l3] = j3 - 1;
                    bigY[l3] = k3 - 1;
                    l3 = (l3 + 1) % j4;
                    walk_prev[j3 - 1][k3 - 1] = 3;
                    walk_dist[j3 - 1][k3 - 1] = l4;
                }
                if (j3 < 103 && k3 > 0 && walk_prev[j3 + 1][k3 - 1] == 0 && (ai[j3 + 1][k3 - 1] & 0x1280183) == 0 && (ai[j3 + 1][k3] & 0x1280180) == 0 && (ai[j3][k3 - 1] & 0x1280102) == 0)
                {
                    bigX[l3] = j3 + 1;
                    bigY[l3] = k3 - 1;
                    l3 = (l3 + 1) % j4;
                    walk_prev[j3 + 1][k3 - 1] = 9;
                    walk_dist[j3 + 1][k3 - 1] = l4;
                }
                if (j3 > 0 && k3 < 103 && walk_prev[j3 - 1][k3 + 1] == 0 && (ai[j3 - 1][k3 + 1] & 0x1280138) == 0 && (ai[j3 - 1][k3] & 0x1280108) == 0 && (ai[j3][k3 + 1] & 0x1280120) == 0)
                {
                    bigX[l3] = j3 - 1;
                    bigY[l3] = k3 + 1;
                    l3 = (l3 + 1) % j4;
                    walk_prev[j3 - 1][k3 + 1] = 6;
                    walk_dist[j3 - 1][k3 + 1] = l4;
                }
                if (j3 < 103 && k3 < 103 && walk_prev[j3 + 1][k3 + 1] == 0 && (ai[j3 + 1][k3 + 1] & 0x12801e0) == 0 && (ai[j3 + 1][k3] & 0x1280180) == 0 && (ai[j3][k3 + 1] & 0x1280120) == 0)
                {
                    bigX[l3] = j3 + 1;
                    bigY[l3] = k3 + 1;
                    l3 = (l3 + 1) % j4;
                    walk_prev[j3 + 1][k3 + 1] = 12;
                    walk_dist[j3 + 1][k3 + 1] = l4;
                }
            }
            anInt1264 = 0;
            if (!flag1)
            {
                if (flag)
                {
                    int i5 = 100;
                    for (int k5 = 1; k5 < 2; k5++)
                    {
                        for (int i6 = x - k5; i6 <= x + k5; i6++)
                        {
                            for (int l6 = y - k5; l6 <= y + k5; l6++)
                                if (i6 >= 0 && l6 >= 0 && i6 < 104 && l6 < 104 && walk_dist[i6][l6] < i5)
                                {
                                    i5 = walk_dist[i6][l6];
                                    j3 = i6;
                                    k3 = l6;
                                    anInt1264 = 1;
                                    flag1 = true;
                                }
                        }
                        if (flag1)
                            break;
                    }
                }
                if (!flag1)
                    return false;
            }
            i4 = 0;
            bigX[i4] = j3;
            bigY[i4++] = k3;
            int l5;
            for (int j5 = l5 = walk_prev[j3][k3]; j3 != j2 || k3 != j1; j5 = walk_prev[j3][k3])
            {
                if (j5 != l5)
                {
                    l5 = j5;
                    bigX[i4] = j3;
                    bigY[i4++] = k3;
                }
                if ((j5 & 2) != 0)
                    j3++;
                else if ((j5 & 8) != 0)
                    j3--;
                if ((j5 & 1) != 0)
                    k3++;
                else if ((j5 & 4) != 0)
                    k3--;
            }
            if (i4 > 0)
            {
                int k4 = i4;
                if (k4 > 25)
                    k4 = 25;
                i4--;
                int k6 = bigX[i4];
                int i7 = bigY[i4];
                // TODO: Dummy
                /*
                 * anInt1288 += k4; if (anInt1288 >= 92) {
                 * stream.createFrame(36); stream.writeDWord(0); anInt1288 = 0;
                 * }
                 */
                if (i == 0)
                {
                    stream.writeOpcode(164);
                    stream.writeByte(k4 + k4 + 3);
                }
                if (i == 1)
                {
                    stream.writeOpcode(248);
                    stream.writeByte(k4 + k4 + 3 + 14);
                }
                if (i == 2)
                {
                    stream.writeOpcode(98);
                    stream.writeByte(k4 + k4 + 3);
                }
                stream.method433(k6 + baseX);
                destX = bigX[0];
                destY = bigY[0];
                for (int j7 = 1; j7 < k4; j7++)
                {
                    i4--;
                    stream.writeByte(bigX[i4] - k6);
                    stream.writeByte(bigY[i4] - i7);
                }
                stream.method431(i7 + baseY);
                stream.readInverseByte(super.keyArray[5] != 1 ? 0 : 1);
                return true;
            }
            return i != 1;
        }
        catch (Exception e)
        {
        }
        return false;
    }

    public void method86(RSBuffer stream)
    {
        for (int j = 0; j < anInt893; j++)
        {
            int k = anIntArray894[j];
            NPC npc = npcArray[k];
            int l = stream.readUByte();
            if ((l & 0x10) != 0)
            {
                int i1 = stream.method434();
                if (i1 == 65535)
                    i1 = -1;
                int i2 = stream.readUByte();
                if (i1 == npc.anim && i1 != -1)
                {
                    int l2 = Sequence.anims[i1].anInt365;
                    if (l2 == 1)
                    {
                        npc.anInt1527 = 0;
                        npc.anInt1528 = 0;
                        npc.anInt1529 = i2;
                        npc.anInt1530 = 0;
                    }
                    if (l2 == 2)
                        npc.anInt1530 = 0;
                }
                else if (i1 == -1 || npc.anim == -1 || Sequence.anims[i1].anInt359 >= Sequence.anims[npc.anim].anInt359)
                {
                    npc.anim = i1;
                    npc.anInt1527 = 0;
                    npc.anInt1528 = 0;
                    npc.anInt1529 = i2;
                    npc.anInt1530 = 0;
                    npc.positionBasedInt = npc.smallXYIndex;
                }
            }
            if ((l & 8) != 0)
            {
                int j1 = stream.method426();
                int j2 = stream.method427();
                npc.updateHitData(j2, j1, loopCycle);
                npc.loopCycleStatus = loopCycle + 300;
                npc.currentHealth = stream.method426();
                npc.maxHealth = stream.readUByte();
            }
            if ((l & 0x80) != 0)
            {
                npc.gfxId = stream.readUShort();
                int k1 = stream.readInt();
                npc.anInt1524 = k1 >> 16;
                npc.anInt1523 = loopCycle + (k1 & 0xffff);
                npc.currentAnim = 0;
                npc.anInt1522 = 0;
                if (npc.anInt1523 > loopCycle)
                    npc.currentAnim = -1;
                if (npc.gfxId == 65535)
                    npc.gfxId = -1;
            }
            if ((l & 0x20) != 0)
            {
                npc.interactingEntity = stream.readUShort();
                if (npc.interactingEntity == 65535)
                    npc.interactingEntity = -1;
            }
            if ((l & 1) != 0)
            {
                npc.textSpoken = stream.readString();
                npc.textCycle = 100;
            }
            if ((l & 0x40) != 0)
            {
                int l1 = stream.method427();
                int k2 = stream.method428();
                npc.updateHitData(k2, l1, loopCycle);
                npc.loopCycleStatus = loopCycle + 300;
                npc.currentHealth = stream.method428();
                npc.maxHealth = stream.method427();
            }
            if ((l & 2) != 0)
            {
                npc.desc = NpcDefintion.forID(stream.method436());
                npc.anInt1540 = npc.desc.size;
                npc.anInt1504 = npc.desc.getDegreesToTurn;
                npc.anInt1554 = npc.desc.walkAnimation;
                npc.anInt1555 = npc.desc.turn180Animation;
                npc.anInt1556 = npc.desc.turn90LeftAnimation;
                npc.anInt1557 = npc.desc.turn90RightAnimation;
                npc.anInt1511 = npc.desc.standAnimation;
            }
            if ((l & 4) != 0)
            {
                npc.anInt1538 = stream.method434();
                npc.anInt1539 = stream.method434();
            }
        }
    }

    public void buildAtNPCMenu(NpcDefintion entityDef, int i, int j, int k)
    {
        if (menuActionRow >= 400)
            return;
        if (entityDef.childrenIDs != null)
            entityDef = entityDef.method161();
        if (entityDef == null)
            return;
        if (!entityDef.aBoolean84)
            return;
        String s = entityDef.name;
        if (entityDef.combatLevel != 0)
            s = s + combatDiffColor(myPlayer.combatLevel, entityDef.combatLevel) + " (level-" + entityDef.combatLevel + ")";
        if (itemSelected == 1)
        {
            for (int i11 = 0; i11 < menuActionRow; i11++)
                if (menuActionID[i11] == 516)
                {
                    menuActionName[i11] = "Use @lre@" + selectedItemName + " @whi@-> @yel@" + s;
                    menuActionID[i11] = 582;
                    menuActionCmd1[i11] = i;
                    menuActionCmd2[i11] = k;
                    menuActionCmd3[i11] = j;
                    menuActionRow = i11;
                }
            menuActionName[menuActionRow] = "Use @lre@" + selectedItemName + " @whi@-> @yel@" + s;
            menuActionID[menuActionRow] = 582;
            menuActionCmd1[menuActionRow] = i;
            menuActionCmd2[menuActionRow] = k;
            menuActionCmd3[menuActionRow] = j;
            menuActionRow++;
            return;
        }
        if (spellSelected == 1)
        {
            if ((spellUsableOn & 2) == 2)
            {
                for (int i11 = 0; i11 < menuActionRow; i11++)
                    if (menuActionID[i11] == 516)
                    {
                        menuActionName[i11] = spellTooltip + " @yel@" + s;
                        menuActionID[i11] = 413;
                        menuActionCmd1[i11] = i;
                        menuActionCmd2[i11] = k;
                        menuActionCmd3[i11] = j;
                        menuActionRow = i11;
                    }
                menuActionName[menuActionRow] = spellTooltip + " @yel@" + s;
                menuActionID[menuActionRow] = 413;
                menuActionCmd1[menuActionRow] = i;
                menuActionCmd2[menuActionRow] = k;
                menuActionCmd3[menuActionRow] = j;
                menuActionRow++;
            }
        }
        else
        {
            if (entityDef.actions != null)
            {
                for (int l = 4; l >= 0; l--)
                    if (entityDef.actions[l] != null && !entityDef.actions[l].equalsIgnoreCase("attack"))
                    {
                        menuActionName[menuActionRow] = entityDef.actions[l] + " @yel@" + s;
                        if (l == 0)
                            menuActionID[menuActionRow] = 20;
                        if (l == 1)
                            menuActionID[menuActionRow] = 412;
                        if (l == 2)
                            menuActionID[menuActionRow] = 225;
                        if (l == 3)
                            menuActionID[menuActionRow] = 965;
                        if (l == 4)
                            menuActionID[menuActionRow] = 478;
                        menuActionCmd1[menuActionRow] = i;
                        menuActionCmd2[menuActionRow] = k;
                        menuActionCmd3[menuActionRow] = j;
                        menuActionRow++;
                    }
            }
            if (entityDef.actions != null)
            {
                for (int i1 = 4; i1 >= 0; i1--)
                    if (entityDef.actions[i1] != null && entityDef.actions[i1].equalsIgnoreCase("attack"))
                    {
                        char c = '\0';
                        if (entityDef.combatLevel > myPlayer.combatLevel)
                            c = '\u07D0';
                        menuActionName[menuActionRow] = entityDef.actions[i1] + " @yel@" + s;
                        if (i1 == 0)
                            menuActionID[menuActionRow] = 20 + c;
                        if (i1 == 1)
                            menuActionID[menuActionRow] = 412 + c;
                        if (i1 == 2)
                            menuActionID[menuActionRow] = 225 + c;
                        if (i1 == 3)
                            menuActionID[menuActionRow] = 965 + c;
                        if (i1 == 4)
                            menuActionID[menuActionRow] = 478 + c;
                        menuActionCmd1[menuActionRow] = i;
                        menuActionCmd2[menuActionRow] = k;
                        menuActionCmd3[menuActionRow] = j;
                        menuActionRow++;
                    }
            }
            menuActionName[menuActionRow] = "Examine @yel@" + s;
            menuActionID[menuActionRow] = 1025;
            menuActionCmd1[menuActionRow] = i;
            menuActionCmd2[menuActionRow] = k;
            menuActionCmd3[menuActionRow] = j;
            menuActionRow++;
        }
    }

    public void buildAtPlayerMenu(int i, int j, Player player, int k)
    {
        if (player == myPlayer)
            return;
        if (menuActionRow >= 400)
            return;
        String s;
        if (player.skill == 0)
            s = player.name + combatDiffColor(myPlayer.combatLevel, player.combatLevel) + " (level-" + player.combatLevel + ")";
        else
            s = player.name + " (skill-" + player.skill + ")";
        if (itemSelected == 1)
        {
            for (int i1 = 0; i1 < menuActionRow; i1++)
                if (menuActionID[i1] == 516)
                {
                    menuActionName[i1] = "Use @lre@" + selectedItemName + " @whi@-> @whi@" + s;
                    menuActionID[i1] = 491;
                    menuActionCmd1[i1] = j;
                    menuActionCmd2[i1] = i;
                    menuActionCmd3[i1] = k;
                    menuActionRow = i1;
                }
            menuActionName[menuActionRow] = "Use @lre@" + selectedItemName + " @whi@-> @whi@" + s;
            menuActionID[menuActionRow] = 491;
            menuActionCmd1[menuActionRow] = j;
            menuActionCmd2[menuActionRow] = i;
            menuActionCmd3[menuActionRow] = k;
            menuActionRow++;
        }
        else if (spellSelected == 1)
        {
            if ((spellUsableOn & 8) == 8)
            {
                for (int i1 = 0; i1 < menuActionRow; i1++)
                    if (menuActionID[i1] == 516)
                    {
                        menuActionName[i1] = spellTooltip + " @whi@" + s;
                        menuActionID[i1] = 365;
                        menuActionCmd1[i1] = j;
                        menuActionCmd2[i1] = i;
                        menuActionCmd3[i1] = k;
                        menuActionRow = i1;
                    }
                menuActionName[menuActionRow] = spellTooltip + " @whi@" + s;
                menuActionID[menuActionRow] = 365;
                menuActionCmd1[menuActionRow] = j;
                menuActionCmd2[menuActionRow] = i;
                menuActionCmd3[menuActionRow] = k;
                menuActionRow++;
            }
        }
        else
        {
            for (int l = 4; l >= 0; l--)
                if (atPlayerActions[l] != null)
                {
                    menuActionName[menuActionRow] = atPlayerActions[l] + " @whi@" + s;
                    char c = '\0';
                    if (atPlayerActions[l].equalsIgnoreCase("attack"))
                    {
                        if (player.combatLevel > myPlayer.combatLevel)
                            c = '\u07D0';
                        if (myPlayer.team != 0 && player.team != 0)
                            if (myPlayer.team == player.team)
                                c = '\u07D0';
                            else
                                c = '\0';
                    }
                    else if (atPlayerArray[l])
                        c = '\u07D0';
                    if (l == 0)
                        menuActionID[menuActionRow] = 561 + c;
                    if (l == 1)
                        menuActionID[menuActionRow] = 779 + c;
                    if (l == 2)
                        menuActionID[menuActionRow] = 27 + c;
                    if (l == 3)
                        menuActionID[menuActionRow] = 577 + c;
                    if (l == 4)
                        menuActionID[menuActionRow] = 729 + c;
                    menuActionCmd1[menuActionRow] = j;
                    menuActionCmd2[menuActionRow] = i;
                    menuActionCmd3[menuActionRow] = k;
                    menuActionRow++;
                }
        }
        for (int i1 = 0; i1 < menuActionRow; i1++)
            if (menuActionID[i1] == 516)
            {
                menuActionName[i1] = "Walk here @whi@" + s;
                return;
            }
    }

    public void method89(SpawnObjectNode class30_sub1)
    {
        int i = 0;
        int j = -1;
        int k = 0;
        int l = 0;
        if (class30_sub1.anInt1296 == 0)
            i = sceneGraph.getWallObjectUID(class30_sub1.anInt1295, class30_sub1.anInt1297, class30_sub1.anInt1298);
        if (class30_sub1.anInt1296 == 1)
            i = sceneGraph.getWallDecorationUID(class30_sub1.anInt1295, class30_sub1.anInt1297, class30_sub1.anInt1298);
        if (class30_sub1.anInt1296 == 2)
            i = sceneGraph.getInteractiveObjectUID(class30_sub1.anInt1295, class30_sub1.anInt1297, class30_sub1.anInt1298);
        if (class30_sub1.anInt1296 == 3)
            i = sceneGraph.getGroundDecortionUID(class30_sub1.anInt1295, class30_sub1.anInt1297, class30_sub1.anInt1298);
        if (i != 0)
        {
            int i1 = sceneGraph.getTileArrayIdForPosition(class30_sub1.anInt1295, class30_sub1.anInt1297, class30_sub1.anInt1298, i);
            j = i >> 14 & 0x7fff;
            k = i1 & 0x1f;
            l = i1 >> 6;
        }
        class30_sub1.anInt1299 = j;
        class30_sub1.anInt1301 = k;
        class30_sub1.anInt1300 = l;
    }

    public final void handleMusicEvents()
    {
        if (!music_enabled)
            return;
        for (int index = 0; index < currentSound; index++)
        {
            if (soundDelay[index] <= 0)
            {
                boolean flag1 = false;
                try
                {
                    RSBuffer stream = Sound.getData(soundType[index], sound[index]);
                    new SoundPlayer((InputStream) new ByteArrayInputStream(stream.buffer, 0, stream.pointer), soundVolume[index], soundDelay[index]);
                    if (System.currentTimeMillis() + (long) (stream.pointer / 22) > aLong1172 + (long) (anInt1257 / 22))
                    {
                        anInt1257 = stream.pointer;
                        aLong1172 = System.currentTimeMillis();
                    }
                }
                catch (Exception exception)
                {
                    exception.printStackTrace();
                }
                if (!flag1 || soundDelay[index] == -5)
                {
                    currentSound--;
                    for (int j = index; j < currentSound; j++)
                    {
                        sound[j] = sound[j + 1];
                        soundType[j] = soundType[j + 1];
                        soundDelay[j] = soundDelay[j + 1];
                        soundVolume[j] = soundVolume[j + 1];
                    }
                    index--;
                }
                else
                {
                    soundDelay[index] = -5;
                }
            }
            else
            {
                soundDelay[index]--;
            }
        }
        if (previousSong > 0)
        {
            previousSong -= 20;
            if (previousSong < 0)
                previousSong = 0;
            if (previousSong == 0 && music_enabled)
            {
                nextSong = currentSong;
                resourceProvider.method558(2, nextSong);
            }
        }
    }

    private void loadExtraConfig()
    {
        // Set fixed monitor image enabled
        anIntArray1045[970] = 1;
        variousSettings[970] = 1;

        if (super.screenManager.desired_mode != null)
        {

            // Set fullscreen resolution text to interface
            RSInterface klol = RSInterface.interfaceCache[22838];
            klol.disabledMessage = super.screenManager.mode_to_string(super.screenManager.current_screen_pos);

            RSInterface klol2 = RSInterface.interfaceCache[22854];
            klol2.scrollMax = (short) (1 + (12 * super.screenManager.avaible_modes.length));

            for (int kkk = 22855; kkk < 22855 + super.screenManager.avaible_modes.length; kkk++)
            {
                RSInterface rsi = RSInterface.interfaceCache[kkk];
                rsi.disabledMessage = super.screenManager.mode_to_string((byte) (kkk - 22855));
            }
        }
    }

    // TODO: Remove these when not needed.
    public void maps()
    {
        for (int MapIndex = 0; MapIndex < 2000; MapIndex++)
        {
            byte[] abyte0 = GetMap(MapIndex);
            if (abyte0 != null && abyte0.length > 0)
            {
                decompressors[4].method234(abyte0.length, abyte0, MapIndex);
            }
        }
    }

    public byte[] GetMap(int Index)
    {
        try
        {
            File Map = new File(Signlink.cacheLocation() + "./maps/" + Index + ".gz");
            byte[] aByte = new byte[(int) Map.length()];
            FileInputStream Fis = new FileInputStream(Map);
            Fis.read(aByte);
            logger.info("" + Index + " aByte = [" + aByte + "]!");
            Fis.close();
            return aByte;
        }
        catch (Exception e)
        {
            return null;
        }
    }

    public static final byte[] ReadFile(String s, boolean antiLeech)
    {
        try
        {
            byte abyte0[];
            File file = new File(s);
            int i = (int) file.length();
            abyte0 = new byte[i];
            DataInputStream datainputstream = new DataInputStream(new BufferedInputStream(new FileInputStream(s)));
            datainputstream.readFully(abyte0, 0, i);
            datainputstream.close();
            return abyte0;
        }
        catch (Exception e)
        {
            logger.info((new StringBuilder()).append("Read Error: ").append(s).toString());
            return null;
        }
    }

    public void connectServer()
    {
        int j = 5;
        expectedCRCs[8] = 0;
        int k = 0;
        while (expectedCRCs[8] == 0)
        {
            String s = "Unknown problem";
            drawLoadingText(5, "Connecting to file-server");
            try
            {
                DataInputStream datainputstream = openJagGrabInputStream("crc" + (int) (Math.random() * 99999999D) + "-" + 317);
                RSBuffer class30_sub2_sub2 = new RSBuffer(new byte[40]);
                datainputstream.readFully(class30_sub2_sub2.buffer, 0, 40);
                datainputstream.close();
                for (int i1 = 0; i1 < 9; i1++)
                    expectedCRCs[i1] = class30_sub2_sub2.readInt();
                int j1 = class30_sub2_sub2.readInt();
                int k1 = 1234;
                for (int l1 = 0; l1 < 9; l1++)
                    k1 = (k1 << 1) + expectedCRCs[l1];
                if (j1 != k1)
                {
                    s = "checksum problem";
                    expectedCRCs[8] = 0;
                }
            }
            catch (EOFException _ex)
            {
                s = "EOF problem";
                expectedCRCs[8] = 0;
            }
            catch (IOException _ex)
            {
                s = "Connection problem";
                expectedCRCs[8] = 0;
            }
            catch (Exception _ex)
            {
                s = "Logic problem";
                expectedCRCs[8] = 0;
            }
            if (expectedCRCs[8] == 0)
            {
                k++;
                for (int l = j; l > 0; l--)
                {
                    if (k >= 10)
                    {
                        drawLoadingText(5, "Game updated - please reload page");
                        l = 10;
                    }
                    else
                    {
                        drawLoadingText(5, s + " - will retry in " + l + " secs.");
                    }
                    try
                    {
                        Thread.sleep(1000L);
                    }
                    catch (Exception _ex)
                    {
                    }
                }
                j *= 2;
                if (j > 60)
                    j = 60;
            }
        }
    }

    void startUp()
    {
        // String s = getDocumentBaseHost();
        loginBkg = new DirectImage("bkg", 2000, 1500); //At top to avoid null pointer exception.
        if (Signlink.cache_dat != null)
        {
            for (int i = 0; i < 5; i++)
                decompressors[i] = new Decompressor(Signlink.cache_dat, Signlink.cache_idx[i], i + 1);
        }
        try
        {
            new CacheDownloader(this).downloadCache();
            // connectServer();
            /*
             * BufferedReader bufferedReader = null; try { bufferedReader = new
             * BufferedReader(new FileReader(signlink.cacheLocation() +
             * "./map_index.txt")); } catch (FileNotFoundException e1) {
             * e1.printStackTrace(); } String line = null; int[] reg = new
             * int[1500]; int[] flo = new int[1500]; int[] obj = new int[1500];
             * byte[] mem = new byte[1500]; int index = 0; try { while ((line =
             * bufferedReader.readLine()) != null) { reg[index] =
             * Integer.parseInt(line.substring(line.indexOf("[REG]") + 5, line
             * .indexOf("[FLO]"))); flo[index] =
             * Integer.parseInt(line.substring(line.indexOf("[FLO]") + 5, line
             * .indexOf("[OBJ]"))); obj[index] =
             * Integer.parseInt(line.substring(line.indexOf("[OBJ]") + 5, line
             * .indexOf("[MEM]"))); mem[index] = (byte)
             * Integer.parseInt(line.substring(line.indexOf("[MEM]") + 5));
             * index++; } DataOutputStream out = new DataOutputStream(new
             * BufferedOutputStream(new FileOutputStream(
             * signlink.cacheLocation() + "map_index.dat"))); for (int kk = 0;
             * kk < index; kk++) { out.writeShort(reg[kk]);
             * out.writeShort(flo[kk]); out.writeShort(obj[kk]);
             * out.writeByte(mem[kk]); } out.close(); } catch (Exception e) {
             * e.printStackTrace(); }
             */
            drawLoadingText(10, "Requesting title screen");
            titleStreamLoader = streamLoaderForName(1, "title screen", "title", expectedCRCs[1], 25);
            smallText = new TextDrawingArea(false, "p11_full", titleStreamLoader);
            regularText = new TextDrawingArea(false, "p12_full", titleStreamLoader);
            chatText = new TextDrawingArea(false, "b12_full", titleStreamLoader);
            TextDrawingArea fancyText = new TextDrawingArea(true, "q8_full", titleStreamLoader);
            newSmallFont = new RSFont(false, "p11_full", titleStreamLoader);
            newRegularFont = new RSFont(false, "p12_full", titleStreamLoader);
            newBoldFont = new RSFont(false, "b12_full", titleStreamLoader);
            drawLoadingText(20, "Unpacking fonts");
            newBoldFont.unpackChatImages(modIcons);
            newRegularFont.unpackChatImages(modIcons);
            newSmallFont.unpackChatImages(modIcons);
            drawLoadingText(35, "Unpacking archives");
            CacheArchive streamLoader = streamLoaderForName(2, "config", "config", expectedCRCs[2], 30);
            CacheArchive streamLoader_1 = streamLoaderForName(3, "interface", "interface", expectedCRCs[3], 35);
            CacheArchive streamLoader_2 = streamLoaderForName(4, "2d graphics", "media", expectedCRCs[4], 40);
            CacheArchive streamLoader_3 = streamLoaderForName(6, "textures", "textures", expectedCRCs[6], 45);
            CacheArchive streamLoader_5 = streamLoaderForName(8, "sound effects", "sounds", expectedCRCs[8], 55);
            byteGroundArray = new byte[4][104][104];
            intGroundArray = new int[4][105][105];
            sceneGraph = new SceneGraph(4, 104, 104, intGroundArray, null);
            for (int j = 0; j < 4; j++)
                collision_maps[j] = new CollisionMap();
            miniMap = new DirectImage(512, 512);
            CacheArchive streamLoader_6 = streamLoaderForName(5, "update list", "versionlist", expectedCRCs[5], 60);
            resourceProvider = new ResourceProvider();
            resourceProvider.start(streamLoader_6, this);
            // call this to update map crc's
            // onDemandFetcher.writeChecksumList(3);
            try
            {
                readSettings();
            }
            catch (IOException e)
            {
            }
            Model.method459(resourceProvider);
            
            // maps();
            // onDemandFetcher.writeChecksumList(0);
            drawLoadingText(50, "Unpacking images");

            for (int j3 = 0; j3 < 5; j3++)
                chatStones[j3] = new IndexedImage(streamLoader_2, "chatstones", j3);
            for (int j3 = 0; j3 < 14; j3++)
                sideIcons[j3] = new IndexedImage(streamLoader_2, "sideicons", j3);
            for (int r1 = 0; r1 < 2; r1++)
                redStones[r1] = new IndexedImage(streamLoader_2, "redstones", r1);
            redStones[2] = new IndexedImage(streamLoader_2, "redstones", 0);
            redStones[2].flipHorizontal();
            redStones[3] = new IndexedImage(streamLoader_2, "redstones", 0);
            redStones[3].flipVertical();
            redStones[4] = new IndexedImage(streamLoader_2, "redstones", 0);
            redStones[4].flipVertical();
            redStones[4].flipHorizontal();
            for (int k7 = 0; k7 < 4; k7++)
                mapArea[k7] = new DirectImage(streamLoader_2, "maparea", k7);
            loginMenuOverlay = new IndexedImage(titleStreamLoader, "menu", 0);
            for (int k7 = 0; k7 < 2; k7++)
                tabArea[k7] = new DirectImage(streamLoader_2, "tabarea", k7);
            tabArea[2] = new DirectImage(streamLoader_2, "tabarea", 3);
            tabArea[3] = new DirectImage(streamLoader_2, "tabarea", 4);
            tabArea_fs = new DirectImage(streamLoader_2, "tabarea", 2);
            chatArea[0] = new DirectImage(streamLoader_2, "chatarea", 0);
            chatArea[1] = new DirectImage(streamLoader_2, "chatarea", 1);
            chatArea[2] = new DirectImage(streamLoader_2, "chatarea", 3);
            chatFrame_fs = new DirectImage(streamLoader_2, "chatarea", 2);
            mapBack = new IndexedImage(streamLoader_2, "mapback", 0);
            compass = new DirectImage(streamLoader_2, "compass", 0);
            infinitySymbol = new IndexedImage(streamLoader_2, "miscgraphics2", 14);
            multiWay = new IndexedImage(streamLoader_2, "Overlay_multiway", 0);
            mapEdge = new DirectImage(streamLoader_2, "mapedge", 0);
            mapEdge.method345();
            for (int k3 = 0; k3 < 80; k3++)
                mapScenes[k3] = new IndexedImage(streamLoader_2, "mapscene", k3);
            for (int l3 = 0; l3 < 76; l3++)
                mapFunctions[l3] = new IndexedImage(streamLoader_2, "mapfunction", l3);
            for (int i4 = 0; i4 < 5; i4++)
                hitMarks[i4] = new IndexedImage(streamLoader_2, "hitmarks", i4);
            for (int h1 = 0; h1 < 2; h1++)
                headIconsHint[h1] = new IndexedImage(streamLoader_2, "headicons_hint", h1);
            for (int j4 = 0; j4 < 7; j4++)
                headIcons[j4] = new IndexedImage(streamLoader_2, "headicons_prayer", j4);
            for (int j45 = 0; j45 < 2; j45++)
                skullIcons[j45] = new IndexedImage(streamLoader_2, "headicons_pk", j45);
            mapFlag = new IndexedImage(streamLoader_2, "mapmarker", 0);
            mapMarker = new IndexedImage(streamLoader_2, "mapmarker", 1);
            // for (int kk = 0; kk < 13; kk++)
            // smileys[kk] = new IndexedImage(streamLoader_2, "smiley", kk);
            for (int k4 = 0; k4 < 8; k4++)
                crosses[k4] = new IndexedImage(streamLoader_2, "cross", k4);
            mapDotItem = new IndexedImage(streamLoader_2, "mapdots", 0);
            mapDotNPC = new IndexedImage(streamLoader_2, "mapdots", 1);
            mapDotPlayer = new IndexedImage(streamLoader_2, "mapdots", 2);
            mapDotFriend = new IndexedImage(streamLoader_2, "mapdots", 3);
            mapDotTeam = new IndexedImage(streamLoader_2, "mapdots", 4);
            for (int l4 = 0; l4 < 4; l4++)
                scrollBar[l4] = new IndexedImage(streamLoader_2, "scrollbar", l4);
            for (int l4 = 0; l4 < 3; l4++)
                modIcons[l4] = new DirectImage(streamLoader_2, "mod_icons", l4);
            IndexedImage sprite = new IndexedImage(streamLoader_2, "screenframe", 0);
            leftFrame = new RSImageProducer(sprite.myWidth, sprite.myHeight, getGameComponent());
            sprite.drawIndexedImage(0, 0);
            sprite = new IndexedImage(streamLoader_2, "screenframe", 1);
            topFrame = new RSImageProducer(sprite.myWidth, sprite.myHeight, getGameComponent());
            /**
             * Chat Area
             */
            chatBox = new DirectImage("chatbox", 505, 130);
            scrollFill = new DirectImage("48", 16, 3);
            scrollTop = new DirectImage("49", 16, 5);
            scrollMiddle = new DirectImage("50", 16, 5);
            scrollBottom = new DirectImage("51", 16, 5);
            scrollUp = new DirectImage("46", 16, 16);
            scrollDown = new DirectImage("47", 16, 16);
            /**
             * Login Sprites
             */            
            loginBox = new DirectImage("input", 268, 31);
            loginBoxHover = new DirectImage("input_hover", 268, 31);
            loginButton = new DirectImage("login", 266, 37);
            loginButtonHover = new DirectImage("login_hover", 266, 37);
            arrow = new DirectImage("nigga", 32, 32);
            myBox = new DirectImage("box", 450, 175);
            for (int index = 0; index < 25; index++) {
    			bubbles.add(new Bubble());
    		}
            
            sprite.drawIndexedImage(0, 0);
            drawLoadingText(60, "Unpacking textures");
            Rasterizer.unpack(streamLoader_3);
            Rasterizer.method372(0.80000000000000004D);
            Rasterizer.method367();
            drawLoadingText(75, "Unpacking config");
            Sequence.unpackConfig(streamLoader);
            ObjectDefinition.unpackConfig(streamLoader);
            Floor.unpackConfig(streamLoader);
            ItemDefinition.unpackConfig(streamLoader);
            NpcDefintion.unpackConfig(streamLoader);
            IdentityKit.unpackConfig(streamLoader);
            Graphic.unpackConfig(streamLoader);
            loadEquipmentBonuses(streamLoader);
            Varp.unpackConfig(streamLoader);
            VarBit.unpackConfig(streamLoader);
            ItemDefinition.isMembers = isMembers;
            if (music_enabled)
            {
                drawLoadingText(80, "Unpacking sounds");
                byte abyte0[] = streamLoader_5.getDataForName("sounds.dat");
                RSBuffer stream = new RSBuffer(abyte0);
                Sound.unpack(stream);
            }
            if (lowMemory)
                setLowMem();
            drawLoadingText(85, "Unpacking interfaces");
            TextDrawingArea textDrawingArea[] =
            { smallText, regularText, chatText, fancyText };
            RSInterface.unpack(streamLoader_1, textDrawingArea, streamLoader_2);
            int[] IDs = { 17002, 17004, 17006, 17008, 17010, 17012, 17014, 17018,
        		   	17020, 17022, 17024, 17026, 17028, 17030, 17032, 17034,
        		   	17036, 17038, 17040, 17042, 17044, 17046, 17048, 17050,
        		   	17052, 17054, 17056, 17058, 17060};
            drawLoadingText(100, "Preparing game engine");
            for (int j6 = 0; j6 < 33; j6++)
            {
                int k6 = 999;
                int i7 = 0;
                for (int k7 = 0; k7 < 34; k7++)
                {
                    if (mapBack.imgPixels[k7 + j6 * mapBack.myWidth] == 0)
                    {
                        if (k6 == 999)
                            k6 = k7;
                        continue;
                    }
                    if (k6 == 999)
                        continue;
                    i7 = k7;
                    break;
                }
                anIntArray968[j6] = k6;
                anIntArray1057[j6] = i7 - k6;
            }
            /**
             * Map area editing
             */
            for (int l6 = 5; l6 < 156; l6++)
            {
                int j7 = 999;
                int l7 = 0;
                for (int j8 = 25; j8 < 172; j8++)
                {
                    if (mapBack.imgPixels[j8 + l6 * mapBack.myWidth] == 0 && (j8 > 34 || l6 > 34))
                    {
                        if (j7 == 999)
                        {
                            j7 = j8;
                        }
                        continue;
                    }
                    if (j7 == 999)
                    {
                        continue;
                    }
                    l7 = j8;
                    break;
                }
                anIntArray1052[l6 - 5] = j7 - 25;
                anIntArray1229[l6 - 5] = l7 - j7;
            }
            Rasterizer.method365(765, 503);
            fullScreenTextureArray = Rasterizer.lineOffsets;
            Rasterizer.method365(516, 165);
            chatAreaTexture = Rasterizer.lineOffsets;
            Rasterizer.method365(249, 335);
            tabAreaTexture = Rasterizer.lineOffsets;
            Rasterizer.method365(512, 334);
            mainGameScreenTexture = Rasterizer.lineOffsets;
            int ai[] = new int[9];
            for (int i = 0; i < 9; i++)
            {
                int pitch = 128 + i * 32 + 15;
                int l8 = 600 + pitch * 3;
                int i9 = Rasterizer.sineTable[pitch];
                ai[i] = l8 * i9 >> 16;
            }
            loadExtraConfig();
            SceneGraph.initViewport(500, 800, 512, 334, ai);
            startThemeMusic();
            drawLoginScreen(false);
            GameObject.clientInstance = this;
            ObjectDefinition.clientInstance = this;
            NpcDefintion.clientInstance = this;
            return;
        }
        catch (Exception exception)
        {
            exception.printStackTrace();
            System.err.println("loaderror " + aString1049 + " " + anInt1079);
        }
        loadingError = true;
    }

    public void method91(RSBuffer stream, int i)
    {
        while (stream.bitPosition + 10 < i * 8)
        {
            int j = stream.readBits(11);
            if (j == 2047)
                break;
            if (playerArray[j] == null)
            {
                playerArray[j] = new Player();
                if (aStreamArray895s[j] != null)
                    playerArray[j].updatePlayer(aStreamArray895s[j]);
            }
            playerIndices[playerCount++] = j;
            Player player = playerArray[j];
            player.anInt1537 = loopCycle;
            int k = stream.readBits(1);
            if (k == 1)
                anIntArray894[anInt893++] = j;
            int l = stream.readBits(1);
            int i1 = stream.readBits(5);
            if (i1 > 15)
                i1 -= 32;
            int j1 = stream.readBits(5);
            if (j1 > 15)
                j1 -= 32;
            player.setPos(myPlayer.smallX[0] + j1, myPlayer.smallY[0] + i1, l == 1);
        }
        stream.finishBitAccess();
    }

    public final String methodR(int j)
    {
        String s2 = "" + (j / 100000);
        NumberFormat nf1 = new DecimalFormat("." + s2.substring(1));
        String s6 = nf1.format((j / 1000000));
        if (j >= 0 && j < 10000)
            return String.valueOf(j);
        if (j >= 10000 && j < 10000000)
            return j / 1000 + "K";
        if (j >= 10000000 && j < 10000000)
            return s6 + "M";
        if (j >= 10000000 && j < Integer.MAX_VALUE)
            return j / 1000000 + "M";
        else
            return "*";
    }

    private String intToKOrMilLongName(int i)
    {
        String s = String.valueOf(i);
        for (int k = s.length() - 3; k > 0; k -= 3)
            s = s.substring(0, k) + "," + s.substring(k);
        if (s.length() > 8)
            s = "@gre@" + s.substring(0, s.length() - 8) + "M @whi@(" + s + ")";
        else if (s.length() > 4)
            s = "@cya@" + s.substring(0, s.length() - 4) + "K @whi@(" + s + ")";
        return " " + s;
    }

    /**
     * TODO: Minimap walking etc..
     */
    public void processMinimapClick()
    {
        if (hideMinimap != 0)
            return;
        if (super.clickMode3 == 1)
        {
            int x = (clientSize == CLIENT_FIXED ? super.saveClickX - 25 - 545 : super.saveClickX - clientWidth + 183);
            int y = (clientSize == CLIENT_FIXED ? super.saveClickY - 9 : super.saveClickY - 10);
            if (x >= 0 && y >= 0 && x < 146 && y < 151)
            {
                x -= 73;
                y -= 75;
                int k = viewRotation + minimapRotation & 0x7ff;
                int i1 = Rasterizer.sineTable[k];
                int j1 = Rasterizer.cosineTable[k];
                i1 = i1 * (minimapZoom + 256) >> 8;
                j1 = j1 * (minimapZoom + 256) >> 8;
                int k1 = y * i1 + x * j1 >> 11;
                int l1 = y * j1 - x * i1 >> 11;
                int i2 = myPlayer.x + k1 >> 7;
                int j2 = myPlayer.y - l1 >> 7;
                boolean flag1 = doWalkTo(1, 0, 0, 0, myPlayer.smallY[0], 0, 0, j2, myPlayer.smallX[0], true, i2);
                if (flag1)
                {
                    stream.writeByte(x);
                    stream.writeByte(y);
                    stream.writeShort(viewRotation);
                    stream.writeByte(57);
                    stream.writeByte(minimapRotation);
                    stream.writeByte(minimapZoom);
                    stream.writeByte(89);
                    stream.writeShort(myPlayer.x);
                    stream.writeShort(myPlayer.y);
                    stream.writeByte(anInt1264);
                    stream.writeByte(63);
                }
            }
        }
    }

    private String interfaceIntToString(int j)
    {
        if (j < 0x3b9ac9ff)
            return String.valueOf(j);
        else
            return "*";
    }

    public void showErrorScreen()
    {
        Graphics g = getGameComponent().getGraphics();
        g.setColor(Color.black);
        g.fillRect(0, 0, 765, 503);
        method4(1);
        if (loadingError)
        {
            g.setFont(new Font("Helvetica", 1, 16));
            g.setColor(Color.yellow);
            int k = 35;
            g.drawString("Sorry, an error has occured whilst loading Exorth", 30, k);
            k += 50;
            g.setColor(Color.white);
            g.drawString("To fix this try the following (in order):", 30, k);
            k += 50;
            g.setColor(Color.white);
            g.setFont(new Font("Helvetica", 1, 12));
            g.drawString("1: Try closing ALL open web-browser windows, and reloading", 30, k);
            k += 30;
            g.drawString("2: Try clearing your web-browsers cache from your web-browsers settings", 30, k);
            k += 30;
            g.drawString("3: Try using a different game-world", 30, k);
            k += 30;
            g.drawString("4: Try rebooting your computer", 30, k);
            k += 30;
            g.drawString("5: Try deleting game cache from: " + Signlink.cacheLocation(), 30, k);
        }
        if (genericLoadingError)
        {
            g.setFont(new Font("Helvetica", 1, 20));
            g.setColor(Color.white);
            g.drawString("Error - unable to load game!", 50, 50);
            g.drawString("To play Exorth make sure you play from", 50, 100);
            g.drawString("http://www.insertourwebsitehere.com", 50, 150);// @akzu
        }
        if (rsAlreadyLoaded)
        {
            g.setColor(Color.yellow);
            int l = 35;
            g.drawString("Error a copy of Exorth already appears to be loaded", 30, l);
            l += 50;
            g.setColor(Color.white);
            g.drawString("To fix this try the following (in order):", 30, l);
            l += 50;
            g.setColor(Color.white);
            g.setFont(new Font("Helvetica", 1, 12));
            g.drawString("1: Try closing ALL open web-browser windows, and reloading", 30, l);
            l += 30;
            g.drawString("2: Try rebooting your computer, and reloading", 30, l);
            l += 30;
        }
    }

    public URL getCodeBase()
    {
        try
        {
            return new URL(server + ":" + (80 + portOff));
        }
        catch (Exception _ex)
        {
        }
        return null;
    }

    public void forceNpcUpdateBlock()
    {
        for (int j = 0; j < npcCount; j++)
        {
            int k = npcIndices[j];
            NPC npc = npcArray[k];
            if (npc != null)
                updateEntity(npc);
        }
    }

    public void updateEntity(Mobile entity)
    {
        if (entity.x < 128 || entity.y < 128 || entity.x >= 13184 || entity.y >= 13184)
        {
            entity.anim = -1;
            entity.gfxId = -1;
            entity.anInt1547 = 0;
            entity.anInt1548 = 0;
            entity.x = (short) (entity.smallX[0] * 128 + entity.anInt1540 * 64);
            entity.y = (short) (entity.smallY[0] * 128 + entity.anInt1540 * 64);
            entity.resetLocationIndex();
        }
        if (entity == myPlayer && (entity.x < 1536 || entity.y < 1536 || entity.x >= 11776 || entity.y >= 11776))
        {
            entity.anim = -1;
            entity.gfxId = -1;
            entity.anInt1547 = 0;
            entity.anInt1548 = 0;
            entity.x = (short) (entity.smallX[0] * 128 + entity.anInt1540 * 64);
            entity.y = (short) (entity.smallY[0] * 128 + entity.anInt1540 * 64);
            entity.resetLocationIndex();
        }
        if (entity.anInt1547 > loopCycle)
            method97(entity);
        else if (entity.anInt1548 >= loopCycle)
            method98(entity);
        else
            method99(entity);
        method100(entity);
        method101(entity);
    }

    public void method97(Mobile entity)
    {
        int i = entity.anInt1547 - loopCycle;
        int j = entity.anInt1543 * 128 + entity.anInt1540 * 64;
        int k = entity.anInt1545 * 128 + entity.anInt1540 * 64;
        entity.x += (j - entity.x) / i;
        entity.y += (k - entity.y) / i;
        entity.anInt1503 = 0;
        if (entity.anInt1549 == 0)
            entity.turnDirection = 1024;
        if (entity.anInt1549 == 1)
            entity.turnDirection = 1536;
        if (entity.anInt1549 == 2)
            entity.turnDirection = 0;
        if (entity.anInt1549 == 3)
            entity.turnDirection = 512;
    }

    public void method98(Mobile entity)
    {
        if (entity.anInt1548 == loopCycle || entity.anim == -1 || entity.anInt1529 != 0 || entity.anInt1528 + 1 > Sequence.anims[entity.anim].getFrameLength(entity.anInt1527))
        {
            int i = entity.anInt1548 - entity.anInt1547;
            int j = loopCycle - entity.anInt1547;
            int k = entity.anInt1543 * 128 + entity.anInt1540 * 64;
            int l = entity.anInt1545 * 128 + entity.anInt1540 * 64;
            int i1 = entity.anInt1544 * 128 + entity.anInt1540 * 64;
            int j1 = entity.anInt1546 * 128 + entity.anInt1540 * 64;
            entity.x = (short) ((k * (i - j) + i1 * j) / i);
            entity.y = (short) ((l * (i - j) + j1 * j) / i);
        }
        entity.anInt1503 = 0;
        if (entity.anInt1549 == 0)
            entity.turnDirection = 1024;
        if (entity.anInt1549 == 1)
            entity.turnDirection = 1536;
        if (entity.anInt1549 == 2)
            entity.turnDirection = 0;
        if (entity.anInt1549 == 3)
            entity.turnDirection = 512;
        entity.anInt1552 = entity.turnDirection;
    }

    public void method99(Mobile entity)
    {
        entity.anInt1517 = entity.anInt1511;
        if (entity.smallXYIndex == 0)
        {
            entity.anInt1503 = 0;
            return;
        }
        if (entity.anim != -1 && entity.anInt1529 == 0)
        {
            Sequence animation = Sequence.anims[entity.anim];
            if (entity.positionBasedInt > 0 && animation.anInt363 == 0)
            {
                entity.anInt1503++;
                return;
            }
            if (entity.positionBasedInt <= 0 && animation.walkable == 0)
            {
                entity.anInt1503++;
                return;
            }
        }
        int i = entity.x;
        int j = entity.y;
        int k = entity.smallX[entity.smallXYIndex - 1] * 128 + entity.anInt1540 * 64;
        int l = entity.smallY[entity.smallXYIndex - 1] * 128 + entity.anInt1540 * 64;
        if (k - i > 256 || k - i < -256 || l - j > 256 || l - j < -256)
        {
            entity.x = (short) k;
            entity.y = (short) l;
            return;
        }
        if (i < k)
        {
            if (j < l)
                entity.turnDirection = 1280;
            else if (j > l)
                entity.turnDirection = 1792;
            else
                entity.turnDirection = 1536;
        }
        else if (i > k)
        {
            if (j < l)
                entity.turnDirection = 768;
            else if (j > l)
                entity.turnDirection = 256;
            else
                entity.turnDirection = 512;
        }
        else if (j < l)
            entity.turnDirection = 1024;
        else
            entity.turnDirection = 0;
        int i1 = entity.turnDirection - entity.anInt1552 & 0x7ff;
        if (i1 > 1024)
            i1 -= 2048;
        int j1 = entity.anInt1555;
        if (i1 >= -256 && i1 <= 256)
            j1 = entity.anInt1554;
        else if (i1 >= 256 && i1 < 768)
            j1 = entity.anInt1557;
        else if (i1 >= -768 && i1 <= -256)
            j1 = entity.anInt1556;
        if (j1 == -1)
            j1 = entity.anInt1554;
        entity.anInt1517 = j1;
        int k1 = 4;
        if (entity.anInt1552 != entity.turnDirection && entity.interactingEntity == -1 && entity.anInt1504 != 0)
            k1 = 2;
        if (entity.smallXYIndex > 2)
            k1 = 6;
        if (entity.smallXYIndex > 3)
            k1 = 8;
        if (entity.anInt1503 > 0 && entity.smallXYIndex > 1)
        {
            k1 = 8;
            entity.anInt1503--;
        }
        if (entity.aBooleanArray1553[entity.smallXYIndex - 1])
            k1 <<= 1;
        if (k1 >= 8 && entity.anInt1517 == entity.anInt1554 && entity.anInt1505 != -1)
            entity.anInt1517 = entity.anInt1505;
        if (i < k)
        {
            entity.x += k1;
            if (entity.x > k)
                entity.x = (short) k;
        }
        else if (i > k)
        {
            entity.x -= k1;
            if (entity.x < k)
                entity.x = (short) k;
        }
        if (j < l)
        {
            entity.y += k1;
            if (entity.y > l)
                entity.y = (short) l;
        }
        else if (j > l)
        {
            entity.y -= k1;
            if (entity.y < l)
                entity.y = (short) l;
        }
        if (entity.x == k && entity.y == l)
        {
            entity.smallXYIndex--;
            if (entity.positionBasedInt > 0)
                entity.positionBasedInt--;
        }
    }

    public void method100(Mobile entity)
    {
        if (entity.anInt1504 == 0)
            return;
        if (entity.interactingEntity != -1 && entity.interactingEntity < 32768)
        {
            NPC npc = npcArray[entity.interactingEntity];
            if (npc != null)
            {
                int i1 = entity.x - npc.x;
                int k1 = entity.y - npc.y;
                if (i1 != 0 || k1 != 0)
                    entity.turnDirection = (int) (Math.atan2(i1, k1) * 325.94900000000001D) & 0x7ff;
            }
        }
        if (entity.interactingEntity >= 32768)
        {
            int j = entity.interactingEntity - 32768;
            if (j == playerID)
                j = myPlayerIndex;
            Player player = playerArray[j];
            if (player != null)
            {
                int l1 = entity.x - player.x;
                int i2 = entity.y - player.y;
                if (l1 != 0 || i2 != 0)
                    entity.turnDirection = (int) (Math.atan2(l1, i2) * 325.94900000000001D) & 0x7ff;
            }
        }
        if ((entity.anInt1538 != 0 || entity.anInt1539 != 0) && (entity.smallXYIndex == 0 || entity.anInt1503 > 0))
        {
            int k = entity.x - (entity.anInt1538 - baseX - baseX) * 64;
            int j1 = entity.y - (entity.anInt1539 - baseY - baseY) * 64;
            if (k != 0 || j1 != 0)
                entity.turnDirection = (int) (Math.atan2(k, j1) * 325.94900000000001D) & 0x7ff;
            entity.anInt1538 = 0;
            entity.anInt1539 = 0;
        }
        int l = entity.turnDirection - entity.anInt1552 & 0x7ff;
        if (l != 0)
        {
            if (l < entity.anInt1504 || l > 2048 - entity.anInt1504)
                entity.anInt1552 = entity.turnDirection;
            else if (l > 1024)
                entity.anInt1552 -= entity.anInt1504;
            else
                entity.anInt1552 += entity.anInt1504;
            entity.anInt1552 &= 0x7ff;
            if (entity.anInt1517 == entity.anInt1511 && entity.anInt1552 != entity.turnDirection)
            {
                if (entity.anInt1512 != -1)
                {
                    entity.anInt1517 = entity.anInt1512;
                    return;
                }
                entity.anInt1517 = entity.anInt1554;
            }
        }
    }

    public void method101(Mobile entity)
    {
        entity.aBoolean1541 = false;
        if (entity.anInt1517 != -1)
        {
            Sequence animation = Sequence.anims[entity.anInt1517];
            entity.anInt1519++;
            if (entity.anInt1518 < animation.anInt352 && entity.anInt1519 > animation.getFrameLength(entity.anInt1518))
            {
                entity.anInt1519 = 1;
                entity.anInt1518++;
            }
            if (entity.anInt1518 >= animation.anInt352)
            {
                entity.anInt1519 = 1;
                entity.anInt1518 = 0;
            }
        }
        if (entity.gfxId != -1 && loopCycle >= entity.anInt1523)
        {
            if (entity.currentAnim < 0)
                entity.currentAnim = 0;
            Sequence animation_1 = Graphic.cache[entity.gfxId].aAnimation_407;
            if (animation_1 == null)
                return;
            Animation.method531(animation_1.anIntArray353[entity.currentAnim]);
            for (entity.anInt1522++; entity.currentAnim < animation_1.anInt352 && entity.anInt1522 > animation_1.getFrameLength(entity.currentAnim); entity.currentAnim++)
                entity.anInt1522 -= animation_1.getFrameLength(entity.currentAnim);
            if (entity.currentAnim >= animation_1.anInt352 && (entity.currentAnim < 0 || entity.currentAnim >= animation_1.anInt352))
                entity.gfxId = -1;
        }
        if (entity.anim != -1 && entity.anInt1529 <= 1)
        {
            Sequence animation_2 = Sequence.anims[entity.anim];
            if (animation_2 == null)
                return;
            if (animation_2.anInt363 == 1 && entity.positionBasedInt > 0 && entity.anInt1547 <= loopCycle && entity.anInt1548 < loopCycle)
            {
                entity.anInt1529 = 1;
                return;
            }
        }
        if (entity.anim != -1 && entity.anInt1529 == 0)
        {
            Sequence animation_3 = Sequence.anims[entity.anim];
            for (entity.anInt1528++; entity.anInt1527 < animation_3.anInt352 && entity.anInt1528 > animation_3.getFrameLength(entity.anInt1527); entity.anInt1527++)
                entity.anInt1528 -= animation_3.getFrameLength(entity.anInt1527);
            if (entity.anInt1527 >= animation_3.anInt352)
            {
                entity.anInt1527 -= animation_3.anInt356;
                entity.anInt1530++;
                if (entity.anInt1530 >= animation_3.anInt362)
                    entity.anim = -1;
                if (entity.anInt1527 < 0 || entity.anInt1527 >= animation_3.anInt352)
                    entity.anim = -1;
            }
            entity.aBoolean1541 = animation_3.aBoolean358;
        }
        if (entity.anInt1529 > 0)
            entity.anInt1529--;
    }

    public void drawGameScreen()
    {

        if (fullscreenInterfaceID != -1 && (loadingStage == 2 || super.fullGameScreen != null))
        {
            if (loadingStage == 2)
            {
                method119(anInt945, fullscreenInterfaceID);
                if (openInterfaceID != -1)
                {
                    method119(anInt945, openInterfaceID);
                }
                anInt945 = 0;
                resetAllImageProducers();
                super.fullGameScreen.initDrawingArea();
                Rasterizer.lineOffsets = fullScreenTextureArray;
                RSRaster.setAllPixelsToZero();
                welcomeScreenRaised = true;
                if (openInterfaceID != -1)
                {
                    RSInterface rsInterface_1 = RSInterface.interfaceCache[openInterfaceID];
                    if (rsInterface_1.width == 512 && rsInterface_1.height == 334 && rsInterface_1.type == 0)
                    {
                        rsInterface_1.width = (short) (clientSize == CLIENT_FIXED ? 765 : clientWidth);
                        rsInterface_1.height = (short) (clientSize == CLIENT_FIXED ? 503 : clientHeight);
                    }
                    drawInterface(0, clientSize == CLIENT_FIXED ? 0 : (clientWidth / 2) - 765 / 2, rsInterface_1, clientSize == CLIENT_FIXED ? 8 : (clientHeight / 2) - 503 / 2, false);
                }
                RSInterface rsInterface = RSInterface.interfaceCache[fullscreenInterfaceID];
                if (rsInterface.width == 512 && rsInterface.height == 334 && rsInterface.type == 0)
                {
                    rsInterface.width = (short) (clientSize == CLIENT_FIXED ? 765 : clientWidth);
                    rsInterface.height = (short) (clientSize == CLIENT_FIXED ? 503 : clientHeight);
                }
                drawInterface(0, clientSize == CLIENT_FIXED ? 0 : (clientWidth / 2) - 765 / 2, rsInterface, clientSize == CLIENT_FIXED ? 8 : (clientHeight / 2) - 503 / 2, false);
                if (!menuOpen)
                {
                    processRightClick();
                    drawTooltip();
                }
                else
                {
                    drawMenu(0, 0);
                }
            }
            drawCount++;
            super.fullGameScreen.drawGraphics(0, super.graphics, 0);
            return;
        }
        else
        {
            if (drawCount != 0)
            {
                resetImageProducers2();
            }
        }
        if (welcomeScreenRaised)
        {
            welcomeScreenRaised = false;
            if (clientSize == CLIENT_FIXED)
            {
                topFrame.drawGraphics(0, super.graphics, 0);
                leftFrame.drawGraphics(4, super.graphics, 0);
            }
            needDrawTabArea = true;
            inputTaken = true;
            if (loadingStage != 2)
            {
                gameScreenImageProducer.drawGraphics((clientSize == CLIENT_FIXED ? 4 : 0), super.graphics, (clientSize == CLIENT_FIXED ? 4 : 0));
                if (clientSize == CLIENT_FIXED)
                    mapAreaImageProducer.drawGraphics(0, super.graphics, 516);
            }
        }
        if (menuOpen)
            drawMenu((clientSize == CLIENT_FIXED ? 4 : 0), (clientSize == CLIENT_FIXED ? 4 : 0));
        if (invOverlayInterfaceID != -1)
        {
            boolean flag1 = method119(anInt945, invOverlayInterfaceID);
            if (flag1)
                needDrawTabArea = true;
        }
        if (atInventoryInterfaceType == 2)
            needDrawTabArea = true;
        if (activeInterfaceType == 2)
            needDrawTabArea = true;
        if (needDrawTabArea)
        {
            // System.err.println("TAB OPEN");
            if (clientSize == CLIENT_FIXED)
                drawTabArea();
            needDrawTabArea = false;
        }
        if (loadingStage == 2)
        {
            renderGameView();
        }
        if (backDialogID == -1)
        {
            aClass9_1059.scrollPosition = (short) (chatScrollMax - chatScrollPos - 114);
            if (clientSize == CLIENT_FIXED)
            {
                if (super.mouseX >= 460 && super.mouseX <= 513 && super.mouseY > 332 && super.mouseY <= 459)
                    moveScroller(496 - 16, 114, super.mouseX - 16, super.mouseY - 345, aClass9_1059, 0, false, chatScrollMax);
            }
            else if (super.mouseX >= 460 && super.mouseX <= 513 && super.mouseY >= clientHeight - 158 && super.mouseY <= clientHeight - 44)
            {
                moveScroller(496 - 16, 114, super.mouseX - 16, super.mouseY, aClass9_1059, clientHeight - 158, false, chatScrollMax);
            }
            int i = chatScrollMax - 114 - aClass9_1059.scrollPosition;
            if (i < 0)
                i = 0;
            if (i > chatScrollMax - 114)
                i = chatScrollMax - 114;
            if (chatScrollPos != i)
            {
                chatScrollPos = i;
                inputTaken = true;
            }
        }
        if (backDialogID != -1)
        {
            boolean flag2 = method119(anInt945, backDialogID);
            if (flag2)
                inputTaken = true;
        }
        if (atInventoryInterfaceType == 3)
            inputTaken = true;
        if (activeInterfaceType == 3)
            inputTaken = true;
        if (aString844 != null)
            inputTaken = true;
        if (inputTaken)
        {
            // System.err.println("CHAT OPEN");
            if (clientSize == CLIENT_FIXED)
               drawChatArea();
            inputTaken = false;
        }
        if (loadingStage == 2)
        {
            // @akzumini map(); << uhm this is the place where minimap is drawn
            // or called
            if (clientSize == CLIENT_FIXED)
            {
                mapAreaImageProducer.initDrawingArea();
                drawMinimap();
                mapAreaImageProducer.drawGraphics(0, super.graphics, 516);
            }
        }
        if (anInt1054 != -1)
        {
            needDrawTabArea = true;
        }
        if (anInt1054 != -1 && anInt1054 == tabID)
        {
            anInt1054 = -1;
            stream.writeOpcode(120);
            stream.writeByte(tabID);
            gameScreenImageProducer.initDrawingArea();
        }
        anInt945 = 0;
    }

    private boolean buildFriendsListMenu(RSInterface class9)
    {
        int i = class9.contentType;
        if (i >= 1 && i <= 200 || i >= 701 && i <= 900)
        {
            if (i >= 801)
                i -= 701;
            else if (i >= 701)
                i -= 601;
            else if (i >= 101)
                i -= 101;
            else
                i--;
            menuActionName[menuActionRow] = "Remove @whi@" + capitalize(friendsList[i]);
            menuActionID[menuActionRow] = 792;
            menuActionRow++;
            menuActionName[menuActionRow] = "Message @whi@" + capitalize(friendsList[i]);
            menuActionID[menuActionRow] = 639;
            menuActionRow++;
            return true;
        }
        if (i >= 401 && i <= 500)
        {
            menuActionName[menuActionRow] = "Remove @whi@" + capitalize(class9.disabledMessage);
            menuActionID[menuActionRow] = 322;
            menuActionRow++;
            return true;
        }
        else
        {
            return false;
        }
    }

    public void method104()
    {
        StillGraphic graphic = (StillGraphic) stillGraphicDeque.reverseGetFirst();
        for (; graphic != null; graphic = (StillGraphic) stillGraphicDeque.reverseGetNext())
            if (graphic.anInt1560 != floor_level || graphic.aBoolean1567)
                graphic.unlink();
            else if (loopCycle >= graphic.anInt1564)
            {
                graphic.method454(anInt945);
                if (graphic.aBoolean1567)
                    graphic.unlink();
                else
                    sceneGraph.addRenderableA(graphic.anInt1560, 0, graphic.anInt1563, -1, graphic.anInt1562, 60, graphic.anInt1561, graphic, false);
            }
    }
    
    
    /*public void drawBlackBox(int xPos, int yPos) {
		RSDrawingArea.drawPixels(71, yPos - 1, xPos - 2, 0x726451, 1);
		RSDrawingArea.drawPixels(69, yPos, xPos + 174, 0x726451, 1);
		RSDrawingArea.drawPixels(1, yPos - 2, xPos - 2, 0x726451, 178);
		RSDrawingArea.drawPixels(1, yPos + 68, xPos, 0x726451, 174);
		RSDrawingArea.drawPixels(71, yPos - 1, xPos - 1, 0x2E2B23, 1);
		RSDrawingArea.drawPixels(71, yPos - 1, xPos + 175, 0x2E2B23, 1);
		RSDrawingArea.drawPixels(1, yPos - 1, xPos, 0x2E2B23, 175);
		RSDrawingArea.drawPixels(1, yPos + 69, xPos, 0x2E2B23, 175);
		RSDrawingArea.method335(0, yPos, 174, 68, 220, xPos);
	}*/
    
    
    public void drawInterface(int scrollPosition, int rsiX, RSInterface rsInterface, int rsiY, boolean chatInterface)
    {
        if (rsInterface.type != 0 || rsInterface.children == null)
            return;
        if (rsInterface.isMouseoverTriggered && anInt1026 != rsInterface.id && anInt1048 != rsInterface.id && anInt1039 != rsInterface.id)
            return;
        if (rsInterface.isMouseoverTriggered && menuOpen)
        {
            anInt1026 = -1;
            anInt1048 = -1;
            anInt1039 = -1;
            return;
        }
                
        int i1 = RSRaster.topX;
        int j1 = RSRaster.topY;
        int k1 = RSRaster.bottomX;
        int l1 = RSRaster.bottomY;
        
        if (!chatInterface)
            RSRaster.setDrawingArea(rsiY + rsInterface.height, rsiX, rsiX + rsInterface.width, rsiY);
        else
            RSRaster.setDrawingArea(rsiY + rsInterface.height - 223, 0, rsiX + rsInterface.width - 20, rsiY - 16);
                
        int i2 = rsInterface.children.length;
        for (int j2 = 0; j2 < i2; j2++)
        {
            int rsiChild_x = rsInterface.childX[j2] + rsiX;
            int rsiChild_y = (rsInterface.childY[j2] + rsiY) - scrollPosition;
            RSInterface rsiChild = RSInterface.interfaceCache[rsInterface.children[j2]];
            rsiChild_x += rsiChild.xOffset;
            rsiChild_y += rsiChild.yOffset;
            if (rsiChild.contentType > 0)
                drawFriendsListOrWelcomeScreen(rsiChild);
            
           // }
            /*
            int[] IDs = { 1196, 1199, 1206, 1215, 1224, 1231, 1240, 1249, 1258,
					1267, 1274, 1283, 1573, 1290, 1299, 1308, 1315, 1324, 1333,
					1340, 1349, 1358, 1367, 1374, 1381, 1388, 1397, 1404, 1583,
					12038, 1414, 1421, 1430, 1437, 1446, 1453, 1460, 1469,
					15878, 1602, 1613, 1624, 7456, 1478, 1485, 1494, 1503,
					1512, 1521, 1530, 1544, 1553, 1563, 1593, 1635, 12426,
					12436, 12446, 12456, 6004, 18471,
					// Ancients
					12940, 12988, 13036, 12902, 12862, 13046, 12964, 13012,
					13054, 12920, 12882, 13062, 12952, 13000, 13070, 12912,
					12872, 13080, 12976, 13024, 13088, 12930, 12892, 13096 };
			for (int m5 = 0; m5 < IDs.length; m5++) {
				if (class9_1.id == IDs[m5] + 1) {
					if (m5 > 61)
						drawBlackBox(k2 + 1, l2);
					else
						drawBlackBox(k2, l2 + 1);
				}
			}
			int[] runeChildren = { 1202, 1203, 1209, 1210, 1211, 1218, 1219,
					1220, 1227, 1228, 1234, 1235, 1236, 1243, 1244, 1245, 1252,
					1253, 1254, 1261, 1262, 1263, 1270, 1271, 1277, 1278, 1279,
					1286, 1287, 1293, 1294, 1295, 1302, 1303, 1304, 1311, 1312,
					1318, 1319, 1320, 1327, 1328, 1329, 1336, 1337, 1343, 1344,
					1345, 1352, 1353, 1354, 1361, 1362, 1363, 1370, 1371, 1377,
					1378, 1384, 1385, 1391, 1392, 1393, 1400, 1401, 1407, 1408,
					1410, 1417, 1418, 1424, 1425, 1426, 1433, 1434, 1440, 1441,
					1442, 1449, 1450, 1456, 1457, 1463, 1464, 1465, 1472, 1473,
					1474, 1481, 1482, 1488, 1489, 1490, 1497, 1498, 1499, 1506,
					1507, 1508, 1515, 1516, 1517, 1524, 1525, 1526, 1533, 1534,
					1535, 1547, 1548, 1549, 1556, 1557, 1558, 1566, 1567, 1568,
					1576, 1577, 1578, 1586, 1587, 1588, 1596, 1597, 1598, 1605,
					1606, 1607, 1616, 1617, 1618, 1627, 1628, 1629, 1638, 1639,
					1640, 6007, 6008, 6011, 8673, 8674, 12041, 12042, 12429,
					12430, 12431, 12439, 12440, 12441, 12449, 12450, 12451,
					12459, 12460, 15881, 15882, 15885, 18474, 18475, 18478 };
			for (int r = 0; r < runeChildren.length; r++)
				if (class9_1.id == runeChildren[r])
					class9_1.modelZoom = 775;
			*/
			if (rsiChild.type == 8 && rsiX >= i2 && i1 >= j2 && rsiX < i2 + rsiChild.width
					&& i1 < j2 + rsiChild.height) {
				anInt1315 = rsiChild.id;
			}
            if (rsiChild.type == 0)
            {
                if (rsiChild.scrollPosition > rsiChild.scrollMax - rsiChild.height)
                    rsiChild.scrollPosition = (short) (rsiChild.scrollMax - rsiChild.height);
                if (rsiChild.scrollPosition < 0)
                    rsiChild.scrollPosition = 0;
                drawInterface(rsiChild.scrollPosition, rsiChild_x, rsiChild, rsiChild_y, false);
            }
            if (rsiChild.scrollMax > rsiChild.height) //Emote interface scrollbar
            {
                drawScrollbar_chat(rsiChild.height, rsiChild.scrollPosition, rsiChild_y, rsiChild_x + rsiChild.width, rsiChild.scrollMax);
            }
            else if (rsiChild.type == 2)
            {
                int i3 = 0;
                for (int l3 = 0; l3 < rsiChild.height; l3++)
                {
                    for (int l4 = 0; l4 < rsiChild.width; l4++)
                    {
                        int k5 = rsiChild_x + l4 * (32 + rsiChild.invSpritePadX);
                        int j6 = rsiChild_y + l3 * (32 + rsiChild.invSpritePadY);
                        if (i3 < 20)
                        {
                            k5 += rsiChild.spritesX[i3];
                            j6 += rsiChild.spritesY[i3];
                        }
                        if (rsiChild.inv[i3] > 0)
                        {
                            int k6 = 0;
                            int j7 = 0;
                            int j9 = rsiChild.inv[i3] - 1;
                            if (k5 > RSRaster.topX - 32 && k5 < RSRaster.bottomX && j6 > RSRaster.topY - 32 && j6 < RSRaster.bottomY || activeInterfaceType != 0 && anInt1085 == i3)
                            {
                                int l9 = 0;
                                if (itemSelected == 1 && anInt1283 == i3 && anInt1284 == rsiChild.id)
                                    l9 = 0xffffff;
                                DirectImage class30_sub2_sub1_sub1_2 = ItemDefinition.getSprite(j9, rsiChild.invStackSizes[i3], l9);
                                if (class30_sub2_sub1_sub1_2 != null)
                                {
                                    if (activeInterfaceType != 0 && anInt1085 == i3 && anInt1084 == rsiChild.id)
                                    {
                                        k6 = super.mouseX - anInt1087;
                                        j7 = super.mouseY - anInt1088;
                                        if (k6 < 5 && k6 > -5)
                                            k6 = 0;
                                        if (j7 < 5 && j7 > -5)
                                            j7 = 0;
                                        if (anInt989 < 10)
                                        {
                                            k6 = 0;
                                            j7 = 0;
                                        }
                                        class30_sub2_sub1_sub1_2.drawSprite1(k5 + k6, j6 + j7);
                                        if (j6 + j7 < RSRaster.topY && rsInterface.scrollPosition > 0)
                                        {
                                            int i10 = (anInt945 * (RSRaster.topY - j6 - j7)) / 3;
                                            if (i10 > anInt945 * 10)
                                                i10 = anInt945 * 10;
                                            if (i10 > rsInterface.scrollPosition)
                                                i10 = rsInterface.scrollPosition;
                                            rsInterface.scrollPosition -= i10;
                                            anInt1088 += i10;
                                        }
                                        if (j6 + j7 + 32 > RSRaster.bottomY && rsInterface.scrollPosition < rsInterface.scrollMax - rsInterface.height)
                                        {
                                            int j10 = (anInt945 * ((j6 + j7 + 32) - RSRaster.bottomY)) / 3;
                                            if (j10 > anInt945 * 10)
                                                j10 = anInt945 * 10;
                                            if (j10 > rsInterface.scrollMax - rsInterface.height - rsInterface.scrollPosition)
                                                j10 = rsInterface.scrollMax - rsInterface.height - rsInterface.scrollPosition;
                                            rsInterface.scrollPosition += j10;
                                            anInt1088 -= j10;
                                        }
                                    }
                                    else if (atInventoryInterfaceType != 0 && atInventoryIndex == i3 && atInventoryInterface == rsiChild.id) {
                                        class30_sub2_sub1_sub1_2.drawSprite1(k5, j6);
                                    } else {
                                        class30_sub2_sub1_sub1_2.drawSprite(k5, j6);
                                    }
                                    if (class30_sub2_sub1_sub1_2.maxWidth == 33 || rsiChild.invStackSizes[i3] != 1)
                                    {
                                        int k10 = rsiChild.invStackSizes[i3];
                                        if (intToKOrMil(k10) == "@inf@")
                                        {
                                            infinitySymbol.drawIndexedImage(k5 + k6, j6 + j7);
                                        }
                                        else
                                        {
                                            smallText.method385(0, intToKOrMil(k10), j6 + 10 + j7, k5 + 1 + k6);
                                            if (k10 >= 1)
                                                smallText.method385(0xFFFF00, intToKOrMil(k10), j6 + 9 + j7, k5 + k6);
                                            if (k10 >= 100000)
                                                smallText.method385(0xFFFFFF, intToKOrMil(k10), j6 + 9 + j7, k5 + k6);
                                            if (k10 >= 10000000)
                                                smallText.method385(0x00FF80, intToKOrMil(k10), j6 + 9 + j7, k5 + k6);
                                        }
                                    }
                                }
                            }
                        }
                        else if (rsiChild.sprites != null && i3 < 20)
                        {
                            DirectImage class30_sub2_sub1_sub1_1 = rsiChild.sprites[i3];
                            if (class30_sub2_sub1_sub1_1 != null)
                                class30_sub2_sub1_sub1_1.drawSprite(k5, j6);
                        }
                        i3++;
                    }
                }
            }
            else if (rsiChild.type == 3)
            {
                boolean flag = false;
                if (anInt1039 == rsiChild.id || anInt1048 == rsiChild.id || anInt1026 == rsiChild.id)
                    flag = true;
                int j3;
                if (interfaceIsSelected(rsiChild))
                {
                    j3 = rsiChild.enabledTextColor;
                    if (flag && rsiChild.enabledTextHoverColor != 0)
                        j3 = rsiChild.enabledTextHoverColor;
                }
                else
                {
                    j3 = rsiChild.disabledTextColor;
                    if (flag && rsiChild.disabledTextHoverColor != 0)
                        j3 = rsiChild.disabledTextHoverColor;
                }
                if (rsiChild.alpha == 0)
                {
                    if (rsiChild.filled)
                        RSRaster.drawPixels(rsiChild.height, rsiChild_y, rsiChild_x, j3, rsiChild.width);
                    else
                        RSRaster.fillPixels(rsiChild_x, rsiChild.width, rsiChild.height, j3, rsiChild_y);
                }
                else if (rsiChild.filled)
                    RSRaster.method335(j3, rsiChild_y, rsiChild.width, rsiChild.height, 256 - (rsiChild.alpha & 0xff), rsiChild_x);
                else
                    RSRaster.method338(rsiChild_y, rsiChild.height, 256 - (rsiChild.alpha & 0xff), j3, rsiChild.width, rsiChild_x);
            }
            else if (rsiChild.type == 4)
            {
                TextDrawingArea textDrawingArea = rsiChild.font;
                String s = rsiChild.disabledMessage;
                boolean flag1 = false;
                if (anInt1039 == rsiChild.id || anInt1048 == rsiChild.id || anInt1026 == rsiChild.id)
                    flag1 = true;
                if (flag1 && menuOpen)
                {
                    anInt1026 = -1;
                    anInt1048 = -1;
                    anInt1039 = -1;
                    flag1 = false;
                }
                int i4;
                if (interfaceIsSelected(rsiChild))
                {
                    i4 = rsiChild.enabledTextColor;
                    if (flag1 && rsiChild.enabledTextHoverColor != 0)
                        i4 = rsiChild.enabledTextHoverColor;
                    if (rsiChild.enabledMessage.length() > 0)
                        s = rsiChild.enabledMessage;
                }
                else
                {
                    i4 = rsiChild.disabledTextColor;
                    if (flag1 && rsiChild.disabledTextHoverColor != 0)
                        i4 = rsiChild.disabledTextHoverColor;
                }
                if (rsiChild.atActionType == 6 && aBoolean1149)
                {
                    s = "Please wait...";
                    i4 = rsiChild.disabledTextColor;
                }
                // TODO: Check this akzu
                if (chatInterface)
                {
                    if (i4 == 0xffff00)
                        i4 = 255;
                    if (i4 == 49152)
                        i4 = 0xffffff;
                }
                int extra = 0;
                if (rsiChild.id == 3557 || rsiChild.id == 3558)
                    extra = 2;
                else if (rsiChild.id == 15038 || rsiChild.id == 2422)
                    extra = 2;
                else if (rsiChild.id == 4444)
                    extra = 3;
                if ((rsiChild.parentID == 1151) || (rsiChild.parentID == 12855)) {
					switch (i4) {
					case 16773120:
						i4 = 0xFE981F;
						break;
					case 7040819:
						i4 = 0xAF6A1A;
						break;
					}
				}
                for (int l6 = rsiChild_y + textDrawingArea.anInt1497; s.length() > 0; l6 += textDrawingArea.anInt1497 + extra)
                {
                    if (s.indexOf("%") != -1)
                    {
                        do
                        {
                            int k7 = s.indexOf("%1");
                            if (k7 == -1)
                                break;
                            // TODO: CHECK IF IT BUGS
                            if (rsInterface.parentID == 1151 || rsInterface.parentID == 1829 || rsInterface.parentID == 1689 || rsInterface.parentID == 12855)
                                s = s.substring(0, k7) + methodR(extractInterfaceValues(rsiChild, 0)) + s.substring(k7 + 2);
                            else
                                s = s.substring(0, k7) + interfaceIntToString(extractInterfaceValues(rsiChild, 0)) + s.substring(k7 + 2);
                        }
                        while (true);
                        do
                        {
                            int l7 = s.indexOf("%2");
                            if (l7 == -1)
                                break;
                            s = s.substring(0, l7) + interfaceIntToString(extractInterfaceValues(rsiChild, 1)) + s.substring(l7 + 2);
                        }
                        while (true);
                        do
                        {
                            int i8 = s.indexOf("%3");
                            if (i8 == -1)
                                break;
                            s = s.substring(0, i8) + interfaceIntToString(extractInterfaceValues(rsiChild, 2)) + s.substring(i8 + 2);
                        }
                        while (true);
                        do
                        {
                            int j8 = s.indexOf("%4");
                            if (j8 == -1)
                                break;
                            s = s.substring(0, j8) + interfaceIntToString(extractInterfaceValues(rsiChild, 3)) + s.substring(j8 + 2);
                        }
                        while (true);
                        do
                        {
                            int k8 = s.indexOf("%5");
                            if (k8 == -1)
                                break;
                            s = s.substring(0, k8) + interfaceIntToString(extractInterfaceValues(rsiChild, 4)) + s.substring(k8 + 2);
                        }
                        while (true);
                    }
                    int l8 = s.indexOf("\\n");
                    String s1;
                    if (l8 != -1)
                    {
                        s1 = s.substring(0, l8);
                        s = s.substring(l8 + 2);
                    }
                    else
                    {
                        s1 = s;
                        s = "";
                    }
                    if (rsiChild.textCentered)
                        textDrawingArea.method382(i4, rsiChild_x + rsiChild.width / 2, s1, l6, rsiChild.textShadow);
                    else
                        textDrawingArea.method389(rsiChild.textShadow, rsiChild_x, i4, s1, l6);
                }
            }
            else if (rsiChild.type == 5)
            {
                DirectImage sprite;
                if (interfaceIsSelected(rsiChild))
                    sprite = rsiChild.enabledSprite;
                else
                    sprite = rsiChild.disabledSprite;
                if(rsiChild.isMagicButton) {
                	if(canCastSpell(rsiChild)) {
                		sprite = rsiChild.enabledSprite;
                	} else {
                		sprite = rsiChild.disabledSprite;
                	}
                }
                // TODO Magic sprite outlining not working whaaaaaaaaaaaat>>>>>>>>>>>>
                //fixed nagga lol squad
                //~ Stephen
                if (sprite != null) {
                    if (spellSelected == 1 && rsiChild.id == spellID && spellID != 0)
                    {
                        sprite.drawSprite(rsiChild_x, rsiChild_y, 0xffffff);
                    }
                    else
                    {
                        sprite.drawSprite(rsiChild_x, rsiChild_y);
                    }
                }
            }
            else if (rsiChild.type == 6)
            {
                int k3 = Rasterizer.textureInt1;
                int j4 = Rasterizer.textureInt2;
                Rasterizer.textureInt1 = rsiChild_x + rsiChild.width / 2;
                Rasterizer.textureInt2 = rsiChild_y + rsiChild.height / 2;
                int[][] runes =
                {
                { 554, 2399 },
                { 555, 2652 },
                { 556, 2405 },
                { 557, 2737 },
                { 558, 2481 },
                { 559, 2340 },
                { 560, 2645 },
                { 561, 2734 },
                { 562, 2707 },
                { 563, 2382 },
                { 564, 2735 },
                { 565, 2665 },
                { 566, 2434 },
                { 9075, 16742 } };
                int zoom = rsiChild.modelZoom;
                for (int i = 0; i < runes.length; i++)
                {
                    if (rsiChild.mediaID == runes[i][1] && (rsiChild.parentID == 1151 || rsiChild.parentID == 12855))
                    {
                        zoom *= 3;
                    }
                }
                int i5 = Rasterizer.sineTable[rsiChild.modelRotY] * zoom >> 16;
                int l5 = Rasterizer.cosineTable[rsiChild.modelRotY] * zoom >> 16;
                boolean flag2 = interfaceIsSelected(rsiChild);
                int i7;
                if (flag2)
                    i7 = rsiChild.enabledAnimation;
                else
                    i7 = rsiChild.disabledAnimation;
                Model model;
                if (i7 == -1)
                {
                    model = rsiChild.method209(-1, -1, flag2, rsiChild);
                }
                else
                {
                    Sequence animation = Sequence.anims[i7];
                    model = rsiChild.method209(animation.anIntArray354[rsiChild.animFrame], animation.anIntArray353[rsiChild.animFrame], flag2, rsiChild);
                }
                if (model != null)
                    model.method482(rsiChild.modelRotX, 0, rsiChild.modelRotY, 0, i5, l5, chatInterface);
                if (model != null)
                {
                    for (int i11 = 0; i11 < runes.length; i11++)
                    {
                        if (rsiChild.mediaID == runes[i11][1] && (rsiChild.parentID == 1151 || rsiChild.parentID == 12855))
                        {
                            DirectImage sprite = ItemDefinition.getSprite_magic(runes[i11][0]);
                            if (sprite != null)
                                sprite.drawSprite(rsiChild_x - 2, rsiChild_y - 2);
                        }
                    }
                }
                Rasterizer.textureInt1 = k3;
                Rasterizer.textureInt2 = j4;
            }
            else if (rsiChild.type == 7)
            {
                TextDrawingArea textDrawingArea_1 = rsiChild.font;
                int k4 = 0;
                for (int j5 = 0; j5 < rsiChild.height; j5++)
                {
                    for (int i6 = 0; i6 < rsiChild.width; i6++)
                    {
                        if (rsiChild.inv[k4] > 0)
                        {
                            ItemDefinition itemDef = ItemDefinition.forID(rsiChild.inv[k4] - 1);
                            String s2 = itemDef.name;
                            if (itemDef.stackable || rsiChild.invStackSizes[k4] != 1)
                                s2 = s2 + " x" + intToKOrMilLongName(rsiChild.invStackSizes[k4]);
                            int i9 = rsiChild_x + i6 * (115 + rsiChild.invSpritePadX);
                            int k9 = rsiChild_y + j5 * (10 + rsiChild.invSpritePadY); // 12
                            // +
                            if (rsiChild.textCentered)
                                textDrawingArea_1.method382(rsiChild.disabledTextColor, i9 + rsiChild.width / 2, s2, k9, rsiChild.textShadow);
                            else
                                textDrawingArea_1.method389(rsiChild.textShadow, i9, rsiChild.disabledTextColor, s2, k9);
                        }
                        k4++;
                    }
                }
            }
            else if (rsiChild.type == 8 && (anInt1500 == rsiChild.id || anInt1044 == rsiChild.id || anInt1129 == rsiChild.id) && anInt1501 == 50 && !menuOpen)
            {
                int boxWidth = 0;
                int boxHeight = 0;
                TextDrawingArea textDrawingArea_2 = regularText;
                for (String s1 = rsiChild.disabledMessage; s1.length() > 0;)
                {
                    if (s1.indexOf("%") != -1)
                    {
                        do
                        {
                            int k7 = s1.indexOf("%1");
                            if (k7 == -1)
                                break;
                            s1 = s1.substring(0, k7) + interfaceIntToString(extractInterfaceValues(rsiChild, 0)) + s1.substring(k7 + 2);
                        }
                        while (true);
                        do
                        {
                            int l7 = s1.indexOf("%2");
                            if (l7 == -1)
                                break;
                            s1 = s1.substring(0, l7) + interfaceIntToString(extractInterfaceValues(rsiChild, 1)) + s1.substring(l7 + 2);
                        }
                        while (true);
                        do
                        {
                            int i8 = s1.indexOf("%3");
                            if (i8 == -1)
                                break;
                            s1 = s1.substring(0, i8) + interfaceIntToString(extractInterfaceValues(rsiChild, 2)) + s1.substring(i8 + 2);
                        }
                        while (true);
                        do
                        {
                            int j8 = s1.indexOf("%4");
                            if (j8 == -1)
                                break;
                            s1 = s1.substring(0, j8) + interfaceIntToString(extractInterfaceValues(rsiChild, 3)) + s1.substring(j8 + 2);
                        }
                        while (true);
                        do
                        {
                            int k8 = s1.indexOf("%5");
                            if (k8 == -1)
                                break;
                            s1 = s1.substring(0, k8) + interfaceIntToString(extractInterfaceValues(rsiChild, 4)) + s1.substring(k8 + 2);
                        }
                        while (true);
                    }
                    int l7 = s1.indexOf("\\n");
                    String s4;
                    if (l7 != -1)
                    {
                        s4 = s1.substring(0, l7);
                        s1 = s1.substring(l7 + 2);
                    }
                    else
                    {
                        s4 = s1;
                        s1 = "";
                    }
                    int j10 = textDrawingArea_2.getTextWidth(s4);
                    if (j10 > boxWidth)
                    {
                        boxWidth = j10;
                    }
                    boxHeight += textDrawingArea_2.anInt1497 + 1;
                }
                boxWidth += 6;
                boxHeight += 7;
                int xPos = (rsiChild_x + rsiChild.width) - 5 - boxWidth;
                int yPos = rsiChild_y + rsiChild.height + 5;
                if (rsInterface.id == 3917 || rsiChild.id != 4070 || rsiChild.id != 4106 || rsiChild.id != 4142)
                    yPos = rsiChild_y + rsiChild.height + 1;
                if (rsiChild.id == 4070 || rsiChild.id == 4106 || rsiChild.id == 4142)
                    yPos = rsiChild_y + rsiChild.height - 2;
                if (xPos < rsiChild_x + 5)
                    xPos = rsiChild_x + 5;
                if (rsInterface.id == 5608)
                {
                    if (xPos + boxWidth > (clientSize == CLIENT_FIXED ? 227 : (clientWidth >= 1006 ? clientWidth - 10 : clientWidth - 23)))
                    {
                        xPos = (clientSize == CLIENT_FIXED ? 252 - boxWidth - rsiX : (clientWidth >= 1006 ? rsiX + 189 - boxWidth : rsiX + 189 - boxWidth));
                    }
                    if (yPos + boxHeight > (clientSize == CLIENT_FIXED ? 283 : (clientWidth >= 1006 ? clientHeight - 47 : clientHeight - 85)))
                    {
                        yPos = (clientSize == CLIENT_FIXED ? 270 - boxHeight - rsiY : (clientWidth >= 1006 ? rsiY + 199 - boxHeight : rsiY + 199 - boxHeight));
                    }
                }
                else if (rsInterface.id == 3917)
                {
                    if (xPos + boxWidth > (clientSize == CLIENT_FIXED ? 227 : (clientWidth >= 1006 ? clientWidth - 10 : clientWidth - 23)))
                    {
                        xPos = (clientSize == CLIENT_FIXED ? 252 - boxWidth - rsiX : (clientWidth >= 1006 ? rsiX + 189 - boxWidth : rsiX + 189 - boxWidth));
                    }
                    if (yPos + boxHeight > (clientSize == CLIENT_FIXED ? 303 : (clientWidth >= 1006 ? clientHeight - 41 : clientHeight - 79)))
                    {
                        yPos = (clientSize == CLIENT_FIXED ? rsiChild_y - boxHeight : (clientWidth >= 1006 ? rsiChild_y - boxHeight : rsiChild_y - boxHeight));
                    }
                }
                if (xPos + boxWidth > rsiX + rsInterface.width)
                    xPos = (rsiX + rsInterface.width) - boxWidth;
                if (yPos + boxHeight > rsiY + rsInterface.height)
                    yPos = (rsiChild_y - boxHeight);
                RSRaster.drawPixels(boxHeight, yPos, xPos, 0xFFFFA0, boxWidth);
                RSRaster.fillPixels(xPos, boxWidth, boxHeight, 0, yPos);
                String s2 = rsiChild.disabledMessage;
                for (int j11 = yPos + textDrawingArea_2.anInt1497 + 2; s2.length() > 0; j11 += textDrawingArea_2.anInt1497 + 1)
                {
                    if (s2.indexOf("%") != -1)
                    {
                        do
                        {
                            int k7 = s2.indexOf("%1");
                            if (k7 == -1)
                                break;
                            s2 = s2.substring(0, k7) + interfaceIntToString(extractInterfaceValues(rsiChild, 0)) + s2.substring(k7 + 2);
                        }
                        while (true);
                        do
                        {
                            int l7 = s2.indexOf("%2");
                            if (l7 == -1)
                                break;
                            s2 = s2.substring(0, l7) + interfaceIntToString(extractInterfaceValues(rsiChild, 1)) + s2.substring(l7 + 2);
                        }
                        while (true);
                        do
                        {
                            int i8 = s2.indexOf("%3");
                            if (i8 == -1)
                                break;
                            s2 = s2.substring(0, i8) + interfaceIntToString(extractInterfaceValues(rsiChild, 2)) + s2.substring(i8 + 2);
                        }
                        while (true);
                        do
                        {
                            int j8 = s2.indexOf("%4");
                            if (j8 == -1)
                                break;
                            s2 = s2.substring(0, j8) + interfaceIntToString(extractInterfaceValues(rsiChild, 3)) + s2.substring(j8 + 2);
                        }
                        while (true);
                        do
                        {
                            int k8 = s2.indexOf("%5");
                            if (k8 == -1)
                                break;
                            s2 = s2.substring(0, k8) + interfaceIntToString(extractInterfaceValues(rsiChild, 4)) + s2.substring(k8 + 2);
                        }
                        while (true);
                    }
                    int l11 = s2.indexOf("\\n");
                    String s5;
                    if (l11 != -1)
                    {
                        s5 = s2.substring(0, l11);
                        s2 = s2.substring(l11 + 2);
                    }
                    else
                    {
                        s5 = s2;
                        s2 = "";
                    }
                    if (rsiChild.textCentered)
                    {
                        textDrawingArea_2.method382(yPos, xPos + rsiChild.width / 2, s5, j11, false);
                    }
                    else
                    {
                        if (s5.contains("\\r"))
                        {
                            String text = s5.substring(0, s5.indexOf("\\r"));
                            String text2 = s5.substring(s5.indexOf("\\r") + 2);
                            textDrawingArea_2.method389(false, xPos + 3, 0, text, j11);
                            int rightX = boxWidth + xPos - textDrawingArea_2.getTextWidth(text2) - 2;
                            textDrawingArea_2.method389(false, rightX, 0, text2, j11);
                            logger.info("Box: " + boxWidth + "");
                        }
                        else
                            textDrawingArea_2.method389(false, xPos + 3, 0, s5, j11);
                    }
                }
            }
        }
        if(rsInterface.id == 17000 && anInt1048 > 0) {
        	//System.out.println("Hovered ID: "+anInt1048);
        	int[] IDs = { 17001, 17003, 17005, 17007, 17009, 17011, 17013, 17015,
        			17017, 17019, 17021, 17023, 17025, 17027, 17029, 17031,
        			17033, 17035, 17037, 17039, 17041, 17043, 17045, 17047,
        			17049, 17051, 17053, 17055, 17057, 17059, 17061, 17063};
        	for(int m5=0; m5<IDs.length; m5++) {
        		if(anInt1048 == (IDs[m5]+1)) {
        			RSInterface child = RSInterface.interfaceCache[IDs[m5]];
        			int childX = rsInterface.childX[m5]+rsiX;
        			int childY = rsInterface.childY[m5]+rsiY;
        			drawRuneBox(child, childX, childY);
        		}
        	}
        }
        RSRaster.setDrawingArea(l1, i1, k1, j1);
    }
    
    private void drawRuneBox(RSInterface child, int xPos, int yPos) {
    	MagicSpell spell = child.spell;
		DirectImage prayerSprite = ItemDefinition.getSprite_magic(1714);
    	boolean isTeleport = false;

    	int childY = yPos + 25;
    	int boxX = 37;
		int boxY = yPos + 25;
	    
		/**
		 * Black box resizing with client
		 */
	    if (clientSize == CLIENT_FIXED) {
	    	 if(childY > 200) {
	 	    	boxY = 45;
	 	    	isTeleport = true;
	 	    } else {
	 	    	boxY = 215;
	 	    }
	    } else if (clientSize != CLIENT_FIXED && clientWidth < 1006) {
	    	boxX = clientWidth - 207;
	    	if (clientHeight - childY >= 185) {
	    		boxY = clientHeight - 170;
	    	} else {
	    		boxY = clientHeight - 340;
	    		isTeleport = true;
	    	}
	    } else {
	    	boxX = clientWidth - 193;
	    	if (clientHeight - childY >= 147) {
	    		boxY = clientHeight - 133;
	    	} else {
	    		boxY = clientHeight - 303;
	    		isTeleport = true;
	    	}
	    }
	    
	    if (child.id == 17001) { //Xp lock has same blackbox as teleports
	    	isTeleport = true;
	    }
	    drawSmallBox(boxX, boxY);
    	if (isTeleport == true || child.id == 17001) {
    		newRegularFont.drawCenteredString(spell.getSpellName(), (boxX+90), boxY+26, 0xFF981F, 0);
    		newSmallFont.drawCenteredString(spell.getDesc(), (boxX+90), boxY+50, 0xAF6A1A, 0);
    	} else {
    		newRegularFont.drawCenteredString("Level "+spell.getSpellLevel()+": "+spell.getSpellName(), (boxX+90), boxY+16, 0xFF981F, 0);
    		newSmallFont.drawCenteredString(spell.getDesc(), (boxX+90), boxY+29, 0xAF6A1A, 0);
    	}
    	
    	int[] spellRunes = spell.getRunes();
    	int[] runeAmounts = spell.getReqRunes();
    	int prayerReq = spell.getReqPrayerLevel();
    	int prayerLevel = currentStats[levelIDs[12]];
    	int runeGap = 62; //How far the runes will be apart in blackbox
    	int runeX = boxX+10;
    	int runeY = boxY+32;
    	int prayerAmountColor = 0xC00000;
    		
    	if (spellRunes.length == 2) {
    		runeX += runeGap/2;
    		if (prayerReq > 0 && prayerSprite != null) {
	    		prayerSprite.drawSprite(runeX - 25, runeY);
	    		if(prayerLevel >= prayerReq)
					prayerAmountColor = 0x00FF00;
				newSmallFont.drawCenteredString(prayerLevel+"/"+prayerReq, runeX+((prayerSprite.myWidth/2)-25), runeY+(prayerSprite.myHeight+11), prayerAmountColor, 0);
				runeX += runeGap - 30; //Moves rune group right as you increase [30] 
	    	}

    	}
    	if (spellRunes.length == 3 && prayerReq > 0) {
    		runeGap = runeGap - 15;
    		if (prayerSprite != null) {
    			prayerSprite.drawSprite(runeX - 5, runeY);
	    		if(prayerLevel >= prayerReq)
					prayerAmountColor = 0x00FF00;
				newSmallFont.drawCenteredString(prayerLevel+"/"+prayerReq, runeX+((prayerSprite.myWidth/2)-5), runeY+(prayerSprite.myHeight+11), prayerAmountColor, 0);
	    		runeX += runeGap - 15;    	
    		}
    	}
    	for(int i=0; i<spellRunes.length; i++) {
    		int runeAmountColor = 0xC00000;
    		DirectImage sprite = ItemDefinition.getSprite_magic(spellRunes[i]);
    		if (sprite != null) {
    			int invAmount = getItemAmountInv(spellRunes[i]);
    	    	if (isTeleport == false)
    	    		sprite.drawSprite(runeX, runeY);
    			if(invAmount >= runeAmounts[i])
    				runeAmountColor = 0x00FF00;
    			if (isTeleport == false)
    				newSmallFont.drawCenteredString(intToKOrMil(invAmount)+"/"+runeAmounts[i], runeX+(sprite.myWidth/2), runeY+(sprite.myHeight+11), runeAmountColor, 0);
    			runeX += runeGap;
    		}	
    	}
    }
    
    /**
     * Draws a smaller rune box for spells with a short desc.
     * @param x
     * @param y
     */
    private void drawSmallBox(int x, int y) {
    	RSRaster.drawRectangle(x, y, 179, 79, 0x726451);
    	RSRaster.drawHorizontalLine(x+1, y+1, 177, 0x2e2b23);
    	RSRaster.drawVerticalLine(x+1, y+2, 76, 0x2e2b23);
    	RSRaster.drawVerticalLine(x+179, y, 80, 0x2e2b23);
    	RSRaster.drawHorizontalLine(x, y+79, 180, 0x2e2b23);
    	RSRaster.fillRectangleAlpha(x+2, y+2, 176, 76, 0x000000, 220);
    }
    
    /**
     * Draws a bigger rune box for spells with a long desc.
     * @param x
     * @param y
     */
    private void drawBigBox(int x, int y) {
    	RSRaster.drawRectangle(x, y, 179, 89, 0x726451);
    	RSRaster.drawHorizontalLine(x+1, y+1, 187, 0x2e2b23);
    	RSRaster.drawVerticalLine(x+1, y+2, 86, 0x2e2b23);
    	RSRaster.drawVerticalLine(x+179, y, 90, 0x2e2b23);
    	RSRaster.drawHorizontalLine(x, y+79, 190, 0x2e2b23);
    	RSRaster.fillRectangleAlpha(x+2, y+2, 176, 86, 0x000000, 220);
    }
    
    private boolean canCastSpell(RSInterface child) {
    	MagicSpell spell = child.spell;
    	int[] spellRunes = spell.getRunes();
    	int[] reqRunes = spell.getReqRunes();
    	int magicLevel = currentStats[levelIDs[15]];
    	int prayerLevel = currentStats[levelIDs[12]];

    	
    	if(magicLevel < spell.getSpellLevel())
    		return false;
    	if(prayerLevel < spell.getReqPrayerLevel())
    		return false;
    	
    	for(int i=0; i<reqRunes.length; i++) {
    		if(getItemAmountInv(spellRunes[i]) < reqRunes[i]) {
    			return false;
    		}
    	}    	
    	return true;
    }
    
    private int getItemAmountInv(int itemID) {
    	RSInterface inventory = RSInterface.interfaceCache[3214];
    	for(int i = 0; i<inventory.inv.length; i++) {
    		if(inventory.inv[i]-1 == itemID) {
    			return inventory.invStackSizes[i];
    		}
    	}
    	
    	return 0;
    }

    private void method107(int i, int j, RSBuffer stream, Player player)
    {
        if ((i & 0x400) != 0)
        {
            player.anInt1543 = stream.method428();
            player.anInt1545 = stream.method428();
            player.anInt1544 = stream.method428();
            player.anInt1546 = stream.method428();
            player.anInt1547 = stream.method436() + loopCycle;
            player.anInt1548 = stream.method435() + loopCycle;
            player.anInt1549 = stream.method428();
            player.resetLocationIndex();
        }
        if ((i & 0x100) != 0)
        {
            player.gfxId = stream.method434();
            int k = stream.readInt();
            player.anInt1524 = k >> 16;
            player.anInt1523 = loopCycle + (k & 0xffff);
            player.currentAnim = 0;
            player.anInt1522 = 0;
            if (player.anInt1523 > loopCycle)
                player.currentAnim = -1;
            if (player.gfxId == 65535)
                player.gfxId = -1;
        }
        if ((i & 8) != 0)
        {
            int l = stream.method434();
            if (l == 65535)
                l = -1;
            int i2 = stream.method427();
            if (l == player.anim && l != -1)
            {
                int i3 = Sequence.anims[l].anInt365;
                if (i3 == 1)
                {
                    player.anInt1527 = 0;
                    player.anInt1528 = 0;
                    player.anInt1529 = i2;
                    player.anInt1530 = 0;
                }
                if (i3 == 2)
                    player.anInt1530 = 0;
            }
            else if (l == -1 || player.anim == -1 || Sequence.anims[l].anInt359 >= Sequence.anims[player.anim].anInt359)
            {
                player.anim = l;
                player.anInt1527 = 0;
                player.anInt1528 = 0;
                player.anInt1529 = i2;
                player.anInt1530 = 0;
                player.positionBasedInt = player.smallXYIndex;
            }
        }
        if ((i & 4) != 0)
        {
            player.textSpoken = stream.readString();
            player.privelage = stream.readUByte();
            if (player == myPlayer)
                pushMessage(player.textSpoken, 2, (player.privelage == 2 ? "@cr3@" : (player.privelage == 1) ? "@cr1@" : (player.privelage == 3 ? "@cr2@" : "")) + player.name);
            player.anInt1513 = 0;
            player.anInt1531 = 0;
            player.textCycle = 150;
        }
        if ((i & 0x80) != 0)
        {
            int i1 = stream.method434();
            int j2 = stream.readUByte();
            int j3 = stream.method427();
            int k3 = stream.pointer;
            if (player.name != null && player.visible)
            {
                long l3 = TextClass.longForName(player.name);
                boolean flag = false;
                if (j2 <= 1)
                {
                    for (int i4 = 0; i4 < ignoreCount; i4++)
                    {
                        if (ignoreListAsLongs[i4] != l3)
                            continue;
                        flag = true;
                        break;
                    }
                }
                if (!flag && anInt1251 == 0)
                    try
                    {
                        aStream_834.pointer = 0;
                        stream.method442(j3, 0, aStream_834.buffer);
                        aStream_834.pointer = 0;
                        String s = TextInput.method525(j3, aStream_834);
                        player.textSpoken = s;
                        player.anInt1513 = i1 >> 8;
                        player.privelage = j2;
                        player.anInt1531 = i1 & 0xff;
                        player.textCycle = 150;
                        if (j2 == 2)
                            pushMessage(s, 1, "@cr3@" + player.name);
                        else if (j2 == 3)
                            pushMessage(s, 1, "@cr2@" + player.name);
                        else if (j2 == 1)
                            pushMessage(s, 1, "@cr1@" + player.name);
                        else
                            pushMessage(s, 2, player.name);
                    }
                    catch (Exception exception)
                    {
                        System.err.println("cde2");
                    }
            }
            stream.pointer = k3 + j3;
        }
        if ((i & 1) != 0)
        {
            player.interactingEntity = stream.method434();
            if (player.interactingEntity == 65535)
                player.interactingEntity = -1;
        }
        if ((i & 0x10) != 0)
        {
            int j1 = stream.method427();
            byte abyte0[] = new byte[j1];
            RSBuffer stream_1 = new RSBuffer(abyte0);
            stream.readBytes(j1, 0, abyte0);
            aStreamArray895s[j] = stream_1;
            player.updatePlayer(stream_1);
        }
        if ((i & 2) != 0)
        {
            player.anInt1538 = stream.method436();
            player.anInt1539 = stream.method434();
        }
        if ((i & 0x20) != 0)
        {
            int k1 = stream.readUByte();
            int k2 = stream.method426();
            player.updateHitData(k2, k1, loopCycle);
            player.loopCycleStatus = loopCycle + 300;
            player.currentHealth = stream.method427();
            player.maxHealth = stream.readUByte();
        }
        if ((i & 0x200) != 0)
        {
            int l1 = stream.readUByte();
            int l2 = stream.method428();
            player.updateHitData(l2, l1, loopCycle);
            player.loopCycleStatus = loopCycle + 300;
            player.currentHealth = stream.readUByte();
            player.maxHealth = stream.method427();
        }
    }

    public void method108()
    {
        try
        {
            int j = myPlayer.x + cameraOffsetX;
            int k = myPlayer.y + cameraOffsetY;
            if (anInt1014 - j < -500 || anInt1014 - j > 500 || anInt1015 - k < -500 || anInt1015 - k > 500)
            {
                anInt1014 = j;
                anInt1015 = k;
            }
            if (anInt1014 != j)
                anInt1014 += (j - anInt1014) / 16;
            if (anInt1015 != k)
                anInt1015 += (k - anInt1015) / 16;
            if (super.keyArray[1] == 1)
                anInt1186 += (-24 - anInt1186) / 2;
            else if (super.keyArray[2] == 1)
                anInt1186 += (24 - anInt1186) / 2;
            else
                anInt1186 /= 2;
            if (super.keyArray[3] == 1)
                anInt1187 += (12 - anInt1187) / 2;
            else if (super.keyArray[4] == 1)
                anInt1187 += (-12 - anInt1187) / 2;
            else
                anInt1187 /= 2;
            viewRotation = viewRotation + anInt1186 / 2 & 0x7ff;
            anInt1184 += anInt1187 / 2;
            if (anInt1184 < 128)
                anInt1184 = 128;
            if (anInt1184 > 383)
                anInt1184 = 383;
            int l = anInt1014 >> 7;
            int i1 = anInt1015 >> 7;
            int j1 = method42(floor_level, anInt1015, anInt1014);
            int k1 = 0;
            if (l > 3 && i1 > 3 && l < 100 && i1 < 100)
            {
                for (int l1 = l - 4; l1 <= l + 4; l1++)
                {
                    for (int k2 = i1 - 4; k2 <= i1 + 4; k2++)
                    {
                        int l2 = floor_level;
                        if (l2 < 3 && (byteGroundArray[1][l1][k2] & 2) == 2)
                            l2++;
                        int i3 = j1 - intGroundArray[l2][l1][k2];
                        if (i3 > k1)
                            k1 = i3;
                    }
                }
            }
            int j2 = k1 * 192;
            if (j2 > 0x17f00)
                j2 = 0x17f00;
            if (j2 < 32768)
                j2 = 32768;
            if (j2 > anInt984)
            {
                anInt984 += (j2 - anInt984) / 24;
                return;
            }
            if (j2 < anInt984)
            {
                anInt984 += (j2 - anInt984) / 80;
            }
        }
        catch (Exception _ex)
        {
            System.err.println("glfc_ex " + myPlayer.x + "," + myPlayer.y + "," + anInt1014 + "," + anInt1015 + "," + anInt1069 + "," + anInt1070 + "," + baseX + "," + baseY);
            throw new RuntimeException("eek");
        }
    }

    public void processDrawing()
    {
        if (rsAlreadyLoaded || loadingError || genericLoadingError)
        {
            showErrorScreen();
            return;
        }
        if (!loggedIn)
        {
            drawLoginScreen(false);
        }
        else if (loggedIn)
        {
            drawGameScreen();
        }
        anInt1213 = 0;
    }

    private boolean isFriendOrSelf(String s)
    {
        if (s == null)
            return false;
        for (int i = 0; i < friendsCount; i++)
            if (s.equalsIgnoreCase(friendsList[i]))
                return true;
        return s.equalsIgnoreCase(myPlayer.name);
    }

    private static String combatDiffColor(int i, int j)
    {
        int k = i - j;
        if (k < -9)
            return "@red@";
        if (k < -6)
            return "@or3@";
        if (k < -3)
            return "@or2@";
        if (k < 0)
            return "@or1@";
        if (k > 9)
            return "@gre@";
        if (k > 6)
            return "@gr3@";
        if (k > 3)
            return "@gr2@";
        if (k > 0)
            return "@gr1@";
        else
            return "@yel@";
    }

    public void draw3dScreen()
    {
        drawSplitPrivateChat();

        if (crossType == 1)
        {
            crosses[crossIndex / 100].drawIndexedImage(crossX - 8 - (clientSize == CLIENT_FIXED ? 4 : 0), crossY - 8 - (clientSize == CLIENT_FIXED ? 4 : 0));
        }
        if (crossType == 2)
            crosses[4 + crossIndex / 100].drawIndexedImage(crossX - 8 - (clientSize == CLIENT_FIXED ? 4 : 0), crossY - 8 - (clientSize == CLIENT_FIXED ? 4 : 0));
        if (drawMultiIcon == 1)
            multiWay.drawIndexedImage((clientSize == CLIENT_FIXED ? 472 : (walkableInterface == 197 ? clientWidth - 268 : clientWidth - 255)), (clientSize == CLIENT_FIXED ? 296 : (walkableInterface == 197 ? 48 : 20)));
        if (walkableInterface != -1)
        {
            method119(anInt945, walkableInterface);

            // Castle wars
            if (walkableInterface == 11146 && clientSize != 0)
            {
                drawInterface(0, 0, RSInterface.interfaceCache[walkableInterface], -5, false);
            }
            else if ((walkableInterface == 2804 || walkableInterface == 11479) && clientSize != 0)
            {
                drawInterface(0, clientWidth / 2 - 256, RSInterface.interfaceCache[walkableInterface], -5, false);
            }
            else if (walkableInterface == 4535 && clientSize != 0)
            {
                drawInterface(0, -418, RSInterface.interfaceCache[walkableInterface], -285, false);
            }
            else if ((walkableInterface == 15892 || walkableInterface == 15917 || walkableInterface == 15931 || walkableInterface == 15962) && clientSize != 0)
            {
                drawInterface(0, (walkableInterface == 15892 ? -325 : -349), RSInterface.interfaceCache[walkableInterface], 25, false);
            }
            else
                drawInterface(0, (clientSize == CLIENT_FIXED ? 0 : (walkableInterface == 197 || walkableInterface == 201 ? clientWidth - 740 : (clientWidth / 2) - 256)), RSInterface.interfaceCache[walkableInterface], (clientSize == CLIENT_FIXED ? 0 : (walkableInterface == 197 || walkableInterface == 201 ? (walkableInterface == 201 ? -280 : -245) : (clientHeight / 2) - 256)), false);
        }
        if (openInterfaceID != -1)
        {
            method119(anInt945, openInterfaceID);
            drawInterface(0, (clientSize == CLIENT_FIXED ? 0 : returnGeneralInterfaceOffsetX()), RSInterface.interfaceCache[openInterfaceID], (clientSize == CLIENT_FIXED ? 0 : (clientHeight / 2) - 256), false);
        }
        method70();
        if (!menuOpen)
        {
            processRightClick();
            drawTooltip();
        }
        else
            drawMenu((clientSize == CLIENT_FIXED ? 4 : 0), (clientSize == CLIENT_FIXED ? 4 : 0));
        if (fpsOn)
        {
            int c = (clientSize == CLIENT_FIXED ? 505 : clientWidth - 300);
            int k = 20;
            int i1 = 0xffff00;
            int x = baseX + (myPlayer.x - 6 >> 7);
            int y = baseY + (myPlayer.y - 6 >> 7);
            if (super.fps < 15)
                i1 = 0xff0000;
            regularText.method380("Fps:" + super.fps, c, i1, k);
            k += 15;
            Runtime runtime = Runtime.getRuntime();
            double usedMem = runtime.totalMemory() - runtime.freeMemory();
            double totalMem = runtime.totalMemory();
            double percentage = (usedMem / totalMem) * 100;
            i1 = 0xffff00;
            if (percentage >= 85.0)
                i1 = 0xff0000;
            regularText.method380("Mem: " + (int) (usedMem / 1024L) + "k (" + (int) percentage + "%)", c, i1, k);
            k += 15;
            regularText.method380("Mouse X: " + super.mouseX, c, 0xffff00, k);
            k += 15;
            regularText.method380("Mouse Y: " + super.mouseY, c, 0xffff00, k);
            k += 15;
            regularText.method380("X, Y, Z: " + x + ", " + y + (y <= 4000 ? "(" + (y + 6400) + "), " : ", ") + floor_level, c, 0xffff00, k);
            k += 15;
            regularText.method380("Camera [curve]: " + xCameraCurve + ", " + yCameraCurve, c, 0xffff00, k);
            k += 15;
            regularText.method380("Dimension: [" + clientWidth + "x" + clientHeight + "]", c, 0xffff00, k);
        }
        if (systemUpdateTime != 0)
        {
            int j = systemUpdateTime / 50;
            int l = j / 60;
            j %= 60;
            if (j < 10)
                regularText.method385(0xffff00, "System update in: " + l + ":0" + j, 328 + (clientSize == CLIENT_FIXED ? 0 : (chatHidden ? clientHeight - 359 : clientHeight - 499)), 4);
            else
                regularText.method385(0xffff00, "System update in: " + l + ":" + j, 328 + (clientSize == CLIENT_FIXED ? 0 : (chatHidden ? clientHeight - 359 : clientHeight - 499)), 4);
        }
    }

    public void addIgnore(long l)
    {
        try
        {
            if (l == 0L)
                return;
            if (ignoreCount >= 100)
            {
                pushMessage("Your ignore list is full. Max of 100 hit.", 0, "");
                return;
            }
            String s = TextClass.fixName(TextClass.nameForLong(l));
            for (int j = 0; j < ignoreCount; j++)
                if (ignoreListAsLongs[j] == l)
                {
                    pushMessage(capitalize(s) + " is already on your ignore list.", 0, "");
                    return;
                }
            for (int k = 0; k < friendsCount; k++)
                if (friendsListAsLongs[k] == l)
                {
                    pushMessage("Please remove " + capitalize(s) + " from your friend list first.", 0, "");
                    return;
                }
            ignoreListAsLongs[ignoreCount++] = l;
            needDrawTabArea = true;
            stream.writeOpcode(133);
            stream.writeLong(l);
            return;
        }
        catch (RuntimeException runtimeexception)
        {
            System.err.println("45688, " + l + ", " + 4 + ", " + runtimeexception.toString());
        }
        throw new RuntimeException();
    }

    public void updatePlayerInstances()
    {
        for (int i = -1; i < playerCount; i++)
        {
            int playerIndex;
            if (i == -1)
                playerIndex = myPlayerIndex;
            else
                playerIndex = playerIndices[i];
            Player player = playerArray[playerIndex];
            if (player != null)
                updateEntity(player);
        }
    }

    // added harlans snippet for redrawing mapscene on server send requests
    public void updateSpawnedObjects()
    {
        if (loadingStage == 2)
        {
            boolean passedRequest = false;
            for (SpawnObjectNode class30_sub1 = (SpawnObjectNode) aClass19_1179.reverseGetFirst(); class30_sub1 != null; class30_sub1 = (SpawnObjectNode) aClass19_1179.reverseGetNext())
            {
                if (class30_sub1.anInt1294 > 0)
                    class30_sub1.anInt1294--;
                if (class30_sub1.anInt1294 == 0)
                {
                    if (class30_sub1.anInt1299 < 0 || Region.isObjectCachedType(class30_sub1.anInt1299, class30_sub1.anInt1301))
                    {
                        method142(class30_sub1.anInt1298, class30_sub1.anInt1295, class30_sub1.anInt1300, class30_sub1.anInt1301, class30_sub1.anInt1297, class30_sub1.anInt1296, class30_sub1.anInt1299);
                        class30_sub1.unlink();
                    }
                }
                else
                {
                    if (class30_sub1.anInt1302 > 0)
                        class30_sub1.anInt1302--;
                    if (class30_sub1.anInt1302 == 0 && class30_sub1.anInt1297 >= 1 && class30_sub1.anInt1298 >= 1 && class30_sub1.anInt1297 <= 102 && class30_sub1.anInt1298 <= 102 && (class30_sub1.anInt1291 < 0 || Region.isObjectCachedType(class30_sub1.anInt1291, class30_sub1.anInt1293)))
                    {
                        method142(class30_sub1.anInt1298, class30_sub1.anInt1295, class30_sub1.anInt1292, class30_sub1.anInt1293, class30_sub1.anInt1297, class30_sub1.anInt1296, class30_sub1.anInt1291);
                        class30_sub1.anInt1302 = -1;
                        if (class30_sub1.anInt1291 == class30_sub1.anInt1299 && class30_sub1.anInt1299 == -1)
                            class30_sub1.unlink();
                        else if (class30_sub1.anInt1291 == class30_sub1.anInt1299 && class30_sub1.anInt1292 == class30_sub1.anInt1300 && class30_sub1.anInt1293 == class30_sub1.anInt1301)
                            class30_sub1.unlink();
                        passedRequest = true;
                    }
                }
                if (passedRequest)
                    renderMapScene(floor_level);
            }
        }
    }

    private void determineMenuSize()
    {
        int i = chatText.getTextWidth("Choose Option");
        for (int j = 0; j < menuActionRow; j++)
        {
            int k = chatText.getTextWidth(menuActionName[j]);
            if (k > i)
                i = k;
        }
        i += 8;
        int l = 15 * menuActionRow + 21;
        if (super.saveClickX >= 0 && super.saveClickY >= 0 && super.saveClickX <= (clientSize == CLIENT_FIXED ? 765 : clientWidth) && super.saveClickY <= (clientSize == CLIENT_FIXED ? 503 : clientHeight))
        {
            int i1 = super.saveClickX - i / 2;
            if (i1 + i > (clientSize == CLIENT_FIXED ? 765 : clientWidth))
            {
                i1 = (clientSize == CLIENT_FIXED ? 760 : clientWidth) - i;
            }
            if (i1 < 0)
            {
                i1 = 0;
            }
            int l1 = super.saveClickY - (clientSize == CLIENT_FIXED ? 4 : 0);
            if (l1 + l > (clientSize == CLIENT_FIXED ? 503 : clientHeight))
            {
                l1 = (clientSize == CLIENT_FIXED ? 497 : clientHeight - 2) - l;
            }
            if (l1 < 0)
            {
                l1 = 0;
            }
            menuOpen = true;
            menuOffsetX = i1;
            menuOffsetY = l1;
            menuWidth = i;
            menuHeight = 15 * menuActionRow + 22;
        }
    }

    /**
     * Joshua Barry
     * 
     * @param stream
     */
    public void movePlayer(RSBuffer stream)
    {
        stream.initBitAccess();
        int j = stream.readBits(1);
        if (j == 0)
            return;
        int movementType = stream.readBits(2); // was int k
        if (movementType == 0)
        { // standing still.
            anIntArray894[anInt893++] = myPlayerIndex;
            return;
        }
        if (movementType == 1)
        { // walking.
            int direction = stream.readBits(3); //
            myPlayer.move(false, direction);
            int moved = stream.readBits(1); //
            if (moved == 1)
                anIntArray894[anInt893++] = myPlayerIndex;
            return;
        }
        if (movementType == 2)
        { // running.
            int dir = stream.readBits(3);
            myPlayer.move(true, dir);
            int moveDir = stream.readBits(3);
            myPlayer.move(true, moveDir);
            int moved = stream.readBits(1);
            if (moved == 1)
                anIntArray894[anInt893++] = myPlayerIndex;
            return;
        }
        if (movementType == 3)
        { // teleporting, moves presumably larger coords
          // and changes map region.
            floor_level = stream.readBits(2);
            int movable = stream.readBits(1);// used as a flag
            int moved = stream.readBits(1); // used as a flag.
            if (moved == 1)
                anIntArray894[anInt893++] = myPlayerIndex;
            int yCoord = stream.readBits(7);
            int xCoord = stream.readBits(7);
            myPlayer.setPos(xCoord, yCoord, movable == 1);
        }
    }

    private boolean method119(int i, int j)
    {
        boolean flag1 = false;
        RSInterface rsInterface = RSInterface.interfaceCache[j];
        for (int k = 0; k < rsInterface.children.length; k++)
        {
            if (rsInterface.children[k] == -1)
                break;
            RSInterface class9_1 = RSInterface.interfaceCache[rsInterface.children[k]];
            if (class9_1.type == 1)
                flag1 |= method119(i, class9_1.id);
            if (class9_1.type == 6 && (class9_1.disabledAnimation != -1 || class9_1.enabledAnimation != -1))
            {
                boolean flag2 = interfaceIsSelected(class9_1);
                int l;
                if (flag2)
                    l = class9_1.enabledAnimation;
                else
                    l = class9_1.disabledAnimation;
                if (l != -1)
                {
                    Sequence animation = Sequence.anims[l];
                    for (class9_1.duration += i; class9_1.duration > animation.getFrameLength(class9_1.animFrame);)
                    {
                        class9_1.duration -= animation.getFrameLength(class9_1.animFrame) + 1;
                        class9_1.animFrame++;
                        if (class9_1.animFrame >= animation.anInt352)
                        {
                            class9_1.animFrame -= animation.anInt356;
                            if (class9_1.animFrame < 0 || class9_1.animFrame >= animation.anInt352)
                                class9_1.animFrame = 0;
                        }
                        flag1 = true;
                    }
                }
            }
        }
        return flag1;
    }

    private int method120()
    {
        int j = 3;
        if (yCameraCurve < 310)
        {
            int k = xCameraPos >> 7;
            int l = yCameraPos >> 7;
            int i1 = myPlayer.x >> 7;
            int j1 = myPlayer.y >> 7;
            if ((byteGroundArray[floor_level][k][l] & 4) != 0)
                j = floor_level;
            int k1;
            if (i1 > k)
                k1 = i1 - k;
            else
                k1 = k - i1;
            int l1;
            if (j1 > l)
                l1 = j1 - l;
            else
                l1 = l - j1;
            if (k1 > l1)
            {
                int i2 = (l1 * 0x10000) / k1;
                int k2 = 32768;
                while (k != i1)
                {
                    if (k < i1)
                        k++;
                    else if (k > i1)
                        k--;
                    if ((byteGroundArray[floor_level][k][l] & 4) != 0)
                        j = floor_level;
                    k2 += i2;
                    if (k2 >= 0x10000)
                    {
                        k2 -= 0x10000;
                        if (l < j1)
                            l++;
                        else if (l > j1)
                            l--;
                        if ((byteGroundArray[floor_level][k][l] & 4) != 0)
                            j = floor_level;
                    }
                }
            }
            else
            {
                int j2 = (k1 * 0x10000) / l1;
                int l2 = 32768;
                while (l != j1)
                {
                    if (l < j1)
                        l++;
                    else if (l > j1)
                        l--;
                    if ((byteGroundArray[floor_level][k][l] & 4) != 0)
                        j = floor_level;
                    l2 += j2;
                    if (l2 >= 0x10000)
                    {
                        l2 -= 0x10000;
                        if (k < i1)
                            k++;
                        else if (k > i1)
                            k--;
                        if ((byteGroundArray[floor_level][k][l] & 4) != 0)
                            j = floor_level;
                    }
                }
            }
        }
        if ((byteGroundArray[floor_level][myPlayer.x >> 7][myPlayer.y >> 7] & 4) != 0)
            j = floor_level;
        return j;
    }

    private int method121()
    {
        int j = method42(floor_level, yCameraPos, xCameraPos);
        if (j - zCameraPos < 800 && (byteGroundArray[floor_level][xCameraPos >> 7][yCameraPos >> 7] & 4) != 0)
            return floor_level;
        else
            return 3;
    }

    public void delIgnore(long l)
    {
        try
        {
            if (l == 0L)
                return;
            for (int j = 0; j < ignoreCount; j++)
                if (ignoreListAsLongs[j] == l)
                {
                    ignoreCount--;
                    needDrawTabArea = true;
                    System.arraycopy(ignoreListAsLongs, j + 1, ignoreListAsLongs, j, ignoreCount - j);
                    stream.writeOpcode(74);
                    stream.writeLong(l);
                    return;
                }
            return;
        }
        catch (RuntimeException runtimeexception)
        {
            System.err.println("47229, " + 3 + ", " + l + ", " + runtimeexception.toString());
        }
        throw new RuntimeException();
    }

    private int extractInterfaceValues(RSInterface class9, int j)
    {
        if (class9.valueIndexArray == null || j >= class9.valueIndexArray.length)
            return -2;
        try
        {
            int ai[] = class9.valueIndexArray[j];
            int k = 0;
            int l = 0;
            int i1 = 0;
            do
            {
                int j1 = ai[l++];
                int k1 = 0;
                byte byte0 = 0;
                if (j1 == 0)
                    return k;
                if (j1 == 1)
                    k1 = currentStats[ai[l++]];
                if (j1 == 2)
                    k1 = maxStats[ai[l++]];
                if (j1 == 3)
                    k1 = currentExp[ai[l++]];
                if (j1 == 4)
                {
                    RSInterface class9_1 = RSInterface.interfaceCache[ai[l++]];
                    int k2 = ai[l++];
                    for (int j3 = 0; j3 < class9_1.inv.length; j3++)
                        if (class9_1.inv[j3] == k2 + 1)
                            k1 += class9_1.invStackSizes[j3];
                }
                if (j1 == 5)
                    k1 = variousSettings[ai[l++]];
                if (j1 == 6)
                    k1 = anIntArray1019[maxStats[ai[l++]] - 1];
                if (j1 == 7)
                    k1 = (variousSettings[ai[l++]] * 100) / 46875;
                if (j1 == 8)
                    k1 = myPlayer.combatLevel;
                if (j1 == 9)
                {
                    for (int l1 = 0; l1 < Skills.skillsCount; l1++)
                        k1 += maxStats[l1];
                }
                if (j1 == 10)
                {
                    RSInterface class9_2 = RSInterface.interfaceCache[ai[l++]];
                    int l2 = ai[l++] + 1;
                    for (int k3 = 0; k3 < class9_2.inv.length; k3++)
                    {
                        if (class9_2.inv[k3] != l2)
                            continue;
                        k1 = Integer.MAX_VALUE;
                        return k1;
                    }
                }
                if (j1 == 11)
                    k1 = energy;
                if (j1 == 12)
                    k1 = weight;
                if (j1 == 13)
                {
                    int i2 = variousSettings[ai[l++]];
                    int i3 = ai[l++];
                    k1 = (i2 & 1 << i3) == 0 ? 0 : 1;
                }
                if (j1 == 14)
                {
                    int j2 = ai[l++];
                    VarBit varBit = VarBit.cache[j2];
                    int l3 = varBit.configId;
                    int i4 = varBit.leastSignificantBit;
                    int j4 = varBit.mostSignificantBit;
                    int k4 = anIntArray1232[j4 - i4];
                    k1 = variousSettings[l3] >> i4 & k4;
                }
                if (j1 == 15)
                    byte0 = 1;
                if (j1 == 16)
                    byte0 = 2;
                if (j1 == 17)
                    byte0 = 3;
                if (j1 == 18)
                    k1 = (myPlayer.x >> 7) + baseX;
                if (j1 == 19)
                    k1 = (myPlayer.y >> 7) + baseY;
                if (j1 == 20)
                    k1 = ai[l++];
                if (byte0 == 0)
                {
                    if (i1 == 0)
                        k += k1;
                    if (i1 == 1)
                        k -= k1;
                    if (i1 == 2 && k1 != 0)
                        k /= k1;
                    if (i1 == 3)
                        k *= k1;
                    i1 = 0;
                }
                else
                {
                    i1 = byte0;
                }
            }
            while (true);
        }
        catch (Exception _ex)
        {
            return -1;
        }
    }

    public void drawTooltip()
    {
        if (menuActionRow < 2 && itemSelected == 0 && spellSelected == 0)
            return;
        String s;
        if (itemSelected == 1 && menuActionRow < 2)
            s = "Use @lre@" + selectedItemName + " @whi@->";
        else if (spellSelected == 1 && menuActionRow < 2)
            s = spellTooltip + " ";
        else
            s = menuActionName[menuActionRow - 1];
        if (menuActionRow > 2)
            s = s + "@whi@ / " + (menuActionRow - 2) + " more options";
        chatText.method390(4, 0xffffff, s, 1, 15);
    }

    public void drawMinimap()
    {
        if (clientSize == CLIENT_FIXED)
            mapAreaImageProducer.initDrawingArea();

        if (hideMinimap == 2)
        {
            mapArea[(clientSize == CLIENT_FIXED ? 1 : 3)].drawSprite((clientSize == CLIENT_FIXED ? 0 : clientWidth - 238), (clientSize == CLIENT_FIXED ? 0 : 3));
            if (menuOpen && clientSize == CLIENT_FIXED)
                drawMenu(516, 0);
            compass.method352(33, viewRotation, anIntArray1057, 256, anIntArray968, 25, (clientSize == CLIENT_FIXED ? 4 : 4 + 3), (clientSize == CLIENT_FIXED ? 34 - 5 : clientWidth + 34 - 5 - 238), 33, 25);
            gameScreenImageProducer.initDrawingArea();
            return;
        }

        int i = viewRotation + minimapRotation & 0x7ff;
        int j = 48 + myPlayer.x / 32;
        int l2 = 464 - myPlayer.y / 32;
        miniMap.method352(151, i, anIntArray1229, 256 + minimapZoom, anIntArray1052, l2, (clientSize == CLIENT_FIXED ? 9 : 12), (clientSize == CLIENT_FIXED ? 59 - 5 : clientWidth + 59 - 5 - 238), 146, j);
        /**
         * Map area editing
         */
        for (int j5 = 0; j5 < anInt1071; j5++)
        {
            int k = (anIntArray1072[j5] * 4 + 2) - myPlayer.x / 32;
            int i3 = (anIntArray1073[j5] * 4 + 2) - myPlayer.y / 32;
            markMinimap(aClass30_Sub2_Sub1_Sub1Array1140[j5], k, i3);
        }
        for (int k5 = 0; k5 < 104; k5++)
        {
            for (int l5 = 0; l5 < 104; l5++)
            {
                Deque class19 = groundArray[floor_level][k5][l5];
                if (class19 != null)
                {
                    int l = (k5 * 4 + 2) - myPlayer.x / 32;
                    int j3 = (l5 * 4 + 2) - myPlayer.y / 32;
                    markMinimap(mapDotItem, l, j3);
                }
            }
        }
        for (int i6 = 0; i6 < npcCount; i6++)
        {
            NPC npc = npcArray[npcIndices[i6]];
            if (npc != null && npc.isVisible())
            {
                NpcDefintion entityDef = npc.desc;
                if (entityDef.childrenIDs != null)
                    entityDef = entityDef.method161();
                if (entityDef != null && entityDef.displayMapIcon && entityDef.aBoolean84)
                {
                    int i1 = npc.x / 32 - myPlayer.x / 32;
                    int k3 = npc.y / 32 - myPlayer.y / 32;
                    markMinimap(mapDotNPC, i1, k3);
                }
            }
        }
        for (int j6 = 0; j6 < playerCount; j6++)
        {
            Player player = playerArray[playerIndices[j6]];
            if (player != null && player.isVisible())
            {
                int j1 = player.x / 32 - myPlayer.x / 32;
                int l3 = player.y / 32 - myPlayer.y / 32;
                boolean flag1 = false;
                long l6 = TextClass.longForName(player.name);
                for (int k6 = 0; k6 < friendsCount; k6++)
                {
                    if (l6 != friendsListAsLongs[k6] || friendsNodeIDs[k6] == 0)
                        continue;
                    flag1 = true;
                    break;
                }
                boolean flag2 = false;
                if (myPlayer.team != 0 && player.team != 0 && myPlayer.team == player.team)
                    flag2 = true;
                if (flag1)
                    markMinimap(mapDotFriend, j1, l3);
                else if (flag2)
                    markMinimap(mapDotTeam, j1, l3);
                else
                    markMinimap(mapDotPlayer, j1, l3);
            }
        }
        if (hintType != 0 && loopCycle % 20 < 10)
        {
            if (hintType == 1 && hintArrowNPCID >= 0 && hintArrowNPCID < npcArray.length)
            {
                NPC npc = npcArray[hintArrowNPCID];
                if (npc != null)
                {
                    int x = npc.x / 32 - myPlayer.x / 32;
                    int y = npc.y / 32 - myPlayer.y / 32;
                    markRedFlag(mapMarker, y, x);
                }
            }
            if (hintType == 2)
            {
                int x = ((anInt934 - baseX) * 4 + 2) - myPlayer.x / 32;
                int y = ((anInt935 - baseY) * 4 + 2) - myPlayer.y / 32;
                markRedFlag(mapMarker, y, x);
            }
            if (hintType == 10 && hintArrowPlayerID >= 0 && hintArrowPlayerID < playerArray.length)
            {
                Player plr = playerArray[hintArrowPlayerID];
                if (plr != null)
                {
                    int x = plr.x / 32 - myPlayer.x / 32;
                    int y = plr.y / 32 - myPlayer.y / 32;
                    markRedFlag(mapMarker, y, x);
                }
            }
        }
        if (destX != 0)
        {
            int x = (destX * 4 + 2) - myPlayer.x / 32;
            int y = (destY * 4 + 2) - myPlayer.y / 32;
            markMinimap(mapFlag, x, y);
        }
        RSRaster.drawPixels(3, (clientSize == CLIENT_FIXED ? 9 : 12) + 74, (clientSize == CLIENT_FIXED ? 130 - 5 : clientWidth + 130 - 5 - 238), 0xFFFFFF, 3);
        mapArea[(clientSize == CLIENT_FIXED ? 0 : 2)].drawSprite((clientSize == CLIENT_FIXED ? 0 : clientWidth - 238), (clientSize == CLIENT_FIXED ? 0 : 3));

        compass.method352(33, viewRotation, anIntArray1057, 256, anIntArray968, 25, (clientSize == CLIENT_FIXED ? 4 : 4 + 3), (clientSize == CLIENT_FIXED ? 34 - 5 : clientWidth + 34 - 5 - 238), 33, 25);

        if (menuOpen && clientSize == CLIENT_FIXED)
            drawMenu(516, 0);
        if (clientSize == CLIENT_FIXED)
            gameScreenImageProducer.initDrawingArea();
    }

    public void npcScreenPos(Mobile entity, int i)
    {
        calcEntityScreenPos(entity.x, i, entity.y);
    }

    public void calcEntityScreenPos(int i, int j, int l)
    {
        if (i < 128 || l < 128 || i > 13056 || l > 13056)
        {
            spriteDrawX = -1;
            spriteDrawY = -1;
            return;
        }
        int i1 = method42(floor_level, l, i) - j;
        i -= xCameraPos;
        i1 -= zCameraPos;
        l -= yCameraPos;
        int j1 = Model.SINE[yCameraCurve];
        int k1 = Model.COSINE[yCameraCurve];
        int l1 = Model.SINE[xCameraCurve];
        int i2 = Model.COSINE[xCameraCurve];
        int j2 = l * l1 + i * i2 >> 16;
        l = l * i2 - i * l1 >> 16;
        i = j2;
        j2 = i1 * k1 - l * j1 >> 16;
        l = i1 * j1 + l * k1 >> 16;
        i1 = j2;
        if (l >= 50)
        {
            spriteDrawX = Rasterizer.textureInt1 + (i << 9) / l;
            spriteDrawY = Rasterizer.textureInt2 + (i1 << 9) / l;
        }
        else
        {
            spriteDrawX = -1;
            spriteDrawY = -1;
        }
    }

    public void buildSplitPrivateChatMenu()
    {
        if (splitPrivateChat == 0)
            return;
        int i = 0;
        if (systemUpdateTime != 0)
            i = 1;
        for (int j = 0; j < 100; j++)
            if (chatMessages[j] != null)
            {
                int k = chatTypes[j];
                String s = chatNames[j];
                if (s != null && s.startsWith("@cr"))
                    s = s.substring(5);
                if ((k == 3 || k == 7) && (k == 7 || chatTabMode[3] == 0 || chatTabMode[3] == 1 && isFriendOrSelf(s)))
                {
                    int l = (clientSize == CLIENT_FIXED ? 326 : (chatHidden ? clientHeight - 37 : clientHeight - 177)) - i * 13;
                    if (super.mouseX > 4 && super.mouseY - 4 > l - 10 && super.mouseY - 4 <= l + 3)
                    {
                        int i1 = regularText.getTextWidth("From:  " + s + chatMessages[j]) + 25;
                        if (i1 > 450)
                            i1 = 450;
                        if (super.mouseX < 4 + i1)
                        {
                            if (myPrivilege >= 1)
                            {
                                menuActionName[menuActionRow] = "Report abuse @whi@" + capitalize(s);
                                menuActionID[menuActionRow] = 2606;
                                menuActionRow++;
                            }
                            menuActionName[menuActionRow] = "Add ignore @whi@" + capitalize(s);
                            menuActionID[menuActionRow] = 2042;
                            menuActionRow++;
                            menuActionName[menuActionRow] = "Add friend @whi@" + capitalize(s);
                            menuActionID[menuActionRow] = 2337;
                            menuActionRow++;
                            menuActionName[menuActionRow] = "Reply to @whi@" + capitalize(s);
                            menuActionID[menuActionRow] = 639;
                            menuActionRow++;
                        }
                    }
                    if (++i >= 5)
                        return;
                }
                if ((k == 5 || k == 6) && chatTabMode[3] < 2 && ++i >= 5)
                    return;
            }
    }

    public void method130(int j, int k, int l, int i1, int j1, int k1, int l1, int i2, int j2)
    {
        SpawnObjectNode class30_sub1 = null;
        for (SpawnObjectNode class30_sub1_1 = (SpawnObjectNode) aClass19_1179.reverseGetFirst(); class30_sub1_1 != null; class30_sub1_1 = (SpawnObjectNode) aClass19_1179.reverseGetNext())
        {
            if (class30_sub1_1.anInt1295 != l1 || class30_sub1_1.anInt1297 != i2 || class30_sub1_1.anInt1298 != j1 || class30_sub1_1.anInt1296 != i1)
                continue;
            class30_sub1 = class30_sub1_1;
            break;
        }
        if (class30_sub1 == null)
        {
            class30_sub1 = new SpawnObjectNode();
            class30_sub1.anInt1295 = l1;
            class30_sub1.anInt1296 = i1;
            class30_sub1.anInt1297 = i2;
            class30_sub1.anInt1298 = j1;
            method89(class30_sub1);
            aClass19_1179.insertHead(class30_sub1);
        }
        class30_sub1.anInt1291 = k;
        class30_sub1.anInt1293 = k1;
        class30_sub1.anInt1292 = l;
        class30_sub1.anInt1302 = j2;
        class30_sub1.anInt1294 = j;
    }

    private boolean interfaceIsSelected(RSInterface class9)
    {
        if (class9.valueCompareType == null)
            return false;
        for (int i = 0; i < class9.valueCompareType.length; i++)
        {
            int j = extractInterfaceValues(class9, i);
            int k = class9.requiredValues[i];
            //System.out.println("Extract: "+j +" - Required: "+k);
            if (class9.valueCompareType[i] == 2)
            {
                if (j >= k)
                    return false;
            }
            else if (class9.valueCompareType[i] == 3)
            {
                if (j <= k)
                    return false;
            }
            else if (class9.valueCompareType[i] == 4)
            {
                if (j == k)
                    return false;
            }
            else if (j != k)
                return false;
        }
        return true;
    }

    private DataInputStream openJagGrabInputStream(String s) throws IOException
    {
        if (aSocket832 != null)
        {
            try
            {
                aSocket832.close();
            }
            catch (Exception _ex)
            {
            }
            aSocket832 = null;
        }
        aSocket832 = openSocket(43595);
        aSocket832.setSoTimeout(10000);
        java.io.InputStream inputstream = aSocket832.getInputStream();
        OutputStream outputstream = aSocket832.getOutputStream();
        outputstream.write(("JAGGRAB /" + s + "\n\n").getBytes());
        return new DataInputStream(inputstream);
    }

    public void method134(RSBuffer stream)
    {
        int j = stream.readBits(8);
        if (j < playerCount)
        {
            for (int k = j; k < playerCount; k++)
                anIntArray840[anInt839++] = playerIndices[k];
        }
        if (j > playerCount)
        {
            System.err.println(myUsername + " Too many players");
            throw new RuntimeException("eek");
        }
        playerCount = 0;
        for (int l = 0; l < j; l++)
        {
            int i1 = playerIndices[l];
            Player player = playerArray[i1];
            int j1 = stream.readBits(1);
            if (j1 == 0)
            {
                playerIndices[playerCount++] = i1;
                player.anInt1537 = loopCycle;
            }
            else
            {
                int k1 = stream.readBits(2);
                if (k1 == 0)
                {
                    playerIndices[playerCount++] = i1;
                    player.anInt1537 = loopCycle;
                    anIntArray894[anInt893++] = i1;
                }
                else if (k1 == 1)
                {
                    playerIndices[playerCount++] = i1;
                    player.anInt1537 = loopCycle;
                    int l1 = stream.readBits(3);
                    player.move(false, l1);
                    int j2 = stream.readBits(1);
                    if (j2 == 1)
                        anIntArray894[anInt893++] = i1;
                }
                else if (k1 == 2)
                {
                    playerIndices[playerCount++] = i1;
                    player.anInt1537 = loopCycle;
                    int i2 = stream.readBits(3);
                    player.move(true, i2);
                    int k2 = stream.readBits(3);
                    player.move(true, k2);
                    int l2 = stream.readBits(1);
                    if (l2 == 1)
                        anIntArray894[anInt893++] = i1;
                }
                else if (k1 == 3)
                {
                    anIntArray840[anInt839++] = i1;
                } /*
                   * else if (k1 == 4) { //TODO Make it face AKZUJACOB
                   * player.turnDirection = 1024; //entity.turnDirection = 1536;
                   * //entity.turnDirection = 0; //entity.turnDirection = 512; }
                   */
            }
        }
    }

    public void sendMusic(int musicID)
    {
        // TODO Fixing up music later on
        if (1 + 1 == 2)
            return;
        int songId = musicID;
        if (songId == 65535)
            songId = -1;
        if (!music_enabled)
            return;
        if (songId != currentSong && previousSong == 0)
        {
            nextSong = songId;
            currentSong = songId;
            resourceProvider.method558(2, nextSong);
        }
    }

    public void startThemeMusic()
    {
        // TODO Fixing up music later on
        if (1 + 1 == 2)
            return;
        if (!music_enabled)
            return;
        if (themeMusic)
        {
            sendMusic(8);
        }
        else
        {
            SoundProvider.getInstance().fadeMidi(true);
        }
    }

    private static boolean objAnimFlag;

    public void drawLoginScreen(boolean flag)
    {
        resetImageProducers();
        loginMenuOverlay.drawIndexedImage(160, 100);
        RSRaster.fillPixels(649, 102, 32, 0xF62817, 459);
        RSRaster.fillPixels(650, 100, 30, 0xFF0000, 460);
        chatText.method382(0xffffff, 700, "Options", 479, false);
        atLoginMenu = true;
        loginScreen.initDrawingArea();
        
        loginBkg.drawSprite(0, 0);
        char c = '\u0168';
        char c1 = '\310';
        int x = 765;
        int y = 503;
        if (loginScreenState == 4)
        {
            newRegularFont.drawBasicString("Mouse X: " + super.mouseX, 5, 15, 0xFFFFFF, 0);
            newRegularFont.drawBasicString("Mouse Y: " + super.mouseY, 5, 30, 0xFFFFFF, 0);

            RSRaster.transparentBox(150, (y/2)-75, (x/2)-150, 0x000000, 300, 0, 100);
            newRegularFont.drawBasicString("Client Settings", (x/2)-33, (y/2)-55, 0xFFFFFF, 0);
            newRegularFont.drawBasicString("Option 1", (x/2)-60, (y/2), 0xFFFFFF, 0);
            newRegularFont.drawBasicString("Off", (x/2)+10, (y/2), 0xFF0000, 0);
            newRegularFont.drawBasicString("On", (x/2)+40, (y/2), 0x00FF00, 0);
            //chatText.method382(0xFFFFFF, (x/2), "S  E  T  T  I  N  G  S", (y/2), true);
            //chatText.method382(themeMusic ? 0x00FF00 : 0xFF0000, c / 2 + 220, "Theme music", 160 + 40, true);
            //chatText.method382(enterOnLogin ? 0x00FF00 : 0xFF0000, c / 2 + 220, "Enter key as hotkey to login", 160 + 55, true);
            //chatText.method382(lowMemory ? 0x00FF00 : 0xFF0000, c / 2 + 220, "Low memory usage", 160 + 70, true);
            //chatText.method382(0x00FF00, c / 2 + 220, "Screen Size: " + (clientSize == CLIENT_FIXED ? "Fixed" : (clientSize == 1 ? "Resizable" : "Fullscreen")), 160 + 70 + 15, true);
            //chatText.method382((server == "127.0.0.1" ? 0xFF0000 : 0x00FF00), c / 2 + 220, "Server: " + (server == "127.0.0.1" ? "Localhost" : "Live"), 160 + 70 + 15 + 15, true);
            //if (objAnimFlag)
                //chatText.method382(0xFFFFFF, c / 2 + 220, "[ Object animations, quality, textures ]", 160 + 70 + 65, true);
            newRegularFont.drawBasicString("Close", (x/2)+110, (y/2)-55, 0xFFFFFF, 0);
        } else if (loginScreenState == 2) {
            newRegularFont.drawBasicString("Mouse X: " + super.mouseX, 5, 15, 0xFFFFFF, 0);
            newRegularFont.drawBasicString("Mouse Y: " + super.mouseY, 5, 30, 0xFFFFFF, 0);
            for (Bubble bubble : bubbles) {
    			bubble.draw(Bubble.BUBBLES);
    		}

            RSRaster.transparentBox(22, 475, 5, 0x000000, 60, 100, 100);
            newRegularFont.drawBasicString("Settings", 10, 490, 0xFFFFFF, 5);
            RSRaster.transparentBox(22, 475, 71, 0x000000, 43, 100, 100);
            newRegularFont.drawBasicString("V. 1.0", 76, 490, 0xFFFFFF, 5);
            drawSlidingDrawer();
            //myBox.drawAdvancedSprite(200, 200);
            RSRaster.transparentBox(27, 300, 477, 0x000000, 279, 100, 100);
            newRegularFont.drawBasicString("Status: " + status, (x/2) + 100, (y/2) + 66, 0xFFFFFF, 5);
            RSRaster.transparentBox(164, 330, 477, 0x000000, 279, 100, 100);
            newRegularFont.drawBasicString("Username:", (x/2) + 100, (y/2) + 95, 0xFFFFFF, 5);
            newRegularFont.drawBasicString("Password:", (x/2) + 100, (y/2) + 145, 0xFFFFFF, 5);
            if (loginInput == 0) {
            	loginBox.drawSprite((x/2) + 100, (y/2) + 100);               
    		} else if (loginInput == 1) {
    			loginBoxHover.drawSprite((x/2) + 100, (y/2) + 100);
    		}
            if (passwordInput == 0) {
            	loginBox.drawSprite((x/2) + 100, (y/2) + 150);               
    		} else if (passwordInput == 1) {
    			loginBoxHover.drawSprite((x/2) + 100, (y/2) + 150);
    		}
            if (loginButtonInput == 0) {
            	loginButton.drawSprite((x/2) + 100, (y/2) + 200);            
    		} else if (loginButtonInput == 1) {
            	loginButtonHover.drawSprite((x/2) + 100, (y/2) + 200);
    		}
        	newRegularFont.drawBasicString(myUsername + ((loginScreenCursorPos == 0) & (loopCycle % 40 < 20) ? "<col=FFFFFF>|</col>" : ""), (x/2) + 105, (y/2) + 120, 0xffffff, -1);
        	newRegularFont.drawBasicString(TextClass.passwordAsterisks(myPassword) + ((loginScreenCursorPos == 1) & (loopCycle % 40 < 20) ? "<col=FFFFFF>|</col>" : ""), (x/2) + 105, (y/2) + 170, 0xffffff, -1);
        	/*
            int j = c1 / 2 - 40;
            if (loginMessage1.length() > 0)
            {
                chatText.method382(0xFF0000, c / 2 + 205, loginMessage1, j + 110 + 15, true);
                chatText.method382(0xFF0000, c / 2 + 205, loginMessage2, j + 110, true);
                j += 30;
            }
            else
            {
                chatText.method382(0xFF0000, c / 2 + 205, loginMessage2, j + 120, true);
                j += 30;
            }
            newBoldFont.drawBasicString("Username: " + myUsername + ((loginScreenCursorPos == 0) & (loopCycle % 40 < 20) ? "<col=FF0000>|</col>" : ""), c / 2 - 90 + 205, j + 120, 0xffffff, -1);
            j += 16;
            newBoldFont.drawBasicString("Password: " + TextClass.passwordAsterisks(myPassword) + ((loginScreenCursorPos == 1) & (loopCycle % 40 < 20) ? "<col=FF0000>|</col>" : ""), c / 2 - 89 + 205, j + 120, 0xffffff, -1);
            j += 15;
            if (!flag)
            {
                int i1 = c / 2 - 80 + 200;
                int l1 = c1 / 2 + 20 + 150;
                RSRaster.fillPixels(l1 - 21, 102, 32, 0xF62817, i1 - 46);
                RSRaster.fillPixels(l1 - 20, 100, 30, 0xFF0000, i1 - 45);
                chatText.method382(0xffffff, i1, "Login", l1 + 5, true);
                i1 = c / 2 + 310;
                RSRaster.fillPixels(l1 - 20 + 169 + 20, 102, 32, 0xF62817, c / 2 + 74);
                RSRaster.fillPixels(l1 - 20 + 170 + 20, 100, 30, 0xFF0000, c / 2 + 75);
                chatText.method382(0xffffff, i1, "Cancel", l1 + 5, true);
            } */
        }
        if (loginScreenState != 4) {
        	loginScreenState = 2;
        }
        loginScreen.drawGraphics(0, super.graphics, 0);
        welcomeScreenRaised = !welcomeScreenRaised;
    }
    
    private boolean boxSliding = false;
    private boolean boxIsOpen = false;
    private int boxHeight = 22;
	private int boxY = 274;
	
    private void drawSlidingDrawer() {
    	int moveSpeed = 10;
    	
    	if(boxSliding) {
    		if(boxIsOpen) {
    			if(boxHeight > 22) {
    				boxHeight -= moveSpeed;
    				boxY += moveSpeed;
    			} else {
    				arrow.mirrorVertical();
    				boxIsOpen = false;
    				boxSliding = false;
    			}
    		} else {
        		if(boxY > 15) {
        			boxHeight += moveSpeed;
        			boxY -= moveSpeed;
        		} else {
        			arrow.mirrorVertical();
        			boxIsOpen = true;
        			boxSliding = false;
        		}	
    		}
    	}
    	    	
        RSRaster.transparentBox(boxHeight, boxY, 477, 0x000000, 279, 100, 100);
        arrow.drawAdvancedSprite(600, boxY-6);
        
    	if(boxIsOpen) {
    		if(updateFeed != null) {
    			drawUpdateText();
    		}
    	}
    }
    
    private void fetchUpdates() {
    	new Thread(new Runnable() {
    		
			@Override
			public void run() {
		    	try {
					URL url = new URL("http://www.backcraft.netai.net/fetch.php");	
					InputStream is = url.openStream();
					String input = null;
					try {
						BufferedReader reader = new BufferedReader(new InputStreamReader(is));
						input = reader.readLine();
					} finally {
						Gson gson = new Gson();
						if(input != null)
							updateFeed = gson.fromJson(input, UpdateFeed.class);
						is.close();
						updatesFetched = true;
					}
					
				} catch (MalformedURLException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
    	}).start();
    }
    
    private void drawUpdateText() {
    	int space = 60;
    	List<Update> updates = updateFeed.getFeed();
    	for(int i=0; i<updates.size(); i++) {
    		Update update = updates.get(i);
    		int title = 60+(space*i);
    		newRegularFont.drawBasicString(update.getDate(), 490, title);
    		newRegularFont.drawBasicString(update.getData(), 490, title+20);
    	}
    }

    public void raiseWelcomeScreen()
    {
        welcomeScreenRaised = true;
    }

    /**
     * 
     * @param buffer The packet buffer.
     * @param uid The packet UID
     */
    public void parsePacketGroup(RSBuffer buffer, int uid)
    {
        /**
         * Edit grounditem amount
         */
        if (uid == 84)
        {
            int k = buffer.readUByte();
            int x = bigRegionX + (k >> 4 & 7);
            int y = bigRegionY + (k & 7);
            int l8 = buffer.readUShort();
            int l13 = buffer.readUShort();
            if (x >= 0 && y >= 0 && x < 104 && y < 104)
            {
                Deque deque = groundArray[floor_level][x][y];
                if (deque != null)
                {
                    for (Item item = (Item) deque.reverseGetFirst(); item != null; item = (Item) deque.reverseGetNext())
                    {
                        if (item.ID != (l8 & 0x7fff))
                            continue;
                        item.item_count = l13;
                        break;
                    }
                    spawnGroundItem(x, y);
                }
            }
            return;
        }
        /**
         * Play sound in location
         */
        if (uid == 105)
        {
            int l = buffer.readUByte();
            int k3 = bigRegionX + (l >> 4 & 7);
            int j6 = bigRegionY + (l & 7);
            int soundId = buffer.readUShort();
            int l11 = buffer.readUByte();
            int i14 = l11 >> 4 & 0xf;
            int i16 = l11 & 7;
            if (myPlayer.smallX[0] >= k3 - i14 && myPlayer.smallX[0] <= k3 + i14 && myPlayer.smallY[0] >= j6 - i14 && myPlayer.smallY[0] <= j6 + i14 && aBoolean848 && currentSound < 50)
            {
                sound[currentSound] = soundId;
                soundType[currentSound] = i16;
                // TODO: IF SOMETHING FUCKS UP COME HREE ITS SOUND!!!!!!
                soundVolume[currentSound] = 8;
                soundDelay[currentSound] = Sound.anIntArray326[soundId];
                currentSound++;
            }
        }
        /**
         * Something with groundItems Global? 104x104x4 region!
         */
        if (uid == 215)
        {
            int itm_id = buffer.method435();
            int offset_coord = buffer.method428();
            int k6 = bigRegionX + (offset_coord >> 4 & 7);
            int j9 = bigRegionY + (offset_coord & 7);
            int i12 = buffer.method435();
            int j14 = buffer.readUShort();
            if (k6 >= 0 && j9 >= 0 && k6 < 104 && j9 < 104 && i12 != playerID)
            {
                Item class30_sub2_sub4_sub2_2 = new Item();
                class30_sub2_sub4_sub2_2.ID = itm_id;
                class30_sub2_sub4_sub2_2.item_count = j14;
                if (groundArray[floor_level][k6][j9] == null)
                    groundArray[floor_level][k6][j9] = new Deque();
                groundArray[floor_level][k6][j9].insertHead(class30_sub2_sub4_sub2_2);
                spawnGroundItem(k6, j9);
            }
            return;
        }
        /**
         * Clear all grounditems
         */
        if (uid == 156)
        {
            int j1 = buffer.method426();
            int i4 = bigRegionX + (j1 >> 4 & 7);
            int l6 = bigRegionY + (j1 & 7);
            int k9 = buffer.readUShort();
            if (i4 >= 0 && l6 >= 0 && i4 < 104 && l6 < 104)
            {
                Deque class19 = groundArray[floor_level][i4][l6];
                if (class19 != null)
                {
                    for (Item item = (Item) class19.reverseGetFirst(); item != null; item = (Item) class19.reverseGetNext())
                    {
                        if (item.ID != (k9 & 0x7fff))
                            continue;
                        item.unlink();
                        break;
                    }
                    if (class19.reverseGetFirst() == null)
                        groundArray[floor_level][i4][l6] = null;
                    spawnGroundItem(i4, l6);
                }
            }
            return;
        }
        /**
         * Spawn wallobject? e.g level I guess
         */
        if (uid == 160)
        {
            int k1 = buffer.method428();
            int j4 = bigRegionX + (k1 >> 4 & 7);
            int i7 = bigRegionY + (k1 & 7);
            int l9 = buffer.method428();
            int j12 = l9 >> 2;
            int k14 = l9 & 3;
            int j16 = anIntArray1177[j12];
            int j17 = buffer.method435();
            if (j4 >= 0 && i7 >= 0 && j4 < 103 && i7 < 103)
            {
                int j18 = intGroundArray[floor_level][j4][i7];
                int i19 = intGroundArray[floor_level][j4 + 1][i7];
                int l19 = intGroundArray[floor_level][j4 + 1][i7 + 1];
                int k20 = intGroundArray[floor_level][j4][i7 + 1];
                if (j16 == 0)
                {
                    WallObject class10 = sceneGraph.getWallObject(floor_level, j4, i7);
                    if (class10 != null)
                    {
                        int k21 = class10.uid >> 14 & 0x7fff;
                        if (j12 == 2)
                        {
                            class10.cacheNode = new GameObject(k21, 4 + k14, 2, i19, l19, j18, k20, j17, false);
                            class10.node2 = new GameObject(k21, k14 + 1 & 3, 2, i19, l19, j18, k20, j17, false);
                        }
                        else
                        {
                            class10.cacheNode = new GameObject(k21, k14, j12, i19, l19, j18, k20, j17, false);
                        }
                    }
                }
                if (j16 == 1)
                {
                    WallDecoration class26 = sceneGraph.getWallDecoration(j4, i7, floor_level);
                    if (class26 != null)
                        class26.decorationNode = new GameObject(class26.uid >> 14 & 0x7fff, 0, 4, i19, l19, j18, k20, j17, false);
                }
                if (j16 == 2)
                {
                    InteractiveObject class28 = sceneGraph.getInteractableObject(j4, i7, floor_level);
                    if (j12 == 11)
                        j12 = 10;
                    if (class28 != null)
                        class28.jagexNode = new GameObject(class28.uid >> 14 & 0x7fff, k14, j12, i19, l19, j18, k20, j17, false);
                }
                if (j16 == 3)
                {
                    GroundDecoration class49 = sceneGraph.getGroundDecoration(i7, j4, floor_level);
                    if (class49 != null)
                        class49.entity = new GameObject(class49.uid >> 14 & 0x7fff, k14, 22, i19, l19, j18, k20, j17, false);
                }
            }
            return;
        }
        /**
         * Spawn player as object??? fight pits
         */
        if (uid == 147)
        {
            int l1 = buffer.method428();
            int k4 = bigRegionX + (l1 >> 4 & 7);
            int j7 = bigRegionY + (l1 & 7);
            int i10 = buffer.readUShort();
            byte byte0 = buffer.method430();
            int l14 = buffer.method434();
            byte byte1 = buffer.method429();
            int k17 = buffer.readUShort();
            int k18 = buffer.method428();
            int j19 = k18 >> 2;
            int i20 = k18 & 3;
            int l20 = anIntArray1177[j19];
            byte byte2 = buffer.readByte();
            int l21 = buffer.readUShort();
            byte byte3 = buffer.method429();
            Player player;
            if (i10 == playerID)
                player = myPlayer;
            else
                player = playerArray[i10];
            if (player != null)
            {
                ObjectDefinition objDefinition = ObjectDefinition.forID(l21);
                int i22 = intGroundArray[floor_level][k4][j7];
                int j22 = intGroundArray[floor_level][k4 + 1][j7];
                int k22 = intGroundArray[floor_level][k4 + 1][j7 + 1];
                int l22 = intGroundArray[floor_level][k4][j7 + 1];
                Model model = objDefinition.generateModel(j19, i20, i22, j22, k22, l22, -1);
                if (model != null)
                {
                    method130(k17 + 1, -1, 0, l20, j7, 0, floor_level, k4, l14 + 1);
                    player.anInt1707 = l14 + loopCycle;
                    player.anInt1708 = k17 + loopCycle;
                    player.aModel_1714 = model;
                    int i23 = objDefinition.width;
                    int j23 = objDefinition.height;
                    if (i20 == 1 || i20 == 3)
                    {
                        i23 = objDefinition.height;
                        j23 = objDefinition.width;
                    }
                    // TODO: Localize??
                    player.anInt1711 = k4 * 128 + i23 * 64;
                    player.anInt1713 = j7 * 128 + j23 * 64;
                    player.anInt1712 = method42(floor_level, player.anInt1713, player.anInt1711);
                    if (byte2 > byte0)
                    {
                        byte byte4 = byte2;
                        byte2 = byte0;
                        byte0 = byte4;
                    }
                    if (byte3 > byte1)
                    {
                        byte byte5 = byte3;
                        byte3 = byte1;
                        byte1 = byte5;
                    }
                    player.anInt1719 = k4 + byte2;
                    player.anInt1721 = k4 + byte0;
                    player.anInt1720 = j7 + byte3;
                    player.anInt1722 = j7 + byte1;
                }
            }
        }
        /**
         * Object spawning?
         */
        if (uid == 151)
        {
            int i2 = buffer.method426();
            int l4 = bigRegionX + (i2 >> 4 & 7);
            int k7 = bigRegionY + (i2 & 7);
            int j10 = buffer.method434();
            int k12 = buffer.method428();
            int i15 = k12 >> 2;
            int k16 = k12 & 3;
            int l17 = anIntArray1177[i15];
            if (l4 >= 0 && k7 >= 0 && l4 < 104 && k7 < 104)
                method130(-1, j10, k16, l17, k7, i15, floor_level, l4, 0);
            return;
        }
        /**
         * Send stillgraphic!
         */
        if (uid == 4)
        {
            int j2 = buffer.readUByte();
            int i5 = bigRegionX + (j2 >> 4 & 7);
            int l7 = bigRegionY + (j2 & 7);
            int k10 = buffer.readUShort();
            int l12 = buffer.readUByte();
            int j15 = buffer.readUShort();
            if (i5 >= 0 && l7 >= 0 && i5 < 104 && l7 < 104)
            {
                i5 = i5 * 128 + 64;
                l7 = l7 * 128 + 64;
                StillGraphic stillGfx = new StillGraphic(floor_level, loopCycle, j15, k10, method42(floor_level, l7, i5) - l12, l7, i5);
                stillGraphicDeque.insertHead(stillGfx);
            }
            return;
        }
        /**
         * Grounditem spawning
         */
        if (uid == 44)
        {
            int itemId = buffer.method436();
            int itemCount = buffer.readUShort();
            int i8 = buffer.readUByte();
            int x = bigRegionX + (i8 >> 4 & 7);
            int y = bigRegionY + (i8 & 7);
            if (x >= 0 && y >= 0 && x < 104 && y < 104)
            {
                Item class30_sub2_sub4_sub2_1 = new Item();
                class30_sub2_sub4_sub2_1.ID = itemId;
                class30_sub2_sub4_sub2_1.item_count = itemCount;
                if (groundArray[floor_level][x][y] == null)
                    groundArray[floor_level][x][y] = new Deque();
                groundArray[floor_level][x][y].insertHead(class30_sub2_sub4_sub2_1);
                spawnGroundItem(x, y);
            }
            return;
        }
        /**
         * Object spawning again
         */
        if (uid == 101)
        {
            int l2 = buffer.method427();
            int k5 = l2 >> 2;
            int j8 = l2 & 3;
            int i11 = anIntArray1177[k5];
            int j13 = buffer.readUByte();
            int k15 = bigRegionX + (j13 >> 4 & 7);
            int l16 = bigRegionY + (j13 & 7);
            if (k15 >= 0 && l16 >= 0 && k15 < 104 && l16 < 104)
                method130(-1, -1, j8, i11, l16, k5, floor_level, k15, 0);
            return;
        }
        /**
         * Sends a globalProjectile
         */
        if (uid == 117)
        {
            int i3 = buffer.readUByte();
            int l5 = bigRegionX + (i3 >> 4 & 7);
            int k8 = bigRegionY + (i3 & 7);
            int j11 = l5 + buffer.readByte();
            int k13 = k8 + buffer.readByte();
            int l15 = buffer.readShort();
            int i17 = buffer.readUShort();
            int i18 = buffer.readUByte() * 4;
            int l18 = buffer.readUByte() * 4;
            int k19 = buffer.readUShort();
            int j20 = buffer.readUShort();
            int i21 = buffer.readUByte();
            int j21 = buffer.readUByte();
            if (l5 >= 0 && k8 >= 0 && l5 < 104 && k8 < 104 && j11 >= 0 && k13 >= 0 && j11 < 104 && k13 < 104 && i17 != 65535)
            {
                l5 = l5 * 128 + 64;
                k8 = k8 * 128 + 64;
                j11 = j11 * 128 + 64;
                k13 = k13 * 128 + 64;
                Projectile class30_sub2_sub4_sub4 = new Projectile(i21, l18, k19 + loopCycle, j20 + loopCycle, j21, floor_level, method42(floor_level, k8, l5) - i18, k8, l5, l15, i17);
                class30_sub2_sub4_sub4.method455(k19 + loopCycle, k13, method42(floor_level, k13, j11) - l18, j11);
                aClass19_1013.insertHead(class30_sub2_sub4_sub4);
            }
        }
    }

    public void method139(RSBuffer stream)
    {
        stream.initBitAccess();
        int k = stream.readBits(8);
        if (k < npcCount)
        {
            for (int l = k; l < npcCount; l++)
                anIntArray840[anInt839++] = npcIndices[l];
        }
        if (k > npcCount)
        {
            System.err.println(myUsername + " Too many npcs");
            throw new RuntimeException("eek");
        }
        npcCount = 0;
        for (int i1 = 0; i1 < k; i1++)
        {
            int j1 = npcIndices[i1];
            NPC npc = npcArray[j1];
            int k1 = stream.readBits(1);
            if (k1 == 0)
            {
                npcIndices[npcCount++] = j1;
                npc.anInt1537 = loopCycle;
            }
            else
            {
                int l1 = stream.readBits(2);
                if (l1 == 0)
                {
                    npcIndices[npcCount++] = j1;
                    npc.anInt1537 = loopCycle;
                    anIntArray894[anInt893++] = j1;
                }
                else if (l1 == 1)
                {
                    npcIndices[npcCount++] = j1;
                    npc.anInt1537 = loopCycle;
                    int i2 = stream.readBits(3);
                    npc.move(false, i2);
                    int k2 = stream.readBits(1);
                    if (k2 == 1)
                        anIntArray894[anInt893++] = j1;
                }
                else if (l1 == 2)
                {
                    npcIndices[npcCount++] = j1;
                    npc.anInt1537 = loopCycle;
                    int j2 = stream.readBits(3);
                    npc.move(true, j2);
                    int l2 = stream.readBits(3);
                    npc.move(true, l2);
                    int i3 = stream.readBits(1);
                    if (i3 == 1)
                        anIntArray894[anInt893++] = j1;
                }
                else if (l1 == 3)
                    anIntArray840[anInt839++] = j1;
            }
        }
    }

    /**
     * Login Screen Methods
     */
    private void handleUsernameInput() {
    	//Clicking
    	if (super.clickMode3 == 1 && super.saveClickX >= 484 && super.saveClickX <= 750 && super.saveClickY >= 354 && super.saveClickY < 382) {
            loginScreenCursorPos = 0;
        }
    	
    	//Hovering
    	if (super.mouseX >= 484 && super.mouseX <= 750 && super.mouseY >= 354 && super.mouseY <= 382) {
        	loginInput = 1;
    	} else {
        	loginInput = 0;
    	}
    }
    
    private void handlePasswordInput() {
    	//Clicking
    	if (super.clickMode3 == 1 && super.saveClickX >= 484 && super.saveClickX <= 750 && super.saveClickY >= 404 && super.saveClickY < 433) {
            loginScreenCursorPos = 1;
        }
    	
    	//Hovering
    	if (super.mouseX >= 484 && super.mouseX <= 750 && super.mouseY >= 404 && super.mouseY <= 433) {
        	passwordInput = 1;
    	} else {
        	passwordInput = 0;
    	}
    }
    
    private void handleLoginButtonInput() {
    	//Clicking
    	 if (super.clickMode3 == 1 && super.saveClickX >= 482 && super.saveClickX <= 748 && super.saveClickY >= 452 && super.saveClickY <= 489) {
             loginFailures = 0;
             login(myUsername, myPassword, false);
             // for (int kk = 0; kk < 100; kk++)
             // login(myUsername+""+kk, myPassword, false);
             if (loggedIn)
                 return;
         }
    	
    	//Hovering
    	if (super.mouseX >= 482 && super.mouseX <= 748 && super.mouseY >= 452 && super.mouseY <= 489) {
        	loginButtonInput = 1;
    	} else {
        	loginButtonInput = 0;
    	}
    }
    
    private void handleSlidingDrawer() {
    	//Clicking
    	    	
    	int y = 268;
    	if(boxIsOpen)
    		y = 15;
    	
    	
    	if (super.clickMode3 == 1 && super.saveClickX >= 600 && super.saveClickX <= 600+arrow.myWidth && super.saveClickY >= y && super.saveClickY < y+arrow.myHeight) {
        	if(!updatesFetched) {
        		fetchUpdates();
        	}
    		boxSliding = !boxSliding;
        }
    }
    
    private void handleSettings() {
    	//Settings button click
    	if (super.clickMode3 == 1 && super.saveClickX >= 5 && super.saveClickX <= 61 && super.saveClickY >= 476 && super.saveClickY <= 499) {
            loginScreenState = 4;
        }
    	
    	//Close settings box
    	if (loginScreenState == 4 && super.clickMode3 == 1 && super.saveClickX >= 492 && super.saveClickX <= 522 && super.saveClickY >= 185 && super.saveClickY <= 196) {
    		loginScreenState = 0;
    	}
    }
    
    public void processLoginScreenInput()
    {
        if (atLoginMenu)
        {
            int x = super.myWidth / 2;
            int y = super.myHeight / 2;
            handleSettings();
            if (loginScreenState == 4)
            {
                if (super.clickMode3 == 1 && super.saveClickX >= x - 130 && super.saveClickX <= x + 163 && super.saveClickY >= y - 66 && super.saveClickY <= y - 49)
                {
                    SoundProvider.getInstance().fadeMidi(themeMusic);
                    themeMusic = !themeMusic;
                    try
                    {
                        writeSettings();
                    }
                    catch (IOException e)
                    {
                    }
                }
                else if (super.clickMode3 == 1 && super.saveClickX >= x - 130 && super.saveClickX <= x + 163 && super.saveClickY >= y - 66 + 15 && super.saveClickY <= y + 14 - 47)
                {
                    enterOnLogin = !enterOnLogin;
                    try
                    {
                        writeSettings();
                    }
                    catch (IOException e)
                    {
                    }
                }
                else if (super.clickMode3 == 1 && super.saveClickX >= x - 130 && super.saveClickX <= x + 163 && super.saveClickY >= y - 66 + 15 + 15 + 17 && super.saveClickY <= y + 15 + 15 + 15 - 47)
                {
                    toggleSize((clientSize + 1) % 3);
                }
                else if (super.clickMode3 == 1 && super.saveClickX >= x - 130 && super.saveClickX <= x + 163 && super.saveClickY >= y - 66 + 15 + 15 + 15 + 17 && super.saveClickY <= y + 15 + 15 + 15 + 15 - 47)
                {
                    //server = (server == "127.0.0.1" ? "5.135.166.154" : "127.0.0.1");
                	server = "178.117.87.243";
                }
                else if (super.clickMode3 == 1 && super.saveClickX >= x - 130 && super.saveClickX <= x + 163 && super.saveClickY >= y - 66 + 15 + 15 && super.saveClickY <= y + 15 + 15 - 47)
                {
                    lowMemory = !lowMemory;
                    if (lowMemory)
                        setLowMem();
                    else
                        setHighMem();
                    try
                    {
                        writeSettings();
                    }
                    catch (IOException e)
                    {
                    }
                }
                else if (super.mouseX >= x - 130 && super.mouseX <= x + 163 && super.mouseY >= y - 66 + 15 + 17 && super.mouseY <= y + 15 + 15 - 47)
                {
                    objAnimFlag = true;
                }
                else
                {
                    objAnimFlag = false;
                }
            }
            else
            {
                if (loginScreenState == 2)
                {
                    int i = super.myWidth / 2 - 80;
                    int j = super.myHeight / 2 - 40;
                    j += 30;
                    j += 25;
                    handleUsernameInput();
                    handlePasswordInput();
                    handleLoginButtonInput();
                    handleSlidingDrawer();
                    j += 15;
                    j += 15;
                    int i1 = super.myWidth / 2 - 80;
                    int k1 = super.myHeight / 2 + 50;
                    k1 += 20;
                    i1 = super.myWidth / 2 + 80;
                    if (super.clickMode3 == 1 && super.saveClickX >= i1 - 25 && super.saveClickX <= i1 + 79 && super.saveClickY >= k1 - 70 && super.saveClickY <= k1 - 32)
                    {
                        loginScreenState = 0;
                        myUsername = "Stephen";//akzuu
                        myPassword = "aids";
                    }
                    do
                    {
                        int l1 = readChar(-796);
                        if (l1 == -1)
                            break;
                        boolean flag1 = false;
                        for (int i2 = 0; i2 < validUserPassChars.length(); i2++)
                        {
                            if (l1 != validUserPassChars.charAt(i2) || (loginScreenCursorPos == 1 && l1 == (char) ' '))
                                continue;
                            flag1 = true;
                            break;
                        }
                        if (loginScreenCursorPos == 0)
                        {
                            if (l1 == 8 && myUsername.length() > 0)
                                myUsername = myUsername.substring(0, myUsername.length() - 1);
                            if (l1 == 9 || l1 == 10 || l1 == 13)
                                loginScreenCursorPos = 1;
                            if (flag1)
                                myUsername += (char) l1;
                            if (myUsername.length() > 12)
                                myUsername = myUsername.substring(0, 12);
                        }
                        else if (loginScreenCursorPos == 1)
                        {
                            if (l1 == 8 && myPassword.length() > 0)
                                myPassword = myPassword.substring(0, myPassword.length() - 1);
                            if (l1 == 9 || l1 == 10 || l1 == 13)
                            {
                                if (enterOnLogin)
                                {
                                    login(myUsername, myPassword, false);
                                    if (loggedIn)
                                        return;
                                }
                                else
                                    loginScreenCursorPos = 0;
                            }
                            if (flag1)
                                myPassword += (char) l1;
                            if (myPassword.length() > 20)
                                myPassword = myPassword.substring(0, 20);
                        }
                    }
                    while (true);
                    return;
                }
                if (loginScreenState == 3)
                {
                    int k = super.myWidth / 2;
                    int j1 = super.myHeight / 2 + 50;
                    j1 += 20;
                    if (super.clickMode3 == 1 && super.saveClickX >= k - 54 && super.saveClickX <= k + 49 && super.saveClickY >= j1 - 39 && super.saveClickY <= j1 - 5)
                    {
                        loginScreenState = 0;
                    }
                }
            }
        }
    }

    private void markMinimap(IndexedImage sprite, int i, int j)
    {
        int k = viewRotation + minimapRotation & 0x7ff;
        int l = sprite.myWidth + sprite.myHeight + i * i + j * j;
        if (l > 4850 && clientSize != 0)
            return;
        int sine = Model.SINE[k];
        int cosine = Model.COSINE[k];
        sine = (sine * 256) / (minimapZoom + 256);
        cosine = (cosine * 256) / (minimapZoom + 256);
        int k1 = j * sine + i * cosine >> 16;
        int l1 = j * cosine - i * sine >> 16;
        sprite.drawIndexedImage((((clientSize == CLIENT_FIXED ? 129 - 5 : clientWidth + 129 - 5 - 238) + k1) - sprite.maxWidth / 2) + 4, (clientSize == CLIENT_FIXED ? 88 : 88 + 3) - l1 - sprite.maxHeight / 2 - 4);
    }

    public void method142(int i, int j, int k, int l, int i1, int j1, int k1)
    {
        if (i1 >= 1 && i >= 1 && i1 <= 102 && i <= 102)
        {
            int i2 = 0;
            if (j1 == 0)
                i2 = sceneGraph.getWallObjectUID(j, i1, i);
            if (j1 == 1)
                i2 = sceneGraph.getWallDecorationUID(j, i1, i);
            if (j1 == 2)
                i2 = sceneGraph.getInteractiveObjectUID(j, i1, i);
            if (j1 == 3)
                i2 = sceneGraph.getGroundDecortionUID(j, i1, i);
            if (i2 != 0)
            {
                int i3 = sceneGraph.getTileArrayIdForPosition(j, i1, i, i2);
                int j2 = i2 >> 14 & 0x7fff;
                int k2 = i3 & 0x1f;
                int l2 = i3 >> 6;
                if (j1 == 0)
                {
                    sceneGraph.removeWallObject(i1, j, i, (byte) -119);
                    ObjectDefinition objDefinition = ObjectDefinition.forID(j2);
                    if (objDefinition.isSolid)
                        collision_maps[j].addClippingForVariableObject(l2, k2, objDefinition.isRangeable, i1, i);
                }
                if (j1 == 1)
                    sceneGraph.removeWallDecoration(i, j, i1);
                if (j1 == 2)
                {
                    sceneGraph.method293(j, i1, i);
                    ObjectDefinition objDefinition_1 = ObjectDefinition.forID(j2);
                    if (i1 + objDefinition_1.width > 103 || i + objDefinition_1.width > 103 || i1 + objDefinition_1.height > 103 || i + objDefinition_1.height > 103)
                        return;
                    if (objDefinition_1.isSolid)
                        collision_maps[j].addClippingForSolidObject(l2, objDefinition_1.width, i1, i, objDefinition_1.height, objDefinition_1.isRangeable);
                }
                if (j1 == 3)
                {
                    sceneGraph.removeGroundDecoration(j, i, i1);
                    ObjectDefinition objDefinition_2 = ObjectDefinition.forID(j2);
                    if (objDefinition_2.isSolid && objDefinition_2.hasActions)
                        collision_maps[j].method218(i, i1);
                }
            }
            if (k1 >= 0)
            {
                int j3 = j;
                if (j3 < 3 && (byteGroundArray[1][i1][i] & 2) == 2)
                    j3++;
                Region.addObject(sceneGraph, k, i, l, j3, collision_maps[j], intGroundArray, i1, k1, j);
            }
        }
    }

    private short[][] bonuses = new short[11791][13];

    private void loadEquipmentBonuses(CacheArchive archive)
    {
        try
        {
            RSBuffer stream = new RSBuffer(archive.getDataForName("equipment.dat"));
            do
            {
                int itemId = stream.readShort();
                for (int i2 = 0; i2 < 12; i2++)
                    bonuses[itemId][i2] = (short) stream.readShort();
            }
            while (stream.pointer < stream.buffer.length);
        }
        catch (Exception e)
        {
            System.err.println("ERROR 3833: File not found please report to AkZu.");
        }
    }

    public void refreshEquipment()
    {
        int flag2 = 0;
        int flag = 0;

        String BONUS_NAME[] =
        { "Stab:", "Slash:", "Crush:", "Magic:", "Ranged:", "Stab:", "Slash:", "Crush:", "Magic:", "Ranged:", "Strength:", "Prayer:" };

        // -- Clear the current strings..
        for (int i2 = 0; i2 < 12; i2++)
        {
            myPlayer.bonusAmount[i2] = 0;
            if (i2 == 10)
                flag2 = 1;
            sendString(BONUS_NAME[i2] + " +0", 1675 + i2 + flag2);
        }

        // -- Assign the new bonuses..
        for (int j = 0; j < 13; j++)
        {
            if (myPlayer.equipment[j] >= 512 && myPlayer.equipment[j] - 512 < ItemDefinition.totalItems)
            {
                try
                {
                    if (j != 6)
                        for (int i2 = 0; i2 < 12; i2++)
                        {
                            myPlayer.bonusAmount[i2] += bonuses[(myPlayer.equipment[j] - 512)][i2];
                            if (i2 == 10)
                                flag = 1;
                            sendString(BONUS_NAME[i2] + (myPlayer.bonusAmount[i2] < 0 ? " " : " +") + myPlayer.bonusAmount[i2], 1675 + i2 + flag);
                        }
                    flag = 0;
                    flag2 = 0;
                }
                catch (Exception e)
                {
                }
            }
        }
    }

    public void updatePlayers(int i, RSBuffer buffer)
    {
        anInt839 = 0;
        anInt893 = 0;
        movePlayer(buffer);
        method134(buffer);
        method91(buffer, i);
        method49(buffer);
        for (int k = 0; k < anInt839; k++)
        {
            int l = anIntArray840[k];
            if (playerArray[l].anInt1537 != loopCycle)
                playerArray[l] = null;
        }
        if (buffer.pointer != i)
        {
            System.err.println("Error packet size mismatch in getplayer pos:" + buffer.pointer + " psize:" + i);
            throw new RuntimeException("eek");
        }
        for (int i1 = 0; i1 < playerCount; i1++)
            if (playerArray[playerIndices[i1]] == null)
            {
                System.err.println(myUsername + " null entry in pl list - pos:" + i1 + " size:" + playerCount);
                throw new RuntimeException("eek");
            }
    }

    // joshua barry
    public void setCameraPos(int zoom, int yCurve, int l, int xCurve, int j1, int k1)
    {
        int l1 = 2048 - yCurve & 0x7ff;
        int i2 = 2048 - j1 & 0x7ff;
        int j2 = 0;
        int k2 = 0;
        int l2 = zoom;
        if (l1 != 0)
        {
            int i3 = Model.SINE[l1];
            int k3 = Model.COSINE[l1];
            int i4 = k2 * k3 - l2 * i3 >> 16;
            l2 = k2 * i3 + l2 * k3 >> 16;
            k2 = i4;
        }
        if (i2 != 0)
        {
            int sine = Model.SINE[i2];
            int cosine = Model.COSINE[i2];
            int j4 = l2 * sine + j2 * cosine >> 16;
            l2 = l2 * cosine - j2 * sine >> 16;
            j2 = j4;
        }
        xCameraPos = l - j2;
        zCameraPos = xCurve - k2;
        yCameraPos = k1 - l2;
        yCameraCurve = yCurve;
        xCameraCurve = j1;
    }

    /**
     * Applies a new string to a child interface.
     * 
     * @param str
     * @param i
     */
    public void sendString(String str, int i)
    {
        RSInterface.interfaceCache[i].disabledMessage = str;
        needDrawTabArea = true;
        inputTaken = true;
    }

    public void sendChatInterface(int interfaceID)
    {
        if (chatHidden)
            changeActiveChatStoneState(0);
        method60(interfaceID);
        if (invOverlayInterfaceID != -1)
        {
            invOverlayInterfaceID = -1;
            needDrawTabArea = true;
        }
        backDialogID = interfaceID;
        inputTaken = true;
        openInterfaceID = -1;
        aBoolean1149 = false;
    }

    public void sendFrame248(int interfaceID, int sideInterfaceID)
    {
        if (backDialogID != -1)
        {
            backDialogID = -1;
            inputTaken = true;
        }
        if (inputDialogState != 0)
        {
            inputDialogState = 0;
            inputTaken = true;
        }
        if (invHidden)
            invHidden = false;
        openInterfaceID = interfaceID;
        invOverlayInterfaceID = sideInterfaceID;
        needDrawTabArea = true;
        aBoolean1149 = false;
    }

    private boolean parsePacket()
    {
        if (socketStream == null)
            return false;
        try
        {
            int i = socketStream.available();
            if (i == 0)
                return false;
            if (opCode == -1)
            {
                socketStream.flushInputStream(inStream.buffer, 1);
                opCode = inStream.buffer[0] & 0xff;
                if (encryption != null)
                    opCode = opCode - encryption.getNextKey() & 0xff;
                pktSize = PacketSizes.packetSizes[opCode];
                i--;
            }
            if (pktSize == -1)
                if (i > 0)
                {
                    socketStream.flushInputStream(inStream.buffer, 1);
                    pktSize = inStream.buffer[0] & 0xff;
                    i--;
                }
                else
                {
                    return false;
                }
            if (pktSize == -2)
                if (i > 1)
                {
                    socketStream.flushInputStream(inStream.buffer, 2);
                    inStream.pointer = 0;
                    pktSize = inStream.readUShort();
                    i -= 2;
                }
                else
                {
                    return false;
                }
            if (i < pktSize)
                return false;
            inStream.pointer = 0;
            socketStream.flushInputStream(inStream.buffer, pktSize);
            timeoutCounter = 0;
            anInt843 = anInt842;
            anInt842 = anInt841;
            anInt841 = opCode;
            switch (opCode)
            {
            case 81:
                updatePlayers(pktSize, inStream);
                aBoolean1080 = false;
                opCode = -1;
                return true;
                /**
                 * open welcome screen
                 */
            case 176:
                int id = inStream.readUByte();
                final short[] mainItf =
                { 17511, 15819, 15812, 15801, 15791, 15774, 15767, 5993 };
                if (openInterfaceID == -1)
                {
                    reportAbuseInput = "";
                    canMute = false;
                    fullscreenInterfaceID = 15244;
                    openInterfaceID = mainItf[id];
                }
                opCode = -1;
                return true;
                /**
                 * @akzu reset entity(items/objects) in whole loaded map.
                 * 
                 *       TODO Contains small cheaphax, clears whole loaded map
                 *       of all ground entitys
                 */
            case 64:
                for (int j = 0; j < 104; j++)
                {
                    for (int l9 = bigRegionY; l9 < 104; l9++)
                    {
                        if (groundArray[floor_level][j][l9] != null)
                        {
                            groundArray[floor_level][j][l9] = null;
                            spawnGroundItem(j, l9);
                        }
                    }
                }
                for (SpawnObjectNode class30_sub1 = (SpawnObjectNode) aClass19_1179.reverseGetFirst(); class30_sub1 != null; class30_sub1 = (SpawnObjectNode) aClass19_1179.reverseGetNext())
                    if (class30_sub1.anInt1297 >= 0 && class30_sub1.anInt1297 < 104 && class30_sub1.anInt1298 >= 0 && class30_sub1.anInt1298 < 104 && class30_sub1.anInt1295 == floor_level)
                        class30_sub1.anInt1294 = 0;
                opCode = -1;
                return true;
                /**
                 * TODO: SEND Player chathead model
                 */
            case 185:
                int k = inStream.method436();
                RSInterface.interfaceCache[k].mediaType = 3;
                if (myPlayer.desc == null)
                    RSInterface.interfaceCache[k].mediaID = (short) ((myPlayer.anIntArray1700[0] << 25) + (myPlayer.anIntArray1700[4] << 20) + (myPlayer.equipment[0] << 15) + (myPlayer.equipment[8] << 10) + (myPlayer.equipment[11] << 5) + myPlayer.equipment[0]);
                else
                    RSInterface.interfaceCache[k].mediaID = (short) (0x12345678L + myPlayer.desc.id);
                opCode = -1;
                return true;
                /**
                 * Reset camera?
                 */
            case 107:
                aBoolean1160 = false;
                for (int l = 0; l < 5; l++)
                    aBooleanArray876[l] = false;
                opCode = -1;
                return true;
                /**
                 * @akzu reset items on interface
                 */
            case 72:
                int i1 = inStream.method434();
                RSInterface class9 = RSInterface.interfaceCache[i1];
                for (int k15 = 0; k15 < class9.inv.length; k15++)
                {
                    class9.inv[k15] = -1;
                    class9.inv[k15] = 0;
                }
                opCode = -1;
                return true;
                /**
                 * send ignore
                 */
            case 214:
                ignoreCount = pktSize / 8;
                for (int j1 = 0; j1 < ignoreCount; j1++)
                    ignoreListAsLongs[j1] = inStream.readLong();
                opCode = -1;
                return true;
                /**
                 * Sends a spinning camera.
                 * 
                 * @param location The camera location.
                 * @param turnSpeed The camera turning speed.
                 * @param movementSpeed The camera movement speed.
                 */
            case 166:
                aBoolean1160 = true;
                anInt1098 = inStream.readUByte();
                anInt1099 = inStream.readUByte();
                anInt1100 = inStream.readUShort();
                anInt1101 = inStream.readUByte();
                anInt1102 = inStream.readUByte();
                if (anInt1102 >= 100)
                {
                    xCameraPos = anInt1098 * 128 + 64;
                    yCameraPos = anInt1099 * 128 + 64;
                    zCameraPos = method42(floor_level, yCameraPos, xCameraPos) - anInt1100;
                }
                opCode = -1;
                return true;
                /**
                 * Send skill
                 */
            case 134:
                int k1 = inStream.readUByte();
                int i10 = inStream.method439();
                int l15 = inStream.readUByte();
                currentExp[k1] = i10;
                currentStats[k1] = l15;
                maxStats[k1] = 1;
                for (int k20 = 0; k20 < 98; k20++)
                    if (i10 >= anIntArray1019[k20])
                        maxStats[k1] = k20 + 2;
                updateStats();
                needDrawTabArea = true;
                opCode = -1;
                return true;
                /**
                 * force tab open
                 */
            case 71:
                int l1 = inStream.readUShort();
                int j10 = inStream.method426();
                if (l1 == 65535)
                    l1 = -1;
                tabInterfaceIDs[j10] = l1;
                needDrawTabArea = true;
                opCode = -1;
                return true;
                /**
                 * play song
                 */
            case 74:
                if (!music_enabled)
                {
                    opCode = -1;
                    return true;
                }
                int songID = inStream.method434();
                sendMusic(songID);
                opCode = -1;
                return true;
                /**
                 * play short (levelup) song
                 */
            case 121:
                if (!music_enabled)
                {
                    opCode = -1;
                    return true;
                }
                int songId = inStream.method436();
                int songDelay = inStream.method435();
                nextSong = songId;
                resourceProvider.method558(2, nextSong);
                previousSong = songDelay;
                opCode = -1;
                return true;
                /**
                 * logout
                 */
            case 109:
                resetLogout();
                opCode = -1;
                return false;
                /**
                 * move interface component
                 */
            case 70:
                int k2 = inStream.readShort();
                int l10 = inStream.readShort();
                int interfaceID = inStream.method434();
                RSInterface class9_5 = RSInterface.interfaceCache[interfaceID];
                class9_5.xOffset = (short) k2;
                class9_5.yOffset = (short) l10;
                opCode = -1;
                return true;
                /**
                 * load/construct map region
                 */
            case 73:
            case 241:
                int l2 = anInt1069;
                int i11 = anInt1070;
                if (opCode == 73)
                {
                    l2 = inStream.method435();
                    i11 = inStream.readUShort();
                    aBoolean1159 = false;
                }
                if (opCode == 241)
                {
                    i11 = inStream.method435();
                    inStream.initBitAccess();
                    for (int j16 = 0; j16 < 4; j16++)
                    {
                        for (int l20 = 0; l20 < 13; l20++)
                        {
                            for (int j23 = 0; j23 < 13; j23++)
                            {
                                int i26 = inStream.readBits(1);
                                if (i26 == 1)
                                    anIntArrayArrayArray1129[j16][l20][j23] = inStream.readBits(26);
                                else
                                    anIntArrayArrayArray1129[j16][l20][j23] = -1;
                            }
                        }
                    }
                    inStream.finishBitAccess();
                    l2 = inStream.readUShort();
                    aBoolean1159 = true;
                }
                if (anInt1069 == l2 && anInt1070 == i11 && loadingStage == 2)
                {
                    opCode = -1;
                    return true;
                }
                anInt1069 = l2;
                anInt1070 = i11;
                baseX = (anInt1069 - 6) * 8;
                baseY = (anInt1070 - 6) * 8;
                aBoolean1141 = (anInt1069 / 8 == 48 || anInt1069 / 8 == 49) && anInt1070 / 8 == 48;
                if (anInt1069 / 8 == 48 && anInt1070 / 8 == 148)
                    aBoolean1141 = true;
                loadingStage = 1;
                aLong824 = System.currentTimeMillis();
                gameScreenImageProducer.initDrawingArea();
                RSRaster.fillPixels(2, 130, 22, 0xffffff, 2);
                RSRaster.drawPixels(20, 3, 3, 0, 128);
                regularText.drawText(0, "Loading - Please Wait", 18, 68);
                regularText.drawText(0xffffff, "Loading - Please Wait", 17, 67);
                gameScreenImageProducer.drawGraphics((clientSize == CLIENT_FIXED ? 4 : 0), super.graphics, (clientSize == CLIENT_FIXED ? 4 : 0));
                if (opCode == 73)
                {
                    int k16 = 0;
                    for (int i21 = (anInt1069 - 6) / 8; i21 <= (anInt1069 + 6) / 8; i21++)
                    {
                        for (int k23 = (anInt1070 - 6) / 8; k23 <= (anInt1070 + 6) / 8; k23++)
                            k16++;
                    }
                    aByteArrayArray1183 = new byte[k16][];
                    aByteArrayArray1247 = new byte[k16][];
                    anIntArray1234 = new int[k16];
                    anIntArray1235 = new int[k16];
                    anIntArray1236 = new int[k16];
                    k16 = 0;
                    for (int l23 = (anInt1069 - 6) / 8; l23 <= (anInt1069 + 6) / 8; l23++)
                    {
                        for (int j26 = (anInt1070 - 6) / 8; j26 <= (anInt1070 + 6) / 8; j26++)
                        {
                            anIntArray1234[k16] = (l23 << 8) + j26;
                            if (aBoolean1141 && (j26 == 49 || j26 == 149 || j26 == 147 || l23 == 50 || l23 == 49 && j26 == 47))
                            {
                                anIntArray1235[k16] = -1;
                                anIntArray1236[k16] = -1;
                                k16++;
                            }
                            else
                            {
                                int k28 = anIntArray1235[k16] = resourceProvider.method562(0, j26, l23);
                                if (k28 != -1)
                                    resourceProvider.method558(3, k28);
                                int j30 = anIntArray1236[k16] = resourceProvider.method562(1, j26, l23);
                                if (j30 != -1)
                                    resourceProvider.method558(3, j30);
                                k16++;
                            }
                        }
                    }
                }
                if (opCode == 241)
                {
                    int l16 = 0;
                    int ai[] = new int[676];
                    for (int i24 = 0; i24 < 4; i24++)
                    {
                        for (int k26 = 0; k26 < 13; k26++)
                        {
                            for (int l28 = 0; l28 < 13; l28++)
                            {
                                int k30 = anIntArrayArrayArray1129[i24][k26][l28];
                                if (k30 != -1)
                                {
                                    int k31 = k30 >> 14 & 0x3ff;
                                    int i32 = k30 >> 3 & 0x7ff;
                                    int k32 = (k31 / 8 << 8) + i32 / 8;
                                    for (int j33 = 0; j33 < l16; j33++)
                                    {
                                        if (ai[j33] != k32)
                                            continue;
                                        k32 = -1;
                                    }
                                    if (k32 != -1)
                                        ai[l16++] = k32;
                                }
                            }
                        }
                    }
                    aByteArrayArray1183 = new byte[l16][];
                    aByteArrayArray1247 = new byte[l16][];
                    anIntArray1234 = new int[l16];
                    anIntArray1235 = new int[l16];
                    anIntArray1236 = new int[l16];
                    for (int l26 = 0; l26 < l16; l26++)
                    {
                        int i29 = anIntArray1234[l26] = ai[l26];
                        int l30 = i29 >> 8 & 0xff;
                        int l31 = i29 & 0xff;
                        int j32 = anIntArray1235[l26] = resourceProvider.method562(0, l31, l30);
                        if (j32 != -1)
                            resourceProvider.method558(3, j32);
                        int i33 = anIntArray1236[l26] = resourceProvider.method562(1, l31, l30);
                        if (i33 != -1)
                            resourceProvider.method558(3, i33);
                    }
                }
                int i17 = baseX - anInt1036;
                int j21 = baseY - anInt1037;
                anInt1036 = baseX;
                anInt1037 = baseY;
                for (int j24 = 0; j24 < 16384; j24++)
                {
                    NPC npc = npcArray[j24];
                    if (npc != null)
                    {
                        for (int j29 = 0; j29 < 10; j29++)
                        {
                            npc.smallX[j29] -= i17;
                            npc.smallY[j29] -= j21;
                        }
                        npc.x -= i17 * 128;
                        npc.y -= j21 * 128;
                    }
                }
                for (int i27 = 0; i27 < maxPlayers; i27++)
                {
                    Player player = playerArray[i27];
                    if (player != null)
                    {
                        for (int i31 = 0; i31 < 10; i31++)
                        {
                            player.smallX[i31] -= i17;
                            player.smallY[i31] -= j21;
                        }
                        player.x -= i17 * 128;
                        player.y -= j21 * 128;
                    }
                }
                aBoolean1080 = true;
                byte byte1 = 0;
                byte byte2 = 104;
                byte byte3 = 1;
                if (i17 < 0)
                {
                    byte1 = 103;
                    byte2 = -1;
                    byte3 = -1;
                }
                byte byte4 = 0;
                byte byte5 = 104;
                byte byte6 = 1;
                if (j21 < 0)
                {
                    byte4 = 103;
                    byte5 = -1;
                    byte6 = -1;
                }
                for (int k33 = byte1; k33 != byte2; k33 += byte3)
                {
                    for (int l33 = byte4; l33 != byte5; l33 += byte6)
                    {
                        int i34 = k33 + i17;
                        int j34 = l33 + j21;
                        for (int k34 = 0; k34 < 4; k34++)
                            if (i34 >= 0 && j34 >= 0 && i34 < 104 && j34 < 104)
                                groundArray[k34][k33][l33] = groundArray[k34][i34][j34];
                            else
                                groundArray[k34][k33][l33] = null;
                    }
                }
                for (SpawnObjectNode class30_sub1_1 = (SpawnObjectNode) aClass19_1179.reverseGetFirst(); class30_sub1_1 != null; class30_sub1_1 = (SpawnObjectNode) aClass19_1179.reverseGetNext())
                {
                    class30_sub1_1.anInt1297 -= i17;
                    class30_sub1_1.anInt1298 -= j21;
                    if (class30_sub1_1.anInt1297 < 0 || class30_sub1_1.anInt1298 < 0 || class30_sub1_1.anInt1297 >= 104 || class30_sub1_1.anInt1298 >= 104)
                        class30_sub1_1.unlink();
                }
                if (destX != 0)
                {
                    destX -= i17;
                    destY -= j21;
                }
                aBoolean1160 = false;
                opCode = -1;
                return true;
                /**
                 * open walkable interface
                 */
            case 208:
                int i3 = inStream.method437();
                if (i3 >= 0)
                {
                    method60(i3);
                    walkableInterfaceMode = true;
                }
                else
                    walkableInterfaceMode = false;
                walkableInterface = i3;
                opCode = -1;
                return true;
                /**
                 * @akzu blackout minimap
                 */
            case 99:
                hideMinimap = inStream.readUByte();
                opCode = -1;
                return true;
                /**
                 * @akzu send NPC_CHATHEAD_model to interface??? TODO: CHECK
                 */
            case 75:
                int j3 = inStream.method436();
                int j11 = inStream.method436();
                RSInterface.interfaceCache[j11].mediaType = 2;
                RSInterface.interfaceCache[j11].mediaID = (short) j3;
                opCode = -1;
                return true;
                /**
                 * @akzu set systemupdatetimer
                 */
            case 114:
                systemUpdateTime = inStream.method434() * 30;
                opCode = -1;
                return true;
                /**
                 * @akzu set current 'placement' 8x8 region, also optionally
                 *       allows grouping of item,object,projectile,etc. packets
                 *       within this one
                 */
            case 60:
                bigRegionY = inStream.readUByte();
                bigRegionX = inStream.method427();
                while (inStream.pointer < pktSize)
                {
                    int k3 = inStream.readUByte();
                    parsePacketGroup(inStream, k3);
                }
                opCode = -1;
                return true;
                /**
                 * @akzu send BARROWS TUNNEL MAP SHAKE?
                 */
            case 35:
                int l3 = inStream.readUByte();
                int k11 = inStream.readUByte();
                int j17 = inStream.readUByte();
                int k21 = inStream.readUByte();
                aBooleanArray876[l3] = true;
                anIntArray873[l3] = k11;
                anIntArray1203[l3] = j17;
                anIntArray928[l3] = k21;
                anIntArray1030[l3] = 0;
                opCode = -1;
                return true;
                /**
                 * @akzu sendsound
                 */
            case 174:
                if (!music_enabled)
                {
                    opCode = -1;
                    return true;
                }
                int soundId = inStream.readUShort();
                int type = inStream.readUByte();
                int delay = inStream.readUShort();
                sound[currentSound] = soundId;
                soundType[currentSound] = type;
                soundDelay[currentSound] = delay + Sound.anIntArray326[soundId];
                soundVolume[currentSound] = 8;
                currentSound++;
                opCode = -1;
                return true;
                /**
                 * @akzu sendplayeroption
                 */
            case 104:
                int j4 = inStream.method427();
                int i12 = inStream.method426();
                String s6 = inStream.readString();
                if (j4 >= 1 && j4 <= 5)
                {
                    if (s6.equalsIgnoreCase("null"))
                    {
                        s6 = null;
                    }
                    atPlayerActions[j4 - 1] = s6;
                    atPlayerArray[j4 - 1] = i12 == 0;
                }
                opCode = -1;
                return true;
                /**
                 * @akzu reset mapflag
                 */
            case 78:
                destX = 0;
                opCode = -1;
                return true;
                /**
                 * send a request send game message
                 */
            case 253:
                String s = inStream.readString();
                if (s.endsWith(":tradereq:"))
                {
                    String s3 = s.substring(0, s.indexOf(":"));
                    long l17 = TextClass.longForName(s3);
                    boolean flag2 = false;
                    for (int j27 = 0; j27 < ignoreCount; j27++)
                    {
                        if (ignoreListAsLongs[j27] != l17)
                            continue;
                        flag2 = true;
                    }
                    if (!flag2 && anInt1251 == 0)
                        pushMessage("wishes to trade with you.", 4, capitalize(s3));
                }
                else if (s.endsWith(":duelreq:"))
                {
                    String s4 = s.substring(0, s.indexOf(":"));
                    long l18 = TextClass.longForName(s4);
                    boolean flag3 = false;
                    for (int k27 = 0; k27 < ignoreCount; k27++)
                    {
                        if (ignoreListAsLongs[k27] != l18)
                            continue;
                        flag3 = true;
                    }
                    if (!flag3 && anInt1251 == 0)
                        pushMessage("wishes to duel with you.", 8, capitalize(s4));
                }
                else if (s.endsWith(":chalreq:"))
                {
                    String s5 = s.substring(0, s.indexOf(":"));
                    long l19 = TextClass.longForName(s5);
                    boolean flag4 = false;
                    for (int l27 = 0; l27 < ignoreCount; l27++)
                    {
                        if (ignoreListAsLongs[l27] != l19)
                            continue;
                        flag4 = true;
                    }
                    if (!flag4 && anInt1251 == 0)
                    {
                        String s8 = s.substring(s.indexOf(":") + 1, s.length() - 9);
                        pushMessage(s8, 8, s5);
                    }
                }
                else
                {
                    pushMessage(s, 0, "");
                }
                opCode = -1;
                return true;
                /**
                 * Reset entity anims
                 */
            case 1:
                for (int k4 = 0; k4 < playerArray.length; k4++)
                    if (playerArray[k4] != null)
                        playerArray[k4].anim = -1;
                for (int j12 = 0; j12 < npcArray.length; j12++)
                    if (npcArray[j12] != null)
                        npcArray[j12].anim = -1;
                opCode = -1;
                return true;
                /**
                 * TODO: Pm stuff?
                 */
            case 50:
                long l4 = inStream.readLong();
                int i18 = inStream.readUByte();
                String s7 = TextClass.fixName(TextClass.nameForLong(l4));
                for (int k24 = 0; k24 < friendsCount; k24++)
                {
                    if (l4 != friendsListAsLongs[k24])
                        continue;
                    if (friendsNodeIDs[k24] != i18)
                    {
                        friendsNodeIDs[k24] = i18;
                        needDrawTabArea = true;
                        if (i18 >= 2)
                        {
                            pushMessage(capitalize(s7) + " has logged in.", 5, "");
                        }
                        if (i18 <= 1)
                        {
                            pushMessage(capitalize(s7) + " has logged out.", 5, "");
                        }
                    }
                    s7 = null;
                }
                if (s7 != null && friendsCount < 200)
                {
                    friendsListAsLongs[friendsCount] = l4;
                    friendsList[friendsCount] = s7;
                    friendsNodeIDs[friendsCount] = i18;
                    friendsCount++;
                    needDrawTabArea = true;
                }
                for (boolean flag6 = false; !flag6;)
                {
                    flag6 = true;
                    for (int k29 = 0; k29 < friendsCount - 1; k29++)
                        if (friendsNodeIDs[k29] != nodeID && friendsNodeIDs[k29 + 1] == nodeID || friendsNodeIDs[k29] == 0 && friendsNodeIDs[k29 + 1] != 0)
                        {
                            int j31 = friendsNodeIDs[k29];
                            friendsNodeIDs[k29] = friendsNodeIDs[k29 + 1];
                            friendsNodeIDs[k29 + 1] = j31;
                            String s10 = friendsList[k29];
                            friendsList[k29] = friendsList[k29 + 1];
                            friendsList[k29 + 1] = s10;
                            long l32 = friendsListAsLongs[k29];
                            friendsListAsLongs[k29] = friendsListAsLongs[k29 + 1];
                            friendsListAsLongs[k29 + 1] = l32;
                            needDrawTabArea = true;
                            flag6 = false;
                        }
                }
                opCode = -1;
                return true;
                /**
                 * @akzu refresh energy
                 */
            case 110:
                if (tabID == 11 && !invHidden)
                    needDrawTabArea = true;
                energy = inStream.readUByte();
                opCode = -1;
                return true;
                /**
                 * Set hinttype
                 */
            case 254:
                hintType = inStream.readUByte();
                if (hintType == 1)
                    hintArrowNPCID = inStream.readUShort();
                if (hintType >= 2 && hintType <= 6)
                {
                    if (hintType == 2)
                    {
                        anInt937 = 64;
                        anInt938 = 64;
                    }
                    if (hintType == 3)
                    {
                        anInt937 = 0;
                        anInt938 = 64;
                    }
                    if (hintType == 4)
                    {
                        anInt937 = 128;
                        anInt938 = 64;
                    }
                    if (hintType == 5)
                    {
                        anInt937 = 64;
                        anInt938 = 0;
                    }
                    if (hintType == 6)
                    {
                        anInt937 = 64;
                        anInt938 = 128;
                    }
                    hintType = 2;
                    anInt934 = inStream.readUShort();
                    anInt935 = inStream.readUShort();
                    anInt936 = inStream.readUByte();
                }
                if (hintType == 10)
                    hintArrowPlayerID = inStream.readUShort();
                opCode = -1;
                return true;
                /**
                 * Force overlay inventory interface + main interface
                 */
            case 248:
                int i5 = inStream.method435();
                int k12 = inStream.readUShort();
                if (backDialogID != -1)
                {
                    backDialogID = -1;
                    inputTaken = true;
                }
                if (inputDialogState != 0)
                {
                    inputDialogState = 0;
                    inputTaken = true;
                }
                if (invHidden)
                    invHidden = false;
                openInterfaceID = i5;
                invOverlayInterfaceID = k12;
                needDrawTabArea = true;
                aBoolean1149 = false;
                opCode = -1;
                return true;
                /**
                 * Set scrollpos
                 */
            case 79:
                int j5 = inStream.method434();
                int l12 = inStream.method435();
                RSInterface class9_3 = RSInterface.interfaceCache[j5];
                if (class9_3 != null && class9_3.type == 0)
                {
                    if (l12 < 0)
                        l12 = 0;
                    if (l12 > class9_3.scrollMax - class9_3.height)
                        l12 = class9_3.scrollMax - class9_3.height;
                    class9_3.scrollPosition = (short) l12;
                }
                needDrawTabArea = true;
                opCode = -1;
                return true;

            case 238: // TODO: Custom packet, setting scrollmax
                int itf_id = inStream.readUShort();
                int scrollmax = inStream.readUShort();
                RSInterface class9_7 = RSInterface.interfaceCache[itf_id];
                if (class9_7 != null && class9_7.type == 0)
                {
                    class9_7.scrollMax = (short) scrollmax;
                }
                needDrawTabArea = true;
                opCode = -1;
                return true;
                /**
                 * TODO: Handles option tab?
                 */
            case 68:
                for (int k5 = 0; k5 < variousSettings.length; k5++)
                    if (variousSettings[k5] != anIntArray1045[k5])
                    {
                        variousSettings[k5] = anIntArray1045[k5];
                        method33(k5);
                        needDrawTabArea = true;
                    }
                opCode = -1;
                return true;
                /**
                 * Something todo with PMING
                 */
            case 196:
                long l5 = inStream.readLong();
                int j18 = inStream.readInt();
                int l21 = inStream.readUByte();
                boolean flag5 = false;
                for (int i28 = 0; i28 < 100; i28++)
                {
                    if (anIntArray1240[i28] != j18)
                        continue;
                    flag5 = true;
                }
                if (l21 <= 1)
                {
                    for (int l29 = 0; l29 < ignoreCount; l29++)
                    {
                        if (ignoreListAsLongs[l29] != l5)
                            continue;
                        flag5 = true;
                    }
                }
                if (!flag5 && anInt1251 == 0)
                    try
                    {
                        anIntArray1240[anInt1169] = j18;
                        anInt1169 = (anInt1169 + 1) % 100;
                        String s9 = TextInput.method525(pktSize - 13, inStream);
                        if (l21 == 2)
                            pushMessage(s9, 7, "@cr3@" + TextClass.fixName(TextClass.nameForLong(l5)));
                        else if (l21 == 3)
                            pushMessage(s9, 7, "@cr2@" + TextClass.fixName(TextClass.nameForLong(l5)));
                        else if (l21 == 1)
                            pushMessage(s9, 7, "@cr1@" + TextClass.fixName(TextClass.nameForLong(l5)));
                        else
                            pushMessage(s9, 3, TextClass.fixName(TextClass.nameForLong(l5)));
                    }
                    catch (Exception exception1)
                    {
                        System.err.println("cde1");
                    }
                opCode = -1;
                return true;
                /**
                 * TODO: Set coords??
                 */
            case 85:
                bigRegionY = inStream.method427();
                bigRegionX = inStream.method427();
                opCode = -1;
                return true;
                /**
                 * Flash sideicon?
                 */
            case 24:
                anInt1054 = inStream.readUByte();
                if (anInt1054 == tabID)
                {
                    if (anInt1054 == 3)
                        tabID = 1;
                    else
                        tabID = 3;
                    needDrawTabArea = true;
                }
                opCode = -1;
                return true;
                /**
                 * Display ITEM model in interface?
                 */
            case 246:
                int i6 = inStream.method434();
                int i13 = inStream.readUShort();
                int k18 = inStream.readUShort();
                if (k18 == 65535)
                {
                    RSInterface.interfaceCache[i6].mediaType = 0;
                    opCode = -1;
                    return true;
                }
                else
                {
                    ItemDefinition itemDef = ItemDefinition.forID(k18);
                    RSInterface.interfaceCache[i6].mediaType = 4;
                    RSInterface.interfaceCache[i6].mediaID = (short) k18;
                    RSInterface.interfaceCache[i6].modelRotY = (short) itemDef.modelRotation1;
                    RSInterface.interfaceCache[i6].modelRotX = (short) itemDef.modelRotation2;
                    RSInterface.interfaceCache[i6].modelZoom = (short) ((itemDef.modelZoom * 100) / i13);
                    opCode = -1;
                    return true;
                }
                /**
                 * TODO: Wtf? Sets hovered, any use really?, besides few things,
                 * maybe
                 * 
                 * got it: hide interface component
                 */
            case 171:
                boolean flag1 = inStream.readUByte() == 1;
                int j13 = inStream.readUShort();
                RSInterface.interfaceCache[j13].isMouseoverTriggered = flag1;
                opCode = -1;
                return true;
                /**
                 * Force interface to inventory area, no main interface
                 */
            case 142:
                int j6 = inStream.method434();
                method60(j6);
                if (backDialogID != -1)
                {
                    backDialogID = -1;
                    inputTaken = true;
                }
                if (inputDialogState != 0)
                {
                    inputDialogState = 0;
                    inputTaken = true;
                }
                if (invHidden)
                    invHidden = false;
                invOverlayInterfaceID = j6;
                needDrawTabArea = true;
                openInterfaceID = -1;
                aBoolean1149 = false;
                opCode = -1;
                return true;
                /**
                 * Send string
                 */
            case 126:
                String text = inStream.readString();
                int frame = inStream.method435();
                sendString(text, frame);
                opCode = -1;
                return true;
                /**
                 * Toggle chat settings
                 */
            case 206:
                chatTabMode[2] = (byte) inStream.readUByte();
                chatTabMode[3] = (byte) inStream.readUByte();
                chatTabMode[5] = (byte) inStream.readUByte();
                inputTaken = true;
                opCode = -1;
                return true;
                /**
                 * Send weight
                 */
            case 240:
                weight = inStream.readShort();
                opCode = -1;
                return true;
                /**
                 * Send ANY model to interface, again????
                 */
            case 8:
                int k6 = inStream.method436();
                int l13 = inStream.readUShort();
                RSInterface.interfaceCache[k6].mediaType = 1;
                RSInterface.interfaceCache[k6].mediaID = (short) l13;
                opCode = -1;
                return true;
                /**
                 * Change interface component color!
                 */
            case 122:
                int l6 = inStream.method436();
                int i14 = inStream.method436();
                int i19 = i14 >> 10 & 0x1f;
                int i22 = i14 >> 5 & 0x1f;
                int l24 = i14 & 0x1f;
                RSInterface.interfaceCache[l6].disabledTextColor = (i19 << 19) + (i22 << 11) + (l24 << 3);
                needDrawTabArea = true;
                inputTaken = true;
                opCode = -1;
                return true;
                /**
                 * Send group of items in interface ("inv") uses: shop, trade,
                 * duel, bank
                 */
            case 53:
                needDrawTabArea = true;
                int i7 = inStream.readUShort();
                RSInterface class9_1 = RSInterface.interfaceCache[i7];
                int j19 = inStream.readUShort();
                for (int j22 = 0; j22 < j19; j22++)
                {
                    int i25 = inStream.readUByte();
                    if (i25 == 255)
                        i25 = inStream.method440();
                    class9_1.inv[j22] = inStream.method436();
                    class9_1.invStackSizes[j22] = i25;
                }
                for (int j25 = j19; j25 < class9_1.inv.length; j25++)
                {
                    class9_1.inv[j25] = 0;
                    class9_1.invStackSizes[j25] = 0;
                }
                opCode = -1;
                return true;
                /**
                 * Modify a component rotation/zoom in interface
                 */
            case 230:
                int j7 = inStream.method435();
                int j14 = inStream.readUShort();
                int k19 = inStream.readUShort();
                int k22 = inStream.method436();
                RSInterface.interfaceCache[j14].modelRotY = (short) k19;
                RSInterface.interfaceCache[j14].modelRotX = (short) k22;
                RSInterface.interfaceCache[j14].modelZoom = (short) j7;
                opCode = -1;
                return true;
                /**
                 * Send friend server status!
                 */
            case 221:
                anInt900 = inStream.readUByte();
                needDrawTabArea = true;
                opCode = -1;
                return true;
                /**
                 * Send camera, again???
                 * 
                 * Sends a camera.
                 * 
                 * @param location The location of the camera.
                 * @param speed The speed of the camera movement.
                 * @param angle The angle of the camera.
                 */
            case 177:
                aBoolean1160 = true;
                anInt995 = inStream.readUByte();
                anInt996 = inStream.readUByte();
                anInt997 = inStream.readUShort();
                anInt998 = inStream.readUByte();
                anInt999 = inStream.readUByte();
                if (anInt999 >= 100)
                {
                    int k7 = anInt995 * 128 + 64;
                    int k14 = anInt996 * 128 + 64;
                    int i20 = method42(floor_level, k14, k7) - anInt997;
                    int l22 = k7 - xCameraPos;
                    int k25 = i20 - zCameraPos;
                    int j28 = k14 - yCameraPos;
                    int i30 = (int) Math.sqrt(l22 * l22 + j28 * j28);
                    yCameraCurve = (int) (Math.atan2(k25, i30) * 325.94900000000001D) & 0x7ff;
                    xCameraCurve = (int) (Math.atan2(l22, j28) * -325.94900000000001D) & 0x7ff;
                    if (yCameraCurve < 128)
                        yCameraCurve = 128;
                    if (yCameraCurve > 383)
                        yCameraCurve = 383;
                }
                opCode = -1;
                return true;
                /**
                 * Send player details
                 */
            case 249:
                anInt1046 = inStream.method426();
                playerID = inStream.method436();
                opCode = -1;
                return true;
                /**
                 * Update npcs
                 */
            case 65:
                updateNPCs(inStream, pktSize);
                opCode = -1;
                return true;
                /**
                 * Open enter amount input dialogue
                 */
            case 27:
                messagePromptRaised = false;
                inputDialogState = 1;
                if (chatHidden)
                    changeActiveChatStoneState(0);
                amountOrNameInput = "";
                inputTaken = true;
                opCode = -1;
                return true;
                /**
                 * Open enter name input dialogue
                 */
            case 187:
                messagePromptRaised = false;
                if (chatHidden)
                    changeActiveChatStoneState(0);
                inputDialogState = 2;
                amountOrNameInput = "";
                inputTaken = true;
                opCode = -1;
                return true;
                /**
                 * Open normal interface
                 */
            case 97:
                int l7 = inStream.readUShort();
                method60(l7);
                if (invOverlayInterfaceID != -1)
                {
                    invOverlayInterfaceID = -1;
                    needDrawTabArea = true;
                }
                if (backDialogID != -1)
                {
                    backDialogID = -1;
                    inputTaken = true;
                }
                if (inputDialogState != 0)
                {
                    inputDialogState = 0;
                    inputTaken = true;
                }
                if (l7 == 3559)
                {
                    draw_sprites_logon = true;
                    needDrawTabArea = true;
                    inputTaken = true;
                }
                openInterfaceID = l7;
                aBoolean1149 = false;
                opCode = -1;
                return true;
                /**
                 * Open dialogue interface over chat
                 */
            case 218:
                int i8 = inStream.method438();
                if (chatHidden)
                    changeActiveChatStoneState(0);
                dialogID = i8;
                inputTaken = true;
                opCode = -1;
                return true;
                /**
                 * Config related shit int
                 */
            case 87:
                int j8 = inStream.method434();
                int l14 = inStream.method439();
                anIntArray1045[j8] = l14;
                if (variousSettings[j8] != l14)
                {
                    variousSettings[j8] = l14;
                    method33(j8);
                    needDrawTabArea = true;
                    if (dialogID != -1)
                        inputTaken = true;
                }
                opCode = -1;
                return true;
                /**
                 * Config related shit byte
                 */
            case 36:
                int k8 = inStream.method434();
                byte byte0 = inStream.readByte();
                anIntArray1045[k8] = byte0;
                updateMagicSettings(k8, byte0);
                if (variousSettings[k8] != byte0)
                {
                    variousSettings[k8] = byte0;
                    method33(k8);
                    needDrawTabArea = true;
                    if (dialogID != -1)
                        inputTaken = true;
                }
                opCode = -1;
                return true;
                /**
                 * Send multi-icon
                 */
            case 61:
                drawMultiIcon = inStream.readUByte();
                opCode = -1;
                return true;
                /**
                 * Set interface component animation // Current uses: chatbox
                 * emotions
                 */
            case 200:
                int interfaceId = inStream.readUShort();
                int animationID = inStream.readShort();
                RSInterface class9_4 = RSInterface.interfaceCache[interfaceId];
                class9_4.disabledAnimation = (short) animationID;
                if (animationID == -1)
                {
                    class9_4.animFrame = 0;
                    class9_4.duration = 0;
                }
                opCode = -1;
                return true;
                /**
                 * Close interface
                 */
            case 219:
                if (invOverlayInterfaceID != -1)
                {
                    invOverlayInterfaceID = -1;
                    needDrawTabArea = true;
                }
                if (backDialogID != -1)
                {
                    backDialogID = -1;
                    inputTaken = true;
                }
                if (inputDialogState != 0)
                {
                    inputDialogState = 0;
                    inputTaken = true;
                }
                openInterfaceID = -1;
                aBoolean1149 = false;
                opCode = -1;
                return true;
                /**
                 * Update single item in inventory container ("inv")
                 */
            case 34:
                needDrawTabArea = true;
                int i9 = inStream.readUShort();
                RSInterface class9_2 = RSInterface.interfaceCache[i9];
                while (inStream.pointer < pktSize)
                {
                    int j20 = inStream.readSmarts();
                    int i23 = inStream.readUShort();
                    int l25 = inStream.readUByte();
                    if (l25 == 255)
                        l25 = inStream.readInt();
                    if (j20 >= 0 && j20 < class9_2.inv.length)
                    {
                        class9_2.inv[j20] = i23;
                        class9_2.invStackSizes[j20] = l25;
                    }
                }
                opCode = -1;
                return true;
                /**
                 * Global shit?
                 */
            case 4:
            case 44:
            case 84:
            case 101:
            case 105:
            case 117:
            case 147:
            case 151:
            case 156:
            case 160:
            case 215:
                parsePacketGroup(inStream, opCode);
                opCode = -1;
                return true;
                /**
                 * Force tab open
                 */
            case 106:
                tabID = inStream.method427();
                needDrawTabArea = true;
                opCode = -1;
                return true;
                /**
                 * Open interface over chat
                 */
            case 164:
                int j9 = inStream.method434();
                method60(j9);
                if (invOverlayInterfaceID != -1)
                {
                    invOverlayInterfaceID = -1;
                    needDrawTabArea = true;
                }
                if (chatHidden)
                    changeActiveChatStoneState(0);
                backDialogID = j9;
                inputTaken = true;
                openInterfaceID = -1;
                aBoolean1149 = false;
                opCode = -1;
                return true;
            }
            System.err.println("T1 - " + opCode + "," + pktSize + " - " + anInt842 + "," + anInt843);
            resetLogout();
        }
        catch (IOException _ex)
        {
            dropClient();
        }
        catch (Exception exception)
        {
            String s2 = "T2 - " + opCode + "," + anInt842 + "," + anInt843 + " - " + pktSize + "," + (baseX + myPlayer.smallX[0]) + "," + (baseY + myPlayer.smallY[0]) + " - ";
            for (int j15 = 0; j15 < pktSize && j15 < 50; j15++)
                s2 = s2 + inStream.buffer[j15] + ",";
            System.err.println(s2);
            resetLogout();
        }
        return true;
    }

    // TODO: AKZUUU
    public void renderGameView()
    {
        try
        {
            anInt1265++;
            method47(true);
            method26(true);
            method47(false);
            method26(false);
            method55();
            method104();
            if (!aBoolean1160)
            {
                int i = anInt1184;
                if (anInt984 / 256 > i)
                    i = anInt984 / 256;
                if (aBooleanArray876[4] && anIntArray1203[4] + 128 > i)
                    i = anIntArray1203[4] + 128;
                int k = viewRotation + viewRotationOffset & 0x7ff;

                double width = clientWidth;
                double height = clientHeight;

                double final_calc = client_zoom + 700 + (i * 2) / ((width / height) * 1.532934131736527);

                // TODO: <AkZu> Found a pretty good solution :)
                setCameraPos((int) (clientSize == CLIENT_FIXED ? (600 + i * 3) : final_calc), i, anInt1014, method42(floor_level, myPlayer.y, myPlayer.x) - 50, k, anInt1015);
            }
            int j;
            if (!aBoolean1160)
                j = method120();
            else
                j = method121();
            int l = xCameraPos;
            int zCamPos = zCameraPos;
            int j1 = yCameraPos;
            int k1 = yCameraCurve;
            int l1 = xCameraCurve;
            // Camera sensitivy via arrow keys
            for (int i2 = 0; i2 < 5; i2++)
                if (aBooleanArray876[i2])
                {
                    int j2 = (int) ((Math.random() * (double) (anIntArray873[i2] * 2 + 1) - (double) anIntArray873[i2]) + Math.sin((double) anIntArray1030[i2] * ((double) anIntArray928[i2] / 100D)) * (double) anIntArray1203[i2]);
                    if (i2 == 0)
                        xCameraPos += j2;
                    if (i2 == 1)
                        zCameraPos += j2;
                    if (i2 == 2)
                        yCameraPos += j2;
                    if (i2 == 3)
                        xCameraCurve = xCameraCurve + j2 & 0x7ff;
                    if (i2 == 4)
                    {
                        yCameraCurve += j2;
                        if (yCameraCurve < 128)
                            yCameraCurve = 128;
                        if (yCameraCurve > 383)
                            yCameraCurve = 383;
                    }
                }
            int textureId = Rasterizer.anInt1481;
            Model.aBoolean1684 = true;
            Model.resourceCount = 0;
            Model.cursorXPos = super.mouseX - (clientSize == CLIENT_FIXED ? 4 : 0);
            Model.cursorYPos = super.mouseY - (clientSize == CLIENT_FIXED ? 4 : 0);
            RSRaster.setAllPixelsToZero();
            sceneGraph.render(xCameraPos, yCameraPos, xCameraCurve, zCameraPos, (roofRemove ? floor_level : j), yCameraCurve);
            sceneGraph.clearInteractableObjectCache();
            updateEntities();
            drawHeadIcon();
            animateTexture(textureId);
            if (clientSize != 0)
            {
                drawChatArea();
                drawTabArea();
                drawMinimap();
            }
            draw3dScreen();
            gameScreenImageProducer.drawGraphics((clientSize == CLIENT_FIXED ? 4 : 0), super.graphics, (clientSize == CLIENT_FIXED ? 4 : 0));
            xCameraPos = l;
            zCameraPos = zCamPos;
            yCameraPos = j1;
            yCameraCurve = k1;
            xCameraCurve = l1;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void clearTopInterfaces()
    {
        stream.writeOpcode(130);
        if (invOverlayInterfaceID != -1)
        {
            invOverlayInterfaceID = -1;
            needDrawTabArea = true;
            aBoolean1149 = false;
        }
        if (backDialogID != -1)
        {
            backDialogID = -1;
            inputTaken = true;
            aBoolean1149 = false;
        }
        if (openInterfaceID > -1)
            openInterfaceID = -1;
        if (fullscreenInterfaceID > -1)
        {
            draw_sprites_logon = true;
            fullscreenInterfaceID = -1;
        }
    }

    // TODO: Akzuuu
    public RSClient()
    {
        super.setClient(this);
        try
        {
            System.setProperty("sun.java2d.d3d", "true");
            System.setProperty("sun.java2d.translaccel", "true");
            System.setProperty("sun.java2d.ddforcevram", "true");
            System.setProperty("sun.java2d.accthreshold", "0");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        fullscreenInterfaceID = -1;
        chatRights = new int[100];
        //server = "178.117.87.243";
        server = "127.0.0.1";
        walk_dist = new int[104][104];
        friendsNodeIDs = new int[200];
        groundArray = new Deque[4][104][104];
        aStream_834 = new RSBuffer(new byte[5000]);
        npcArray = new NPC[16384];
        npcIndices = new int[16384];
        anIntArray840 = new int[1000];
        aStream_847 = RSBuffer.create();
        aBoolean848 = true;
        openInterfaceID = -1;
        aCRC32_930 = new CRC32();
        currentExp = new int[Skills.skillsCount];
        anIntArray873 = new int[5];
        aBooleanArray876 = new boolean[5];
        reportAbuseInput = "";
        playerID = -1;
        menuOpen = false;
        inputString = "";
        maxPlayers = 2048;
        myPlayerIndex = 2047;
        playerArray = new Player[maxPlayers];
        playerIndices = new int[maxPlayers];
        anIntArray894 = new int[maxPlayers];
        aStreamArray895s = new RSBuffer[maxPlayers];
        walk_prev = new int[104][104];
        anInt902 = 0x766654;
        animatedPixels = new byte[16384];
        currentStats = new int[Skills.skillsCount];
        ignoreListAsLongs = new long[100];
        loadingError = false;
        anInt927 = 0x332d25;
        anIntArray928 = new int[5];
        anIntArrayArray929 = new int[104][104];
        chatTypes = new byte[100];
        chatNames = new String[100];
        chatMessages = new String[100];
        sideIcons = new IndexedImage[14];
        chatStones = new IndexedImage[5];
        scrollBar = new IndexedImage[4];
        chatArea = new DirectImage[3];
        tabArea = new DirectImage[4];
        mapArea = new DirectImage[4];
        redStones = new IndexedImage[5];
        aBoolean954 = true;
        friendsListAsLongs = new long[200];
        currentSong = -1;
        spriteDrawX = -1;
        spriteDrawY = -1;
        anIntArray968 = new int[33];
        decompressors = new Decompressor[5];
        variousSettings = new int[2000];
        aBoolean972 = false;
        anInt975 = 50;
        anIntArray976 = new int[anInt975];
        anIntArray977 = new int[anInt975];
        anIntArray978 = new int[anInt975];
        anIntArray979 = new int[anInt975];
        chat_color = new int[anInt975];
        chat_effects = new int[anInt975];
        anIntArray982 = new int[anInt975];
        aStringArray983 = new String[anInt975];
        anInt985 = -1;
        hitMarks = new IndexedImage[5];
        player_outfit_colors = new int[5];
        anInt1002 = 0x23201b;
        amountOrNameInput = "";
        aClass19_1013 = new Deque();
        aBoolean1017 = false;
        walkableInterface = -1;
        anIntArray1030 = new int[5];
        mapFunctions = new IndexedImage[76];
        dialogID = -1;
        maxStats = new int[Skills.skillsCount];
        anIntArray1045 = new int[2000];
        gender = true;
        anIntArray1052 = new int[151];
        anInt1054 = -1;
        stillGraphicDeque = new Deque();
        anIntArray1057 = new int[33];
        aClass9_1059 = new RSInterface();
        mapScenes = new IndexedImage[80];
        barFillColor = 0x4d4233;
        body_part_list = new int[7];
        anIntArray1072 = new int[1000];
        anIntArray1073 = new int[1000];
        aBoolean1080 = false;
        friendsList = new String[200];
        inStream = RSBuffer.create();
        expectedCRCs = new int[9];
        menuActionCmd2 = new int[500];
        menuActionCmd3 = new int[500];
        menuActionID = new int[500];
        menuActionCmd1 = new int[500];
        headIcons = new IndexedImage[7];
        skullIcons = new IndexedImage[2];
        headIconsHint = new IndexedImage[2];
        inputTitle = "";
        atPlayerActions = new String[5];
        atPlayerArray = new boolean[5];
        anIntArrayArrayArray1129 = new int[4][13][13];
        aClass30_Sub2_Sub1_Sub1Array1140 = new IndexedImage[200];// @akzu
        // originally
        // 1000 bug
        // bug
        // bug
        // MAYBE!
        aBoolean1141 = false;
        aBoolean1149 = false;
        crosses = new IndexedImage[8];
        // smileys = new IndexedImage[13];
        needDrawTabArea = false;
        loggedIn = false;
        canMute = false;
        atLoginMenu = false;
        aBoolean1159 = false;
        aBoolean1160 = false;
        myUsername = "";
        myPassword = "";
        genericLoadingError = false;
        reportAbuseInterfaceID = -1;
        aClass19_1179 = new Deque();
        anInt1184 = 128;
        invOverlayInterfaceID = -1;
        stream = RSBuffer.create();
        menuActionName = new String[500];
        anIntArray1203 = new int[5];
        sound = new int[50];
        chatScrollMax = 115;
        promptInput = "";
        modIcons = new DirectImage[3];
        tabID = 3;
        inputTaken = false;
        anIntArray1229 = new int[151];
        collision_maps = new CollisionMap[4];
        anIntArray1240 = new int[100];
        soundType = new int[50];
        aBoolean1242 = false;
        soundDelay = new int[50];
        soundVolume = new int[50];
        rsAlreadyLoaded = false;
        welcomeScreenRaised = false;
        messagePromptRaised = false;
        loginMessage1 = "";
        loginMessage2 = "";
        backDialogID = -1;
        bigX = new int[4000];
        bigY = new int[4000];
    }

    public int rights;
    public String name;
    public String message;
    public static int spellID = 0;
    private final int[] chatRights;
    public int duelMode;
    private DirectImage[] chatArea;
    private DirectImage chatFrame_fs;
    
    /**
     * Chatbox
     */
    private DirectImage chatBox;
    private DirectImage scrollFill;
    private DirectImage scrollTop;
    private DirectImage scrollMiddle;
    private DirectImage scrollBottom;
    private DirectImage scrollUp;
    private DirectImage scrollDown;
    
    /**
     * Login sprites
     */
    private DirectImage loginBkg;
    private DirectImage loginBox;
    private DirectImage loginBoxHover;
    private DirectImage loginButton;
    private DirectImage loginButtonHover;
    private DirectImage arrow;
    private DirectImage myBox;
    
    /**
     * Switching login sprites
     */
    private int loginInput = 0;
    private int passwordInput = 0;
    private int loginButtonInput = 0;
    
    /**
     * Login Messages
     */
    private String status = "Idle...";
    
    /**
     * Moving Login Components
     */ 
    private int moving = 0;
    
    private IndexedImage loginMenuOverlay;
    private DirectImage[] tabArea;
    private DirectImage tabArea_fs;
    private DirectImage[] mapArea;
    private RSImageProducer leftFrame;
    private RSImageProducer topFrame;
    private int ignoreCount;
    private long aLong824;
    private int[][] walk_dist;
    private int[] friendsNodeIDs;
    private Deque[][][] groundArray;
    private Socket aSocket832;
    private int loginScreenState;
    private RSBuffer aStream_834;
    private NPC[] npcArray;
    private int npcCount;
    private int[] npcIndices;
    private int anInt839;
    private int[] anIntArray840;
    private int anInt841;
    private int anInt842;
    private int anInt843;
    private String aString844;
    private RSBuffer aStream_847;
    private boolean aBoolean848;
    private int hintType;
    public int openInterfaceID;
    private int xCameraPos;
    private int zCameraPos;
    private int yCameraPos;
    private int yCameraCurve;
    private int xCameraCurve;
    private int myPrivilege;
    private final int[] currentExp;
    private IndexedImage[] redStones;
    private IndexedImage mapFlag;
    private IndexedImage mapMarker;
    private final int[] anIntArray873;
    private final boolean[] aBooleanArray876;
    private int weight;
    private String reportAbuseInput;
    private int playerID;
    public CRC32 aCRC32_930;
    private boolean menuOpen;
    private int anInt886;
    private String inputString;
    private final int maxPlayers;
    private final int myPlayerIndex;
    private Player[] playerArray;
    private int playerCount;
    private int[] playerIndices;
    private int anInt893;
    private int[] anIntArray894;
    private RSBuffer[] aStreamArray895s;
    private int viewRotationOffset;
    private int friendsCount;
    private int anInt900;
    private int[][] walk_prev;
    private final int anInt902;
    private byte[] animatedPixels;
    private int anInt913;
    private int crossX;
    private int crossY;
    private int crossIndex;
    private int crossType;
    private int floor_level;
    private final int[] currentStats;
    private final long[] ignoreListAsLongs;
    private boolean loadingError;
    private final int anInt927;
    private final int[] anIntArray928;
    private int[][] anIntArrayArray929;
    private DirectImage aClass30_Sub2_Sub1_Sub1_931;
    private DirectImage aClass30_Sub2_Sub1_Sub1_932;
    private int hintArrowPlayerID;
    private int anInt934;
    private int anInt935;
    private int anInt936;
    private int anInt937;
    private int anInt938;
    private final byte[] chatTypes;
    private final String[] chatNames;
    private final String[] chatMessages;
    private int anInt945;
    private boolean updatesFetched = false;
    private SceneGraph sceneGraph;
    private IndexedImage[] sideIcons;
    private IndexedImage[] chatStones;
    private int menuOffsetX;
    private int menuOffsetY;
    private int menuWidth;
    private int menuHeight;
    private long aLong953;
    private boolean aBoolean954;
    private long[] friendsListAsLongs;
    private int currentSong;
    private static int nodeID = 10;
    static int portOff;
    private static boolean isMembers = true;
    private int spriteDrawX;
    private int spriteDrawY;
    private final int[] anIntArray965 =
    { 0xffff00, 0xff0000, 65280, 65535, 0xff00ff, 0xffffff };
    private final int[] anIntArray968;
    final Decompressor[] decompressors;
    public int variousSettings[];
    private boolean aBoolean972;
    private final int anInt975;
    private final int[] anIntArray976;
    private final int[] anIntArray977;
    private final int[] anIntArray978;
    private final int[] anIntArray979;
    private final int[] chat_color;
    private final int[] chat_effects;
    private final int[] anIntArray982;
    private final String[] aStringArray983;
    private int anInt984;
    private int anInt985;
    private IndexedImage[] hitMarks;
    private int anInt989;
    private final int[] player_outfit_colors;
    private int anInt995;
    private int anInt996;
    private int anInt997;
    private int anInt998;
    private int anInt999;
    private ISAACGenerator encryption;
    private DirectImage mapEdge;
    private final int anInt1002;
    static final short[][] player_outfit_color_array =
    {
    { 6798, 107, 10283, 16, 4797, 7744, 5799, 4634, -31839, 22433, 2983, -11343, 8, 5281, 10438, 3650, -27322, -21845, 200, 571, 908, 21830, 28946, -15701, -14010 },
    { 8741, 12, -1506, -22374, 7735, 8404, 1701, -27106, 24094, 10153, -8915, 4783, 1341, 16578, -30533, 25239, 8, 5281, 10438, 3650, -27322, -21845, 200, 571, 908, 21830, 28946, -15701, -14010 },
    { 25238, 8742, 12, -1506, -22374, 7735, 8404, 1701, -27106, 24094, 10153, -8915, 4783, 1341, 16578, -30533, 8, 5281, 10438, 3650, -27322, -21845, 200, 571, 908, 21830, 28946, -15701, -14010 },
    { 4626, 11146, 6439, 12, 4758, 10270 },
    { 4550, 4537, 5681, 5673, 5790, 6806, 8076, 4574 } };
    private String amountOrNameInput;
    private int pktSize;
    private int opCode;
    private int timeoutCounter;
    private int anInt1010;
    private int anInt1011;
    private Deque aClass19_1013;
    private int anInt1014;
    private int anInt1015;
    private int anInt1016;
    public boolean aBoolean1017;
    private int walkableInterface;
    private boolean walkableInterfaceMode;
    private static final int[] anIntArray1019;
    private int hideMinimap;
    private int loadingStage;
    private IndexedImage[] scrollBar;
    private int anInt1026;
    private final int[] anIntArray1030;
    private boolean char_edit_screen_update = false;
    private IndexedImage[] mapFunctions;
    private int baseX;
    private int baseY;
    private int anInt1036;
    private int anInt1037;
    private int loginFailures;
    private int anInt1039;
    private int dialogID;
    private final int[] maxStats;
    private final int[] anIntArray1045;
    private int anInt1046;
    private boolean gender;
    private int anInt1048;
    private String aString1049;
    private final int[] anIntArray1052;
    private CacheArchive titleStreamLoader;
    private int anInt1054;
    private int drawMultiIcon;
    private Deque stillGraphicDeque;
    private final int[] anIntArray1057;
    public final RSInterface aClass9_1059;
    private IndexedImage[] mapScenes;
    private int currentSound;
    private final int barFillColor;
    private int friendsListAction;
    private int customMenuAddAction;
    private final int[] body_part_list;
    private int mouseInvInterfaceIndex;
    private int lastActiveInvInterface;
    public ResourceProvider resourceProvider;
    private int anInt1069;
    private int anInt1070;
    private int anInt1071;
    private int[] anIntArray1072;
    private int[] anIntArray1073;
    private IndexedImage mapDotItem;
    private IndexedImage mapDotNPC;
    private IndexedImage mapDotPlayer;
    private IndexedImage mapDotFriend;
    private IndexedImage mapDotTeam;
    private int anInt1079;
    private boolean aBoolean1080;
    private String[] friendsList;
    private RSBuffer inStream;
    private int anInt1084;
    private int anInt1085;
    private int activeInterfaceType;
    private int anInt1087;
    private int anInt1088;
    public static int chatScrollPos;
    private final int[] expectedCRCs;
    private int[] menuActionCmd2;
    private int[] menuActionCmd3;
    private int[] menuActionID;
    private int[] menuActionCmd1;
    private IndexedImage[] headIcons;
    private IndexedImage[] skullIcons;
    private IndexedImage multiWay;
    private IndexedImage[] headIconsHint;
    private int anInt1098;
    private int anInt1099;
    private int anInt1100;
    private int anInt1101;
    private int anInt1102;
    private int systemUpdateTime;
    private RSImageProducer loginScreen;
    private String inputTitle;
    private DirectImage compass;
    public static Player myPlayer;
    private final String[] atPlayerActions;
    private final boolean[] atPlayerArray;
    private final int[][][] anIntArrayArrayArray1129;
    public static final int[] tabInterfaceIDs =
    { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 };
    private int cameraOffsetY;
    private int menuActionRow;
    private int spellSelected;
    private IndexedImage infinitySymbol;
    // private IndexedImage[] smileys;
    private int anInt1137;
    private int spellUsableOn;
    private String spellTooltip;
    private IndexedImage[] aClass30_Sub2_Sub1_Sub1Array1140;
    private boolean aBoolean1141;
    private int energy;
    private boolean aBoolean1149;
    private IndexedImage[] crosses;
    boolean needDrawTabArea;
    private static boolean fpsOn;
    public boolean loggedIn;
    public boolean atLoginMenu;
    private boolean canMute;
    private boolean aBoolean1159;
    private boolean aBoolean1160;
    static int loopCycle = 0;
    private static final String validUserPassChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789 ";
    private RSImageProducer inventoryImageProducer;
    private RSImageProducer mapAreaImageProducer;
    private RSImageProducer gameScreenImageProducer;
    private RSImageProducer chatImageProducer;
    private RSSocket socketStream;
    private int anInt1169;
    private int minimapZoom;
    private long aLong1172;
    private String myUsername;
    private String myPassword;
    private boolean genericLoadingError;
    private final int[] anIntArray1177 =
    { 0, 0, 0, 0, 1, 1, 1, 1, 1, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 3 };
    private int reportAbuseInterfaceID;
    private Deque aClass19_1179;
    private int[] chatAreaTexture;
    private int[] tabAreaTexture;
    private int[] mainGameScreenTexture;
    private byte[][] aByteArrayArray1183;
    private int anInt1184;
    private int viewRotation;
    private int anInt1186;
    private int anInt1187;
    public int invOverlayInterfaceID;
    private RSBuffer stream;
    private int splitPrivateChat;
    private IndexedImage mapBack;
    private String[] menuActionName;
    private final int[] anIntArray1203;
    static final short[] anIntArray1204 =
    { 9104, 10275, 7595, 3610, 7975, 8526, 918, -26734, 24466, 10145, -6882, 5027, 1457, 16565, -30545, 25486, 24, 5392, 10429, 3673, -27335, -21957, 192, 687, 412, 21821, 28835, -15460, -14019 };
    private final int[] sound;
    private int minimapRotation;
    public static int chatScrollMax;
    private String promptInput;
    private int anInt1213;
    private int[][][] intGroundArray;
    private long aLong1215;
    private int loginScreenCursorPos;
    private final DirectImage[] modIcons;
    public static int tabID;
    private int hintArrowNPCID;
    public static boolean inputTaken;
    private int inputDialogState;
    private int nextSong;
    private final int[] anIntArray1229;
    private CollisionMap[] collision_maps;
    public static int anIntArray1232[];
    private int[] anIntArray1234;
    private int[] anIntArray1235;
    private int[] anIntArray1236;
    public final int anInt1239 = 100;
    private final int[] anIntArray1240;
    private final int[] soundType;
    private boolean aBoolean1242;
    private int atInventoryLoopCycle;
    private int atInventoryInterface;
    private int atInventoryIndex;
    private int atInventoryInterfaceType;
    private byte[][] aByteArrayArray1247;
    private int chat_colors_config;
    private final int[] soundDelay;
    private final int[] soundVolume;
    private int anInt1251;
    private final boolean rsAlreadyLoaded;
    private int mouse_buttons;
    private boolean welcomeScreenRaised;
    private boolean messagePromptRaised;
    private int anInt1257;
    private byte[][][] byteGroundArray;
    private int previousSong;
    private int destX;
    private int destY;
    private DirectImage miniMap;
    private int anInt1264;
    private int anInt1265;
    private String loginMessage1;
    private String loginMessage2;
    private int bigRegionX;
    private int bigRegionY;
    private TextDrawingArea smallText;
    public TextDrawingArea regularText;
    public TextDrawingArea chatText;
    private int backDialogID;
    private UpdateFeed updateFeed;
    private int cameraOffsetX;
    private int[] bigX;
    private int[] bigY;
    private int itemSelected;
    private int anInt1283;
    private int anInt1284;
    private int anInt1285;
    private String selectedItemName;
    public static String server = "";
    public int drawCount;
    public int fullscreenInterfaceID;
    public boolean draw_sprites_logon;
    public int anInt1044;
    public int anInt1129;
    public int anInt1315;
    public int anInt1500;
    public int anInt1501;
    public int[] fullScreenTextureArray;
    public static ArrayList<Bubble> bubbles = new ArrayList<Bubble>();

    public void resetAllImageProducers()
    {
        if (super.fullGameScreen != null)
        {
            return;
        }
        System.err.println("resetAllImageProducers - called");
        chatImageProducer = null;
        mapAreaImageProducer = null;
        inventoryImageProducer = null;
        gameScreenImageProducer = null;
        loginScreen = null;
        super.fullGameScreen = new RSImageProducer((clientSize == CLIENT_FIXED ? 765 : clientWidth), (clientSize == CLIENT_FIXED ? 503 : clientHeight), getGameComponent());
        welcomeScreenRaised = true;
    }
    
    public static boolean isWidget()
    {
        return widget;
    }

    static
    {
        anIntArray1019 = new int[99];
        int i = 0;
        for (int j = 0; j < 99; j++)
        {
            int l = j + 1;
            int i1 = (int) ((double) l + 300D * Math.pow(2D, (double) l / 7D));
            i += i1;
            anIntArray1019[j] = i / 4;
        }
        anIntArray1232 = new int[32];
        i = 2;
        for (int k = 0; k < 32; k++)
        {
            anIntArray1232[k] = i - 1;
            i += i;
        }
    }
}
