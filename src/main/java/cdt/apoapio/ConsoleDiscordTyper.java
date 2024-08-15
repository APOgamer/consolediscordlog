package cdt.apoapio;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;

public class ConsoleDiscordTyper extends JavaPlugin {

    private JDA jda;
    private String token;
    private String channelId;
    private static final String COMMAND_PREFIX = "command "; // Define el prefijo de comando

    @Override
    public void onEnable() {
        saveDefaultConfig();
        token = getConfig().getString("token");
        channelId = getConfig().getString("channelId");

        if (token == null || channelId == null) {
            getLogger().severe("Please set the Discord bot token and channel ID in the config file.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        try {
            jda = JDABuilder.createDefault(token)
                    .enableIntents(GatewayIntent.MESSAGE_CONTENT) // Habilita la intención de contenido de mensajes
                    .addEventListeners(new DiscordListener())
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
            getServer().getPluginManager().disablePlugin(this);
        }
    }

    @Override
    public void onDisable() {
        if (jda != null) {
            jda.shutdown();
        }
    }

    private class DiscordListener extends ListenerAdapter {
        @Override
        public void onMessageReceived(MessageReceivedEvent event) {
            if (event.getChannel().getId().equals(channelId) && !event.getAuthor().isBot()) {
                String message = event.getMessage().getContentRaw().trim();

                if (message.startsWith(COMMAND_PREFIX)) {
                    String command = message.substring(COMMAND_PREFIX.length()).trim();

                    if (!command.isEmpty()) {
                        Bukkit.getScheduler().runTask(ConsoleDiscordTyper.this, () -> {
                            boolean success = Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
                            if (!success) {
                                event.getChannel().sendMessage("El comando '" + command + "' no se pudo ejecutar.").queue();
                            }
                        });
                    } else {
                        event.getChannel().sendMessage("El comando no puede estar vacío.").queue();
                    }
                }
            }
        }
    }
}
