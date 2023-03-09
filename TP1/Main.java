import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Scanner;

public class Main {
    public static Scanner sc = new Scanner(System.in);
    public static int i = 0;

    public static Jogador tratamentoFluxoDados(RandomAccessFile arquivodb,RandomAccessFile csv,Jogador fifa) throws IOException{
        /* Declaraçaão de variaveis */
        String line = csv.readLine();

        String[] elemento = new String[100]; 
        
        int j = 0,tamanhoString = 0, controle = 0,controleValue = 0,controlePositionPlayer = 0,controleBestPosition = 0,controleNationality = 0,
            controleImage = 0,controleFullName = 0,controleOverall = 0,controlePotential = 0;

        //tratamento de dados da extensão csv    
        while(line.charAt(j) != ','){//enquanto não achar uma virgula soma caractere por caractere
            if(controle > 8) break;
            elemento[i] += line.charAt(j);
            j++;//somo mais um para
            if(j == line.length()){ break;}//se a linha chegar ao fim sai do loop
            if(line.charAt(j) == ','){
                    elemento[i] = elemento[i].replaceAll("null", "");
                    elemento[i] += ",";
                    tamanhoString = elemento[i].length() - tamanhoString;
                    if(controle == 0){ fifa.KnownAs = elemento[i]; controle++;controleFullName = j;}

                    else if(controle == 1){ fifa.FullName = elemento[i].substring(controleFullName+1,j); controle++;controleOverall = j;}
                    else if(controle == 2){ fifa.Overall =   Integer.parseInt(elemento[i].substring(controleOverall+1,j)); controle++;controlePotential = j;}
                    else if(controle == 3){ fifa.Potential = Integer.parseInt(elemento[i].substring(controlePotential+1,j)); controle++; controleValue = j;}

                    else if(controle == 4){ fifa.Value = Integer.parseInt(elemento[i].substring(controleValue + 1 ,j)); controle++; controlePositionPlayer = j;}
                     
                    else if(controle == 5){
                        //System.out.println(line.charAt(controlePositionPlayer+1));   
                        int aux = controlePositionPlayer+2;                                             
                        
                        if(line.charAt(controlePositionPlayer+1) == '"'){ 
                            while(line.charAt(aux) != '"'){
                                fifa.PositionsPlayed += line.charAt(aux);  
                                aux++;
                            }

                            controle++; controleBestPosition = aux; 
                        }
                        else{
                            fifa.PositionsPlayed = elemento[i].substring(j-2,j); 
                            controle++; controleBestPosition = j;
                        }
                    }
                    else if(controle == 6){ 
                        int aux = controleBestPosition + 2;
                        
                        if(line.charAt(controleBestPosition+1) == ','){
                            while(line.charAt(aux) != ','){
                                fifa.BestPosition += line.charAt(aux);  
                                aux++;
                            }
                            controle++; controleNationality = aux;
                        }
                        else{
                            fifa.BestPosition = elemento[i].substring(controleBestPosition+1,j); controle++; controleNationality = j;
                        }
                    }    
                    
                    else if(controle == 7){ 
                       // System.out.println(line.charAt(controleNationality));
                        int aux = controleNationality + 1;
                        
                        if(line.charAt(controleNationality) == ','){
                            while(line.charAt(aux) != ','){
                                fifa.Nationality += line.charAt(aux);  
                                fifa.Nationality = fifa.Nationality.replaceAll("null", "");
                        
                                aux++;
                            }
                            controle++; controleImage = aux;
                        }
                        else{
                            fifa.Nationality = elemento[i].substring(controleNationality+1,j); controle++; controleImage = j;
                        }
                    }
                    else if(controle == 8){ 
                        //System.out.println(line.charAt(controleImage));
                        int aux = controleImage + 1;

                        if(line.charAt(controleImage) == ','){
                            while(line.charAt(aux) != ','){
                                fifa.ImageLink += line.charAt(aux);  
                                fifa.ImageLink = fifa.ImageLink.replaceAll("null", "");

                                aux++;
                            }
                            controle++;
                        }

                        else{
                            fifa.ImageLink   = elemento[i].substring(controleImage+1,j); controle++; 
                        }
                    }
                    j++;
            }
        }
        i++;
        return fifa;
    }    

