package org.winterblade.tinkertooltweaker.compat;

import com.blamejared.mtlib.helpers.InputHelper;
import crafttweaker.CraftTweakerAPI;
import crafttweaker.annotations.ModOnly;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IItemStack;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import slimeknights.tconstruct.library.modifiers.TinkerGuiException;
import slimeknights.tconstruct.library.tinkering.PartMaterialType;
import slimeknights.tconstruct.library.tools.ToolCore;
import slimeknights.tconstruct.library.utils.ToolBuilder;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

@ZenClass("mods.tconstruct.ToolBuilder")
@ZenRegister
@ModOnly("tconstruct")
public class ToolBuilderAdapter {
    private static boolean init = false;

    private static void init() {
        if(init) return;

        MinecraftForge.EVENT_BUS.register(new ToolBuilderAdapter());
        init = true;
    }

    /**
     * Modifies the current tool with the given part(s) and returns the modified tool
     * or an empty item stack if modification failed
     * @param tool  The tool to replace
     * @param parts The parts to replace with
     * @return  The modified tool or an empty IItemStack
     */
    @ZenMethod
    public static IItemStack replaceMaterial(IItemStack tool, IItemStack[] parts) {
        init();
        // Make it so we can work with TiCon's methods:
        ItemStack toolStack = InputHelper.toStack(tool);

        try {
            toolStack = ToolBuilder.tryReplaceToolParts(
                    toolStack,
                    InputHelper.toNonNullList(InputHelper.toStacks(parts)),
                    false
            );
        } catch (TinkerGuiException e) {
            CraftTweakerAPI.logInfo("Unable to replace tool parts due to: "+e.getLocalizedMessage());
            toolStack = ItemStack.EMPTY;
        }

        // Shove it back as the proper item:
        return InputHelper.toIItemStack(toolStack);
    }

    /**
     * Adds a modifier to the tool, returning the modified tool
     * @param tool  The tool to modify
     * @param modifiers The modifier items to apply
     * @return  The modified tool or an empty IItemStack
     */
    @ZenMethod
    public static IItemStack addModifier(IItemStack tool, IItemStack[] modifiers) {
        init();

        // Make it so we can work with TiCon's methods:
        ItemStack toolStack = InputHelper.toStack(tool);

        try {
            toolStack = ToolBuilder.tryModifyTool(
                    InputHelper.toNonNullList(InputHelper.toStacks(modifiers)),
                    toolStack,
                    false
            );
        } catch (TinkerGuiException e) {
            CraftTweakerAPI.logInfo("Unable to modify tool due to: " + e.getLocalizedMessage());
            toolStack = ItemStack.EMPTY;
        }

        // Shove it back as the proper item:
        return InputHelper.toIItemStack(toolStack);
    }

    /**
     * Tries to build a tool out of the parts; if successful, merges any damage/tags from the output to the new tool.
     * @param parts  The parts to try to build the tool out of; should be all parts to make up the new tool
     * @return The output tool
     */
    @ZenMethod
    public static IItemStack buildTool(IItemStack[] parts, IItemStack outputType) {
        Item targetType = InputHelper.toStack(outputType).getItem();
        if(!(targetType instanceof ToolCore)) return null;
        ToolCore core = (ToolCore)targetType;

        List<PartMaterialType> components = core.getRequiredComponents();

        // This is ugly, but TiCon tools have to be in the right order,
        // and I'm too lazy to build a better sorting algorithm.
        // Build fewer tools and it won't be an issue.
        ItemStack[] sortedParts = Arrays.stream(parts)
                .map(InputHelper::toStack)
                .sorted(
                        Comparator.comparing(item -> item.isEmpty()
                            ? 9
                            : components
                                .stream()
                                .filter(c -> c.isValid(item))
                                .findFirst()
                                .map(components::indexOf)
                                .orElse(10))
                )
                .toArray(ItemStack[]::new);

        return InputHelper.toIItemStack(ToolBuilder.tryBuildTool(
                InputHelper.toNonNullList(sortedParts),
                "",
                Collections.singletonList(core)));
    }
}
