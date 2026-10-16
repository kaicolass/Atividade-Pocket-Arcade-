package br.edu.pucgoias.pocketarcade;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import br.edu.pucgoias.pocketarcade.databinding.ActivityRandomizadorBinding;

public class RandomizadorActivity extends AppCompatActivity {

    private static final String TAG = "PocketArcade_Randomizador";

    private static final int[] EXEMPLOS_POR_CATEGORIA = {
            R.array.exemplos_filmes,
            R.array.exemplos_comidas,
            R.array.exemplos_lugares,
            R.array.exemplos_desafios
    };

    private ActivityRandomizadorBinding binding;
    private final List<Item> itens = new ArrayList<>();
    private final Random sorteio = new Random();
    private ItemAdapter adapter;
    private boolean emGrade = false;

    private MediaPlayer somSorteio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRandomizadorBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Log.d(TAG, "onCreate");

        somSorteio = MediaPlayer.create(this, R.raw.som_sorteio);

        configurarSpinner();
        carregarExemplos();
        configurarLista();

        binding.btnAdicionar.setOnClickListener(v -> adicionarItem());
        binding.editValor.setOnEditorActionListener((v, acao, evento) -> {
            if (acao == EditorInfo.IME_ACTION_DONE) {
                adicionarItem();
                return true;
            }
            return false;
        });
        binding.btnSortear.setOnClickListener(v -> sortear());
        binding.btnAlternarLayout.setOnClickListener(v -> alternarLayout());
        binding.btnVoltar.setOnClickListener(v -> finish());
        binding.btnEncerrarSessao.setOnClickListener(v -> encerrarSessao());
    }

    private void configurarSpinner() {
        ArrayAdapter<CharSequence> categorias = ArrayAdapter.createFromResource(
                this, R.array.categorias, android.R.layout.simple_spinner_item);
        categorias.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerCategoria.setAdapter(categorias);
    }

    private void carregarExemplos() {
        String[] categorias = getResources().getStringArray(R.array.categorias);
        for (int i = 0; i < categorias.length; i++) {
            for (String palavra : getResources().getStringArray(EXEMPLOS_POR_CATEGORIA[i])) {
                itens.add(new Item(palavra, categorias[i]));
            }
        }
    }

    private void configurarLista() {
        adapter = new ItemAdapter(itens);
        binding.recyclerItens.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerItens.setAdapter(adapter);

        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int inicio, int quantidade) {
                atualizarListaVazia();
            }

            @Override
            public void onItemRangeRemoved(int inicio, int quantidade) {
                atualizarListaVazia();
            }
        });
        atualizarListaVazia();
    }

    private void adicionarItem() {
        String palavra = String.valueOf(binding.editValor.getText()).trim();
        String categoria = categoriaSelecionada();

        if (palavra.isEmpty()) {
            binding.layoutValor.setError(getString(R.string.erro_valor_vazio));
            return;
        }
        if (jaExiste(palavra, categoria)) {
            binding.layoutValor.setError(getString(R.string.erro_item_repetido));
            return;
        }

        binding.layoutValor.setError(null);
        itens.add(new Item(palavra, categoria));
        int posicao = itens.size() - 1;
        adapter.notifyItemInserted(posicao);
        binding.recyclerItens.smoothScrollToPosition(posicao);
        binding.editValor.setText("");
    }

    private boolean jaExiste(String palavra, String categoria) {
        for (Item item : itens) {
            if (item.getCategoria().equals(categoria) && item.getPalavra().equalsIgnoreCase(palavra)) {
                return true;
            }
        }
        return false;
    }

    private void sortear() {
        String categoria = categoriaSelecionada();

        List<Item> daCategoria = new ArrayList<>();
        for (Item item : itens) {
            if (item.getCategoria().equals(categoria)) {
                daCategoria.add(item);
            }
        }

        if (daCategoria.isEmpty()) {
            binding.txtRotuloSorteio.setText(getString(R.string.random_sem_itens, categoria));
            binding.txtResultadoSorteio.setVisibility(View.GONE);
            return;
        }

        Item escolhido = daCategoria.get(sorteio.nextInt(daCategoria.size()));
        binding.txtRotuloSorteio.setText(getString(R.string.random_sorteado_em, categoria));
        binding.txtResultadoSorteio.setText(escolhido.getPalavra());
        binding.txtResultadoSorteio.setVisibility(View.VISIBLE);

        animarResultado();
        tocarSom();
    }

    private void animarResultado() {
        binding.txtResultadoSorteio.setAlpha(0f);
        binding.txtResultadoSorteio.setScaleX(0.6f);
        binding.txtResultadoSorteio.setScaleY(0.6f);
        binding.txtResultadoSorteio.animate()
                .alpha(1f)
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(getResources().getInteger(R.integer.duracao_sorteio));
    }

    private void alternarLayout() {
        emGrade = !emGrade;
        if (emGrade) {
            int colunas = getResources().getInteger(R.integer.colunas_grade);
            binding.recyclerItens.setLayoutManager(new GridLayoutManager(this, colunas));
            binding.btnAlternarLayout.setText(R.string.btn_ver_lista);
        } else {
            binding.recyclerItens.setLayoutManager(new LinearLayoutManager(this));
            binding.btnAlternarLayout.setText(R.string.btn_ver_grade);
        }
    }

    private String categoriaSelecionada() {
        return String.valueOf(binding.spinnerCategoria.getSelectedItem());
    }

    private void atualizarListaVazia() {
        binding.txtListaVazia.setVisibility(itens.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private void tocarSom() {
        if (somSorteio == null) {
            return;
        }
        somSorteio.seekTo(0);
        somSorteio.start();
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
    protected void onPause() {
        super.onPause();
        if (somSorteio != null && somSorteio.isPlaying()) {
            somSorteio.pause();
        }
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
        if (somSorteio != null) {
            somSorteio.release();
            somSorteio = null;
        }
    }
}
