import org.la4j.Matrix;
import org.la4j.decomposition.EigenDecompositor;
import org.la4j.matrix.dense.Basic2DMatrix;
import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class main_aplicacao {
    static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) throws IOException, InterruptedException {
        int ngenerations = 0;
        int tipoformato = 0;
        String nomeficheiroEntrada = "", nomeficheiroSaida = "", nomepopulacao="";
        boolean ValorVetorProprio = false, Dimensaopopulacao = false, TaxaVariacao = false;
        Scanner ler = new Scanner(System.in);
        if (args.length == 0) {
            //----
        } else if (args.length != 0) {
            if (args[0].equals("-n")) {
                if (args.length == 2 && args[1].endsWith(".txt")) {
                    nomeficheiroEntrada = args[1];
                } else if (args.length < 2 || args.length > 2 || !args[1].endsWith(".txt")) {
                    System.out.println("Insira um argumento valido!");
                }
            } else if (args.length >= 6 && args.length <= 9) {
                for(int i=0;i< args.length;i++){
                    if(args[i].equals("-t") && i+1< args.length && args[i+1].matches("^[0-9]*$") && Integer.parseInt(args[i+1]) > 0){
                        for(int j=0;j< args.length;j++){
                            if(args[j].equals("-g") && j+1< args.length && args[i+1].matches("^[0-9]*$") && Integer.parseInt(args[j+1]) >= 1 && Integer.parseInt(args[j+1])<=3){
                                if(args[args.length-2].endsWith(".txt") && args[args.length-1].endsWith(".txt")){
                                    if(args.length==6){
                                        ngenerations = Integer.parseInt(args[i+1]);
                                        nomeficheiroEntrada = args[args.length - 2];
                                        nomeficheiroSaida = args[args.length - 1];
                                        tipoformato = Integer.parseInt(args[j+1]);
                                    }else{
                                        ngenerations = Integer.parseInt(args[i+1]);
                                        nomeficheiroEntrada = args[args.length - 2];
                                        nomeficheiroSaida = args[args.length - 1];
                                        tipoformato = Integer.parseInt(args[j+1]);
                                        for (int k = 0; k < args.length; k++) {
                                            if (args[k].equals("-e")) {
                                                ValorVetorProprio = true;
                                            }
                                            if (args[k].equals("-v")) {
                                                Dimensaopopulacao = true;
                                            }
                                            if (args[k].equals("-r")) {
                                                TaxaVariacao = true;
                                            }
                                        }
                                        if(ValorVetorProprio == false && Dimensaopopulacao == false && TaxaVariacao == false){
                                            System.out.println("Insira um argumento valido!");
                                            //tem parametros errados a mais
                                        }
                                    }
                                }else{
                                    System.out.println("Insira um argumento valido!");
                                    //nao tem ficheiros ou estao no sitio errado
                                }
                            }else if(j==args.length){
                                System.out.println("Insira um argumento valido!");
                                //nao tem o g
                            }
                        }
                    }else if(i==args.length){
                        System.out.println("Insira um argumento valido!");
                        //nao tem o t
                    }
                }
            }else{
                System.out.println("Insira um argumento valido!");
                //ou nao tem parametros sufecientes ou tem a mais
            }
        }
        String resposta = "";
        if (nomeficheiroSaida.equals("") || nomeficheiroEntrada.equals("")) {
            int c = 200;
            System.out.println("Bem-vindo à aplicação!");
            System.out.println();
            System.out.println("Insira primeiro os dados necessários para as simulações:");
            double[] vetor = LerVetor(c, nomeficheiroEntrada);
            int tamanho = vetor.length;
            double[] Sobrev = Sobrev(tamanho, nomeficheiroEntrada);
            double[] Fecun = Fecun(tamanho, nomeficheiroEntrada);
            double[][] matriz = matriz(tamanho, Sobrev, Fecun);
            double[] vetoreigenreal=vetoreigenreal(matriz);
            double[] vetoreigen = vetoreigen(matriz);
            System.out.println("Introduza o nome da população a estudar:");
            Scanner in = new Scanner(System.in);
            nomepopulacao = in.nextLine();

            do{
                System.out.println("Escolha agora o que pretende visualizar:");
                System.out.println("1- Distribuição da população | Distribuição normalizada");
                System.out.println("2- Matriz de Leslie");
                System.out.println("3- Comportamento assintótico");
                System.out.println("4- Representações gráficas");
                System.out.println("5- Estudar nova população");
                System.out.println("6 -Sair");
                Scanner scan = new Scanner(System.in);
                resposta = scan.nextLine();
                switch (resposta) {
                    case "1" -> {
                        System.out.println("Quantas gerações pretende prever?");
                        ngenerations = sc.nextInt();
                        if (ngenerations >= 0) {
                            Printgeracoes(ngenerations, matriz, vetor);
                        } else {
                            System.out.println("Número de gerações inválido! Tente novamente.");
                        }
                    }
                    case "2" -> {
                        System.out.println("Matriz de Leslie");
                        escrever(matriz);
                    }
                    case "3" -> {
                        System.out.printf("Maior Valor próprio = %.4f\n", valorProprio(matriz));
                        System.out.println("Vetor Próprio associado:");
                        PrintVetorDecimal(vetoreigen);
                    }
                    case "4" -> {
                        int interativo = 1;
                        gnuplot(Dimensaopopulacao, TaxaVariacao, interativo, ngenerations, tipoformato, vetor.length, matriz, Fecun, Sobrev, vetor,nomepopulacao);
                    }
                    case "5" -> {
                        System.out.println("Pretende inserir um ficheiro de entrada? (s/n)");
                        Scanner input = new Scanner(System.in);
                        String a = input.nextLine();
                        switch (a) {
                            case "s", "S","sim","SIM" -> {
                                System.out.println("Insira o nome do ficheiro de entrada");
                                nomeficheiroEntrada = input.nextLine();
                                vetor = LerVetor(c, nomeficheiroEntrada);
                                tamanho = vetor.length;
                                Sobrev = Sobrev(tamanho,nomeficheiroEntrada);
                                Fecun = Fecun(tamanho, nomeficheiroEntrada);
                                matriz = matriz(tamanho, Sobrev, Fecun);
                                vetoreigenreal=vetoreigenreal(matriz);
                                vetoreigen = vetoreigen(matriz);
                                System.out.println("Introduza o nome da população a estudar:");
                                Scanner in2 = new Scanner(System.in);
                                nomepopulacao = in2.nextLine();
                            }
                            case "n" , "N","nao","NAO","não","NÃO" -> {
                                nomeficheiroEntrada = "";
                                System.out.println();
                                vetor = LerVetor(c, nomeficheiroEntrada);
                                tamanho = vetor.length;
                                Sobrev = Sobrev(tamanho, nomeficheiroEntrada);
                                Fecun = Fecun(tamanho, nomeficheiroEntrada);
                                matriz = matriz(tamanho, Sobrev, Fecun);
                                vetoreigenreal = vetoreigenreal(matriz);
                                vetoreigen = vetoreigen(matriz);
                                System.out.println("Introduza o nome da população a estudar:");
                                Scanner in2 = new Scanner(System.in);
                                nomepopulacao = in2.nextLine();
                            }
                            default -> {
                                System.out.println("Não inseriu um valor válido");
                                System.exit(0);
                            }
                        }
                    }
                    case "6" -> {
                        System.exit(0);
                    }
                    default -> {
                        System.out.println("Não selecionou uma opção válida! Tente de novo.");
                        System.out.println();
                    }
                }
            }while(!resposta.equals("6"));
        } else {
            Path path1 = Paths.get(nomeficheiroEntrada);
            File ficheiro = new File(String.valueOf(path1));
            Path path2 = Paths.get(nomeficheiroSaida);
            PrintWriter out = new PrintWriter(String.valueOf(path2));
            String s = "";
            int tamanho = tamanho(ficheiro);
            double[] vetor = new double[tamanho];
            double[] Fecun = new double[tamanho];
            double[] Sobrev = new double[tamanho - 1];
            Scanner sce = new Scanner(ficheiro);
            int contadorlinhs=0;
            do {

                s = sce.nextLine();
                if (!s.equals("")) {
                    contadorlinhs+=1;
                    if (s.charAt(0) == 'x') {
                        if (s.indexOf('.')!=-1){
                            System.out.println("O vetor dos X têm pontos, e não devia pois são números inteiros.");
                            System.out.println("A desligar o programa.");
                            System.exit(0);
                        }
                        vetor = lerVetor(s);
                    } else if (s.charAt(0) == 'f') {
                        Fecun = lerVetor(s);

                    } else if (s.charAt(0) == 's') {
                        Sobrev = lerVetor(s);
                        for (int d=0;d<Sobrev.length;d++){
                            if (Sobrev[d]>1){
                                System.out.println("A taxa de sobrevivência não pode ser maior que 1.");
                                System.exit(0);
                            }
                        }
                    } else {

                    }
                }

            } while (sce.hasNextLine());
            if (contadorlinhs>3){
                System.out.println("O ficheiro tem mais do que 3 linhas com palavaras ou letras.");
                System.exit(0);
            }
            contadorlinhs=0;
            double[][] matriz = MatrizLeslie(Fecun, Sobrev);
            double[] vetoreigenreal=vetoreigenreal(matriz);
            double[] vetoreigen = vetoreigen2(matriz);
            out.println("Matriz de Leslie");
            escrever2(matriz, out);
            out.println();
            PrintGeracoes2(TaxaVariacao, Dimensaopopulacao, ngenerations, matriz, vetor, out);
            if (ValorVetorProprio == true) {
                out.println();
                out.printf("Maior Valor próprio = %.4f\n", valorProprio2(matriz));
            }
            if (ValorVetorProprio == true) {
                out.println("Vetor Próprio associado:");
                PrintVetorDecimal2(vetoreigen, out);
            }
            out.close();

            //-------------------gnuplot nao interativo
            int interativo = 0;
            gnuplot(Dimensaopopulacao, TaxaVariacao, interativo, ngenerations, tipoformato, vetor.length, matriz, Fecun, Sobrev, vetor,nomeficheiroEntrada);
        }
    }
    public static void PrintVetorDecimalreal2(double[] vetor, PrintWriter out) throws FileNotFoundException {
        for (int i = 0; i < vetor.length; i++) {
            out.printf("%.4f\n", vetor[i]);
        }
    }

    public static void PrintVetorDecimalreal(double[] vetor) {
        for (int i = 0; i < vetor.length; i++) {
            System.out.printf("%.4f\n", vetor[i]);
        }
    }
    public static double DimensaoPopulacao2(double[][] matriz, double[] vetor, int t) {
        double dpopulacao = 0;

        for (int c = 0; c < t; c++) {
            MultiMatrizPorVetor2(matriz, vetor);
            vetor = MultiMatrizPorVetor(matriz, vetor);
        }

        for (int i = 0; i < vetor.length; i++) {
            dpopulacao = vetor[i] + dpopulacao;
        }

        return dpopulacao;
    }


    public static double TaxaVariacao2(double[][] matriz, double[] vetor, int t) {
        double taxavariacao;

        taxavariacao = DimensaoPopulacao2(matriz, vetor, t) / DimensaoPopulacao2(matriz, vetor, t - 1);

        return taxavariacao;
    }

    public static void PrintTaxaVariacao2(double taxavariacao, PrintWriter out) throws FileNotFoundException {
        if (taxavariacao == taxavariacao) {
            out.printf("Taxa de Variação = %.2f%n", taxavariacao);
        } else out.printf("Taxa de Variação = 1.00%n", taxavariacao);
    }


    public static void PrintVetorDecimal2(double[] vetor, PrintWriter out) throws FileNotFoundException {
        for (int i = 0; i < vetor.length; i++) {
            out.printf("%.3f\n", vetor[i]);
        }
    }

    public static double[] MultiMatrizPorVetor2(double[][] matriz, double[] vetor) {
        double[] vf = new double[vetor.length];
        double soma = 0;
        double valor = 0;
        for (int i = 0; i < vetor.length; i++) {
            for (int b = 0; b < vetor.length; b++) {
                valor = matriz[i][b] * vetor[b];
                soma += valor;
            }
            soma = Math.floor(soma);
            vf[i] = soma;
            soma = 0;
        }
        return vf;
    }

    public static void PrintGeracoes2(boolean TaxaVariacao,boolean Dimensaopopulacao,int ngeracoes, double[][] matriz, double[] vetor,PrintWriter out) {
        int i =1;
        int ola=0;
        int ola2=0;
        double[] vetororigin=vetor;
        double[] vetororigin2=vetor;
        if (ngeracoes >= 0) {
            if (Dimensaopopulacao == true) {
                out.println("Número total de individuos");
                out.println("(t, Nt)");
                for (int d = 0; d < ngeracoes + 1; d++) {
                    out.printf("(%d, %.2f)\n", d, DimensaoPopulacao2(matriz, vetororigin, d));
                }
                out.println();
            }
            if (TaxaVariacao == true) {
                out.println("Crescimento da população");
                out.println("(t, delta_t)");
                for (int d = 1; d < ngeracoes + 1; d++) {
                    if (TaxaVariacao2(matriz,vetororigin,d)==TaxaVariacao2(matriz,vetororigin,d)){
                        out.printf("(%d ,%.2f)\n", d - 1, TaxaVariacao2(matriz, vetororigin, d));
                    }else out.printf("(%d ,0,00)\n", d - 1);
                }
                out.println();
            }
            if (Dimensaopopulacao == true) {
                out.println("Numero por classe (não normalizado)");
                out.printf("(t, ");
                for (int d = 1; d < vetor.length; d++) {
                    out.printf("%d, ", d);
                    ola = d;
                }
                out.printf("%d)\n", ola + 1);
                for (int g = 0; g < ngeracoes + 1; g++) {
                    out.printf("(%d, ", g);
                    if (vetor.length > 1) {


                        for (int m = 0; m < vetor.length - 1; m++) {
                            out.printf("%.2f, ", vetor[m]);
                            ola2 = m;
                        }
                        out.printf("%.2f)", vetor[ola2 + 1]);
                    }else out.printf("%.2f)",vetor[0]);
                    vetor = MultiMatrizPorVetor(matriz, vetor);
                    out.println();
                }
                out.println();
                out.println("Numero por classe (normalizado)");
                out.printf("(t, ");
                for (int d = 1; d < vetororigin2.length; d++) {
                    out.printf("%d, ", d);
                    ola = d;
                }
                out.printf("%d)\n", ola + 1);
                for (int g = 0; g < ngeracoes + 1; g++) {
                    out.printf("(%d, ", g);
                    if (vetor.length>1) {


                        for (int m = 0; m < vetororigin2.length - 1; m++) {
                            if (vetororigin2[m] / DimensaoPopulacao2(matriz, vetororigin, g) * 100 == vetororigin2[m] / DimensaoPopulacao2(matriz, vetororigin, g) * 100) {
                                out.printf("%.2f, ", vetororigin2[m] / DimensaoPopulacao2(matriz, vetororigin, g) * 100);
                            } else
                                out.printf("0,00, ", vetororigin2[m] / DimensaoPopulacao2(matriz, vetororigin, g) * 100);
                            ola2 = m;
                        }
                        if (vetororigin2[ola2 + 1] / DimensaoPopulacao2(matriz, vetororigin, g) * 100 == vetororigin2[ola2 + 1] / DimensaoPopulacao2(matriz, vetororigin, g) * 100) {
                            out.printf("%.2f)", vetororigin2[ola2 + 1] / DimensaoPopulacao2(matriz, vetororigin, g) * 100);
                        } else
                            out.printf("0,00)", vetororigin2[ola2 + 1] / DimensaoPopulacao2(matriz, vetororigin, g) * 100);
                    }else out.printf("1,00)");
                    vetororigin2 = MultiMatrizPorVetor(matriz, vetororigin2);
                    out.println();
                }
            }
        } else {
            System.out.println("Número de gerações inválido. Tente de novo.");
        }
    }

    public static double[] populacaonormalizada2(double[][] matriz, double[] vetor) {
        double populacao = 0;
        double[] populacaonormal = new double[vetor.length];
        for (int x = 0; x < vetor.length; x++) {
            populacao += vetor[x];
        }
        for (int i = 0; i < vetor.length; i++) {
            populacaonormal[i] = vetor[i] / populacao;
        }
        return populacaonormal;
    }


    public static void PrintVetorpop2(double[][] matriz, double[] vetor, PrintWriter out) throws FileNotFoundException {
        double[] populacaonormal = populacaonormalizada2(matriz, vetor);
        for (int i = 0; i < vetor.length; i++) {
            out.printf("%.0f ", vetor[i]);
            if (populacaonormal[i] == populacaonormal[i]) {
                out.printf("%.4f\n", populacaonormal[i]);
            } else out.printf("0\n", populacaonormal[i]);
        }
    }

    public static double[] vetoreigen2(double[][] matrizleslie) {
        double soma = 0;
        Matrix matrizlesd = new Basic2DMatrix(matrizleslie);
        EigenDecompositor decomposi = new EigenDecompositor(matrizlesd);
        Matrix[] eigens = decomposi.decompose();
        double[][] mat1 = eigens[0].toDenseMatrix().toArray();
        double[][] mat2 = eigens[1].toDenseMatrix().toArray();
        double[] vetoreigen = new double[mat1.length];
        for (int i = 0; i < vetoreigen.length; i++) {
            vetoreigen[i] = mat1[i][0];
        }
        for (int i = 0; i < vetoreigen.length; i++) {
            soma += vetoreigen[i];
        }
        for (int i = 0; i < vetoreigen.length; i++) {
            try {
                vetoreigen[i] = (1 / soma) * vetoreigen[i];
            } catch (ArithmeticException e) {
                System.out.println("divisao por 0");
            }
        }

        return vetoreigen;
    }

    public static double valorProprio2(double[][] matrizleslie) {
        Matrix matrizlesd = new Basic2DMatrix(matrizleslie);
        EigenDecompositor decomposi = new EigenDecompositor(matrizlesd);
        Matrix[] eigens = decomposi.decompose();
        double[][] mat = eigens[1].toDenseMatrix().toArray();
        return mat[0][0];
    }

    public static double[][] MatrizLeslie(double[] Fecundidade, double[] sobrevivencia) {
        int contlinhas = 1;
        double[][] matrizleslie = new double[Fecundidade.length][Fecundidade.length];
        for (int i = 0; i < matrizleslie.length; i++) {
            matrizleslie[0][i] = Fecundidade[i];
        }
        for (int i = 0; i < matrizleslie.length - 1; i++) {
            matrizleslie[contlinhas][i] = sobrevivencia[i];
            contlinhas += 1;
        }
        return matrizleslie;
    }
    public static boolean MatrizLeslietest(double[] Fecundidade, double[] sobrevivencia,double[][] matrizesperada){
        if (Arrays.equals(MatrizLeslie(Fecundidade,sobrevivencia),matrizesperada)){
            return true;
        }else return false;

    }


    public static double[] lerVetor(String s) {
        int contadornumsdepoisdoponto=0;
        int contador=0;
        int ol = 0;
        String p = "";
        String[] nums = s.split(",");
        double[] ds = new double[nums.length];
        for (int i = 0; i < nums.length; i++) {
            for (int g = 0; g < nums[i].length(); g++) {
                if (nums[i].charAt(g) == '=') {
                    contador=1;
                    ol = g + 1;
                    try {
                        do {
                            p += nums[i].charAt(ol);
                            ol += 1;
                        } while (ol < nums[i].length());
                        ds[i] = Double.parseDouble(p);
                        if (ds[i]<0){
                            System.out.println("Existem valores negativos.");
                            System.exit(0);
                        }
                    } catch (NumberFormatException e) {
                    }
                    char d= nums[i].charAt(g-1);
                    String l=""+d;
                    if (Integer.parseInt(l)!=i){
                        System.out.println("É possivel que a ordem dos elementos não esteja corretamente inserida, ou falta uma vírgula.");
                        System.out.println("A desligar o programa.");
                        System.exit(0);
                    }
                }
                if (nums[i].charAt(g)== '.') {
                    for (int c=g;c<nums[i].length();c++){
                        contadornumsdepoisdoponto+=1;
                    }
                    try {
                        String testeaseguiraospontos=""+nums[i].charAt(g+2);
                    }catch (StringIndexOutOfBoundsException ls){
                        System.out.println("Algum dos elementos está mal formatado.");
                        System.exit(0);
                    }
                    String testeaseguiraospontos=""+nums[i].charAt(g+2);
                    if (testeaseguiraospontos==""){
                        System.out.println("Os números com pontos têm que ter 2 casas decimais");
                        System.exit(0);
                    }
                }
                if (contadornumsdepoisdoponto>3){
                    System.out.println("Um dos numeros tem mais do que 2 casas decimais, ou tem espaço a mais antes da virgula(entre os elementos), ou não tem virgula(entre os elementos");
                    System.out.println("A desligar o programa.");
                    System.exit(0);
                }
                if (contadornumsdepoisdoponto==1){
                    System.out.println("Algum dos elementos tem apenas um número(ou nenhum) à frente do ponto das casas decimais.");
                    System.out.println("A desligar o programa.");
                    System.exit(0);
                }
                contadornumsdepoisdoponto=0;
                p = "";
            }
            if (contador!=1){
                System.out.println("Em algum dos seus elementos não existe a formatação correta para o número");
                System.out.println("A desligar o programa.");
                System.exit(0);
            }
            contador=0;

        }
        return ds;
    }

    public static void escrever2(double[][] matriz, PrintWriter out) {
        for (int i = 0; i < matriz.length; i++) {
            for (int j = 0; j < matriz.length; j++) {
                out.printf("%.2f ", matriz[i][j]);
            }
            out.println();
        }
    }

    public static int tamanho(File ficheiro) throws FileNotFoundException {
        int tamanho = 0;

        String s = "";

        Scanner ler = new Scanner(ficheiro);
        do {
            s = ler.nextLine();
            if (!s.equals("")) {
                if (s.charAt(0) == 'x') {
                    String[] tam = s.split(",");
                    tamanho = tam.length;
                } else if (s.charAt(0) == 'f') {
                    double[] Fecun = lerVetor(s);
                    String[] tam = s.split(",");
                    tamanho = tam.length;
                } else if (s.charAt(0) == 's') {
                    double[] Sobrev = lerVetor(s);
                    String[] tam = s.split(",");
                    tamanho = tam.length + 1;
                } else {
                    s = "";
                }
            } else {

                tamanho = 1;
            }
            return tamanho;
        } while (ler.hasNextLine());

    }

    //INTERATIVO---------------------------------------------------------------------------------------------

    public static void escrever(double[][] matriz) {

        for (int i = 0; i < matriz.length; i++) {
            for (int j = 0; j < matriz.length; j++) {
                System.out.printf("%.3f ", matriz[i][j]);
            }
            System.out.println();
        }
    }

    public static double DimensaoPopulacao(double[][] matriz, double[] vetor, int t) {
        double dpopulacao = 0;

        for (int c = 0; c < t; c++) {
            MultiMatrizPorVetor(matriz, vetor);
            vetor = MultiMatrizPorVetor(matriz, vetor);
        }

        for (int i = 0; i < vetor.length; i++) {
            dpopulacao = vetor[i] + dpopulacao;
        }

        return dpopulacao;
    }
    public static boolean DimensaoPopulacaotest(double[][] matriz, double[] vetor, int t, double dimensaoesperada){
        if (DimensaoPopulacao(matriz,vetor,t)==dimensaoesperada){
            return true;
        }
        else return false;
    }

    public static void PrintDimensaoPopulacao(double[][] matriz, double[] vetor, int t) {
        System.out.printf("Dimensão da População = %.0f%n", DimensaoPopulacao(matriz, vetor, t));
    }

    public static double TaxaVariacao(double[][] matriz, double[] vetor, int t) {
        double taxavariacao;

        taxavariacao = DimensaoPopulacao(matriz, vetor, t) / DimensaoPopulacao(matriz, vetor, t - 1);

        return taxavariacao;
    }
    public static boolean TaxaVariacao(double[][] matriz, double[] vetor, int t,double taxavariacaoesperada){
        if (TaxaVariacao(matriz,vetor,t)==taxavariacaoesperada){
            return true;
        }else return false;
    }

    public static void PrintTaxaVariacao(double taxavariacao) {
        System.out.printf("Taxa de Variação = %.2f%n", taxavariacao);
    }

    public static void PrintVetor(double[] vetor) {
        for (int i = 0; i < vetor.length; i++) {
            System.out.printf("%.0f\n", vetor[i]);
        }
    }

    public static void PrintVetorDecimal(double[] vetor) {
        for (int i = 0; i < vetor.length; i++) {
            System.out.printf("%.3f\n", vetor[i]);
        }
    }

    public static double[] MultiMatrizPorVetor(double[][] matriz, double[] vetor) {
        double[] vf = new double[vetor.length];
        double soma = 0;
        double valor = 0;
        for (int i = 0; i < vetor.length; i++) {
            for (int b = 0; b < vetor.length; b++) {
                valor = matriz[i][b] * vetor[b];
                soma += valor;
            }
            soma = Math.floor(soma);
            vf[i] = soma;
            soma = 0;
        }
        return vf;
    }
    public static boolean MultiMatrizPorVetortest(double[][] matriz, double[] vetor, double[]vetoresperado){
        if (Arrays.equals(MultiMatrizPorVetor(matriz,vetor),vetoresperado)){
            return true;
        }else return false;
    }
