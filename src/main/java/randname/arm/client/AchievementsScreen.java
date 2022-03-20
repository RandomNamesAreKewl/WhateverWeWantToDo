package randname.arm.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.lwjgl.system.CallbackI;

import java.io.*;
import java.util.Scanner;

public class AchievementsScreen extends Screen {
    public static class Achievement {
        Text title;
        Text description;
        int icon;
        int repeats;

        Achievement(Text title, Text description, int icon) {
            this.title = title;
            this.description = description;
            this.icon = icon;
            this.repeats = 1;
        }

        Achievement(Text title, Text description, int icon, int repeats) {
            this.title = title;
            this.description = description;
            this.icon = icon;
            this.repeats = repeats;
        }
    }
    int[] repeats;
    public static final Identifier ACHIEVEMENT_BACKGROUND_TEXTURE = new Identifier("textures/gui/achievement_background.png");
    Screen parent;
    public static Achievement[] achievements = new Achievement[] {
            new Achievement(Text.of("Taking Inventory"), Text.of("Open your inventory."), 1),
            new Achievement(Text.of("Getting Wood"), Text.of("Punch a tree until the block of wood pops out."), 2),
            new Achievement(Text.of("Builder"), Text.of("Place 100 Blocks"), 3, 100),
            new Achievement(Text.of("Great Builder"), Text.of("Place 200 Blocks"), 3, 500),
            new Achievement(Text.of("Master Builder"), Text.of("Place 1000 Blocks"), 3, 1000)
    };
    int scroll = 0;

    public AchievementsScreen(Screen parent) {
        super(Text.of("Achievements"));
        this.parent = parent;
        MinecraftClient.getInstance().getTextureManager().getTexture(ACHIEVEMENT_BACKGROUND_TEXTURE);
    }

    public static int[] ReadAchievementData() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader("achievements.txt"));
            int[] LoadedStats = new int[achievements.length];

            for (int i=0; i<achievements.length; i++) {
                LoadedStats[i] = Integer.parseInt(reader.readLine());
            }
            reader.close();
            return LoadedStats;
        } catch (FileNotFoundException e) {
            // Write a new achievements file if it does not exist already
            try {
                BufferedWriter writer = new BufferedWriter(new FileWriter("achievements.txt"));
                for(int i=0; i<achievements.length; i++) {
                    writer.write("0\n");
                }
                writer.close();
                return ReadAchievementData();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    @Override
    protected void init() {
        // Load in achievement data
        repeats = ReadAchievementData();

        this.addDrawableChild(new ButtonWidget(this.width / 2 - 100, this.height - 20, 200, 20, ScreenTexts.DONE, (button) -> MinecraftClient.getInstance().setScreen(this.parent)));
    }

    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 15, 16777215);
        for(var i=0; i<achievements.length; i++) {
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderTexture(0, ACHIEVEMENT_BACKGROUND_TEXTURE);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.enableDepthTest();
            this.drawTexture(matrices, 0, 43 + i * 55 + scroll, 0, 0, this.width, 50);
            this.drawTexture(matrices, this.width / 8, 43 + i * 55 + 3 + scroll, 0, 50, 44, 44);
            this.drawTexture(matrices, this.width / 8, 43 + i * 55 + 3 + scroll, (achievements[i].icon % 5) * 44, 50 + (achievements[i].icon / 5) * 44, 44, 44);
            if(repeats[i] < achievements[i].repeats) {
                int progress = (int)((((float)repeats[i]) / ((float)achievements[i].repeats)) * 44);
                this.drawTexture(matrices, this.width / 8 + progress, 43 + i * 55 + 3 + scroll, 0, 50, 44 - progress, 44);
                this.drawTexture(matrices, this.width / 8 + 34, 43 + i * 55 + 3 + scroll + 30, 220, 50, 10, 14);
            }

            drawTextWithShadow(matrices, this.textRenderer, achievements[i].title, this.width / 8 + 49, 48 + i * 55 + scroll, 16777215);
            drawTextWithShadow(matrices, this.textRenderer, achievements[i].description, this.width / 8 + 49, 48 + i * 55 + 16 + scroll, 16777215);
        }
        super.render(matrices, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        scroll += (mouseY / 10) * amount;
        if(scroll < -achievements.length * 55 + this.height - 90)
            scroll = -achievements.length * 55 + this.height - 90;
        if(scroll > 0)
            scroll = 0;
        return super.mouseScrolled(mouseX, mouseY, amount);
    }
}
