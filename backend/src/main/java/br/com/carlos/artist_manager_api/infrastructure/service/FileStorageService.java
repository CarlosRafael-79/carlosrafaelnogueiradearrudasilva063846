package br.com.carlos.artist_manager_api.infrastructure.service;

import java.io.InputStream;

public interface FileStorageService {

    String upload(String nomeArquivo, InputStream conteudo, String contentType);


    String gerarUrlAssinada(String nomeArquivo);

    void deletar(String nomeArquivo);
}