package com.serezka.jpt.telegram.bot;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Getter
public class TUpdate {
    Update self;
    QueryType queryType;

    // cache utils
    @NonFinal
    private List<Flag> messageFlags = null;

    public TUpdate(Update self) {
        this.self = self;
        this.queryType = queryType(self);
    }

    public enum QueryType {
        MESSAGE, INLINE_QUERY, CHOSEN_INLINE_QUERY, CALLBACK_QUERY,
        EDITED_MESSAGE, CHANNEL_POST, EDITED_CHANNEL_POST, SHIPPING_QUERY,
        PRE_CHECKOUT_QUERY, POLL, POLL_ANSWER, CHAT_JOIN_REQUEST,
        CHAT_MEMBER_UPDATED_MY, CHAT_MEMBER_UPDATED, /*unknown*/ UNKNOWN;
    }

    public enum Flag { // if needed add some fields todo
        TEXT, ENTITIES, CAPTION_ENTITIES, AUDIO, DOCUMENT, PHOTO, STICKER,
        VIDEO, CONTACT, LOCATION, VENUE, ANIMATION, PINNED_MESSAGE, NEW_CHAT_MEMBERS,
        LEFT_CHAT_MEMBER, NEW_CHAT_TITLE, NEW_CHAT_PHOTO, DELETE_CHAT_PHOTO,
        GROUPCHAT_CREATED, REPLY_TO_MESSAGE, VOICE, CAPTION, SUPERGROUP_CREATED,
        CHANNEL_CHAT_CREATED, MIGRATE_TO_CHAT_ID, MIGRATE_FROM_CHAT_ID, EDIT_DATE, GAME,
        FORWARD_FROM_MESSAGE_ID, INVOICE, SUCCESSFUL_PAYMENT, VIDEO_NOTE, AUTHOR_SIGNATURE,
        FORWARD_SIGNATURE, MEDIA_GROUP_ID, CONNECTED_WEBSITE, PASSPORT_DATA, FORWARD_SENDER_NAME,
        POLL, REPLY_MARKUP, DICE, VIA_BOT, SENDER_CHAT, PROXIMITY_ALERT_TRIGGERED, MESSAGE_AUTO_DELETE_TIMER_CHANGED,
        IS_AUTOMATIC_FORWARD, HAS_PROTECTED_CONTENT, WEB_APP_DATA, VIDEO_CHAT_STARTED, VIDEO_CHAT_ENDED,
        VIDE_CHAT_PARTICIPANTS_INVITED ,VIDEO_CHAT_SCHEDULED, IS_TOPIC_MESSAGE, FORUM_TOPIC_CREATED, FORUM_TOPIC_CLOSED,
        FORUM_TOPIC_REOPENED, FORUM_TOPIC_EDITED, GENERAL_FORUM_TOPIC_HIDDEN, GENERAL_FORUM_TOPIC_UNHIDDEN,
        WRITE_ACCESS_ALLOWED, HAS_MEDIA_SPOILER, USER_SHARED, CHAT_SHARED;

//        private final String flagName;
    }

