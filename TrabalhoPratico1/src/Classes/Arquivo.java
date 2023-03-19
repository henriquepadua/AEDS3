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

    // public static void escreverJogador(Jogador jogador)throws IOException{
    //     byte[] ba = jogador.toByteArray();
    //     fileReader.seek(0);
    //     fileReader.writeInt(jogador.getId());

    //     fileReader.seek(fileReader.length());
    //     fileReader.writeInt(ba.length);
    //     fileReader.write(ba);

    //}

    public static void imprimeArquivo (long comeco) { // imprime as ids de um vo
		int ultimaId;
		int tamRegAtual;
		long pos0;
		int idAtual;
		
		try {
			fileReader.seek(comeco);
			ultimaId = fileReader.readInt();
			idAtual = -1;
			System.out.print("| ");
			while(idAtual != ultimaId) { // varre o fileReaderuivo e imprime as ids
				tamRegAtual = fileReader.readInt();
				pos0 = fileReader.getFilePointer();
				if(fileReader.readBoolean() != false) {
					idAtual = fileReader.readInt();
					System.out.print(idAtual + ", ");
				} else {
					System.out.print("*, ");
				}
				fileReader.seek(pos0);
				fileReader.skipBytes(tamRegAtual);
				System.out.print(tamRegAtual + "B | "); // teste
			}
			System.out.println("");
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}

    public static Jogador leJogador(int tamanhoArquivo, int id, boolean lapide) throws Exception{
        Jogador jogador = new Jogador();
        String s = "";
        Date date = jogador.getJoinedOn();
        int tamanhoString = 0;

        jogador.setLapide(lapide);
        jogador.setId(id);
        tamanhoString = fileReader.readInt();
        jogador.setKnownAs(s = fileReader.readUTF());
        System.out.println(s);
        tamanhoString = fileReader.readInt();
        jogador.setFullName(s = fileReader.readUTF());
        jogador.setOverall(fileReader.readByte());
        jogador.setValue(fileReader.readDouble());
        tamanhoString = fileReader.readInt();
        jogador.setBestPosition(s = fileReader.readUTF());
        tamanhoString = fileReader.readInt();
        jogador.setNacionality(s = fileReader.readUTF());
        jogador.setAge(fileReader.readByte());
        tamanhoString = fileReader.readInt();
        jogador.setClubName(s = fileReader.readUTF());
        System.out.println(tamanhoString = fileReader.readInt());
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
    

    public static void create(Jogador jogador) throws IOException{ 

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

    public static void criarJogador(Jogador jogador) throws Exception{
        Scanner sc = new Scanner(System.in);
        System.out.println("Digite o nome: ");
        jogador.setKnownAs(sc.nextLine());
                    
        System.out.println("Digite o Overall: ");
        jogador.setOverall(sc.nextByte());
        
        System.out.println("Digite o valor: ");
        jogador.setValue(sc.nextDouble());
        
        System.out.println("Digite a posição: ");
        jogador.setBestPosition(sc.next());
        
        System.out.println("Digite a nacionalidade: ");
        jogador.setNacionality(sc.next());
        
        System.out.println("Digite a idade: ");
        jogador.setAge(sc.nextByte());
        
        System.out.println("Digite o clube(SIGLA): ");
        jogador.setClubName(sc.next());

        System.out.println("Digite o ano: ");
        jogador.setJoinedOn(sc.next());
        create(jogador);
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
