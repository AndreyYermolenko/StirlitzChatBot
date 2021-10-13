package ru.yermolenko.service.impl;

import lombok.extern.log4j.Log4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.yermolenko.dao.DocumentDAO;
import ru.yermolenko.dao.PhotoDAO;
import ru.yermolenko.model.Document;
import ru.yermolenko.model.LinkType;
import ru.yermolenko.model.Photo;
import ru.yermolenko.service.FileService;
import ru.yermolenko.tools.CryptoTool;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;


@Service
@Log4j
public class FileServiceImpl implements FileService {
    @Value("${token}")
    private String token;
    @Value("${service.file_info.url}")
    private String fileInfoUrl;
    @Value("${service.file_storage.url}")
    private String fileStorageUrl;
    @Value("${link.address}")
    private String linkAddress;
    @Value("${server.port}")
    private String serverPort;
    private final DocumentDAO documentDAO;
    private final PhotoDAO photoDAO;
    private final CryptoTool cryptoTool;

    public FileServiceImpl(DocumentDAO documentDAO, PhotoDAO photoDAO, CryptoTool cryptoTool) {
        this.documentDAO = documentDAO;
        this.photoDAO = photoDAO;
        this.cryptoTool = cryptoTool;
    }

    @Override
    public Document processDoc(Message externalMessage) {
        String fileId = externalMessage.getDocument().getFileId();
        org.telegram.telegrambots.meta.api.objects.Document externalDoc = externalMessage.getDocument();
        ResponseEntity<String> response = getFilePath(fileId);
        if (response.getStatusCode() == HttpStatus.OK) {
            JSONObject jsonObject = new JSONObject(response.getBody());
            log.debug(jsonObject);
            String filePath = String.valueOf(jsonObject
                    .getJSONObject("result")
                    .getString("file_path"));
            Integer contentLength = externalMessage.getDocument().getFileSize();
            byte[] fileInByte = downloadFile(filePath, contentLength);
            Document transientDoc = Document.builder()
                    .externalServiceFileId(externalDoc.getFileId())
                    .docName(externalDoc.getFileName())
                    .fileAsArrayOfBytes(fileInByte)
                    .mimeType(externalDoc.getMimeType())
                    .fileSize(contentLength)
                    .build();
            return documentDAO.save(transientDoc);
        } else {
            return null;
        }
    }

    @Override
    public Photo processPhoto(Message externalMessage) {
        String fileId = externalMessage.getPhoto().get(0).getFileId();
        org.telegram.telegrambots.meta.api.objects.PhotoSize externalPhoto = externalMessage.getPhoto().get(0);
        ResponseEntity<String> response = getFilePath(fileId);
        if (response.getStatusCode() == HttpStatus.OK) {
            JSONObject jsonObject = new JSONObject(response.getBody());
            log.debug(jsonObject);
            String filePath = String.valueOf(jsonObject
                    .getJSONObject("result")
                    .getString("file_path"));
            Integer contentLength = externalMessage.getPhoto().get(0).getFileSize();
            byte[] fileInByte = downloadFile(filePath, contentLength);
            Photo transientPhoto = Photo.builder()
                    .externalServiceFileId(externalPhoto.getFileId())
                    .fileAsArrayOfBytes(fileInByte)
                    .fileSize(contentLength)
                    .build();
            return photoDAO.save(transientPhoto);
        } else {
            return null;
        }
    }

    private ResponseEntity<String> getFilePath(String fileId) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        HttpEntity request = new HttpEntity(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                fileInfoUrl,
                HttpMethod.GET,
                request,
                String.class,
                token, fileId
        );
        return response;
    }

    private byte[] downloadFile(String filePath, Integer contentLength) {
        String fullUrl = fileStorageUrl.replace("{token}", token)
                .replace("{filePath}", filePath);
        URL urlObj = null;
        try {
            urlObj = new URL(fullUrl);
        } catch (MalformedURLException e) {
            log.error(e);
        }
        byte[] byteChunk = new byte[contentLength];

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (InputStream is = urlObj.openStream()) {
            int n;
            while ((n = is.read(byteChunk)) > 0) {
                baos.write(byteChunk, 0, n);
            }
        } catch (IOException e) {
            log.error(String.format("Failed while reading bytes from %s: %s",
                    urlObj.toExternalForm(), e.getMessage()));
            log.error(e);
        }
        return byteChunk;
    }

    @Override
    public Document getDocument(String id) {
        Long numberId = cryptoTool.idOf(id);
        if (numberId == null) {
            return null;
        }
        return documentDAO.findById(numberId).orElse(null);
    }

    @Override
    public Photo getPhoto(String id) {
        Long numberId = cryptoTool.idOf(id);
        if (numberId == null) {
            return null;
        }
        return photoDAO.findById(numberId).orElse(null);
    }

    public String generateLink(Long persistenceDocId, LinkType linkType) {
        String hash = cryptoTool.hashOf(persistenceDocId);
        return "http://" + linkAddress + ":" + serverPort + "/"
                + linkType + "?id=" + hash;
    }
}
