package Classes;

import java.io.IOException;

public class Kmp {

    public static void KMPSearch(String pat) throws IOException
    {
        String txt = "";
        int M = pat.length();
        int N = 0;
        Arquivo.fileReader.seek(0);
        int ultimoId = Arquivo.fileReader.readInt(),controle = 1;
        Arquivo.fileReader.seek(0);

        while(controle <= ultimoId){//pega os dados de todos os jogadores do arquivo.db
            txt += lzw.pesquisa(controle);
            controle++;
        }

        N = txt.length();
        // criar lps[] que irá manter o mais longo
        // valores de sufixo de prefixo para padrão
        int lps[] = new int[M];
        int j = 0; // index for pat[]
 
        // Pré-processar o padrão (calcular lps[]
        // variedade)
        computeLPSArray(pat, M, lps);
 
        int i = 0; // index for txt[]
        while ((N - i) >= (M - j)) {
            if (pat.charAt(j) == txt.charAt(i)) {
                j++;
                i++;
            }
            if (j == M) {
                System.out.println("Found pattern "
                                   + "at index " + (i - j));
                j = lps[j - 1];
            }
 
            // incompatibilidade após j correspondências
            else if (i < N
                     && pat.charAt(j) != txt.charAt(i)) {
                // Não corresponde aos caracteres lps[0..lps[j-1]],
                // eles vão combinar de qualquer maneira
                if (j != 0)
                    j = lps[j - 1];
                else
                    i = i + 1;
            }
        }
    }
 
    public static void computeLPSArray(String pat, int M, int lps[])
    {
        // comprimento do sufixo de prefixo mais longo anterior
        int len = 0;
        int i = 1;
        lps[0] = 0; // lps[0] é sempre 0
 
        // o loop calcula lps[i] para i = 1 a M-1
        while (i < M) {
            if (pat.charAt(i) == pat.charAt(len)) {
                len++;
                lps[i] = len;
                i++;
            }
            else // (pat[i] != pat[len])
            {
                // Isso é complicado. Considere o exemplo.
                // AAACAAAA e i = 7. A ideia é semelhante
                // para a etapa de busca.
                if (len != 0) {
                    len = lps[len - 1];
 
                    // Além disso, observe que não incrementamos
                    // eu aqui
                }
                else // if (len == 0)
                {
                    lps[i] = len;
                    i++;
                }
            }
        }
    }
}