    public static Jogador Create(RandomAccessFile arquivodb,RandomAccessFile csv,Jogador fifa) throws IOException{  
        if(fifa != null){         
            fifa = tratamentoFluxoDados(arquivodb, csv, fifa);
            
            arquivodb.seek(0);
            int ultimoId = arquivodb.readInt();
            
            fifa.Id = ultimoId + 1;
            arquivodb.seek(0);
            
            arquivodb.writeInt(fifa.Id);

            fifa.lapide = ' ';
            
            arquivodb.seek(arquivodb.length());
            byte[] registro = fifa.toByteArray();
            arquivodb.writeInt(registro.length);

            arquivodb.write(registro);
                
            return fifa;
        }
        return null;
    }

    public static long buscaId (RandomAccessFile arq, long comeco, int idDesejada) { // retorna a posicao do registro com a id desejada no arquivo, antes do indicador de tamanho
    	try {
	    	arq.seek(comeco);
			int ultimaId = arq.readInt();
			int tamRegAtual;
			long pos0;
			int idAtual = 0;
			if(ultimaId > 0 ) { // se tiver mais de um registro
				do {
                    char lapide = arq.readChar();
					pos0 = arq.getFilePointer();
					tamRegAtual = arq.readInt();
					if(lapide != '*') { // se o registro atual nao tiver sido deletado
						idAtual = arq.readInt();
						
						if(idAtual!=idDesejada) { // se nao for o registro desejado, pula para o proximo
							arq.seek(pos0);
							arq.readInt();
							arq.skipBytes(tamRegAtual);
						}
						
					} else { // se o registro atual tiver sido deletado
						arq.seek(pos0); // vai para o proximo registro
						arq.readInt();
						arq.skipBytes(tamRegAtual);
					}
				} while (idAtual!=ultimaId && idAtual!=idDesejada); // repete para cada registro ate chegar no ultimo ou ate chegar na id desejada
				
				if(idAtual == idDesejada) { // se encontramos o registro desejado, retorna a posicao do registro no arquivo
					return pos0;
				} else { // se nao encontramos o registro, retorna -1
					return -1;
				}
			} else { // se nao tiver nenhum registro
				return -1;
			}
    	} catch(IOException e) {
			System.out.println(e.getMessage());
		}
    	return -1;
    }

