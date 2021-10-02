import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;

import java.awt.*;

public class timer extends Thread{
    String id;
    boolean is = true;
    User user;

    public timer(String id, User user){
        this.id = id;
        this.user = user;
    }

    @Override
    public void run() {
        try {
            Thread.sleep(60000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if(is){
            Message ms = bot.st.get(id);
            ms.editMessageEmbeds(new EmbedBuilder().setTitle("MUG 문의").addField("문의가 취소되었습니다.", "시간이 초과되어 자동적으로 문의가 취소되었습니다", false)
                    .setColor(Color.RED).build()).override(true).queue();
            bot.st.remove(id);
            bot.message.remove(user);
        }
    }

    @Override
    public void interrupt() {
        is = false;
    }
}
