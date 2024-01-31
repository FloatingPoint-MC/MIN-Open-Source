package cn.floatingpoint.min.system.ui.hyt.germ.component.impl;

import cn.floatingpoint.min.management.Managers;
import cn.floatingpoint.min.system.ui.hyt.germ.component.GermComponent;

import java.awt.*;

public class GermText implements GermComponent {
    private final String text;

    public GermText(String text) {
        this.text = text;
    }

    @Override
    public void drawComponent(String parentUuid, int x, int y, int mouseX, int mouseY) {
        Managers.fontManager.sourceHansSansCN_Regular_20.drawCenteredString(text, x, y - Managers.fontManager.sourceHansSansCN_Regular_20.FONT_HEIGHT / 2, new Color(216, 216, 216).getRGB());
    }

    @Override
    public void mouseClicked(String parentUuid) {

    }

    @Override
    public int getHeight() {
        return 16;
    }

    @Override
    public int getSeparation() {
        return 8;
    }
}