//======================================================================================================================


    public static void Printgeracoes(int ngeracoes, double[][] matriz, double[] vetor) {
        int i =1;
        int ola=0;
        int ola2=0;
        double[] vetororigin=vetor;
        double[] vetororigin2=vetor;
        if (ngeracoes >= 0) {
            System.out.println("Número total de individuos");
            System.out.println("(t, Nt)");
            for (int d=0;d<ngeracoes+1;d++){
                System.out.printf("(%d, %.2f)\n",d,DimensaoPopulacao(matriz, vetororigin, d));
            }
            System.out.println();
            System.out.println("Crescimento da população");
            System.out.println("(t, delta_t)");
            for (int d=1;d<ngeracoes+1;d++){
                if(TaxaVariacao(matriz,vetororigin,d)==TaxaVariacao(matriz,vetororigin,d)){
                    System.out.printf("(%d ,%.2f)\n",d-1,TaxaVariacao(matriz,vetororigin,d));
                }else System.out.printf("(%d ,0,00)\n",d-1);

            }
            System.out.println();
            System.out.println("Numero por classe (não normalizado)");
            System.out.printf("(t, ");
            for (int d=1;d<vetor.length;d++){
                System.out.printf("%d, ",d);
                ola=d;
            }
            System.out.printf("%d)\n",ola +1);
            for (int g=0;g<ngeracoes+1;g++){
                System.out.printf("(%d, ", g);
                if (vetor.length>1) {
                    for (int m = 0; m < vetor.length - 1; m++) {
                        System.out.printf("%.2f, ", vetor[m]);
                        ola2 = m;
                    }

                    System.out.printf("%.2f)", vetor[ola2 + 1]);
                }else System.out.printf("%.2f)", vetor[0]);
                vetor=MultiMatrizPorVetor(matriz, vetor);
                System.out.println();
            }
            System.out.println();
            System.out.println("Numero por classe (normalizado)");
            System.out.printf("(t, ");
            for (int d=1;d<vetororigin2.length;d++){
                System.out.printf("%d, ",d);
                ola=d;
            }
            System.out.printf("%d)\n",ola +1);
            for (int g=0;g<ngeracoes+1;g++){
                System.out.printf("(%d, ", g);
                if (vetororigin2.length>1){
                    for (int m=0;m<vetororigin2.length-1;m++){
                        if (vetororigin2[m]/DimensaoPopulacao(matriz,vetororigin,g)*100==vetororigin2[m]/DimensaoPopulacao(matriz,vetororigin,g)*100){
                            System.out.printf("%.2f, ",vetororigin2[m]/DimensaoPopulacao(matriz,vetororigin,g)*100);
                        }else System.out.printf("0,00, ");
                        ola2=m;
                    }
                    if (vetororigin2[ola2+1]/DimensaoPopulacao(matriz,vetororigin,g)*100==vetororigin2[ola2+1]/DimensaoPopulacao(matriz,vetororigin,g)*100){
                        System.out.printf("%.2f)",vetororigin2[ola2+1]/DimensaoPopulacao(matriz,vetororigin,g)*100);
                    }else System.out.printf("0,00)");
                }else System.out.printf("1,00)");
                vetororigin2=MultiMatrizPorVetor(matriz, vetororigin2);
                System.out.println();
            }
            System.out.println();
        } else {
            System.out.println("Número de gerações inválido. Tente de novo.");
        }
    }

    public static double[] populacaonormalizada(double[][] matriz, double[] vetor) {
        double populacao = 0;
        double[] populacaonormal = new double[vetor.length];
        for (int x = 0; x < vetor.length; x++) {
            populacao += vetor[x];
        }
        for (int i = 0; i < vetor.length; i++) {
            populacaonormal[i] = vetor[i] / populacao;
        }
        return populacaonormal;
    }
    public static boolean populacaonormalizadatest(double[][] matriz, double[] vetor,double[]vetoresperado){
        if (Arrays.equals(populacaonormalizada(matriz,vetor),vetoresperado)){
            return true;
        }else return false;
    }


    public static double[][] matriz(int num1, double[] sobrev, double[] fecun) {
        int contlinhas = 1;
        double[][] matrizleslie = new double[num1][num1];
        for (int i = 0; i < matrizleslie.length; i++) {
            matrizleslie[0][i] = fecun[i];
        }
        for (int i = 0; i < matrizleslie.length - 1; i++) {
            matrizleslie[contlinhas][i] = sobrev[i];
            contlinhas += 1;
        }
        return matrizleslie;
    }

    public static void PrintVetorpop(double[][] matriz, double[] vetor) {
        double[] populacaonormal = populacaonormalizada(matriz, vetor);
        for (int i = 0; i < vetor.length; i++) {
            System.out.printf("%.0f ", vetor[i]);
            System.out.printf("%.2f\n", populacaonormal[i]);
        }
    }

    public static double[] Sobrev(int num1, String nomeficheiroEntrada) throws FileNotFoundException {
        if (nomeficheiroEntrada.equals("")) {
            double[] sobrev = new double[num1];
            System.out.println("Insira as taxas de sobrevivência [0-1].");
            for (int j = 0; j < num1 - 1; j++) {
                try {
                    sobrev[j] = sc.nextDouble();
                }catch (InputMismatchException po){
                    System.out.println("O programa só aceita números.");
                    System.out.println("A desligar o programa.");
                    System.exit(0);
                }
                if (sobrev[j]>1){
                    System.out.println("A taxa de sobrevivência não pode ser maior que 1.");
                    System.out.println("A desligar o programa");
                    System.exit(0);
                }
                if (sobrev[j]<0){
                    System.out.println("Não existem taxas negativas");
                    System.exit(0);
                }
            }
            return sobrev;
        } else {
            Path path = Paths.get(nomeficheiroEntrada);
            int ol = 0;
            String palha = "";
            String s = "";
            String p = "";
            File file = new File(String.valueOf(path));
            Scanner sc = new Scanner(file);
            palha = sc.nextLine();
            s = sc.nextLine();
            String[] nums = s.split(",");
            double[] ds = new double[nums.length];
            for (int i = 0; i < nums.length; i++) {
                for (int g = 0; g < nums[i].length(); g++) {
                    if (nums[i].charAt(g) == '=') {
                        ol = g + 1;
                        try {
                            do {
                                p += nums[i].charAt(ol);
                                ol += 1;
                            } while (ol < nums[i].length());
                            ds[i] = Double.parseDouble(p);
                        } catch (NumberFormatException e) {
                            System.out.println("valor invalido");
                            System.exit(0);
                        }
                    }
                    p = "";
                }
            }
            return ds;
        }
    }


    public static double[] Fecun(int num1, String nomeficheiroEntrada) throws FileNotFoundException {
        if (nomeficheiroEntrada.equals("")) {
            double[] fecun = new double[num1];
            System.out.println("Insira as taxas de fecundação.");
            for (int j = 0; j < num1; j++) {
                try {
                    fecun[j] = sc.nextDouble();
                } catch (InputMismatchException po) {
                    System.out.println("O programa só aceita números.");
                    System.out.println("A desligar o programa.");
                    System.exit(0);
                }
                if (fecun[j]<0){
                    System.out.println("Não existe fecundidade negativa.");
                    System.exit(0);
                }
            }
            return fecun;
        } else {
            Path path = Paths.get(nomeficheiroEntrada);
            int ol = 0;
            String palha = "";
            String palha2 = "";
            String s = "";
            String p = "";
            File file = new File(String.valueOf(path));
            Scanner sc = new Scanner(file);
            palha = sc.nextLine();
            palha2 = sc.nextLine();
            s = sc.nextLine();
            String[] nums = s.split(",");
            double[] ds = new double[nums.length];
            for (int i = 0; i < nums.length; i++) {
                for (int g = 0; g < nums[i].length(); g++) {
                    if (nums[i].charAt(g) == '=') {
                        ol = g + 1;
                        try {
                            do {
                                p += nums[i].charAt(ol);
                                ol += 1;
                            } while (ol < nums[i].length());
                            ds[i] = Double.parseDouble(p);
                        } catch (NumberFormatException e) {
                            System.out.println("valor invalido");
                            System.exit(0);
                        }
                    }
                    p = "";
                }
            }
            return ds;
        }
    }


    public static double[] LerVetor(int c, String nomeficheiroEntrada) throws FileNotFoundException {
        double teste=0;
        double teste2=0;
        int testeint=0;
        Scanner ler1 = new Scanner(System.in);
        if (nomeficheiroEntrada.equals("")) {
            int t = -1;
            String[] vetor = new String[c];
            System.out.println("Insira a distribuição inicial da população.(Escreva FIM quando acabar de inserir valores)");
            do {
                t += 1;
                vetor[t] = ler1.nextLine();
            } while (!vetor[t].equals("FIM")&& !vetor[t].equals("fim"));
            double[] vetornum = new double[t];
            for (int i = 0; i < vetornum.length; i++) {
                try{
                    vetornum[i] = Double.parseDouble(vetor[i]);
                    if (vetornum[i]<0){
                        System.out.println("Não existem populações negativas.");
                        System.exit(0);
                    }
                }catch (NumberFormatException pi){
                    System.out.println("O utilizador escreveu uma letra no vetor dos x (ou um número não inteiro)");
                    System.out.println("A desligar o programa");
                    System.exit(0);
                }



            }
            return vetornum;
        } else {
            int contadornumsdepoisdoponto=0;
            int contador=0;
            Path path = Paths.get(nomeficheiroEntrada);
            int ol = 0;
            String s = "";
            String p = "";
            File file = new File(String.valueOf(path));
            Scanner sc = new Scanner(file);
            s = sc.nextLine();
            String[] nums = s.split(",");
            double[] ds = new double[nums.length];
            for (int i = 0; i < nums.length; i++) {
                for (int g = 0; g < nums[i].length(); g++) {
                    if (nums[i].charAt(g) == '=') {
                        contador = 1;
                        ol = g + 1;
                        try {
                            do {
                                p += nums[i].charAt(ol);
                                ol += 1;
                            } while (ol < nums[i].length());
                            ds[i] = Double.parseDouble(p);
                        } catch (NumberFormatException e) {
                        }
                        char d = nums[i].charAt(g - 1);
                        String l = "" + d;
                        if (Integer.parseInt(l) != i) {
                            System.out.println("É possivel que a ordem dos elementos não esteja corretamente inserida, ou  falta uma vírgula.");
                            System.out.println("A desligar o programa.");
                            System.exit(0);

                        }
                    }
                    if (nums[i].charAt(g) == '.') {
                        for (c = g; c < nums[i].length(); c++) {
                            contadornumsdepoisdoponto += 1;
                        }
                    }
                    if (contadornumsdepoisdoponto > 3) {
                        System.out.println("Um dos numeros tem mais do que 2 casas decimais, ou tem espaço a mais antes da virgula, ou não tem virgula");
                        System.exit(0);
                    }
                    contadornumsdepoisdoponto = 0;
                    p = "";
                }
                if (contador != 1) {
                    System.out.println("Em algum dos seus elementos não existe a formatação correta para o número");
                }
                contador = 0;
            }
            return ds;
        }
    }
    public static double[] vetoreigenreal(double[][] matrizleslie) {
        double soma = 0;

        Matrix matrizlesd = new Basic2DMatrix(matrizleslie);
        EigenDecompositor decomposi = new EigenDecompositor(matrizlesd);
        Matrix[] eigens = decomposi.decompose();
        double[][] mat1 = eigens[0].toDenseMatrix().toArray();
        double[][] mat2 = eigens[1].toDenseMatrix().toArray();
        double[] vetoreigen = new double[mat1.length];
        for (int i = 0; i < vetoreigen.length; i++) {
            vetoreigen[i] = mat1[i][0];
        }
        return vetoreigen;
    }

    public static double[] vetoreigen(double[][] matrizleslie) {
        double soma = 0;

        Matrix matrizlesd = new Basic2DMatrix(matrizleslie);
        EigenDecompositor decomposi = new EigenDecompositor(matrizlesd);
        Matrix[] eigens = decomposi.decompose();
        double[][] mat1 = eigens[0].toDenseMatrix().toArray();
        double[][] mat2 = eigens[1].toDenseMatrix().toArray();
        double[] vetoreigen = new double[mat1.length];
        for (int i = 0; i < vetoreigen.length; i++) {
            vetoreigen[i] = mat1[i][0];
        }
        for (int i = 0; i < vetoreigen.length; i++) {
            soma += vetoreigen[i];
        }
        for (int i = 0; i < vetoreigen.length; i++) {
            try {
                vetoreigen[i] = (1 / soma) * vetoreigen[i];
            } catch (ArithmeticException e) {
                System.out.println("divisao por 0");
            }
        }
        return vetoreigen;
    }

    public static boolean TesteEigen(double[][] matrizleslise, double[] vetoresperado) {
        double[] vetore = vetoreigen(matrizleslise);
        if (Arrays.equals(vetore, vetoresperado)) {
            return true;
        } else return false;
    }

    public static double valorProprio(double[][] matrizleslie) {
        Matrix matrizlesd = new Basic2DMatrix(matrizleslie);
        EigenDecompositor decomposi = new EigenDecompositor(matrizlesd);
        Matrix[] eigens = decomposi.decompose();
        double[][] mat = eigens[1].toDenseMatrix().toArray();
        return mat[0][0];
    }
    public static boolean valorPropriotest(double[][] matrizleslie, double valorproprioesperado){
        if (valorProprio(matrizleslie)==valorproprioesperado){
            return true;
        }else return false;
    }

    //gnuplot===========================================================================================================
    public static String PrintTaxaVariacaognuplot(double taxavariacao) {
        Locale.setDefault(Locale.US);
        String s = String.format("%.2f", taxavariacao);
        return s;
    }

    public static double PopTotal(double[] vetor) {
        double soma = 0;
        for (int i = 0; i < vetor.length; i++) {
            soma += vetor[i];
        }
        return soma;
    }

    public static void Printgnuplot(int ngeracoes, double[][] matriz, double[] vetor, int resposta,int mudancadeformato) throws FileNotFoundException {
        PrintWriter out = new PrintWriter("dados.txt");
        double[] vef = vetor;
        int ola = 0;
        if (resposta == 1) {

            if (ngeracoes >= 0) {
                if(mudancadeformato==0){
                    System.out.println("Número total de individuos");
                    System.out.println("(t, Nt)");
                }
                for (int i = 0; i <= ngeracoes; i++) {
                    out.printf((i) + " " + PopTotal(vef));
                    if(mudancadeformato==0) {
                        System.out.println((i) + " " + PopTotal(vef));
                    }
                    vef = MultiMatrizPorVetor(matriz, vetor);
                    vetor = vef;
                    out.println();
                    //Mostrar os valores na consola
                }
                out.close();
            } else {
                out.println("Número de gerações inválido. Tente de novo.");
            }
            out.close();
            if(mudancadeformato==0) {
                System.out.println();
            }
        } else if (resposta == 2) {
            if(mudancadeformato==0){
                System.out.println("Crescimento da população");
                System.out.println("(t, delta_t)");
            }
            for (int i = 1; i <= ngeracoes; i++) {
                out.print(i + " ");
                out.printf(PrintTaxaVariacaognuplot(TaxaVariacao(matriz, vetor, i)));
                out.println();
                if(mudancadeformato==0) {
                    System.out.println(i + " " + PrintTaxaVariacaognuplot(TaxaVariacao(matriz, vetor, i)));
                }
            }
            System.out.println();
            out.close();
        } else if (resposta == 3) {
            if(mudancadeformato==0){

                System.out.println("Numero por classe (não normalizado)");
                System.out.printf("(t, ");
                for (int d=0;d<vetor.length-1;d++){
                    System.out.printf("%d, ",d);
                    ola=d;
                }
                System.out.printf("%d)\n",ola +1);
            }
            if (ngeracoes >= 0) {
                out.printf("0 ");
                if(mudancadeformato==0) {
                    System.out.print("0 ");
                }
                for (int i = 0; i < vetor.length; i++) {
                    double[] populacaonormal = populacaonormalizada(matriz, vetor);
                    out.printf("%.0f ", vetor[i]);
                    if(mudancadeformato==0) {
                        System.out.printf("%.0f ", vetor[i]);//
                    }

                }
                out.println();
                if(mudancadeformato==0) {
                    System.out.println();
                }
                for (int c = 0; c < ngeracoes; c++) {
                    out.printf((c + 1) + " ");
                    if(mudancadeformato==0) {
                        System.out.print((c + 1) + " ");
                    }
                    vetor = MultiMatrizPorVetor(matriz, vetor);
                    for (int i = 0; i < vetor.length; i++) {
                        double[] populacaonormal = populacaonormalizada(matriz, vetor);
                        out.printf("%.0f ", vetor[i]);
                        if(mudancadeformato==0) {
                            System.out.printf("%.0f ", vetor[i]);
                        }
                    }
                    out.println();
                    if(mudancadeformato==0) {
                        System.out.println();
                    }
                }
                if(mudancadeformato==0) {
                    System.out.println();
                }
            } else {
                out.println("Número de gerações inválido. Tente de novo.");
            }
            out.close();
        } else if (resposta == 4) {


            out.print("0 ");
            if(mudancadeformato==0) {

                System.out.println("Numero por classe (normalizado)");
                System.out.printf("(t, ");
                for (int d=0;d<vetor.length-1;d++){
                    System.out.printf("%d, ",d);
                    ola=d;
                }
                System.out.printf("%d)\n",ola +1);

                System.out.print("0 ");
            }
            for (int i = 0; i < vetor.length; i++) {
                double[] populacaonormal = populacaonormalizada(matriz, vetor);
                out.printf("%.2f ", populacaonormal[i] * 100);
                if(mudancadeformato==0) {
                    System.out.printf("%.2f ", populacaonormal[i] * 100);
                }

            }
            out.println();
            if(mudancadeformato==0) {
                System.out.println();
            }
            for (int c = 0; c < ngeracoes; c++) {
                out.printf((c + 1) + " ");
                if(mudancadeformato==0) {
                    System.out.print((c + 1) + " ");
                }

                vetor = MultiMatrizPorVetor(matriz, vetor);
                for (int i = 0; i < vetor.length; i++) {
                    double[] populacaonormal = populacaonormalizada(matriz, vetor);
                    out.printf("%.2f ", populacaonormal[i] * 100);
                    if(mudancadeformato==0) {
                        System.out.printf("%.2f ", populacaonormal[i] * 100);
                    }
                }
                out.println();
                if(mudancadeformato==0) {
                    System.out.println();
                }
            }
            if(mudancadeformato==0) {
                System.out.println();
            }
        } else {
            out.println("Número de gerações inválido. Tente de novo.");
        }
        out.close();
    }

    public static String data() {
        Date today = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(today);
        int dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);
        int month = cal.get(Calendar.MONTH); //Esta API do java considera JAN-0 e DEC-11, logo é necessário somar 1
        int year = cal.get(Calendar.YEAR);
        month += 1;
        String Date = String.valueOf(dayOfMonth) + "-" + String.valueOf(month) + "-" + String.valueOf(year);
        return Date;
    }


    public static void gnuplot(boolean Dimensaopopulacao, boolean TaxaVariacao, int interativo, int ngenerations, int tipoformato, int classes, double[][] matriz, double[] fecund, double[] sobrev, double[] vetor,String nomepopulacao) throws IOException, InterruptedException {
        int cont = 0, repetir = 0, tipo2 = 0,geracoes2=0,contperola =0;
        String extensao = "", terminal = "", ylabel = "", nomevisualizado = "", nometxt = "dados.txt",
                title = "", legenda = "", graphtype = "";
        if (interativo == 1) {
            Scanner sc = new Scanner(System.in);
            System.out.println("Bem vindo à funcionalidade dos gráficos!");


            while (!graphtype.equals("fim")) {

                System.out.println("Escolha agora o gráfico que pretende gerar (Digite " + "\"fim\"" + " para sair desta funcionalidade):");
                System.out.println("1-Dimensão da população(número total de indíviduos)");
                System.out.println("2-Taxa de variação");
                System.out.println("3-Evolução da distribuição da população");
                System.out.println("4-Distribuição da população normalizada pelo total da população");



                graphtype = sc.nextLine();
                if (graphtype.equals("fim")) {

                } else if (Integer.parseInt(graphtype) >= 1 && Integer.parseInt(graphtype) <= 4) { // Para nao perguntar as geraçoes se for invalido
                    switch (graphtype) {
                        case "1" -> {
                            nomevisualizado = nomepopulacao+"dimensaoTotalPop_" + data(); // O nome do ficheiro visualizado (com sufixação .png/.eps/.txt)
                        }
                        case "2" -> {
                            nomevisualizado = nomepopulacao+"variacaopopulacao_" + data();
                        }
                        case "3" -> {
                            nomevisualizado = nomepopulacao+"distribporclasse_" + data();
                        }
                        case "4" -> {
                            nomevisualizado = nomepopulacao+"distribporclasse_norm_" + data();
                        }
                        case "fim" -> {
                            System.out.println("A sair da funcionalidade dos gráficos.");
                        }
                        default -> {
                            System.out.println("Número Inválido! Tente de novo!");
                            System.out.println();
                        }
                    }
                    do {
                        System.out.println("Quantas gerações pretende prever?");
                        int ngenerationsint = sc.nextInt();
                        geracoes2=ngenerationsint;
                        if (ngenerationsint >= 0) {
                            contperola =0;
                            tipo2=0;
                            Printgnuplot(ngenerationsint, matriz, vetor, Integer.parseInt(graphtype),contperola);
                            fazergraficos(Integer.parseInt(graphtype), tipo2,nomevisualizado,contperola,nomepopulacao,interativo);
                            repetir = 0;
                        } else {
                            System.out.println("Número de gerações Inválido!Tente novamente.");
                            repetir = 1;
                        }
                    } while (repetir != 0);
                }

                if (!graphtype.equals("fim")) {
                    extensao="png";
                    System.out.println("Está a visualizar:" + nomevisualizado);
                    System.out.println("Pretende guardar o que visualizou? (s/n)");
                    String guardar = sc.next();
                    sc.nextLine();
                    File file = new File(nomevisualizado+"."+extensao);
                    if (!guardar.equals("s")) {
                        file.delete();
                    } else {
                        file.delete();
                        System.out.println("Qual o tipo de ficheiro que pretende criar?");
                        do {
                            cont = 0;
                            System.out.println("1-png");
                            System.out.println("2-txt");
                            System.out.println("3-eps");
                            String tipo = sc.nextLine();

                            if (tipo.equals("1")) {
                                extensao = "png";
                            } else {
                                if (tipo.equals("2")) {
                                    extensao = "txt";
                                } else {
                                    if (tipo.equals("3")) {
                                        extensao = "eps";
                                    } else {
                                        System.out.println("Não introduziu um valor válido");
                                        System.out.println("Reentroduza o valor correspondente ao formato que deseja:");
                                        cont = 1;
                                    }
                                }
                            }
                            tipo2 = Integer.parseInt(tipo);
                        } while (cont != 0);
                        contperola=1;
                        Printgnuplot(geracoes2, matriz, vetor, Integer.parseInt(graphtype),contperola);
                        fazergraficos(Integer.parseInt(graphtype), tipo2,nomevisualizado,contperola,nomepopulacao,interativo);

                        File file2 = new File(nomevisualizado + "." + extensao);
                        Path path = Paths.get(String.valueOf(file2));
                        File localizacao = new File(String.valueOf(path));
                        System.out.println();
                        System.out.println("O ficheiro foi guardado com o seguinte nome: " + localizacao);
                        System.out.println();

                    }
                    File txt = new File("dados.txt");
                    File auxfile = new File("auxfile.gp");

                    Thread.sleep(500);
                    auxfile.delete();
                    txt.delete();
                }

            }
        } else {
            int graficos = 1;
            int contgraficos = 0;
            interativo=0;
            if (Dimensaopopulacao == true && TaxaVariacao == true) {
                do {
                    Printgnuplot(ngenerations, matriz, vetor, graficos,contperola);
                    fazergraficos(graficos, tipoformato,nomevisualizado,contperola,nomepopulacao,interativo);
                    graficos++;
                } while (graficos != 5);
            } else if (Dimensaopopulacao == true) {
                do {
                    if (contgraficos == 0) {
                        graficos = 1;
                    } else if (contgraficos == 1) {
                        graficos = 3;
                    } else {
                        graficos = 4;
                    }
                    Printgnuplot(ngenerations, matriz, vetor, graficos,contperola);
                    fazergraficos(graficos, tipoformato,nomevisualizado,contperola,nomepopulacao,interativo);
                    contgraficos += 1;
                } while (contgraficos != 3);
            } else if (TaxaVariacao) {
                graficos = 2;
                do {
                    Printgnuplot(ngenerations, matriz, vetor, graficos, contperola);
                    fazergraficos(graficos, tipoformato, nomevisualizado, contperola, nomepopulacao, interativo);
                    graficos++;
                }while(graficos!=5);
            }else{
                graficos = 3;
                do {
                    Printgnuplot(ngenerations,matriz,vetor,graficos,contperola);
                    fazergraficos(graficos,tipoformato,nomevisualizado,contperola,nomepopulacao,interativo);
                    graficos++;
                }while(graficos!=5);
            }
            File txt = new File("dados.txt");
            File auxfile = new File("auxfile.gp");
            txt.delete();
            Thread.sleep(500);
            auxfile.delete();
        }
    }

    public static void fazergraficos(int graficos, int tipoformato,String nomevisualizado, int mudancaformato,String nomepopulacao,int interativo) throws IOException, InterruptedException {
        int cont = 0, repetir = 0;
        String extensao = "", terminal = "", ylabel = "", nometxt = "",
                title = "", legenda = "", graphtype = "";

        if (tipoformato == 0) {
            extensao = "png";
            terminal = extensao;
        }
        if (tipoformato == 1) {
            extensao = "png";
            terminal = extensao;
        }
        if (tipoformato == 2) {
            extensao = "txt";
            terminal = "dumb";
        }
        if (tipoformato == 3) {
            extensao = "eps";
            terminal = "postscript";
        }
        if(interativo==0) {
            int tamanho = nomepopulacao.length();
            nomepopulacao = nomepopulacao.substring(0, tamanho - 4);
        }
        switch (graficos) {
            case (1) -> {

                ylabel = "População"; // O que irá aparecer como legenda no Y
                nomevisualizado = nomepopulacao+"dimensaoTotalPop_" + data(); // O nome do ficheiro visualizado (com sufixação .png/.eps/.txt)
                nometxt = "dados.txt"; // O nome do ficheiro.txt que tem os dados para criar o gráfico.
                title = "População em relação do tempo"; // O título do gráfico em questão
                legenda = "População";//Legenda no canto superior direito
            }
            case (2) -> {
                ylabel = "Variação da população";
                nomevisualizado = nomepopulacao + "variacaopopulacao_" + data();
                nometxt = "dados.txt";
                title = "Variação da população em relação do tempo";
                legenda = "Taxa de variação";
            }
            case (3) -> {
                ylabel = "Numero por classe (não normalizado)";
                nomevisualizado = nomepopulacao + "distribporclasse_" + data();
                nometxt = "dados.txt";
                title = "Distribuição da população em relação do tempo";
            }
            case (4) -> {
                ylabel = "Numero por classe (normalizado)";
                nomevisualizado = nomepopulacao + "distribporclasse_norm_" + data();
                nometxt = "dados.txt";
                title = "Distribuição da população normalizada em relação do tempo";
            }
        }
        if (graficos == 1 || graficos == 2) {
            PrintWriter out = new PrintWriter("auxfile.gp");
            out.print("set title " + "\"" + title + "\n");
            out.print("set xlabel " + "\"Gerações\"" + "\n");
            out.print("set ylabel " + "\"" + ylabel + "\"" + "\n");
            out.println("set terminal " + terminal);
            out.println("set output " + "\"" + nomevisualizado + "." + extensao + "\"");
            out.println("plot 'dados.txt' using 1:2 title " + "\"" + legenda + "\"" + " with linespoints,");
            out.close();
            Runtime rt = Runtime.getRuntime();
            Process prcs = rt.exec("C:\\Program Files\\gnuplot\\bin\\gnuplot auxfile.gp");
            File file = new File(nomevisualizado + "." + extensao);
            if(mudancaformato==0) {
                Desktop desktop = Desktop.getDesktop();
                Thread.sleep(500); // Faz com que o java tenha tempo de criar o ficheiro antes de o ler.
                desktop.open(file);
            }


        } else if (graficos == 3 || graficos == 4) {
            File txt = new File(nometxt);
            Scanner in = new Scanner(txt);

            String line = in.nextLine();
            String[] itens = line.split(" ");
            cont += itens.length;

            PrintWriter out = new PrintWriter("auxfile.gp");
            out.print("set title " + "\"" + title + "\n");
            out.print("set xlabel " + "\"Gerações\"" + "\n");
            out.print("set ylabel " + "\"" + ylabel + "\"" + "\n");
            out.println("set terminal " + terminal);
            out.println("set output " + "\"" + nomevisualizado + "." + extensao + "\"");
            for (int i = 2, x = 0; i <= cont; i++, x++) {
                if (i == 2) {
                    out.println("plot 'dados.txt' using 1:" + i + " title " + "\"" + x + "\"" + " with linespoints, \\");
                } else {
                    out.println("'dados.txt' using 1:" + i + " title " + "\"" + x + "\"" + " with linespoints, \\");
                }
                if (i == cont - 1) {
                    out.println("'dados.txt' using 1:" + (i + 1) + " title " + "\"" + (x + 1) + "\"" + " with linespoints,");
                }
            }
            out.close();
            Runtime rt = Runtime.getRuntime();
            Process prcs = rt.exec("C:\\Program Files\\gnuplot\\bin\\gnuplot auxfile.gp");
            File file = new File(nomevisualizado + "." + extensao);
            if(mudancaformato==0) {
                Desktop desktop = Desktop.getDesktop();
                Thread.sleep(500);
                desktop.open(file);
            }

        }
    }
}




