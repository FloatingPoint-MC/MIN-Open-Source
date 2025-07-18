package net.optifine.shaders;

import net.optifine.Config;
import net.optifine.shaders.config.ShaderOption;
import net.optifine.shaders.config.ShaderProfile;

public class ShaderUtils {
    public static ShaderOption getShaderOption(String name, ShaderOption[] opts) {
        if (opts == null) {
            return null;
        } else {
            for (ShaderOption shaderoption : opts) {
                if (shaderoption.getName().equals(name)) {
                    return shaderoption;
                }
            }

            return null;
        }
    }

    public static ShaderProfile detectProfile(ShaderProfile[] profs, ShaderOption[] opts, boolean def) {
        if (profs == null) {
            return null;
        } else {
            for (ShaderProfile shaderprofile : profs) {
                if (matchProfile(shaderprofile, opts, def)) {
                    return shaderprofile;
                }
            }

            return null;
        }
    }

    public static boolean matchProfile(ShaderProfile prof, ShaderOption[] opts, boolean def) {
        if (prof == null) {
            return false;
        } else if (opts == null) {
            return false;
        } else {
            String[] astring = prof.getOptions();

            for (String s : astring) {
                ShaderOption shaderoption = getShaderOption(s, opts);

                if (shaderoption != null) {
                    String s1 = def ? shaderoption.getValueDefault() : shaderoption.getValue();
                    String s2 = prof.getValue(s);

                    if (!Config.equals(s1, s2)) {
                        return false;
                    }
                }
            }

            return true;
        }
    }
}
