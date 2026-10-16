package br.edu.pucgoias.pocketarcade;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import br.edu.pucgoias.pocketarcade.databinding.ActivityCofreBinding;

public class CofreActivity extends AppCompatActivity {

    private static final String TAG = "PocketArcade_Cofre";

    private static final int TAMANHO_CODIGO = 4;
    private static final int MAXIMO_TENTATIVAS = 8;

    private static final int PISTA_CERTA = 0;
    private static final int PISTA_OUTRO_LUGAR = 1;
    private static final int PISTA_AUSENTE = 2;

    private static final int[] CORES_FUNDO_PISTA = {R.color.pista_certa, R.color.pista_outro_lugar, R.color.pista_ausente};
    private static final int[] CORES_TEXTO_PISTA = {R.color.branco, R.color.texto_principal, R.color.branco};

    private ActivityCofreBinding binding;

    private String codigoSecreto;
    private int tentativasUsadas;
    private long inicioPartida;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCofreBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Log.d(TAG, "onCreate");

        binding.btnTentar.setOnClickListener(v -> tentar());
        binding.editPalpite.setOnEditorActionListener((v, acao, evento) -> {
            if (acao == EditorInfo.IME_ACTION_DONE) {
                tentar();
                return true;
            }
            return false;
        });
        binding.btnNovaPartida.setOnClickListener(v -> novaPartida());
        binding.btnVoltar.setOnClickListener(v -> finish());
        binding.btnEncerrarSessao.setOnClickListener(v -> encerrarSessao());

