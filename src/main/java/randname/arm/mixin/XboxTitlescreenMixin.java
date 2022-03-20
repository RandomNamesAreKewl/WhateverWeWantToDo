package randname.arm.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.option.OptionsScreen;
import net.minecraft.client.gui.screen.world.SelectWorldScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import randname.arm.client.AchievementsScreen;

@Mixin(TitleScreen.class)
public abstract class XboxTitlescreenMixin extends Screen {
    protected XboxTitlescreenMixin(Text title) {
        super(title);
    }

    @Inject(at = @At("HEAD"), method = "init", cancellable = true)
    private void init(CallbackInfo ci) {
        int y = this.height / 4 + 24;
        int spacing = 24;
        this.addDrawableChild(new ButtonWidget(this.width / 2 - 100, y, 200, 20, new TranslatableText("menu.singleplayer"), (button) -> {
            MinecraftClient.getInstance().setScreen(new SelectWorldScreen(this));
        }));
        this.addDrawableChild(new ButtonWidget(this.width / 2 - 100, y + spacing, 200, 20, new TranslatableText("menu.multiplayer"), (button) -> {
            MinecraftClient.getInstance().setScreen(new MultiplayerScreen(this));
        }));
        //this.addDrawableChild(new ButtonWidget(this.width / 2 - 100, y + spacing * 2, 200, 20, Text.of("Leaderboards"), (button) -> {}));
        this.addDrawableChild(new ButtonWidget(this.width / 2 - 100, y + spacing * 2, 200, 20, Text.of("Achievements"), (button) -> {
            MinecraftClient.getInstance().setScreen(new AchievementsScreen(this));
        }));
        this.addDrawableChild(new ButtonWidget(this.width / 2 - 100, y + spacing * 3, 200, 20, Text.of("Help & Options"), (button) -> {
            MinecraftClient.getInstance().setScreen(new OptionsScreen(this, MinecraftClient.getInstance().options));
        }));
        //this.addDrawableChild(new ButtonWidget(this.width / 2 - 100, y + spacing * 4, 200, 20, Text.of("Minecraft Store"), (button) -> {}));
        this.addDrawableChild(new ButtonWidget(this.width / 2 - 100, y + spacing * 4, 200, 20, Text.of("Exit Game"), (button) -> {
            MinecraftClient.getInstance().scheduleStop();
        }));
        ci.cancel();
    }
}
