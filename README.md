# Pocket Arcade

Aplicativo Android desenvolvido como trabalho avaliativo da disciplina **Desenvolvimento de Aplicações para Dispositivos Móveis** (APP05 - Central de Desafios).

O Pocket Arcade reúne quatro minijogos rápidos em uma única central. O jogador escolhe um apelido, entra na Central de Desafios e pode alternar entre os módulos quantas vezes quiser.

## Autor

- **Nome:** Kaique Granato Santos Silva
- **Matrícula:** 20241012001030
- **Curso:** Análise e Desenvolvimento de Sistemas, PUC Goiás
- **Modalidade:** individual

## Telas

| # | Tela | Activity | O que faz |
|---|------|----------|-----------|
| 1 | Apresentação | `ApresentacaoActivity` | Mostra o logotipo e os dados do autor, pede o apelido (mínimo de 3 caracteres) e exibe o último módulo usado ao voltar de um desafio. |
| 2 | Central de Desafios | `CentralActivity` | Saúda o jogador pelo apelido e lista os quatro desafios em painéis com animação de entrada. |
| 3 | Scanner de Mensagens | `ScannerActivity` | Conta caracteres sem espaços, palavras e vogais (inclusive acentuadas), mostra o texto invertido e diz se é palíndromo. |
| 4 | Arena | `ArenaActivity` | Pedra, papel e tesoura contra o celular em melhor de três, com placar, animações e efeitos sonoros. |
| 5 | Cofre do Código Secreto | `CofreActivity` | Descobrir um código de 4 dígitos sem repetição em até 8 tentativas, com pistas coloridas por dígito, histórico e tempo de partida. |
| 6 | Randomizador | `RandomizadorActivity` | Lista de itens por categoria em RecyclerView (lista ou grade), com inclusão, exclusão por clique longo e sorteio dentro da categoria escolhida. |

## Navegação

```
Apresentação ──(apelido)──> Central ──> Scanner / Arena / Cofre / Randomizador
      ^                        ^                     │            │
      │                        └─────── Voltar (finish) ──────────┘
      └────────── Encerrar sessão (CLEAR_TOP | SINGLE_TOP + nome do módulo)
```

- Todas as navegações usam **Intents explícitas**. As chaves dos extras ficam na classe `Constantes`.
- O botão **Voltar** dos desafios chama `finish()`, então a Central não é recriada.
- O botão **Encerrar sessão** volta para a Apresentação com `FLAG_ACTIVITY_CLEAR_TOP | FLAG_ACTIVITY_SINGLE_TOP`. A tela recebe o nome do módulo em `onNewIntent()` e mostra "Último módulo usado".

## Tecnologias e recursos usados

- Java e layouts XML, com **View Binding** em todas as Activities
- ConstraintLayout, LinearLayout, NestedScrollView, CardView, TableLayout e TextInputLayout (Material Components)
- RecyclerView com Adapter, ViewHolder, classe de modelo (`Item`), LinearLayoutManager e GridLayoutManager
- Spinner com categorias definidas em `string-array`
- MediaPlayer com sons locais em `res/raw`: criados no `onCreate`, pausados no `onPause` e liberados no `onDestroy`
- Animações XML em `res/anim` (entrada dos painéis e tremor de derrota) e ViewPropertyAnimator
- SpannableStringBuilder para o histórico colorido do Cofre
- `Log.d` em `onCreate`, `onResume`, `onStop` e `onDestroy` de todas as Activities, com uma TAG por tela (`PocketArcade_...`)
- Textos em `strings.xml`, cores em `colors.xml`, dimensões em `dimens.xml` e durações em `integers.xml`
- Sem banco de dados, sem SharedPreferences e sem acesso à internet: os dados ficam só em memória

## Estrutura do projeto

```
app/src/main/
├── java/br/edu/pucgoias/pocketarcade/
│   ├── ApresentacaoActivity.java
│   ├── CentralActivity.java
│   ├── ScannerActivity.java
│   ├── ArenaActivity.java
│   ├── CofreActivity.java
│   ├── RandomizadorActivity.java
│   ├── Item.java
│   ├── ItemAdapter.java
│   └── Constantes.java
└── res/
    ├── anim/        animações XML
    ├── drawable/    ícones e logotipo em Vector Drawable
    ├── layout/      layouts das telas e do item da lista
    ├── raw/         efeitos sonoros (.wav)
    └── values/      strings, cores, dimensões, temas e arrays
ferramentas/
└── gerar_sons.py    script que gera os sons do app
```

## Como executar

1. Abrir a pasta do projeto no Android Studio e aguardar o Gradle Sync.
2. Executar em um emulador ou celular com Android 8.0 (API 26) ou superior.

Para gerar o APK pela linha de comando:

```
./gradlew assembleDebug
```

O APK fica em `app/build/outputs/apk/debug/app-debug.apk`.

Para acompanhar o ciclo de vida das telas, filtre o Logcat por `PocketArcade`.

## Créditos

- **Imagens:** o logotipo, o ícone do aplicativo e todos os ícones (lupa, troféu, cadeado, dado, pedra, papel e tesoura) são Vector Drawables desenhados pelo autor para este projeto.
- **Sons:** os efeitos sonoros (vitória, derrota, empate e sorteio) foram gerados proceduralmente pelo autor com o script `ferramentas/gerar_sons.py`, que sintetiza tons simples usando só a biblioteca padrão do Python. Para gerar de novo: `python3 ferramentas/gerar_sons.py`.
- **Bibliotecas:** AndroidX (AppCompat, ConstraintLayout, RecyclerView, CardView) e Material Components for Android.
