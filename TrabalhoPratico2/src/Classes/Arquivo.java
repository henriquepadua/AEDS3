package Classes;

import java.io.*;
import java.net.CacheRequest;
import java.util.Date;
import java.util.Scanner;

public class Arquivo{
    private File arquivo;
    public static RandomAccessFile fileReader;
    private static long posicao;
    final int cabecalho = 4;

    private static ListaInvertida listaInvertida = new ListaInvertida();

    public Arquivo(String arquivo)throws IOException{
        this.arquivo = new File(arquivo);
        fileReader = new RandomAccessFile(arquivo, "rw");
        if(fileReader.length() == 0) fileReader.writeInt(0);
    }

    public Arquivo(){}

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
        int tamanhoString = 0;double value = 0;byte overall = 0;

        System.out.print("Jogador#" +id + "=");
        jogador.setLapide(lapide);
        jogador.setId(id);
        tamanhoString = fileReader.readInt();
        jogador.setKnownAs(s = fileReader.readUTF());
        tamanhoString = fileReader.readInt();
        System.out.print(s);
        jogador.setFullName(s = fileReader.readUTF());
        System.out.print(","+s);
        jogador.setOverall( overall = fileReader.readByte());
        System.out.print(","+overall);
        jogador.setValue(value = fileReader.readDouble());
        System.out.print(","+ value);
        tamanhoString = fileReader.readInt();
        jogador.setBestPosition(s = fileReader.readUTF());
        System.out.print(","+s);
        tamanhoString = fileReader.readInt();
        jogador.setNacionality(s = fileReader.readUTF());
        System.out.print(","+s);
        jogador.setAge(overall = fileReader.readByte());
        System.out.println(","+overall);
        tamanhoString = fileReader.readInt();
        jogador.setClubName(s = fileReader.readUTF());
        System.out.print(","+s);
        tamanhoString = fileReader.readInt();
        jogador.setJoinedOn(s = fileReader.readUTF());
        System.out.println(","+s);


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
        //src/Dados/ListaInvertidaNome.db

        //adiciona o registro ao arquivo hash
        //adicionaHash(comeco,novaConta,endFinal);

