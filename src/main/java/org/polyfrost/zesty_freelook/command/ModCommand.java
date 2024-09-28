package org.polyfrost.zesty_freelook.command;

import org.polyfrost.zesty_freelook.ZestyFreelook;
import cc.polyfrost.oneconfig.utils.commands.annotations.Command;
import cc.polyfrost.oneconfig.utils.commands.annotations.Main;

/**
 * @see Command
 * @see Main
 * @see ZestyFreelook
 */
@Command(value = ZestyFreelook.MODID, description = "Access the " + ZestyFreelook.NAME + " GUI.")
public class ModCommand {
    @Main
    private void handle() {
        ZestyFreelook.INSTANCE.config.openGui();
    }
}