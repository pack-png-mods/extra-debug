package net.earthcomputer.extradebug;

import java.util.*;

import net.minecraft.src.EntityPlayerSP;
import net.minecraft.src.FontRenderer;
import net.minecraft.src.Tessellator;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import net.minecraft.client.Minecraft;

public class ExtraDebug {

    public static void extraDebug(Minecraft mc) {
        List<String> extraLines = new ArrayList<>();
        EntityPlayerSP player = mc.field_6322_g;

        extraLines.add(String.format("Pos: %.4f / %.4f / %.4f (%.4f feet)", player.posX, player.posY, player.posZ, player.boundingBox.minY));
        extraLines.add(String.format("Chunk: %d / %d In Chunk %d / %d", floor(player.posX) & 15, floor(player.posZ) & 15, floor(player.posX) >> 4, floor(player.posZ) >> 4));
        extraLines.add(String.format("Rot: %.4f / %.4f Facing: %s", normalizeYaw(player.rotationYaw), player.rotationPitch, getFacingString(player.rotationYaw)));

        FontRenderer fontRenderer = mc.field_6314_o;
        for (int i = 0; i < extraLines.size(); i++) {
            fontRenderer.drawStringWithShadow(extraLines.get(i), 2, 42 + 10 * i, 0xffffff);
        }
    }

    static int floor(double x) {
        int i = (int) x;
        return x < i ? i - 1 : i;
    }

    static float normalizeYaw(float yaw) {
        yaw %= 360;
        if (yaw < -180)
            yaw += 360;
        else if (yaw >= 180)
            yaw -= 360;
        return yaw;
    }

    static String getFacingString(float yaw) {
        switch ((int) (normalizeYaw(yaw + 45) + 180) / 90) {
            case 2: return "South (Towards Positive Z)";
            case 3: return "West (Towards Negative X)";
            case 0: return "North (Towards Negative Z)";
            case 1: return "East (Towards Positive X)";
            default: throw new AssertionError();
        }
    }

