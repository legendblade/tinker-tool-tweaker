package org.winterblade.tinkertooltweaker;

import net.minecraft.init.Blocks;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.Logger;

@Mod(modid = TinkerToolTweaker.MODID, name = TinkerToolTweaker.NAME, version = TinkerToolTweaker.VERSION)
public class TinkerToolTweaker
{
    public static final String MODID = "tinkertooltweaker";
    public static final String NAME = "Tinker's Tool Tweaker";
    public static final String VERSION = "0.1";

    private static Logger logger;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        logger = event.getModLog();
    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
    }
}
