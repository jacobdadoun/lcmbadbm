package edu.touro.mco152.bm.externalsys;

import com.slack.api.Slack;
import com.slack.api.methods.MethodsClient;
import com.slack.api.methods.SlackApiException;
import com.slack.api.methods.request.chat.ChatPostMessageRequest;
import com.slack.api.methods.response.api.ApiTestResponse;
import com.slack.api.methods.response.chat.ChatPostMessageResponse;
import edu.touro.mco152.bm.BMObserver;

import java.io.IOException;

/**
 * SlackManager is an observer class that will send a message to slack that our BM has finished after the read command
 * class has been executed from DiskWorker
 */

/**
 * A Slack Manager for BadBM that just knows how to send a string msg to a pre-designated channel
 * using hard-coded authentication token.
 * Usage:
 *          SlackManager slackmgr = new SlackManager("myAppName");
 *          Boolean worked = slackmgr.postMsg2OurChannel(":smile: Benchmark completed");
 */
public class SlackManager implements BMObserver {
    private static Slack slack = null;  // obtain/keep one copy of expensive item
    /**
     * Token for use with Slack API, representing info about our bot/app/channel.
     * If the token is a bot token, it starts with `xoxb-` while if it's a user token, it starts with `xoxp-`
     * This should really come from one of the badbm properties files, or from System.getenv()
     */
    private final String token = "xoxb-121008389125-1115203038051-sUdT3T2GJQ4aLZDVHjazQgxC";
    /**
     * The channel we will send all our messages to
     */
    private final String ourChannel = "mco152_auto_notifications";
    private String appName = "App";

    private SlackManager() {
        // disallow private constructor
    }

    /**
     * Construct a Slack Manager for our application, to be used for sending messages to Slack
     *
     * @param appName - pass a sring like BadBM, or whatever name you want to appear in msgs
     */
    public SlackManager(String appName) {
        this.appName = appName;

        if (slack == null)
            slack = Slack.getInstance();

        // auto-validate environment
        try {
            ApiTestResponse testResponse = slack.methods().apiTest(r -> r.foo("bar"));
            if (!testResponse.isOk()) {
                System.err.println("SlackManager: Problem with auto-validation of Slack: "
                        + testResponse.getError());
            }
        } catch (IOException | SlackApiException exc) {
            System.err.println("SlackManager: Problem with auto-validation of Slack");
            exc.printStackTrace();
        }
    }

    /**
     * Post slack message to our pre-defined channel using predefined credential token
     *
     * @param msg - String with message to post, can contain emojis like :smile:
     * @return True if successful
     */
    private Boolean postMsg2OurChannel(String msg) {
        // Initialize an API Methods client with the given token
        MethodsClient methods = slack.methods(this.token);

        String postText = String.format("Automated message from <%s>%s: %s",
                System.getProperty("user.name"), appName, msg);

        // Build a request object
        ChatPostMessageRequest request = ChatPostMessageRequest.builder()
                .channel(ourChannel)
                .text(postText)
                .build();

        // Get a response as a Java object
        ChatPostMessageResponse response = null;
        try {
            response = methods.chatPostMessage(request);
        } catch (IOException | SlackApiException exc) {
            System.err.println("SlackManager: Problem with execution of chatPostMessage");
            exc.printStackTrace();
            return false;
        }


        if (response.isOk()) {
            return true;
        } else {
            System.err.println("SlackManager: Problem with Response = " + response);
            return false;
        }
    }

    private String msg;

    public void setMessage(String msg){
        this.msg = msg;
    }

    @Override
    public void update() {
        // Boolean worked = slackmgr.postMsg2OurChannel(":cry: Benchmark failed");
        Boolean worked = postMsg2OurChannel(msg);
        System.err.println("Returned boolean from sending msg is " + worked);
    }
}
