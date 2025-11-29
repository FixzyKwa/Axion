import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

public class CustomMainMenu extends GuiScreen {
    private GuiButton animatedButton;
    private boolean expanded = false;
    private float animProgress = 0f;
    private final float ANIM_SPEED = 0.025f;

    @Override
    public void initGui() {
        int centerX = this.width / 2 - 100;
        int centerY = this.height / 2 - 20;
        animatedButton = new GuiButton(1, centerX, centerY, 200, 40, "Singleplayer");
        this.buttonList.add(animatedButton);
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if(button.id == 1) expanded = !expanded;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {

        // Animation update
        if(expanded && animProgress < 1f) animProgress += ANIM_SPEED;
        if(!expanded && animProgress > 0f) animProgress -= ANIM_SPEED;
        animProgress = Math.max(0f, Math.min(animProgress, 1f));

        // Background overlay effect (HTML’deki blur yok ama opacity overlay var)
        this.drawGradientRect(0, 0, this.width, this.height, 0xFFAA0000, 0xFF550000); // kırmızı arka plan
        if(animProgress > 0) {
            this.drawGradientRect(0, 0, this.width, this.height, 0x66000000, 0x66000000);
        }

        // Lerp animation calculations
        int startX = this.width / 2 - 100;
        int startY = this.height / 2 - 20;
        int targetX = (int)(this.width * 0.05f);
        int targetY = (int)(this.height * 0.05f);
        int targetW = (int)(this.width * 0.90f);
        int targetH = (int)(this.height * 0.90f);

        animatedButton.xPosition = (int)(startX + (targetX - startX) * animProgress);
        animatedButton.yPosition = (int)(startY + (targetY - startY) * animProgress);
        animatedButton.width    = (int)(200 + (targetW - 200) * animProgress);
        animatedButton.height   = (int)(40  + (targetH - 40)  * animProgress);

        // Draw button
        animatedButton.drawButton(Minecraft.getMinecraft(), mouseX, mouseY);

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}