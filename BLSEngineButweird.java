import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class SmoothPanelGUI extends Gui {
    // idk if this works lol
    private final Minecraft mc;
    private boolean isOpen = false;
    private boolean isAnimating = false;
    private long animationStartTime = 0;
    private boolean openingAnimation = false;
    
    private float panelX, panelY, panelWidth, panelHeight;
    private float startX, startY, startWidth, startHeight;
    private float targetX, targetY, targetWidth, targetHeight;
    
    private float buttonX, buttonY, buttonWidth = 200, buttonHeight = 50;
    
    private List<GuiButton> panelButtons = new ArrayList<>();
    
    public SmoothPanelGUI(Minecraft mc) {
        this.mc = mc;
        ScaledResolution sr = new ScaledResolution(mc);
        
        // Buton pozisyonunu ortala
        buttonX = (sr.getScaledWidth() - buttonWidth) / 2;
        buttonY = (sr.getScaledHeight() - buttonHeight) / 2;
        
        // Panel başlangıç pozisyonu butonla aynı
        panelX = buttonX;
        panelY = buttonY;
        panelWidth = buttonWidth;
        panelHeight = buttonHeight;
    }
    
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        ScaledResolution sr = new ScaledResolution(mc);
        
        // Buton çizimi
        drawButton(mouseX, mouseY, partialTicks);
        
        // Animasyon hesaplama
        if (isAnimating) {
            updateAnimation(partialTicks);
        }
        
        // Panel çizimi
        if (isOpen || isAnimating) {
            drawPanel(mouseX, mouseY, partialTicks);
        }
        
        // Panel butonlarını çiz
        if (isOpen) {
            for (GuiButton button : panelButtons) {
                button.drawButton(mc, mouseX, mouseY);
            }
        }
    }
    
    private void drawButton(int mouseX, int mouseY, float partialTicks) {
        // Buton rengi (hover durumuna göre)
        int color = isMouseOverButton(mouseX, mouseY) ? 0xFF3a3a3a : 0xFF2a2a2a;
        
        // Buton dikdörtgeni
        drawRoundedRect(buttonX, buttonY, buttonWidth, buttonHeight, 10, color);
        
        // Buton metni
        String text = "Multiplayer";
        int textWidth = mc.fontRendererObj.getStringWidth(text);
        mc.fontRendererObj.drawStringWithShadow(
            text,
            buttonX + (buttonWidth - textWidth) / 2,
            buttonY + (buttonHeight - 8) / 2,
            0xFFFFFFFF
        );
    }
    
    private void drawPanel(int mouseX, int mouseY, float partialTicks) {
        // Panel arkaplanı
        drawRoundedRect(panelX, panelY, panelWidth, panelHeight, 12, 0x66282828);
        
        // Kapatma butonu
        boolean closeHovered = isMouseOverClose(mouseX, mouseY);
        String closeText = "✕";
        int closeWidth = mc.fontRendererObj.getStringWidth(closeText);
        float closeX = panelX + panelWidth - 20 - closeWidth;
        float closeY = panelY + 15;
        
        mc.fontRendererObj.drawStringWithShadow(
            closeText,
            closeX,
            closeY,
            closeHovered ? 0xFFFF5555 : 0xFFFFFFFF
        );
        
        // Panel içeriği (sadece açıkken)
        if (isOpen && !isAnimating) {
            // Başlık
            String title = "No content";
            int titleWidth = mc.fontRendererObj.getStringWidth(title);
            mc.fontRendererObj.drawStringWithShadow(
                title,
                panelX + (panelWidth - titleWidth) / 2,
                panelY + 60,
                0xFFFFFFFF
            );
            
            // Mock GUI elemanları
            float mockWidth = panelWidth * 0.6f;
            float mockHeight = 50;
            float mockX = panelX + (panelWidth - mockWidth) / 2;
            
            // Mock 1
            drawRoundedRect(mockX, panelY + 100, mockWidth, mockHeight, 10, 0xFF333333);
            mc.fontRendererObj.drawStringWithShadow(
                "bleeh bleh blah",
                mockX + (mockWidth - mc.fontRendererObj.getStringWidth("bleeh bleh blah")) / 2,
                panelY + 100 + (mockHeight - 8) / 2,
                0xFFDDDDDD
            );
            
            // Mock 2
            drawRoundedRect(mockX, panelY + 170, mockWidth, mockHeight, 10, 0xFF333333);
            mc.fontRendererObj.drawStringWithShadow(
                "i like femboys",
                mockX + (mockWidth - mc.fontRendererObj.getStringWidth("i like femboys")) / 2,
                panelY + 170 + (mockHeight - 8) / 2,
                0xFFDDDDDD
            );
        }
    }
    
    private void updateAnimation(float partialTicks) {
        long currentTime = System.currentTimeMillis();
        float elapsed = (currentTime - animationStartTime) / 550f; // 550ms animasyon süresi
        
        if (elapsed >= 1.0f) {
            // Animasyon tamamlandı
            isAnimating = false;
            panelX = targetX;
            panelY = targetY;
            panelWidth = targetWidth;
            panelHeight = targetHeight;
            
            if (!openingAnimation) {
                isOpen = false;
            }
            return;
        }
        
        // Cubic bezier easing: (0.15, 0.85, 0.25, 1)
        float progress = cubicBezier(elapsed, 0.15f, 0.85f, 0.25f, 1.0f);
        
        // Interpolasyon
        panelX = startX + (targetX - startX) * progress;
        panelY = startY + (targetY - startY) * progress;
        panelWidth = startWidth + (targetWidth - startWidth) * progress;
        panelHeight = startHeight + (targetHeight - startHeight) * progress;
    }
    
    private float cubicBezier(float t, float p0, float p1, float p2, float p3) {
        float u = 1 - t;
        float tt = t * t;
        float uu = u * u;
        float uuu = uu * u;
        float ttt = tt * t;
        
        return uuu * p0 + 3 * uu * t * p1 + 3 * u * tt * p2 + ttt * p3;
    }
    
    private void drawRoundedRect(float x, float y, float width, float height, float radius, int color) {
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        
        float alpha = (color >> 24 & 0xFF) / 255.0F;
        float red = (color >> 16 & 0xFF) / 255.0F;
        float green = (color >> 8 & 0xFF) / 255.0F;
        float blue = (color & 0xFF) / 255.0F;
        
        GlStateManager.color(red, green, blue, alpha);
        
        // Yuvarlatılmış dikdörtgen çizimi
        drawRoundedRectInternal(x, y, width, height, radius);
        
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }
    
    private void drawRoundedRectInternal(float x, float y, float width, float height, float radius) {
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        
        worldrenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);
        
        // Ana dikdörtgen
        worldrenderer.pos(x + radius, y, 0).endVertex();
        worldrenderer.pos(x + width - radius, y, 0).endVertex();
        worldrenderer.pos(x + width - radius, y + height, 0).endVertex();
        worldrenderer.pos(x + radius, y + height, 0).endVertex();
        
        // Sol dikdörtgen
        worldrenderer.pos(x, y + radius, 0).endVertex();
        worldrenderer.pos(x + radius, y + radius, 0).endVertex();
        worldrenderer.pos(x + radius, y + height - radius, 0).endVertex();
        worldrenderer.pos(x, y + height - radius, 0).endVertex();
        
        // Sağ dikdörtgen
        worldrenderer.pos(x + width - radius, y + radius, 0).endVertex();
        worldrenderer.pos(x + width, y + radius, 0).endVertex();
        worldrenderer.pos(x + width, y + height - radius, 0).endVertex();
        worldrenderer.pos(x + width - radius, y + height - radius, 0).endVertex();
        
        tessellator.draw();
        
        // Köşe yuvarlakları (dört çeyrek daire)
        drawQuarterCircle(x + radius, y + radius, radius, 180, 270);
        drawQuarterCircle(x + width - radius, y + radius, radius, 270, 360);
        drawQuarterCircle(x + radius, y + height - radius, radius, 90, 180);
        drawQuarterCircle(x + width - radius, y + height - radius, radius, 0, 90);
    }
    
    private void drawQuarterCircle(float x, float y, float radius, int startAngle, int endAngle) {
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        
        worldrenderer.begin(GL11.GL_TRIANGLE_FAN, DefaultVertexFormats.POSITION);
        worldrenderer.pos(x, y, 0).endVertex();
        
        for (int i = startAngle; i <= endAngle; i++) {
            double angle = Math.toRadians(i);
            worldrenderer.pos(x + Math.sin(angle) * radius, y + Math.cos(angle) * radius, 0).endVertex();
        }
        
        tessellator.draw();
    }
    
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (mouseButton == 0) {
            ScaledResolution sr = new ScaledResolution(mc);
            
            // Butona tıklanırsa
            if (isMouseOverButton(mouseX, mouseY)) {
                if (isAnimating) {
                    // Animasyon devam ediyorsa, ters yönde animasyon başlat
                    if (isOpen) {
                        startClosingAnimation();
                    } else {
                        startOpeningAnimation();
                    }
                } else if (isOpen) {
                    // Panel açıksa, kapat
                    startClosingAnimation();
                } else {
                    // Panel kapalıysa, aç
                    startOpeningAnimation();
                }
                return;
            }
            
            // Kapatma butonuna tıklanırsa
            if (isOpen && isMouseOverClose(mouseX, mouseY)) {
                startClosingAnimation();
                return;
            }
            
            // Panel butonlarına tıklama
            if (isOpen) {
                for (GuiButton button : panelButtons) {
                    if (button.mousePressed(mc, mouseX, mouseY)) {
                        button.playPressSound(mc.getSoundHandler());
                        // Buton aksiyonları buraya
                        break;
                    }
                }
            }
        }
    }
    
    private void startOpeningAnimation() {
        if (isAnimating && currentAnimation != null) {
            currentAnimation.cancel();
        }
        
        isAnimating = true;
        openingAnimation = true;
        animationStartTime = System.currentTimeMillis();
        
        ScaledResolution sr = new ScaledResolution(mc);
        
        // Başlangıç pozisyonu (buton)
        startX = buttonX;
        startY = buttonY;
        startWidth = buttonWidth;
        startHeight = buttonHeight;
        
        // Hedef pozisyon (panel)
        targetX = sr.getScaledWidth() * 0.05f;
        targetY = sr.getScaledHeight() * 0.05f;
        targetWidth = sr.getScaledWidth() * 0.90f;
        targetHeight = sr.getScaledHeight() * 0.85f;
        
        // Anlık pozisyonu başlangıca ayarla
        panelX = startX;
        panelY = startY;
        panelWidth = startWidth;
        panelHeight = startHeight;
        
        isOpen = true;
    }
    
    private void startClosingAnimation() {
        if (isAnimating && currentAnimation != null) {
            currentAnimation.cancel();
        }
        
        isAnimating = true;
        openingAnimation = false;
        animationStartTime = System.currentTimeMillis();
        
        // Başlangıç pozisyonu (panel)
        startX = panelX;
        startY = panelY;
        startWidth = panelWidth;
        startHeight = panelHeight;
        
        // Hedef pozisyon (buton)
        targetX = buttonX;
        targetY = buttonY;
        targetWidth = buttonWidth;
        targetHeight = buttonHeight;
    }
    
    private boolean isMouseOverButton(int mouseX, int mouseY) {
        return mouseX >= buttonX && mouseX <= buttonX + buttonWidth &&
               mouseY >= buttonY && mouseY <= buttonY + buttonHeight;
    }
    
    private boolean isMouseOverClose(int mouseX, int mouseY) {
        String closeText = "✕";
        int closeWidth = mc.fontRendererObj.getStringWidth(closeText);
        float closeX = panelX + panelWidth - 20 - closeWidth;
        float closeY = panelY + 15;
        
        return mouseX >= closeX && mouseX <= closeX + closeWidth &&
               mouseY >= closeY && mouseY <= closeY + 10;
    }
    
    // HTML'deki currentAnimation değişkeninin Java karşılığı
    private Object currentAnimation = null;
    
    // Bu sınıfı GUI'nizde kullanmak için
    public void render() {
        ScaledResolution sr = new ScaledResolution(mc);
        int mouseX = Mouse.getX() * sr.getScaledWidth() / mc.displayWidth;
        int mouseY = sr.getScaledHeight() - Mouse.getY() * sr.getScaledHeight() / mc.displayHeight - 1;
        
        drawScreen(mouseX, mouseY, mc.getRenderPartialTicks());
    }
}