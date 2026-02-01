package me.freznel.compumancy.config;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;

public class CompumancyConfig {
    public static final BuilderCodec<CompumancyConfig> CODEC = BuilderCodec.builder(CompumancyConfig.class, CompumancyConfig::new)
            .append(new KeyedCodec<>("MaxExecutionBudget", Codec.INTEGER), (o, v) -> o.MaxExecutionBudget = v, o -> o.MaxExecutionBudget)
            .documentation("Maximum static execution budget for invocations per step")
            .add()

            .append(new KeyedCodec<>("MaxExecutionTime", Codec.INTEGER), (o, v) -> o.MaxExecutionTime = v, o -> o.MaxExecutionTime)
            .documentation("Maximum execution time for a single invocation step (ms)")
            .add()

            .append(new KeyedCodec<>("AsyncStepDelay", Codec.INTEGER), (o, v) -> o.AsyncStepDelay = v, o -> o.AsyncStepDelay)
            .documentation("Delay between invocation steps when running on a background thread (ms)")
            .add()

            .append(new KeyedCodec<>("SyncStepDelay", Codec.INTEGER), (o, v) -> o.SyncStepDelay = v, o -> o.SyncStepDelay)
            .documentation("Delay between invocation steps when running on a world thread (ms)")
            .add()

            .append(new KeyedCodec<>("CheckpointInterval", Codec.INTEGER), (o, v) -> o.CheckpointInterval = v, o -> o.CheckpointInterval)
            .documentation("Delay between invocation checkpoints.  Long-running invocations will only persist their state to the InvocationComponent this often (ms)")
            .add()

            .append(new KeyedCodec<>("AsyncThreadCount", Codec.INTEGER), (o, v) -> o.AsyncThreadCount = v, o -> o.AsyncThreadCount)
            .documentation("The number of threads available for async execution of invocations")
            .add()

            .append(new KeyedCodec<>("MaxPlayerInvocations", Codec.INTEGER), (o, v) -> o.MaxPlayerInvocations = v, o -> o.MaxPlayerInvocations)
            .documentation("The maximum number of invocations a player can run directly at the same time")
            .add()

            .append(new KeyedCodec<>("MaxPlayerDefinitions", Codec.INTEGER), (o, v) -> o.MaxPlayerDefinitions = v, o -> o.MaxPlayerDefinitions)
            .documentation("The maximum number of definitions a for direct player invocations")
            .add()

            .append(new KeyedCodec<>("MaxDefinitionSize", Codec.INTEGER), (o, v) -> o.MaxDefinitionSize = v, o -> o.MaxDefinitionSize)
            .documentation("The maximum size of a definition.  Each object has a size of 1.  Lists have a size of 1 + count.  Strings have a size of 1 + length/50.  Vectors have a size of 3.")
            .add()

            .build();

    public int MaxExecutionBudget = 512;
    public int MaxExecutionTime = 5;
    public int AsyncStepDelay = 50;
    public int SyncStepDelay = 100;
    public int CheckpointInterval = 5000;
    public int AsyncThreadCount = 4;
    public int MaxPlayerInvocations = 3;
    public int MaxPlayerDefinitions = 256;
    public int MaxDefinitionSize = 1024;


}
