package cn.floatingpoint.min.system.boost;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.util.math.AxisAlignedBB;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL33;
import org.lwjglx.opengl.GLContext;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * Used for stopping entities from rendering if they are not visible to the player
 * <p>
 * Subsequent entity on entity occlusion derived from <a href="https://en.wikipedia.org/wiki/Line%E2%80%93plane_intersection">...</a>
 */
public class EntityCulling {

    private static final Minecraft mc = Minecraft.getMinecraft();
    private static final ConcurrentHashMap<UUID, OcclusionQuery> queries = new ConcurrentHashMap<>();
    private static final boolean SUPPORT_NEW_GL = GLContext.getCapabilities().OpenGL33;
    public static boolean shouldPerformCulling = false;
    private static int destroyTimer;
    public static boolean renderingSpawnerEntity = false;

    public static void drawSelectionBoundingBox(AxisAlignedBB b) {
        GlStateManager.disableAlpha();
        GlStateManager.disableCull();
        GlStateManager.depthMask(false);
        GlStateManager.colorMask(false, false, false, false);
        final Tessellator tessellator = Tessellator.getInstance();
        final BufferBuilder worldrenderer = tessellator.getBuffer();
        worldrenderer.begin(GL11.GL_QUAD_STRIP, DefaultVertexFormats.POSITION);
        worldrenderer.pos(b.maxX, b.maxY, b.maxZ).endVertex();
        worldrenderer.pos(b.maxX, b.maxY, b.minZ).endVertex();
        worldrenderer.pos(b.minX, b.maxY, b.maxZ).endVertex();
        worldrenderer.pos(b.minX, b.maxY, b.minZ).endVertex();
        worldrenderer.pos(b.minX, b.minY, b.maxZ).endVertex();
        worldrenderer.pos(b.minX, b.minY, b.minZ).endVertex();
        worldrenderer.pos(b.minX, b.maxY, b.minZ).endVertex();
        worldrenderer.pos(b.minX, b.minY, b.minZ).endVertex();
        worldrenderer.pos(b.maxX, b.maxY, b.minZ).endVertex();
        worldrenderer.pos(b.maxX, b.minY, b.minZ).endVertex();
        worldrenderer.pos(b.maxX, b.maxY, b.maxZ).endVertex();
        worldrenderer.pos(b.maxX, b.minY, b.maxZ).endVertex();
        worldrenderer.pos(b.minX, b.maxY, b.maxZ).endVertex();
        worldrenderer.pos(b.minX, b.minY, b.maxZ).endVertex();
        worldrenderer.pos(b.minX, b.minY, b.maxZ).endVertex();
        worldrenderer.pos(b.maxX, b.minY, b.maxZ).endVertex();
        worldrenderer.pos(b.minX, b.minY, b.minZ).endVertex();
        worldrenderer.pos(b.maxX, b.minY, b.minZ).endVertex();
        tessellator.draw();
        GlStateManager.depthMask(true);
        GlStateManager.colorMask(true, true, true, true);
        GlStateManager.enableAlpha();
    }

    public static boolean shouldCancelRenderItem(Entity stack) {
        return shouldPerformCulling && stack.world == mc.player.world && checkEntity(stack);
    }

    private static int getQuery() {
        try {
            return GL15.glGenQueries();
        } catch (Throwable throwable) {
            return 0;
        }
    }

    /**
     * Used OpenGL queries in order to determine if the given is visible
     *
     * @param entity entity to check
     * @return true if the entity rendering should be skipped
     */
    private static boolean checkEntity(Entity entity) {
        if (renderingSpawnerEntity) return false;
        OcclusionQuery query = queries.computeIfAbsent(entity.getUniqueID(), OcclusionQuery::new);
        if (query.refresh) {
            query.nextQuery = getQuery();
            query.refresh = false;
            int mode = SUPPORT_NEW_GL ? GL33.GL_ANY_SAMPLES_PASSED : GL15.GL_SAMPLES_PASSED;
            GL15.glBeginQuery(mode, query.nextQuery);
            drawSelectionBoundingBox(entity.getEntityBoundingBox()
                    .expand(.2, .2, .2)
                    .offset(-mc.getRenderManager().getRenderPosX(), -mc.getRenderManager().getRenderPosY(), -mc.getRenderManager().getRenderPosZ())
            );
            GL15.glEndQuery(mode);
        }

        return query.occluded;
    }

    /**
     * Fire rays from the player's eyes, detecting on if it can see an entity or not.
     * If it can see an entity, continue to render the entity, otherwise save some time
     * performing rendering and cancel the entity render.
     */
    public static boolean shouldCancelRenderEntity(EntityLivingBase entity) {
        if (!shouldPerformCulling) {
            return false;
        }

        boolean armorStand = entity instanceof EntityArmorStand;
        if (entity == mc.player || entity.world != mc.player.world ||
                (armorStand && ((EntityArmorStand) entity).hasMarker()) ||
                (!entity.isVisibleToPlayer(mc.player) && !armorStand)
        ) {
            return false;
        }

        return checkEntity(entity);
    }

    public static void check() {
        long delay = 50;
        long nanoTime = TimeUnit.NANOSECONDS.toMillis(System.nanoTime());
        for (OcclusionQuery query : queries.values()) {
            if (query.nextQuery != 0) {
                final long queryObject = GL15.glGetQueryObjecti(query.nextQuery, GL15.GL_QUERY_RESULT_AVAILABLE);
                if (queryObject != 0) {
                    query.occluded = GL15.glGetQueryObjecti(query.nextQuery, GL15.GL_QUERY_RESULT) == 0;
                    GL15.glDeleteQueries(query.nextQuery);
                    query.nextQuery = 0;

                }
            }
            if (query.nextQuery == 0 && nanoTime - query.executionTime > delay) {
                query.executionTime = nanoTime;
                query.refresh = true;
            }
        }
    }

    public static void tick() {
        if (destroyTimer++ < 120) {
            return;
        }

        destroyTimer = 0;
        WorldClient theWorld = mc.world;
        if (theWorld == null) {
            return;
        }

        List<UUID> remove = new ArrayList<>();
        Set<UUID> loaded = new HashSet<>();
        for (Entity entity : theWorld.loadedEntityList) {
            loaded.add(entity.getUniqueID());
        }

        for (OcclusionQuery value : queries.values()) {
            if (loaded.contains(value.uuid)) {
                continue;
            }

            remove.add(value.uuid);
            if (value.nextQuery != 0) {
                GL15.glDeleteQueries(value.nextQuery);
            }
        }

        for (UUID uuid : remove) {
            queries.remove(uuid);
        }
    }

    static class OcclusionQuery {
        private final UUID uuid; //Owner
        private int nextQuery;
        private boolean refresh = true;
        private boolean occluded;
        private long executionTime = 0;

        public OcclusionQuery(UUID uuid) {
            this.uuid = uuid;
        }
    }
}