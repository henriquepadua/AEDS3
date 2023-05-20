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
        System.out.print(s);
        jogador.setFullName(s += Arquivo.fileReader.readUTF());
        System.out.print(","+s);
        jogador.setOverall( overall = Arquivo.fileReader.readByte());
        s += overall;
        System.out.print(","+overall);
        jogador.setValue(value = Arquivo.fileReader.readDouble());
        s += value + "";
        System.out.print(","+ value);
        tamanhoString = Arquivo.fileReader.readInt();
        s += tamanhoString + "";
        jogador.setBestPosition(s += Arquivo.fileReader.readUTF());
        System.out.print(","+s);
        tamanhoString = Arquivo.fileReader.readInt();
        s += tamanhoString + "";
        jogador.setNacionality(s += Arquivo.fileReader.readUTF());
        System.out.print(","+s);
        jogador.setAge(overall = Arquivo.fileReader.readByte());
        s += overall;
        System.out.println(","+overall);
        tamanhoString = Arquivo.fileReader.readInt();
        s += tamanhoString + "";
        jogador.setClubName(s += Arquivo.fileReader.readUTF());
        System.out.print(","+s);
        tamanhoString = Arquivo.fileReader.readInt();
        s += tamanhoString + "";
        jogador.setJoinedOn(s += Arquivo.fileReader.readUTF());
        System.out.println("Jogador#" + id +":" +s);


        return s;
    }

    public static String pesquisa(int id) throws IOException{
        Arquivo.fileReader.seek(0);
        Arquivo.fileReader.readInt();
        int tamanhoJogador,idJogador;
        boolean lapide;
        String s = "";
        //int idJogador;

        try{
            while(Arquivo.fileReader.getFilePointer() < Arquivo.fileReader.length()){
                long posicao = Arquivo.fileReader.getFilePointer();
                tamanhoJogador = Arquivo.fileReader.readInt();
                lapide = Arquivo.fileReader.readBoolean();
                
                if(lapide){
                   // Arquivo.fileReader.readInt();
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

            /*if(jogador.getFullName() == ""){ System.out.println("Id NÃO EXISTE");
                return null;
            }*/
            

        } catch(Exception e){
            System.err.println("Id nao encontrado");
        }

        return s;
    }

    public static List<Integer> compress(RandomAccessFile arq,String uncompressed) throws IOException {
        // Build the dictionary.
        Arquivo.fileReader.seek(0);
        int ultimoId = Arquivo.fileReader.readInt(),controle = 1;
        Arquivo.fileReader.seek(0);
        String saida = "";

        while(controle <= ultimoId){
            saida += pesquisa(controle);
            controle++;
        }

        int dictSize = 256;
        Map<String,Integer> dictionary = new HashMap<String,Integer>();
        for (int i = 0; i < 256; i++)
            dictionary.put("" + (char)i, i);
        
        String w = "";
        List<Integer> result = new ArrayList<Integer>();
        for (char c : saida.toCharArray()) {
            String wc = w + c;
            if (dictionary.containsKey(wc))
                w = wc;
            else {
                result.add(dictionary.get(w));
                // Add wc to the dictionary.
                dictionary.put(wc, dictSize++);
                w = "" + c;
            }
        }
 
        // Output the code for w.
        if (!w.equals(""))
            result.add(dictionary.get(w));

            
            System.out.println("\nArquivo inicial: " + Arquivo.fileReader.length() + " bytes");
            System.out.println("Arquivo final: " + arq.length() + " bytes");
            System.out.println("Compressão: " + (((float) arq.length() / arq.length()) * 100) + "% do tamanho");
            System.out.println("\nAperte enter para continuar.");
            //--sc.nextLine();
            //arqComprimido.close();

            //arq.writeUTF(result.toString().trim());
            System.out.println(result.toString());
        return result;
    }
    
    /** Decompress a list of output ks to a string. 
     * @throws IOException*/
    public static String decompress(List<Integer> compressed,RandomAccessFile arq) throws IOException {
        // Build the dictionary.
        int dictSize = 256;
        Map<Integer,String> dictionary = new HashMap<Integer,String>();
        for (int i = 0; i < 256; i++)
            dictionary.put(i, "" + (char)i);
        
        String w = "" + (char)(int)compressed.remove(0);
        StringBuffer result = new StringBuffer(w);
        for (int k : compressed) {
            String entry;
            if (dictionary.containsKey(k))
                entry = dictionary.get(k);
            else if (k == dictSize)
                entry = w + w.charAt(0);
            else
                throw new IllegalArgumentException("Bad compressed k: " + k);
            
            result.append(entry);
            
            // Add w+entry[0] to the dictionary.
            dictionary.put(dictSize++, w + entry.charAt(0));
            
            w = entry;
        }
        System.out.println(result.toString());
        arq.writeUTF(result.toString());
        return result.toString();
    }
}