package me.freznel.compumancy.commands;

import com.hypixel.hytale.assetstore.AssetRegistry;
import com.hypixel.hytale.codec.ExtraInfo;
import com.hypixel.hytale.protocol.GameMode;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.inventory.container.ItemContainer;
import it.unimi.dsi.fastutil.shorts.Short2ObjectOpenHashMap;
import me.freznel.compumancy.Compumancy;
import me.freznel.compumancy.codec.CustomMapCodec;
import me.freznel.compumancy.vm.execution.Invocation;
import me.freznel.compumancy.vm.objects.BoolObject;
import me.freznel.compumancy.vm.objects.MetaObject;
import me.freznel.compumancy.vm.objects.VMObject;
import org.bson.BsonValue;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

/**
 * This is an example command that will simply print the name of the plugin in chat when used.
 */
public class TestCommand extends CommandBase {

    public TestCommand() {
        super("test", "Test command");
        this.setPermissionGroup(GameMode.Adventure);
        this.setAllowsExtraArguments(true);
    }

    @Override
    protected void executeSync(@Nonnull CommandContext ctx) {
            var sender = ctx.senderAsPlayerRef();
            assert sender != null;
            var store = sender.getStore();

            var container = CompletableFuture.supplyAsync(() -> {
                var player = store.getComponent(sender, Player.getComponentType());
                assert player != null;
                return player.getInventory().getStorage();
            }, store.getExternalData().getWorld()).join();

            var tagIndex = AssetRegistry.getOrCreateTagIndex("Type=Compumancy:Capacitor");
            var found = new Short2ObjectOpenHashMap<ItemStack>();

            container.forEach((slot, stack) -> {
                var item = stack.getItem();
                var data = item.getData();
                if (data == null || !data.getExpandedTagIndexes().contains(tagIndex)) return;
                var durability = stack.getDurability();
                if (durability > 0) found.put(slot, stack);
            });

            double remaining = 2500;
            for (short slot : found.keySet()) {
                var original = found.get(slot);
                var durability = original.getDurability();
                double consume = Math.min(remaining, durability);
                if (consume > 0) {
                    var result = container.replaceItemStackInSlot(slot, original, original.withDurability(durability - consume));
                    if (result.succeeded()) remaining -= consume;
                }
                if (remaining < 0.01) break;
            }



//        try {
//            String inputString = ctx.getInputString();
//            String cmdName = this.getName();
//            assert cmdName != null;
//            String input = inputString.substring(inputString.indexOf(cmdName) + this.getName().length());
//            ctx.sendMessage(Message.raw("Processing: " + input));
//
//            var sender = ctx.senderAsPlayerRef();
//            assert sender != null;
//            var world = sender.getStore().getExternalData().getWorld();
//            var store = world.getEntityStore().getStore();
//
//            CompletableFuture.runAsync(() -> {
//                var UUIDComponent = store.getComponent(sender, com.hypixel.hytale.server.core.entity.UUIDComponent.getComponentType());
//                if (UUIDComponent == null) return;
//
//                var invocationComponentType = Compumancy.Get().GetInvocationComponentType();
//                var invocationComponent = store.getComponent(sender, invocationComponentType);
//                if (invocationComponent == null) {
//                    invocationComponent = store.addComponent(sender, invocationComponentType);
//                }
//                invocationComponent.SetMaxInvocations(Compumancy.Get().GetConfig().MaxPlayerInvocations);
//
//                var defComponentType = Compumancy.Get().GetDefinitionStoreComponentType();
//                var defComponent = store.getComponent(sender, defComponentType);
//                if (defComponent == null) {
//                    defComponent = store.addComponent(sender, defComponentType);
//                }
//                defComponent.SetMaxUserDefs(Compumancy.Get().GetConfig().MaxPlayerInvocations);
//
//                var invocation = new Invocation(world, sender, UUIDComponent.getUuid(), input, 1000);
//                invocationComponent.Add(invocation);
//                invocation.Start();
//
//            }, world).handle((_, e) -> {
//
//                return null;
//            });
//
//        } catch (Exception e) {
//            ctx.sendMessage(Message.raw(e.getClass().getSimpleName() + ": " + e.getMessage()));
//        }
    }
}