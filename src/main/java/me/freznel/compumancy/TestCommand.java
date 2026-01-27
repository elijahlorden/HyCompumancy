package me.freznel.compumancy;

import com.hypixel.hytale.protocol.GameMode;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import me.freznel.compumancy.vm.compiler.Compiler;
import me.freznel.compumancy.vm.compiler.Tokenizer;
import me.freznel.compumancy.vm.execution.Invocation;

import javax.annotation.Nonnull;

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

            var invocation = new Invocation(world, sender, program, 1000);
            invocation.Run();

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

            ctx.sendMessage(Message.raw("Done"));
        } catch (Exception e) {
            ctx.sendMessage(Message.raw(e.getClass().getSimpleName() + ": " + e.getMessage()));
        }
    }
}