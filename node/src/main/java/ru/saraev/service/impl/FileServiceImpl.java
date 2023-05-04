package ru.saraev.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import lombok.extern.log4j.Log4j;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.saraev.dao.AppDocumentDAO;
import ru.saraev.dao.BinaryContentDAO;
import ru.saraev.entity.enums.AppDocument;
import ru.saraev.entity.enums.BinaryContent;
import ru.saraev.service.FileService;
import ru.saraev.exceptions.UploadFileException;

@Log4j
@Service
public class FileServiceImpl implements FileService {
    @Value("${token}")
    private String token;
    @Value("${service.file_info.uri}")
    private String fileInfoUri;
    @Value("${service.file_storage.uri}")
    private String fileStorageUri;
    private final AppDocumentDAO appDocumentDAO;
    private final BinaryContentDAO binaryContentDAO;

    public FileServiceImpl(AppDocumentDAO appDocumentDAO, BinaryContentDAO binaryContentDAO) {
        this.appDocumentDAO = appDocumentDAO;
        this.binaryContentDAO = binaryContentDAO;
    }

    @Override
    public AppDocument processDoc(Message telegramMessage) {
        String fieldId = telegramMessage.getDocument().getFileId();
        ResponseEntity<String> response = getFilePath(fieldId);
        if (response.getStatusCode() == HttpStatus.OK) {
            JSONObject jsonObject = new JSONObject(response.getBody());
            String filePath = String.valueOf(jsonObject
                .getJSONObject("result")
                .getString("file_path"));
            byte[] fileInByte = downloadFile(filePath);
            BinaryContent transientBinaryContent = BinaryContent.builder()
                .fileAsArrayOfBytes(fileInByte)
                .build();
            BinaryContent persistentBinaryContent = binaryContentDAO.save(transientBinaryContent);
            Document telegramDoc = telegramMessage.getDocument();
            AppDocument transientAppDocument = buildTransientAppDoc(telegramDoc, persistentBinaryContent);
            return appDocumentDAO.save(transientAppDocument);
        } else {
            throw new UploadFileException("Bad response from telegram service: " + response);
        }
    }

    private AppDocument buildTransientAppDoc(Document telegramDoc, BinaryContent persistentBinaryContent) {
        return AppDocument.builder()
            .telegramFieldId(telegramDoc.getFileId())
            .docName(telegramDoc.getFileName())
            .binaryContent(persistentBinaryContent)
            .mimeType(telegramDoc.getMimeType())
            .fileSize(telegramDoc.getFileSize())
            .build();
    }

    private byte[] downloadFile(String filePath) {
        String fullUri = fileStorageUri.replace("{token}", token)
            .replace("{filePath}", filePath);
        URL urlObj = null;
        try {
            urlObj = new URL(fullUri);
        } catch (MalformedURLException e) {
            throw new UploadFileException(e);
        }

        //TODO подумать над оптимизацией
        try (InputStream is = urlObj.openStream()) {
            return is.readAllBytes();
        } catch (IOException e) {
            throw new UploadFileException(urlObj.toExternalForm(), e);
        }
    }

    private ResponseEntity<String> getFilePath(String fieldId) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> request = new HttpEntity<>(headers);
        return restTemplate.exchange(
            fileInfoUri,
            HttpMethod.GET,
            request,
            String.class,
            token,
            fieldId
        );
    }
}
