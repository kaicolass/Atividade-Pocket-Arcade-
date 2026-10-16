package br.edu.pucgoias.pocketarcade;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import br.edu.pucgoias.pocketarcade.databinding.ActivityApresentacaoBinding;

public class ApresentacaoActivity extends AppCompatActivity {

    private static final String TAG = "PocketArcade_Apresentacao";
    private static final int TAMANHO_MINIMO_APELIDO = 3;
    private static final long DURACAO_PULSO = 120;

    private ActivityApresentacaoBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityApresentacaoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Log.d(TAG, "onCreate");

        binding.btnEntrar.setOnClickListener(v -> animarBotaoEntrar());
        mostrarUltimoModulo(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        mostrarUltimoModulo(intent);
    }

    private void mostrarUltimoModulo(Intent intent) {
        String modulo = intent.getStringExtra(Constantes.EXTRA_MODULO);
        if (modulo == null || modulo.trim().isEmpty()) {
            return;
        }
        binding.txtUltimoModulo.setText(getString(R.string.ultimo_modulo, modulo));
        binding.txtUltimoModulo.setVisibility(View.VISIBLE);
    }

    private void animarBotaoEntrar() {
        binding.btnEntrar.setClickable(false);
        binding.btnEntrar.animate()
                .scaleX(0.9f)
                .scaleY(0.9f)
                .setDuration(DURACAO_PULSO)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        binding.btnEntrar.animate()
                                .scaleX(1f)
                                .scaleY(1f)
                                .setDuration(DURACAO_PULSO)
                                .setListener(null);
                        validarEEntrar();
                    }
                });
    }

    private void validarEEntrar() {
        String apelido = String.valueOf(binding.editApelido.getText()).trim();

        if (apelido.length() < TAMANHO_MINIMO_APELIDO) {
            binding.layoutApelido.setError(getString(R.string.erro_apelido_curto, TAMANHO_MINIMO_APELIDO));
            binding.btnEntrar.setClickable(true);
            return;
        }

        binding.layoutApelido.setError(null);
        abrirCentral(apelido);
    }

    private void abrirCentral(String apelido) {
        Intent intent = new Intent(this, CentralActivity.class);
        intent.putExtra(Constantes.EXTRA_APELIDO, apelido);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        binding.btnEntrar.setClickable(true);
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
