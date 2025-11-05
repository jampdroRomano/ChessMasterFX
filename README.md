![Status](https://img.shields.io/badge/status-Versão.1.0.1-yellow)
![Frontend](https://img.shields.io/badge/tecnologia-JavaFx-blue)
![Backend](https://img.shields.io/badge/tecnologia-Java-red)


# ♟️ ChessMasterFX

Um jogo de **xadrez** desenvolvido em **Java** com interface feita em **JavaFX**.  
Conta com dois modos de jogo: **Player vs Player (local)** e **Player vs Bot**, com uma **inteligência artificial** que analisa movimentos e responde estrategicamente.

---

## 🧠 Sobre o Projeto

O **ChessMasterFX** foi criado com o objetivo de praticar **lógica de programação**, **orientação a objetos (POO)** e **desenvolvimento de interfaces gráficas** em Java.  
A ideia é oferecer uma experiência fluida e intuitiva tanto para partidas entre dois jogadores quanto contra o computador.

---

## 🗂 Estrutura do Repositório

```text
ChessMasterFX/
│
├─ src/main/java/com/chessmaster/fx/
│  ├─ controller/            # Controladores JavaFX (Controller, HomeController)
│  ├─ model/                 # Modelos de dados (Peca, Tabuleiro, Rei, Casa, etc.)
│  ├─ service/               # Lógica de negócio (Bot IA)
│  └─ App.java               # Classe principal da aplicação
│
├─ src/main/resources/
│  ├─ imagens/               # Recursos gráficos (.png) das peças
│  ├─ Home.fxml              # Tela de Menu Principal
│  └─ tabuleiro.fxml         # Tela do jogo (Tabuleiro)
│
└─ pom.xml                    # Dependências e configuração do Maven
```



## ⚙ Funcionalidades Principais
| ID    | Funcionalidade           | Descrição                                                   |
|-------|--------------------------|-------------------------------------------------------------|
| RF01  | Modo Player vs Player (local)            | Permite que dois jogadores disputem uma partida no mesmo computador.              |
| RF02  | Modo Player vs Bot (IA)        | Permite ao jogador enfrentar uma inteligência artificial com níveis de dificuldade (Fácil, Médio, Difícil).             |
| RF03  | Interface Gráfica JavaFX        | Layout limpo e intuitivo, com destaques visuais para movimentos legais e situações de cheque.        |
| RF04  | Verificação de Regras     |Implementa a lógica padrão do xadrez, incluindo verificação automática de xeque e xeque-mate.Implementa a lógica padrão do xadrez, incluindo verificação automática de xeque e xeque-mate.
| RF05  | Lógica de Movimentação    | Implementação de todos os movimentos especiais, incluindo Roque, promoção de peão e captura.                              |



---

## 🛠 Tecnologias Utilizadas
- **Core** Java 17
- **Interface Gráfica:** JavaFX 17
- **IA:**  Algoritmos de avaliação heurística para tomada de decisão do Bot. 
- **Princípios:**   Programação Orientada a Objetos (POO)
- **Gerenciamento de Dependências:**  Maven 
- **Ferramentas Auxiliares:**  Git, Figma.

---

## ⚠️ Pré-requisitos

- Java JDK  >= 17 
- Apache Maven  >= 3.x
- JavaFX  >= 17 Gerenciado automaticamente pelo Maven)

---

## 💻 Como Rodar o Projeto

- Certifique-se de que os pré-requisitos (Java 17 e Maven) estão instalados..
- Execute o projeto utilizando o plugin do Maven para JavaFX:
```
mvn clean javafx:run

```
---

## 📂 Documentação
Toda a documentação técnica está no próprio código-fonte, separada por responsabilidade:
- Lógica de IA: ```src/main/java/com/chessmaster/fx/service/```
- Lógica de Jogo (Modelos): ```src/main/java/com/chessmaster/fx/model/```
- Controladores das Telas: ```src/main/java/com/chessmaster/fx/controller/```