        String idString = jogador.getId() + "";
				listaInvertida.createArqLista(jogador.getKnownAs(), Byte.parseByte(idString), "src/Dados/ListaInvertidaKnownAs.db");
				listaInvertida.createArqLista(jogador.getNacionality(), Byte.parseByte(idString), "src/Dados/ListaInvertidaNationality.db");
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
            String idString = jogador.getId() + "";
            listaInvertida.updateLista(jogador.getKnownAs(), Byte.parseByte(idString), "src/Dados/ListaInvertidaKnownAs.db",false);
            listaInvertida.updateLista(jogador.getNacionality(), Byte.parseByte(idString), "src/Dados/ListaInvertidaNationality.db",false);
        }

        //System.out.println("Não foi possível encontrar Jogador, seu Jogador foi deletado ou não existe!! Favor verificar seus dados");
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

            String idString = id + "";
            listaInvertida.DeleteAllIdForList(Byte.parseByte(idString), "src/Dados/ListaInvertidaKnownAs.db"); // remove o registro da lista invertida
			listaInvertida.DeleteAllIdForList(Byte.parseByte(idString), "src/Dados/ListaInvertidasetNationality.db"); // remove o registro da lista invertida
        } catch (Exception e){
            e.printStackTrace();
        }   
    }

    public static void buscaListaInvertida(Scanner sc) {
		// pergunta se o usuario quer apelido ou nacionalidade
		System.out.println("\n=== BUSCAR IDS POR LISTA INVERTIDA ===\n");
		System.out.println("Você quer buscar por KnownAs ou Nationality? 1) KnownAs - 2) Nationality");
		int tipo = sc.nextInt();
		sc.nextLine();
		
		if (tipo == 1) { // se for por apelido
			// pede o apelido desejado ao usuario
			System.out.println("\nDigite o apelido desejado:");
			String KnownAs = sc.nextLine();
			
			// chama a funcao de busca passando o apelido desejado e o arquivo correto
			listaInvertida.searchList(KnownAs, "src/Dados/ListaInvertidaKnownAs.db");
			
		} else if (tipo == 2) { // se for por Nacionalidade
			// pede a Nacionalidade desejada ao usuario
			System.out.println("\nDigite a Nationality desejada:");
			String Nationality = sc.nextLine();
			
			// chama a funcao de busca passando a cidade desejada e o arquivo correto
			listaInvertida.searchList(Nationality, "src/Dados/ListaInvertidaNationality.db");
		} else { // se digitou invalido
			System.out.println("\nOpção inválida. Aperte enter para continuar.\n");
			sc.nextLine();
			return;
		}
		
		System.out.println("\nAperte enter para continuar.\n");
		sc.nextLine();
		
		return;
	}

    public static Jogador leJogadorHash(RandomAccessFile arq, long comeco, long pos0) throws Exception{
        Jogador jogador = new Jogador();
        String s = "";
        Date date = jogador.getJoinedOn();
        int tamanhoString = 0;double value = 0;byte overall = 0;
        int id = 0;

        arq.readByte();
		id = arq.readInt();
        tamanhoString = fileReader.readInt();
        jogador.setKnownAs(s = fileReader.readUTF());
        tamanhoString = fileReader.readInt();
        System.out.print(s);
        jogador.setFullName(s = fileReader.readUTF());
        System.out.print(","+s);
        jogador.setOverall( overall = fileReader.readByte());
        System.out.print(","+overall);
        jogador.setValue(value = fileReader.readDouble());
        System.out.print(","+ value);
        tamanhoString = fileReader.readInt();
        jogador.setBestPosition(s = fileReader.readUTF());
        System.out.print(","+s);
        tamanhoString = fileReader.readInt();
        jogador.setNacionality(s = fileReader.readUTF());
        System.out.print(","+s);
        jogador.setAge(overall = fileReader.readByte());
        System.out.println(","+overall);
        tamanhoString = fileReader.readInt();
        jogador.setClubName(s = fileReader.readUTF());
        System.out.print(","+s);
        tamanhoString = fileReader.readInt();
        jogador.setJoinedOn(s = fileReader.readUTF());
        System.out.println(","+s);


        return jogador;
    }

    public static void criaHash(long comeco) throws Exception { // cria o arquivo de hash inicial
		// abre o arquivo de indice hash
		// limpa o arquivo de indice hash
		// abre o arquivo de diretorio
		// limpa o arquivo de diretorio
		// inicializa os arquivos de indice e diretorio com p=1 e buckets vazios
		// para cada registro no arquivo
			// le o registro
			// ve no diretorio em qual bucket vai cair
			// acessa o bucket
			// se o bucket ja estiver cheio
				// se a profundidade local for menor que a profundidade global
					// aumenta a profundidade local
					// cria um bucket novo
					// troca o ponteiro da segunda metade de % atual para o bucket novo ((numBucket >> Pl-1)%2)==1(segunda metade) && (bits da direita iguais)(numBucket%2^(p-1))==(idAtual%2^(p-1)) 
					// rebalanceia o bucket atual com o seu segundo %
					// insere o registro no bucket equivalente
				// se a profundidade local for igual a profundidade global
					// aumenta a profundidade local
					// aumenta a profundidade global
					// dobra o tamanho do diretorio
					// copia os ponteiros antigos do diretorio para os campos novos
					// cria um bucket novo
					// troca o ponteiro do segundo registro de % atual para o bucket novo
					// rebalanceia o bucket atual com o seu segundo %
					// insere o registro no bucket equivalente
			// se o bucket nao estiver cheio
				// insere o registro no bucket
		// end while
		
		try {
			
			int ultimaId;
			int idAtual;
			Jogador contaTemp;
			int tamRegAtual;
			long pos0, pos1, posBucketNovo;
			long endDir;
			int profGlobal;
			int profLocal;
			int numRegs;
			byte lapide;
			int ultimoBucket;
			double tamDir;
						
			// abre o arquivo de indice hash
			RandomAccessFile arqHash = new RandomAccessFile("src/dados/hash.db", "rw");
			
			// limpa o arquivo de indice hash
			arqHash.setLength(0);
			
			// abre o arquivo de diretorio
			RandomAccessFile arqDir = new RandomAccessFile("src/dados/diretorio.db", "rw");
			
			// limpa o arquivo de diretorio
			arqDir.setLength(0);
			
			// inicializa os arquivos de indice e diretorio com p=1 e buckets vazios
			arqDir.seek(comeco);
			arqDir.writeInt(1); // profundidade global
			profGlobal = 1;
			arqHash.seek(comeco);
			arqHash.writeInt(1); // numero ultimo bucket
			for(int i=0; i<2; i++) {
				arqDir.writeLong(arqHash.getFilePointer()); // escreve o endereco do bucket correspondente
				arqHash.writeInt(1); // profundidade local
				arqHash.writeInt(0); // numero de elementos
				for(int j=0; j<4; j++) {
					arqHash.writeInt(-1); // chave
					arqHash.writeLong(-1); // endereco
				}
			}
						
			
			// para cada registro no arquivo
			fileReader.seek(comeco);
			ultimaId = fileReader.readInt();
			idAtual = -1;
			while (idAtual != ultimaId) {
				
				// le o registro
				pos1 = fileReader.getFilePointer(); // endereco antes do tamanho do registro, eh o que sera gravado no diretorio
				tamRegAtual = fileReader.readInt();
				pos0 = fileReader.getFilePointer();
				lapide = fileReader.readByte();
				fileReader.seek(pos0);
				contaTemp = leJogadorHash(fileReader, comeco, pos0);
				if(lapide != '*') {
					idAtual = contaTemp.getId();
					
					// ve no diretorio em qual bucket vai cair
					endDir = getEndDir(arqDir, comeco, idAtual);
					
					// acessa o bucket
					arqHash.seek(endDir);
					profLocal = arqHash.readInt();
					numRegs = arqHash.readInt();
					
					// se o bucket ja estiver cheio
					if(numRegs == 4) {
						
						// se a profundidade local for menor que a profundidade global
						if(profLocal < profGlobal) {
							
							// aumenta a profundidade local
							profLocal += 1;
							arqHash.seek(endDir);
							arqHash.writeInt(profLocal);
							
							// cria um bucket novo
							arqHash.seek(comeco);
							ultimoBucket = arqHash.readInt();
							for(int i=0; i<=ultimoBucket; i++) { // pula todos os buckets existentes
								arqHash.skipBytes(56);
							}
							posBucketNovo = arqHash.getFilePointer(); // endereco do bucket novo
							arqHash.writeInt(profLocal);
							arqHash.writeInt(0); // numero de registros
							for(int j=0; j<4; j++) { // escreve valores -1
								arqHash.writeInt(-1); // chave
								arqHash.writeLong(-1); // endereco
							}
							
							// troca o ponteiro da segunda metade de % atual para o bucket novo ((numBucket >> Pl-1)%2)==1(segunda metade) && (bits da direita iguais)(numBucket%2^(p-1))==(idAtual%2^(p-1))
							arqDir.seek(comeco);
							profGlobal = arqDir.readInt(); // profundidade global
							tamDir = Math.pow(2, profGlobal);
							for(int i=0; i<tamDir; i++) {
								if(((i >> profLocal-1) % 2) == 1 && (i % Math.pow(2, (profGlobal-1))) == (idAtual % Math.pow(2, (profGlobal-1)))) { // se a id atual for parte da 2a metade das ids que pertencem ao grupo que contem ponteiros para o mesmo bucket 
									arqDir.writeLong(posBucketNovo); // grava a posicao do novo bucket
								}
								else { // senao, pula para o proximo
									arqDir.skipBytes(8);
								}
							}
							
							// rebalanceia o bucket atual com o seu segundo %
								
								// le os registros
							int[] chave = new int [5];
							long[] endereco = new long [5];
							arqHash.seek(endDir);
							arqHash.readInt(); // pula a profundidade
							arqHash.readInt(); // pula o numero de elementos (eh 4)
							for(int i=0; i<4; i++) {
								chave[i] = arqHash.readInt();
								endereco[i] = arqHash.readLong();
							}
								// define em qual posicao vai ficar
							int novaPosicao = 4; // se for maior que todos a posicao eh a ultima
							for(int i=0; i<4; i++) {
								if(idAtual < chave[i]) {
									novaPosicao = i; // se for menor que a chave[i] entao achou em qual posicao devera ficar
									i = 4; // break
								} else if(idAtual == chave[i]) {
									System.out.println("Erro: ID duplicada");
									return;
								}
							}
							
								// insere o registro e remaneja os existentes
							for(int i=3; i>=novaPosicao; i--) {
								chave[i+1] = chave[i];
								endereco[i+1] = endereco[i];
							}
							chave[novaPosicao] = idAtual;
							endereco[novaPosicao] = pos1;
							
							// insere os registros nos buckets equivalentes
							long posPrimeiro, posSegundo;
							arqHash.seek(endDir); // vai para a posicao do primeiro bucket
							arqHash.readInt(); // pula a profundidade
							posPrimeiro = arqHash.getFilePointer();
							arqHash.writeInt(0); // numero de elementos vai para zero para ser aumentado depois
							arqHash.seek(posBucketNovo); // vai para a posicao do segundo bucket
							arqHash.readInt(); // pula a profundidade
							posSegundo = arqHash.getFilePointer();
							arqHash.writeInt(0); // numero de elementos vai para zero para ser aumentado depois
							int numRegs2;
							for(int i=0; i<5; i++) {
								if(chave[i] % Math.pow(2, profLocal-1) == chave[i] % Math.pow(2, profLocal)) { // se a chave pertencer ao primeiro bucket, adiciona a chave ao primeiro bucket e aumenta o numero de elementos
									arqHash.seek(posPrimeiro);
									numRegs2 = arqHash.readInt();
									arqHash.seek(posPrimeiro);
									arqHash.writeInt(numRegs2 + 1); // aumenta o numero de elementos
									arqHash.skipBytes(12 * numRegs2); // pula os registros que ja foram inseridos
									arqHash.writeInt(chave[i]); // escreve a chave
									arqHash.writeLong(endereco[i]); // escreve o endereco
									
								} else { // se a chave pertencer ao segundo bucket, adiciona a chave ao segundo bucket e aumenta o numero de elementos
									arqHash.seek(posSegundo);
									numRegs2 = arqHash.readInt();
									arqHash.seek(posSegundo);
									arqHash.writeInt(numRegs2 + 1); // aumenta o numero de elementos
									arqHash.skipBytes(12 * numRegs2); // pula os registros que ja foram inseridos
									arqHash.writeInt(chave[i]); // escreve a chave
									arqHash.writeLong(endereco[i]); // escreve o endereco
								}
							}
							
							// aumenta o ultimoBucket
							ultimoBucket += 1;
							arqHash.seek(comeco);
							arqHash.writeInt(ultimoBucket);
						}
						
						// se a profundidade local for igual a profundidade global
						else {
							
							// aumenta a profundidade local
							profLocal += 1;
							arqHash.seek(endDir);
							arqHash.writeInt(profLocal);
							
							// aumenta a profundidade global
							profGlobal += 1;
							arqDir.seek(comeco);
							arqDir.writeInt(profGlobal);
							
							// dobra o tamanho do diretorio
							tamDir = Math.pow(2, profGlobal);
							
							// copia os ponteiros antigos do diretorio para os campos novos
							long posMetade1 = arqDir.getFilePointer();
							arqDir.skipBytes((int) ((tamDir/2) * 8));
							long posMetade2 = arqDir.getFilePointer();
							long endAtual;
							for(int i=0; i<tamDir/2; i++) { // varre o arquivo de diretorio copiando os enderecos para a segunda metade
								arqDir.seek(posMetade1);
								endAtual = arqDir.readLong();
								posMetade1 = arqDir.getFilePointer();
								arqDir.seek(posMetade2);
								arqDir.writeLong(endAtual);
								posMetade2 = arqDir.getFilePointer();
							}
							
							// cria um bucket novo
							arqHash.seek(comeco);
							ultimoBucket = arqHash.readInt();
							for(int i=0; i<=ultimoBucket; i++) { // pula todos os buckets existentes
								arqHash.skipBytes(56);
							}
							posBucketNovo = arqHash.getFilePointer(); // endereco do bucket novo
							arqHash.writeInt(profLocal);
							arqHash.writeInt(0); // numero de registros
							for(int j=0; j<4; j++) { // escreve valores -1
								arqHash.writeInt(-1); // chave
								arqHash.writeLong(-1); // endereco
							}
							
							// aumenta o ultimoBucket
							ultimoBucket += 1;
							arqHash.seek(comeco);
							arqHash.writeInt(ultimoBucket);
							
							// troca o ponteiro do segundo registro de % atual para o bucket novo
							arqDir.seek(comeco);
							arqDir.readInt(); // pula a profundidade global
							arqDir.skipBytes((int) ((idAtual % Math.pow(2, profGlobal-1)) + Math.pow(2, profGlobal-1)) * 8); // pula para o segundo registro de % atual
							arqDir.writeLong(posBucketNovo);
							
							// rebalanceia o bucket atual com o seu segundo %
							
								// le os registros
							int[] chave = new int [5];
							long[] endereco = new long [5];
							arqHash.seek(endDir);
							arqHash.readInt(); // pula a profundidade
							arqHash.readInt(); // pula o numero de elementos (eh 4)
							for(int i=0; i<4; i++) {
								chave[i] = arqHash.readInt();
								endereco[i] = arqHash.readLong();
							}
								// define em qual posicao vai ficar
							int novaPosicao = 4; // se for maior que todos a posicao eh a ultima
							for(int i=0; i<4; i++) {
								if(idAtual < chave[i]) {
									novaPosicao = i; // se for menor que a chave[i] entao achou em qual posicao devera ficar
									i = 4; // break
								} else if(idAtual == chave[i]) {
									System.out.println("Erro: ID duplicada");
									return;
								}
							}
							
								// insere o registro e remaneja os existentes
							for(int i=3; i>=novaPosicao; i--) {
								chave[i+1] = chave[i];
								endereco[i+1] = endereco[i];
							}
							chave[novaPosicao] = idAtual;
							endereco[novaPosicao] = pos1;
							
							// insere os registros nos buckets equivalentes
							long posPrimeiro, posSegundo;
							arqHash.seek(endDir); // vai para a posicao do primeiro bucket
							arqHash.readInt(); // pula a profundidade
							posPrimeiro = arqHash.getFilePointer();
							arqHash.writeInt(0); // numero de elementos vai para zero para ser aumentado depois
							arqHash.seek(posBucketNovo); // vai para a posicao do segundo bucket
							arqHash.readInt(); // pula a profundidade
							posSegundo = arqHash.getFilePointer();
							arqHash.writeInt(0); // numero de elementos vai para zero para ser aumentado depois
							int numRegs2;
							for(int i=0; i<5; i++) {
								if(chave[i] % Math.pow(2, profLocal-1) == chave[i] % Math.pow(2, profLocal)) { // se a chave pertencer ao primeiro bucket, adiciona a chave ao primeiro bucket e aumenta o numero de elementos
									arqHash.seek(posPrimeiro);
									numRegs2 = arqHash.readInt();
									arqHash.seek(posPrimeiro);
									arqHash.writeInt(numRegs2 + 1); // aumenta o numero de elementos
									arqHash.skipBytes(12 * numRegs2); // pula os registros que ja foram inseridos
									arqHash.writeInt(chave[i]); // escreve a chave
									arqHash.writeLong(endereco[i]); // escreve o endereco
								} else { // se a chave pertencer ao segundo bucket, adiciona a chave ao segundo bucket e aumenta o numero de elementos
									arqHash.seek(posSegundo);
									numRegs2 = arqHash.readInt();
									arqHash.seek(posSegundo);
									arqHash.writeInt(numRegs2 + 1); // aumenta o numero de elementos
									arqHash.skipBytes(12 * numRegs2); // pula os registros que ja foram inseridos
									arqHash.writeInt(chave[i]); // escreve a chave
									arqHash.writeLong(endereco[i]); // escreve o endereco
								}
							}
						}
					}
					
					// se o bucket nao estiver cheio
					else {
						
						// insere o registro no bucket
						
							// le os registros
						int[] chave = new int [4];
						long[] endereco = new long [4];
						for(int i=0; i<numRegs; i++) {
							chave[i] = arqHash.readInt();
							endereco[i] = arqHash.readLong();
						}
						
							// define em qual posicao vai ficar
						int novaPosicao = numRegs; // se for maior que todos a posicao eh a ultima
						for(int i=0; i<numRegs; i++) {
							if(idAtual < chave[i]) {
								novaPosicao = i; // se for menor que a chave[i] entao achou em qual posicao devera ficar
								i = numRegs; // break
							} else if(idAtual == chave[i]) {
								System.out.println("Erro: ID duplicada");
								return;
							}
						}
						
							// insere o registro e remaneja os existentes
						if(numRegs > 0) {
							for(int i=numRegs-1; i>=novaPosicao; i--) {
								chave[i+1] = chave[i];
								endereco[i+1] = endereco[i];
							}
							chave[novaPosicao] = idAtual;
							endereco[novaPosicao] = pos1;
							numRegs += 1;
						} else {
							chave[0] = idAtual;
							endereco[0] = pos1;
							numRegs = 1;
						}
						
							// aumenta o numero de elementos no bucket
						arqHash.seek(endDir);
						arqHash.readInt(); // profundidade local
						arqHash.writeInt(numRegs);
						
							// grava o bucket novo
						for(int i=0; i<numRegs; i++) {
							arqHash.writeInt(chave[i]);
							arqHash.writeLong(endereco[i]);
						}
					}
				}
			// end while
			}
				
			// imprimeArqDir(arqDir, comeco);
			// imprimeArqHash(arqHash, comeco);
			// System.out.println("\nArquivo inicial de hash criado.\n\nAperte enter para continuar.");
			// sc.nextLine();
			arqHash.close();
			arqDir.close();

		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
		
		return;
	}

    public static long getEndDir(RandomAccessFile arqDir,long comeco,int idDesejada){//varre o arquivo de diretorio buscando o registro de id selecionado e retorna o endereco do bucket correspondente
        long endDir = -1;
        try{
            arqDir.seek(comeco);
            int profGlobal = arqDir.readInt();
            double tamDir = Math.pow(2,profGlobal);
            // para cada valor no diretorio
            for(int i=0; i<tamDir;i++){
                //checa se eh o bucket da id desejada
                if(i == idDesejada % tamDir){
                    //salva o endereco do bucket
                    endDir = arqDir.readLong();
                }else{
                    //pula para o proximo
                    arqDir.readLong();
                }
            }
        } catch(IOException e){
            System.out.println(e.getMessage());
        }
        return endDir;
    }
}