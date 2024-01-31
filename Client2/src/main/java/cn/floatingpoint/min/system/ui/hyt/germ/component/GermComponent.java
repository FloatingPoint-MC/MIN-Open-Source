package cn.floatingpoint.min.system.ui.hyt.germ.component;

public interface GermComponent {
    void drawComponent(String parentUuid, int x, int y, int mouseX, int mouseY);

    void mouseClicked(String parentUuid);

    int getHeight();

    int getSeparation();
}
