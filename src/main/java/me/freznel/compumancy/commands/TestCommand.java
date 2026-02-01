package me.freznel.compumancy.commands;

import com.hypixel.hytale.codec.ExtraInfo;
import com.hypixel.hytale.protocol.GameMode;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import me.freznel.compumancy.Compumancy;
import me.freznel.compumancy.codec.CustomMapCodec;
import me.freznel.compumancy.vm.execution.Invocation;
import me.freznel.compumancy.vm.objects.BoolObject;
import me.freznel.compumancy.vm.objects.MetaObject;
import me.freznel.compumancy.vm.objects.VMObject;
import org.bson.BsonValue;

import javax.annotation.Nonnull;
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

        BsonValue ser = VMObject.CODEC.encode(MetaObject.START_LIST, new ExtraInfo());
        BsonValue ser2 = VMObject.CODEC.encode(MetaObject.END_LIST, new ExtraInfo());
        ctx.sendMessage(Message.raw(ser.toString()));
        var ins1 = VMObject.CODEC.decode(ser, new ExtraInfo());
        var ins2 = VMObject.CODEC.decode(ser2, new ExtraInfo());
        ctx.sendMessage(Message.raw(ins1.toString()));
        ctx.sendMessage(Message.raw(ins2.toString()));
        ctx.sendMessage(Message.raw(Boolean.toString(ins1 == ins2)));

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
//                var invocation = new Invocation(world, sender, UUIDComponent.getUuid(), input, 1000);
//
//                var invocationComponentType = Compumancy.Get().GetInvocationComponentType();
//                var invocationComponent = store.getComponent(sender, invocationComponentType);
//                if (invocationComponent == null) {
//                    invocationComponent = store.addComponent(sender, invocationComponentType);
//                }
//                invocationComponent.SetMaxInvocations(3);
//
//                invocationComponent.Add(invocation);
//                invocation.Start();
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