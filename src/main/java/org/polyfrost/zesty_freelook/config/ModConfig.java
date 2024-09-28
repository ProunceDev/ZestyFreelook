package org.polyfrost.zesty_freelook.config;

import org.polyfrost.zesty_freelook.ZestyFreelook;
import cc.polyfrost.oneconfig.config.Config;
import cc.polyfrost.oneconfig.config.annotations.Switch;
import cc.polyfrost.oneconfig.config.data.Mod;
import cc.polyfrost.oneconfig.config.data.ModType;
import cc.polyfrost.oneconfig.config.data.OptionSize;

/**
 * The main Config entrypoint that extends the Config type and inits the config options.
 * See <a href="https://docs.polyfrost.cc/oneconfig/config/adding-options">this link</a> for more config Options
 */
public class ModConfig extends Config {
    
    @Switch(
            name = "Hold To Look",
            size = OptionSize.SINGLE
    )
    public static boolean holdToLook = true;

    @Switch(
            name = "Invert Pitch in Freelook",
            size = OptionSize.SINGLE
    )
    public static boolean invertPitch = true;

    @Switch(
            name = "Start Facing Rear",
            size = OptionSize.SINGLE
    )
    public static boolean startFacingRear = false;

    @Switch(
            name = "Notifications",
            size = OptionSize.SINGLE
    )
    public static boolean notifications = false;

    public ModConfig() {
        super(new Mod(ZestyFreelook.NAME, ModType.UTIL_QOL), ZestyFreelook.MODID + ".json");
        initialize();
    }
}

