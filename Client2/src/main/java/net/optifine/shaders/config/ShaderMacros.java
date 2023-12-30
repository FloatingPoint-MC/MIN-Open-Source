package net.optifine.shaders.config;

import net.optifine.Config;
import net.minecraft.util.Util;
import net.optifine.shaders.Shaders;

public class ShaderMacros {
    private static final String PREFIX_MACRO = "MC_";
    private static ShaderMacro[] extensionMacros;

    public static String getOs() {
        Util.EnumOS osType = Util.getOSType();

        return switch (osType) {
            case WINDOWS -> "MC_OS_WINDOWS";
            case OSX -> "MC_OS_MAC";
            case LINUX -> "MC_OS_LINUX";
            default -> "MC_OS_OTHER";
        };
    }

    public static String getVendor() {
        String s = Config.openGlVendor;

        if (s == null) {
            return "MC_GL_VENDOR_OTHER";
        } else {
            s = s.toLowerCase();

            if (s.startsWith("ati")) {
                return "MC_GL_VENDOR_ATI";
            } else if (s.startsWith("intel")) {
                return "MC_GL_VENDOR_INTEL";
            } else if (s.startsWith("nvidia")) {
                return "MC_GL_VENDOR_NVIDIA";
            } else {
                return s.startsWith("x.org") ? "MC_GL_VENDOR_XORG" : "MC_GL_VENDOR_OTHER";
            }
        }
    }

    public static String getRenderer() {
        String s = Config.openGlRenderer;

        if (s == null) {
            return "MC_GL_RENDERER_OTHER";
        } else {
            s = s.toLowerCase();

            if (s.startsWith("amd")) {
                return "MC_GL_RENDERER_RADEON";
            } else if (s.startsWith("ati")) {
                return "MC_GL_RENDERER_RADEON";
            } else if (s.startsWith("radeon")) {
                return "MC_GL_RENDERER_RADEON";
            } else if (s.startsWith("gallium")) {
                return "MC_GL_RENDERER_GALLIUM";
            } else if (s.startsWith("intel")) {
                return "MC_GL_RENDERER_INTEL";
            } else if (s.startsWith("geforce")) {
                return "MC_GL_RENDERER_GEFORCE";
            } else if (s.startsWith("nvidia")) {
                return "MC_GL_RENDERER_GEFORCE";
            } else if (s.startsWith("quadro")) {
                return "MC_GL_RENDERER_QUADRO";
            } else if (s.startsWith("nvs")) {
                return "MC_GL_RENDERER_QUADRO";
            } else {
                return s.startsWith("mesa") ? "MC_GL_RENDERER_MESA" : "MC_GL_RENDERER_OTHER";
            }
        }
    }

    public static String getPrefixMacro() {
        return PREFIX_MACRO;
    }

    public static ShaderMacro[] getExtensions() {
        if (extensionMacros == null) {
            String[] astring = Config.getOpenGlExtensions();
            ShaderMacro[] ashadermacro = new ShaderMacro[astring.length];

            for (int i = 0; i < astring.length; ++i) {
                ashadermacro[i] = new ShaderMacro(PREFIX_MACRO + astring[i], "");
            }

            extensionMacros = ashadermacro;
        }

        return extensionMacros;
    }

    public static String getFixedMacroLines() {
        StringBuilder stringbuilder = new StringBuilder();
        addMacroLine(stringbuilder, "MC_VERSION", Config.getMinecraftVersionInt());
        addMacroLine(stringbuilder, "MC_GL_VERSION " + Config.getGlVersion().toInt());
        addMacroLine(stringbuilder, "MC_GLSL_VERSION " + Config.getGlslVersion().toInt());
        addMacroLine(stringbuilder, getOs());
        addMacroLine(stringbuilder, getVendor());
        addMacroLine(stringbuilder, getRenderer());
        return stringbuilder.toString();
    }

    public static String getOptionMacroLines() {
        StringBuilder stringbuilder = new StringBuilder();

        if (Shaders.configAntialiasingLevel > 0) {
            addMacroLine(stringbuilder, "MC_FXAA_LEVEL", Shaders.configAntialiasingLevel);
        }

        if (Shaders.configNormalMap) {
            addMacroLine(stringbuilder, "MC_NORMAL_MAP");
        }

        if (Shaders.configSpecularMap) {
            addMacroLine(stringbuilder, "MC_SPECULAR_MAP");
        }

        addMacroLine(stringbuilder, "MC_RENDER_QUALITY", Shaders.configRenderResMul);
        addMacroLine(stringbuilder, "MC_SHADOW_QUALITY", Shaders.configShadowResMul);
        addMacroLine(stringbuilder, "MC_HAND_DEPTH", Shaders.configHandDepthMul);

        if (Shaders.isOldHandLight()) {
            addMacroLine(stringbuilder, "MC_OLD_HAND_LIGHT");
        }

        if (Shaders.isOldLighting()) {
            addMacroLine(stringbuilder, "MC_OLD_LIGHTING");
        }

        return stringbuilder.toString();
    }

    private static void addMacroLine(StringBuilder sb, String name, int value) {
        sb.append("#define ");
        sb.append(name);
        sb.append(" ");
        sb.append(value);
        sb.append("\n");
    }

    private static void addMacroLine(StringBuilder sb, String name, float value) {
        sb.append("#define ");
        sb.append(name);
        sb.append(" ");
        sb.append(value);
        sb.append("\n");
    }

    private static void addMacroLine(StringBuilder sb, String name) {
        sb.append("#define ");
        sb.append(name);
        sb.append("\n");
    }
}
