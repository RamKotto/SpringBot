package ru.saraev.service;

import org.telegram.telegrambots.meta.api.objects.Message;
import ru.saraev.entity.enums.AppDocument;

public interface FileService {
    AppDocument processDoc(Message externalMessage);
}
