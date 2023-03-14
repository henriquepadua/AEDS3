import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.List;
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

            fifa.lapide = 0;
            
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
                    byte lapide = arq.readByte();
					pos0 = arq.getFilePointer();
					tamRegAtual = arq.readInt();
					if(lapide != 1) { // se o registro atual nao tiver sido deletado
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
        
        byte lapide = 0;
        long posRegistro = 0;
        try {
			arquivodb.seek(0);
			int ultimaId = arquivodb.readInt();
            
			if(ultimaId > 0 ) { // se tiver mais de um registro
                    do {
                        pos = arquivodb.getFilePointer();
                        if(pos + 8 > arquivodb.length()) break;

                        int tamRegAtual = arquivodb.readInt();
                        pos = arquivodb.getFilePointer();
       
                        lapide = arquivodb.readByte();

                        idAtual = arquivodb.readInt();

                        if(idAtual > idDesejada) break;
                        posRegistro = arquivodb.getFilePointer();
                        if( lapide != 1) { // se o registro atual nao tiver sido deletado
                            
                            if(idAtual!=idDesejada) { // se nao for o registro desejado, pula para o proximo
                                arquivodb.seek(pos);
                                arquivodb.skipBytes(tamRegAtual);
                            }   
                        }
                        else{
                        arquivodb.seek(pos + tamRegAtual); // vai para o proximo registro
                        }
                                               
                        posRegistro = arquivodb.getFilePointer();
                    } while (idAtual!=ultimaId && idAtual!=idDesejada || jogadoresPossiveis == 1 ); // repete para cada registro ate chegar no ultimo ou ate chegar na id desejada
				
				if(idAtual == idDesejada && lapide != 1) { // se encontramos o registro desejado, le os dados do registro e o imprime
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
					
					Jogador jogador = new Jogador(idDesejada, KnownAs, FullName, Overall, Potential, Value, PositionsPlayed, BestPosition, Nationality, ImageLink);
					System.out.println("\n"+ "Jogador#" + idDesejada + " = " +  jogador.toString());

					System.out.println("\nAperte enter para continuar.");
					sc.nextLine();
					return jogador;
				} else {
					System.out.println("\nJogador deletado. Aperte enter para continuar.");
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
        byte lapide;

        if(ultimaId > 0 ) { // se tiver mais de um registro
                
            do {
                
                int tamanho = arquivodb.readInt();//ler o tamanho do registro

                long pos = arquivodb.getFilePointer();

                lapide = arquivodb.readByte() ;

                id = arquivodb.readInt();

                if(lapide != 1){//verifica se a lapide esta vazia ou foi apagada
                    //id = arquivodb.readInt();// ler o id do jogador pesquisado atual

                    if(id == jogador.Id){//se id lido do arquivo for o mesmo da nova conta

                        byte[] conta = jogador.toByteArray();// escreve os dados da nova conta e retorna o bytearray

                        if(conta.length <= tamanho){//se o tamanho da nova conta for igual ao tamanho da conta lida do arquivo
                            
                        arquivodb.seek(pos);//retorna o ponteiro para depois do tamanho para escrever a nova conta      
                        
                        arquivodb.write(conta);//escreve o novo bytearray

                        resp =  true;//resp recebe true se todos os dados conferem
                        }
                        else{
                        arquivodb.seek(pos);   arquivodb.writeByte(1);
                        
                        arquivodb.seek(arquivodb.length()); jogador.lapide = 0;

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
            if(lapide == 1) System.out.println("Jogador deletado");
            else System.out.println("\nJogador não encontrado. Aperte enter para continuar.");
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

            byte lapide;

            do {
                
                int tamanho = arquivodb.readInt();//ler o tamanho do registro

                long pos = arquivodb.getFilePointer();

                lapide = arquivodb.readByte();

                idAtual = arquivodb.readInt();// ler o id do jogador pesquisado atual
                
                if(lapide != 1){//verifica se a lapide esta vazia ou foi apagada
                    
                    if(idAtual != jogador.Id){//se id lido do arquivo for o mesmo da nova conta                     
                        arquivodb.seek(pos);
                        arquivodb.skipBytes(tamanho);
                    }
                    else{
                        arquivodb.seek(pos);
                        arquivodb.writeByte(1);
                        resp = true;
                    }
                }
                else if(lapide == 1 && idAtual < jogador.Id){
                    arquivodb.seek(pos);
                    arquivodb.skipBytes(tamanho);
                }
                else if(lapide == 1 && idAtual == jogador.Id){
                    jogadoresMesmoIdDeletados++;
                    arquivodb.seek(pos);
                    arquivodb.skipBytes(tamanho);
                }
                if(jogadoresMesmoIdDeletados == 2) break;
                if(lapide == 0 && idAtual == jogador.Id) break;
            } while (idAtual !=ultimaId && idAtual !=jogador.Id || (jogadoresMesmoIdDeletados > 0)); // repete para cada registro ate chegar no ultimo ou ate chegar na id desejada    
            if(lapide == 1 && idAtual == jogador.Id){
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

      public static void imprimeArquivo (RandomAccessFile arq, long comeco) { // imprime as ids de um arquivo
		int ultimaId;
		int tamRegAtual;
		long pos0;
		int idAtual;
		
		try {
			arq.seek(comeco);
			ultimaId = arq.readInt();
			idAtual = -1;
			System.out.print("| ");
			while(idAtual != ultimaId) { // varre o arquivo e imprime as ids
				tamRegAtual = arq.readInt();
				pos0 = arq.getFilePointer();
				if(arq.readByte() != 1) {
					idAtual = arq.readInt();
					System.out.print(idAtual + ", ");
				} else {
					System.out.print("*, ");
				}
				arq.seek(pos0);
				arq.skipBytes(tamRegAtual);
				System.out.print(tamRegAtual + "B | "); // teste
			}
			System.out.println("");
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}

    public static Jogador leRegistro (RandomAccessFile arquivodb, long comeco, long pos0) { // le um registro e retorna esse registro como objeto (nao le o tamanho do registro)
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
        int id = 0;
        long pos = 0;
		
		try {
            arquivodb.readByte();
			id = arquivodb.readInt();
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
			
			Jogador jogador = new Jogador(id, KnownAs, FullName, Overall, Potential, Value, PositionsPlayed, BestPosition, Nationality, ImageLink);
			return jogador;
		}  catch (IOException e) {
			System.out.println(e.getMessage());
		}
		
		return new Jogador();
	}

    public static void escreveRegistro (RandomAccessFile arq, long pos0, Jogador jogador) { // escreve um jogador na posicao dada (escreve tambem o tamanho, nao grava ultimaId)
		try {
			arq.seek(pos0);
			byte[] ba = jogador.toByteArray();
			arq.writeInt(ba.length);
			arq.write(ba);
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
		return;
	}

    public static void copiaArquivo (RandomAccessFile arqOrigem, long comeco, RandomAccessFile arqDestino) { // copia um arquivo no lugar de outro arquivo
		long pos0, pos1;
		Jogador jogadorTemp;
		int tamRegAtual;
		int ultimaId;
		int idAtual;
		
		try {
			arqOrigem.seek(comeco);
			arqDestino.seek(comeco);
			
			// le qual eh a ultima id
			ultimaId = arqOrigem.readInt();
			
			if(ultimaId > 0) {
				// escreve a ultima id
				arqDestino.writeInt(ultimaId);
				
				do {

					// le o registro atual
					tamRegAtual = arqOrigem.readInt();
					pos0 = arqOrigem.getFilePointer();
					jogadorTemp = leRegistro (arqOrigem, comeco, pos0);
					idAtual = jogadorTemp.getId();
					//System.out.println(jogadorTemp.toString()); // teste
					
					// se o registro atual nao tiver sido deletado
					if(jogadorTemp.getLapide() != 1) {
						
						// escreve o registro atual
						pos1 = arqDestino.getFilePointer();
						escreveRegistro(arqDestino, pos1, jogadorTemp);
					}
					
				} while (idAtual != ultimaId);
				
			} else {
				arqDestino.writeInt(-1);
				return;
			}
			
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}

    public static void intercalacaoBalanceada2 (RandomAccessFile arq, long comeco) {
		try {
			System.out.println("\n=== INTERCALAÇÃO BALANCEADA COMUM ===\n");
			
			int m, n; // m registros, n caminhos
			int ultimaId;
			int idAtual = 0;
			int tamRegAtual;
			long pos0, pos1, posBucketNovo;
			Jogador jogadortemp;
			int arquivoFinal = 0;
			
			System.out.println("Por favor, informe...\nNúmero de registros que cabem na memória:");
			m = sc.nextInt();
			sc.nextLine();
			System.out.println("Número de caminhos:");
			n = sc.nextInt();
			sc.nextLine();
			ArrayList<RandomAccessFile> arqTemp = new ArrayList<RandomAccessFile>();
			List<Jogador> memoria = new ArrayList<Jogador>(m);
			int [] ultimaId2 = new int [2*n];
			int[] ultimoSalvo = new int[2*n];
			
			
			arq.seek(comeco);
			ultimaId = arq.readInt();
			System.out.println("\nArquivo antes da ordenação:");
			
			imprimeArquivo(arq, comeco);
			
			for(int i=0; i<2*n; i++) { // inicia os RandomAccesFiles dos arquivos temporarios
				arqTemp.add(new RandomAccessFile("arqTemp" + i + ".db", "rw"));
				arqTemp.get(i).writeInt(-1); // escreve -1 como sendo a ultima id
				ultimaId2[i] = -1;
			}
			
			arq.seek(comeco);
			ultimaId = arq.readInt();
			idAtual = 0;
			
			for(int i=0; idAtual != ultimaId; i++){ // faz a distribuicao
				// carrega a memoria com os dados
				while(memoria.size()<m && idAtual != ultimaId) { // carrega os m registros na memoria
					tamRegAtual = arq.readInt();
					pos1 = arq.getFilePointer();
					if(arq.readByte()!=1) {
						arq.seek(pos1);
						jogadortemp = leRegistro(arq, comeco, pos1);
						memoria.add(jogadortemp);
						idAtual = jogadortemp.getId();
					} else {
						arq.seek(pos1);
						arq.skipBytes(tamRegAtual);
					}
				}
				
				//for(Conta k : memoria) { // teste
				//	System.out.print(k.getId());
				//}
				
				memoria.sort(Comparator.comparing(Jogador::getId)); // ordena a memoria
				
				//for(Conta k : memoria) { // teste
				//	System.out.print(k.getId());
				//}
				//System.out.println(memoria.size()); // teste
				
				if(arqTemp.get(i%n).length() == 0) { // se o arquivo for vazio, escreve -1 como a ultima id
					arqTemp.get(i%n).seek(comeco);
					arqTemp.get(i%n).writeInt(-1);
					posBucketNovo = arqTemp.get(i%n).getFilePointer(); // salva a posicao atual
					//System.out.println("arquivo vazio"); // teste
				} else {
					posBucketNovo = arqTemp.get(i%n).getFilePointer(); // salva a posicao atual
					arqTemp.get(i%n).seek(comeco); // navega ate o comeco do arquivo temporario para gravar qual foi a ultima id
					arqTemp.get(i%n).writeInt(memoria.get(memoria.size()-1).getId()); // salva qual eh a ultima id na memoria
					arqTemp.get(i%n).seek(posBucketNovo); // navega ate a posicao do comeco do bloco atual
				}
				for(Jogador jogadorTemp : memoria) { // grava os registros da memoria no arquivo temporario
					//System.out.println("escreveu registro " + jogadorTemp.getId()); // teste
					arqTemp.get(i%n).seek(comeco);
					arqTemp.get(i%n).writeInt(jogadorTemp.getId()); // escreve a ultima id
					ultimaId2[i%n] = jogadorTemp.getId();
					arqTemp.get(i%n).seek(posBucketNovo);
					escreveRegistro(arqTemp.get(i%n), posBucketNovo, jogadorTemp);
					posBucketNovo = arqTemp.get(i%n).getFilePointer();
				}
				
				memoria.clear(); // limpa a memoria
				
				//System.out.println("Arquivo " + i%n + ":"); // teste
				//System.out.println("ultimaid " + ultimaId); // teste
				//imprimeArquivo(arqTemp.get(i%n), comeco); // teste
			}
			
			//System.out.println("INTERCALAÇÃO"); // teste
			
			// do {
				// do {
					// enquanto bloco nao acabou
						// para cada arquivo temporario do lado atual
							// se for a primeira passada ou for o que saiuDaFita
								// se a posicao do bloco atual for < tamanho do bloco ordenado
									// se nao tiver acabado o arquivo
										// le o registro atual
										// aumenta a posBlocoAtual
									// se tiver acabado o arquivo
										// salva o blocoAcabou
								// se a posicao do bloco atual for >= tamanho do bloco ordenado
									// salva o blocoAcabou
						// checa o menor na memoria
							// se o registro for null pula ele
						// salva o saiuDaFita
						// salva o registro no arquivo de saida
						// salva o ultimoSalvo
						// remove o registro da memoria
						// checa se todos os blocos acabaram
					// volta as posBlocoAtual para 0
					// reseta os blocos
					// troca o arquivo de saida 
					// checa se todos os registros foram lidos
				// } repete enquanto todos ultimoSalvo forem diferentes do ultimaId2 para os n arquivos atuais
				// dobra o tamBlocoOrdenado
				// troca os arquivos atuais
				// troca a fitaDeSaida
					// limpa os novos arquivos de saida
			// } repete enquanto tiver mais de um arquivo de saida com dados
			
			int[] posBlocoAtual = new int [n]; // tamanho do bloco
			int tamBlocoOrdenado = m;
			int saiuDaFita = 0;
			boolean[] blocoAcabou = new boolean[n];
			Jogador[] memoria2 = new Jogador[n];
			int fitaAtual;
			int ordem = 0;
			long[] pos = new long[n*2];
			int menor;
			int indiceMenor;
			int fitaDeSaida = n;
			boolean todosAcabaram = false;
			boolean todosRegistrosLidos;
			int naoOrdem;
			boolean temMaisDeUmComDados = true;
			int arqComDados = n;
			
			// zera o ultimoSalvo, posBlocoAtual e pos e volta o ponteiro para o comeco dos arquivos temporarios
			for(int i=0; i<n*2; i++) {
				ultimoSalvo[i] = -1;
				arqTemp.get(i).seek(comeco);
				ultimaId2[i] = arqTemp.get(i).readInt(); // salva a ultima id 
				pos[i] = arqTemp.get(i).getFilePointer();
			}
			for(int i=0; i<n; i++) {
				posBlocoAtual[i] = 0;
			}
			
			// do {
			do {
				
				// reseta os ponteiros para o comeco do arquivo
				for(int i=0; i<n*2; i++) {
					arqTemp.get(i).seek(comeco);
					arqTemp.get(i).readInt();
					pos[i] = arqTemp.get(i).getFilePointer();
				}
				
				// do {
				do {
					
					// enquanto bloco nao acabou
					while(!todosAcabaram) {
						
						// para cada arquivo temporario do lado atual
						for(int i=0; i<n; i++) {
							
							// se for a primeira passada ou for o que saiuDaFita
							fitaAtual = (ordem*n) + i;
							if(posBlocoAtual[fitaAtual%n] == 0 || fitaAtual == saiuDaFita) {
								
								// se a posicao do bloco atual for < tamanho do bloco ordenado
								if(posBlocoAtual[fitaAtual%n] < tamBlocoOrdenado) {
									
									// se nao tiver acabado o arquivo
									if(ultimoSalvo[fitaAtual] != ultimaId2[fitaAtual]) {
										
										// le o registro atual
										//System.out.print("tentou ler registro na fita " + fitaAtual + " na posição " + pos[fitaAtual] + "\nfita " + fitaAtual + ": "); // teste
										//imprimeArquivo(arqTemp.get(fitaAtual), comeco); // teste
										//System.out.println("ultimoSalvo[" + fitaAtual + "] = " + ultimoSalvo[fitaAtual] + " ultimaId2[" + fitaAtual + "] = " + ultimaId2[fitaAtual] + " posBlocoAtual = " + posBlocoAtual[fitaAtual%n]); // teste
										arqTemp.get(fitaAtual).seek(pos[fitaAtual]);
										tamRegAtual = arqTemp.get(fitaAtual).readInt();
										memoria2[i] = leRegistro(arqTemp.get(fitaAtual), comeco, pos[fitaAtual]);
										pos[fitaAtual] = arqTemp.get(fitaAtual).getFilePointer();
										// ultimoSalvo[fitaAtual] = memoria2[i].getId();
										//System.out.println("leu registro " + memoria2[i].getId() + " posBlocoAtual[" + fitaAtual%n + "] calculado = " + (posBlocoAtual[fitaAtual%n] + 1)); // teste
										
										// aumenta a posBlocoAtual
										posBlocoAtual[fitaAtual%n]++;
										//System.out.println("depois da leitura posBlocoAtual[" + fitaAtual%n + "] = " + posBlocoAtual[fitaAtual%n]); // teste
									}
									// se tiver acabado o arquivo
									else {
										
										// salva o blocoAcabou
										blocoAcabou[fitaAtual%n] = true;
									}
								}
								// se a posicao do bloco atual for >= tamanho do bloco ordenado
								else {
									
									// salva o blocoAcabou
									blocoAcabou[fitaAtual%n] = true;
								}
							}
						}
						
						//for(int i=0; i<n; i++) // teste
						//	if(memoria2[i] != null) // teste
						//		System.out.print(memoria2[i].getId() + " "); // teste
						//System.out.println(""); // teste
						
						
						/*if(memoria2[0] != null) {
							menor = memoria2[0].getId();
							indiceMenor = 0;
						} else { 
							
							// se o registro de indice 0 for null busca o proximo registro not null
							menor = 2147483647;
							indiceMenor = 2147483647;
							boolean achouProximo = false;
							for(int i=0; i<n && !achouProximo; i++) {
								if(memoria2[i] != null) {
									menor = memoria2[i].getId();
									indiceMenor = i;
									achouProximo = true;
								} else {
									menor = 2147483647;
									indiceMenor = 2147483647;
								}
							}
						}*/
						
						// checa o menor na memoria
							// se o registro for null pula ele
						menor = 2147483647;
						indiceMenor = 2147483647;
						for(int i=0; i<n; i++) {
							if(memoria2[i] != null) {
								if(memoria2[i].getId() < menor) {
									menor = memoria2[i].getId();
									indiceMenor = i;
								}
							}
						}
						
						// se achou um registro, salva ele
						if(indiceMenor != 2147483647) { 
							
							// salva o saiuDaFita
							saiuDaFita = indiceMenor + (ordem*n);
							int fitaQueFoiLida = (ordem*n) + indiceMenor; 
							
							//System.out.println("Tentou escrever registro " + menor + " de indice " + indiceMenor + " no arquivo " + fitaDeSaida); // teste
							// salva o registro no arquivo de saida
							arqTemp.get(fitaDeSaida).seek(comeco);
							arqTemp.get(fitaDeSaida).writeInt(menor);
							escreveRegistro(arqTemp.get(fitaDeSaida), pos[fitaDeSaida], memoria2[indiceMenor]);
							pos[fitaDeSaida] = arqTemp.get(fitaDeSaida).getFilePointer();
							//pos[saiuDaFita] = arqTemp.get(saiuDaFita).getFilePointer();
							
							// salva o ultimoSalvo
							ultimaId2[fitaDeSaida] = memoria2[indiceMenor].getId();
							ultimoSalvo[fitaQueFoiLida] = memoria2[indiceMenor].getId();
							//System.out.println("escreveu registro " + menor + " de indice " + indiceMenor + " no arquivo " + fitaDeSaida + ", ultimoSalvo[" + fitaQueFoiLida + "] = " + ultimoSalvo[fitaQueFoiLida]); // teste
							
							// remove o registro da memoria
							memoria2[indiceMenor] = null;
						} else {
							//System.out.println("não escreveu nenhum registro"); // teste
						}
						
						// checa se todos os blocos acabaram
						todosAcabaram = true;
						for(int i=0; i<n; i++) {
							if(!blocoAcabou[i]) {
								todosAcabaram = false;
							}
						}
						
						//System.out.println("fita de saida " + fitaDeSaida + ":"); // teste
						//imprimeArquivo(arqTemp.get(fitaDeSaida), comeco); // teste
						
					}
					
					// volta as posBlocoAtual para 0
					for(int i=0; i<n; i++) {
						posBlocoAtual[i] = 0;
						blocoAcabou[i] = false;
						//System.out.println("zerou posBloco["+i+"]"); // teste
					}
					
					// reseta os blocos
					todosAcabaram = false;
					
					// troca o arquivo de saida
					//System.out.print("fita de saida antes: " + fitaDeSaida); // teste
					if (ordem == 0) {
						naoOrdem = 1;
					} else {
						naoOrdem = 0;
					}
					fitaDeSaida = (naoOrdem*n) + ((fitaDeSaida+1)%n);
					//System.out.println(" fita de saida depois: " + fitaDeSaida); // teste
					
					// checa se todos os registros foram lidos
					todosRegistrosLidos = true;
					for(int i=0; i<n; i++) {
						if(ultimoSalvo[(ordem*n) + i] != ultimaId2[(ordem*n) + i]) {
							todosRegistrosLidos = false;
						}
						//System.out.println("final do while todosRegistrosLidos ultimoSalvo[" + ((ordem*n) + i) + "] = " + ultimoSalvo[(ordem*n) + i] + " ultimaId2[" + ((ordem*n) + i) + "] = " + ultimaId2[(ordem*n) + i]); // teste
						//sc.nextLine();
					}
					
				// } repete enquanto todos ultimoSalvo forem diferentes do ultimaId2 para os n arquivos atuais
				} while (!todosRegistrosLidos);
				
				// dobra o tamBlocoOrdenado
				tamBlocoOrdenado *= 2;
				
				// troca os arquivos atuais
				if(ordem == 0) {
					ordem = 1;
					naoOrdem = 0;
				} else {
					ordem = 0;
					naoOrdem = 1;
				}
				
				// troca a fitaDeSaida
				//System.out.print("troca grupo de fitas. fita de saida antes: " + fitaDeSaida); // teste
				fitaDeSaida = naoOrdem*n;
				//System.out.println(" fita de saida depois: " + fitaDeSaida); // teste
				
				// limpa os novos arquivos de saida
				for(int i=0; i<n; i++) {
					int arquivoAtual = (naoOrdem*n) + i; 
					arqTemp.get(arquivoAtual).setLength(0);
					arqTemp.get(arquivoAtual).writeInt(-1);
					pos[arquivoAtual] = arqTemp.get(arquivoAtual).getFilePointer();
					//System.out.println("limpou o ultimoSalvo do arquivo " + ((ordem*n) + i)); // teste
					ultimoSalvo[(ordem*n) + i] = -1; // ultimo salvo do arquivo de entrada
					ultimaId2[arquivoAtual] = -1;
				}
				
				// checa se tem mais de um com dados
				temMaisDeUmComDados = false;
				int numComDados = 0;
				for(int i=0; i<n; i++) {
					int arquivoAtual = (ordem*n) + i;
					
					//System.out.println("arquivo " + arquivoAtual + ":"); // teste
					//imprimeArquivo(arqTemp.get(arquivoAtual), comeco); // teste
					//System.out.println("length = " + arqTemp.get(arquivoAtual).length()); // teste
					if(arqTemp.get(arquivoAtual).length() > 4) {
						numComDados++;
						arqComDados = arquivoAtual;
						//System.out.println("arquivo " + (arquivoAtual) + " tem dados"); // teste
						arquivoFinal = arquivoAtual;
					}
					// sc.nextLine(); // teste
					
					// reseta os ponteiros para o comeco do arquivo
					arqTemp.get(arquivoAtual).seek(comeco);
					arqTemp.get(arquivoAtual).readInt();
					pos[arquivoAtual] = arqTemp.get(arquivoAtual).getFilePointer();
				}
				if(numComDados > 1) {
					temMaisDeUmComDados = true;
				} 
				
			// } repete enquanto tiver mais de um arquivo de saida com dados
			} while (temMaisDeUmComDados);
			//System.out.println("arquivo final com dados: "); // teste
			//imprimeArquivo(arqTemp.get(arqComDados), comeco); // teste
			
			// escreve sobre o arquivo de dados
			copiaArquivo(arqTemp.get(arquivoFinal), comeco, arq);
			System.out.println("\nArquivo após a ordenação:");
			imprimeArquivo(arq, comeco);
			System.out.println("\nAperte enter para continuar.");
			sc.nextLine();
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
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
                 case "5":
                     intercalacaoBalanceada2(arquivodb, 0);
                     break;
                    //  System.out.println("\nOpção inválida. Tente novamente.\n\n");
                    //  break;
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