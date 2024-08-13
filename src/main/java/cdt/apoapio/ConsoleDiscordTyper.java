package cdt.apoapio;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import javax.security.auth.login.LoginException;

public class ConsoleDiscordTyper extends JavaPlugin {

    private JDA jda;
    private String token;
    private String channelId;

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

        jda = JDABuilder.createDefault(token).addEventListeners(new DiscordListener()).build();
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
                String command = event.getMessage().getContentRaw();
                Bukkit.getScheduler().runTask(ConsoleDiscordTyper.this, () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command));
            }
        }
    }
}