    public static Jogador Read(RandomAccessFile arquivodb,int id){
        System.out.println("\n=== BUSCAR UM JOGADOR ===\n");
		System.out.println("Digite a ID do Jogador que quer exibir:");

        int tamanhoString;
        String KnownAs;
        String FullName;
        int Overall;
        int Potential;
        int Value;
        String PositionsPlayed;
        String BestPosition;
        String Nationality;
        String ImageLink;
        int idAtual = 0;
        long pos = 0;
        
        int idDesejada = sc.nextInt(); int jogadoresPossiveis = 0;
		sc.nextLine();
        
        char lapide;
        long posRegistro = 0;
        try {
			arquivodb.seek(0);
			int ultimaId = arquivodb.readInt();
            
			if(ultimaId > 0 ) { // se tiver mais de um registro
                    do {
                        pos = arquivodb.getFilePointer();
                        int tamRegAtual = arquivodb.readInt();

                        pos = arquivodb.getFilePointer();
                        lapide = arquivodb.readChar();

                        idAtual = arquivodb.readInt();
                        posRegistro = arquivodb.getFilePointer();
                        if( lapide != '*') { // se o registro atual nao tiver sido deletado
                            
                            if(idAtual!=idDesejada) { // se nao for o registro desejado, pula para o proximo
                                arquivodb.seek(pos);
                                arquivodb.skipBytes(tamRegAtual);
                            }
                            if(jogadoresPossiveis == 1 && idAtual == idDesejada){ posRegistro = arquivodb.getFilePointer() - 10 ; break;
                            }
                            
                        } else { // se o registro atual tiver sido deletado
                            if(lapide == '*' && idAtual == idDesejada){
                                jogadoresPossiveis++;
                                arquivodb.seek(pos);
                                arquivodb.skipBytes(tamRegAtual);
                            }else{
                                arquivodb.seek(pos + tamRegAtual); // vai para o proximo registro
                            }
                            if(jogadoresPossiveis == 1 && idAtual == idDesejada) idAtual = idAtual - 1;
                            if(jogadoresPossiveis == 2) return null;
                        }
                        posRegistro = arquivodb.getFilePointer();
                    } while (idAtual!=ultimaId && idAtual!=idDesejada); // repete para cada registro ate chegar no ultimo ou ate chegar na id desejada
				
				if(idAtual == idDesejada && lapide != '*') { // se encontramos o registro desejado, le os dados do registro e o imprime
                    if(jogadoresPossiveis == 1) arquivodb.seek(posRegistro+10);
					tamanhoString = arquivodb.readInt();
					KnownAs = arquivodb.readUTF();
					tamanhoString = arquivodb.readInt();
					FullName = arquivodb.readUTF();
					Overall = arquivodb.readInt();
                    Potential = arquivodb.readInt();
                    Value = arquivodb.readInt();
                    tamanhoString = arquivodb.readInt();
					PositionsPlayed = arquivodb.readUTF();
                    tamanhoString = arquivodb.readInt();
					BestPosition = arquivodb.readUTF();
                    tamanhoString = arquivodb.readInt();
					Nationality = arquivodb.readUTF();
					tamanhoString = arquivodb.readInt();
					ImageLink = arquivodb.readUTF();
					
					Jogador jogador = new Jogador(idDesejada, KnownAs, FullName, Overall, Potential, Value, PositionsPlayed, BestPosition, Nationality, ImageLink, ultimaId);
					System.out.println("\n"+ "Jogador#" + idDesejada + " = " +  jogador.toString());
					//System.out.println(jogador.toString());
					System.out.println("\nAperte enter para continuar.");
					sc.nextLine();
					return jogador;
				} else {
					System.out.println("\nJogador não encontrado. Aperte enter para continuar.");
					sc.nextLine();
					return null;
				}
			} else { // se nao tiver nenhum registro
				System.out.println("\nBanco de dados vazio. Aperte enter para continuar.");
				sc.nextLine();
				return null;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

    public static boolean Update(RandomAccessFile arquivodb,RandomAccessFile csv,Jogador jogador) throws IOException{//metodo para atualizar jogador
        System.out.println("\n=== ATUALIZAR UM JOGADOR ===\n");
		System.out.println("Digite a ID do Jogador que quer atualizar:");
        
        boolean resp = false;
        long comeco = 0;
        int id = 0;

        int idDesejada = sc.nextInt();
		sc.nextLine();
  
        jogador = tratamentoFluxoDados(arquivodb, csv, jogador);
        jogador.Id = idDesejada;

        arquivodb.seek(comeco);// move a posicao para a primeira lapide
  
        int ultimaId  = arquivodb.readInt();
        char lapide;

        if(ultimaId > 0 ) { // se tiver mais de um registro
                
            do {
                
                long voltaParaTamanho = arquivodb.getFilePointer();

                int tamanho = arquivodb.readInt();//ler o tamanho do registro

                long pos = arquivodb.getFilePointer();

                lapide = arquivodb.readChar() ;

                id = arquivodb.readInt();

                if(lapide != '*'){//verifica se a lapide esta vazia ou foi apagada
                    //id = arquivodb.readInt();// ler o id do jogador pesquisado atual

                    if(id == jogador.Id){//se id lido do arquivo for o mesmo da nova conta

                        byte[] conta = jogador.toByteArray();// escreve os dados da nova conta e retorna o bytearray

                        if(conta.length <= tamanho){//se o tamanho da nova conta for igual ao tamanho da conta lida do arquivo
                            
                        arquivodb.seek(pos);//retorna o ponteiro para depois do tamanho para escrever a nova conta      
                        
                        arquivodb.write(conta);//escreve o novo bytearray

                        resp =  true;//resp recebe true se todos os dados conferem
                        }
                        else{
                        arquivodb.seek(pos);   arquivodb.writeChar('*');
                        
                        arquivodb.seek(arquivodb.length()); jogador.lapide = ' ';

                        conta = jogador.toByteArray(); arquivodb.writeInt(conta.length);
                        
                        arquivodb.write(conta);
                        
                        resp = true;
                        }
                        return resp;
                    }
                    else{ // se nao for o registro desejado, pula para o proximo
                        arquivodb.seek(pos);
                        arquivodb.skipBytes(tamanho);
                    }
                } else {
                    arquivodb.seek(pos);
                    arquivodb.skipBytes(tamanho);
                }
                //pos = arquivodb.getFilePointer() + tamanho + 4;// se a conta foi apagada ou o id nao foi encontrado move para a proxima lapide do proximo registro (o + 4 para pular os 4 bytes do id)
            } while (id !=ultimaId && id !=jogador.Id); // repete para cada registro ate chegar no ultimo ou ate chegar na id desejada 
            System.out.println("\nJogador não encontrado. Aperte enter para continuar.");
            sc.nextLine();
            return resp;
        }
          return resp;
    }

    public static boolean Delete(RandomAccessFile arquivodb,Jogador jogador) throws IOException{//metodo para deletar conta
        System.out.println("\n=== DELETAR UM JOGADOR ===\n");
		System.out.println("Digite a ID do Jogador que quer deletar:");
        
        boolean resp = false;

        int idDesejada = sc.nextInt();
		sc.nextLine();

        arquivodb.seek(0);
        jogador.Id = idDesejada;

        int ultimaId  = arquivodb.readInt();
        int jogadoresMesmoIdDeletados = 0;

        if(ultimaId > 0 ) { // se tiver mais de um registro
            
            int idAtual = 0;

            char lapide;

            do {
                
                long voltaParaTamanho = arquivodb.getFilePointer();

                int tamanho = arquivodb.readInt();//ler o tamanho do registro

                long pos = arquivodb.getFilePointer();

                lapide = arquivodb.readChar();

                idAtual = arquivodb.readInt();// ler o id do jogador pesquisado atual
                
                if(lapide != '*'){//verifica se a lapide esta vazia ou foi apagada
                    
                    if(idAtual != jogador.Id){//se id lido do arquivo for o mesmo da nova conta                     
                        arquivodb.seek(pos);
                        arquivodb.skipBytes(tamanho);
                    }
                    else{
                        arquivodb.seek(pos);
                        arquivodb.writeChar('*');
                        resp = true;
                    }
                }
                else if(lapide == '*' && idAtual < jogador.Id){
                    arquivodb.seek(pos);
                    arquivodb.skipBytes(tamanho);
                }
                else if(lapide == '*' && idAtual == jogador.Id){
                    jogadoresMesmoIdDeletados++;
                    arquivodb.seek(pos);
                    arquivodb.skipBytes(tamanho);
                }
                if(jogadoresMesmoIdDeletados == 2) break;
                if(lapide == ' ' && idAtual == jogador.Id) break;
            } while (idAtual !=ultimaId && idAtual !=jogador.Id || (jogadoresMesmoIdDeletados > 0)); // repete para cada registro ate chegar no ultimo ou ate chegar na id desejada    
            if(lapide == '*' && idAtual == jogador.Id){
                System.out.println("Jogador ja foi deletado");
                return resp;
            }
            if(idDesejada > ultimaId){           
                System.out.println("Jogador nao existe");
                return resp;
            }
        }
        return resp;
      }

    public static void main(String[] args) throws IOException 
    {
        RandomAccessFile csv = new RandomAccessFile("Fifa 23 Players Data.csv", "rw");
        RandomAccessFile arquivodb = new RandomAccessFile("Jogadores.db", "rw");

        arquivodb.seek(0); 
        
        if(arquivodb.length() == 0) arquivodb.writeInt(0);   arquivodb.seek(arquivodb.length());

        csv.readLine();
       // tratamentoDadosIniciais(csv, arquivodb);

        boolean sair = false;
        String opcao; Scanner sc = new Scanner(System.in);
        //Jogador fifa = new Jogador();
        while(!sair) { // mostra o menu enquanto a pessoa nao escolher sair
            Jogador fifa = new Jogador();
            System.out.println("=== SISTEMA DE CONTAS ===\n");
            System.out.println("Escolha uma opção:");
            System.out.println("1) Criar Jogador");
            System.out.println("2) Pesquisar Jogador");
            System.out.println("3) Alterar Jogador");
            System.out.println("4) Deletar Jogador");
            System.out.println("5) Intercalação balanceada comum");
            System.out.println("S) Sair");
            opcao = sc.nextLine();
            switch(opcao) { // trata as opcoes
                case "1":
                    fifa = Create(arquivodb,csv, fifa);
                    break;
                case "2":
                   Read(arquivodb, 0);
                    break;
                case "3":
                    Update(arquivodb,csv, fifa);
                    break;
                 case "4":
                     Delete(arquivodb, fifa);
                     break;
                // case "5":
                //     intercalacaoBalanceada2(arq, comeco);
                //     break;
                //     System.out.println("\nOpção inválida. Tente novamente.\n\n");
                //     break;
                case "s":
                    sair = true;
                    System.out.println("Saindo...");
                    sc.close();
                    break;
                case "S":
                    sair = true;
                    System.out.println("Saindo...");
                    sc.close();
                    break;
            }
        }
    }
}