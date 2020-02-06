package com.example.gerenciamentodesalas.service;

import android.content.Context;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class FileWritterService {
    public String lerArquivo (Context context, String nomeArquivo) {
        try {
            FileInputStream fis = context.openFileInput(nomeArquivo);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader bufferedReader = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }
            return sb.toString();
        } catch (FileNotFoundException fileNotFound) {
            return null;
        } catch (IOException ioException) {
            return null;
        }
    }

    public boolean criarArquivo (Context context, String nomeArquivo, String jsonString){
        try {
            FileOutputStream fos = context.openFileOutput(nomeArquivo,Context.MODE_PRIVATE);
            if (jsonString != null) {
                fos.write(jsonString.getBytes());
            }
            fos.close();
            return true;
        } catch (FileNotFoundException fileNotFound) {
            return false;
        } catch (IOException ioException) {
            return false;
        }

    }

    public boolean arquivoExiste(Context context, String nomeArquivo) {
        String path = context.getFilesDir().getAbsolutePath() + "/" + nomeArquivo;
        File file = new File(path);
        return file.exists();
    }

    public boolean arquivoDeletado(Context context, String nomeArquivo) {
        String path = context.getFilesDir().getAbsolutePath() + "/" + nomeArquivo;
        File file = new File(path);
        file.delete();
        return !file.exists();
    }

}
