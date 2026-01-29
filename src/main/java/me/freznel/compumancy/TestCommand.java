package me.freznel.compumancy;

import com.hypixel.hytale.protocol.GameMode;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import me.freznel.compumancy.casting.InvocationComponent;
import me.freznel.compumancy.vm.compiler.Compiler;
import me.freznel.compumancy.vm.execution.Invocation;

import javax.annotation.Nonnull;
import java.util.concurrent.CompletableFuture;

/**
 * This is an example command that will simply print the name of the plugin in chat when used.
 */
public class TestCommand extends CommandBase {

    public TestCommand(String pluginName) {
        super("test", "Prints a test message from the " + pluginName + " plugin.");
        this.setPermissionGroup(GameMode.Adventure);
        this.setAllowsExtraArguments(true);
    }

    @Override
    protected void executeSync(@Nonnull CommandContext ctx) {
        try {
            String input = ctx.getInputString().substring(this.getName() == null ? 0 : this.getName().length());
            ctx.sendMessage(Message.raw("Processing: " + input));

            var program = Compiler.Compile(input);

            var sender = ctx.senderAsPlayerRef();
            assert sender != null;
            var world = sender.getStore().getExternalData().getWorld();
            var store = world.getEntityStore().getStore();

            var invocation = new Invocation(world, sender, program, 1000);

            CompletableFuture.runAsync(() -> {
                var invocationComponentType = Compumancy.Get().GetInvocationComponentType();
                var invocationComponent = store.getComponent(sender, invocationComponentType);
                if (invocationComponent == null) {
                    invocationComponent = store.addComponent(sender, invocationComponentType);
                }
                invocationComponent.SetMaxInvocations(5);
                invocationComponent.RemoveAll();

                invocationComponent.Add(invocation);
                invocation.Start();
            }, world).handle((_, e) -> {


                return null;
            });











            /*invocation.Step();

            var resultStack = invocation.GetOperandStack();
            StringBuilder sb = new StringBuilder();
            sb.append(String.format("Result size: %d\n", resultStack.size()));
            sb.append("-- Top of stack --\n");
            for (var obj : resultStack.reversed()) {
                sb.append(obj.toString());
                sb.append("\n");
            }
            sb.append("-- Bottom of stack --");
            ctx.sendMessage(Message.raw(sb.toString()));

            ctx.sendMessage(Message.raw("Done"));*/
        } catch (Exception e) {
            ctx.sendMessage(Message.raw(e.getClass().getSimpleName() + ": " + e.getMessage()));
        }
    }
}