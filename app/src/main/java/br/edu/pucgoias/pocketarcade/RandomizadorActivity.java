package br.edu.pucgoias.pocketarcade;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import br.edu.pucgoias.pocketarcade.databinding.ActivityRandomizadorBinding;

public class RandomizadorActivity extends AppCompatActivity {

    private static final String TAG = "PocketArcade_Randomizador";

    private ActivityRandomizadorBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRandomizadorBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Log.d(TAG, "onCreate");

        binding.btnVoltar.setOnClickListener(v -> finish());
        binding.btnEncerrarSessao.setOnClickListener(v -> encerrarSessao());
    }

    private void encerrarSessao() {
        Intent intent = new Intent(this, ApresentacaoActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra(Constantes.EXTRA_MODULO, getString(R.string.modulo_randomizador));
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
