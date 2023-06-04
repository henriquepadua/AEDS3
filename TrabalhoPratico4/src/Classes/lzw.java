package Classes;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.*;

public class lzw {
    public static String leJogador(int tamanhoArquivo, int id, boolean lapide) throws Exception{
        Jogador jogador = new Jogador();
        String s = "";
        Date date = jogador.getJoinedOn();
        int tamanhoString = 0;double value = 0;byte overall = 0;

        s = id + "";
        jogador.setLapide(lapide);
        jogador.setId(id);
        tamanhoString = Arquivo.fileReader.readInt() ;
        s += tamanhoString + "";
        jogador.setKnownAs(s += Arquivo.fileReader.readUTF());
        tamanhoString = Arquivo.fileReader.readInt();
        s += tamanhoString + "";
        //System.out.print(s);
        jogador.setFullName(s += Arquivo.fileReader.readUTF());
        //System.out.print(","+s);
        jogador.setOverall( overall = Arquivo.fileReader.readByte());
        s += overall;
        //System.out.print(","+overall);
        jogador.setValue(value = Arquivo.fileReader.readDouble());
        s += value + "";
        //System.out.print(","+ value);
        tamanhoString = Arquivo.fileReader.readInt();
        s += tamanhoString + "";
        jogador.setBestPosition(s += Arquivo.fileReader.readUTF());
        //System.out.print(","+s);
        tamanhoString = Arquivo.fileReader.readInt();
        s += tamanhoString + "";
        jogador.setNacionality(s += Arquivo.fileReader.readUTF());
        //System.out.print(","+s);
        jogador.setAge(overall = Arquivo.fileReader.readByte());
        s += overall;
        //System.out.println(","+overall);
        tamanhoString = Arquivo.fileReader.readInt();
        s += tamanhoString + "";
        jogador.setClubName(s += Arquivo.fileReader.readUTF());
        //System.out.print(","+s);
        tamanhoString = Arquivo.fileReader.readInt();
        s += tamanhoString + "";
        jogador.setJoinedOn(s += Arquivo.fileReader.readUTF());
        //System.out.println("Jogador#" + id +":" +s);

        return s;
    }

    public static String pesquisa(int id) throws IOException{
        Arquivo.fileReader.seek(0);
        Arquivo.fileReader.readInt();
        int tamanhoJogador,idJogador;
        boolean lapide;
        String s = "";

        try{
            while(Arquivo.fileReader.getFilePointer() < Arquivo.fileReader.length()){
                long posicao = Arquivo.fileReader.getFilePointer();
                tamanhoJogador = Arquivo.fileReader.readInt();
                lapide = Arquivo.fileReader.readBoolean();
                
                if(lapide){
                    idJogador = Arquivo.fileReader.readInt();
                    if(idJogador == id){
                       s += leJogador(tamanhoJogador, id, lapide);
                        break;
                    }else {
                        Arquivo.fileReader.skipBytes(tamanhoJogador - 5);
                    }
                } else{
                    Arquivo.fileReader.skipBytes(tamanhoJogador - 1);
                }
            }
        } catch(Exception e){
            System.err.println("Id nao encontrado");
        }

        return s;
    }

    public static List<Integer> comprimindoLZW(RandomAccessFile arq) throws IOException {
        Arquivo.fileReader.seek(0);
        int ultimoId = Arquivo.fileReader.readInt(),controle = 1;
        Arquivo.fileReader.seek(0);
        String saida = "";

        while(controle <= ultimoId){//pega os dados de todos os jogadores do arquivo.db
            saida += pesquisa(controle);
            controle++;
        }

        int tamanhoDicionario = 256;
        Map<String,Integer> dicionario = new HashMap<String,Integer>();
        for (int i = 0; i < 256; i++)
            dicionario.put("" + (char)i, i);
        
        String w = "";
        List<Integer> resultado = new ArrayList<Integer>();
        for (char c : saida.toCharArray()) {//enquanto o arquivo original transformado em array de char não chegar ao fim adiciona a uma string se esse elemento possui dentro do dicionario
            String wc = w + c;
            if (dicionario.containsKey(wc))// se o caracter atual tiver dentro do dicionário w recebe o caractere repetido
                w = wc;
            else {// se não tiver adiciona o caractere adiciona o valor do caractere dentro do resultado que é uma lista de inteiros
                resultado.add(dicionario.get(w));
                
                dicionario.put(wc, tamanhoDicionario++);//tira o valor com a chave wc e adiciona um ao tamanho do dicionario
                w = "" + c;//set w como uma string vazia mais o caractere para não dar conflito
            }
        }
 
        // Saída do codigo para w
        if (!w.equals(""))
            resultado.add(dicionario.get(w));

            arq.seek(0);
            arq.writeUTF(resultado.toString().trim());
            //System.out.println(resultado.toString().trim());
            System.out.println("\nArquivo inicial: " + saida.length() + " bytes");
            System.out.println("Arquivo final: " + resultado.toString().length() + " bytes");
            
            System.out.println("Compressão: " + (((float) arq.length() / arq.length()) * 100) + "% do tamanho");
            System.out.println("\nAperte enter para continuar.");

        
            return resultado;
    }
    
    /** 
     * Descompressão do arquivo db
    */
    public static String decomprimindoLZW(List<Integer> comprimido,RandomAccessFile arq) throws IOException {
        // Build the dicionario.
        int tamanhoDicionario = 256;
        Map<Integer,String> dicionario = new HashMap<Integer,String>();//Cria o dicionário com posições inteiras e com elementos string
        for (int i = 0; i < 256; i++)
            dicionario.put(i, "" + (char)i);
        
        String w = "" + (char)(int)comprimido.remove(0);//remove o elemento na posição zero primeiro faz o cast de inteiro pois foi como ele foi comprimido depois pega char para concatenar com a string
        StringBuffer resultado = new StringBuffer(w);
        for (int k : comprimido) {//enquanto a lista não chegar ao seu fiz pega o valor da posição atual e retorna o elemento
            String entry;
            if (dicionario.containsKey(k))// se o elemento na posição k tiver dentro do dicionario 
                entry = dicionario.get(k);
            else if (k == tamanhoDicionario)
                entry = w + w.charAt(0);
            else
                throw new IllegalArgumentException("Falha na compressão do k: " + k);
            
            resultado.append(entry);
            
            // Adiciona o valor de 2 + entry[0] no dicionário
            dicionario.put(tamanhoDicionario++, w + entry.charAt(0));
            
            w = entry;
        }
        //System.out.println(resultado.toString());
        arq.seek(0);
        arq.writeUTF(resultado.toString());
        System.out.println("\nArquivo inicial: " + resultado.toString().length() + " bytes");
            System.out.println("Arquivo final: " + arq.length() + " bytes");
            System.out.println("Compressão: " + (((float) arq.length() / arq.length()) * 100) + "% do tamanho");
            System.out.println("\nAperte enter para continuar.");
        return resultado.toString();
    }
}