package mod.gubbybee.debug;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.block.Block;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import javax.xml.soap.Text;
import java.text.Normalizer;
import java.util.Set;

public class DebugCommands {

    @SubscribeEvent
    public static void onRegisterCommandEvent(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSource> commandDispatcher = event.getDispatcher();
        registerCommands(commandDispatcher);
    }

    public static void registerCommands(CommandDispatcher<CommandSource> dispatcher) {
        LiteralArgumentBuilder<CommandSource> printtags =
                Commands.literal("printtags")
                        .then(Commands.argument("modid", StringArgumentType.word())
                            .executes(context -> printTags(context, StringArgumentType.getString(context, "modid"))))
                        .executes(context -> printTags(context, "_"));

        dispatcher.register(printtags);
    }

    static int printTags(CommandContext<CommandSource> context, String filter){
        Entity sender = context.getSource().getEntity();
        if(sender instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) sender;
            ItemStack inhand = player.getHeldItemMainhand();
            if(!inhand.isEmpty()) {
               Item type = inhand.getItem();
               StringTextComponent out = new StringTextComponent("Item: " + TextFormatting.YELLOW + type.getName().getString() + TextFormatting.GOLD + " (" + type.getRegistryName() + ")");
               player.sendMessage(out, Util.DUMMY_UUID);

               Set<ResourceLocation> iTags = type.getTags();
               StringBuilder sb = new StringBuilder(0);
               if(!iTags.isEmpty()) {
                   sb.append("Item Tags: ").append(TextFormatting.GRAY);
                   for (ResourceLocation tag : iTags) {
                       String ns = tag.getNamespace();
                       if(ns.equals(filter)) {sb.append(TextFormatting.GREEN).append(TextFormatting.BOLD);}
                       sb.append(tag.toString()).append(", ");
                       if(ns.equals(filter)) {sb.append(TextFormatting.GRAY);}
                   }
                   player.sendMessage(new StringTextComponent(sb.toString()), Util.DUMMY_UUID);
                   sb.setLength(0);
               }

                Set<ResourceLocation> bTags = Block.getBlockFromItem(type).getTags();
                if(!bTags.isEmpty()) {
                    sb.append("Block Tags: ").append(TextFormatting.GRAY);
                    for (ResourceLocation tag : bTags) {
                        String ns = tag.getNamespace();
                        if(ns.equals(filter)) {sb.append(TextFormatting.GREEN).append(TextFormatting.BOLD);}
                        sb.append(tag.toString()).append(", ");
                        if(ns.equals(filter)) {sb.append(TextFormatting.GRAY);}
                    }
                    player.sendMessage(new StringTextComponent(sb.toString()), Util.DUMMY_UUID);
                    sb.setLength(0);
                }
            }
            else {
                player.sendMessage(new StringTextComponent(TextFormatting.RED+"You're not holding anything!"), Util.DUMMY_UUID);
            }
        }

        return 1;
    }

}