    public static void renderChunkBorders(Minecraft mc, float partialTicks) {
        if (!Keyboard.isKeyDown(61)) // f3
            return;

        EntityPlayerSP player = mc.field_6322_g;
        double x = player.prevPosX + (player.posX - player.prevPosX) * partialTicks;
        double y = player.prevPosY + (player.posY - player.prevPosY) * partialTicks;
        double z = player.prevPosZ + (player.posZ - player.prevPosZ) * partialTicks;

        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glShadeModel(GL11.GL_SMOOTH);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glAlphaFunc(GL11.GL_GREATER, 0.1f);
        Tessellator tess = Tessellator.instance;
        double bottom = -y;
        double top = 128 - y;
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
        double originX = (floor(x) >> 4 << 4) - x;
        double originZ = (floor(z) >> 4 << 4) - z;
        GL11.glLineWidth(1);
        tess.startDrawing(GL11.GL_LINE_STRIP);

        int dx, dy, dz;
        for (dx = -16; dx <= 32; dx += 16) {
            for (dz = -16; dz <= 32; dz += 16) {
                tess.setColorRGBA_F(1f, 0f, 0f, 0f);
                tess.addVertex(originX + dx, bottom, originZ + dz);
                tess.setColorRGBA_F(1f, 0f, 0f, .5f);
                tess.addVertex(originX + dx, bottom, originZ + dz);
                tess.addVertex(originX + dx, top, originZ + dz);
                tess.setColorRGBA_F(1f, 0f, 0f, 0f);
                tess.addVertex(originX + dx, top, originZ + dz);
            }
        }

        for (dx = 2; dx < 16; dx += 2) {
            tess.setColorRGBA_F(1f, 1f, 0f, 0f);
            tess.addVertex(originX + dx, bottom, originZ);
            tess.setColorRGBA_F(1f, 1f, 0f, 1f);
            tess.addVertex(originX + dx, bottom, originZ);
            tess.addVertex(originX + dx, top, originZ);
            tess.setColorRGBA_F(1f, 1f, 0f, 0f);
            tess.addVertex(originX + dx, top, originZ);
            tess.addVertex(originX + dx, bottom, originZ + 16);
            tess.setColorRGBA_F(1f, 1f, 0f, 1f);
            tess.addVertex(originX + dx, bottom, originZ + 16);
            tess.addVertex(originX + dx, top, originZ + 16);
            tess.setColorRGBA_F(1f, 1f, 0f, 0f);
            tess.addVertex(originX + dx, top, originZ + 16);
        }

        for (dz = 2; dz < 16; dz += 2) {
            tess.setColorRGBA_F(1f, 1f, 0f, 0f);
            tess.addVertex(originX, bottom, originZ + dz);
            tess.setColorRGBA_F(1f, 1f, 0f, 1f);
            tess.addVertex(originX, bottom, originZ + dz);
            tess.addVertex(originX, top, originZ + dz);
            tess.setColorRGBA_F(1f, 1f, 0f, 0f);
            tess.addVertex(originX, top, originZ + dz);
            tess.addVertex(originX + 16, bottom, originZ + dz);
            tess.setColorRGBA_F(1f, 1f, 0f, 1f);
            tess.addVertex(originX + 16, bottom, originZ + dz);
            tess.addVertex(originX + 16, top, originZ + dz);
            tess.setColorRGBA_F(1f, 1f, 0f, 0f);
            tess.addVertex(originX + 16, top, originZ + dz);
        }

        for (dy = 0; dy <= 128; dy += 2) {
            double lineY = dy - y;
            tess.setColorRGBA_F(1f, 1f, 0f, 0f);
            tess.addVertex(originX, lineY, originZ);
            tess.setColorRGBA_F(1f, 1f, 0f, 1f);
            tess.addVertex(originX, lineY, originZ);
            tess.addVertex(originX, lineY, originZ + 16);
            tess.addVertex(originX + 16, lineY, originZ + 16);
            tess.addVertex(originX + 16, lineY, originZ);
            tess.addVertex(originX, lineY, originZ);
            tess.setColorRGBA_F(1f, 1f, 0f, 0f);
            tess.addVertex(originX, lineY, originZ);
        }

        tess.draw();
        GL11.glLineWidth(2);
        tess.startDrawing(GL11.GL_LINE_STRIP);

        for (dx = 0; dx <= 16; dx += 16) {
            for (dz = 0; dz <= 16; dz += 16) {
                tess.setColorRGBA_F(.25f, .25f, 1f, 0f);
                tess.addVertex(originX + dx, bottom, originZ + dz);
                tess.setColorRGBA_F(.25f, .25f, 1f, 1f);
                tess.addVertex(originX + dx, bottom, originZ + dz);
                tess.addVertex(originX + dx, top, originZ + dz);
                tess.setColorRGBA_F(.25f, .25f, 1f, 0f);
                tess.addVertex(originX + dx, top, originZ + dz);
            }
        }

        for (dy = 0; dy <= 128; dy += 16) {
            double lineY = dy - y;
            tess.setColorRGBA_F(.25f, .25f, 1f, 0f);
            tess.addVertex(originX, lineY, originZ);
            tess.setColorRGBA_F(.25f, .25f, 1f, 1f);
            tess.addVertex(originX, lineY, originZ);
            tess.addVertex(originX, lineY, originZ + 16);
            tess.addVertex(originX + 16, lineY, originZ + 16);
            tess.addVertex(originX + 16, lineY, originZ);
            tess.addVertex(originX, lineY, originZ);
            tess.setColorRGBA_F(.25f, .25f, 1f, 0f);
            tess.addVertex(originX, lineY, originZ);
        }

        tess.draw();

        GL11.glLineWidth(1);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glShadeModel(GL11.GL_FLAT);
    }

}
