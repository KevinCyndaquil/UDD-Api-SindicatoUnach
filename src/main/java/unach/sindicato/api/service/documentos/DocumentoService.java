package unach.sindicato.api.service.documentos;

import com.mongodb.DuplicateKeyException;
import com.mongodb.MongoWriteException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import unach.sindicato.api.persistence.documentos.Documento;
import unach.sindicato.api.persistence.documentos.Pdf;
import unach.sindicato.api.repository.DocumentoRepository;
import unach.sindicato.api.repository.UddRepository;
import unach.sindicato.api.service.persistence.PersistenceService;
import unach.sindicato.api.service.auth.EncryptorService;
import unach.sindicato.api.utils.errors.BusquedaSinResultadoException;
import unach.sindicato.api.utils.errors.CollectionNoActualizadaException;
import unach.sindicato.api.utils.errors.ErrorEncriptacionException;
import unach.sindicato.api.utils.errors.NombrableRepetidoException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DocumentoService implements PersistenceService<Documento> {
    final DocumentoRepository repository;

    final EncryptorService encryptorService;

    @Override
    public @NonNull UddRepository<Documento> repository() {
        return repository;
    }

    @Override
    public @NonNull Class<Documento> clazz() {
        return Documento.class;
    }

    @Override
    public Documento save(@NonNull Documento documento) throws NombrableRepetidoException, DuplicateKeyException, MongoWriteException {
        if (documento instanceof Pdf pdf) encrypt(pdf);
        return PersistenceService.super.save(documento);
    }

    @Override
    public Documento findById(ObjectId id) throws BusquedaSinResultadoException {
        Documento documento = PersistenceService.super.findById(id);
        if (documento instanceof Pdf pdf) decrypt(pdf);
        return documento;
    }

    @Override
    public List<Documento> findAll() {
        Object[] matches = Arrays.stream(new Class<?>[] {
                        Documento.class,
                        Pdf.class,})
                .map(c -> new Document("_class", c.getName()))
                .toArray();

        return repository.findAll(matches)
                .stream()
                .peek(d -> {
                    if (d instanceof Pdf pdf) decrypt(pdf);
                })
                .toList();
    }

    @Override
    public boolean update(@NonNull Documento documento) {
        if (documento instanceof Pdf pdf) encrypt(pdf);
        return PersistenceService.super.update(documento);
    }

    @Transactional
    public Documento saveOrUpdate(@NonNull Pdf pdf) {
        System.out.println("ddddddddddddddddddddddddddddd " + pdf.getId());
        System.out.println(pdf.getReporte().getMotivo());

        if (pdf.getId() != null)
            if (repository.existsById(pdf.getId())) {
                System.out.println("HOllaaaaaaaa");
                if (!update(pdf))
                    throw new CollectionNoActualizadaException(pdf, getClass());
                else return findById(pdf.getId());
            }
        return repository.save(pdf);
    }

    protected void encrypt(@NonNull Pdf pdf) throws ErrorEncriptacionException {
        if (pdf.getBytes() == null) return;

        try {
            encryptorService.encrypt(pdf);
        } catch (NoSuchPaddingException | BadPaddingException | IllegalBlockSizeException | InvalidKeyException |
                 InvalidAlgorithmParameterException | NoSuchAlgorithmException e) {
            throw new ErrorEncriptacionException(pdf);
        }
    }

    protected void decrypt(@NonNull Pdf pdf) throws ErrorEncriptacionException {
        if (pdf.getBytes() == null) return;

        try {
            encryptorService.decrypt(pdf);
        } catch (NoSuchPaddingException | BadPaddingException | IllegalBlockSizeException | InvalidKeyException | InvalidAlgorithmParameterException | NoSuchAlgorithmException e) {
            throw new ErrorEncriptacionException(pdf);
        }
    }
}
