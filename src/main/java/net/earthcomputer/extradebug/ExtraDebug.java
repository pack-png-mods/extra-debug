package net.earthcomputer.extradebug;

import java.util.*;

import net.minecraft.src.EntityPlayerSP;
import net.minecraft.src.FontRenderer;
import net.minecraft.src.MobSpawnerBase;
import net.minecraft.src.Tessellator;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import net.minecraft.client.Minecraft;

public class ExtraDebug {

    public static void extraDebug(Minecraft mc) {
        List<String> extraLines = new ArrayList<>();
        EntityPlayerSP player = mc.field_6322_g;

        extraLines.add(String.format("Pos: %.4f / %.4f / %.4f (%.4f feet)", player.posX, player.posY, player.posZ, player.boundingBox.minY));
        extraLines.add(String.format("Chunk: %d / %d In Chunk %d / %d In Population %d / %d", floor(player.posX) & 15, floor(player.posZ) & 15, floor(player.posX) >> 4, floor(player.posZ) >> 4, floor(player.posX - 8) >> 4, floor(player.posZ - 8) >> 4));
        extraLines.add(String.format("Rot: %.4f / %.4f Facing: %s", normalizeYaw(player.rotationYaw), player.rotationPitch, getFacingString(player.rotationYaw)));
        extraLines.add(String.format("Seed: %d", mc.field_6324_e.randomSeed));
        long populationSeed = getPopulationSeed(mc.field_6324_e.randomSeed, floor(player.posX - 8) >> 4, floor(player.posZ - 8) >> 4);
        extraLines.add(String.format("Population seed: %d (xored: %d)", populationSeed, populationSeed ^ 0x5deece66dL));
        MobSpawnerBase biome = mc.field_6324_e.func_4075_a().func_4073_a(floor(player.posX), floor(player.posZ));
        extraLines.add("Biome: " + biome.field_6504_m);

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
        switch (((int) (normalizeYaw(yaw + 45) + 180) / 90) % 4) {
            case 2: return "South (Towards Positive Z)";
            case 3: return "West (Towards Negative X)";
            case 0: return "North (Towards Negative Z)";
            case 1: return "East (Towards Positive X)";
            default: throw new AssertionError();
        }
    }

    static long getPopulationSeed(long worldSeed, int chunkX, int chunkZ) {
        Random rand = new Random(worldSeed);
        long i = rand.nextLong() / 2 * 2 + 1;
        long j = rand.nextLong() / 2 * 2 + 1;
        return ((chunkX * i + chunkZ * j) ^ worldSeed) & 0xffffffffffffL;
    }

    public static void renderChunkBorders(Minecraft mc, float partialTicks) {
        if (!Keyboard.isKeyDown(61)) // f3
            return;


        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glShadeModel(GL11.GL_SMOOTH);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glAlphaFunc(GL11.GL_GREATER, 0.1f);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);

        EntityPlayerSP player = mc.field_6322_g;
        double x = player.prevPosX + (player.posX - player.prevPosX) * partialTicks;
        double y = player.prevPosY + (player.posY - player.prevPosY) * partialTicks;
        double z = player.prevPosZ + (player.posZ - player.prevPosZ) * partialTicks;

        // normal chunk borders
        double originX = (floor(x) >> 4 << 4) - x;
        double originZ = (floor(z) >> 4 << 4) - z;
        renderChunkBorders(originX, originZ, y,
                1f, 0f, 0f,
                .25f, .25f, 1f,
                1f, 1f, 0f);

