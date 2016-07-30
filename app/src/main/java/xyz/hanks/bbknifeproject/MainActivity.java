package xyz.hanks.bbknifeproject;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import xyz.hanks.BBKnife;
import xyz.hanks.BindView;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.bb_button) Button mButton;
    @BindView(R.id.bb_image)  ImageView mImage;
    @BindView(R.id.bb_text)   TextView mText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BBKnife.bind(this);

        mButton.setText("hanks.xyz");
        mText.setText("hanks.xyz");
        mImage.setImageResource(R.mipmap.ic_launcher);
    }
}
