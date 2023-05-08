package Classes;

import java.io.*;

public class Csv extends Arquivo{

    static String path = "src/csv/Fifa 23 Players Data.csv";

    public void lendoArquivo() throws Exception{

        FileReader fileReader = new FileReader(path);
        BufferedReader br = new BufferedReader(fileReader);
        String linha = "";
        int id = 0;

        br.readLine(); // Ignora a primeira linha do csv
        linha = br.readLine();

        while(linha != null){
            Jogador novoJogador = new Jogador();
            id++;

            String[] array = linha.split(",");
            novoJogador.setId(id);

            String knowAs = array[0];
            novoJogador.setKnownAs(knowAs);

            String fullName = array[1];
            novoJogador.setFullName(fullName);

            Byte overall = Byte.parseByte(array[2]);
            novoJogador.setOverall(overall);

            double value = Double.parseDouble(array[3]);
            novoJogador.setValue(value);

            String bestPosition = array[4];
            novoJogador.setBestPosition(bestPosition);

            String nacionality = array[5];
            novoJogador.setNacionality(nacionality);

            Byte age = Byte.parseByte(array[6]);
            novoJogador.setAge(age);

            String clubName = array[7];
            novoJogador.setClubName(clubName);
            
            String dateString = array[8];   
            novoJogador.setJoinedOn(dateString);
            
            //Jogador novoJogador = new Jogador(id, knowAs, fullName, overall, value, bestPosition, nacionality, age, clubName);
            Arquivo arquivo = new Arquivo();
            var teste = Arquivo.fileReader;

            teste.seek(0);
            int ultimoId = teste.readInt();
            
            if(ultimoId < 22)  arquivo.create(novoJogador);
            else break;

            linha = br.readLine();
        }

        br.close();
        fileReader.close();
    }

}
