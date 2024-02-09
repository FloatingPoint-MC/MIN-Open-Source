package cn.floatingpoint.min.system.ui.loading;

import cn.floatingpoint.min.management.Managers;
import cn.floatingpoint.min.system.eula.Eula;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class GuiEula extends GuiScreen {
    private final GuiScreen nextScreen;
    private final Eula eula;
    private GuiButton deny, accept;

    public GuiEula(GuiScreen nextScreen) {
        this.nextScreen = nextScreen;
        eula = new Eula();
    }

    @Override
    public void initGui() {
        deny = new GuiButton(0, this.width / 2 - 110, this.height / 2 + 90, 100, 20, Managers.i18NManager.getSelectedLanguage().equalsIgnoreCase("English") ? "Deny" : "拒绝");
        accept = new GuiButton(1, this.width / 2 + 10, this.height / 2 + 90, 100, 20, Managers.i18NManager.getSelectedLanguage().equalsIgnoreCase("English") ? "Accept" : "接受");
        if (eula.isAccepted()) {
            mc.displayGuiScreen(new GuiDamnJapaneseAction(nextScreen));
        }
        buttonList.add(accept);
        buttonList.add(deny);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        Gui.drawRect(0, 0, width, height, new Color(0, 0, 0).getRGB());
        int white = new Color(216, 216, 216).getRGB();
        String text = Managers.i18NManager.getTranslation("clickgui.language") + ": ";
        Managers.fontManager.sourceHansSansCN_Regular_20.drawString(text, width - 26 - Managers.fontManager.sourceHansSansCN_Regular_20.getStringWidth(text), 9, white);
        Gui.drawRect(width - 24, 2, width - 23, 24, white);
        Gui.drawRect(width - 24, 2, width - 3, 3, white);
        Gui.drawRect(width - 3, 2, width - 2, 24, white);
        Gui.drawRect(width - 24, 23, width - 2, 24, white);
        if (Managers.i18NManager.getSelectedLanguage().equalsIgnoreCase("English")) {
            Managers.fontManager.sourceHansSansCN_Regular_18.drawString("ENG", width - 22, 9, white);
            Managers.fontManager.sourceHansSansCN_Regular_26.drawCenteredString("End User License Agreement (EULA)", width / 2, height / 2 - 110, white);
            Gui.drawRect(width / 2 - 106, height / 2 - 102, width / 2 + 106, height / 2 - 101, white);
            Managers.fontManager.sourceHansSansCN_Regular_17.drawString("This End User License Agreement (\"Agreement\") governs your use of the software", width / 2 - 187, height / 2 - 100, white);
            Managers.fontManager.sourceHansSansCN_Regular_17.drawString("application (\"Software\") provided by FloatingPoint-MC (\"Company\"). By installing and using the", width / 2 - 204, height / 2 - 92, white);
            Managers.fontManager.sourceHansSansCN_Regular_17.drawString("Software, you agree to be bound by this Agreement. (Last Updated: 02-10-2024)", width / 2 - 204, height / 2 - 84, white);
            Managers.fontManager.sourceHansSansCN_Regular_17.drawString("The Company grants you a revocable, non-exclusive, non-transferable, limited license to", width / 2 - 187, height / 2 - 76, white);
            Managers.fontManager.sourceHansSansCN_Regular_17.drawString("install and use the Software solely for your personal or internal business purposes.", width / 2 - 204, height / 2 - 68, white);
            Managers.fontManager.sourceHansSansCN_Regular_17.drawString("You may not:", width / 2 - 204, height / 2 - 60, white);
            Managers.fontManager.sourceHansSansCN_Regular_17.drawString("·Modify, adapt, or reverse engineer the Software.", width / 2 - 187, height / 2 - 52, white);
            Managers.fontManager.sourceHansSansCN_Regular_17.drawString("·Attempt to bypass or circumvent any security measures or access restrictions of the Software.", width / 2 - 187, height / 2 - 44, white);
            Managers.fontManager.sourceHansSansCN_Regular_17.drawString("·Use the Software for any unlawful purpose or in any manner that violates this Agreement.", width / 2 - 187, height / 2 - 36, white);
            Managers.fontManager.sourceHansSansCN_Regular_17.drawString("The Software and any related documentation are protected by intellectual property laws and", width / 2 - 204, height / 2 - 28, white);
            Managers.fontManager.sourceHansSansCN_Regular_17.drawString("remain the exclusive property of the Company.", width / 2 - 204, height / 2 - 20, white);
            Managers.fontManager.sourceHansSansCN_Regular_17.drawString("The Software is provided \"as is\" without warranties of any kind, either express or implied. The", width / 2 - 187, height / 2 - 12, white);
            Managers.fontManager.sourceHansSansCN_Regular_17.drawString("Company disclaims all warranties, including but not limited to, the implied warranties of", width / 2 - 204, height / 2 - 4, white);
            Managers.fontManager.sourceHansSansCN_Regular_17.drawString("merchantability, fitness for a particular purpose, and non-infringement.", width / 2 - 204, height / 2 + 4, white);
            Managers.fontManager.sourceHansSansCN_Regular_17.drawString("In no event shall the Company be liable for any indirect, incidental, special, or consequential", width / 2 - 187, height / 2 + 12, white);
            Managers.fontManager.sourceHansSansCN_Regular_17.drawString("damages arising out of or in any way connected with the use of the Software.", width / 2 - 204, height / 2 + 20, white);
            Managers.fontManager.sourceHansSansCN_Regular_17.drawString("This Agreement is effective until terminated. The Company may terminate this Agreement at any", width / 2 - 187, height / 2 + 28, white);
            Managers.fontManager.sourceHansSansCN_Regular_17.drawString("time if you breach any provision of this Agreement.", width / 2 - 204, height / 2 + 36, white);
            Managers.fontManager.sourceHansSansCN_Regular_17.drawString("This Agreement shall be governed by and construed in accordance with the laws of", width / 2 - 187, height / 2 + 44, white);
            Managers.fontManager.sourceHansSansCN_Regular_17.drawString("the People's Republic of China, without regard to its conflicts of law provisions.", width / 2 - 204, height / 2 + 52, white);
            Managers.fontManager.sourceHansSansCN_Regular_17.drawString("The Company reserves the right to modify or replace this Agreement at any time. Your continued", width / 2 - 187, height / 2 + 60, white);
            Managers.fontManager.sourceHansSansCN_Regular_17.drawString("use of the Software after any such changes constitutes your acceptance of the new Agreement.", width / 2 - 204, height / 2 + 68, white);
            Managers.fontManager.sourceHansSansCN_Regular_17.drawString("If you have any questions about this Agreement, please contact us at KOOK.", width / 2 - 187, height / 2 + 76, white);
            Managers.fontManager.sourceHansSansCN_Regular_17.drawCenteredString("By clicking \"Accept\", you agree to our Eula, otherwise you will not be able to use our product.", width / 2, height / 2 + 84, white);
        } else {
            Managers.fontManager.sourceHansSansCN_Regular_18.drawString("中", width - 17, 9, white);
            Managers.fontManager.sourceHansSansCN_Regular_26.drawCenteredString("最终用户许可条款 (EULA)", width / 2, height / 2 - 110, white);
            Gui.drawRect(width / 2 - 74, height / 2 - 102, width / 2 + 74, height / 2 - 101, white);
            Managers.fontManager.sourceHansSansCN_Regular_17.drawString("本最终用户许可条款（“条款”）管辖您使用由FloatingPoint-MC（“公司”）提供的软件应用", width / 2 - 187, height / 2 - 100, white);
            Managers.fontManager.sourceHansSansCN_Regular_17.drawString("程序（“软件”）。通过安装和使用该软件，您同意受本条款约束。（最后更新于: 2024年2月10日）", width / 2 - 204, height / 2 - 92, white);
            Managers.fontManager.sourceHansSansCN_Regular_17.drawString("公司授予您一项可撤销的、非独占的、不可转让的、有限的许可，仅供您个人或内部业务目的安装和使用", width / 2 - 187, height / 2 - 84, white);
            Managers.fontManager.sourceHansSansCN_Regular_17.drawString("该软件。", width / 2 - 204, height / 2 - 76, white);
            Managers.fontManager.sourceHansSansCN_Regular_17.drawString("您不得：", width / 2 - 204, height / 2 - 68, white);
            Managers.fontManager.sourceHansSansCN_Regular_17.drawString("·修改、调整或逆向工程软件。", width / 2 - 187, height / 2 - 60, white);
            Managers.fontManager.sourceHansSansCN_Regular_17.drawString("·试图绕过或规避软件的任何安全措施或访问限制。", width / 2 - 187, height / 2 - 52, white);
            Managers.fontManager.sourceHansSansCN_Regular_17.drawString("·将软件用于任何违法目的或违反本条款的方式。", width / 2 - 187, height / 2 - 44, white);
            Managers.fontManager.sourceHansSansCN_Regular_17.drawString("该软件及其相关文档受知识产权法保护，并且仍然是公司的专有财产。", width / 2 - 204, height / 2 - 36, white);
            Managers.fontManager.sourceHansSansCN_Regular_17.drawString("该软件按原样提供，不附带任何形式的保证，无论是明示的还是暗示的。公司不承担任何保证责任，包括", width / 2 - 187, height / 2 - 28, white);
            Managers.fontManager.sourceHansSansCN_Regular_17.drawString("但不限于对适销性、特定目的适用性和非侵权的默示保证。", width / 2 - 204, height / 2 - 20, white);
            Managers.fontManager.sourceHansSansCN_Regular_17.drawString("在任何情况下，公司均不对因使用该软件而引起的任何间接、附带、特殊或后果性损害承担责任。", width / 2 - 187, height / 2 - 12, white);
            Managers.fontManager.sourceHansSansCN_Regular_17.drawString("本条款自生效之日起至终止之日止。如果您违反本条款的任何条款，公司可以随时终止本条款。", width / 2 - 187, height / 2 - 4, white);
            Managers.fontManager.sourceHansSansCN_Regular_17.drawString("本条款应依照中华人民共和国的法律加以解释和执行，不考虑其法律冲突规定。", width / 2 - 187, height / 2 + 4, white);
            Managers.fontManager.sourceHansSansCN_Regular_17.drawString("公司保留随时修改或替换本条款的权利。在任何此类更改后继续使用该软件即表示您接受新的条款。", width / 2 - 187, height / 2 + 12, white);
            Managers.fontManager.sourceHansSansCN_Regular_17.drawString("如果您对本条款有任何疑问，请通过KOOK与我们联系。", width / 2 - 204, height / 2 + 20, white);
            Managers.fontManager.sourceHansSansCN_Regular_17.drawCenteredString("通过点击“接受”，代表您同意我们的条款，否则您将无法使用我们的产品。", width / 2, height / 2 + 76, white);
        }
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {

    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        if (isHovered(width - 24, 2, width - 2, 24, mouseX, mouseY)) {
            Managers.i18NManager.nextLanguage();
        }
        if (Managers.i18NManager.getSelectedLanguage().equalsIgnoreCase("English")) {
            if (isHovered(width / 2 - 106, height / 2 - 110, width / 2 + 106, height / 2 - 101, mouseX, mouseY)) {
                openWeb();
            }
        } else {
            if (isHovered(width / 2 - 74, height / 2 - 110, width / 2 + 74, height / 2 - 101, mouseX, mouseY)) {
                openWeb();
            }
        }
        deny.displayString = Managers.i18NManager.getSelectedLanguage().equalsIgnoreCase("English") ? "Deny" : "拒绝";
        accept.displayString = Managers.i18NManager.getSelectedLanguage().equalsIgnoreCase("English") ? "Accept" : "接受";
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    private void openWeb() {
        try {
            openWebLink(new URI("https://eula.minclient.xyz"));
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.id == deny.id) {
            mc.shutdown();
        } else if (button.id == accept.id) {
            eula.accept();
            mc.displayGuiScreen(new GuiDamnJapaneseAction(nextScreen));
        }
    }
}
