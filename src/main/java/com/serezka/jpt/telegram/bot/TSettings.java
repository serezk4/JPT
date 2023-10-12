package com.serezka.jpt.telegram.bot;

import java.util.List;

public class TSettings {
    public static final List<TUpdate.QueryType> availableQueryTypes = List.of(TUpdate.QueryType.MESSAGE, TUpdate.QueryType.CALLBACK_QUERY, TUpdate.QueryType.INLINE_QUERY);
}