        novaPartida();
    }

    private void novaPartida() {
        codigoSecreto = gerarCodigo();
        Log.d(TAG, "Código secreto: " + codigoSecreto);
        tentativasUsadas = 0;
        inicioPartida = System.currentTimeMillis();

        binding.layoutHistorico.removeAllViews();
        binding.txtHistoricoVazio.setVisibility(View.VISIBLE);
        binding.cardConclusao.clearAnimation();
        binding.cardConclusao.setVisibility(View.GONE);
        binding.editPalpite.setText("");
        binding.layoutPalpite.setError(null);
        habilitarEntrada(true);
        atualizarContador();
    }

    private String gerarCodigo() {
        List<Integer> digitos = new ArrayList<>();
        for (int i = 0; i <= 9; i++) {
            digitos.add(i);
        }
        Collections.shuffle(digitos);

        StringBuilder codigo = new StringBuilder();
        for (int i = 0; i < TAMANHO_CODIGO; i++) {
            codigo.append(digitos.get(i));
        }
        return codigo.toString();
    }

    private void tentar() {
        String palpite = String.valueOf(binding.editPalpite.getText()).trim();

        String erro = validarPalpite(palpite);
        if (erro != null) {
            binding.layoutPalpite.setError(erro);
            return;
        }

        binding.layoutPalpite.setError(null);
        binding.editPalpite.setText("");
        tentativasUsadas++;
        atualizarContador();

        int[] pistas = calcularPistas(palpite);
        adicionarAoHistorico(palpite, pistas);

        if (palpite.equals(codigoSecreto)) {
            terminarPartida(true);
        } else if (tentativasUsadas == MAXIMO_TENTATIVAS) {
            terminarPartida(false);
        }
    }

    private String validarPalpite(String palpite) {
        if (palpite.isEmpty()) {
            return getString(R.string.erro_palpite_vazio);
        }
        if (!palpite.matches("\\d+")) {
            return getString(R.string.erro_palpite_numeros);
        }
        if (palpite.length() != TAMANHO_CODIGO) {
            return getString(R.string.erro_palpite_tamanho, TAMANHO_CODIGO);
        }
        if (temDigitoRepetido(palpite)) {
            return getString(R.string.erro_palpite_repetido);
        }
        return null;
    }

    private boolean temDigitoRepetido(String palpite) {
        for (int i = 0; i < palpite.length(); i++) {
            if (palpite.indexOf(palpite.charAt(i), i + 1) >= 0) {
                return true;
            }
        }
        return false;
    }

    private int[] calcularPistas(String palpite) {
        int[] pistas = new int[TAMANHO_CODIGO];
        for (int i = 0; i < TAMANHO_CODIGO; i++) {
            char digito = palpite.charAt(i);
            if (codigoSecreto.charAt(i) == digito) {
                pistas[i] = PISTA_CERTA;
            } else if (codigoSecreto.indexOf(digito) >= 0) {
                pistas[i] = PISTA_OUTRO_LUGAR;
            } else {
                pistas[i] = PISTA_AUSENTE;
            }
        }
        return pistas;
    }

    private void adicionarAoHistorico(String palpite, int[] pistas) {
        SpannableStringBuilder linha = new SpannableStringBuilder();
        linha.append(getString(R.string.cofre_linha_historico, tentativasUsadas)).append("  ");

        for (int i = 0; i < TAMANHO_CODIGO; i++) {
            int inicio = linha.length();
            linha.append(" ").append(palpite.charAt(i)).append(" ");
            int fim = linha.length();

            int corFundo = ContextCompat.getColor(this, CORES_FUNDO_PISTA[pistas[i]]);
            int corTexto = ContextCompat.getColor(this, CORES_TEXTO_PISTA[pistas[i]]);
            linha.setSpan(new BackgroundColorSpan(corFundo), inicio, fim, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            linha.setSpan(new ForegroundColorSpan(corTexto), inicio, fim, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            linha.append(" ");
        }

        TextView txtLinha = new TextView(this);
        txtLinha.setText(linha);
        txtLinha.setTypeface(Typeface.MONOSPACE, Typeface.BOLD);
        txtLinha.setTextColor(ContextCompat.getColor(this, R.color.texto_principal));
        txtLinha.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.texto_subtitulo));
        txtLinha.setPadding(0, getResources().getDimensionPixelSize(R.dimen.espaco_pequeno), 0, 0);

        binding.txtHistoricoVazio.setVisibility(View.GONE);
        binding.layoutHistorico.addView(txtLinha);

        txtLinha.setAlpha(0f);
        txtLinha.animate()
                .alpha(1f)
                .setDuration(getResources().getInteger(R.integer.duracao_linha_historico));
    }

    private void terminarPartida(boolean venceu) {
        long tempoGasto = System.currentTimeMillis() - inicioPartida;
        String tempo = formatarTempo(tempoGasto);
        String tentativas = getResources().getQuantityString(R.plurals.cofre_tentativas_usadas, tentativasUsadas, tentativasUsadas);

        habilitarEntrada(false);
        binding.cardConclusao.setVisibility(View.VISIBLE);

        if (venceu) {
            binding.txtConclusaoTitulo.setText(R.string.cofre_vitoria_titulo);
            binding.txtConclusaoTitulo.setTextColor(ContextCompat.getColor(this, R.color.vitoria));
            binding.txtConclusaoDetalhe.setText(getString(R.string.cofre_vitoria_detalhe, codigoSecreto, tentativas, tempo));
            animarVitoria();
        } else {
            binding.txtConclusaoTitulo.setText(R.string.cofre_derrota_titulo);
            binding.txtConclusaoTitulo.setTextColor(ContextCompat.getColor(this, R.color.derrota));
            binding.txtConclusaoDetalhe.setText(getString(R.string.cofre_derrota_detalhe, codigoSecreto, tentativas, tempo));
            animarDerrota();
        }
    }

    private String formatarTempo(long milissegundos) {
        long segundos = milissegundos / 1000;
        if (segundos < 60) {
            return getString(R.string.tempo_segundos, segundos);
        }
        return getString(R.string.tempo_minutos, segundos / 60, segundos % 60);
    }

    private void animarVitoria() {
        binding.cardConclusao.setAlpha(0f);
        binding.cardConclusao.setScaleX(0.8f);
        binding.cardConclusao.setScaleY(0.8f);
        binding.cardConclusao.animate()
                .alpha(1f)
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(getResources().getInteger(R.integer.duracao_campeao));
    }

    private void animarDerrota() {
        binding.cardConclusao.setAlpha(1f);
        binding.cardConclusao.setScaleX(1f);
        binding.cardConclusao.setScaleY(1f);
        binding.cardConclusao.startAnimation(AnimationUtils.loadAnimation(this, R.anim.tremer));
    }

    private void habilitarEntrada(boolean habilitar) {
        binding.editPalpite.setEnabled(habilitar);
        binding.btnTentar.setEnabled(habilitar);
    }

    private void atualizarContador() {
        binding.txtTentativas.setText(getString(R.string.cofre_tentativas, tentativasUsadas, MAXIMO_TENTATIVAS));
    }

    private void encerrarSessao() {
        Intent intent = new Intent(this, ApresentacaoActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra(Constantes.EXTRA_MODULO, getString(R.string.modulo_cofre));
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
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
    }
}
