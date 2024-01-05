package com.app.dz.quranapp.MushafParte;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresPermission;

import com.app.dz.quranapp.R;
import com.bumptech.glide.Glide;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;

public class CustomListAdapter extends ArrayAdapter<Reader> implements MediaPlayer.OnPreparedListener,MediaPlayer.OnCompletionListener {

    DecimalFormat decimalFormat = new DecimalFormat("000");
    private Context mContext;
    private int mResource;
    private MediaPlayer mediaPlayer;
    private int PlayingReaderId = -1;

    public CustomListAdapter(Context context, int resource, List<Reader> items) {
        super(context, resource, items);
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnPreparedListener(this);
        mContext = context;
        mResource = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(mResource, parent, false);
        }

        Reader reader = getItem(position);

        TextView itemNameTextView = convertView.findViewById(R.id.item_name);
        ImageView audio_image = convertView.findViewById(R.id.audio_image);
        ImageView reader_image = convertView.findViewById(R.id.reader_image);

        itemNameTextView.setText(reader.readerName);

        audio_image.setOnClickListener(v -> {
            if (PlayingReaderId == -1) {
                //no playing audio
                try {
                    PlayingReaderId = reader.readerId;
                    Glide.with(mContext).load(R.drawable.ic_headphones_playing).into(audio_image);
                    String url = getCorrectUrl(reader.readerId, 112);
                    Log.e("bottomsheet","url : "+url);
                    mediaPlayer.setDataSource(mContext, Uri.parse(url));
                    mediaPlayer.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                mediaPlayer.stop();
                mediaPlayer.reset();
                mediaPlayer.release();

                if (PlayingReaderId != reader.readerId) {
                    //  audio is playing and user click on another one
                    try {
                        PlayingReaderId = reader.readerId;
                        Glide.with(mContext).load(R.drawable.ic_headphones_playing).into(audio_image);
                        String url = getCorrectUrl(reader.readerId, 112);
                        Log.e("bottomsheet","url : is already playing new url : "+url);
                        mediaPlayer.setDataSource(mContext, Uri.parse(url));
                        mediaPlayer.prepare();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }else {
                    Glide.with(mContext).load(R.drawable.ic_headphones).into(audio_image);
                    PlayingReaderId = -1;
                }

            }

        });


        switch (reader.readerId) {
            case 1:
                Log.e("bottomsheet","inside switch "+reader.readerId);
                Glide.with(mContext).load(R.drawable.alafasy).into(reader_image);
                break;
            case 2:
                Log.e("bottomsheet","inside switch "+reader.readerId);
                Glide.with(mContext).load(R.drawable.sharum).into(reader_image);
                break;
            case 3:
                Log.e("bottomsheet","inside switch "+reader.readerId);
                Glide.with(mContext).load(R.drawable.sudais).into(reader_image);
                break;
            case 4:
                Log.e("bottomsheet","inside switch "+reader.readerId);
                Glide.with(mContext).load(R.drawable.al_manshawi).into(reader_image);
                break;
            default:
                Log.e("bottomsheet","inside switch "+reader.readerId);
                Glide.with(mContext).load(R.drawable.abd_baset).into(reader_image);
                break;

        }

        return convertView;
    }


    public String getCorrectUrl(int readerId, int suraIndex) {
        String suraFormatNumber = decimalFormat.format(suraIndex);
        switch (readerId) {
            case 1:
                return "https://download.quranicaudio.com/qdc/mishari_al_afasy/murattal/" + suraIndex + ".mp3";
            case 2:
                return "https://download.quranicaudio.com/qdc/saud_ash-shuraym/murattal/" + suraFormatNumber + ".mp3";
            case 3:
                return "https://download.quranicaudio.com/qdc/abdurrahmaan_as_sudais/murattal/" + suraIndex + ".mp3";
            case 4:
                return "https://download.quranicaudio.com/qdc/siddiq_al-minshawi/mujawwad/" + suraFormatNumber + ".mp3";
            default:
                return "https://download.quranicaudio.com/qdc/abdul_baset/murattal/" + suraIndex + ".mp3";
        }
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        Log.e("bottomsheet","onPrepared ");
        mediaPlayer.start();
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        Log.e("bottomsheet","onCompletion ");
        PlayingReaderId = -1;
    }
}