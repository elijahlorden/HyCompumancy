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

            .append(new KeyedCodec<>("AsyncThreadCount", Codec.INTEGER), (o, v) -> o.AsyncThreadCount = v, o -> o.AsyncThreadCount)
            .documentation("The number of threads available for async execution of invocations")
            .add()

            .append(new KeyedCodec<>("AsyncStepDelay", Codec.INTEGER), (o, v) -> o.AsyncStepDelay = v, o -> o.AsyncStepDelay)
            .documentation("Base delay between invocation steps when running on a background thread (ms)")
            .add()

            .append(new KeyedCodec<>("SyncStepDelay", Codec.INTEGER), (o, v) -> o.SyncStepDelay = v, o -> o.SyncStepDelay)
            .documentation("Base delay between invocation steps when running on a world thread (ms)")
            .add()

            .append(new KeyedCodec<>("DelayThreshold", Codec.INTEGER), (o, v) -> o.DelayThreshold = v, o -> o.DelayThreshold)
            .documentation("The number of invocation steps scheduled per second before the delay kicks in, per player")
            .add()

            .append(new KeyedCodec<>("CheckpointInterval", Codec.INTEGER), (o, v) -> o.DelayPerStep = v, o -> o.DelayPerStep)
            .documentation("How many milliseconds to add onto the invocation step delay per step scheduled over the threshold")
            .add()

            .append(new KeyedCodec<>("MaxPlayerDefinitions", Codec.INTEGER), (o, v) -> o.MaxPlayerDefinitions = v, o -> o.MaxPlayerDefinitions)
            .documentation("The maximum number of definitions a for direct player invocations")
            .add()

            .append(new KeyedCodec<>("MaxDefinitionSize", Codec.INTEGER), (o, v) -> o.MaxDefinitionSize = v, o -> o.MaxDefinitionSize)
            .documentation("The maximum size of a definition.  Each object has a size of 1.  Lists have a size of 1 + count.  Strings have a size of 1 + length/50.  Vectors have a size of 3.")
            .add()

            .build();

    public int MaxExecutionBudget = 512;
    public int MaxExecutionTime = 2;
    public int AsyncStepDelay = 50;
    public int SyncStepDelay = 100;
    public int AsyncThreadCount = 4;
    public int DelayThreshold = 100;
    public int DelayPerStep = 1;
    public int MaxPlayerDefinitions = 256;
    public int MaxDefinitionSize = 1024;


}
