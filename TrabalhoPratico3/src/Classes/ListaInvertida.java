package Classes;

import java.io.RandomAccessFile;
import java.util.ArrayList;

import java.io.*;

public class ListaInvertida {
    private RandomAccessFile arq;
    private final String arqListaInvertidaNome = "src/Dados/ListaInvertidaKnownAs.db";
    private final String arqListaInvertidaCidade = "src/Dados/ListaInvertidaNationality.db";

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
         //   e.printStackTrace();
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
          //  e.printStackTrace();
        }
            return false;
    }

    /*
     * Função para procurar a posicao no arquivo que esta livre para inserir o indice
     * @ param palavra -> palavra que foi repetida
     * @ param arquivo -> arquivo que tem que ser lido
     * @return -> a posicao do arquivo livre
     */
    public long posIndiceLivre(String palavra,String arquivo){
        try{
            arq = new RandomAccessFile(arquivo, "rw");

            arq.seek(0);
            long pos = arq.getFilePointer();
            String palavra_arq;

            while(arq.getFilePointer() < arq.length()){
                palavra_arq = arq.readUTF();

                if(palavra.compareTo(palavra_arq) == 0){
                    //Pega a posição antes de ler para se caso o valor seja == a - 1 retornar a posição correta livre
                    pos = arq.getFilePointer();
                    if(arq.readByte() == -1){
                        return pos;
                    }

                    pos = arq.getFilePointer();
                    if(arq.readByte() == -1){
                        return pos;
                    }

                    pos = arq.getFilePointer();
                    if(arq.readByte() == -1){
                        return pos;
                    }

                    pos = arq.getFilePointer();
                    if(arq.readByte() == -1){
                        return pos;
                    }

                    pos = arq.getFilePointer();
                    if(arq.readLong() == -1){
                        arq.seek(pos);                   // vai para a ultima posicao livre registrada 
                        arq.writeLong(arq.length());     // escreve a ultima posicao do arquivo como se fosse um ponteiro para a continuacao do array 
                        arq.seek(arq.length());          // vai para a ultima posicao
                        return arq.getFilePointer();     // retorna a ultima posicao do arquivo para criar o objeto apontado
                    }
                }
            }
        }catch(Exception e){
           // e.printStackTrace();
        }
        return -1;
    }

    /**
     * Cria a lista invertida do apelido e cidade do time, quando ele existe faz um ponteiro para a proxima localizacao da continuacao, pois
     * ele so tem tamanho 5. Caso ele nao exista ele cria um normal de tamanho 5 na ultima posicao do arquivo
     * @param nome -> apelido ou cidade do time inteiro a ser inserido na lista
     * @param id -> id do time a ser inserido na lista
     */
    public void createArqLista(String nome,byte id,String arquivo){
        String palavras[] = new String[contarNumeroPalavras(nome)];
        palavras = nome.split(" ");

        try{
            arq = new RandomAccessFile(arquivo,"rw");

            for(int i = 0; i < palavras.length; i++){
                if(contemPalavra(palavras[i],arquivo) == true){
                    long posAvaliable = posIndiceLivre(palavras[i], arquivo);

                    if(posAvaliable != arq.length())
                    {
                        arq.seek(posAvaliable);
                        arq.writeByte(id);
                    }
                    else
                    //Temos que criar a mesma palavras novamente com o id desejado
                    {
                    arq.seek(posAvaliable);
                    arq.writeUTF(palavras[i]);
                    arq.writeByte(id);
                    arq.writeByte(-1);
                    arq.writeByte(-1);
                    arq.writeByte(-1);
                    arq.writeByte(-1);
                    arq.writeByte(-1);
                    }
                }
                else{
                    arq.seek(arq.length());
                    arq.writeUTF(palavras[i]);
                    arq.writeByte(id);
                }
            }
        } catch(Exception e){
         //   e.printStackTrace();
        }
    }

    public void showListaInvertida(){
        try{
            arq = new RandomAccessFile(arqListaInvertidaNome, "rw");

            while(arq.getFilePointer() < arq.length()){
                System.out.println(arq.readUTF());
                System.out.println(arq.readByte());
                System.out.println(arq.readByte());
                System.out.println(arq.readByte());
                System.out.println(arq.readByte());
                System.out.println(arq.readByte());
                System.out.println(arq.readLong());
                System.out.println("------------------------");
            }
        } catch (Exception e){
           // e.printStackTrace();
        }
    }

    /*
     * Remover do arquivo da lista todas as palavras relacionadas ao id
     * @param id -> id que deve ser deletado
     */
    public void DeleteAllIdForList(byte id,String arquivo){
        try{
            arq = new RandomAccessFile(arquivo, "rw");

            long pos;

            while(arq.getFilePointer() < arq.length()){
                arq.readUTF(); // ler a palavra

                pos = arq.getFilePointer();
                if(arq.readByte() == id){
                    arq.seek(pos);
                    arq.writeByte(-1);
                }

                pos = arq.getFilePointer();
                if(arq.readByte() == id){
                    arq.seek(pos);
                    arq.writeByte(-1);
                }

                pos = arq.getFilePointer();
                if(arq.readByte() == id){
                    arq.seek(pos);
                    arq.writeByte(-1);
                }

                pos = arq.getFilePointer();
                if(arq.readByte() == id){
                    arq.seek(pos);
                    arq.writeByte(-1);
                }

                pos = arq.getFilePointer();
                if(arq.readByte() == id){
                    arq.seek(pos);
                    arq.writeByte(-1);
                }

                arq.readLong();
            }
        } catch(Exception e){
           // e.printStackTrace();
        }
    }

    /*
     * Funcao tem a funcao de deletar os indices do arquivo e atualizar conforme as mudanças no arquivo original
     * @param palavra -> nova palavra a ser atualizada
     * @param id -> id a ser deletado e/ou inserido
     * @param arquivo -> arquivo a ser lido
     * @param isDelete -> boolean para saber das mudanças que devem ser feitas
     */
    public void updateLista(String palavra,byte id,String arquivo,boolean isDelete){
        try{
            //Pega a posicao que precisa ser deletada e deleta do arquivo
            DeleteAllIdForList(id, arquivo);

            if(isDelete == false){
                createArqLista(palavra, id, arquivo);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    /*
     * Função necessaria para confirmar se o id esta presente na lista
     * @param ids -> lista de ids a ser percorrida
     * @param id -> id a ser encontrado ou nao na lista
     * @return -> true se achar o id e false se nao a char o id na lista
     */
    public boolean idExistsInArray(ArrayList<Byte> ids,byte id)
    {
        for(Byte idList : ids){
            if(idList == id)
            {
                return true;
            }
            else
            {
                break;
            }
        }
        return false;
    }

    /*
     * Funcao busca e printa os ids disponiveis para cada palavra inserida pelo usuario
     * @param palavra -> palavra que deve ser buscada no arquivo da lista (cada uma)
     * @param arquivo -> arquivo de busca
     */
    public void searchList(String palavra,String arquivo){
        String palavras[] = new String[contarNumeroPalavras(palavra)];
        palavras = palavra.split(" ");

        try{
            arq = new RandomAccessFile(arquivo, "rw");
            String palavras_arq;
            byte id;
            long pos;
            ArrayList<Byte> ids = new ArrayList<>();

            //for para buscar por cada palavra no arquivo
            for(int i = 0; i < palavras.length; i++){
                //while para navegar dentro do arquivo tentando buscar a palavra
                arq.seek(0);
                while(arq.getFilePointer() < arq.length()){
                    long poss = arq.getFilePointer();
                    System.out.println(poss + "-" + arq.length());

                    palavras_arq = arq.readUTF();
                    
                    System.out.println(palavras_arq);

                    if(palavras[i].compareTo(palavras_arq) == 0){
                        pos = arq.getFilePointer();
                        if(arq.readByte() != -1){
                            arq.seek(pos);
                            id = arq.readByte();
                            if(idExistsInArray(ids,id) == false){
                                ids.add(id);
                            }
                            if(arq.getFilePointer() + 1 == arq.length()) break;
                        }
                    }
                    else{id = arq.readByte();}
                    }
                }
 
                System.out.println("\n Os ID's relacionados à palavra digitada são: ");;
                System.out.println(ids);

        }catch (Exception e){
          //  e.printStackTrace();
        }
    }
}