    // ~10.000 nano sec to process
    @SneakyThrows
    public List<Flag> flags() {
        // cache
        if (messageFlags != null) return messageFlags;

        if (queryType != QueryType.MESSAGE) return Collections.emptyList();
        final Message message = self.getMessage();

//        System.out.println(message.getClass().getDeclaredMethod("hasText").invoke(message));
        // it will be more time to load >>>!! 99000ns vs 10000ns todo

        List<Flag> flags = new ArrayList<>();
        if (message.hasText()) flags.add(Flag.TEXT);
        if (message.hasEntities()) flags.add(Flag.ENTITIES);
        if (message.getCaptionEntities() != null) flags.add(Flag.CAPTION_ENTITIES); // TODO
        if (message.hasAudio()) flags.add(Flag.AUDIO);
        if (message.hasDocument()) flags.add(Flag.DOCUMENT);
        if (message.hasPhoto()) flags.add(Flag.PHOTO);
        if (message.hasSticker()) flags.add(Flag.STICKER);
        if (message.hasVideo()) flags.add(Flag.VIDEO);
        if (message.hasContact()) flags.add(Flag.CONTACT);
        if (message.hasLocation()) flags.add(Flag.LOCATION);
        if (message.getVenue() != null) flags.add(Flag.VENUE); // TODO
        if (message.hasAnimation()) flags.add(Flag.ANIMATION);
        if (message.getPinnedMessage() != null) flags.add(Flag.PINNED_MESSAGE); // TODO
        if (message.getNewChatMembers() != null) flags.add(Flag.NEW_CHAT_MEMBERS);
        if (message.getLeftChatMember() != null) flags.add(Flag.LEFT_CHAT_MEMBER);
        if (message.getNewChatTitle() != null) flags.add(Flag.NEW_CHAT_TITLE);
        if (message.getNewChatPhoto() != null) flags.add(Flag.NEW_CHAT_PHOTO);
        if (message.getDeleteChatPhoto() != null) flags.add(Flag.DELETE_CHAT_PHOTO);
        if (message.getGroupchatCreated() != null) flags.add(Flag.GROUPCHAT_CREATED);
        if (message.getReplyToMessage() != null) flags.add(Flag.REPLY_TO_MESSAGE);
        if (message.hasVoice()) flags.add(Flag.VOICE);
        if (message.getCaption() != null) flags.add(Flag.CAPTION);
        if (message.getSuperGroupCreated() != null) flags.add(Flag.SUPERGROUP_CREATED);
        if (message.getChannelChatCreated() != null) flags.add(Flag.CHANNEL_CHAT_CREATED);
        if (message.getMigrateToChatId() != null) flags.add(Flag.MIGRATE_TO_CHAT_ID);
        if (message.getMigrateFromChatId() != null) flags.add(Flag.MIGRATE_FROM_CHAT_ID);
        if (message.getEditDate() != null) flags.add(Flag.EDIT_DATE);
        if (message.getGame() != null) flags.add(Flag.GAME);
        if (message.getForwardFromMessageId() != null) flags.add(Flag.FORWARD_FROM_MESSAGE_ID);
        if (message.hasInvoice()) flags.add(Flag.INVOICE);
        if (message.hasSuccessfulPayment()) flags.add(Flag.SUCCESSFUL_PAYMENT);
        if (message.hasVideoNote()) flags.add(Flag.VIDEO_NOTE);
        if (message.getAuthorSignature() != null) flags.add(Flag.AUTHOR_SIGNATURE);
        if (message.getForwardSignature() != null) flags.add(Flag.FORWARD_SIGNATURE);
        if (message.getMediaGroupId() != null) flags.add(Flag.MEDIA_GROUP_ID);
        if (message.getConnectedWebsite() != null) flags.add(Flag.CONNECTED_WEBSITE);
        if (message.hasPassportData()) flags.add(Flag.PASSPORT_DATA);
        if (message.getForwardSenderName() != null) flags.add(Flag.FORWARD_SENDER_NAME);
        if (message.hasPoll()) flags.add(Flag.POLL);
        if (message.hasReplyMarkup()) flags.add(Flag.REPLY_MARKUP);
        if (message.hasDice()) flags.add(Flag.DICE);
        if (message.hasViaBot()) flags.add(Flag.VIA_BOT);
        if (message.getSenderChat() != null) flags.add(Flag.SENDER_CHAT);
        if (message.getProximityAlertTriggered() != null) flags.add(Flag.PROXIMITY_ALERT_TRIGGERED);
        if (message.getMessageAutoDeleteTimerChanged() != null) flags.add(Flag.MESSAGE_AUTO_DELETE_TIMER_CHANGED);
        if (message.getIsAutomaticForward() != null && message.getIsAutomaticForward()) flags.add(Flag.IS_AUTOMATIC_FORWARD);
        if (message.getHasProtectedContent() != null && message.getHasProtectedContent()) flags.add(Flag.HAS_PROTECTED_CONTENT);
        if (message.getWebAppData() != null) flags.add(Flag.WEB_APP_DATA);
        if (message.getVideoChatStarted() != null) flags.add(Flag.VIDEO_CHAT_STARTED);
        if (message.getVideoChatEnded() != null) flags.add(Flag.VIDEO_CHAT_ENDED);
        if (message.getVideoChatParticipantsInvited() != null) flags.add(Flag.VIDE_CHAT_PARTICIPANTS_INVITED);
        if (message.getVideoChatScheduled() != null) flags.add(Flag.VIDEO_CHAT_SCHEDULED);
        if (message.getIsTopicMessage() != null && message.getIsTopicMessage()) flags.add(Flag.IS_TOPIC_MESSAGE);
        if (message.getForumTopicCreated() != null) flags.add(Flag.FORUM_TOPIC_CREATED);
        if (message.getForumTopicEdited() != null) flags.add(Flag.FORUM_TOPIC_EDITED);
        if (message.getForumTopicReopened() != null) flags.add(Flag.FORUM_TOPIC_REOPENED);
        if (message.getForumTopicClosed() != null) flags.add(Flag.FORUM_TOPIC_CLOSED);
        if (message.getGeneralForumTopicHidden() != null) flags.add(Flag.GENERAL_FORUM_TOPIC_HIDDEN);
        if (message.getGeneralForumTopicUnhidden() != null) flags.add(Flag.GENERAL_FORUM_TOPIC_UNHIDDEN);
        if (message.getWriteAccessAllowed() != null) flags.add(Flag.WRITE_ACCESS_ALLOWED);
        if (message.getHasMediaSpoiler() != null && message.getHasMediaSpoiler()) flags.add(Flag.HAS_MEDIA_SPOILER);
        if (message.getUserShared() != null) flags.add(Flag.USER_SHARED);
        if (message.getChatShared() != null) flags.add(Flag.CHAT_SHARED);

        messageFlags = flags;
        return messageFlags;
    }

