"""
Gera os efeitos sonoros do Pocket Arcade (res/raw) usando apenas a
biblioteca padrão do Python. Os sons são tons simples sintetizados,
por isso não há problema de direitos autorais.

Uso (a partir da raiz do projeto):
    python3 ferramentas/gerar_sons.py
"""

import math
import struct
import wave
from pathlib import Path

TAXA = 22050  # amostras por segundo
PASTA_SAIDA = Path(__file__).resolve().parent.parent / "app" / "src" / "main" / "res" / "raw"


def tom(frequencia, duracao, volume=0.5, forma="seno"):
    """Gera uma nota com ataque e decaimento curtos para evitar estalos."""
    total = int(TAXA * duracao)
    ataque = int(TAXA * 0.005)
    amostras = []
    for i in range(total):
        t = i / TAXA
        fase = 2 * math.pi * frequencia * t
        if forma == "quadrada":
            valor = 1.0 if math.sin(fase) >= 0 else -1.0
            valor *= 0.4  # onda quadrada é mais "forte", então reduz
        else:
            valor = math.sin(fase)

        # envelope: sobe rápido no início e cai até zero no final
        if i < ataque:
            env = i / ataque
        else:
            env = 1.0 - (i - ataque) / max(1, total - ataque)
        amostras.append(valor * env * volume)
    return amostras


def silencio(duracao):
    return [0.0] * int(TAXA * duracao)


def salvar(nome, amostras):
    PASTA_SAIDA.mkdir(parents=True, exist_ok=True)
    caminho = PASTA_SAIDA / nome
    with wave.open(str(caminho), "wb") as arquivo:
        arquivo.setnchannels(1)
        arquivo.setsampwidth(2)  # 16 bits
        arquivo.setframerate(TAXA)
        dados = b"".join(struct.pack("<h", int(max(-1.0, min(1.0, a)) * 32767)) for a in amostras)
        arquivo.writeframes(dados)
    print("gerado:", caminho)


def main():
    # Vitória: arpejo subindo (dó, mi, sol, dó)
    vitoria = []
    for f in (523, 659, 784):
        vitoria += tom(f, 0.10, 0.5, "quadrada")
    vitoria += tom(1047, 0.30, 0.5, "quadrada")
    salvar("som_vitoria.wav", vitoria)

    # Derrota: notas descendo e mais graves
    derrota = []
    for f in (392, 330):
        derrota += tom(f, 0.16, 0.5, "quadrada")
    derrota += tom(262, 0.40, 0.5, "quadrada")
    salvar("som_derrota.wav", derrota)

    # Empate: duas notas iguais
    empate = tom(440, 0.12, 0.45, "quadrada") + silencio(0.05) + tom(440, 0.12, 0.45, "quadrada")
    salvar("som_empate.wav", empate)

    # Sorteio: sequência rápida de notas, como uma roleta parando
    sorteio = []
    for f in (600, 700, 800, 900, 1000, 1100):
        sorteio += tom(f, 0.05, 0.4, "seno")
    sorteio += tom(1320, 0.25, 0.5, "seno")
    salvar("som_sorteio.wav", sorteio)


if __name__ == "__main__":
    main()
