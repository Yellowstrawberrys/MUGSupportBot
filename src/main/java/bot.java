import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SelectionMenuEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.Button;
import net.dv8tion.jda.api.interactions.components.selections.SelectionMenu;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class bot extends ListenerAdapter {
    String guildid = "894088261767684168";
    String report = "894128322404048896";
    String server_bug = "894129235063627797";
    String server_support = "894128282935640115";

    public static HashMap<User, Message> message = new HashMap<>();
    public static HashMap<String, Message> st = new HashMap<>();
    HashMap<UUID, User> other = new HashMap<>();
    HashMap<UUID, Message> other_ms = new HashMap<>();
    HashMap<User, String> user = new HashMap<>();
    HashMap<String, User> channel = new HashMap<>();
    HashMap<String, User> users = new HashMap<>();

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if(!event.getChannelType().isGuild()){
            if(!event.getAuthor().isSystem() && !event.getAuthor().isBot() && !user.containsKey(event.getAuthor())) {
                SelectionMenu.Builder builder = SelectionMenu.create(event.getAuthor().getId() + "_support");
                builder.setPlaceholder("카테고리를 선택");
                builder.addOption("서버 건의사항", "server_support", "서버의 건의사항에 대해 문의를 시작합니다.", Emoji.fromUnicode("\uD83D\uDCCB"));
                builder.addOption("서버 오류/버그", "server_bug","서버의 오류와 버그에 대해 문의를 시작합니다.", Emoji.fromUnicode("\u2699"));
                builder.addOption("유저 신고", "user_report","유저 신고에 대해 문의를 시작합니다.", Emoji.fromUnicode("\u26D4"));
                builder.addOption("기타 문의", "other","서버에 관한 기타 문의를 시작합니다.", Emoji.fromUnicode("\uD83D\uDCA1"));
                EmbedBuilder em = new EmbedBuilder().setTitle("MUG 문의센터").addField("문의 카테고리를 선택해 주세요", "60초 안에 카테고리 미선택 시, 문의가 종료됩니다.", false).setColor(Color.YELLOW);
                event.getChannel().sendMessageEmbeds(em.build()).setActionRow(builder.build()).queue(s ->{
                    Thread th = new timer(event.getAuthor().getId(), event.getAuthor());
                    th.setName(s.getId());
                    th.start();
                    st.put(event.getAuthor().getId(), s);
                    message.put(event.getAuthor(), event.getMessage());
                });
            }else if(!event.getAuthor().isSystem() && !event.getAuthor().isBot() && user.containsKey(event.getAuthor())){
                Objects.requireNonNull(Objects.requireNonNull(main.jda.getGuildById(guildid)).getTextChannelById(user.get(event.getAuthor()))).sendMessageEmbeds(new EmbedBuilder().setTitle("MUG 문의 시스템")
                        .addField(event.getMessage().getContentRaw(), event.getMessage().getTimeCreated().toZonedDateTime().format(DateTimeFormatter.RFC_1123_DATE_TIME), false)
                        .setAuthor(event.getAuthor().getAsTag(), "https://thdisstudio.cf/", event.getAuthor().getAvatarUrl()).build()).queue(s -> {
                    event.getMessage().addReaction("✅").queue();
                });
            }
        }
    }

    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
        String[] args = event.getMessage().getContentDisplay().split(" ");
        if(event.getMember().hasPermission(Permission.ADMINISTRATOR)) {
            if(channel.containsKey(event.getChannel().getId())){
                if(event.getMessage().getContentRaw().equalsIgnoreCase("!문의종료")){
                    User u = channel.get(event.getChannel().getId());
                    u.openPrivateChannel().queue(ch -> {
                        ch.sendMessageEmbeds(new EmbedBuilder().setTitle("MUG 문의센터").setColor(Color.RED)
                                .addField("문의가 종료되었습니다.", "스태프에 의해 종료되었습니다.", false).build()).queue();
                    });
                    user.remove(u);
                    channel.remove(event.getChannel().getId());
                    event.getChannel().delete().queue();
                }else if(!event.getAuthor().isBot() && !event.getAuthor().isSystem()){
                    User u = channel.get(event.getChannel().getId());
                    u.openPrivateChannel().queue(ch -> {
                        ch.sendMessageEmbeds(new EmbedBuilder().setTitle("MUG 문의센터")
                                .addField(event.getMessage().getContentRaw(), event.getMessage().getTimeCreated().toZonedDateTime().format(DateTimeFormatter.RFC_1123_DATE_TIME), false)
                                .setAuthor(event.getAuthor().getAsTag(), "https://discord.gg/GyxAjyGK2v", event.getAuthor().getAvatarUrl()).build()).queue(s -> {
                                    event.getMessage().addReaction("✅").queue();
                        });
                    });
                }
            }else {
                if (event.getMessage().getContentRaw().startsWith("!connect")) {
                    if (other.containsKey(UUID.fromString(args[1]))) {
                        event.getGuild().getCategoryById("894130604701024278").createTextChannel(args[1]).queue(ch -> {
                            try {
                                ch.createPermissionOverride(event.getGuild().getPublicRole())
                                        .setDeny(Permission.ALL_PERMISSIONS)
                                        .queue();
                                ch.createPermissionOverride(event.getMember())
                                        .setAllow(Permission.ALL_TEXT_PERMISSIONS)
                                        .queue();
                            }catch (Exception e){

                            }
                            user.put(other.get(UUID.fromString(args[1])), ch.getId());
                            channel.put(ch.getId(), other.get(UUID.fromString(args[1])));
                            ch.sendMessageEmbeds(new EmbedBuilder().setTitle("MUG 문의 시스템")
                                    .addField("유저와 성공적으로 연결되었습니다!", "!문의종료를 통해 문의를 종료하십시오", false).setColor(Color.GREEN).build()).queue();
                            other_ms.get(UUID.fromString(args[1])).editMessageEmbeds(new EmbedBuilder().setTitle("MUG 문의센터")
                                    .addField("해당 문의 담당자가 "+event.getMember().getEffectiveName()+"로 지정되었습니다.", "문의 내용 입력 시, 담당자에게 전달됩니다.", false).setColor(Color.GREEN)
                                    .build()).override(true).queue();
                            other_ms.remove(UUID.fromString(args[1]));
                            other.remove(UUID.fromString(args[1]));
                            event.getChannel().sendMessageEmbeds(new EmbedBuilder().setTitle("MUG 문의 시스템").addField("밑에 버튼을 눌러 해당채널으로 이동해주시길 바랍니다", "(시간이 걸릴 수 있습니다)", false)
                                            .setColor(Color.GREEN).build())
                                    .setActionRow(Button.link("https://discord.com/channels/" + event.getGuild().getId() + "/" + ch.getId(), "채널로 이동")).queue();
                        });
                    }
                }
            }
        }
    }

    @Override
    public void onSelectionMenu(SelectionMenuEvent event) {
        for(Thread t : Thread.getAllStackTraces().keySet()) {
            if(Objects.equals(t.getName(), event.getMessageId())) {
                t.interrupt();
            }
        }
        String op = Objects.requireNonNull(event.getSelectedOptions()).get(0).getValue();
        if(op.equals("other")) {
            UUID uuid = UUID.randomUUID();
            Objects.requireNonNull(Objects.requireNonNull(event.getJDA().getGuildById(guildid)).getTextChannelById("894129314956713984")).sendMessageEmbeds(new EmbedBuilder().setTitle("기타 문의")
                    .addField(event.getUser().getName()+"님이 기타 문의로 문의를 주셨습니다.", "!connect "+uuid+" 를 입력후, 버튼을 눌러, 문의를 시작해 주세요",false).build()).queue();
            other.put(uuid, event.getUser());
            other_ms.put(uuid, event.getMessage());
            event.getMessage().editMessageEmbeds(new EmbedBuilder().setTitle("MUG 문의센터").setColor(Color.GREEN)
                    .addField("정상적으로 문의가 접수되었습니다.", "**문의에 대한 답변은 48시간 내로 답변 드리겠습니다.**", false).build()).override(true).queue();
        }else if (op.equals("server_support")){
            try {
                users.put(event.getUser().getId(), event.getUser());
            }catch (Exception e){}
            Objects.requireNonNull(Objects.requireNonNull(event.getJDA().getGuildById(guildid)).getTextChannelById(server_support)).sendMessageEmbeds(new EmbedBuilder().setTitle("\uD83D\uDCCB 서버 건의사항")
                    .addField(event.getUser().getName()+"님이 서버 건의사항을 주셨습니다.", message.get(event.getUser()).getContentRaw(),false).build()).setActionRow(Button.secondary( event.getUser().getId()+"good","처리완료")).queue();
            message.remove(event.getUser());
            event.getMessage().editMessageEmbeds(new EmbedBuilder().setTitle("MUG 문의센터").addField("스테프가 승인할떄 까지 기다리는중...", "서버문의가 성공적으로 완료되었습니다", false).setColor(Color.GREEN).build()).override(true)
                    .queue();
        }else if (op.equals("server_bug")){
            try {
                users.put(event.getUser().getId(), event.getUser());
            }catch (Exception e){}
            Objects.requireNonNull(Objects.requireNonNull(event.getJDA().getGuildById(guildid)).getTextChannelById(server_bug)).sendMessageEmbeds(new EmbedBuilder().setTitle("\u2699 서버 오류/버그")
                    .addField(event.getUser().getName()+"님이 오류/버그을 제보해주셨습니다.", message.get(event.getUser()).getContentRaw(),false)
                    .setColor(Color.RED).build()).setActionRow(Button.secondary( event.getUser().getId()+"good","처리완료")).queue();
            message.remove(event.getUser());
            event.getMessage().editMessageEmbeds(new EmbedBuilder().setTitle("MUG 문의센터").addField("스테프가 승인할떄 까지 기다리는중...", "버그 제보가 성공적으로 완료되었습니다", false).setColor(Color.GREEN).build()).override(true)
                    .queue();
        }else if (op.equals("user_report")){
            try {
                users.put(event.getUser().getId(), event.getUser());
            }catch (Exception e){}
            Objects.requireNonNull(Objects.requireNonNull(event.getJDA().getGuildById(guildid)).getTextChannelById(report)).sendMessageEmbeds(new EmbedBuilder().setTitle("\u26D4 유저신고")
                    .addField(event.getUser().getName()+"님이 유저신고를 제보해주셨습니다.", message.get(event.getUser()).getContentRaw(),false)
                    .setColor(Color.RED).build()).setActionRow(Button.secondary( event.getUser().getId()+"good","처리완료")).queue();
            message.remove(event.getUser());
            event.getMessage().editMessageEmbeds(new EmbedBuilder().setTitle("MUG 문의센터").addField("스테프가 승인할떄 까지 기다리는중...", "유저신고가 성공적으로 완료되었습니다", false).setColor(Color.GREEN).build()).override(true)
                    .queue();
        }
    }

    @Override
    public void onButtonClick(ButtonClickEvent event) {
        String id = Objects.requireNonNull(Objects.requireNonNull(event.getButton()).getId());
        try {
            if (id.contains("good")) {
                users.get(id.replaceAll("good", "")).openPrivateChannel().queue(s -> {
                    s.sendMessageEmbeds(new EmbedBuilder().setTitle("MUG 문의 센터").addField("✅ 처리가 완료되었습니다",
                            event.getUser().getName() + " 스태프님이 처리하셨습니다", false).setColor(Color.GREEN).build()).queue();
                    event.getMessage().editMessageEmbeds(event.getMessage().getEmbeds().get(0)).setActionRow(Button.secondary("1", "✅ 처리가 완료됨").asDisabled()).override(true).queue();
                });
            }
        }catch (Exception e){

        }
    }
}
