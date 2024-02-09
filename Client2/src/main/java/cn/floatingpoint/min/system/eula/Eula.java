package cn.floatingpoint.min.system.eula;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

import net.minecraft.client.Minecraft;

public class Eula {
    private final File eulaFile;
    private final boolean accepted;

    public Eula() {
        this.eulaFile = new File(Minecraft.getMinecraft().gameDir, "eula.txt");
        this.accepted = this.readEula(this.eulaFile);
    }

    private boolean readEula(File file) {
        boolean accepted = false;
        try (FileInputStream in = new FileInputStream(file)) {
            Properties properties = new Properties();
            properties.load(in);
            accepted = Boolean.parseBoolean(properties.getProperty("eula", "false"));
        } catch (Exception e) {
            this.initEula();
        }
        return accepted;
    }

    public boolean isAccepted() {
        return this.accepted;
    }

    public void initEula() {
        try (FileOutputStream out = new FileOutputStream(this.eulaFile)) {
            Properties properties = new Properties();
            properties.setProperty("eula", "false");
            properties.store(out, "By changing the setting below to TRUE you are indicating your agreement to our EULA (https://eula.minclient.xyz/).");
        } catch (Exception ignored) {
        }
    }

    public void accept() {
        try (FileOutputStream out = new FileOutputStream(this.eulaFile)) {
            Properties properties = new Properties();
            properties.setProperty("eula", "true");
            properties.store(out, "By changing the setting below to TRUE you are indicating your agreement to our EULA (https://eula.minclient.xyz/).");
        } catch (Exception ignored) {
        }
    }
}
