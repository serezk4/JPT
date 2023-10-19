package com.serezka.jpt.telegram.utils.methods.v2;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.telegram.telegrambots.meta.api.methods.ParseMode;

@AllArgsConstructor
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum Parse {
    HTML(ParseMode.HTML), MARKDOWN(ParseMode.MARKDOWN), MARKDOWNV2(ParseMode.MARKDOWNV2);

    String name;
}
