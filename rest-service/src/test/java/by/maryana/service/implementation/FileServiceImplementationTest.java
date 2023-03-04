package by.maryana.service.implementation;

import by.maryana.dao.AppDocumentDAO;
import by.maryana.dao.AppPhotoDAO;
import by.maryana.entity.AppDocument;
import by.maryana.entity.AppPhoto;
import by.maryana.utils.CryptoTool;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class FileServiceImplementationTest {

    private  AppPhotoDAO appPhotoDAO;
    private  AppDocumentDAO appDocumentDAO;

    private final Optional<AppPhoto> appPhoto = Optional.of(new AppPhoto());
    private final Optional<AppDocument> appDocument = Optional.of(new AppDocument());

    private  final String salt = "salt";
    private  final CryptoTool cryptoTool = new CryptoTool(salt);
    private  final Long id = 1L;
    private final Long notId = 2L;

    @BeforeEach
     void init(){
        appPhotoDAO = mock(AppPhotoDAO.class);
        appDocumentDAO = mock(AppDocumentDAO.class);
        when(appPhotoDAO.findById(id)).thenReturn(appPhoto);
        when(appDocumentDAO.findById(id)).thenReturn(appDocument);
    }

    @Test
    void shouldReturnProperDocument(){
        String hash = cryptoTool.encode(id);

        FileServiceImpl fileService = new FileServiceImpl(appDocumentDAO, appPhotoDAO, cryptoTool);
        AppDocument documentFromService = fileService.getDocument(hash);

        verify(appDocumentDAO).findById(id);
        assertEquals(documentFromService, appDocument.get());
    }

    @Test
    void shouldReturnNullIfNotExistsDocumentWithSuchId(){
        String hash = cryptoTool.encode(notId);

        FileServiceImpl fileService = new FileServiceImpl(appDocumentDAO, appPhotoDAO, cryptoTool);
        AppDocument documentFromService = fileService.getDocument(hash);

        assertNull(documentFromService);
    }


    @Test
    void shouldReturnProperPhoto(){
        String hash = cryptoTool.encode(id);

        FileServiceImpl fileService = new FileServiceImpl(appDocumentDAO, appPhotoDAO, cryptoTool);
        AppPhoto photoFromService = fileService.getPhoto(hash);

        verify(appPhotoDAO).findById(id);
        assertEquals(photoFromService, appPhoto.get());
    }

    @Test
    void shouldReturnNullIfNotExistsPhotoWithSuchId(){
        String hash = cryptoTool.encode(notId);

        FileServiceImpl fileService = new FileServiceImpl(appDocumentDAO, appPhotoDAO, cryptoTool);
        AppPhoto photoFromService = fileService.getPhoto(hash);


        assertNull(photoFromService);
    }
}
