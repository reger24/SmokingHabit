package de.diegruender49.smokinghabit;

import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import de.diegruender49.smokinghabit.placeholder.PlaceholderContent.PlaceholderItem;
import de.diegruender49.smokinghabit.databinding.FragmentLogEntryBinding;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link PlaceholderItem}.
 */
public class LogEntryRecyclerViewAdapter extends RecyclerView.Adapter<LogEntryRecyclerViewAdapter.ViewHolder> {

    private final List<PlaceholderItem> mValues;

    public LogEntryRecyclerViewAdapter(List<PlaceholderItem> items) {
        mValues = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        return new ViewHolder(FragmentLogEntryBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mIdView.setText(mValues.get(position).id);
        holder.mContentView.setText(mValues.get(position).content);
        holder.mLogDesc.setText( mValues.get(position).logDesc);
        holder.mLogDateTxt.setText(mValues.get(position).logDate);
        holder.mLogTimeTxt.setText(mValues.get(position).logTime);
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView mIdView;
        public final TextView mContentView;
        public PlaceholderItem mItem;
        public final TextView mLogDateTxt;
        public final TextView mLogTimeTxt;
        public final TextView mLogDesc;

        public ViewHolder(FragmentLogEntryBinding binding) {
            super(binding.getRoot());
            mIdView = binding.itemNumber;
            mContentView = binding.content;
            mLogDesc = binding.textLogDesc;
            mLogDateTxt = binding.textLogDate;
            mLogTimeTxt = binding.textLogTime;
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}