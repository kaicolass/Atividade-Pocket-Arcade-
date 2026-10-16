package br.edu.pucgoias.pocketarcade;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.appcompat.app.AppCompatActivity;

import java.text.Normalizer;
import java.util.Locale;

import br.edu.pucgoias.pocketarcade.databinding.ActivityScannerBinding;

public class ScannerActivity extends AppCompatActivity {

    private static final String TAG = "PocketArcade_Scanner";
    private static final int TAMANHO_MINIMO = 3;
    private static final String VOGAIS = "aeiouáàâãäéèêëíìîïóòôõöúùûü";

    private ActivityScannerBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityScannerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Log.d(TAG, "onCreate");

        binding.btnAnalisar.setOnClickListener(v -> analisar());
        binding.btnLimpar.setOnClickListener(v -> limpar());
        binding.btnVoltar.setOnClickListener(v -> finish());
        binding.btnEncerrarSessao.setOnClickListener(v -> encerrarSessao());
    }

    private void analisar() {
        String texto = String.valueOf(binding.editMensagem.getText()).trim();

        if (texto.length() < TAMANHO_MINIMO) {
            binding.layoutMensagem.setError(getString(R.string.erro_mensagem_curta, TAMANHO_MINIMO));
            binding.cardResultado.setVisibility(View.GONE);
            return;
        }

        binding.layoutMensagem.setError(null);
        esconderTeclado();

        binding.txtResCaracteres.setText(String.valueOf(contarCaracteresSemEspaco(texto)));
        binding.txtResPalavras.setText(String.valueOf(contarPalavras(texto)));
        binding.txtResVogais.setText(String.valueOf(contarVogais(texto)));
        binding.txtResInvertido.setText(inverter(texto));
        binding.txtResPalindromo.setText(ehPalindromo(texto) ? R.string.sim : R.string.nao);

        mostrarResultado();
    }

    private int contarCaracteresSemEspaco(String texto) {
        return texto.replaceAll("\\s", "").length();
    }

    private int contarPalavras(String texto) {
        return texto.split("\\s+").length;
    }

    private int contarVogais(String texto) {
        int total = 0;
        for (char letra : texto.toLowerCase(Locale.ROOT).toCharArray()) {
            if (VOGAIS.indexOf(letra) >= 0) {
                total++;
            }
        }
        return total;
    }

    private String inverter(String texto) {
        return new StringBuilder(texto).reverse().toString();
    }

    private boolean ehPalindromo(String texto) {
        String semAcentos = Normalizer.normalize(texto, Normalizer.Form.NFD).replaceAll("\\p{M}", "");
        String limpo = semAcentos.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9]", "");
        return !limpo.isEmpty() && limpo.equals(inverter(limpo));
    }

    private void mostrarResultado() {
        float deslocamento = getResources().getDimension(R.dimen.deslocamento_animacao);
        long duracao = getResources().getInteger(R.integer.duracao_resultado);

        binding.cardResultado.setAlpha(0f);
        binding.cardResultado.setTranslationY(deslocamento);
        binding.cardResultado.setVisibility(View.VISIBLE);
        binding.cardResultado.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(duracao);
    }

    private void limpar() {
        binding.editMensagem.setText("");
        binding.layoutMensagem.setError(null);
        binding.txtResCaracteres.setText("");
        binding.txtResPalavras.setText("");
        binding.txtResVogais.setText("");
        binding.txtResInvertido.setText("");
        binding.txtResPalindromo.setText("");
        binding.cardResultado.setVisibility(View.GONE);
    }

    private void esconderTeclado() {
        InputMethodManager teclado = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        teclado.hideSoftInputFromWindow(binding.editMensagem.getWindowToken(), 0);
        binding.editMensagem.clearFocus();
    }

    private void encerrarSessao() {
        Intent intent = new Intent(this, ApresentacaoActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra(Constantes.EXTRA_MODULO, getString(R.string.modulo_scanner));
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
