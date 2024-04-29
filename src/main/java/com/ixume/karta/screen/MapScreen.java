package com.ixume.karta.screen;


import com.ixume.karta.IDManager;
import com.ixume.karta.Main;
import com.ixume.karta.commands.ScreenCommandManager;
import com.ixume.karta.gui.MapElement;
import com.ixume.karta.render.MapScreenInteractionManager;
import com.ixume.karta.render.MapScreenRenderer;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.core.Vec3i;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import net.minecraft.network.protocol.game.ClientboundSetTimePacket;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.joml.Vector3d;

import java.util.ArrayList;
import java.util.List;

public abstract class MapScreen {
    private final Player player; //is the one who the screen is being showed to
    private VirtualHorse virtualHorse; //used to keep the player in place
    private final int width; //measured in sections
    private final int height; //measured in sections
    private final int doubleSwingThreshold;
    private final List<IndividualMap> maps; //ordered by each x row then next y value; bottom left start
    private final Cursor cursor;
    public List<MapElement> elements;
    public List<MapElement> elementsToAdd;
    protected MapScreenInteractionManager interactionManager;
    protected MapScreenRenderer renderer;
    private int timeSinceSwing;

    public MapScreen(Player player, int width, int height) {
        this.player = player;
        this.width = width;
        this.height = height;
        cursor = new Cursor(this);
        elements = new ArrayList<>();
        elementsToAdd = new ArrayList<>();
        doubleSwingThreshold = 15;
        timeSinceSwing = -1;
        maps = new ArrayList<>();
    }

    public void init() {
        initEnvironment();
        ScreenCommandManager.enableTextInput(player);
        setInteractionManager();
        setRenderer();
        initElements();
        addElements();
        renderer.init();
    }

    private void addElements() {
        elements.addAll(elementsToAdd);
        elementsToAdd.clear();
    }

    public void registerElement(MapElement element) {
        elementsToAdd.add(element);
    }

    protected void initEnvironment() {
        Vector3d roundedPos = Main.getInstance().getLocationManager().intakePlayer(player);
        //adjust for horse mount offset + eye height in y-coord
        Vec3i origin = new Vec3i((int) (Math.floor(roundedPos.x - Math.floor(width / 2f) - (width % 2) * 0.5f - 1)),
                (int) (roundedPos.y),
                (int) (roundedPos.z + 1));

        this.virtualHorse = new VirtualHorse(player, new Vector3d(roundedPos.x - (width % 2) * 0.5f - 1,
                roundedPos.y - 1.15625 - (height % 2) * 0.5f + height / 2f - 2,
                roundedPos.z));

        virtualHorse.lock();

        for (int y = 0; y < height; y++) {
            for (int x = width - 1; x >= 0; x--) { //start at bottom left corner
                IndividualMap map = new IndividualMap(player, IDManager.getMapID(), origin.offset(x, y, 0));
                maps.add(map);
            }
        }

        ((CraftPlayer) player).getHandle().connection.send(new ClientboundSetTimePacket(player.getWorld().getGameTime(), 6000, false));
    }

    protected abstract void initElements();

    public Player getPlayer() {
        return player;
    }

    public List<IndividualMap> getMaps() {
        return maps;
    }

    public MapScreenInteractionManager getInteractionManager() {
        return interactionManager;
    }

    protected void setInteractionManager() {
        this.interactionManager = new MapScreenInteractionManager(this);
    }

    protected abstract void setRenderer();

    public Cursor getCursor() {
        return cursor;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void tick() {
        if (timeSinceSwing > -1) {
            if (timeSinceSwing > doubleSwingThreshold) timeSinceSwing = -1;
            else timeSinceSwing++;
        }

        addElements();
        interactionManager.tick(elements);
        renderer.tick();
        interactionManager.resetInputs();
    }

    public void onPlayerDisconnect() {

    }

    public void closeScreen() {
        ((CraftPlayer) player).getHandle().connection.send(new ClientboundSetTimePacket(player.getWorld().getGameTime(), player.getPlayerTime(), false));
        virtualHorse.unlock();
        //destroy maps
        CraftPlayer craftPlayer = ((CraftPlayer) player);

        craftPlayer.getHandle().connection.send(new ClientboundRemoveEntitiesPacket(IntList.of(maps.stream().mapToInt(IndividualMap::getFrameID).toArray())));
        player.updateInventory();

        ScreenCommandManager.disableTextInput(player);
    }

    public void sendFull() {
        for (IndividualMap map : maps) {
            map.sendSpawnPacket();
        }
    }

    public void onPlayerMove(float xRot, float yRot) {
        cursor.cursorInput(xRot, yRot);
        interactionManager.mouseMoved(cursor.getX(), cursor.getY());
    }

    public void onPlayerSwing() {
        if (timeSinceSwing == -1) {
            interactionManager.leftClick();
            onLeftClick();
            timeSinceSwing = 0;
        } else {
            onLeftClick();
            onDoubleClick();
            timeSinceSwing = -1;
        }
    }

    public void onScroll(boolean scroll) {

    }

    public void onPlayerTextInput(String input) {
        interactionManager.onTextInput(input);
    }

    public void onLeftClick() {

    }

    public void onDoubleClick() {

    }
}
