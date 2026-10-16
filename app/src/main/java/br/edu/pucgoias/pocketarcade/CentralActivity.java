package br.edu.pucgoias.pocketarcade;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.appcompat.app.AppCompatActivity;

import br.edu.pucgoias.pocketarcade.databinding.ActivityCentralBinding;

public class CentralActivity extends AppCompatActivity {

    private static final String TAG = "PocketArcade_Central";

    private ActivityCentralBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCentralBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Log.d(TAG, "onCreate");

        mostrarSaudacao();
        configurarBotoes();
        animarPaineis();
    }

    private void mostrarSaudacao() {
        String apelido = getIntent().getStringExtra(Constantes.EXTRA_APELIDO);
        if (apelido == null || apelido.trim().isEmpty()) {
            apelido = getString(R.string.apelido_padrao);
        }
        binding.txtSaudacao.setText(getString(R.string.saudacao, apelido.trim()));
    }

    private void configurarBotoes() {
        binding.btnAbrirScanner.setOnClickListener(v -> abrirDesafio(ScannerActivity.class));
        binding.btnAbrirArena.setOnClickListener(v -> abrirDesafio(ArenaActivity.class));
        binding.btnAbrirCofre.setOnClickListener(v -> abrirDesafio(CofreActivity.class));
        binding.btnAbrirRandomizador.setOnClickListener(v -> abrirDesafio(RandomizadorActivity.class));
    }

    private void abrirDesafio(Class<?> destino) {
        Intent intent = new Intent(this, destino);
        startActivity(intent);
    }

    private void animarPaineis() {
        View[] paineis = {
                binding.cardScanner,
                binding.cardArena,
                binding.cardCofre,
                binding.cardRandomizador
        };
        int atraso = getResources().getInteger(R.integer.atraso_entre_paineis);

        for (int i = 0; i < paineis.length; i++) {
            Animation entrada = AnimationUtils.loadAnimation(this, R.anim.entrada_painel);
            entrada.setStartOffset((long) i * atraso);
            paineis[i].startAnimation(entrada);
        }
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
