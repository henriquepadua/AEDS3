package Classes;

import java.io.RandomAccessFile;
import java.util.ArrayList;

import java.io.*;

public class ListaInvertida {
    private RandomAccessFile arq;
    private final String arqListaInvertidaNome = "src/Dados/ListaInvertidaNome.db";
    private final String arqListaInvertidaCidade = "src/Dados/ListaInvertidaCidade.db";

    public ListaInvertida(){
        try{
            boolean exists_name = (new File(arqListaInvertidaNome)).exists();
            boolean exists_city = (new File(arqListaInvertidaCidade)).exists();

            if(exists_name == true && exists_city == true){
                // Arquivo Existe
            }else{
                try{
                    arq = new RandomAccessFile(arqListaInvertidaNome,"rw");
                    arq = new RandomAccessFile(arqListaInvertidaCidade, "rw");
                    arq.close();
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    /*
     * Contar a quantidade de palavras que um nome tem
     * @param recebe o nome que deve ser pesquisado a quantidade de palavras
     * @return retorna a quantidade de palavras
     */
    public int contarNumeroPalavras(String nome){
        int qnt_palavras = 0;

        for(int i = 0;i < nome.length(); i++){
            if(nome.charAt(i) == ' '){
                qnt_palavras++;
            }
        }
        return qnt_palavras;
    }

    /*
     * Procura no arquivo da listra Invertida se tem a palavra
     * @param palavra -> recebe uma palavras para ser pesquisada no arquivo
     * @param arquivo -> recebe o nome do arquivo que deve ser pesquisado
     * @return true se achara a palavra e false se nao achar a palavra
     */
    public boolean contemPalavra(String palavra,String arquivo){
        try{
            arq = new RandomAccessFile(arquivo, "rw");

            String palavra_arq;

            arq.seek(0);
            while(arq.getFilePointer() < arq.length()){
                palavra_arq = arq.readUTF();
                arq.readByte();
                arq.readByte();
                arq.readByte();
                arq.readByte();
                arq.readByte();
                arq.readLong();
                if(palavra.compareTo(palavra_arq) == 0){
                    return true;
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
            return false;
    }

}
