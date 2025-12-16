import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;

import java.io.IOException;

public class GuiRestrictedBuildWarning extends GuiScreen {

    private final GuiScreen parent;

    public GuiRestrictedBuildWarning(GuiScreen parent) {
        this.parent = parent;
    }

    @Override
    public void initGui() {
        this.buttonList.clear();

        ScaledResolution sr = new ScaledResolution(mc);
        int centerX = sr.getScaledWidth() / 2;
        int centerY = sr.getScaledHeight() / 2;

        this.buttonList.add(
                new GuiButton(
                        0,
                        centerX - 75,
                        centerY + 85,
                        150,
                        20,
                        "I Understand"
                )
        );
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.id == 0) {
            mc.displayGuiScreen(parent);
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();

        ScaledResolution sr = new ScaledResolution(mc);
        int centerX = sr.getScaledWidth() / 2;
        int y = sr.getScaledHeight() / 2 - 110;

        drawCenteredString(fontRendererObj, "§c§lRESTRICTED BUILD DETECTED", centerX, y, 0xFFFFFF);
        y += 18;

        drawCenteredString(fontRendererObj,
                "You are running a §fSecurity Research Build§7.",
                centerX, y, 0xAAAAAA);
        y += 15;

        drawCenteredString(fontRendererObj,
                "This session will be §cterminated within 24 hours§7.",
                centerX, y, 0xAAAAAA);
        y += 25;

        drawCenteredString(fontRendererObj, "§cDISABLED FEATURES", centerX, y, 0xFFFFFF);
        y += 14;

        String[] disabled = {
                "Friends & invitations",
                "Multiplayer chat",
                "Log storage",
                "Screenshots & recordings",
                "Sharing or publishing content",
                "Public server access"
        };

        for (String s : disabled) {
            drawCenteredString(fontRendererObj, "§7- " + s, centerX, y, 0xAAAAAA);
            y += 12;
        }

        y += 10;
        drawCenteredString(fontRendererObj, "§cMONITORED ACTIVITIES", centerX, y, 0xFFFFFF);
        y += 14;

        String[] detects = {
                "Cheats",
                "Screenshots",
                "Screen recordings",
                "Tracking attempts",
                "Unauthorized sessions",
                "Unknown or suspicious IP addresses"
        };

        for (String s : detects) {
            drawCenteredString(fontRendererObj, "§7- " + s, centerX, y, 0xAAAAAA);
            y += 12;
        }

        y += 14;
        drawCenteredString(fontRendererObj,
                "§cUnauthorized use may result in permanent access revocation",
                centerX, y, 0xFF5555);
        y += 12;

        drawCenteredString(fontRendererObj,
                "§cand legal action.",
                centerX, y, 0xFF5555);
        y += 20;

        drawCenteredString(fontRendererObj,
                "§8Secured by §b§lFlexy",
                centerX, y, 0xAAAAAA);

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        // ESC engelli
    }

    @Override
    public boolean doesGuiPauseGame() {
        return true;
    }
}