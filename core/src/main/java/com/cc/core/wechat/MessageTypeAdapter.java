package com.cc.core.wechat;

import com.cc.core.wechat.model.message.ImageMessage;
import com.cc.core.wechat.model.message.TextMessage;
import com.cc.core.wechat.model.message.UnsupportMessage;
import com.cc.core.wechat.model.message.VideoMessage;
import com.cc.core.wechat.model.message.WeChatMessage;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

public class MessageTypeAdapter implements JsonDeserializer<WeChatMessage>,
    JsonSerializer<WeChatMessage> {

    @Override
    public JsonElement serialize(WeChatMessage object, Type interfaceType,
        JsonSerializationContext context) {
        return context.serialize(object);
    }

    @Override
    public WeChatMessage deserialize(JsonElement elem, Type interfaceType,
        JsonDeserializationContext context) throws JsonParseException {
        JsonObject wrapper = elem.getAsJsonObject();

        final JsonElement messageTypeElem = get(wrapper, "type");
        int messageType = messageTypeElem.getAsInt();

        final Type actualType = getMessageActualType(messageType);
        return context.deserialize(elem, actualType);
    }

    private JsonElement get(final JsonObject wrapper, String memberName) {
        final JsonElement elem = wrapper.get(memberName);
        if (elem == null) {
            throw new JsonParseException(
                "no '"
                    + memberName
                    + "' member found in what was expected to be an interface wrapper");
        }
        return elem;
    }

    private Type getMessageActualType(int type) {
        switch (type) {
            case WeChatMessageType.IMAGE:
            case WeChatMessageType.EMOJI:
                return ImageMessage.class;
            case WeChatMessageType.VIDEO:
                return VideoMessage.class;
            case WeChatMessageType.TEXT:
                return TextMessage.class;
            default:
                return UnsupportMessage.class;
        }
    }
}
