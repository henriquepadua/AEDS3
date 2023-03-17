import Classes.*;

public class Main {

    public static void main(String[] args) {
        Arquivo arquivo;
        Csv arquivoCsv = new Csv();

        try{
            arquivo = new Arquivo("src/Jogadores.db");

            arquivoCsv.lendoArquivo();

            Menu menu = new Menu();
            menu.exibeMenu();

        }catch (Exception e){
            e.printStackTrace();
        }
    }
}