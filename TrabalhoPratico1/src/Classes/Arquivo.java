package Classes;

import java.io.*;
import java.util.Date;
import java.util.Scanner;

public class Arquivo{
    private File arquivo;
    public static RandomAccessFile fileReader;
    private static long posicao;
    final int cabecalho = 4;
    

    public Arquivo(String arquivo)throws IOException{
        this.arquivo = new File(arquivo);
        fileReader = new RandomAccessFile(arquivo, "rw");
        if(fileReader.length() == 0) fileReader.writeInt(0);
    }

    public Arquivo(){}

    public static void escreverJogador(Jogador jogador)throws IOException{
        byte[] ba = jogador.toByteArray();
        fileReader.seek(0);
        fileReader.writeInt(jogador.getId());

        fileReader.seek(fileReader.length());
        fileReader.writeInt(ba.length);
        fileReader.write(ba);

    }

    public static Jogador leJogador(int tamanhoArquivo, int id, boolean lapide) throws Exception{
        Jogador jogador = new Jogador();
        String s = "";
        Date date = jogador.getJoinedOn();

        jogador.setLapide(lapide);
        jogador.setId(id);
        jogador.setKnownAs(s = fileReader.readUTF());
        System.out.println(s);
        jogador.setFullName(s = fileReader.readUTF());
        jogador.setOverall(fileReader.readByte());
        jogador.setValue(fileReader.readDouble());
        jogador.setBestPosition(s = fileReader.readUTF());
        jogador.setNacionality(s = fileReader.readUTF());
        jogador.setAge(fileReader.readByte());
        jogador.setClubName(s = fileReader.readUTF());
        //fileReader.seek(fileReader.length() - 4);
        int tamanho = 0;
        System.out.println(tamanho = fileReader.readInt());
        jogador.setJoinedOn(s = fileReader.readUTF());


        return jogador;
    }

    public void pesquisa(int id)throws IOException{
        fileReader.seek(0);
        fileReader.readInt();
        int tamanhoJogador = 0;
        boolean lapide;
        int idJogador;

        try{
            while(fileReader.getFilePointer() < fileReader.length()){
                lapide = fileReader.readBoolean();

                if(lapide){
                    idJogador = fileReader.readInt();
                    tamanhoJogador = fileReader.readInt();

                    if(idJogador == id){
                        System.out.println(leJogador(tamanhoJogador, id, lapide));
                        break;
                    }else{
                        fileReader.skipBytes(tamanhoJogador - 11);
                    }
                }else{
                    fileReader.skipBytes(tamanhoJogador - 1);
                }
            }
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    public static Jogador pesquisa(int id, Jogador jogador) throws IOException{
        fileReader.seek(0);
        fileReader.readInt();
        int tamanhoJogador;
        boolean lapide;
        int idJogador;

        try{
            while(fileReader.getFilePointer() < fileReader.length()){
                posicao = fileReader.getFilePointer();
                tamanhoJogador = fileReader.readInt();
                lapide = fileReader.readBoolean();
                
                if(lapide){
                   // fileReader.readInt();
                    idJogador = fileReader.readInt();
                    if(idJogador == id){
                       jogador = leJogador(tamanhoJogador, id, lapide);
                        break;
                    }else {
                        fileReader.skipBytes(tamanhoJogador - 5);
                    }
                } else{
                    fileReader.skipBytes(tamanhoJogador - 1);
                }
            }

            if(jogador.getFullName() == ""){ System.out.println("Id deletado");
                return null;
            }
            

        } catch(Exception e){
            System.err.println("Id nao encontrado");
        }

        return jogador;
    }
    

    public void create(Jogador jogador) throws IOException{ 

        //fileReader = new RandomAccessFile("jogador.db", "rw");
        fileReader.seek(0);
        
        if(fileReader.length() == 0) fileReader.writeInt(0);
        
        int ultimoId = fileReader.readInt();
        int proximoId = ultimoId + 1;
        
        fileReader.seek(0);
        fileReader.writeInt(proximoId);

        fileReader.seek(fileReader.length());

        jogador.setLapide(true);
        jogador.setId(proximoId);

        byte[] ba = jogador.toByteArray();
               
        fileReader.writeInt(ba.length);
        fileReader.write(ba);     

    }

    	

    public void update(int id, byte opcao) throws IOException{ //metodo para atualizar jogador
        Jogador jogador = new Jogador();
        Scanner sc = new Scanner(System.in);

        jogador = pesquisa(id,jogador);

        if(jogador != null && jogador.getLapide() == true){
            System.out.println("Jogador selecionado:");
            System.out.println(jogador);        


            switch(opcao){
                case 1:
                    System.out.println("Digite o novo nome: ");
                    jogador.setKnownAs(sc.nextLine());
                    break;
                case 2:
                    System.out.println("Digite o novo Overall: ");
                    jogador.setOverall(sc.nextByte());
                    break;
                case 3:
                    System.out.println("Digite o novo valor: ");
                    jogador.setValue(sc.nextDouble());
                    break;
                case 4:
                    System.out.println("Digite a nova posição: ");
                    jogador.setBestPosition(sc.nextLine());
                    break;
                case 5:
                    System.out.println("Digite a nova nacionalidade: ");
                    jogador.setNacionality(sc.nextLine());
                    break;
                case 6:
                    System.out.println("Digite a nova idade: ");
                    jogador.setAge(sc.nextByte());
                    break;
                case 7:
                    System.out.println("Digite o novo clube: ");
                    jogador.setClubName(sc.nextLine());
                    break;
                default:
                    System.out.println("A opção escolhida não é válida");
            }
            
            //sc.close();

            jogador.setId(id);
            jogador.setLapide(true);
            byte[] ba = jogador.toByteArray();
            fileReader.seek(posicao);

            int tamanhoJogador = fileReader.readInt();
            if(ba.length <= tamanhoJogador){
                fileReader.write(ba);
            }else{
                byte[] jogadorba = jogador.toByteArray();              
                fileReader.writeBoolean(false);
                fileReader.seek(fileReader.length());
                fileReader.writeInt(jogadorba.length);
                fileReader.write(jogadorba);
            }
        }
        System.out.println("Não foi possível encontrar Jogador, seu Jogador foi deletado ou não existe!! Favor verificar seus dados");
    }

    public void delete(int id) throws IOException{ //metodo para deletar conta
        fileReader.seek(0);
        fileReader.readInt();
        int tamanhoJogador;
        boolean lapide;
        int idJogador;
        long posicaoLapide;

        try{
            while(fileReader.getFilePointer() < fileReader.length()){
                tamanhoJogador = fileReader.readInt();
                posicaoLapide = fileReader.getFilePointer();
                lapide = fileReader.readBoolean();

                if(lapide){
                    idJogador = fileReader.readInt();
                    if(idJogador == id){
                        fileReader.seek(posicaoLapide);
                        fileReader.writeBoolean(false);
                        fileReader.skipBytes(4);
                        System.out.println("Jogador deletado: \n" + leJogador(tamanhoJogador,id,false));
                        break;
                    }else{
                        fileReader.skipBytes(tamanhoJogador - 5);
                    }
                } else{
                    fileReader.skipBytes(tamanhoJogador - 1);
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }   
    }
}
