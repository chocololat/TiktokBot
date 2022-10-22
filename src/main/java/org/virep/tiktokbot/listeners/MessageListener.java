package org.virep.tiktokbot.listeners;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.ErrorHandler;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import net.dv8tion.jda.api.requests.ErrorResponse;
import net.dv8tion.jda.api.utils.FileUpload;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONObject;

import java.net.URL;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MessageListener extends ListenerAdapter {

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        Message message = event.getMessage();

        Pattern pattern = Pattern.compile("\\bhttps?:\\/\\/(?:m|www|vm)\\.tiktok\\.com\\/\\S*?\\b(?:(?:(?:usr|v|embed|user|video)\\/|\\?shareId=|\\&item_id=)(\\d+)|(?=\\w{7})(\\w*?[A-Z\\d]\\w*)(?=\\s|\\/$))\\b");
        Matcher matcher = pattern.matcher(message.getContentRaw());

        if (matcher.find()) {
            event.getChannel().sendTyping().queue();
            System.out.println("Full Match: " + matcher.group(0));

            try {
                OkHttpClient client = new OkHttpClient.Builder()
                        .readTimeout(30, TimeUnit.SECONDS)
                        .build();

                Request request = new Request.Builder()
                        .url("https://api.mesavirep.xyz/v1/tiktok?url=" + matcher.group(0))
                        .build();

                Response res = client.newCall(request).execute();

                assert res.body() != null;

                JSONObject jsonObject = new JSONObject(res.body().string());
                JSONObject jsonBody = jsonObject.getJSONObject("body");

                message.replyFiles(FileUpload.fromData(new URL(jsonBody.getString("downloadURL")).openStream(), "tiktok.mp4"))
                        .queue(null, new ErrorHandler()
                                .handle(ErrorResponse.REQUEST_ENTITY_TOO_LARGE, (e) -> message.reply(jsonBody.getString("downloadURL")).queue()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
