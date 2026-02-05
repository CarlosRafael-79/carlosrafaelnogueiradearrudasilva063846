package br.com.carlos.artist_manager_api.infrastructure.service;

import io.minio.*;
import io.minio.http.Method;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class MinioStorageService implements FileStorageService {

    private final MinioClient minioClient;

    @Value("${minio.bucket-name:capas-albuns}")
    private String bucketName;

    @Override
    public String upload(String nomeArquivo, InputStream conteudo, String contentType) {
        try {

            boolean found = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
            if (!found) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
            }

            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(nomeArquivo)
                            .stream(conteudo, -1, 10485760)
                            .contentType(contentType)
                            .build()
            );

            log.info("Arquivo enviado com sucesso: {}", nomeArquivo);
            return nomeArquivo;

        } catch (Exception e) {
            log.error("Erro ao enviar arquivo para o MinIO", e);
            throw new RuntimeException("Erro no upload de arquivo", e);
        }
    }

    @Override
    public String gerarUrlAssinada(String nomeArquivo) {
        try {
            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET)
                            .bucket(bucketName)
                            .object(nomeArquivo)
                            .expiry(30, TimeUnit.MINUTES)
                            .build()
            );
        } catch (Exception e) {
            log.error("Erro ao gerar URL assinada", e);
            return null;
        }
    }

    @Override
    public void deletar(String nomeArquivo) {
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder().bucket(bucketName).object(nomeArquivo).build()
            );
        } catch (Exception e) {
            log.error("Erro ao deletar arquivo", e);
        }
    }
}