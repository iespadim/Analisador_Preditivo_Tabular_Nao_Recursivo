sentença vazia: ε

## Classes e arquitetura do projeto:
entrada: uma gramática G definida por uma quádrupla:

  G = ( N, T, P, S ), onde:
  - N) conjunto finito de não-terminais (ou variáveis)
  - T) - conjunto finito de terminais
  - P) - conjunto finito de regras de produção
  - S) - símbolo inicial da gramática

### Classe app ✔ 
* Interface de entrada da gramatica ✔
* entradas:✔ 
* mostra tabela com produções
* gera tabela de analise
* 


### Classe gramatica
* armazenar a gramática e verificar se ela é LL. ✔
* leGramatica() ✔
* verificaLL() ✔ 
* adicionar produções,✔ 
* adicionaProdução(arg simbolo_não_terminal)✔ 
* calcular os conjuntos FIRST e FOLLOW,
* first(arg simbolo_não_terminal)
* follow(arg simbolo_não_terminal)


### ~Classe produção~ ❌ Não será usada, as produções estão representadas em um Hashmap(key naoTerminal,value Arraylist<String> Produções) dentro da classe gramatica
~* representa uma produção da gramática.~
~* informações sobr simbolo a esquerda e a lista de símbolos à direita~

### Classe tabelaPreditiva
* constroi a tabela de análise preditiva tabular
* adicionar entradas à tabela
* realizar consultas na tabela

### Classe analisador
* faz análise sintática de uma sentença como entrada
* usa a tabela preditiva para verificar se a sentença é reconhecida ou não
* demonstrando o reconhecimento passo a passo.

## Roteiro de execução do programa
roteiro de execução do programa


* ler a gramatica ✔
* verificar se é LL ✔
  * verificar se nao possui recursao a esquerda✔
  * verificar se é fatoravel ✔
* criar conjuntos first e follow ✔
* montar tabela preditiva tabular ✔
  * criar uma tabela sendo as linhas os nao terminais e as colunas os terminais + $ ✔
  * preencher a tabelas com first e follow ✔ 👀 atenção ao $ na tabela. está incompleto
* mostrar a tabela de analise preditiva
  * main - le sentença
  * Inicie a pilha com o símbolo de início da gramática e um símbolo $.
  * Enquanto a pilha não estiver vazia:
  * Se o topo da pilha for um terminal, verifique se ele corresponde ao próximo símbolo na sentença. Se sim, remova-o da pilha e avance na sentença.
  * Se o topo da pilha for um não-terminal, consulte a tabela de análise preditiva tabular e substitua o topo da pilha pela produção correspondente.
  * Se o topo da pilha for o símbolo $ e a sentença estiver vazia, a análise foi bem-sucedida.
  * Mostre o resultado da análise (reconhecida ou não) e o passo a passo da simulação.