        // population chunk borders
        originX = (floor(x - 8) >> 4 << 4) - x + 8;
        originZ = (floor(z - 8) >> 4 << 4) - z + 8;
        renderChunkBorders(originX, originZ, y,
                1f, 0f, 1f,
                0f, .25f, 0f,
                0f, 1f, 0f);

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glShadeModel(GL11.GL_FLAT);
    }

    static void renderChunkBorders(double originX, double originZ, double y,
                                   float farCornerRed, float farCornerGreen, float farCornerBlue,
                                   float nearCornerRed, float nearCornerGreen, float nearCornerBlue,
                                   float borderRed, float borderGreen, float borderBlue) {
        Tessellator tess = Tessellator.instance;
        double bottom = -y;
        double top = 128 - y;
        GL11.glLineWidth(1);
        tess.startDrawing(GL11.GL_LINE_STRIP);

        // vertical red lines marking chunk corners further out
        int dx, dy, dz;
        for (dx = -16; dx <= 32; dx += 16) {
            for (dz = -16; dz <= 32; dz += 16) {
                tess.setColorRGBA_F(farCornerRed, farCornerGreen, farCornerBlue, 0f);
                tess.addVertex(originX + dx, bottom, originZ + dz);
                tess.setColorRGBA_F(farCornerRed, farCornerGreen, farCornerBlue, .5f);
                tess.addVertex(originX + dx, bottom, originZ + dz);
                tess.addVertex(originX + dx, top, originZ + dz);
                tess.setColorRGBA_F(farCornerRed, farCornerGreen, farCornerBlue, 0f);
                tess.addVertex(originX + dx, top, originZ + dz);
            }
        }

        // vertical yellow lines on z chunk borders
        for (dx = 2; dx < 16; dx += 2) {
            tess.setColorRGBA_F(borderRed, borderGreen, borderBlue, 0f);
            tess.addVertex(originX + dx, bottom, originZ);
            tess.setColorRGBA_F(borderRed, borderGreen, borderBlue, 1f);
            tess.addVertex(originX + dx, bottom, originZ);
            tess.addVertex(originX + dx, top, originZ);
            tess.setColorRGBA_F(borderRed, borderGreen, borderBlue, 0f);
            tess.addVertex(originX + dx, top, originZ);
            tess.addVertex(originX + dx, bottom, originZ + 16);
            tess.setColorRGBA_F(borderRed, borderGreen, borderBlue, 1f);
            tess.addVertex(originX + dx, bottom, originZ + 16);
            tess.addVertex(originX + dx, top, originZ + 16);
            tess.setColorRGBA_F(borderRed, borderGreen, borderBlue, 0f);
            tess.addVertex(originX + dx, top, originZ + 16);
        }

        // vertical yellow lines on x chunk borders
        for (dz = 2; dz < 16; dz += 2) {
            tess.setColorRGBA_F(borderRed, borderGreen, borderBlue, 0f);
            tess.addVertex(originX, bottom, originZ + dz);
            tess.setColorRGBA_F(borderRed, borderGreen, borderBlue, 1f);
            tess.addVertex(originX, bottom, originZ + dz);
            tess.addVertex(originX, top, originZ + dz);
            tess.setColorRGBA_F(borderRed, borderGreen, borderBlue, 0f);
            tess.addVertex(originX, top, originZ + dz);
            tess.addVertex(originX + 16, bottom, originZ + dz);
            tess.setColorRGBA_F(borderRed, borderGreen, borderBlue, 1f);
            tess.addVertex(originX + 16, bottom, originZ + dz);
            tess.addVertex(originX + 16, top, originZ + dz);
            tess.setColorRGBA_F(borderRed, borderGreen, borderBlue, 0f);
            tess.addVertex(originX + 16, top, originZ + dz);
        }

        // horizontal yellow lines
        for (dy = 0; dy <= 128; dy += 2) {
            double lineY = dy - y;
            tess.setColorRGBA_F(borderRed, borderGreen, borderBlue, 0f);
            tess.addVertex(originX, lineY, originZ);
            tess.setColorRGBA_F(borderRed, borderGreen, borderBlue, 1f);
            tess.addVertex(originX, lineY, originZ);
            tess.addVertex(originX, lineY, originZ + 16);
            tess.addVertex(originX + 16, lineY, originZ + 16);
            tess.addVertex(originX + 16, lineY, originZ);
            tess.addVertex(originX, lineY, originZ);
            tess.setColorRGBA_F(borderRed, borderGreen, borderBlue, 0f);
            tess.addVertex(originX, lineY, originZ);
        }

        tess.draw();
        GL11.glLineWidth(2);
        tess.startDrawing(GL11.GL_LINE_STRIP);

        // vertical blue lines marking corners of the chunk the players is in
        for (dx = 0; dx <= 16; dx += 16) {
            for (dz = 0; dz <= 16; dz += 16) {
                tess.setColorRGBA_F(nearCornerRed, nearCornerGreen, nearCornerBlue, 0f);
                tess.addVertex(originX + dx, bottom, originZ + dz);
                tess.setColorRGBA_F(nearCornerRed, nearCornerGreen, nearCornerBlue, 1f);
                tess.addVertex(originX + dx, bottom, originZ + dz);
                tess.addVertex(originX + dx, top, originZ + dz);
                tess.setColorRGBA_F(nearCornerRed, nearCornerGreen, nearCornerBlue, 0f);
                tess.addVertex(originX + dx, top, originZ + dz);
            }
        }

        // horizontal blue lines marking chunk section borders of the chunk the player is in
        for (dy = 0; dy <= 128; dy += 16) {
            double lineY = dy - y;
            tess.setColorRGBA_F(nearCornerRed, nearCornerGreen, nearCornerBlue, 0f);
            tess.addVertex(originX, lineY, originZ);
            tess.setColorRGBA_F(nearCornerRed, nearCornerGreen, nearCornerBlue, 1f);
            tess.addVertex(originX, lineY, originZ);
            tess.addVertex(originX, lineY, originZ + 16);
            tess.addVertex(originX + 16, lineY, originZ + 16);
            tess.addVertex(originX + 16, lineY, originZ);
            tess.addVertex(originX, lineY, originZ);
            tess.setColorRGBA_F(nearCornerRed, nearCornerGreen, nearCornerBlue, 0f);
            tess.addVertex(originX, lineY, originZ);
        }

        tess.draw();

        GL11.glLineWidth(1);
    }

}