    private static QueryType queryType(Update update) {
        if (update.hasMessage()) return QueryType.MESSAGE;
        if (update.hasCallbackQuery()) return QueryType.CALLBACK_QUERY;
        if (update.hasInlineQuery()) return QueryType.INLINE_QUERY;
        if (update.hasChosenInlineQuery()) return QueryType.CHOSEN_INLINE_QUERY;
        if (update.hasEditedMessage()) return QueryType.EDITED_MESSAGE;
        if (update.hasChannelPost()) return QueryType.CHANNEL_POST;
        if (update.hasEditedChannelPost()) return QueryType.EDITED_CHANNEL_POST;
        if (update.hasShippingQuery()) return QueryType.SHIPPING_QUERY;
        if (update.hasPreCheckoutQuery()) return QueryType.PRE_CHECKOUT_QUERY;
        if (update.hasPoll()) return QueryType.POLL;
        if (update.hasPollAnswer()) return QueryType.POLL_ANSWER;
        if (update.hasChatJoinRequest()) return QueryType.CHAT_JOIN_REQUEST;
        if (update.hasMyChatMember()) return QueryType.CHAT_MEMBER_UPDATED_MY;
        if (update.hasChatMember()) return QueryType.CHAT_MEMBER_UPDATED;

        return QueryType.UNKNOWN;
    }

    public int getMessageId() {
        return switch (queryType) {
            case MESSAGE -> self.getMessage().getMessageId();
            case CALLBACK_QUERY -> Integer.parseInt(self.getCallbackQuery().getInlineMessageId());
            case CHOSEN_INLINE_QUERY -> Integer.parseInt(self.getChosenInlineQuery().getInlineMessageId());
            case EDITED_MESSAGE -> self.getEditedMessage().getMessageId();
            case CHANNEL_POST -> self.getChannelPost().getMessageId();
            case EDITED_CHANNEL_POST -> self.getEditedChannelPost().getMessageId();
            default -> -1;
        };
    }

    public long getChatId() {
        return switch (queryType) {
            case MESSAGE -> self.getMessage().getChatId();
            case CALLBACK_QUERY -> self.getCallbackQuery().getMessage().getChatId();
            case EDITED_MESSAGE -> self.getEditedMessage().getChatId();
            case CHANNEL_POST -> self.getChannelPost().getChatId();
            case EDITED_CHANNEL_POST -> self.getEditedMessage().getChatId();
            case CHAT_JOIN_REQUEST -> self.getChatJoinRequest().getUserChatId();
            case CHAT_MEMBER_UPDATED_MY -> self.getMyChatMember().getChat().getId();
            case CHAT_MEMBER_UPDATED -> self.getChatMember().getChat().getId();
            default -> -1;
        };
    }

    public String getUsername() {

        return switch (queryType) {
            case MESSAGE -> self.getMessage().getChat().getUserName();
            case CALLBACK_QUERY -> self.getCallbackQuery().getFrom().getUserName();
            case INLINE_QUERY -> self.getInlineQuery().getFrom().getUserName();
            case CHOSEN_INLINE_QUERY -> self.getChosenInlineQuery().getFrom().getUserName();
            case EDITED_MESSAGE -> self.getEditedMessage().getFrom().getUserName();
            case CHANNEL_POST -> self.getChannelPost().getFrom().getUserName();
            case EDITED_CHANNEL_POST -> self.getEditedChannelPost().getFrom().getUserName();
            case SHIPPING_QUERY -> self.getShippingQuery().getFrom().getUserName();
            case PRE_CHECKOUT_QUERY -> self.getPreCheckoutQuery().getFrom().getUserName();
            case POLL_ANSWER -> self.getPollAnswer().getUser().getUserName();
            case CHAT_JOIN_REQUEST -> self.getChatJoinRequest().getUser().getUserName();
            case CHAT_MEMBER_UPDATED_MY -> self.getMyChatMember().getFrom().getUserName();
            case CHAT_MEMBER_UPDATED -> self.getChatMember().getFrom().getUserName();
            default -> null;
        };
    }

    public String getText() {
        // stuff for webapp
        if (self.getMessage() != null && self.getMessage().getWebAppData() != null) return self.getMessage().getWebAppData().getData();

        return switch (queryType) {
            case MESSAGE -> self.getMessage().hasText() ? self.getMessage().getText() : null;
            case CALLBACK_QUERY -> self.getCallbackQuery().getData();
            case INLINE_QUERY -> self.getInlineQuery().getQuery();
            case CHOSEN_INLINE_QUERY -> self.getChosenInlineQuery().getQuery();
            case EDITED_MESSAGE -> self.getEditedMessage().getText();
            case CHANNEL_POST -> self.getChannelPost().getText();
            case EDITED_CHANNEL_POST -> self.getEditedChannelPost().getText();
            default -> null;
        };
    }
}