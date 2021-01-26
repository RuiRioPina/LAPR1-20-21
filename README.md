COMO USAR

1. Coloque os ficheiros que quer analisar na pasta com o .jar.

2. Abra o cmd e use o comando: cd DIRECTORIO , onde DIRECTORIO é o local onde tem o .jar.

3. Use o comando:

	java -jar nome_programa.jar

para começar a aplicação em modo interativo

	java -jar nome_programa.jar -n ficheiro_entrada.txt

caso queira inserir os parâmetros iniciais através de um ficheiro de texto e seguir para o menu do modo interativo 
	
    java -jar nome_programa.jar -t XXX -g Y -e -v -r ficheiro_entrada.txt ficheiro_saida.txt

caso queira escrever para um ficheiro de texto a informação do estudo da população, em que XXX será o numero de gerações desejadas,
e o Y corresponde ao tipo de formato do gráfico (1-png, 2-txt e 3-eps). Os parametros e, v, r, quando específicados, identificam a 
obrigatoriedade de calcular o valor e o vetor próprio (e), a dimensão da população a cada geração (v), e a variação da população 
entre gerações (r). O ficheiro de entrada e o ficheiro de saída são fixos.

IMPORTANTE:
O nome do ficheiro de entrada deve corresponder ao nome da espécie a estudar.

O programa aceita vírgulas ou pontos para números decimais consoante o país do teclado.


