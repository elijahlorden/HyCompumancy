package me.freznel.compumancy;

import com.hypixel.hytale.protocol.GameMode;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import me.freznel.compumancy.vm.compiler.Tokenizer;

import javax.annotation.Nonnull;

/**
 * This is an example command that will simply print the name of the plugin in chat when used.
 */
public class TestCommand extends CommandBase {

    public TestCommand(String pluginName) {
        super("test", "Prints a test message from the " + pluginName + " plugin.");
        this.setPermissionGroup(GameMode.Adventure);
    }

    @Override
    protected void executeSync(@Nonnull CommandContext ctx) {
        try {
            /*var program = new ArrayList<VMObject>();
            program.add(new NumberObject(1));
            program.add(new ActionObject("dup"));
            program.add(new BinaryOperatorObject(BinaryOperator.Add));
            program.add(new NumberObject(12));
            program.add(new BinaryOperatorObject(BinaryOperator.Multiply));
            program.add(new NumberObject(1));

            var sublist = new ArrayList<VMObject>();
            sublist.add(new NumberObject(123));
            sublist.add(new UnaryOperatorObject(UnaryOperator.Negate));
            program.add(new ListObject(sublist));

            var sender = ctx.senderAsPlayerRef();
            assert sender != null;
            var world = sender.getStore().getExternalData().getWorld();

            var invocation = new Invocation(world, sender, program, 1000);
            invocation.Run();

            var resultStack = invocation.GetOperandStack();
            StringBuilder sb = new StringBuilder();
            sb.append("-- Top of stack --\n");
            for (var obj : resultStack.reversed()) {
                sb.append(obj.toString());
                sb.append("\n");
            }
            sb.append("-- Bottom of stack --");
            ctx.sendMessage(Message.raw(sb.toString()));*/

            String test = "{ 123 456 abc \"d e f\" <1 2 3> <4, 5, 6> <7,8,9> } ( ) a";
            Tokenizer tokenizer = new Tokenizer(test);
            Tokenizer.Token tkn;
            ctx.sendMessage(Message.raw("Test"));
            while ((tkn = tokenizer.Next()) != null) ctx.sendMessage(Message.raw(String.format("%s: %s", tkn.type().toString(), tkn.value().toString())));

            ctx.sendMessage(Message.raw("Done"));
        } catch (Exception e) {
            ctx.sendMessage(Message.raw(e.getClass().getSimpleName() + ": " + e.getMessage()));
        }
    }
}