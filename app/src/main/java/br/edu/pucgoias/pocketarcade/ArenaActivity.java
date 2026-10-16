package br.edu.pucgoias.pocketarcade;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.util.Random;

import br.edu.pucgoias.pocketarcade.databinding.ActivityArenaBinding;

public class ArenaActivity extends AppCompatActivity {

    private static final String TAG = "PocketArcade_Arena";

    private static final int PEDRA = 0;
    private static final int PAPEL = 1;
    private static final int TESOURA = 2;
    private static final int VITORIAS_PARA_CAMPEAO = 2;

    private static final int[] IMAGENS_JOGADAS = {R.drawable.ic_pedra, R.drawable.ic_papel, R.drawable.ic_tesoura};
    private static final int[] NOMES_JOGADAS = {R.string.jogada_pedra, R.string.jogada_papel, R.string.jogada_tesoura};

    private ActivityArenaBinding binding;
    private final Random sorteio = new Random();

    private int pontosJogador;
    private int pontosDispositivo;

    private MediaPlayer somVitoria;
    private MediaPlayer somDerrota;
    private MediaPlayer somEmpate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityArenaBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Log.d(TAG, "onCreate");

        somVitoria = MediaPlayer.create(this, R.raw.som_vitoria);
        somDerrota = MediaPlayer.create(this, R.raw.som_derrota);
        somEmpate = MediaPlayer.create(this, R.raw.som_empate);

        binding.btnPedra.setOnClickListener(v -> jogar(PEDRA));
        binding.btnPapel.setOnClickListener(v -> jogar(PAPEL));
        binding.btnTesoura.setOnClickListener(v -> jogar(TESOURA));
        binding.btnNovaPartida.setOnClickListener(v -> novaPartida());
        binding.btnVoltar.setOnClickListener(v -> finish());
        binding.btnEncerrarSessao.setOnClickListener(v -> encerrarSessao());

