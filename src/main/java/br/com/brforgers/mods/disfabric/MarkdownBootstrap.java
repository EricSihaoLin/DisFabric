package br.com.brforgers.mods.disfabric;// Created 2022-05-03T04:45:21

import br.com.brforgers.mods.disfabric.markdown.DiscordChatExtension;
import br.com.brforgers.mods.disfabric.markdown.EmoteNode;
import br.com.brforgers.mods.disfabric.markdown.MentionNode;
import br.com.brforgers.mods.disfabric.markdown.TimeNode;
import br.com.brforgers.mods.disfabric.utils.Utils;
import dev.gegy.mdchat.NodeStyler;
import dev.gegy.mdchat.StylerBootstrap;
import net.dv8tion.jda.api.entities.Channel;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.fabricmc.fabric.api.event.Event;
import org.commonmark.parser.Parser;

import java.util.Collections;

/**
 * @author KJP12
 * @since ${version}
 **/
public class MarkdownBootstrap implements StylerBootstrap {
    @Override
    public void bootstrap(Parser.Builder parser, Event<NodeStyler> styler) {
        parser.extensions(Collections.singleton(DiscordChatExtension.INSTANCE));
        styler.register((node, child) -> {
            if (node instanceof EmoteNode emote) {
                return Utils.convertUnknownEntityToFormattedText(emote.id, "emote", ":" + emote.name + ":");
            } else if (node instanceof MentionNode mention) {
                if (DisFabric.jda == null) {
                    // We cannot do much without JDA, so, just return default.
                    return child.get();
                }
                switch (mention.type) {
                    case USER -> {
                        User user = DisFabric.jda.getUserById(mention.id);
                        return user == null ? Utils.convertUnknownEntityToFormattedText(mention.id, "user", "@Unknown")
                                : Utils.convertMemberToFormattedText(user, DisFabric.guild.getMember(user), "@");
                    }
                    case ROLE -> {
                        Role role = DisFabric.guild.getRoleById(mention.id);
                        return role == null ? Utils.convertUnknownEntityToFormattedText(mention.id, "role", "@Unknown")
                                : Utils.convertRoleToFormattedText(role, "@");
                    }
                    case CHANNEL -> {
                        Channel channel = DisFabric.guild.getGuildChannelById(mention.id);
                        return channel == null ? Utils.convertUnknownEntityToFormattedText(mention.id, "channel", "#unknown-channel")
                                : Utils.convertChannelToFormattedText(channel, "#");
                    }
                }
            } else if (node instanceof TimeNode time) {
                return Utils.convertUnknownEntityToFormattedText(time.timestamp, "time", time.style.style(time.timestamp));
            }
            return null;
        });
    }
}
