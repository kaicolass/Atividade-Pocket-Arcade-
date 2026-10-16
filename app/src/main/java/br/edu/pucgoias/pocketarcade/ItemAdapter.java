package br.edu.pucgoias.pocketarcade;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import br.edu.pucgoias.pocketarcade.databinding.ItemListaBinding;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemViewHolder> {

    private final List<Item> itens;

    public ItemAdapter(List<Item> itens) {
        this.itens = itens;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemListaBinding binding = ItemListaBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ItemViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        Item item = itens.get(position);
        holder.binding.txtPalavra.setText(item.getPalavra());
        holder.binding.txtCategoria.setText(item.getCategoria());
    }

    @Override
    public int getItemCount() {
        return itens.size();
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {

        private final ItemListaBinding binding;

        ItemViewHolder(ItemListaBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            binding.getRoot().setOnLongClickListener(v -> {
                int posicao = getBindingAdapterPosition();
                if (posicao == RecyclerView.NO_POSITION) {
                    return false;
                }

                Item removido = itens.remove(posicao);
                notifyItemRemoved(posicao);

                String mensagem = v.getContext().getString(R.string.item_removido, removido.getPalavra());
                Toast.makeText(v.getContext(), mensagem, Toast.LENGTH_SHORT).show();
                return true;
            });
        }
    }
}