        novaPartida();
    }

    private void jogar(int jogadaJogador) {
        int jogadaDispositivo = sorteio.nextInt(3);
        mostrarEscolhas(jogadaJogador, jogadaDispositivo);

        int resultado = (jogadaJogador - jogadaDispositivo + 3) % 3;

        if (resultado == 0) {
            mostrarResultadoRodada(R.string.rodada_empate, R.color.texto_secundario);
            tocar(somEmpate);
        } else if (resultado == 1) {
            pontosJogador++;
            mostrarResultadoRodada(R.string.rodada_vitoria, R.color.vitoria);
            tocar(somVitoria);
        } else {
            pontosDispositivo++;
            mostrarResultadoRodada(R.string.rodada_derrota, R.color.derrota);
            tocar(somDerrota);
        }

        atualizarPlacar();
        verificarFimDePartida();
    }

    private void mostrarEscolhas(int jogadaJogador, int jogadaDispositivo) {
        binding.imgEscolhaJogador.setImageResource(IMAGENS_JOGADAS[jogadaJogador]);
        binding.txtEscolhaJogador.setText(NOMES_JOGADAS[jogadaJogador]);
        binding.imgEscolhaDispositivo.setImageResource(IMAGENS_JOGADAS[jogadaDispositivo]);
        binding.txtEscolhaDispositivo.setText(NOMES_JOGADAS[jogadaDispositivo]);

        long duracao = getResources().getInteger(R.integer.duracao_escolha);
        long atraso = getResources().getInteger(R.integer.atraso_escolha_dispositivo);
        animarEscolha(binding.imgEscolhaJogador, duracao, 0);
        animarEscolha(binding.imgEscolhaDispositivo, duracao, atraso);
    }

    private void animarEscolha(View imagem, long duracao, long atraso) {
        imagem.setScaleX(0.5f);
        imagem.setScaleY(0.5f);
        imagem.setAlpha(0f);
        imagem.animate()
                .scaleX(1f)
                .scaleY(1f)
                .alpha(1f)
                .setStartDelay(atraso)
                .setDuration(duracao);
    }

    private void mostrarResultadoRodada(int mensagem, int cor) {
        binding.txtResultadoRodada.setText(mensagem);
        binding.txtResultadoRodada.setTextColor(ContextCompat.getColor(this, cor));

        binding.txtResultadoRodada.setAlpha(0f);
        binding.txtResultadoRodada.setTranslationY(getResources().getDimension(R.dimen.deslocamento_animacao));
        binding.txtResultadoRodada.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(getResources().getInteger(R.integer.duracao_resultado));
    }

    private void atualizarPlacar() {
        binding.txtPontosJogador.setText(String.valueOf(pontosJogador));
        binding.txtPontosDispositivo.setText(String.valueOf(pontosDispositivo));
    }

    private void verificarFimDePartida() {
        if (pontosJogador == VITORIAS_PARA_CAMPEAO) {
            mostrarCampeao(getString(R.string.campeao_jogador, pontosJogador, pontosDispositivo), R.color.vitoria);
        } else if (pontosDispositivo == VITORIAS_PARA_CAMPEAO) {
            mostrarCampeao(getString(R.string.campeao_dispositivo, pontosDispositivo, pontosJogador), R.color.derrota);
        }
    }

    private void mostrarCampeao(String mensagem, int cor) {
        habilitarJogadas(false);

        binding.txtCampeao.setText(mensagem);
        binding.txtCampeao.setTextColor(ContextCompat.getColor(this, cor));
        binding.txtCampeao.setVisibility(View.VISIBLE);
        binding.txtCampeao.setScaleX(0.8f);
        binding.txtCampeao.setScaleY(0.8f);
        binding.txtCampeao.setAlpha(0f);
        binding.txtCampeao.animate()
                .scaleX(1f)
                .scaleY(1f)
                .alpha(1f)
                .setDuration(getResources().getInteger(R.integer.duracao_campeao));
    }

    private void novaPartida() {
        pontosJogador = 0;
        pontosDispositivo = 0;
        atualizarPlacar();

        binding.imgEscolhaJogador.setImageDrawable(null);
        binding.imgEscolhaDispositivo.setImageDrawable(null);
        binding.txtEscolhaJogador.setText(R.string.arena_aguardando);
        binding.txtEscolhaDispositivo.setText(R.string.arena_aguardando);
        binding.txtResultadoRodada.setText(R.string.arena_inicio);
        binding.txtResultadoRodada.setTextColor(ContextCompat.getColor(this, R.color.texto_secundario));
        binding.txtCampeao.setVisibility(View.GONE);

        habilitarJogadas(true);
    }

    private void habilitarJogadas(boolean habilitar) {
        float transparencia = habilitar ? 1f : 0.4f;
        View[] botoes = {binding.btnPedra, binding.btnPapel, binding.btnTesoura};
        for (View botao : botoes) {
            botao.setEnabled(habilitar);
            botao.setAlpha(transparencia);
        }
    }

    private void tocar(MediaPlayer som) {
        if (som == null) {
            return;
        }
        pausarSeTocando(somVitoria);
        pausarSeTocando(somDerrota);
        pausarSeTocando(somEmpate);
        som.seekTo(0);
        som.start();
    }

    private void pausarSeTocando(MediaPlayer som) {
        if (som != null && som.isPlaying()) {
            som.pause();
        }
    }

    private void encerrarSessao() {
        Intent intent = new Intent(this, ApresentacaoActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra(Constantes.EXTRA_MODULO, getString(R.string.modulo_arena));
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        pausarSeTocando(somVitoria);
        pausarSeTocando(somDerrota);
        pausarSeTocando(somEmpate);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
        if (somVitoria != null) {
            somVitoria.release();
            somVitoria = null;
        }
        if (somDerrota != null) {
            somDerrota.release();
            somDerrota = null;
        }
        if (somEmpate != null) {
            somEmpate.release();
            somEmpate = null;
        }
    }
}
