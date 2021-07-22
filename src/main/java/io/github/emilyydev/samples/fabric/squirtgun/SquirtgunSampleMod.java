package io.github.emilyydev.samples.fabric.squirtgun;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import me.lucyy.squirtgun.command.argument.GreedyStringArgument;
import me.lucyy.squirtgun.command.context.CommandContext;
import me.lucyy.squirtgun.command.node.CommandNode;
import me.lucyy.squirtgun.command.node.NodeBuilder;
import me.lucyy.squirtgun.fabric.FabricNodeExecutor;
import me.lucyy.squirtgun.fabric.FabricPlatform;
import me.lucyy.squirtgun.format.FormatProvider;
import me.lucyy.squirtgun.platform.audience.SquirtgunUser;
import me.lucyy.squirtgun.platform.scheduler.Task;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import org.jetbrains.annotations.NotNull;

import static net.kyori.adventure.text.Component.space;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.TextColor.color;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class SquirtgunSampleMod implements DedicatedServerModInitializer {

  private MinecraftServer server = null;
  private FabricPlatform squirtgunPlatform = null;

  @Override
  public void onInitializeServer() {
    ServerLifecycleEvents.SERVER_STARTING.register(this::serverStarting);
    ServerLifecycleEvents.SERVER_STOPPING.register(this::serverStopping);
    CommandRegistrationCallback.EVENT.register(this::commandRegistration);
  }

  private void serverStarting(final MinecraftServer server) {
    this.server = server;
    this.squirtgunPlatform = new FabricPlatform(server);

    this.squirtgunPlatform
        .getTaskScheduler()
        .start(Task.builder(platform -> platform.getConsole().sendMessage(text().append(text("Players online:"), space(),
                                                                                        text(platform.getOnlinePlayers().size()))))
                   .async().delay(20 * 60).interval(20 * 60).build());
  }

  private void serverStopping(final MinecraftServer server) {
    this.server = null;
    this.squirtgunPlatform = null;
  }

  private void commandRegistration(final CommandDispatcher<ServerCommandSource> dispatcher, final boolean dedicatedServer) {
    final CommandNode<SquirtgunUser> node = new NodeBuilder<SquirtgunUser>()
        .name("squirtgun-sample-mod")
        .permission("ssm.command")
        .arguments(new GreedyStringArgument("rest", "Command arguments...", true))
        .executes(this::command)
        .build();

    final FabricNodeExecutor executor = new FabricNodeExecutor(node, new SimpleFormatProvider(), () -> this.squirtgunPlatform);
    dispatcher.register(literal(node.getName())
                            .executes(executor)
                            .then(argument("rest", StringArgumentType.greedyString())
                                      .suggests(executor)
                                      .executes(executor)));
  }

  private Component command(final CommandContext<SquirtgunUser> context) {
    return text().append(text(context.getRaw()), space(), text(":D")).build();
  }

  private static final class SimpleFormatProvider implements FormatProvider {

    private static final Component CHAT_PREFIX = text().append(text('['), text("SSM", color(0xf597cf)), text(']')).build();

    @Override
    public Component formatMain(@NotNull final String message, @NotNull final TextDecoration[] decorations) {
      return text(message, NamedTextColor.WHITE, decorations);
    }

    @Override
    public Component formatAccent(@NotNull final String message, @NotNull final TextDecoration[] decorations) {
      return text(message, NamedTextColor.WHITE, decorations);
    }

    @Override
    public Component getPrefix() {
      return CHAT_PREFIX;
    }
  }
}
