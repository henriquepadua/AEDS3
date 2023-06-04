package Classes;

import java.io.IOException;

public class BooyerMoore {
     
    static int NO_OF_CHARS = 256;
     
   //Uma função de utilidade para obter o máximo de dois inteiros
    public static int maximo (int a, int b) { return (a > b)? a: b; }

    //A função de pré-processamento para Boyer Moore
    // heurística de mau caráter
    public static void badCharHeuristica( char []str, int size,int badchar[])
    {

     // Inicializa todas as ocorrências como -1
     for (int i = 0; i < NO_OF_CHARS; i++)
          badchar[i] = -1;

     // Preencha o valor real da última ocorrência
     // de um caractere (índices de tabela são ascii e valores são índice de ocorrência)
     for (int i = 0; i < size; i++)
          badchar[(int) str[i]] = i;
    }

    /* Uma função de busca de padrão que usa Bad
    Heurística de Caracteres do Algoritmo de Boyer Moore */
    public static void pesquisa( char pat[]) throws IOException
    {
     String texto = "";
     int m = pat.length;
     int n = 0;
     Arquivo.fileReader.seek(0);
     int ultimoId = Arquivo.fileReader.readInt(),controle = 1;
     Arquivo.fileReader.seek(0);

     while(controle <= ultimoId){//pega os dados de todos os jogadores do arquivo.db
        texto += lzw.pesquisa(controle);
        controle++;
     }   

     n = texto.length();
     char txt[] = texto.toCharArray();
     int badchar[] = new int[NO_OF_CHARS];

     /* Preencha a matriz de caracteres inválidos chamando
        a função de pré-processamento badCharHeuristica()
        para determinado padrão */
     badCharHeuristica(pat, m, badchar);

     int s = 0;  // s é o deslocamento do padrão com
                // respeita o texto
            //existem n-m+1 alinhamentos potenciais
     while(s <= (n - m))
     {
         int j = m-1;

         /*Continue reduzindo o índice j do padrão enquanto
            caracteres de padrão e texto são
            combinando neste turno s */
         while(j >= 0 && pat[j] == txt[s+j])
             j--;

         /* Se o padrão estiver presente na corrente
            deslocamento, então o índice j se tornará -1 após
            o loop acima */
         if (j < 0)
         {
             System.out.println("padroes encontrados no shift = " + s);

             /* Mude o padrão para que o próximo
                caractere no texto se alinha com o último
                ocorrência dele no padrão.
                A condição s+m < n é necessária para
                o caso quando o padrão ocorre no final
                de texto */
             //txt[s+m] é o caractere após o padrão no texto
             s += (s+m < n)? m-badchar[txt[s+m]] : 1;

         }

         else
             /* Mude o padrão para que o mau caráter
                no texto se alinha com a última ocorrência de
                isso em padrão. A função maximo é usada para
                certifique-se de obter uma mudança positiva.
                Podemos obter uma mudança negativa se o último
                ocorrência de mau caráter no padrão
                está do lado direito da corrente
                personagem. */
             s += maximo(1, j - badchar[txt[s+j]]);
     }
    }
}