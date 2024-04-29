package com.ixume.karta.screen;

import com.ixume.karta.dataloading.SpriteManager;
import com.ixume.karta.gui.*;
import com.ixume.karta.render.ActiveSectionUtils;
import com.ixume.karta.render.MapScreenRenderer;
import com.ixume.karta.render.RenderUtils;
import com.ixume.karta.render.TextRenderer;
import com.ixume.karta.render.background.ImageBackground;
import org.bukkit.entity.Player;
import org.bukkit.map.MinecraftFont;

import java.util.ArrayList;

public class AlchemistScreen extends MapScreen {
    public AlchemistScreen(Player player, int width, int height) {
        super(player, width, height);
    }

    @Override
    protected void initElements() {
        ImageElement potion = new ImageElement(this, 4, 288, 268, SpriteManager.getSprite("potion"), 4);
        registerElement(potion);

        TextElement alchemistTitle = new TextElement(this, 4, 320 - MinecraftFont.Font.getWidth("Alchemist") * 2 - 2, 244, "Alchemist", (byte) 34, true, 4);
        registerElement(alchemistTitle);

        ImageElement brutalBaseImage = new ImageElement(this, 1, 140, 160, SpriteManager.getSprite("brutalize"), 2);
        registerElement(brutalBaseImage);

        TextElement brutalTitle = new TextElement(this, 2, 174, 174, "Brutal Alchemy", (byte) 34, true, 2);
        registerElement(brutalTitle);

        MapElement skillLevel = new MapElement(this, 4, 174, 160) {
            @Override
            public boolean[] updateActiveSections() {
                activeSections = ActiveSectionUtils.sectionsFromRectangle(mapScreen, x, y, x + 128 + 12, y + 8);
                return activeSections;
            }

            @Override
            public void drawElement() {
                RenderUtils.drawImage(mapScreen, x, y, SpriteManager.getSprite("skill_base"), 2);
                RenderUtils.drawImage(mapScreen, x, y, SpriteManager.getSprite("skill_selected"), 2);
            }
        };

        registerElement(skillLevel);

        java.util.List<String> lines = new ArrayList<>();
        lines.addAll(TextRenderer.wrapText("Level 1: Your Brutal Alchemist's Potions now apply a Damage Over Time effect which does 20% base damage as magic damage every second, over 8 seconds.\nLevel 2: The Damage Over Time effect now does 35% base damage as magic damage.\nEnhancement: Your Brutal Alchemist's Potions now apply a second Damage Over Time effect which does 20% base damage as magic damage every 2 seconds, over 8 seconds.", 160, 1));

        CursorTooltip tooltip =  new ScrollTooltip(this, 5, 0, 0, lines);
        TooltippedArea brutalTooltip = new TooltippedArea(this, 4, 140, 160, 128, 32, tooltip);

        registerElement(brutalTooltip);
    }

    @Override
    public void setRenderer() {
        this.renderer = new MapScreenRenderer(this, getWidth(), getHeight(), new ImageBackground(SpriteManager.getSprite("scroll_background"), this, 4));
    }
}
