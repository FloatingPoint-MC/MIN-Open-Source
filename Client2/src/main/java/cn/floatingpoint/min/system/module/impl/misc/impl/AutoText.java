package cn.floatingpoint.min.system.module.impl.misc.impl;

import cn.floatingpoint.min.system.module.impl.misc.MiscModule;
import cn.floatingpoint.min.system.module.value.impl.IntegerValue;
import cn.floatingpoint.min.system.module.value.impl.OptionValue;
import cn.floatingpoint.min.system.module.value.impl.TextValue;
import cn.floatingpoint.min.utils.client.Pair;
import cn.floatingpoint.min.utils.math.TimeHelper;

/**
 * @projectName: MIN
 * @author: vlouboos
 * @date: 2023-07-19 22:33:54
 */
public class AutoText extends MiscModule {
    public static final OptionValue startMessage = new OptionValue(false);
    private final TextValue startText = new TextValue("gl hf", startMessage::getValue);
    private final IntegerValue startDelay = new IntegerValue(0, 5000, 500, 1000, startMessage::getValue);
    public static final OptionValue winMessage = new OptionValue(false);
    private final TextValue winText = new TextValue("ez game", winMessage::getValue);
    private final IntegerValue winDelay = new IntegerValue(0, 5000, 500, 1000, winMessage::getValue);
    public static final OptionValue endMessage = new OptionValue(true);
    private final TextValue endText = new TextValue("GG", endMessage::getValue);
    private final IntegerValue endDelay = new IntegerValue(0, 5000, 500, 1000, endMessage::getValue);
    private final TimeHelper startTimer = new TimeHelper(), winTimer = new TimeHelper(), endTimer = new TimeHelper();
    public static boolean startToSend = false;
    public static boolean winToSend = false;
    public static boolean endToSend = false;
    private boolean sendStart, sendWin, sendEnd;

    public AutoText() {
        addValues(
                new Pair<>("StartToSend", startMessage),
                new Pair<>("StartText", startText),
                new Pair<>("StartDelay", startDelay),
                new Pair<>("WinToSend", winMessage),
                new Pair<>("WinText", winText),
                new Pair<>("WinDelay", winDelay),
                new Pair<>("EndToSend", endMessage),
                new Pair<>("EndText", endText),
                new Pair<>("EndDelay", endDelay)
        );
    }

    @Override
    public void onEnable() {

    }

    @Override
    public void onDisable() {

    }

    @Override
    public void tick() {
        if (startToSend) {
            startTimer.reset();
            sendStart = true;
            startToSend = false;
        }
        if (sendStart && startTimer.isDelayComplete(startDelay.getValue())) {
            mc.player.sendChatMessage(startText.getValue());
            sendStart = false;
        }
        if (winToSend) {
            winTimer.reset();
            sendWin = true;
            winToSend = false;
        }
        if (sendWin && winTimer.isDelayComplete(winDelay.getValue())) {
            mc.player.sendChatMessage(winText.getValue());
            sendWin = false;
        }
        if (endToSend) {
            endTimer.reset();
            sendEnd = true;
            endToSend = false;
        }
        if (sendEnd && endTimer.isDelayComplete(endDelay.getValue())) {
            mc.player.sendChatMessage(endText.getValue());
            sendEnd = false;
        }
    }
}
